package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.LoginListAdapter;
import com.youcoupon.john_li.transportationapp.TMSAdapter.MainAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInCustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInOrderStatusInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInPhotoInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialCorrespondenceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialNumberInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.InvoiceThisVhiclePullModel;
import com.youcoupon.john_li.transportationapp.TMSModel.InvoiceViewModel;
import com.youcoupon.john_li.transportationapp.TMSModel.MaterialCorrespondenceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSService.PostPositionService;
import com.youcoupon.john_li.transportationapp.TMSUtils.GpsUtils;
import com.youcoupon.john_li.transportationapp.TMSService.PostPhotoService;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSBussinessUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSService.TimingPositionService;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import hardware.print.printer;

/**
 *
 * 主界面
 */
public class MainActivity extends BaseActivity {
    printer mPrinter = new printer();
    TextView textView;
    private GridView menuGv;
    private TMSHeadView headView;
    private MainAdapter mAdapter;
    private List<String> menuList;
    private List<UserModel> mUserModelList;
    private ProgressDialog mLoadDialog;
    private Map<String, Boolean> updateStatus;
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    boolean b = false;
                    for (Map.Entry<String, Boolean> entry : updateStatus.entrySet()) {
                        if (entry.getValue()) {
                            b = true;
                        }
                    }

