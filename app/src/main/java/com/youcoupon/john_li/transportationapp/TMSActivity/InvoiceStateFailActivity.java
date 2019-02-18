package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.InvoiceStateListAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John_Li on 25/1/2019.
 */

public class InvoiceStateFailActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private ListView mLv;

    private List<InvoiceStateInfo> recordList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_state_fail);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.invoice_state_fail_head);
        mLv = findViewById(R.id.invoice_state_fail_lv);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        headView.setLeft(this);
        headView.setTitle("歷史記錄");
        headView.setRightText("重新提交", this);

        recordList = new ArrayList<>();
        try {
            //添加查询条件进行查询
            List<InvoiceStateInfo> all = TMSApplication.db.selector(InvoiceStateInfo.class).findAll();
            if (all != null) {
                recordList.addAll(all);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        mLv.setAdapter(new InvoiceStateListAdapter(this, recordList));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.head_right_tv:
                try {
                    //添加查询条件进行查询
                    List<InvoiceStateInfo> all = new ArrayList<>();
                    all.addAll(TMSApplication.db.selector(InvoiceStateInfo.class).where("status","!=",1).findAll());
                    for(InvoiceStateInfo info :all){
                        Intent intent = new Intent(InvoiceStateFailActivity.this, SubmitFailIntentStateService.class);
                        intent.putExtra("invoiceStateBillNo", info.getBillNo());
                        startService(intent);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
