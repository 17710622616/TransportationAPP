package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;

/**
 * Created by John_Li on 20/7/2018.
 */

public class DataUpdateActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_update);
        initView();
        setListener();
        initData();
        TMSCommonUtils.checkTimeByUrl(this);
    }

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }
}