                    if (!b) {
                        if (mLoadDialog != null) {
                            if (mLoadDialog.isShowing()) {
                                mLoadDialog.dismiss();
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case 2:
                    if (mLoadDialog != null) {
                        if (mLoadDialog.isShowing()) {
                            mLoadDialog.dismiss();
                        }
                    }

                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };
    @SuppressLint("MissingPermission")
    @RequiresApi(api = 26)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 獲取手機IMEI
        TelephonyManager tm = (TelephonyManager)this.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        //TMSShareInfo.IMEI = Build.getSerial() + "/" + tm.getSimSerialNumber();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Todo Don't forget to ask the permission
            TMSShareInfo.IMEI = Build.getSerial();
        } else {
            TMSShareInfo.IMEI = Build.SERIAL;
        }

        // 判断定位打开权限
        if (!GpsUtils.isOPen(this)) {
            GpsUtils.openGPS(this);
        }

        // 打开地图定位服务
        if (!TMSCommonUtils.isServiceRunning(this, "com.youcoupon.john_li.transportationapp.TMSService.TimingPositionService")) {
            Intent intent = new Intent(this, TimingPositionService.class);
            startService(intent);
        }

        // 打开定时提交定位信息服务
        if (!TMSCommonUtils.isServiceRunning(this, "com.youcoupon.john_li.transportationapp.TMSService.PostPositionService")) {
            Intent intent = new Intent(this, PostPositionService.class);
            startService(intent);
        }

        // 清楚一个星期前的定位数据
        TMSCommonUtils.deleteSevenDaysAgoPosition();

        // 检查登录状态及更新状态
        updateStatus = new HashMap<>();
        updateStatus.put("40CsutomerStatus", false);
        updateStatus.put("40InovieStatus", true);
        updateStatus.put("40MaterialStatus", false);
        updateStatus.put("40MaterialCorrespondenceStatus", false);
        updateStatus.put("72CustomerStatus", true);
        updateStatus.put("72OrderStatus", true);
        String loginMsg = String.valueOf(SpuUtils.get(this, "loginMsg", ""));
        if (!loginMsg.equals("") && !loginMsg.equals("null")) {    // 判斷是否有登錄記錄
            mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
            boolean flag = false;
            UserModel cachModel = null;
            for (UserModel model : mUserModelList) {
                if (model.getLoginTime().equals(TMSCommonUtils.getTimeToday())) { // 當有登錄記錄的情況下，判斷是否是今天登錄的
                    TMSShareInfo.mUserModelList.add(model);
                    flag = true;
                    if (model.getCorp().equals("40") || model.getCorp().equals("XX") || model.getCorp().equals("72")){      // 40：可乐，72：BLS OK
                        cachModel = model;
                    }
                }
            }

            // 為當天登錄信息
            if (flag) {
                if (cachModel != null) {
                    if (cachModel.getCorp().equals("40")) {
                        // 當是为40时今天時判斷上次是否更新完全cachModel.isMaterialTbStatus()和isInvoiceTbStatus()
                        if (cachModel.isCustomerTbStatus() && cachModel.isInvoiceTbStatus() && cachModel.isMaterialTbStatus() && cachModel.isMaterialCorrespondenceTbStatus()) {
                            initView();
                            setListener();
                            initData();
                            TMSApplication.setDebug(false);
                        } else {
                            // 當是今天，但上次更新有失敗的情況則再次更新,但不刪除業務資料
                            // 判断需要更新资料的公司，分别由40、72、XX
                            mLoadDialog = new ProgressDialog(MainActivity.this);
                            mLoadDialog.setTitle("提示");
                            mLoadDialog.setMessage("正在更新資料......");
                            mLoadDialog.setCancelable(false);
                            mLoadDialog.show();
                            update40CustomerData();
                            updateMaterialData();
                            updateMaterialCorrespondenceData();
                            updateInovieData();
                            //getMaterilList();
                        }
                    } else if (cachModel.getCorp().equals("72")){   // 當是为72时今天時判斷上次是否更新完全cachModel.isMaterialTbStatus()和isInvoiceTbStatus()
                        if (cachModel.isClockInCustomerTbStatus() && cachModel.isClockInOrderSatusTbStatus()) {    // 當是为72时今天時判斷上次是否更新完全cachModel.isClockInCustomerTbStatus()和isClockInOrderSatusTbStatus()
                            initView();
                            setListener();
                            initData();
                            TMSApplication.setDebug(false);
                        } else {
                            // 當是今天，但上次更新有失敗的情況則再次更新
                            try {
                                TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
                                TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
                                TMSApplication.db.delete(ClockInPhotoInfo.class); //child_info表中数据将被全部删除
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // 判断需要更新资料的公司，分别由40、72、XX
                            mLoadDialog = new ProgressDialog(MainActivity.this);
                            mLoadDialog.setTitle("提示");
                            mLoadDialog.setMessage("正在更新資料......");
                            mLoadDialog.setCancelable(false);
                            mLoadDialog.show();
                            forcedUpdate72Data();
                            update72CustomerData();
                        }
                    } else {    //为XX测试公司
                        if (cachModel.isCustomerTbStatus() && cachModel.isInvoiceTbStatus() && cachModel.isClockInCustomerTbStatus() && cachModel.isClockInOrderSatusTbStatus() ) {    // 當是为40时今天時判斷上次是否更新完全cachModel.isMaterialTbStatus()和isInvoiceTbStatus()
                            initView();
                            setListener();
                            initData();
                        } else {
                            // 當是今天，但上次更新有失敗的情況則再次更新
                            // 判断需要更新资料的公司，分别由40、72、XX
                            mLoadDialog = new ProgressDialog(MainActivity.this);
                            mLoadDialog.setTitle("提示");
                            mLoadDialog.setMessage("正在更新資料......");
                            mLoadDialog.setCancelable(false);
                            mLoadDialog.show();
                            update40CustomerData();
                            updateMaterialData();
                            updateMaterialCorrespondenceData();
                            updateInovieData();
                            //getMaterilList();

                            forcedUpdate72Data();
                            update72CustomerData();
                        }
                    }
                } else {
                    // 其他公司，例如70
                    initView();
                    setListener();
                    initData();
                    TMSApplication.setDebug(false);
                }
            } else {
                // 當不是當天的登錄資料，清除資料並重新登錄
                clearDBData();
                SpuUtils.put(this, "loginMsg", "");
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
            }
        } else {
            // 當不是當天的登錄資料，清除資料並重新登錄
            clearDBData();
            SpuUtils.put(this, "loginMsg", "");
            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
        }

        // 检查时间
        TMSCommonUtils.checkTimeByUrl(this);
    }

    @Override
    public void initView() {
        textView = (TextView) findViewById(R.id.username_tv);
        headView = (TMSHeadView) findViewById(R.id.main_head);
        menuGv = (GridView) findViewById(R.id.main_gv);
    }

    @Override
    public void setListener() {
        menuGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        delieverGoods();
                        break;
                    case 1:
                        todayOrder();
                        break;
                    case 2:
                        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                            closeAccount();
                        } else {
                            startActivityForResult(new Intent(MainActivity.this, InvoiceStateActivity.class), 3);
                        }
                        break;
                    case 3:
                        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                            //當用沒被鎖定則提示用戶操作分車將鎖定用戶
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("系統提示")
                                    .setMessage("開始執行分車動作後您將鎖定賬戶，其他設備將無法操作該賬戶，直至您完成分車操作，是否確定!")
                                    .setPositiveButton("確定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    confrimInvoiceDivide();
                                                    dialog.dismiss();
                                                }
                                            })
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();

                        } else {
                            changeCorp();
                        }
                        break;
                    case 4:
                        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                            startActivityForResult(new Intent(MainActivity.this, InvoiceStateActivity.class), 3);
                        } else {
                            showUpdateDialog();
                        }
                        break;
                    case 5:
                        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                            changeCorp();
                        } else {
                            circleKClockIn();
                        }
                        break;
                    case 6:
                        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                            showUpdateDialog();
                        } else {
                            reUploadPhoto();
                        }
                        break;
                    case 7:
                        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                            circleKClockIn();
                        } else {
                            loginOut();
                        }

                        break;
                    case 8:
                        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                            reUploadPhoto();
                        } else {
                            loginOut();
                        }
                        break;
                    case 9:
                        loginOut();
                        break;
                    default:

                        break;

                }
            }
        });
    }

    @Override
    public void initData() {
        headView.setTitle("首頁");

        menuList = new ArrayList<>();
        initMenu();
        mAdapter = new MainAdapter(this, menuList);
        menuGv.setAdapter(mAdapter);
        textView.setText("用戶名：" + TMSShareInfo.mUserModelList.get(0).getID() + "   中文名：" + TMSShareInfo.mUserModelList.get(0).getNameChinese());
    }

    /** 
     * 初始化菜單項
     */
    private void initMenu() {
        menuList.add("物料回收");
        menuList.add("業務審核");
        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
            menuList.add("物料結算");
        }
        if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
            menuList.add("發票分車");
        }
        //menuList.add("發票分車");
        menuList.add("客戶簽收");
        menuList.add("切換公司");
        menuList.add("數據更新");
        menuList.add("OK簽到");
        menuList.add("重交相片");
        menuList.add("登出");
    }

    /**
     * 清空过往数据
     */
    private void clearDBData() {
        try {

            TMSApplication.db.delete(TrainsInfo.class);
            TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
            TMSApplication.db.delete(MaterialNumberInfo.class); //child_info表中数据将被全部删除
            TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
            TMSApplication.db.delete(ClockInPhotoInfo.class); //child_info表中数据将被全部删除
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*------------------------------------------------------------------------更新数据-----------------------------------------------------------------------------------*/
    /**
     * 更新40公司下该账户的客户资料
     * 上次有更新失敗情況，強制要求更新
     */
    private void update40CustomerData() {
        updateStatus.put("40CsutomerStatus", true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        paramsMap.put("salesmanid", TMSCommonUtils.getUserFor40(this).getSalesmanID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_CUSTOMER_LIST + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        List<CustomerInfo> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData().toString()), new TypeToken<List<CustomerInfo>>() {}.getType());
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("40") || mUserModelList.get(i).getCorp().equals("XX")) {
                                mUserModelList.get(i).setCustomerTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setCustomerTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取客戶資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "獲取客戶資料失敗！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "獲取客戶資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "獲取客戶資料網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "獲取客戶資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                updateStatus.put("40CsutomerStatus", true);
                mHandle.sendEmptyMessageDelayed(2, 1 * 1000);
            }
        });
    }

    /**
     * 更新该账户40公司的发票列表，for拉取物料回收时发票列表
     */
    private void updateInovieData() {
        updateStatus.put("40InovieStatus", true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        paramsMap.put("salesmanid", TMSCommonUtils.getUserFor40(this).getDriverID());
        paramsMap.put("truckID", TMSCommonUtils.getUserFor40(this).getTruckID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_TODAY_INVOICE_LIST + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        String invoiceJson = TMSCommonUtils.decode(commonModel.getData().toString());
                        List<InvoiceViewModel> list = new Gson().fromJson(invoiceJson, new TypeToken<List<InvoiceViewModel>>() {}.getType());
                        //ViewModel转Model
                        List<InvoiceInfo> infos = new ArrayList<>();
                        for (InvoiceViewModel model : list) {
                            InvoiceInfo info = new InvoiceInfo();
                            //info.setId(model.getHeader().getInvoiceNo());
                            info.setInvoiceNo(model.getHeader().getInvoiceNo());
                            info.setCustomerID(model.getHeader().getCustomerID());
                            info.setRemark(model.getHeader().getRemark());
                            info.setLines(new Gson().toJson(model.getLineList()));
                            infos.add(info);
                        }
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(infos);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("40") || mUserModelList.get(i).getCorp().equals("XX")) {
                                mUserModelList.get(i).setInvoiceTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setInvoiceTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取發票資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "獲取發票資料失敗！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "獲取發票資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "獲取發票資料網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "獲取發票資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mHandle.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    /**
     * 获取物料回收的物料列表
     */
    private void updateMaterialData() {
        updateStatus.put("40MaterialStatus", true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_MATERIAL_LIST + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        String matrialJson = TMSCommonUtils.decode(commonModel.getData().toString());
                        List<MaterialNumberInfo> list = new Gson().fromJson(matrialJson, new TypeToken<List<MaterialNumberInfo>>() {}.getType());

                        // 當就資料中存在就直接寫入新資料列表中，保證更新時不會剔除原來的數量
                        List<MaterialNumberInfo> all = TMSApplication.db.selector(MaterialNumberInfo.class).findAll();
                        if(all != null) {
                            for(MaterialNumberInfo info :all){
                                for(MaterialNumberInfo newInfo :list) {
                                    if (newInfo.getMaterialID().equals(info.getMaterialID())) {
                                        newInfo.setMaterialRefundNum(info.getMaterialRefundNum());
                                        newInfo.setMaterialDepositeNum(info.getMaterialDepositeNum());
                                    }
                                }
                            }
                        }

                        TMSApplication.db.delete(MaterialNumberInfo.class);

                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        // 记录更新状态
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("40") || mUserModelList.get(i).getCorp().equals("XX")) {
                                mUserModelList.get(i).setMaterialTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setMaterialTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取物料資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "獲取物料資料失敗！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "獲取物料資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "獲取物料資料網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "獲取物料資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                updateStatus.put("40MaterialStatus", true);
                mHandle.sendEmptyMessageDelayed(1, 1000);
                initView();
                setListener();
                initData();
            }
        });
    }

    /**
     * 获取物料回收时物料及商品的对应关系
     */
    private void updateMaterialCorrespondenceData() {
        updateStatus.put("40MaterialCorrespondenceStatus", true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_MATERIAL_CORRESPONDENCE_LIST + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        List<MaterialCorrespondenceModel> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData().toString()), new TypeToken<List<MaterialCorrespondenceModel>>() {}.getType());
                        //Model轉ViewModel
                        List<MaterialCorrespondenceInfo> infoList = new ArrayList<>();
                        for (MaterialCorrespondenceModel model : list) {
                            MaterialCorrespondenceInfo info = new MaterialCorrespondenceInfo();
                            info.setMerchandiseID(model.getMerchandiseID());
                            info.setMaterialListJson(new Gson().toJson(model.getMaterial()));
                            infoList.add(info);
                        }

                        TMSApplication.db.delete(MaterialCorrespondenceInfo.class);

                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(infoList);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("40") || mUserModelList.get(i).getCorp().equals("XX")) {
                                mUserModelList.get(i).setMaterialCorrespondenceTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setMaterialCorrespondenceTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取物料關係資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "獲取物料關係資料失敗！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "獲取物料關係資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "獲取物料關係資料網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "獲取物料關係資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                updateStatus.put("40MaterialCorrespondenceStatus", true);
                mHandle.sendEmptyMessageDelayed(1, 1000);
                initView();
                setListener();
                initData();
            }
        });
    }

    /**
     * 获取72公司打卡路线
     */
    private void forcedUpdate72Data() {
        updateStatus.put("72OrderStatus", true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor72(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor72(this).getID());
        paramsMap.put("driverid", TMSCommonUtils.getUserFor72(this).getDriverID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_CIRCLE_ORDER_STATUS + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        List<ClockInOrderStatusInfo> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData().toString()), new TypeToken<List<ClockInOrderStatusInfo>>() {}.getType());
                        //清空路线表
                        TMSApplication.db.delete(ClockInOrderStatusInfo.class);
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("72") || mUserModelList.get(i).getCorp().equals("XX")) {
                                mUserModelList.get(i).setClockInOrderSatusTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setClockInOrderSatusTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取OK簽到路線成功！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "獲取OK簽到路線失敗！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "獲取OK簽到路線失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "獲取OK簽到路線網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "獲取OK簽到路線失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                updateStatus.put("72OrderStatus", true);
                mHandle.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    /**
     * 获取72公司的客户资料
     */
    private void update72CustomerData() {
        updateStatus.put("72CustomerStatus", true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor72(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor72(this).getID());
        paramsMap.put("salesmanid", TMSCommonUtils.getUserFor72(this).getSalesmanID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_CUSTOMER_LIST + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        List<ClockInCustomerInfo> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData().toString()), new TypeToken<List<ClockInCustomerInfo>>() {}.getType());
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("72") || mUserModelList.get(i).getCorp().equals("XX")) {
                                mUserModelList.get(i).setClockInCustomerTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setClockInCustomerTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取OK簽到客戶資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "獲取OK簽到客戶資料失敗！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "獲取OK簽到客戶資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "獲取OK簽到客戶資料網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "獲取OK簽到客戶資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                updateStatus.put("72CustomerStatus", true);
                mHandle.sendEmptyMessageDelayed(1, 1000);
                initView();
                setListener();
                initData();
            }
        });
    }
    /**
     * 测试：获取所有物料的资料
     */
    private void getMaterilList() {
        try {
            TMSApplication.db.delete(MaterialNumberInfo.class);
            // 初始化物料总数
            MaterialNumberInfo materialNumberInfo = new MaterialNumberInfo();
            materialNumberInfo.setMaterialID("013A");
            materialNumberInfo.setMaterialName("木卡板");
            materialNumberInfo.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo);
            MaterialNumberInfo materialNumberInfo1 = new MaterialNumberInfo();
            materialNumberInfo1.setMaterialID("013B");
            materialNumberInfo1.setMaterialName("膠卡板(大)");
            materialNumberInfo1.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo1);
            MaterialNumberInfo materialNumberInfo2 = new MaterialNumberInfo();
            materialNumberInfo2.setMaterialID("013D");
            materialNumberInfo2.setMaterialName("專用膠卡板");
            materialNumberInfo2.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo2);
            MaterialNumberInfo materialNumberInfo3 = new MaterialNumberInfo();
            materialNumberInfo3.setMaterialID("013C");
            materialNumberInfo3.setMaterialName("膠片(5加侖)");
            materialNumberInfo3.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo3);
            MaterialNumberInfo materialNumberInfo4 = new MaterialNumberInfo();
            materialNumberInfo4.setMaterialID("014");
            materialNumberInfo4.setMaterialName("5加侖吉膠桶");
            materialNumberInfo4.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo4);
            MaterialNumberInfo materialNumberInfo5 = new MaterialNumberInfo();
            materialNumberInfo5.setMaterialID("015");
            materialNumberInfo5.setMaterialName("飛雪吉膠箱");
            materialNumberInfo5.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo5);
            MaterialNumberInfo materialNumberInfo6 = new MaterialNumberInfo();
            materialNumberInfo6.setMaterialID("013E");
            materialNumberInfo6.setMaterialName("綠色膠卡板");
            materialNumberInfo6.setMaterialRefundNum(0);
            materialNumberInfo6.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo6);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initView();
        setListener();
        initData();
        TMSApplication.setDebug(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {     // 登录
                mUserModelList = TMSShareInfo.mUserModelList;

                for (UserModel model : mUserModelList) {
                    if (model.getLoginTime().equals(TMSCommonUtils.getTimeToday())) { // 當有登錄記錄的情況下，判斷是否是今天登錄的
                        TMSShareInfo.mUserModelList.add(model);
                        if (model.getCorp().equals("40")) {
                            if (model.isCustomerTbStatus() && model.isInvoiceTbStatus()) {    // 當是今天時判斷上次是否更新完全 && cachModel.isMaterialTbStatus()
                                initView();
                                setListener();
                                initData();
                                TMSApplication.setDebug(false);
                            } else {
                                // 當是今天，但上次更新有失敗的情況則再次更新
                                clearDBData();

                                // 判断需要更新资料的公司，分别由40、72、XX
                                mLoadDialog = new ProgressDialog(MainActivity.this);
                                mLoadDialog.setTitle("提示");
                                mLoadDialog.setMessage("正在更新資料......");
                                mLoadDialog.setCancelable(false);
                                mLoadDialog.show();
                                update40CustomerData();
                                updateMaterialData();
                                updateMaterialCorrespondenceData();
                                updateInovieData();
                                //getMaterilList();
                            }
                        } else if (model.getCorp().equals("72")) {
                            if (model.isClockInOrderSatusTbStatus() && model.isClockInCustomerTbStatus()) {    // 當是今天時判斷上次是否更新完全 && cachModel.isMaterialTbStatus()
                                initView();
                                setListener();
                                initData();
                                TMSApplication.setDebug(false);
                            } else {
                                // 當是今天，但上次更新有失敗的情況則再次更新
                                try {
                                    TMSApplication.db.delete(ClockInPhotoInfo.class); //child_info表中数据将被全部删除
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                forcedUpdate72Data();
                                update72CustomerData();
                            }
                        } else if (model.getCorp().equals("XX")) {
                            if (model.isCustomerTbStatus() && model.isInvoiceTbStatus() && model.isClockInCustomerTbStatus() && model.isClockInOrderSatusTbStatus() ) {    // 當是为40时今天時判斷上次是否更新完全cachModel.isMaterialTbStatus()和isInvoiceTbStatus()
                                initView();
                                setListener();
                                initData();
                            } else {
                                // 當是今天，但上次更新有失敗的情況則再次更新
                                clearDBData();
                                // 判断需要更新资料的公司，分别由40、72、XX
                                mLoadDialog = new ProgressDialog(MainActivity.this);
                                mLoadDialog.setTitle("提示");
                                mLoadDialog.setMessage("正在更新資料......");
                                mLoadDialog.setCancelable(false);
                                mLoadDialog.show();
                                update40CustomerData();
                                updateMaterialData();
                                updateMaterialCorrespondenceData();
                                updateInovieData();
                                //getMaterilList();
                                forcedUpdate72Data();
                                update72CustomerData();
                            }
                        } else {
                            initView();
                            setListener();
                            initData();
                            TMSApplication.setDebug(false);
                        }
                    }
                }
            } else if (requestCode == 2){   // 送/收货
                String result = data.getStringExtra("result");
                if (result.equals("1")) {
                    startActivityForResult(new Intent(MainActivity.this, InvoiceStateActivity.class), 3);
                } else if (result.equals("2")){
                    circleKClockIn();
                }
            } else if (requestCode == 3){   // 扫描发票
                String result = data.getStringExtra("result");
                if (result.equals("1")) {
                    delieverGoods();
                } else if (result.equals("2")){
                    circleKClockIn();
                }
            } else if (requestCode == 4){   // Circle-K打卡
                String result = data.getStringExtra("result");
                if (result.equals("1")) {
                    delieverGoods();
                } else if (result.equals("2")){
                    startActivityForResult(new Intent(MainActivity.this, InvoiceStateActivity.class), 3);
                }
            }
        }
    }

    /*------------------------------------------------------------------------主菜单按钮-----------------------------------------------------------------------------------*/
    private void delieverGoods() {
        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag = false;
        for (UserModel model : userModelList) {
            if (model.getCorp().equals("40") ||model.getCorp().equals("XX")) {
                flag = true;
            }
        }

        if (!flag) {
            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(new Intent(MainActivity.this, DeliverGoodsActivity.class), 2);
        }
    }

    private void todayOrder() {
        List<UserModel> userModelList1 = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag1 = false;
        for (UserModel model : userModelList1) {
            if (model.getCorp().equals("40") || model.getCorp().equals("XX")) {
                flag1 = true;
            }
        }

        if (!flag1) {
            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(MainActivity.this, TodayInvoiceListActivity.class));
        }
    }

    private void closeAccount() {
        //判斷登錄戶口為40的
        List<UserModel> userModelList2 = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag1 = false;
        for (UserModel model : userModelList2) {
            if (model.getCorp().equals("40") || model.getCorp().equals("XX")) {
                flag1 = true;
            }
        }
        // 判斷車組發票已提交成功
        boolean flag2 = false;
        List<SubmitInvoiceInfo> all = null;
        try {
            all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            if(all != null){
                for (SubmitInvoiceInfo submitInvoiceList : all) {
                    if (submitInvoiceList.getDepositStatus() != 1 || submitInvoiceList.getRefundStatus() != 1){
                        flag2 = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!flag1) {
            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
        } else if(flag2) {
            Toast.makeText(MainActivity.this, "您尚有未提交成功的發票，請先提交成功再結算！", Toast.LENGTH_SHORT).show();
        }else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("系統提示")
                    .setMessage("結算前會拉取車隊本車次所有數據，請確認!")
                    .setPositiveButton("確認",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    // 拉取车队本车次所有数据
                                    pullInvoiceThisTime();
                                }
                            })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 啟動更新界面
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void changeCorp() {
        View dialogView = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        dialogView = inflater.inflate(R.layout.dialog_login_list, null);
        builder.setView(dialogView);
        // 初始化dialog中所以控件
        final Dialog loginDialog = builder.create();
        ListView lv = dialogView.findViewById(R.id.dialog_login_list_lv);
        TextView cancelTv = dialogView.findViewById(R.id.dialog_login_cancel);
        TextView ohterTv = dialogView.findViewById(R.id.dialog_login_other);
        mUserModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        lv.setAdapter(new LoginListAdapter(MainActivity.this, mUserModelList));
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.dismiss();
            }
        });
        ohterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("startWay", "1");
                startActivity(intent);
                loginDialog.dismiss();
            }
        });
        loginDialog.show();
    }

    private void dataUpdate() {
        List<UserModel> modelList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag3 = false;
        for (UserModel model : modelList) {
            if (model.getCorp().equals("40") || model.getCorp().equals("XX") || model.getCorp().equals("72")) {
                flag3 = true;
            }
        }

        if (!flag3) {
            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司或澳門Circle-K賬戶！", Toast.LENGTH_SHORT).show();
        } else {
            if (TMSCommonUtils.getUserForXX(MainActivity.this) != null) {
                // 判断需要更新资料的公司，分别由40、72、XX
                mLoadDialog = new ProgressDialog(MainActivity.this);
                mLoadDialog.setTitle("提示");
                mLoadDialog.setMessage("正在更新資料......");
                mLoadDialog.setCancelable(false);
                mLoadDialog.show();
                update40CustomerData();
                updateMaterialData();
                updateMaterialCorrespondenceData();
                updateInovieData();
                //getMaterilList();
                forcedUpdate72Data();
                update72CustomerData();
            } else if (TMSCommonUtils.getUserFor72(MainActivity.this) != null){
                // 判断需要更新资料的公司，分别由40、72、XX
                mLoadDialog = new ProgressDialog(MainActivity.this);
                mLoadDialog.setTitle("提示");
                mLoadDialog.setMessage("正在更新資料......");
                mLoadDialog.setCancelable(false);
                mLoadDialog.show();
                forcedUpdate72Data();
                update72CustomerData();
            } else {
                // 判断需要更新资料的公司，分别由40、72、XX
                mLoadDialog = new ProgressDialog(MainActivity.this);
                mLoadDialog.setTitle("提示");
                mLoadDialog.setMessage("正在更新資料......");
                mLoadDialog.setCancelable(false);
                mLoadDialog.show();
                update40CustomerData();
                updateMaterialData();
                updateMaterialCorrespondenceData();
                updateInovieData();
                //getMaterilList();
            }
        }
    }

    private void circleKClockIn() {
        List<UserModel> userList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag4 = false;
        for (UserModel model : userList) {
            if (model.getCorp().equals("72") ||model.getCorp().equals("XX")) {
                flag4 = true;
            }
        }

        if (!flag4) {
            Toast.makeText(MainActivity.this, "請先登錄澳門OK(BLS)賬戶！", Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(new Intent(MainActivity.this, ClockInOrderStatusActivity.class), 4);
        }
    }

    private void reUploadPhoto() {
        // 加入IntentService队列
        try {
            List<ClockInPhotoInfo> clockInPhotoInfoList = TMSApplication.db.selector(ClockInPhotoInfo.class).where("status", "!=", "1").findAll();
            if (clockInPhotoInfoList != null) {
                for (ClockInPhotoInfo clockInPhotoInfo : clockInPhotoInfoList) {
                    Intent intent = new Intent(MainActivity.this, PostPhotoService.class);
                    intent.putExtra("ClockInPhotoInfo", new Gson().toJson(clockInPhotoInfo));
                    MainActivity.this.startService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginOut() {
        try {
            boolean b = false;
            List<SubmitInvoiceInfo> all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            if (all != null) {
                for(SubmitInvoiceInfo info :all){
                    if (info.getDepositStatus() != 1 || info.getDepositStatus() != 1) {
                        b = true;
                    }
                }
            }

            MaterialNumberInfo first = TMSApplication.db.findFirst(MaterialNumberInfo.class);
            if (b) {
                Toast.makeText(MainActivity.this, "您有未提交成功訂單，請完成！", Toast.LENGTH_SHORT).show();
            } else {
                if (TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("D") || TMSShareInfo.mUserModelList.get(0).getID().substring(0,1).equals("d")) {
                    if (first != null) {
                        int refund = first.getMaterialRefundNum();
                        int deposite = first.getMaterialDepositeNum();
                        if (refund == 0 && deposite == 0) {
                            showLoginOutDialog();
                        } else {
                            Toast.makeText(MainActivity.this, "您有物料未結算，請完成！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showLoginOutDialog();
                    }
                } else {
                    showLoginOutDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 顯示登出dialog
     */
    private void showLoginOutDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("系統提示")
                .setMessage("您即將登出系統!")
                .setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 啟動更新界面
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("註銷并退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callNetLoginOut();
                    }
                })
                .setNeutralButton("登出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                })
                .create()
                .show();
    }

    /**
     * 顯示数据更新dialog
     */
    private void showUpdateDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("系統提示")
                .setMessage("是否確定更新數據!")
                .setPositiveButton("確認",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 啟動更新界面
                                dataUpdate();
                                dialog.dismiss();
                            }
                        })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * 登出
     */
    private void callNetLoginOut() {
        mLoadDialog = new ProgressDialog(this);
        mLoadDialog.setTitle("提示");
        mLoadDialog.setMessage("正在退出系統......");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.LOGIN_OUT_API + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel model = new Gson().fromJson(result, CommonModel.class);
                if (model.getCode() == 0) {
                    Toast.makeText(MainActivity.this, "登出成功！", Toast.LENGTH_SHORT).show();
                    SpuUtils.put(MainActivity.this, "loginMsg", "");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLoadDialog.dismiss();
                            System.exit(0);
                        }
                    }, 3000);
                } else {
                    mLoadDialog.dismiss();
                    Toast.makeText(MainActivity.this, "登出失敗，請重試", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadDialog.dismiss();
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(MainActivity.this, "網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "登出失敗，請重新提交", Toast.LENGTH_SHORT).show();
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

    /**
     * 拉取车队本车次所有数据
     */
    private void pullInvoiceThisTime() {
        mLoadDialog = new ProgressDialog(this);
        mLoadDialog.setTitle("提示");
        mLoadDialog.setMessage("正在拉取車隊本車次所有數據......");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
        try {
            // 獲取車次
            Thread t = new Thread() {
                @Override
                public void run() {
                    TMSBussinessUtils.asyncLastTrunkNo(MainActivity.this,this);
                }
            };
            t.run();
            t.join();
            long trunkNumber = TMSCommonUtils.searchTrainsInfoMaxTimes();
            if (trunkNumber == 0) {
                trunkNumber = 1;
            }

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
            paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
            paramsMap.put("truckID", TMSShareInfo.mUserModelList.get(0).getTruckID());
            paramsMap.put("truckNumber", String.valueOf(trunkNumber));
            RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_TRUNK_INVOICE + TMSCommonUtils.createLinkStringByGet(paramsMap));
            params.setConnectTimeout(30 * 1000);
            String uri = params.getUri();
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                    if (commonModel.getCode() == 0) {
                        String invoiceJson = TMSCommonUtils.decode(commonModel.getData().toString());
                        List<InvoiceThisVhiclePullModel> list = new Gson().fromJson(invoiceJson, new TypeToken<List<InvoiceThisVhiclePullModel>>() {}.getType());
                        if (list.size() > 0) {
                            List<SubmitInvoiceInfo> all = null;
                            try {
                                all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
                                if (all == null) {
                                    all = new ArrayList<>();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            for (InvoiceThisVhiclePullModel pullInvoiceModel : list) {
                                boolean isExit = false;
                                for (SubmitInvoiceInfo submitInvoiceList : all) {
                                    if (pullInvoiceModel.getHeader().getInvoiceNo().equals(submitInvoiceList.getInvoiceNo()) || pullInvoiceModel.getHeader().getInvoiceNo().equals(submitInvoiceList.getSunInvoiceNo())){
                                        isExit = true;
                                    }
                                }

                                if (!isExit) {
                                    // 判斷是否存在該發票，PDA提交失敗，但服務器已提交成功
                                    boolean flag = false;
                                    for (SubmitInvoiceInfo submitInvoiceList : all) {
                                        if (pullInvoiceModel.getHeader().getReference().equals(submitInvoiceList.getRefrence())){
                                            // 判斷是否為主單
                                            try {
                                                WhereBuilder b = WhereBuilder.b();
                                                b.and("refrence","=", pullInvoiceModel.getHeader().getReference()); //构造修改的条件
                                                KeyValue v1 = new KeyValue("invoice_no", pullInvoiceModel.getHeader().getInvoiceNo());
                                                KeyValue v2 = null;
                                                if (pullInvoiceModel.getLine().get(0).getQuantity() > 0) {
                                                    v2 = new KeyValue("depositStatus", 1);
                                                } else {
                                                    v2 = new KeyValue("refundStatus", 1);
                                                }
                                                TMSApplication.db.update(SubmitInvoiceInfo.class,b,v1,v2);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }finally {
                                                flag = true;
                                                break;
                                            }
                                        }

                                        if (pullInvoiceModel.getHeader().getReference().equals(submitInvoiceList.getSunRefrence())) {
                                            // 判斷是否為子單
                                            try {
                                                WhereBuilder b = WhereBuilder.b();
                                                b.and("sun_refrence","=", pullInvoiceModel.getHeader().getReference()); //构造修改的条件
                                                KeyValue v1 = new KeyValue("sun_invoice_no", pullInvoiceModel.getHeader().getInvoiceNo());
                                                KeyValue v2 = null;
                                                if (pullInvoiceModel.getLine().get(0).getQuantity() > 0) {
                                                    v2 = new KeyValue("depositStatus", 1);
                                                } else {
                                                    v2 = new KeyValue("refundStatus", 1);
                                                }
                                                TMSApplication.db.update(SubmitInvoiceInfo.class,b,v1,v2);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }finally {
                                                flag = true;
                                                break;
                                            }
                                        }
                                    }

                                    if (!flag) {
                                        // 都不存在插入該條數據
                                        try {
                                            List<DeliverInvoiceModel> deliverInvoiceModelList = pullInvoiceModel.transModelToDB(pullInvoiceModel.getLine());
                                            // 物料送出/回收记录
                                            SubmitInvoiceInfo mSubmitInvoiceInfo = new SubmitInvoiceInfo();
                                            mSubmitInvoiceInfo.setOrderBody(new Gson().toJson(deliverInvoiceModelList));
                                            mSubmitInvoiceInfo.setRefrence(pullInvoiceModel.getHeader().getReference());
                                            mSubmitInvoiceInfo.setSalesmanId(pullInvoiceModel.getHeader().getOperationID());
                                            mSubmitInvoiceInfo.setCustomerID(pullInvoiceModel.getHeader().getCustomerID());
                                            mSubmitInvoiceInfo.setCustomerName(pullInvoiceModel.getHeader().getCustomerName());
                                            mSubmitInvoiceInfo.setInvoiceNo(pullInvoiceModel.getHeader().getInvoiceNo());
                                            mSubmitInvoiceInfo.setDepositStatus(1);
                                            mSubmitInvoiceInfo.setRefundStatus(1);
                                            TMSApplication.db.save(mSubmitInvoiceInfo);

                                            //插入物料記錄
                                            for (DeliverInvoiceModel deliverInvoiceModel : deliverInvoiceModelList) {
                                                int depositNum = 0;
                                                int refundNum = 0;

                                                // 查詢該物料原本有多少回收及送出物料
                                                List<MaterialNumberInfo> allMaterialNumber = TMSApplication.db.selector(MaterialNumberInfo.class).where("material_number_id", "=", deliverInvoiceModel.getMaterialId()).findAll();
                                                if(allMaterialNumber == null) {
                                                    allMaterialNumber = new ArrayList<>();
                                                }
                                                for(MaterialNumberInfo model : allMaterialNumber) {
                                                    depositNum = model.getMaterialDepositeNum();
                                                    refundNum = model.getMaterialRefundNum();
                                                }

                                                // 修改物料總數量
                                                if (deliverInvoiceModel.getSendOutNum() > 0) {
                                                    WhereBuilder b = WhereBuilder.b();
                                                    b.and("material_number_id","=", deliverInvoiceModel.getMaterialId()); //构造修改的条件
                                                    KeyValue name = new KeyValue("material_deposite_num", depositNum + deliverInvoiceModel.getSendOutNum());
                                                    TMSApplication.db.update(MaterialNumberInfo.class,b,name);
                                                }

                                                if (deliverInvoiceModel.getRecycleNum() > 0) {
                                                    WhereBuilder b = WhereBuilder.b();
                                                    b.and("material_number_id","=", deliverInvoiceModel.getMaterialId()); //构造修改的条件
                                                    KeyValue name = new KeyValue("material_refund_num", refundNum + deliverInvoiceModel.getRecycleNum());
                                                    TMSApplication.db.update(MaterialNumberInfo.class,b,name);
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "拉取發票信息異常：" + e.getStackTrace() + "\n" + new Gson().toJson(pullInvoiceModel), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Error");
                                        }
                                    }

                                }
                            }
                        }

                        startActivity(new Intent(MainActivity.this, CloseAccountActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, String.valueOf(commonModel.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    if (ex instanceof java.net.SocketTimeoutException) {
                        Toast.makeText(MainActivity.this, "網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "獲取資料失敗，請重新提交", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                    mLoadDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mLoadDialog.dismiss();
            Toast.makeText(this, "未獲取到車次，請重試或聯繫技術人員！" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 確認分車
     */
    private void confrimInvoiceDivide() {
        mLoadDialog = new ProgressDialog(this);
        mLoadDialog.setTitle("請稍等");
        mLoadDialog.setMessage("正在檢查您的操作狀態......");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        paramsMap.put("IMEI", TMSShareInfo.IMEI);
        paramsMap.put("salesmanid", TMSShareInfo.mUserModelList.get(0).getSalesmanID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.CHECK_ONLINE_USER + TMSCommonUtils.createLinkStringByGet(paramsMap));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final CommonModel commonModel = new  Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    // 判斷目前倉庫日結方式拉取發票
                    if (commonModel.getData().toString().equals("2")) {
                        dismissDialog();
                        // 按照出倉日期方式则拉取一个月前到目前的發票
                        callNetPushSplitInvoices(TMSCommonUtils.getLastMonthDate(), TMSCommonUtils.getTomorrowDate());
                    } else {
                        dismissDialog();
                        // 按照發票日期則需要選擇時間
                        InvoiceDivideByDate();
                    }
                } else {
                    dismissDialog();
                    //當用沒被鎖定則提示用戶操作分車將鎖定用戶
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("系統提示")
                            .setMessage(String.valueOf(commonModel.getMessage()) + "，您的賬戶已被其他設備鎖定，請先退出或聯繫辦公人員解除鎖定!")
                            .setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })

                            .create()
                            .show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
                doCheckOut();
                Toast.makeText(MainActivity.this, "檢查用戶狀態異常！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dismissDialog();
            }
        });
    }

    /**
     * 按發票日期方式拉取發票分車
     */
    private void InvoiceDivideByDate() {
        // 獲取分車方式，傳統為獲取今明兩天發票，新版為獲取未出車發票
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("选择日期")
                .setMessage("請選擇分車日期!")
                .setPositiveButton("今天",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                callNetPushSplitInvoices(TMSCommonUtils.getTimeToday(), "0001-01-01");
                            }
                        })
                .setNegativeButton("明天", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callNetPushSplitInvoices(TMSCommonUtils.getTomorrowDate(), "0001-01-01");
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLoadDialog = new ProgressDialog(MainActivity.this);
                        mLoadDialog.setTitle("加載");
                        mLoadDialog.setMessage("正在退出操作......");
                        mLoadDialog.setCancelable(false);
                        mLoadDialog.show();
                        doCheckOut();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * 拉取分車列表
     */
    private void callNetPushSplitInvoices(String startDate, String endDate) {
        mLoadDialog = new ProgressDialog(this);
        mLoadDialog.setTitle("請稍等");
        mLoadDialog.setMessage("正在拉取發票，該操作可能需要一段時間，請勿息屏......");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("DriverID", TMSShareInfo.mUserModelList.get(0).getDriverID());
        paramsMap.put("StartDate", startDate);
        paramsMap.put("EndDate", endDate);
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        //paramsMap.put("salesmanid", TMSShareInfo.mUserModelList.get(0).getSalesmanID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_SPLIT_INVOICE + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(1 * 60 * 1000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel model = new Gson().fromJson(result, CommonModel.class);
                if (model.getCode() == 0) {
                    String invoiceJson = TMSCommonUtils.decode(model.getData().toString());
                    Intent intent1 = new Intent(MainActivity.this, ChooseTrainNumActivity.class);
                    intent1.putExtra("InvoiceList", invoiceJson);
                    startActivity(intent1);

                    dismissDialog();
                } else {
                    doCheckOut();
                    dismissDialog();
                    Toast.makeText(MainActivity.this, "拉取分車發票失敗！" + String.valueOf(model.getMessage()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                doCheckOut();
                dismissDialog();
                Toast.makeText(MainActivity.this, "拉取分車發票異常！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dismissDialog();
            }
        });
    }

    /**
     * 登出操作
     */
    private void doCheckOut() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        paramsMap.put("DriverID", TMSShareInfo.mUserModelList.get(0).getDriverID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.CHECK_OUT_OCCUPY + TMSCommonUtils.createLinkStringByGet(paramsMap));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel model = new Gson().fromJson(result, CommonModel.class);
                if (model.getCode() == 0) {
                } else {
                    Toast.makeText(MainActivity.this,  String.valueOf(model.getMessage()) + "，退出操作失败，请重试！", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this, "退出操作異常，请重试或聯繫IT人員！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dismissDialog();
            }
        });
    }

    /**
     * 关闭loadingdialog
     */
    private void dismissDialog() {
        if (mLoadDialog != null) {
            if (mLoadDialog.isShowing()) {
                mLoadDialog.dismiss();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
