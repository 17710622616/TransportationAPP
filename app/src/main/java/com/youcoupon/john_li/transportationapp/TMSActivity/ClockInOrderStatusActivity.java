package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.BLSOrderStatusAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInCustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInOrderStatusInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.ClockInPhotoInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.ClockInOrderStatusModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John_Li on 20/5/2019.
 */

public class ClockInOrderStatusActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private ListView mLv;
    private BLSOrderStatusAdapter mAdapter;
    private List<ClockInOrderStatusModel> list;
    private TextView deliverTv, invoiceTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bls_customer);
        initView();
        setListener();
        initData();
        TMSCommonUtils.checkTimeByUrl(this);
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.bls_customer_head);
        mLv = findViewById(R.id.bls_customer_lv);
        deliverTv = findViewById(R.id.shortcut_circle_deliver);
        invoiceTv = findViewById(R.id.shortcut_circle_invoice);
    }

    @Override
    public void setListener() {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClockInOrderStatusActivity.this, ClockInCustomerDetialActivity.class);
                intent.putExtra("ClockInOrderStatusModel", new Gson().toJson(list.get(position)));
                startActivityForResult(intent, 1);
            }
        });

        deliverTv.setOnClickListener(this);
        invoiceTv.setOnClickListener(this);
    }

    @Override
    public void initData() {
        headView.setLeft(this);
        headView.setTitle("BLS簽到");
        mAdapter = new BLSOrderStatusAdapter(this, getData());
        mLv.setAdapter(mAdapter);
    }

    private List<ClockInOrderStatusModel> getData() {
        list = new ArrayList<>();
        try {
            List<ClockInOrderStatusInfo> clockInOrderStatusInfos = TMSApplication.db.selector(ClockInOrderStatusInfo.class).findAll();
            if (clockInOrderStatusInfos != null) {
                for (ClockInOrderStatusInfo clockInOrderStatusInfo : clockInOrderStatusInfos) {
                    ClockInCustomerInfo clockInCustomerInfo = TMSApplication.db.selector(ClockInCustomerInfo.class).where("customer_id","=",clockInOrderStatusInfo.getCustomerID()).findFirst();
                    if (clockInCustomerInfo != null) {
                        List<ClockInPhotoInfo> ClockInPhotoInfos = TMSApplication.db.selector(ClockInPhotoInfo.class).where("customer_id","=",clockInCustomerInfo.getCustomerID()).findAll();
                        ClockInOrderStatusModel model = new ClockInOrderStatusModel();
                        model.setCustomerID(clockInCustomerInfo.getCustomerID());
                        model.setCustomerAddress(clockInCustomerInfo.getCustomerAddress());
                        model.setCustomerName(clockInCustomerInfo.getCustomerName());
                        model.setTelephone(clockInCustomerInfo.getTelephone());
                        model.setContact(clockInCustomerInfo.getContact());
                        model.setOperatorID(clockInOrderStatusInfo.getOperatorID());
                        model.setSeqNo(clockInOrderStatusInfo.getSeqNo());
                        model.setWeekday(clockInOrderStatusInfo.getWeekday());
                        if (ClockInPhotoInfos != null) {
                            if(ClockInPhotoInfos.size() > 0) {
                                model.setClockIn(true);
                            } else {
                                model.setClockIn(false);
                            }
                        } else {
                            model.setClockIn(false);
                        }
                        list.add(model);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.shortcut_circle_deliver:
                Intent intent = new Intent();
                intent.putExtra("result", "1");
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.shortcut_circle_invoice:
                Intent intent1 = new Intent();
                intent1.putExtra("result", "2");
                setResult(RESULT_OK, intent1);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data == null) {
                    return;
                }
                String result = data.getStringExtra("result");
                switch (result) {
                    case "1":
                        mAdapter.refreshData(getData());
                        break;
                    case "2":
                        Intent intent = new Intent();
                        intent.putExtra("result", "1");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case "3":
                        Intent intent1 = new Intent();
                        intent1.putExtra("result", "2");
                        setResult(RESULT_OK, intent1);
                        finish();
                        break;
                }
            }
        }
    }
}
