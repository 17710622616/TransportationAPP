package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.util.List;

/**
 * Created by John_Li on 27/11/2018.
 */

public class DeleverInvoiceDetialActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private WebView webview;
    String url;
    private SubmitInvoiceInfo mSubmitInvoiceInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_invoice_detial);
        initView();
        setListener();
        initData();
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

        webview = (WebView) findViewById(R.id.invoice_detial_wv);
        headView = findViewById(R.id.invoice_detial_head);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        headView.setLeft(this);
        headView.setTitle("訂單詳情");
        List<SubmitInvoiceInfo> all = null;
        try {
            all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            for (SubmitInvoiceInfo info : all) {
                if (info.getRefrence().equals(getIntent().getStringExtra("ReferenceNo")))
                    mSubmitInvoiceInfo = info;
            }

            // 设置WebView属性，能够执行Javascript脚本
            WebSettings settings = webview.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setPluginState(WebSettings.PluginState.ON);
            //settings.setPluginsEnabled(true);
            webview.setWebViewClient(new DeleverInvoiceDetialActivity.MvtFlashWebViewClient());
            // 截图用
            webview.setDrawingCacheEnabled(true);
            // 自适应屏幕大小
            settings.setLoadWithOverviewMode(true);

            if (mSubmitInvoiceInfo.getInvoiceNo() != null) {
                Bitmap bm = TMSCommonUtils.creatBarcode(DeleverInvoiceDetialActivity.this, mSubmitInvoiceInfo.getInvoiceNo(),160,60, false);
                url = "file:///" + testCreateHTML(TMSCommonUtils.saveBitmap(bm));// 载入本地生成的页面
            } else {
                url = "file:///" + testCreateHTML("");// 载入本地生成的页面
            }
            webview.loadUrl(url);
        } catch (Exception e) {
            Toast.makeText(this, "訂單查詢失敗！", Toast.LENGTH_SHORT).show();
        }
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
            finish();
        } else if (v.getId() == R.id.head_left){
            finish();
        }
    }


    public String testCreateHTML(String barCodeImagePath) {
        List<DeliverInvoiceModel> mDeliverInvoiceModelList = new Gson().fromJson(mSubmitInvoiceInfo.getOrderBody(), new TypeToken<List<DeliverInvoiceModel>>() {}.getType());
        List<CustomerInfo> all = null;
        String customerName = "";
        try {
            all = TMSApplication.db.selector(CustomerInfo.class).where("customerID","=",mSubmitInvoiceInfo.getCustomerID()).findAll();
            for(CustomerInfo customerInfo : all){
                customerName = customerInfo.getCustomerName();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".html").getPath();
        ToHtml.convert(mSubmitInvoiceInfo.getInvoiceNo(),mSubmitInvoiceInfo.getRefrence(), mSubmitInvoiceInfo.getCustomerID(), mSubmitInvoiceInfo.getCustomerName(), path, mDeliverInvoiceModelList, barCodeImagePath, this);
        return path;
    }
}
