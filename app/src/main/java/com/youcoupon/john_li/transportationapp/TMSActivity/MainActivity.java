package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.LoginListAdapter;
import com.youcoupon.john_li.transportationapp.TMSAdapter.MainAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialNumberInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
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
    @RequiresApi(api = 26)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 獲取手機IMEI
        TelephonyManager tm = (TelephonyManager)this.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        //TMSShareInfo.IMEI = Build.getSerial() + "/" + tm.getSimSerialNumber();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // Todo Don't forget to ask the permission
            TMSShareInfo.IMEI = Build.getSerial();
        }
        else
        {
            TMSShareInfo.IMEI = Build.SERIAL;
        }
        //TMSShareInfo.IMEI = "863907040024533";

        try {
            TrainsInfo info = new TrainsInfo();
            info.setId(1);
            info.setTodayDate(TMSCommonUtils.getTimeNow());
            info.setTrainsTimes(1);
            TMSApplication.db.save(info);
        } catch (DbException e) {
            e.printStackTrace();
        }

        String loginMsg = String.valueOf(SpuUtils.get(this, "loginMsg", ""));
        if (!loginMsg.equals("") && !loginMsg.equals("null")) {    // 判斷是否有登錄記錄
            mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
            boolean flag = false;
            UserModel cachModel = null;
            for (UserModel model : mUserModelList) {
                if (model.getLoginTime().equals(TMSCommonUtils.getTimeToday())) { // 當有登錄記錄的情況下，判斷是否是今天登錄的
                    TMSShareInfo.mUserModelList.add(model);
                    flag = true;
                    if (model.getCorp().equals("40")) {
                        cachModel = model;
                    }
                }
            }

            if (flag) {
                if (cachModel != null) {
                    if (cachModel.isCustomerTbStatus() && cachModel.isInvoiceTbStatus()) {    // 當是今天時判斷上次是否更新完全 && cachModel.isMaterialTbStatus()
                        initView();
                        setListener();
                        initData();
                    } else {
                        // 當是今天，但上次更新有失敗的情況則再次更新
                        try {
                            TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
                            TMSApplication.db.delete(MaterialNumberInfo.class); //child_info表中数据将被全部删除
                            TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        forcedUpdateData();
                    }
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
                        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
                        boolean flag = false;
                        for (UserModel model : userModelList) {
                            if (model.getCorp().equals("40")) {
                                flag = true;
                            }
                        }

                        if (!flag) {
                            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(MainActivity.this, DeliverGoodsActivity.class));
                        }
                        break;
                    case 1:
                        List<UserModel> userModelList1 = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
                        boolean flag1 = false;
                        for (UserModel model : userModelList1) {
                            if (model.getCorp().equals("40")) {
                                flag1 = true;
                            }
                        }

                        if (!flag1) {
                            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(MainActivity.this, TodayInvoiceListActivity.class));
                        }
                        break;
                    case 2:
                        List<UserModel> userModelList2 = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
                        boolean flag2 = false;
                        for (UserModel model : userModelList2) {
                            if (model.getCorp().equals("40")) {
                                flag2 = true;
                            }
                        }

                        if (!flag2) {
                            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(MainActivity.this, CloseAccountActivity.class));
                        }
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, InvoiceStateActivity.class));
                        break;
                    case 4:
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
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                loginDialog.dismiss();
                            }
                        });
                        loginDialog.show();
                        break;
                    case 5:
                        List<UserModel> modelList = new Gson().fromJson(String.valueOf(SpuUtils.get(MainActivity.this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
                        boolean flag3 = false;
                        for (UserModel model : modelList) {
                            if (model.getCorp().equals("40")) {
                                flag3 = true;
                            }
                        }

                        if (!flag3) {
                            Toast.makeText(MainActivity.this, "請先登錄澳門可口可樂飲料有限公司賬戶！", Toast.LENGTH_SHORT).show();
                        } else {
                            forcedUpdateData();
                        }
                        break;
                    case 6:
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
        menuList.add("登出");
    }

    /**
     * 上次有更新失敗情況，強制要求更新
     */
    private void forcedUpdateData() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("正在更新資料......");
        dialog.setCancelable(false);
        dialog.show();
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
                            if (mUserModelList.get(i).getCorp().equals("40")) {
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
                updateInovieData();
            }
        });
    }

    private void updateInovieData() {
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
                            if (mUserModelList.get(i).getCorp().equals("40")) {
                                mUserModelList.get(i).setInvoiceTbStatus(true);
                                TMSShareInfo.mUserModelList.get(i).setInvoiceTbStatus(true);
                                SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
                            }
                        }
                        Toast.makeText(MainActivity.this, "獲取發票資料成功！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } catch (DbException e) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "獲取發票資料失敗！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "獲取發票資料失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dialog.dismiss();
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
                initView();
                setListener();
                initData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                mUserModelList = TMSShareInfo.mUserModelList;

                for (UserModel model : mUserModelList) {
                    if (model.getLoginTime().equals(TMSCommonUtils.getTimeToday())) { // 當有登錄記錄的情況下，判斷是否是今天登錄的
                        TMSShareInfo.mUserModelList.add(model);
                        if (model.getCorp().equals("40")) {
                            if (model.isCustomerTbStatus() && model.isInvoiceTbStatus()) {    // 當是今天時判斷上次是否更新完全 && cachModel.isMaterialTbStatus()
                                initView();
                                setListener();
                                initData();
                            } else {
                                // 當是今天，但上次更新有失敗的情況則再次更新
                                try {
                                    TMSApplication.db.delete(SubmitInvoiceInfo.class); //child_info表中数据将被全部删除
                                    TMSApplication.db.delete(MaterialNumberInfo.class); //child_info表中数据将被全部删除
                                    TMSApplication.db.delete(InvoiceStateInfo.class); //child_info表中数据将被全部删除
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                forcedUpdateData();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 顯示登錄dialog
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
                            TrainsInfo first1 = TMSApplication.db.findFirst(TrainsInfo.class);
                            first1.setTrainsTimes(0);
                            TMSApplication.db.saveOrUpdate(first1);
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
