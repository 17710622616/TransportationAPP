package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.InvoiceStateAdapter;
import com.youcoupon.john_li.transportationapp.TMSAdapter.MainAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.ScannerRevicer;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John_Li on 22/1/2019.
 */

public class InvoiceStateActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private EditText invoiceNoEt;
    private RadioGroup mRg;
    private RadioButton m40Rb, m71Rb, m72Rb;
    private TextView r0Tv, r1Tv, r2Tv, r3Tv, r4Tv, r5Tv, r6Tv, r7Tv, r8Tv, r9Tv, r10Tv, r11Tv, r12Tv;

    private ScannerRevicer mScannerRevicer;
    private String invoiceNo;
    private String crop = "40";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_state);
        initView();
        setListener();
        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        mScannerRevicer = new ScannerRevicer(this);
        intentFilter.addAction("com.barcode.sendBroadcast");
        registerReceiver(mScannerRevicer, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScannerRevicer);
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.invoice_state_head);
        //mGv = findViewById(R.id.invoice_state_gv);
        invoiceNoEt = findViewById(R.id.invoice_state_invoice_no);
        mRg = findViewById(R.id.invoice_state_rg);
        m40Rb = findViewById(R.id.invoice_state_40_rb);
        m71Rb = findViewById(R.id.invoice_state_71_rb);
        m72Rb = findViewById(R.id.invoice_state_72_rb);

        r0Tv = findViewById(R.id.item_invoice_0_tv);
        r1Tv = findViewById(R.id.item_invoice_1_tv);
        r2Tv = findViewById(R.id.item_invoice_2_tv);
        r3Tv = findViewById(R.id.item_invoice_3_tv);
        r4Tv = findViewById(R.id.item_invoice_4_tv);
        r5Tv = findViewById(R.id.item_invoice_5_tv);
        r6Tv = findViewById(R.id.item_invoice_6_tv);
        r7Tv = findViewById(R.id.item_invoice_7_tv);
        r8Tv = findViewById(R.id.item_invoice_8_tv);
        r9Tv = findViewById(R.id.item_invoice_9_tv);
        r10Tv = findViewById(R.id.item_invoice_10_tv);
        r11Tv = findViewById(R.id.item_invoice_11_tv);
        r12Tv = findViewById(R.id.item_invoice_11_tv);
    }

    @Override
    public void setListener() {
        /*mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (invoiceNo != null) {
                    if (!invoiceNo.equals("")) {
                        saveInvoiceStateInDB(position);
                    } else {
                        Toast.makeText(InvoiceStateActivity.this, "請先掃描發票！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InvoiceStateActivity.this, "請先掃描發票！", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        r0Tv.setOnClickListener(this);
        r1Tv.setOnClickListener(this);
        r2Tv.setOnClickListener(this);
        r3Tv.setOnClickListener(this);
        r4Tv.setOnClickListener(this);
        r5Tv.setOnClickListener(this);
        r6Tv.setOnClickListener(this);
        r7Tv.setOnClickListener(this);
        r8Tv.setOnClickListener(this);
        r9Tv.setOnClickListener(this);
        r10Tv.setOnClickListener(this);
        r11Tv.setOnClickListener(this);
        r12Tv.setOnClickListener(this);

        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.invoice_state_40_rb:
                        crop = "40";
                        break;
                    case R.id.invoice_state_71_rb:
                        crop = "71";
                        break;
                    case R.id.invoice_state_72_rb:
                        crop = "72";
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        headView.setTitle("掃描發票狀態");
        headView.setLeft(this);
        headView.setRightText("歷史記錄", this);

        List<UserModel> userModelList = new Gson().fromJson(String.valueOf(SpuUtils.get(this, "loginMsg", "")), new TypeToken<List<UserModel>>() {}.getType());
        for (UserModel model : userModelList) {
            if (model.getCorp().equals("40")) {
                m40Rb.setVisibility(View.VISIBLE);
            } else if (model.getCorp().equals("71")) {
                m71Rb.setVisibility(View.VISIBLE);
            } else if (model.getCorp().equals("72")) {
                m72Rb.setVisibility(View.VISIBLE);
            }
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //隐藏软键盘 //
        if (imm != null) {
            imm.hideSoftInputFromWindow(invoiceNoEt.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 將發票保存至DB
     * @param position
     */
    private void saveInvoiceStateInDB(int position) {
        invoiceNo = invoiceNoEt.getText().toString();
        if (invoiceNo != null) {
            if (!invoiceNo.equals("")) {
                try {
                    List<InvoiceStateInfo> all = TMSApplication.db.selector(InvoiceStateInfo.class).where("bill_no","=",invoiceNo).findAll();
                    if (all != null) {
                        if (all.size() == 0) {
                            InvoiceStateInfo invoiceStateInfo = new InvoiceStateInfo();
                            invoiceStateInfo.setBillNo(invoiceNo);
                            invoiceStateInfo.setCorp(crop);
                            invoiceStateInfo.setStatus(0);
                            UserModel model = TMSCommonUtils.getUserInfoByCorp(this, crop);
                            invoiceStateInfo.setUserID(model.getID());
                            invoiceStateInfo.setUserName(model.getNameChinese());
                            switch (position) {
                                case 0:
                                    invoiceStateInfo.setStaticCode("SD   SD0  ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 1:
                                    invoiceStateInfo.setStaticCode("RCO  RCO1 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 2:
                                    invoiceStateInfo.setStaticCode("RCO  RCO4 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 3:
                                    invoiceStateInfo.setStaticCode("RCO  RCS3 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 4:
                                    invoiceStateInfo.setStaticCode("RCS  RCS4 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 5:
                                    invoiceStateInfo.setStaticCode("RCO  RCO5 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 6:
                                    invoiceStateInfo.setStaticCode("RCS  RCS5 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 7:
                                    invoiceStateInfo.setStaticCode("RCO  RCO6 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 8:
                                    invoiceStateInfo.setStaticCode("RCO  RCO8 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 9:
                                    invoiceStateInfo.setStaticCode("RCS  RCS6 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 10:
                                    invoiceStateInfo.setStaticCode("RCO  RCO7 ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 11:
                                    invoiceStateInfo.setStaticCode("");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                                case 12:
                                    invoiceStateInfo.setStaticCode("RCO  RCOO ");
                                    invoiceStateInfo.setStaticType("DELY ");
                                    break;
                            }
                            String str = invoiceStateInfo.toString();
                            TMSApplication.db.save(invoiceStateInfo);
                        } else {
                            UserModel model = TMSCommonUtils.getUserInfoByCorp(this, crop);
                            WhereBuilder b = WhereBuilder.b();
                            b.and("bill_no","=", invoiceNo); //构造修改的条件
                            KeyValue name2 = null;
                            switch (position) {
                                case 0:
                                    name2 = new KeyValue("static_type", "SD   SD0  ");
                                    break;
                                case 1:
                                    name2 = new KeyValue("static_type", "RCO  RCO1 ");
                                    break;
                                case 2:
                                    name2 = new KeyValue("static_type", "RCO  RCO4 ");
                                    break;
                                case 3:
                                    name2 = new KeyValue("static_type", "RCO  RCS3 ");
                                    break;
                                case 4:
                                    name2 = new KeyValue("static_type", "RCS  RCS4 ");
                                    break;
                                case 5:
                                    name2 = new KeyValue("static_type", "RCO  RCO5 ");
                                    break;
                                case 6:
                                    name2 = new KeyValue("static_type", "RCS  RCS5 ");
                                    break;
                                case 7:
                                    name2 = new KeyValue("static_type", "RCO  RCO6 ");
                                    break;
                                case 8:
                                    name2 = new KeyValue("static_type", "RCO  RCO8 ");
                                    break;
                                case 9:
                                    name2 = new KeyValue("static_type", "RCS  RCS6 ");
                                    break;
                                case 10:
                                    name2 = new KeyValue("static_type", "RCO  RCO7 ");
                                    break;
                                case 11:
                                    name2 = new KeyValue("static_type", "");
                                    break;
                                case 12:
                                    name2 = new KeyValue("static_type", "RCO  RCOO ");
                                    break;
                            }

                            KeyValue name = new KeyValue("corp", crop);
                            KeyValue name1 = new KeyValue("status", 0);
                            KeyValue name3 = new KeyValue("user_id", model.getID());
                            KeyValue name4 = new KeyValue("user_name", model.getNameChinese());
                            TMSApplication.db.update(InvoiceStateInfo.class,b,name, name1, name2, name3, name4);
                        }
                    } else {
                        InvoiceStateInfo invoiceStateInfo = new InvoiceStateInfo();
                        invoiceStateInfo.setBillNo(invoiceNo);
                        invoiceStateInfo.setCorp(crop);
                        invoiceStateInfo.setStatus(0);
                        UserModel model = TMSCommonUtils.getUserInfoByCorp(this, crop);
                        invoiceStateInfo.setUserID(model.getID());
                        invoiceStateInfo.setUserName(model.getNameChinese());
                        switch (position) {
                            case 0:
                                invoiceStateInfo.setStaticCode("SD   SD0  ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 1:
                                invoiceStateInfo.setStaticCode("RCO  RCO1 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 2:
                                invoiceStateInfo.setStaticCode("RCO  RCO4 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 3:
                                invoiceStateInfo.setStaticCode("RCO  RCS3 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 4:
                                invoiceStateInfo.setStaticCode("RCS  RCS4 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 5:
                                invoiceStateInfo.setStaticCode("RCO  RCO5 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 6:
                                invoiceStateInfo.setStaticCode("RCS  RCS5 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 7:
                                invoiceStateInfo.setStaticCode("RCO  RCO6 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 8:
                                invoiceStateInfo.setStaticCode("RCO  RCO8 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 9:
                                invoiceStateInfo.setStaticCode("RCS  RCS6 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 10:
                                invoiceStateInfo.setStaticCode("RCO  RCO7 ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 11:
                                invoiceStateInfo.setStaticCode("");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                            case 12:
                                invoiceStateInfo.setStaticCode("RCO  RCOO ");
                                invoiceStateInfo.setStaticType("DELY ");
                                break;
                        }
                        String str = invoiceStateInfo.toString();
                        TMSApplication.db.save(invoiceStateInfo);
                    }

                    Intent intent = new Intent(InvoiceStateActivity.this, SubmitFailIntentStateService.class);
                    intent.putExtra("invoiceStateBillNo", invoiceNo);
                    startService(intent);
                } catch (DbException e) {
                    e.printStackTrace();
                    Toast.makeText(InvoiceStateActivity.this, "發票狀態保存錯誤！", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(InvoiceStateActivity.this, "請先掃描發票！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(InvoiceStateActivity.this, "請先掃描發票！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.head_right_tv:
                startActivity(new Intent(this, InvoiceStateFailActivity.class));
                break;
            case R.id.item_invoice_0_tv:
                saveInvoiceStateInDB(0);
                break;
            case R.id.item_invoice_1_tv:
                saveInvoiceStateInDB(1);
                break;
            case R.id.item_invoice_2_tv:
                saveInvoiceStateInDB(2);
                break;
            case R.id.item_invoice_3_tv:
                saveInvoiceStateInDB(3);
                break;
            case R.id.item_invoice_4_tv:
                saveInvoiceStateInDB(4);
                break;
            case R.id.item_invoice_5_tv:
                saveInvoiceStateInDB(5);
                break;
            case R.id.item_invoice_6_tv:
                saveInvoiceStateInDB(6);
                break;
            case R.id.item_invoice_7_tv:
                saveInvoiceStateInDB(7);
                break;
            case R.id.item_invoice_8_tv:
                saveInvoiceStateInDB(8);
                break;
            case R.id.item_invoice_9_tv:
                saveInvoiceStateInDB(9);
                break;
            case R.id.item_invoice_10_tv:
                saveInvoiceStateInDB(10);
                break;
            case R.id.item_invoice_11_tv:
                saveInvoiceStateInDB(11);
                break;
            case R.id.item_invoice_12_tv:
                saveInvoiceStateInDB(12);
                break;
        }
    }

    public void scanCallback(String barcode) {
        if(barcode != null) {
            if (!barcode.equals("")) {
                invoiceNo = barcode.substring(0, 7);
                invoiceNoEt.setText(invoiceNo);
            } else {
                Toast.makeText(InvoiceStateActivity.this, "未掃描到條碼！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(InvoiceStateActivity.this, "未掃描到條碼！", Toast.LENGTH_SHORT).show();
        }
    }
}