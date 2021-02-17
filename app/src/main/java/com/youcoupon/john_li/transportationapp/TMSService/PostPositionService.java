package com.youcoupon.john_li.transportationapp.TMSService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInPhotoInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TimingPositionInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.GPS;
import com.youcoupon.john_li.transportationapp.TMSModel.PostLocationModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostPhoto;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.GPSConverterUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时发送位置
 */
public class PostPositionService extends Service {
    int TIME_INTERVAL = 5 *60 * 1000; // 这是50min
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(POST_LOCATION_ACTION);
        registerReceiver(receiver, intentFilter);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(POST_LOCATION_ACTION);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0低电量模式需要使用该方法触发定时任务
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以上 需要使用该方法精确执行时间
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        } else {//4。4一下 使用老方法
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), TIME_INTERVAL, pendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final String POST_LOCATION_ACTION = "com.youcoupon.john_li.transportationapp" + "_TIMING_POST_LOCATION_ACTION";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (POST_LOCATION_ACTION.equals(action)) {
                TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "5min一次发送定位：当前时间" + TMSCommonUtils.getTimeNow(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder/log").getPath(), TMSCommonUtils.getTimeToday() + "Today_log.txt");
                // 判断是否登陆，未登陆直接不提交
                if (isLogin()) {
                    // 提交定位状态
                    sendPhotoToServer(context);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
                }
            }
        }
    };

    private boolean isLogin() {
        String loginMsg = String.valueOf(SpuUtils.get(this, "loginMsg", ""));
        if (!loginMsg.equals("") && !loginMsg.equals("null")) {    // 判斷是否有登錄記錄
            List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
            boolean flag = false;
            for (UserModel model : mUserModelList) {
                if (model.getLoginTime().equals(TMSCommonUtils.getTimeToday())) { // 當有登錄記錄的情況下，判斷是否是今天登錄的
                    TMSShareInfo.mUserModelList.add(model);
                    flag = true;
                }
            }

            // 為當天登錄信息
            if (flag) {
                return true;
            }
        }

        return false;
    }

    private void sendPhotoToServer(Context context) {
        List<PostLocationModel> postList = new ArrayList<>();
        List<TimingPositionInfo> all = null;
        try {
            all = TMSApplication.db.selector(TimingPositionInfo.class).where("status","!=", "1").and("userid","!=",0).findAll();

            if (all != null) {
                if (all.size() > 0) {
                    for (TimingPositionInfo timingPositionInfo : all) {
                        GPS gps = GPSConverterUtils.gps84_To_Gcj02(Double.parseDouble(timingPositionInfo.getLantitude()), Double.parseDouble(timingPositionInfo.getLongtitude()));
                        postList.add(new PostLocationModel(gps.getLon(), gps.getLat(), timingPositionInfo.getCreatTime()));
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        } catch (DbException e) {
            e.printStackTrace();
            return;
        }

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("salesmanid", TMSCommonUtils.getUserFor40(this).getSalesmanID());
        paramsMap.put("deviceno", TMSCommonUtils.getIMEI(context));
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.POST_LOCATION + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setAsJsonContent(true);
        params.setConnectTimeout(90 * 1000);
        params.setBodyContent(new Gson().toJson(postList));
        String uri = params.getUri();
        final List<TimingPositionInfo> finalAll = all;
        x.http().request(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    String sqlString = "update timing_position_tb set status = ";
                    if (result.equals("true")) {
                        sqlString += " 1 where userid != 0 and time_id in (";
                    } else {
                        sqlString += " 2 where userid != 0 and time_id in (";
                    }

                    for (TimingPositionInfo info : finalAll) {
                        sqlString = sqlString + "'" + info.get_id() + "',";
                    }
                    sqlString = sqlString.substring(0,sqlString.length()-1);
                    sqlString += ")";
                    TMSApplication.db.execNonQuery(sqlString);
                    //int i = TMSApplication.db.update(InvoiceStateInfo.class,b,status);
                } catch (DbException e) {
                    e.printStackTrace();
                    TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "提交定位信息成功，保存异常：当前时间" + TMSCommonUtils.getTimeNow() + "\n" + e.getMessage(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder/log").getPath(), TMSCommonUtils.getTimeToday() + "Today_log.txt");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                try {
                    String sqlString = "update timing_position_tb set status = 2 where userid != 0 and time_id in (";
                    for (TimingPositionInfo info : finalAll) {
                        sqlString = sqlString + "'" + info.get_id() + "',";
                    }
                    sqlString = sqlString.substring(0,sqlString.length()-1);
                    sqlString += ")";
                    TMSApplication.db.execNonQuery(sqlString);
                } catch (DbException e) {
                    e.printStackTrace();
                    TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "提交定位信息失败，保存异常：当前时间" + TMSCommonUtils.getTimeNow() + "\n" + e.getMessage(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder/log").getPath(), TMSCommonUtils.getTimeToday() + "Today_log.txt");
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {


            }
        });
    }
}