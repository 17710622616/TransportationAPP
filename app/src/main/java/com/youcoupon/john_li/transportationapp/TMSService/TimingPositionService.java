package com.youcoupon.john_li.transportationapp.TMSService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TimingPositionInfo;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.List;

public class TimingPositionService extends Service {
    int TIME_INTERVAL = 30  * 1000; // 这是30s
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(TEST_ACTION);
        registerReceiver(receiver, intentFilter);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(TEST_ACTION);
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

    public static final String TEST_ACTION = "com.youcoupon.john_li.transportationapp" + "_TIMING_POSITION_ACTION";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TEST_ACTION.equals(action)) {
                TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "开始定位：当前时间" + TMSCommonUtils.getTimeNow(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Today_log.txt");
                TMSCommonUtils.getLngAndLat(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
                }
            }
        }
    };
}