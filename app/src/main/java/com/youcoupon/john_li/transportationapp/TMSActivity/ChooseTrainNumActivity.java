package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

public class ChooseTrainNumActivity extends BaseActivity implements View.OnClickListener {
    private TMSHeadView headView;
    private TextView notTv, firstTv, secondTv, thridTv, fourthTv, fifthTv, noArrangeTv;
    private ProgressDialog mLoadDialog;
    private String mInvoiceListJson;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_train_num);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.choose_trains_head_view);
        notTv = findViewById(R.id.choose_trains_not_divided);
        firstTv = findViewById(R.id.choose_trains_first);
        secondTv = findViewById(R.id.choose_trains_second);
        thridTv = findViewById(R.id.choose_trains_thrid);
        fourthTv = findViewById(R.id.choose_trains_fourth);
        fifthTv = findViewById(R.id.choose_trains_fifth);
        noArrangeTv = findViewById(R.id.choose_trains_not_arrange);
    }

    @Override
    public void setListener() {
        notTv.setOnClickListener(this);
        firstTv.setOnClickListener(this);
        secondTv.setOnClickListener(this);
        thridTv.setOnClickListener(this);
        fourthTv.setOnClickListener(this);
        fifthTv.setOnClickListener(this);
        noArrangeTv.setOnClickListener(this);
    }

    @Override
    public void initData() {
        headView.setTitle("選擇車次");
        headView.setLeft(this);
        mInvoiceListJson = getIntent().getStringExtra("InvoiceList");
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.head_left:
                // 做賬戶解鎖------------------------------------------------------------------------>未處理
                doCheckOut();
                finish();
                break;
            case R.id.choose_trains_not_divided:
                /*intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity.class);*/
                intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity1.class);
                intent.putExtra("Trains", "NOT_DIVIDED");
                intent.putExtra("DataWay", "TODAY");
                intent.putExtra("InvoiceList", mInvoiceListJson);
                startActivity(intent);
                finish();
                break;
            case R.id.choose_trains_first:
                intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity1.class);
                intent.putExtra("Trains", "FIRST");
                intent.putExtra("DataWay", "TODAY");
                intent.putExtra("InvoiceList", mInvoiceListJson);
                startActivity(intent);
                finish();
                break;
            case R.id.choose_trains_second:
                intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity1.class);
                intent.putExtra("Trains", "SECOND");
                intent.putExtra("DataWay", "TODAY");
                intent.putExtra("InvoiceList", mInvoiceListJson);
                startActivity(intent);
                finish();
                break;
            case R.id.choose_trains_thrid:
                intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity1.class);
                intent.putExtra("Trains", "THIRD");
                intent.putExtra("DataWay", "TODAY");
                intent.putExtra("InvoiceList", mInvoiceListJson);
                startActivity(intent);
                finish();
                break;
            case R.id.choose_trains_fourth:
                intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity1.class);
                intent.putExtra("Trains", "FOURTH");
                intent.putExtra("DataWay", "TODAY");
                intent.putExtra("InvoiceList", mInvoiceListJson);
                startActivity(intent);
                finish();
                break;
            case R.id.choose_trains_fifth:
                intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity1.class);
                intent.putExtra("Trains", "FIFTH");
                intent.putExtra("DataWay", "TODAY");
                intent.putExtra("InvoiceList", mInvoiceListJson);
                startActivity(intent);
                finish();
                break;
            case R.id.choose_trains_not_arrange:
                intent = new Intent(ChooseTrainNumActivity.this, CarSplitActivity1.class);
                intent.putExtra("Trains", "NOT_ARRANGE");
                intent.putExtra("DataWay", "TODAY");
                intent.putExtra("InvoiceList", mInvoiceListJson);
                startActivity(intent);
                break;
        }
    }

    /**
     * 登出操作
     */
    private void doCheckOut() {
        mLoadDialog = new ProgressDialog(this);
        mLoadDialog.setTitle("加載");
        mLoadDialog.setMessage("正在退出操作......");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        paramsMap.put("DriverID", TMSShareInfo.mUserModelList.get(0).getDriverID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.CHECK_OUT_OCCUPY + TMSCommonUtils.createLinkStringByGet(paramsMap));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel model = new Gson().fromJson(result, CommonModel.class);
                if (model.getCode() == 0) {
                    finish();
                } else {
                    Toast.makeText(ChooseTrainNumActivity.this,  String.valueOf(model.getMessage()) + "，退出操作失败，请重试！", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(ChooseTrainNumActivity.this, "退出操作異常，请重试或聯繫IT人員！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mLoadDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mLoadDialog != null) {
            if (mLoadDialog.isShowing()) {
                mLoadDialog.dismiss();
            }
        }
        super.onDestroy();
    }
}
