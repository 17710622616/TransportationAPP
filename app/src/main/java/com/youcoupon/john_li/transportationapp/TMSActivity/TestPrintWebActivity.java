package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.OrderDetialPageAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.ToHtml;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.ex.DbException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hardware.print.printer;

/**
 * Created by John_Li on 27/7/2018.
 */

public class TestPrintWebActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private OrderDetialPageAdapter mAdapter;
    printer mPrinter = new printer();
    private SubmitInvoiceInfo mSubmitInvoiceInfo;
    private ArrayList<WebView> mViewList;
    private ViewPager mVp;
    private ImageView nextPageIv;
    private ImageView previousPageIv;
    private String sunUrl;
    private WebView sunWebview;
    private TextView textView;
    String url;
    private WebView webview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_print_web);
        initView();
        setListener();
        initData();
        TMSCommonUtils.checkTimeByUrl(this);
    }

    @Override
    public void initView() {
        // 2.2版本以上服务器取数据冲突解决办法========start=========
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                .detectNetwork() // or
                // .detectAll()
                // for
                // all
                // detectable
                // problems
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        textView = findViewById(R.id.test_print_tv);
        headView = findViewById(R.id.test_print_headview);
        previousPageIv = (ImageView) findViewById(R.id.test_previous_page);
        nextPageIv = (ImageView) findViewById(R.id.test_next_page);
        textView = (TextView) findViewById(R.id.test_print_tv);
        headView = (TMSHeadView) findViewById(R.id.test_print_headview);
        mVp = (ViewPager) findViewById(R.id.test_print_vp);
    }

    @Override
    public void setListener() {
        previousPageIv.setOnClickListener(this);
        nextPageIv.setOnClickListener(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
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
                            mPrinter.printBlankLine(40);
                            if (sunUrl != null && !sunUrl.equals("")) {
                                Picture sunpic = sunWebview.capturePicture();
                                int sunnw = sunpic.getWidth();
                                int sunnh = sunpic.getHeight();
                                Bitmap sunbitmap = Bitmap.createBitmap(sunnw, sunnh, Bitmap.Config.ARGB_4444);
                                Canvas canvas = new Canvas(sunbitmap);
                                sunpic.draw(canvas);
                                stroageBitmap(sunbitmap);
                                int sunnewWidth = sunnw;
                                int sunnewHeight = sunnh;
                                if (sunnw > 400) {
                                    sunnewWidth = 400;
                                    sunnewHeight = (int) (((float) sunnh) * (400.0f / ((float) sunnw)) * 1.0f);
                                }
                                Bitmap sunnewBitmap = Bitmap.createBitmap(Bitmap.createScaledBitmap(sunbitmap, sunnewWidth, sunnewHeight, true), 0, 0, 399, sunnewHeight);
                                stroageBitmap(sunnewBitmap);
                                mPrinter.PrintBitmap(sunnewBitmap);
                                mPrinter.printBlankLine(40);
                            }

                            headView.leftTv.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Error error) {
                            error.printStackTrace();
                        }
                    }

                });
            }
        });
    }

    @Override
    public void initData() {
        //headView.setLeft(this);
        headView.setTitle("訂單審核");
        headView.setRightText("結束", TestPrintWebActivity.this);
        mViewList = new ArrayList<>();
        LayoutInflater layoutInflater = getLayoutInflater();
        try {
            printer printer = mPrinter;
            printer.Open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<SubmitInvoiceInfo> all = null;
        try {
            all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            for (SubmitInvoiceInfo info : all) {
                if (info.getRefrence().equals(getIntent().getStringExtra("ReferenceNo")))
                mSubmitInvoiceInfo = info;
            }

            webview = new WebView(this);
            WebSettings settings = webview.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setPluginState(WebSettings.PluginState.ON);
            webview.setWebViewClient(new MvtFlashWebViewClient());
            webview.setDrawingCacheEnabled(true);
            settings.setLoadWithOverviewMode(true);
            if (mSubmitInvoiceInfo.getInvoiceNo() != null) {
                url = "file:///" + testCreateHTML(TMSCommonUtils.saveBitmap(TMSCommonUtils.creatBarcode(this, TMSCommonUtils.ean8(mSubmitInvoiceInfo.getInvoiceNo()), 160, 60, false)), true);
            } else {
                url = "file:///" + testCreateHTML("", true);
            }
            webview.loadUrl(url);
            mViewList.add(webview);
            if (mSubmitInvoiceInfo.getSunRefrence() != null) {
                nextPageIv.setVisibility(View.VISIBLE);
                sunWebview = new WebView(this);
                WebSettings sunSettings = sunWebview.getSettings();
                sunSettings.setJavaScriptEnabled(true);
                sunSettings.setPluginState(WebSettings.PluginState.ON);
                sunWebview.setWebViewClient(new MvtFlashWebViewClient());
                sunWebview.setDrawingCacheEnabled(true);
                sunSettings.setLoadWithOverviewMode(true);
                try {
                    printer printer2 = mPrinter;
                    printer.Open();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (mSubmitInvoiceInfo.getSunInvoiceNo() != null) {
                    sunUrl = "file:///" + testCreateHTML(TMSCommonUtils.saveBitmap(TMSCommonUtils.creatBarcode(this, TMSCommonUtils.ean8(mSubmitInvoiceInfo.getSunInvoiceNo()), 160, 60, false)), false);
                } else {
                    sunUrl = "file:///" + testCreateHTML("", false);
                }
                sunWebview.loadUrl(sunUrl);
                mViewList.add(sunWebview);
            }
            mAdapter = new OrderDetialPageAdapter(mViewList);
            mVp.setAdapter(mAdapter);
            mVp.setCurrentItem(0);

            //mSubmitInvoiceInfo = new Gson().fromJson(getIntent().getStringExtra("SubmitInvoiceInfo"), SubmitInvoiceInfo.class);
            // 设置WebView属性，能够执行Javascript脚本
            /*WebSettings settings = refundWebview.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setPluginState(WebSettings.PluginState.ON);
            //settings.setPluginsEnabled(true);
            refundWebview.setWebViewClient(new MvtFlashWebViewClient());
            // 截图用
            refundWebview.setDrawingCacheEnabled(true);
            // 自适应屏幕大小
            settings.setLoadWithOverviewMode(true);

            try {
                mPrinter.Open();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mSubmitInvoiceInfo.getInvoiceNo() != null) {
                Bitmap bm = TMSCommonUtils.creatBarcode(TestPrintWebActivity.this, TMSCommonUtils.ean8(mSubmitInvoiceInfo.getInvoiceNo()),160,60, false);
                //Bitmap bm = TMSCommonUtils.creatBarcode(TestPrintWebActivity.this, TMSCommonUtils.ean8("1803297"),160,60, false);
                url = "file:///" + testCreateHTML(TMSCommonUtils.saveBitmap(bm));// 载入本地生成的页面
            } else {
                url = "file:///" + testCreateHTML("");// 载入本地生成的页面
            }*/
        } catch (Exception e) {
            TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：TestPrintWebActivity.initData():" + e.getStackTrace().toString() + "\n" + e.getMessage().toString(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
            Log.d("訂單查詢失敗", "訂單查詢失敗" + String.valueOf(e.getStackTrace()));
            Toast.makeText(this, "訂單查詢失敗！" + String.valueOf(e.getStackTrace()), Toast.LENGTH_SHORT).show();
        }
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
                // refundWebview.loadUrl("javascript:canvasMouseDown("+event.getX()+","+event.getY()+")");
                return true;
            }
            return false;
        }
    }

    public String testCreateHTML(String barCodeImagePath, boolean isMother) {
        List<DeliverInvoiceModel> mDeliverInvoiceModelList = new Gson().fromJson(mSubmitInvoiceInfo.getOrderBody(), new TypeToken<List<DeliverInvoiceModel>>() {}.getType());
        List<CustomerInfo> all = null;
        String customerName = "";
        try {
            all = TMSApplication.db.selector(CustomerInfo.class).where("customerID","=",mSubmitInvoiceInfo.getCustomerID()).findAll();
            for(CustomerInfo customerInfo : all)      {
                customerName = customerInfo.getCustomerName();
            }
        } catch (Exception e) {
            TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：TestPrintWebActivity.testCreateHTML():" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
            e.printStackTrace();
        }
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".html").getPath();
        if (isMother) {
            ToHtml.convert(mSubmitInvoiceInfo.getInvoiceNo(), mSubmitInvoiceInfo.getRefrence(), mSubmitInvoiceInfo.getCustomerID(), mSubmitInvoiceInfo.getCustomerName(), path, mDeliverInvoiceModelList, barCodeImagePath, this, isMother);
        } else {
            ToHtml.convert(mSubmitInvoiceInfo.getSunInvoiceNo(), mSubmitInvoiceInfo.getSunRefrence(), mSubmitInvoiceInfo.getCustomerID(), mSubmitInvoiceInfo.getCustomerName(), path, mDeliverInvoiceModelList, barCodeImagePath, this, isMother);
        }
        //ToHtml.convert(mSubmitInvoiceInfo.getInvoiceNo(),mSubmitInvoiceInfo.getRefrence(), mSubmitInvoiceInfo.getCustomerID(), mSubmitInvoiceInfo.getCustomerName(), path, mDeliverInvoiceModelList, barCodeImagePath, this);
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(v.getId()==R.id.head_right_tv){
            setResult(RESULT_OK);
            finish();
        } else if (v.getId() == R.id.head_left){
            finish();
        } else if (v.getId() == R.id.test_previous_page) {
            this.previousPageIv.setVisibility(View.GONE);
            this.nextPageIv.setVisibility(View.VISIBLE);
            this.mVp.setCurrentItem(0);
        } else if (v.getId() == R.id.test_next_page) {
            this.previousPageIv.setVisibility(View.VISIBLE);
            this.nextPageIv.setVisibility(View.GONE);
            this.mVp.setCurrentItem(1);
        }
    }

    public static String stroageBitmap(Bitmap bitmap){
        String path = "";
        if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)){
            String  sdCardDir = Environment.getExternalStorageDirectory()+ "/AAPrintImage/";
            File dirFile  = new File(sdCardDir);
            if (!dirFile .exists()) {
                dirFile .mkdirs();
            }
            File file = new File(sdCardDir, System.currentTimeMillis()+".jpg");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                path = file.getPath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                if (out != null){
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
