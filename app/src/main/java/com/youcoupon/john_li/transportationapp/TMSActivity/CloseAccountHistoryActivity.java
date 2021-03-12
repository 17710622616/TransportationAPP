package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.CloseAccountHistoryAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostStockMovementModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

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
 * Created by John_Li on 11/3/2019.
 */

public class CloseAccountHistoryActivity extends BaseActivity implements View.OnClickListener {
    private ListView mLv;
    private TMSHeadView headView;

    private List<TrainsInfo> mList;
    private CloseAccountHistoryAdapter mCloseAccountHistoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_account_history);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        mLv = findViewById(R.id.close_account_history_lv);
        headView = findViewById(R.id.close_account_history_head);

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        headView.setTitle("物料回收發票歷史");
        headView.setLeft(this);
        headView.setRightText("重新提交", this);
        mList = new ArrayList<>();
        mCloseAccountHistoryAdapter = new CloseAccountHistoryAdapter(this, mList);
        mLv.setAdapter(mCloseAccountHistoryAdapter);
        refreshListView();
    }

    private void refreshListView() {
        try {
            mList.clear();
            List<TrainsInfo> cacheList = TMSApplication.db.findAll(TrainsInfo.class);
            if (cacheList != null) {
                int qty = 0;
                for (TrainsInfo info : cacheList) {
                    PostStockMovementModel depositBody = new Gson().fromJson(info.getTodayDepositBody(), PostStockMovementModel.class);
                    if (depositBody != null) {
                        if (depositBody.getLines() != null) {
                            for (PostStockMovementModel.Line line : depositBody.getLines()) {
                                qty += line.getQuantity();
                            }
                        }
                        PostStockMovementModel refundBody = new Gson().fromJson(info.getTodayRefundBody(), PostStockMovementModel.class);
                        if (refundBody.getLines() != null) {
                            for (PostStockMovementModel.Line line : refundBody.getLines()) {
                                qty += line.getQuantity();
                            }
                        }
                    }

                    if (qty > 0) {
                        mList.add(info);
                    }
                    qty = 0;
                }

                //mList.addAll(cacheList);
            }
            mCloseAccountHistoryAdapter.notifyDataSetChanged();
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
            case R.id.head_right_tv:
                try {
                    List<TrainsInfo> all = TMSApplication.db.selector(TrainsInfo.class).findAll();
                    for (TrainsInfo trainsInfo : all) {
                        if (trainsInfo.getTodayDepositStatus() != 1) {
                            PostStockMovementModel depositModel = new Gson().fromJson(trainsInfo.getTodayDepositBody(), PostStockMovementModel.class);
                            int qty = 0;
                            if (depositModel != null) {
                                for (PostStockMovementModel.Line lines : depositModel.getLines()) {
                                    qty += lines.getQuantity();
                                }
                                if (qty != 0) {
                                    callNetSubmitMaterialsAndSettlement(depositModel, true);
                                }
                            }
                        }

                        if (trainsInfo.getTodayRefundStatus() != 1) {
                            PostStockMovementModel refundModel = new Gson().fromJson(trainsInfo.getTodayRefundBody(), PostStockMovementModel.class);
                            if (refundModel != null){
                                int qty = 0;
                                for (PostStockMovementModel.Line lines : refundModel.getLines()) {
                                    qty += lines.getQuantity();
                                }
                                if (qty != 0) {
                                    callNetSubmitMaterialsAndSettlement(refundModel, false);
                                }
                            }
                        }
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
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
                        WhereBuilder b = WhereBuilder.b();
                        b.and("trains_times", "=", movementModel.getHeader().getTruckNo()); //构造修改的条件
                        KeyValue name = null;
                        if (driverout) {
                            name = new KeyValue("today_deposit_status", 1);
                        } else {
                            name = new KeyValue("today_refund_status", 1);
                        }
                        TMSApplication.db.update(TrainsInfo.class, b, name);
                        Toast.makeText(CloseAccountHistoryActivity.this, commonModel.getMessage() + ",提交結算成功！", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CloseAccountHistoryActivity.this, "提交結算網絡連接超時，請重試" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CloseAccountHistoryActivity.this, "提交結算失敗！" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                refreshListView();
            }
        });
    }
}
