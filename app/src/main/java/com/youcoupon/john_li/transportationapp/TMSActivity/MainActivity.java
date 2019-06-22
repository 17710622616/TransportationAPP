package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialNumberInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.PostPhotoService;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hardware.print.printer;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {
    printer mPrinter = new printer();
    TextView textView;
    private GridView menuGv;
    private TMSHeadView headView;

    private List<String> menuList;
    private List<UserModel> mUserModelList;
    private ProgressDialog dialog;
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
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                    break;
            }
        }
    };
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
        updateStatus = new HashMap<>();
        updateStatus.put("40CsutomerStatus", false);
        updateStatus.put("40InovieStatus", false);
        updateStatus.put("72CustomerStatus", false);
        updateStatus.put("72OrderStatus", false);
        String loginMsg = String.valueOf(SpuUtils.get(this, "loginMsg", ""));
        if (!loginMsg.equals("") && !loginMsg.equals("null")) {    // 判斷是否有登錄記錄
            mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
            boolean flag = false;
            UserModel cachModel = null;
            for (UserModel model : mUserModelList) {
                if (model.getLoginTime().equals(TMSCommonUtils.getTimeToday())) { // 當有登錄記錄的情況下，判斷是否是今天登錄的
                    TMSShareInfo.mUserModelList.add(model);
                    flag = true;
                    if (model.getCorp().equals("40") || model.getCorp().equals("xx") || model.getCorp().equals("72")){      // 40：可乐，72：BLS OK
                        cachModel = model;
                    }
                }
            }

            if (flag) {
                if (cachModel != null) {
                    if (cachModel.getCorp().equals("40")) {
                        if (cachModel.isCustomerTbStatus() && cachModel.isInvoiceTbStatus()) {    // 當是为40时今天時判斷上次是否更新完全cachModel.isMaterialTbStatus()和isInvoiceTbStatus()
                            initView();
                            setListener();
                            initData();
                            TMSApplication.setDebug(false);
                        } else {
                            // 當是今天，但上次更新有失敗的情況則再次更新
                            try {
                                TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
                                TMSApplication.db.delete(MaterialNumberInfo.class); //child_info表中数据将被全部删除
                                TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                            // 判断需要更新资料的公司，分别由40、72、xx
                            dialog = new ProgressDialog(MainActivity.this);
                            dialog.setTitle("提示");
                            dialog.setMessage("正在更新資料......");
                            dialog.setCancelable(false);
                            dialog.show();
                            forcedUpdate40Data();
                            updateInovieData();
                            getMaterilList();
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
                                TMSApplication.db.delete(ClockInPhotoInfo.class); //child_info表中数据将被全部删除
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                            // 判断需要更新资料的公司，分别由40、72、xx
                            dialog = new ProgressDialog(MainActivity.this);
                            dialog.setTitle("提示");
                            dialog.setMessage("正在更新資料......");
                            dialog.setCancelable(false);
                            dialog.show();
                            forcedUpdate72Data();
                            update72CustomerData();
                        }
                    } else {    //为xx测试公司
                        if (cachModel.isCustomerTbStatus() && cachModel.isInvoiceTbStatus() && cachModel.isClockInCustomerTbStatus() && cachModel.isClockInOrderSatusTbStatus() ) {    // 當是为40时今天時判斷上次是否更新完全cachModel.isMaterialTbStatus()和isInvoiceTbStatus()
                            initView();
                            setListener();
                            initData();
                        } else {
                            // 當是今天，但上次更新有失敗的情況則再次更新
                            try {
                                TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
                                TMSApplication.db.delete(MaterialNumberInfo.class); //child_info表中数据将被全部删除
                                TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
                                TMSApplication.db.delete(ClockInPhotoInfo.class); //child_info表中数据将被全部删除
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                            // 判断需要更新资料的公司，分别由40、72、xx
                            dialog = new ProgressDialog(MainActivity.this);
                            dialog.setTitle("提示");
                            dialog.setMessage("正在更新資料......");
                            dialog.setCancelable(false);
                            dialog.show();
                            forcedUpdate40Data();
                            updateInovieData();
                            getMaterilList();
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
                SpuUtils.put(this, "loginMsg", "");
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
            }
        } else {
            SpuUtils.put(this, "loginMsg", "");
            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
        }
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
                        closeAccount();
                        break;
                    case 3:
                        startActivityForResult(new Intent(MainActivity.this, InvoiceStateActivity.class), 3);
                        break;
                    case 4:
                        changeCorp();
                        break;
                    case 5:
                        dataUpdate();
                        break;
                    case 6:
                        circleKClockIn();
                        break;
                    case 7:
                        reUploadPhoto();
                        break;
                    case 8:
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
        menuGv.setAdapter(new MainAdapter(this, menuList));
        textView.setText("用戶名：" + TMSShareInfo.mUserModelList.get(0).getID() + "   中文名：" + TMSShareInfo.mUserModelList.get(0).getNameChinese());
    }

    /**
     * 初始化菜單項
     */
    private void initMenu() {
        menuList.add("送/收貨");
        menuList.add("今日訂單");
        menuList.add("結算");
        menuList.add("發票狀態");
        menuList.add("切換公司");
        menuList.add("數據更新");
        menuList.add("OK簽到");
        menuList.add("重交相片");
        menuList.add("登出");
    }

    /**
     * 上次有更新失敗情況，強制要求更新
     */
    private void forcedUpdate40Data() {
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
                        List<CustomerInfo> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData()), new TypeToken<List<CustomerInfo>>() {}.getType());
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("40") || mUserModelList.get(i).getCorp().equals("xx")) {
                                mUserModelList.get(i).setCustomerTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setCustomerTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取客戶資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
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
                updateStatus.put("40CsutomerStatus", false);
                mHandle.sendEmptyMessageDelayed(1, 5000);
            }
        });
    }

    private void updateInovieData() {
        updateStatus.put("40InovieStatus", true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        paramsMap.put("salesmanid", TMSCommonUtils.getUserFor40(this).getSalesmanID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_TODAY_INVOICE_LIST + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(30 * 1000);
        String uri = params.getUri();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        List<InvoiceInfo> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData()), new TypeToken<List<InvoiceInfo>>() {}.getType());
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("40") || mUserModelList.get(i).getCorp().equals("xx")) {
                                mUserModelList.get(i).setInvoiceTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setInvoiceTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取發票資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
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
                updateStatus.put("40InovieStatus", false);
                mHandle.sendEmptyMessageDelayed(1, 5000);
            }
        });
    }

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
                        List<ClockInOrderStatusInfo> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData()), new TypeToken<List<ClockInOrderStatusInfo>>() {}.getType());
                        //清空路线表
                        TMSApplication.db.delete(ClockInOrderStatusInfo.class);
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("72") || mUserModelList.get(i).getCorp().equals("xx")) {
                                mUserModelList.get(i).setClockInOrderSatusTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setClockInOrderSatusTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取OK簽到路線成功！", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
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
                updateStatus.put("72OrderStatus", false);
                mHandle.sendEmptyMessageDelayed(1, 5000);
            }
        });
    }

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
                        List<ClockInCustomerInfo> list = new Gson().fromJson(TMSCommonUtils.decode(commonModel.getData()), new TypeToken<List<ClockInCustomerInfo>>() {}.getType());
                        //用集合向child_info表中插入多条数据
                        //db.save()方法不仅可以插入单个对象，还能插入集合
                        TMSApplication.db.save(list);
                        String loginMsg = String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", ""));
                        List<UserModel> mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
                        for (int i = 0; i < mUserModelList.size(); i++) {
                            if (mUserModelList.get(i).getCorp().equals("72") || mUserModelList.get(i).getCorp().equals("xx")) {
                                mUserModelList.get(i).setClockInCustomerTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setClockInCustomerTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取OK簽到客戶資料成功！", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
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
                updateStatus.put("72CustomerStatus", false);
                mHandle.sendEmptyMessageDelayed(1, 5000);
                initView();
                setListener();
                initData();
            }
        });
    }

    /**
     * 获取所有物料的资料
     */
    private void getMaterilList() {
        try {
            TMSApplication.db.delete(MaterialNumberInfo.class);
            // 初始化物料总数
            MaterialNumberInfo materialNumberInfo = new MaterialNumberInfo();
            materialNumberInfo.setMaterialId("013A");
            materialNumberInfo.setMaterialName("木卡板");
            materialNumberInfo.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo);
            MaterialNumberInfo materialNumberInfo1 = new MaterialNumberInfo();
            materialNumberInfo1.setMaterialId("013B");
            materialNumberInfo1.setMaterialName("膠卡板(大)");
            materialNumberInfo1.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo1);
            MaterialNumberInfo materialNumberInfo2 = new MaterialNumberInfo();
            materialNumberInfo2.setMaterialId("013D");
            materialNumberInfo2.setMaterialName("專用膠卡板");
            materialNumberInfo2.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo2);
            MaterialNumberInfo materialNumberInfo3 = new MaterialNumberInfo();
            materialNumberInfo3.setMaterialId("013C");
            materialNumberInfo3.setMaterialName("膠片(5加侖)");
            materialNumberInfo3.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo3);
            MaterialNumberInfo materialNumberInfo4 = new MaterialNumberInfo();
            materialNumberInfo4.setMaterialId("014");
            materialNumberInfo4.setMaterialName("5加侖吉膠桶");
            materialNumberInfo4.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo4);
            MaterialNumberInfo materialNumberInfo5 = new MaterialNumberInfo();
            materialNumberInfo5.setMaterialId("015");
            materialNumberInfo5.setMaterialName("飛雪吉膠箱");
            materialNumberInfo5.setMaterialRefundNum(0);
            materialNumberInfo.setMaterialDepositeNum(0);
            TMSApplication.db.save(materialNumberInfo5);
        } catch (DbException e) {

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
                                try {
                                    TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
                                    TMSApplication.db.delete(MaterialNumberInfo.class); //child_info表中数据将被全部删除
                                    TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }

                                // 判断需要更新资料的公司，分别由40、72、xx
                                dialog = new ProgressDialog(MainActivity.this);
                                dialog.setTitle("提示");
                                dialog.setMessage("正在更新資料......");
                                dialog.setCancelable(false);
                                dialog.show();
                                forcedUpdate40Data();
                                updateInovieData();
                                getMaterilList();
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
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                forcedUpdate72Data();
                                update72CustomerData();
                            }
                        } else if (model.getCorp().equals("xx")) {
                            if (model.isCustomerTbStatus() && model.isInvoiceTbStatus() && model.isClockInCustomerTbStatus() && model.isClockInOrderSatusTbStatus() ) {    // 當是为40时今天時判斷上次是否更新完全cachModel.isMaterialTbStatus()和isInvoiceTbStatus()
                                initView();
                                setListener();
                                initData();
                            } else {
                                // 當是今天，但上次更新有失敗的情況則再次更新
                                try {
                                    TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
                                    TMSApplication.db.delete(MaterialNumberInfo.class); //child_info表中数据将被全部删除
                                    TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
                                    TMSApplication.db.delete(ClockInPhotoInfo.class); //child_info表中数据将被全部删除
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                // 判断需要更新资料的公司，分别由40、72、xx
                                dialog = new ProgressDialog(MainActivity.this);
                                dialog.setTitle("提示");
                                dialog.setMessage("正在更新資料......");
                                dialog.setCancelable(false);
                                dialog.show();
                                forcedUpdate40Data();
                                updateInovieData();
                                getMaterilList();
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

    private void delieverGoods() {
        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag = false;
        for (UserModel model : userModelList) {
            if (model.getCorp().equals("40") ||model.getCorp().equals("xx")) {
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
            if (model.getCorp().equals("40") || model.getCorp().equals("xx")) {
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
        List<UserModel> userModelList2 = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag2 = false;
        for (UserModel model : userModelList2) {
            if (model.getCorp().equals("40") || model.getCorp().equals("xx")) {
                flag2 = true;
            }
        }

        if (!flag2) {
            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(MainActivity.this, CloseAccountActivity.class));
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
            if (model.getCorp().equals("40") || model.getCorp().equals("xx") || model.getCorp().equals("72")) {
                flag3 = true;
            }
        }

        if (!flag3) {
            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司或澳門Circle-K賬戶！", Toast.LENGTH_SHORT).show();
        } else {
            if (TMSCommonUtils.getUserForXX(MainActivity.this) != null) {
                // 判断需要更新资料的公司，分别由40、72、xx
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("正在更新資料......");
                dialog.setCancelable(false);
                dialog.show();
                forcedUpdate40Data();
                updateInovieData();
                getMaterilList();
                forcedUpdate72Data();
                update72CustomerData();
            } else if (TMSCommonUtils.getUserFor72(MainActivity.this) != null){
                // 判断需要更新资料的公司，分别由40、72、xx
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("正在更新資料......");
                dialog.setCancelable(false);
                dialog.show();
                forcedUpdate72Data();
                update72CustomerData();
            } else {
                // 判断需要更新资料的公司，分别由40、72、xx
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("正在更新資料......");
                dialog.setCancelable(false);
                dialog.show();
                forcedUpdate40Data();
                updateInovieData();
                getMaterilList();
            }
        }
    }

    private void circleKClockIn() {
        List<UserModel> userList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        boolean flag4 = false;
        for (UserModel model : userList) {
            if (model.getCorp().equals("72") ||model.getCorp().equals("xx")) {
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
        } catch (DbException e) {
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
            }
        } catch (DbException e) {
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
                        try {
                            TMSApplication.db.delete(TrainsInfo.class);
                        } catch (DbException e) {

                        }
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
     * 登出
     */
    private void callNetLoginOut() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在退出系統......");
        dialog.setCancelable(false);
        dialog.show();
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
                            dialog.dismiss();
                            System.exit(0);
                        }
                    }, 3000);
                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "登出失敗，請重試", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dialog.dismiss();
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
}
