package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.MainAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.TestMaterialModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hardware.print.BarcodeUtil;
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
        TMSShareInfo.IMEI = "863907040024533";

        String loginMsg = String.valueOf(SpuUtils.get(this, "loginMsg", ""));
        if (!loginMsg.equals("") && !loginMsg.equals("null")) {    // 判斷是否有登錄記錄
            mUserModelList = new Gson().fromJson(loginMsg, new TypeToken<List<UserModel>>() {}.getType());
            if (mUserModelList.get(0).getLoginTime().equals(TMSCommonUtils.getTimeToday())) { // 當有登錄記錄的情況下，判斷是否是今天登錄的
                TMSShareInfo.mUserModelList.add(mUserModelList.get(0));
                if (mUserModelList.get(0).isCustomerTbStatus() && mUserModelList.get(0).isInvoiceTbStatus()) {    // 當是今天時判斷上次是否更新完全 && mUserModelList.get(0).isMaterialTbStatus()
                    initView();
                    setListener();
                    initData();
                } else {
                    // 當是今天，但上次更新有失敗的情況則再次更新
                    forcedUpdateData();
                }
            } else {
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
            }
        } else {
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
                        startActivity(new Intent(MainActivity.this, DeliverGoodsActivity.class));
                        /*try {
                            int reslut = mPrinter.Open();
                            if (reslut == 0) {
                                mPrinter.PrintLineInit(25);
                                mPrinter.PrintLineString("物料單\n", 35, 0, false);
                                mPrinter.PrintLineInit(25);
                                mPrinter.PrintLineString("no.12634552", 25, 0, false);
                                mPrinter.PrintLineInit(25);
                                mPrinter.PrintLineString("REFERENCENO：12345678", 25, 0, false);
                                Bitmap bm = TMSCommonUtils.creatBarcode(MainActivity.this, "12634552",220,75, false);
                                if (bm != null) {
                                    mPrinter.PrintBitmap(bm);
                                }
                                mPrinter.PrintStringEx("澳門可口可樂飲料有限公司", 25, false, false, printer.PrintType.Centering);
                                mPrinter.PrintStringEx("客戶名稱：萬通出入口貿易行(來來21店)\n客戶編號：006804021\n司\t\t\t\t\t機：陳大文\n經\t\t手\t\t人：李小英\n日\t\t\t\t\t期：2018-05-21", 25, false, false, printer.PrintType.Left);

                                // 打印表体
                                List<TestMaterialModel> list = new ArrayList<TestMaterialModel>();
                                list.add(new TestMaterialModel("物料", 999));
                                list.add(new TestMaterialModel("5L礦泉水", 10));
                                list.add(new TestMaterialModel("5L礦泉水", 10));
                                list.add(new TestMaterialModel("卡板", 5));
                                list.add(new TestMaterialModel("卡板", 5));
                                list.add(new TestMaterialModel("卡板", 5));
                                for (TestMaterialModel model : list) {
                                    mPrinter.PrintLineInit(25);
                                    mPrinter.PrintLineStringByType(model.getMaterialName(), 25, printer.PrintType.Left, false);
                                    if (model.getMaterialNum() == 999) {
                                        mPrinter.PrintLineStringByType("数量", 25, printer.PrintType.Right, false);
                                    } else {
                                        mPrinter.PrintLineStringByType(String.valueOf(model.getMaterialNum()) + "\n", 25, printer.PrintType.Right, false);
                                    }
                                }

                                mPrinter.PrintLineEnd();
                                mPrinter.Close();
                            } else {
                                Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                            }

                            // 打印表体
                            *//*List<TestMaterialModel> list = new ArrayList<TestMaterialModel>();
                            list.add(new TestMaterialModel("物料", 999));
                            list.add(new TestMaterialModel("5L礦泉水", 10));
                            list.add(new TestMaterialModel("5L礦泉水", 10));
                            list.add(new TestMaterialModel("卡板", 5));
                            list.add(new TestMaterialModel("卡板", 5));
                            list.add(new TestMaterialModel("卡板", 5));
                            for (TestMaterialModel model : list) {
                                int reslut1 = mPrinter.Open();
                                if (reslut1 == 0) {
                                    mPrinter.PrintLineInit(25);
                                    mPrinter.PrintLineStringByType(model.getMaterialName(), 25, printer.PrintType.Left, false);
                                    if (model.getMaterialNum() == 999) {
                                        mPrinter.PrintLineStringByType("数量", 25, printer.PrintType.Right, false);
                                    } else {
                                        mPrinter.PrintLineStringByType(String.valueOf(model.getMaterialNum()), 25, printer.PrintType.Right, false);
                                    }
                                    mPrinter.PrintLineEnd();
                                    mPrinter.Close();
                                } else {
                                    Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                                }
                                if (model.getMaterialNum() == 999) {
                                    mPrinter.PrintLineStringByType("数量", 25, printer.PrintType.Right, false);
                                } else {
                                    mPrinter.PrintLineStringByType(String.valueOf(model.getMaterialNum()) + "\n", 25, printer.PrintType.Right, false);
                                }
                            }*//*

                            int reslut2 = mPrinter.Open();
                            if (reslut2 == 0) {
                                mPrinter.PrintLineInit(25);
                                mPrinter.PrintStringEx("\n\n客戶簽收及蓋章\t\t\t\t\t\t\t\t\t\t\n\n\n\n\n____________________________\n\n\n\n\n", 25, false, false, printer.PrintType.Right);
                                mPrinter.PrintLineEnd();
                                mPrinter.Close();
                            } else {
                                Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "打印机打开错误", Toast.LENGTH_LONG).show();
                        }*/
                        break;
                    case 1:
                        /*try {
                            int reslut = mPrinter.Open();
                            if (reslut == 0) {
                                mPrinter.PrintLineInit(12);
                                Bitmap bm = TMSCommonUtils.encode("12634552",380,75, MainActivity.this);
                                if (bm != null) {
                                    mPrinter.PrintBitmap(bm);
                                }
                                mPrinter.PrintStringEx("訂單號：12634552\n", 25, false, false, printer.PrintType.Centering);
                                mPrinter.PrintStringEx("澳門工藝可口可樂飲料有限公司\n物料單", 25, false, false, printer.PrintType.Centering);
                                mPrinter.PrintStringEx("REFERENCENO：12345678", 25, false, false, printer.PrintType.Right);
                                mPrinter.PrintStringEx("司機：陳大文\n經手人：李小英\n客戶名稱：萬通出入口貿易行(來來21店)\n客戶編號：006804021\n回收日期：2018-05-21\n物料\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t數量 " +
                                        "\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t10\n卡板\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t5", 25, false, false, printer.PrintType.Left);
                                mPrinter.PrintStringEx("\n\n客戶簽收及蓋章\t\t\t\t\t\t\t\t\t\t\n\n\n\n\n____________________________\n\n\n\n\n", 25, false, false, printer.PrintType.Right);
                                mPrinter.PrintLineEnd();
                                mPrinter.Close();
                            } else {
                                Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "打印机打开错误", Toast.LENGTH_LONG).show();
                        }*/
                        startActivity(new Intent(MainActivity.this, TodayInvoiceListActivity.class));
                        break;
                    case 2:
                        /*try {
                            int reslut = mPrinter.Open();
                            if (reslut == 0) {
                                mPrinter.PrintLineInit(25);
                                Bitmap bm = TMSCommonUtils.encode("12634552",370,75, MainActivity.this);
                                if (bm != null) {
                                    mPrinter.PrintBitmap(bm);
                                }
                                mPrinter.PrintStringEx("訂單號：12634552\n", 25, false, false, printer.PrintType.Centering);
                                mPrinter.PrintStringEx("澳門工藝可口可樂飲料有限公司\n物料單", 25, false, false, printer.PrintType.Centering);
                                mPrinter.PrintStringEx("REFERENCENO：12345678", 25, false, false, printer.PrintType.Right);
                                mPrinter.PrintStringEx("司機：陳大文\n經手人：李小英\n客戶名稱：萬通出入口貿易行(來來21店)\n客戶編號：006804021\n回收日期：2018-05-21", 25, false, false, printer.PrintType.Left);
                                mPrinter.PrintLineEnd();
                                mPrinter.Close();
                            } else {
                                Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                            }

                            List<TestMaterialModel> list = new ArrayList<TestMaterialModel>();
                            list.add(new TestMaterialModel("物料", 999));
                            list.add(new TestMaterialModel("5L礦泉水", 10));
                            list.add(new TestMaterialModel("5L礦泉水", 10));
                            list.add(new TestMaterialModel("卡板", 5));
                            list.add(new TestMaterialModel("卡板", 5));
                            list.add(new TestMaterialModel("卡板", 5));
                            for (TestMaterialModel model : list) {
                                int reslut1 = mPrinter.Open();
                                if (reslut1 == 0) {
                                    mPrinter.PrintLineInit(25);
                                    mPrinter.PrintLineStringByType(model.getMaterialName(), 25, printer.PrintType.Left, false);
                                    if (model.getMaterialNum() == 999) {
                                        mPrinter.PrintLineStringByType("数量", 25, printer.PrintType.Right, false);
                                    } else {
                                        mPrinter.PrintLineStringByType(String.valueOf(model.getMaterialNum()), 25, printer.PrintType.Right, false);
                                    }
                                    mPrinter.PrintLineEnd();
                                    mPrinter.Close();
                                } else {
                                    Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                                }
                            }

                            int reslut2 = mPrinter.Open();
                            if (reslut2 == 0) {
                                mPrinter.PrintLineInit(25);
                                mPrinter.PrintStringEx("\n\n客戶簽收及蓋章\t\t\t\t\t\t\t\t\t\t\n\n\n\n\n____________________________\n\n\n\n\n", 25, false, false, printer.PrintType.Right);
                                mPrinter.PrintLineEnd();
                                mPrinter.Close();
                            } else {
                                Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "打印机打开错误", Toast.LENGTH_LONG).show();
                        }*/
                        startActivity(new Intent(MainActivity.this, CloseAccountActivity.class));
                        break;
                    case 3:
                        forcedUpdateData();
                        //startActivity(new Intent(MainActivity.this, TestPrintWebActivity.class));
                        break;
                    case 4:
                        showLoginOutDialog();
                        break;
                    default:
                        break;
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int reslut = mPrinter.Open();
                    if (reslut == 0) {
                        mPrinter.PrintLineInit(12);
                        Bitmap bm = TMSCommonUtils.encode("12634552",220,80, MainActivity.this);
                        if (bm != null) {
                            mPrinter.PrintBitmap(bm);
                        }
                        mPrinter.PrintStringEx("12634552", 25, false, false, printer.PrintType.Centering);
                        mPrinter.PrintStringEx("澳門工藝可口可樂飲料有限公司\n物料回收收據", 25, false, false, printer.PrintType.Centering);
                        mPrinter.PrintStringEx("編號：12345678\n司機：XXXXXXXXXX\t經手人：XXXXXXXXXX\n客戶名稱：萬通出入口貿易行(來來21店)\n客戶編號：006804021\n回收如期：2018-05-21\n物料\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t數量 " +
                                "\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10" +
                                "\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10\n5L礦泉水\t\t\t\t\t\t\t\t\t\t\t\t10", 25, false, false, printer.PrintType.Left);
                        mPrinter.PrintStringEx("\n\n客戶簽收及蓋章\t\t\t\t\t\t\t\t\t\t\n\n\n\n\n____________________________\n\n\n\n\n", 25, false, false, printer.PrintType.Right);
                        mPrinter.PrintLineEnd();
                        mPrinter.Close();
                    } else {
                        Toast.makeText(MainActivity.this, "打印机打开失败", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "打印机打开错误", Toast.LENGTH_LONG).show();
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
        menuList.add("送貨");
        menuList.add("今日訂單");
        menuList.add("結算");
        menuList.add("數據更新");
        menuList.add("登出");
    }

    /**
     * 上次有更新失敗情況，強制要求更新
     */
    private void forcedUpdateData() {
        /*new AlertDialog.Builder(MainActivity.this)
                .setTitle("系統提示")
                .setMessage("您有數據未更新成功，請點擊主界面數據更新按鈕更新!")
                .setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 啟動更新界面

                            }
                        }).create()
                .show();*/
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("正在更新資料......");
        dialog.setCancelable(false);
        dialog.show();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        paramsMap.put("salesmanid", TMSShareInfo.mUserModelList.get(0).getSalesmanID());
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
                        mUserModelList.get(0).setCustomerTbStatus(true);
                        TMSShareInfo.mUserModelList.get(0).setCustomerTbStatus(true);
                        SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
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
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        paramsMap.put("salesmanid", TMSShareInfo.mUserModelList.get(0).getSalesmanID());
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
                        mUserModelList.get(0).setInvoiceTbStatus(true);
                        TMSShareInfo.mUserModelList.get(0).setInvoiceTbStatus(true);
                        SpuUtils.put(MainActivity.this, "loginMsg", new Gson().toJson(mUserModelList));
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
                initView();
                setListener();
                initData();
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
                    dialog.dismiss();
                    System.exit(0);
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
