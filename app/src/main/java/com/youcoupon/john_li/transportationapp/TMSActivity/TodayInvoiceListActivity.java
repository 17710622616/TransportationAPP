package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.TodayInvoiceListAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSService.SubmitFailIntentService;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
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
                    try {
                        if (dialog != null) {
                            dialog.dismiss();
                            Toast.makeText(TodayInvoiceListActivity.this, "重新提交操作完成！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {

                    }
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
        TMSCommonUtils.checkTimeByUrl(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        headView.setRightTextEnable();
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
        list.clear();
        List<SubmitInvoiceInfo> all = null;
        try {
            all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            if (all != null) {
                if (all.size() > 0) {
                    for (SubmitInvoiceInfo info : all) {
                        list.add(info);
                    }
                    mTodayInvoiceListAdapter.refreshData(list);
                } else {
                    Toast.makeText(this, "暫無今日訂單！", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "暫無今日訂單！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
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
                headView.setRightTextUnable();
                getData();
                if(list.size() > 0) {
                    dialog = new ProgressDialog(TodayInvoiceListActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("正在重新提交失敗訂單......");
                    dialog.setCancelable(false);
                    dialog.show();

                    TMSCommonUtils.resubmitFailOrder(this);

                    handler.sendEmptyMessageDelayed(1, 20 * 1000);
                } else {
                    Toast.makeText(this, "訂單已全部提交成功！", Toast.LENGTH_SHORT).show();
                }

                for (SubmitInvoiceInfo info : list) {
                    if (info.getRefundStatus() != 1 || info.getDepositStatus() != 1) {
                        failOrderNum ++;
                    }
                }
                /*for (SubmitInvoiceInfo info : list) {
                    if (info.getRefundStatus() != 1 || info.getDepositStatus() != 1) {
                        failOrderNum ++;
                        //callNetSubmitFailOrder(info);
                        //Intent intent = new Intent(TodayInvoiceListActivity.this, SubmitFailIntentService.class);
                        //intent.putExtra("SubmitInvoiceInfo", new Gson().toJson(info));
                        //startService(intent);
                    }
                }
                // 当有提交失败时
                if (failOrderNum > 0) {
                    Intent intent = new Intent(TodayInvoiceListActivity.this, SubmitFailIntentService.class);
                    intent.putExtra("SubmitInvoiceInfo", new Gson().toJson(null));
                    startService(intent);
                }*/
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

                if (headView !=null) {
                    headView.setRightTextEnable();
                }
            }
        }
    }
}
