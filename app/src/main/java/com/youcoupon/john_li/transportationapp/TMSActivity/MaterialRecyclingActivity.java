package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.w3c.dom.Text;

/**
 * Created by John_Li on 7/8/2018.
 */

public class MaterialRecyclingActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private TextView customerNumTv, customerNameTv, addressTv, telTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_recycling);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.material_recycling_head_view);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        headView.setTitle("回收頁面");
        headView.setLeft(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                break;
        }
    }
}
