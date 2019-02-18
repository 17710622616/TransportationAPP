package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.TodayInvoiceListAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceOutModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        EventBus.getDefault().register(this);
        initView();
        setListener();
        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.today_invoice_head);
        invoiceLv = findViewById(R.id.today_invoice_lv);
    }

    @Override
    public void setListener() {
        invoiceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TodayInvoiceListActivity.this, DeleverInvoiceDetialActivity.class);
                intent.putExtra("ReferenceNo", list.get(position).getRefrence());
                startActivity(intent);
            }
        });
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

    int failOrderNum;

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
                for (SubmitInvoiceInfo info : list) {
                    if (info.getRefundStatus() != 1 || info.getDepositStatus() != 1) {
                        failOrderNum ++;
                        //callNetSubmitFailOrder(info);
                        Intent intent = new Intent(TodayInvoiceListActivity.this, SubmitFailIntentService.class);
                        intent.putExtra("SubmitInvoiceInfo", new Gson().toJson(info));
                        startService(intent);
                    }
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(String msg){
        if (msg.equals("SUBMIT_FAIL_INVOICE")) {
            failOrderNum --;

            if (failOrderNum == 0) {
                dialog.dismiss();
                list.clear();
                getData();
            }
        }
    }
}
