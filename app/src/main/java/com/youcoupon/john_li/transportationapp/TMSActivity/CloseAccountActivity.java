package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
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
 * Created by John_Li on 27/11/2018.
 */

public class CloseAccountActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private WebView webview;
    private TextView textView;
    printer mPrinter = new printer();
    String url;

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
                .detectNetwork() // or
                // .detectAll()
                // for
                // all
                // detectable
                // problems
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        webview = (WebView) findViewById(R.id.close_account_print_wv);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        headView.setTitle("結算");
        headView.setLeft(this);
        headView.setRightText("打印", this);

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

        Bitmap bm = TMSCommonUtils.creatBarcode(CloseAccountActivity.this, "12634552",160,60, false);
        url = "file:///" + testCreateHTML(TMSCommonUtils.saveBitmap(bm));// 载入本地生成的页面
        webview.loadUrl(url);
    }

    private void getData() {
        DeliverInvoiceModel model = new DeliverInvoiceModel();
        model.setMaterialId(1);
        model.setMaterialName("木卡板");
        model.setMaterialId(0);
        model.setMaterialId(0);
        mDeliverInvoiceModelList.add(model);
        DeliverInvoiceModel model1 = new DeliverInvoiceModel();
        model1.setMaterialId(1);
        model1.setMaterialName("膠卡板(大)");
        model1.setMaterialId(0);
        model1.setMaterialId(0);
        mDeliverInvoiceModelList.add(model1);
        DeliverInvoiceModel model2 = new DeliverInvoiceModel();
        model2.setMaterialId(1);
        model2.setMaterialName("膠卡板(小)");
        model2.setMaterialId(0);
        model2.setMaterialId(0);
        mDeliverInvoiceModelList.add(model2);
        DeliverInvoiceModel model3 = new DeliverInvoiceModel();
        model3.setMaterialId(1);
        model3.setMaterialName("膠片(5加侖)");
        model3.setMaterialId(0);
        model3.setMaterialId(0);
        mDeliverInvoiceModelList.add(model3);
        DeliverInvoiceModel model4 = new DeliverInvoiceModel();
        model4.setMaterialId(1);
        model4.setMaterialName("5加侖吉膠桶");
        model4.setMaterialId(0);
        model4.setMaterialId(0);
        mDeliverInvoiceModelList.add(model4);
        DeliverInvoiceModel model5 = new DeliverInvoiceModel();
        model5.setMaterialId(1);
        model5.setMaterialName("5加侖吉膠箱");
        model5.setMaterialId(0);
        model5.setMaterialId(0);
        mDeliverInvoiceModelList.add(model5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.head_right_tv:
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
                break;
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
                // webview.loadUrl("javascript:canvasMouseDown("+event.getX()+","+event.getY()+")");
                return true;
            }
            return false;
        }

    }

    public String testCreateHTML(String barCodeImagePath) {
        try {
            List<SubmitInvoiceInfo> all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            for (SubmitInvoiceInfo info : all) {
                List<DeliverInvoiceModel> cachedeliverInvoiceModelList = new Gson().fromJson(info.getOrderBody(), new TypeToken<List<DeliverInvoiceModel>>() {}.getType());
                mDeliverInvoiceModelList.get(2).setSendOutNum(mDeliverInvoiceModelList.get(2).getSendOutNum() + cachedeliverInvoiceModelList.get(2).getSendOutNum());
                mDeliverInvoiceModelList.get(2).setRecycleNum(mDeliverInvoiceModelList.get(2).getRecycleNum() + cachedeliverInvoiceModelList.get(2).getRecycleNum());
            }
        } catch (Exception e) {

        }
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".html").getPath();
        ToHtml.convertCloseccount(mDeliverInvoiceModelList, path);
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
}
