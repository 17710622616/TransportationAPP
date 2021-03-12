package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialCorrespondenceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialNumberInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.CurrentTrunkNoViewModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.MaterialCorrespondenceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostStockMovementModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSBussinessUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSUtils.ToHtml;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hardware.print.printer;

/**
 * Created by John_Li on 27/11/2018.
 */

public class CloseAccountActivity extends BaseActivity implements View.OnClickListener {
    private TMSHeadView headView;
    private WebView webview;
    private TextView textView;
    printer mPrinter = new printer();
    String url;
    private boolean hasPrint = false;

    private List<DeliverInvoiceModel> mDeliverInvoiceModelList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_account);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.close_account_head);
        // 2.2版本以上服务器取数据冲突解决办法========start=========
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                .detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        textView = findViewById(R.id.close_account_print);
        webview = (WebView) findViewById(R.id.close_account_print_wv);
    }

    @Override
    public void setListener() {
        textView.setOnClickListener(this);
    }

    @Override
    public void initData() {
        headView.setTitle("結算");
        headView.setLeft(this);
        headView.setRightText("历史", this);

        mDeliverInvoiceModelList = new ArrayList<>();
        getData();
        // 设置WebView属性，能够执行Javascript脚本
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        //settings.setPluginsEnabled(true);
        webview.setWebViewClient(new CloseAccountActivity.MvtFlashWebViewClient());
        // 截图用
        webview.setDrawingCacheEnabled(true);
        // 自适应屏幕大小
        settings.setLoadWithOverviewMode(true);

        try {
            mPrinter.Open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        url = "file:///" + testCreateHTML();// 载入本地生成的页面
        webview.loadUrl(url);
    }

    private void getData() {
        try {
            List<MaterialNumberInfo> all = TMSApplication.db.selector(MaterialNumberInfo.class).findAll();
            for (MaterialNumberInfo model : all) {
                DeliverInvoiceModel deliverInvoiceModel = new DeliverInvoiceModel();
                deliverInvoiceModel.setMaterialId(model.getMaterialID());
                deliverInvoiceModel.setMaterialName(model.getNameChinese());
                deliverInvoiceModel.setSendOutNum(model.getMaterialDepositeNum());
                deliverInvoiceModel.setRecycleNum(model.getMaterialRefundNum());
                mDeliverInvoiceModelList.add(deliverInvoiceModel);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.close_account_print:
                if (!hasPrint) {
                    hasPrint = true;
                    // 拉取最新车次
                    long maxTimes = TMSCommonUtils.searchTrainsInfoMaxTimes();
                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
                    paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
                    paramsMap.put("driverID", TMSCommonUtils.getUserFor40(this).getDriverID());
                    RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_TRUNKNO_NOW + TMSCommonUtils.createLinkStringByGet(paramsMap));
                    params.setAsJsonContent(true);
                    params.setConnectTimeout(30 * 1000);
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                            if (commonModel.getCode() == 0) {
                                String resultStr = TMSCommonUtils.decode(commonModel.getData().toString());
                                CurrentTrunkNoViewModel model = new Gson().fromJson(resultStr, CurrentTrunkNoViewModel.class);
                                if (model.getTruckNo() != maxTimes) {
                                    // 当最大车次不等于服务器的车次时直接新增一条
                                    try {
                                        TrainsInfo trainsInfo = new TrainsInfo();
                                        trainsInfo.setTrainsTimes(model.getTruckNo());
                                        //trainsInfo.setTodayDepositBody(new Gson().toJson(movementDepositModel));
                                        //trainsInfo.setTodayRefundBody(new Gson().toJson(movementRefundModel));
                                        trainsInfo.setTodayDate(TMSCommonUtils.getTimeNow());
                                        trainsInfo.setTodayDepositStatus(0);
                                        trainsInfo.setTodayRefundStatus(0);

                                        TMSApplication.db.saveOrUpdate(trainsInfo);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                //TrainsInfo depositfirst = TMSApplication.db.findFirst(TrainsInfo.class);
                            } else {
                                TMSBussinessUtils.getTrunkNoErrorUpdateTrunkNo(maxTimes);
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            TMSBussinessUtils.getTrunkNoErrorUpdateTrunkNo(maxTimes);
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {
                            // 拉取最新车次结束，开始生成结算单
                            try {
                                startCloseAccount();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    // 已结算过了，打印結算單
                    CloseAccountActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Picture pic = webview.capturePicture();
                                int nw = pic.getWidth();
                                int nh = pic.getHeight();
                                Bitmap bitmap = Bitmap.createBitmap(nw, nh, Bitmap.Config.ARGB_4444);
                                Canvas can = new Canvas(bitmap);
                                pic.draw(can);
                                stroageBitmap(bitmap);

                                int newWidth = nw;
                                int newHeight = nh;
                                if (nw > 400) {
                                    float rate = 400 * 1.0f / nw * 1.0f;
                                    newWidth = 400;
                                    newHeight = (int) (nh * rate);
                                }
                                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                                //                        Utils.stroageBitmap(bitmap);
                                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, 399, newHeight);
                                stroageBitmap(newBitmap);
                                mPrinter.PrintBitmap(newBitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;
            case R.id.head_right_tv:
                startActivity(new Intent(this, CloseAccountHistoryActivity.class));
                break;
        }
    }

    /**
     * 开始结算
     */
    private void startCloseAccount() throws DbException {
        try {
            // 查询所有物料
            List<MaterialNumberInfo> materialNumberList = TMSApplication.db.selector(MaterialNumberInfo.class).findAll();
            // 按金
            PostStockMovementModel movementDepositModel = new PostStockMovementModel();
            PostStockMovementModel.Header depositheader = new PostStockMovementModel.Header();
            String deposittime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()).replace("-", "");
            deposittime = deposittime.replace(":", "").replace(" ", "");
            //TrainsInfo depositfirst = TMSApplication.db.findFirst(TrainsInfo.class);
            //if (depositfirst != null) {
            //    depositheader.setTruckNo(TMSCommonUtils.searchTrainsInfoMaxTimes() + 1);
            //} else {
            //    depositheader.setTruckNo(1);
            //}
            // 上面第一行已获得了最新车次不需要再叠加，直接用当前车次
            depositheader.setTruckNo(TMSCommonUtils.searchTrainsInfoMaxTimes());
            depositheader.setReference(TMSShareInfo.IMEI + deposittime + "D");
            depositheader.setSalesmanID(TMSCommonUtils.getUserFor40(this).getSalesmanID());
            movementDepositModel.setHeader(depositheader);

            List<PostStockMovementModel.Line> lineList = new ArrayList<>();
            for (MaterialNumberInfo info : materialNumberList) {
                PostStockMovementModel.Line depositLine = new PostStockMovementModel.Line();
                if (info.getMaterialDepositeNum() != 0) {
                    depositLine.setQuantity(info.getMaterialDepositeNum());
                } else {
                    depositLine.setQuantity(0);
                }
                depositLine.setMerchandiseID(info.getMaterialID());
                lineList.add(depositLine);
            }
            movementDepositModel.setLines(lineList);

            // 回收
            PostStockMovementModel movementRefundModel = new PostStockMovementModel();
            PostStockMovementModel.Header refundheader = new PostStockMovementModel.Header();
            String refundtime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()).replace("-", "");
            refundtime = refundtime.replace(":", "").replace(" ", "");
            refundheader.setReference(TMSShareInfo.IMEI + refundtime + "R");
            refundheader.setSalesmanID(TMSCommonUtils.getUserFor40(this).getSalesmanID());
            //TrainsInfo refundfirst = TMSApplication.db.findFirst(TrainsInfo.class);
            //if (refundfirst != null) {
            //    refundheader.setTruckNo(TMSCommonUtils.searchTrainsInfoMaxTimes() + 1);
            //} else {
            //refundheader.setTruckNo(1);
            //}
            // 上面第一行已获得了最新车次不需要再叠加，直接用当前车次
            refundheader.setTruckNo(TMSCommonUtils.searchTrainsInfoMaxTimes());
            movementRefundModel.setHeader(refundheader);

            List<PostStockMovementModel.Line> refundlineList = new ArrayList<>();
            for (MaterialNumberInfo info : materialNumberList) {
                PostStockMovementModel.Line refundLine = new PostStockMovementModel.Line();
                if (info.getMaterialRefundNum() != 0) {
                    refundLine.setQuantity(info.getMaterialRefundNum());
                } else {
                    refundLine.setQuantity(0);
                }
                refundLine.setMerchandiseID(info.getMaterialID());
                refundlineList.add(refundLine);
            }
            movementRefundModel.setLines(refundlineList);

            long maxTimes = TMSCommonUtils.searchTrainsInfoMaxTimes();
            // 更新最新车次的出入仓
            try {
                TMSApplication.db.update(TrainsInfo.class, WhereBuilder.b().and("trains_times", "=", maxTimes), new KeyValue("today_refund_body", new Gson().toJson(movementRefundModel)));
                TMSApplication.db.update(TrainsInfo.class, WhereBuilder.b().and("trains_times", "=", maxTimes), new KeyValue("today_deposit_body", new Gson().toJson(movementDepositModel)));
            } catch (DbException e) {
                e.printStackTrace();
            }
            // 更新最新车次的物料表状态
            boolean a = true;
            for (PostStockMovementModel.Line line : movementDepositModel.getLines()) {
                if (line.getQuantity() != 0) {
                    a = false;
                }
            }
            if (a) {
                TMSApplication.db.update(TrainsInfo.class, WhereBuilder.b().and("trains_times", "=", maxTimes), new KeyValue("today_deposit_status", 0));
            }

            boolean b = true;
            for (PostStockMovementModel.Line line : movementRefundModel.getLines()) {
                if (line.getQuantity() != 0) {
                    b = false;
                }
            }
            if (b) {
                TMSApplication.db.update(TrainsInfo.class, WhereBuilder.b().and("trains_times", "=", maxTimes), new KeyValue("today_refund_status", 0));
            }

            //新增本車次車次表
                        /*TrainsInfo trainsInfo = new TrainsInfo();
                        TrainsInfo first1 = TMSApplication.db.findFirst(TrainsInfo.class);
                        if (first1 != null) {
                            trainsInfo.setTrainsTimes(TMSCommonUtils.searchTrainsInfoMaxTimes() + 1);
                        } else {
                            trainsInfo.setTrainsTimes(1);
                        }
                        trainsInfo.setTodayDepositBody(new Gson().toJson(movementDepositModel));
                        trainsInfo.setTodayRefundBody(new Gson().toJson(movementRefundModel));
                        trainsInfo.setTodayDate(TMSCommonUtils.getTimeNow());
                        boolean a = true;
                        for (PostStockMovementModel.Line line : movementDepositModel.getLines()) {
                            if (line.getQuantity() != 0) {
                                a = false;
                            }
                        }

                        if (!a) {
                            trainsInfo.setTodayDepositStatus(0);
                        } else {
                            trainsInfo.setTodayDepositStatus(1);
                        }

                        boolean b = true;
                        for (PostStockMovementModel.Line line : movementRefundModel.getLines()) {
                            if (line.getQuantity() != 0) {
                                b = false;
                            }
                        }
                        if (!b) {
                            trainsInfo.setTodayRefundStatus(0);
                        } else {
                            trainsInfo.setTodayRefundStatus(1);
                        }

                        TMSApplication.db.saveOrUpdate(trainsInfo);*/

            // 將結算單提交至服務器
            callNetSubmitThatTrainsData();

            // 打印結算單
            CloseAccountActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Picture pic = webview.capturePicture();
                        int nw = pic.getWidth();
                        int nh = pic.getHeight();
                        Bitmap bitmap = Bitmap.createBitmap(nw, nh, Bitmap.Config.ARGB_4444);
                        Canvas can = new Canvas(bitmap);
                        pic.draw(can);
                        stroageBitmap(bitmap);

                        int newWidth = nw;
                        int newHeight = nh;
                        if (nw > 400) {
                            float rate = 400 * 1.0f / nw * 1.0f;
                            newWidth = 400;
                            newHeight = (int) (nh * rate);
                        }
                        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                        //                        Utils.stroageBitmap(bitmap);
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, 399, newHeight);
                        stroageBitmap(newBitmap);
                        mPrinter.PrintBitmap(newBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (Error error) {
                        error.printStackTrace();
                    }
                }
            });


            // 清空物料回收數量表
            List<MaterialNumberInfo> list = TMSApplication.db.selector(MaterialNumberInfo.class).findAll();
            for (MaterialNumberInfo info : list) {
                info.setMaterialDepositeNum(0);
                info.setMaterialRefundNum(0);
                TMSApplication.db.saveOrUpdate(info);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 重新提交所有未成功結算
     */
    private void callNetSubmitThatTrainsData() {
        try {
            List<TrainsInfo> all = TMSApplication.db.selector(TrainsInfo.class).findAll();
            Log.d("物料結算列表", new Gson().toJson(all));
            for (TrainsInfo trainsInfo : all) {
                if (trainsInfo.getTodayDepositStatus() != 1) {
                    PostStockMovementModel depositModel = new Gson().fromJson(trainsInfo.getTodayDepositBody(), PostStockMovementModel.class);
                    int qty = 0;
                    for (PostStockMovementModel.Line lines : depositModel.getLines()) {
                        qty += lines.getQuantity();
                    }
                    if (qty != 0) {
                        callNetSubmitMaterialsAndSettlement(depositModel, true);
                    }
                }

                if (trainsInfo.getTodayRefundStatus() != 1) {
                    PostStockMovementModel refundModel = new Gson().fromJson(trainsInfo.getTodayRefundBody(), PostStockMovementModel.class);
                    int qty = 0;
                    for (PostStockMovementModel.Line lines : refundModel.getLines()) {
                        qty += lines.getQuantity();
                    }
                    if (qty != 0) {
                        callNetSubmitMaterialsAndSettlement(refundModel, false);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交物料结算状态
     */
    private void callNetSubmitMaterialsAndSettlement(final PostStockMovementModel movementModel, final boolean driverout) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getSalesmanID());
        paramsMap.put("driverout", String.valueOf(driverout));
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.SUBMIT_MATERIALS_SETTLEMENT + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setAsJsonContent(true);
        String json = new Gson().toJson(movementModel);
        params.setBodyContent(json);
        String uri = params.getUri();
        params.setConnectTimeout(30 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        String resultStr = TMSCommonUtils.decode(commonModel.getData().toString());
                        WhereBuilder b = WhereBuilder.b();
                        b.and("trains_times", "=", movementModel.getHeader().getTruckNo()); //构造修改的条件V
                        KeyValue name = null;
                        if (driverout) {
                            name = new KeyValue("today_deposit_status", 1);
                        } else {
                            name = new KeyValue("today_refund_status", 1);
                        }
                        TMSApplication.db.update(TrainsInfo.class, b, name);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("trains_times", "=", movementModel.getHeader().getTruckNo()); //构造修改的条件
                        KeyValue name = null;
                        if (driverout) {
                            name = new KeyValue("today_deposit_status", 2);
                        } else {
                            name = new KeyValue("today_refund_status", 2);
                        }
                        TMSApplication.db.update(TrainsInfo.class, b, name);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                try {
                    WhereBuilder b = WhereBuilder.b();
                    b.and("trains_times", "=", movementModel.getHeader().getTruckNo()); //构造修改的条件
                    KeyValue name = null;
                    if (driverout) {
                        name = new KeyValue("today_deposit_status", 2);
                    } else {
                        name = new KeyValue("today_refund_status", 2);
                    }
                    TMSApplication.db.update(TrainsInfo.class, b, name);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(CloseAccountActivity.this, "提交結算網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CloseAccountActivity.this, "提交結算失敗！", Toast.LENGTH_SHORT).show();
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

    private List<String> getHasResponseMatrialList() {
        List<String> returnMaterialList = new ArrayList<>();
        try {
            //查找所有商品的物料关系列表
            List<MaterialCorrespondenceInfo> materialCorrespondenceList = TMSApplication.db.selector(MaterialCorrespondenceInfo.class).findAll();
            for (MaterialCorrespondenceInfo info : materialCorrespondenceList) {
                List<MaterialCorrespondenceModel.CorrespondingMaterial> materialList = new Gson().fromJson(info.getMaterialListJson(), new TypeToken<List<MaterialCorrespondenceModel.CorrespondingMaterial>>() {
                }.getType());
                for (MaterialCorrespondenceModel.CorrespondingMaterial correspondingMaterial : materialList) {
                    returnMaterialList.add(correspondingMaterial.getMaterialID().trim());
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        // 去重后返回
        return TMSCommonUtils.removeDuplicate(returnMaterialList);
    }

    public class OnTouchListenerHTML5 implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                webview.loadUrl("javascript:canvasMouseDown(" + event.getX() + "," + event.getY() + ")");
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                webview.loadUrl("javascript:canvasMouseMove(" + event.getX() + "," + event.getY() + ")");
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // webview.loadUrl("javascript:canvasMouseDown("+event.getX()+","+event.getY()+")");
                return true;
            }
            return false;
        }

    }

    public String testCreateHTML() {
        /*try {
            MaterialNumberInfo info = TMSApplication.db.findFirst(MaterialNumberInfo.class);
            int deposit = info.getMaterialDepositeNum();
            int refund = info.getMaterialRefundNum();
            mDeliverInvoiceModelList.get(2).setSendOutNum(deposit);
            mDeliverInvoiceModelList.get(2).setRecycleNum(refund);
        } catch (Exception e) {

        }*/
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".html").getPath();
        ToHtml.convertCloseccount(mDeliverInvoiceModelList, path, this);
        return path;
    }

    public void saveWebviewPic() {
        Picture picture = webview.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmp);
        picture.draw(c);
        savePic(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + "html.jpg").getPath(), bmp, 1);
    }

    // 保存文件
    public static boolean savePic(String path, Bitmap bmp, int quality) {
        if (bmp == null || bmp.isRecycled()) {
            return false;
        }
        File myCaptureFile = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            if (quality == 1) {// jpg
                bmp.compress(Bitmap.CompressFormat.JPEG, 85, bos);
            } else if (quality == 2) {// png
                bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } else if (quality == 3) {// 发微薄用
                bmp.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            } else if (quality == 5) {// jpg
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            try {
                bos.flush();
                bos.close();
                // writeEixf(path);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    // Web视图
    private class MvtFlashWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub


            super.onPageFinished(view, url);
        }
    }

    public static String stroageBitmap(Bitmap bitmap) {
        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = Environment.getExternalStorageDirectory() + "/AAPrintImage/";
            File dirFile = new File(sdCardDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(sdCardDir, System.currentTimeMillis() + ".jpg");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                path = file.getPath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
        return path;
    }
}
