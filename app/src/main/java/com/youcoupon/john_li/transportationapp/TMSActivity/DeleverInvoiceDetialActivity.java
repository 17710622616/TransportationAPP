package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
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
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.ToHtml;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John_Li on 27/11/2018.
 */

public class DeleverInvoiceDetialActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private OrderDetialPageAdapter mAdapter;
    private SubmitInvoiceInfo mSubmitInvoiceInfo;
    private ArrayList<WebView> mViewList;
    private ViewPager mVp;
    private ImageView nextPageIv;
    private ImageView previousPageIv;
    String sunUrl;
    private WebView sunWebview;
    private TextView textView;
    String url;
    private WebView webview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_invoice_detial);
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

        //webview = (WebView) findViewById(R.id.invoice_detial_wv);
        headView = findViewById(R.id.invoice_detial_head);
        previousPageIv = (ImageView) findViewById(R.id.detial_previous_page);
        nextPageIv = (ImageView) findViewById(R.id.detial_next_page);
        headView = (TMSHeadView) findViewById(R.id.invoice_detial_head);
        mVp = (ViewPager) findViewById(R.id.invoice_detial_vp);
    }

    @Override
    public void setListener() {
        previousPageIv.setOnClickListener(this);
        nextPageIv.setOnClickListener(this);
    }

    @Override
    public void initData() {
        headView.setLeft(this);
        headView.setTitle("訂單詳情");
        mViewList = new ArrayList<>();
        LayoutInflater layoutInflater = getLayoutInflater();
        try {
            for (SubmitInvoiceInfo info : TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll()) {
                if (info.getRefrence().equals(getIntent().getStringExtra("ReferenceNo"))) {
                    mSubmitInvoiceInfo = info;
                }
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
        } catch (Exception e) {
            Toast.makeText(this, "訂單查詢失敗！", Toast.LENGTH_SHORT).show();
        }
//        List<SubmitInvoiceInfo> all = null;
//        try {
//            all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
//            for (SubmitInvoiceInfo info : all) {
//                if (info.getRefrence().equals(getIntent().getStringExtra("ReferenceNo")))
//                    mSubmitInvoiceInfo = info;
//            }
//
//            // 设置WebView属性，能够执行Javascript脚本
//            WebSettings settings = webview.getSettings();
//            settings.setJavaScriptEnabled(true);
//            settings.setPluginState(WebSettings.PluginState.ON);
//            //settings.setPluginsEnabled(true);
//            webview.setWebViewClient(new DeleverInvoiceDetialActivity.MvtFlashWebViewClient());
//            // 截图用
//            webview.setDrawingCacheEnabled(true);
//            // 自适应屏幕大小
//            settings.setLoadWithOverviewMode(true);
//
//            if (mSubmitInvoiceInfo.getInvoiceNo() != null) {
//                Bitmap bm = TMSCommonUtils.creatBarcode(DeleverInvoiceDetialActivity.this, TMSCommonUtils.ean8(mSubmitInvoiceInfo.getInvoiceNo()),160,60, false);
//                url = "file:///" + testCreateHTML(TMSCommonUtils.saveBitmap(bm));// 载入本地生成的页面
//            } else {
//                url = "file:///" + testCreateHTML("");// 载入本地生成的页面
//            }
//            webview.loadUrl(url);
//        } catch (Exception e) {
//            Toast.makeText(this, "訂單查詢失敗！", Toast.LENGTH_SHORT).show();
//        }
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
        } else if (v.getId() == R.id.detial_previous_page) {
            this.previousPageIv.setVisibility(View.GONE);
            this.nextPageIv.setVisibility(View.VISIBLE);
            this.mVp.setCurrentItem(0);
        } else if (v.getId() == R.id.detial_next_page) {
            this.previousPageIv.setVisibility(View.VISIBLE);
            this.nextPageIv.setVisibility(View.GONE);
            this.mVp.setCurrentItem(1);
        }
    }


    public String testCreateHTML(String barCodeImagePath, boolean isMother) {
        List<DeliverInvoiceModel> mDeliverInvoiceModelList = new Gson().fromJson(mSubmitInvoiceInfo.getOrderBody(), new TypeToken<List<DeliverInvoiceModel>>() {}.getType());
        List<CustomerInfo> all = null;
        String customerName = "";
        try {
            all = TMSApplication.db.selector(CustomerInfo.class).where("customerID","=",mSubmitInvoiceInfo.getCustomerID()).findAll();
            for(CustomerInfo customerInfo : all){
                customerName = customerInfo.getCustomerName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".html").getPath();
        if (isMother) {
            ToHtml.convert(mSubmitInvoiceInfo.getInvoiceNo(), mSubmitInvoiceInfo.getRefrence(), mSubmitInvoiceInfo.getCustomerID(), mSubmitInvoiceInfo.getCustomerName(), path, mDeliverInvoiceModelList, barCodeImagePath, this, isMother);
        } else {
            ToHtml.convert(mSubmitInvoiceInfo.getSunInvoiceNo(), mSubmitInvoiceInfo.getSunRefrence(), mSubmitInvoiceInfo.getCustomerID(), mSubmitInvoiceInfo.getCustomerName(), path, mDeliverInvoiceModelList, barCodeImagePath, this, isMother);
        }
        //ToHtml.convert(mSubmitInvoiceInfo.getInvoiceNo(), mSubmitInvoiceInfo.getRefrence(), mSubmitInvoiceInfo.getCustomerID(), mSubmitInvoiceInfo.getCustomerName(), path, mDeliverInvoiceModelList, barCodeImagePath, this);
        return path;
    }
}
