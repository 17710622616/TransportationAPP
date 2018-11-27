package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.TodayInvoiceListAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceOutModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John_Li on 27/11/2018.
 */

public class TodayInvoiceListActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private ListView invoiceLv;
    private List<SubmitInvoiceInfo> list;
    private TodayInvoiceListAdapter mTodayInvoiceListAdapter;
    private ProgressDialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_invoice_list);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.today_invoice_head);
        invoiceLv = findViewById(R.id.today_invoice_lv);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        headView.setLeft(this);
        headView.setRightText("重新提交", this);

        list = new ArrayList<>();
        mTodayInvoiceListAdapter = new TodayInvoiceListAdapter(this, list);
        invoiceLv.setAdapter(mTodayInvoiceListAdapter);
        getData();
    }

    private void getData() {
        List<SubmitInvoiceInfo> all = null;
        try {
            all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            for (SubmitInvoiceInfo info : all) {
                list.add(info);
            }
            mTodayInvoiceListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "訂單查詢失敗！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.head_right_tv:
                dialog = new ProgressDialog(TodayInvoiceListActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("正在重新提交失敗訂單......");
                dialog.setCancelable(false);
                dialog.show();
                handler.sendEmptyMessageDelayed(1, 2000);
                break;
        }
    }
}
