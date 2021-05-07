package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSService.SubmitFailIntentStateService;
import com.youcoupon.john_li.transportationapp.TMSUtils.ScannerRevicer;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;

import java.util.List;

/**
 * Created by John_Li on 22/1/2019.
 */

public class InvoiceStateActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private EditText invoiceNoEt;
    private RadioGroup mRg;
    private RadioButton m40Rb, m71Rb, m72Rb;
    private TextView r0Tv, r1Tv, r2Tv, r3Tv, r4Tv, r5Tv, r6Tv, r7Tv, r8Tv, r9Tv, r10Tv, r11Tv, r12Tv, r13Tv, r14Tv, deliverTv, circleclockin;

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
        TMSCommonUtils.checkTimeByUrl(this);
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
        r12Tv = findViewById(R.id.item_invoice_12_tv);
        r13Tv = findViewById(R.id.item_invoice_13_tv);
        r14Tv = findViewById(R.id.item_invoice_14_tv);
        deliverTv = findViewById(R.id.shortcut_invoice_deliver);
        circleclockin = findViewById(R.id.shortcut_invoice_circle_clockin);
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
        r13Tv.setOnClickListener(this);
        r14Tv.setOnClickListener(this);

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

        deliverTv.setOnClickListener(this);
        circleclockin.setOnClickListener(this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提交發票狀態");
                switch (position) {
                    case 0:
                        builder.setMessage("是否提交發票狀態：收貨");
                        break;
                    case 1:
                        builder.setMessage("是否提交發票狀態：客戶無落單");
                        break;
                    case 2:
                        builder.setMessage("是否提交發票狀態：落錯品種/貨量");
                        break;
                    case 3:
                        builder.setMessage("是否提交發票狀態：無錢比");
                        break;
                    case 4:
                        builder.setMessage("是否提交發票狀態：無開門/店鋪休息");
                        break;
                    case 5:
                        builder.setMessage("是否提交發票狀態：發票地址不詳");
                        break;
                    case 6:
                        builder.setMessage("是否提交發票狀態：客戶要求搬上樓");
                        break;
                    case 7:
                        builder.setMessage("是否提交發票狀態：執漏貨");
                        break;
                    case 8:
                        builder.setMessage("是否提交發票狀態：道路管制");
                        break;
                    case 9:
                        builder.setMessage("是否提交發票狀態：客戶已結業/搬遷");
                        break;
                    case 10:
                        builder.setMessage("是否提交發票狀態：營業員提早通知取消");
                        break;
                    case 11:
                        builder.setMessage("是否提交發票狀態：發票沒有附上PO/無入閘紙");
                        break;
                    case 12:
                        builder.setMessage("是否提交發票狀態：其他");
                        break;
                    case 13:
                        builder.setMessage("是否提交發票狀態：收貨留單");
                        break;
                    case 14:
                        builder.setMessage("是否提交發票狀態：當日未能送貨");
                        break;
                }
                builder.setIcon(R.mipmap.ic_launcher_round);
                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                final int cachePosition = position;
                AlertDialog dialog = null;
                if (cachePosition == 12) {
                    dialog = builder.create();
                    View dialogView = View.inflate(this, R.layout.dialog_other, null);
                    //设置对话框布局
                    dialog.setView(dialogView);
                    dialog.show();
                    final EditText etReason = (EditText) dialogView.findViewById(R.id.dialog_other_reason);
                    Button btnSave = (Button) dialogView.findViewById(R.id.dialog_other_save);
                    Button btnCancel = (Button) dialogView.findViewById(R.id.dialog_other_cancel);
                    final AlertDialog finalDialog = dialog;
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String reason = etReason.getText().toString();
                            if (TextUtils.isEmpty(reason)) {
                                Toast.makeText(InvoiceStateActivity.this, "具體原因不可以為空", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                try {
                                    List<InvoiceStateInfo> all = TMSApplication.db.selector(InvoiceStateInfo.class).where("bill_no","=",invoiceNo).findAll();
                                    if (all != null) {
                                        if (all.size() == 0) {
                                            InvoiceStateInfo invoiceStateInfo = new InvoiceStateInfo();
                                            invoiceStateInfo.setBillNo(invoiceNo);
                                            invoiceStateInfo.setCorp(crop);
                                            invoiceStateInfo.setStatus(0);
                                            UserModel model = TMSCommonUtils.getUserInfoByCorp(InvoiceStateActivity.this, crop);
                                            invoiceStateInfo.setUserID(model.getID());
                                            invoiceStateInfo.setUserName(model.getNameChinese());
                                            invoiceStateInfo.setStaticCode("RCO  RCOO ");
                                            invoiceStateInfo.setStaticType("DELY ");
                                            TMSApplication.db.save(invoiceStateInfo);
                                        } else {
                                            UserModel model = TMSCommonUtils.getUserInfoByCorp(InvoiceStateActivity.this, crop);
                                            WhereBuilder b = WhereBuilder.b();
                                            b.and("bill_no","=", invoiceNo); //构造修改的条件
                                            KeyValue name2 = new KeyValue("static_code", "RCO  RCOO ");
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
                                        UserModel model = TMSCommonUtils.getUserInfoByCorp(InvoiceStateActivity.this, crop);
                                        invoiceStateInfo.setUserID(model.getID());
                                        invoiceStateInfo.setUserName(model.getNameChinese());
                                        invoiceStateInfo.setStaticCode("RCO  RCOO ");
                                        invoiceStateInfo.setStaticType("DELY ");
                                        TMSApplication.db.save(invoiceStateInfo);
                                    }

                                    Intent intent = new Intent(InvoiceStateActivity.this, SubmitFailIntentStateService.class);
                                    intent.putExtra("invoiceStateBillNo", invoiceNo);
                                    intent.putExtra("reason", reason);
                                    startService(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(InvoiceStateActivity.this, "發票狀態保存錯誤！", Toast.LENGTH_SHORT).show();
                                }
                            }
                            finalDialog.dismiss();
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalDialog.dismiss();
                        }
                    });
                } else {
                    //设置正確按钮
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                List<InvoiceStateInfo> all = TMSApplication.db.selector(InvoiceStateInfo.class).where("bill_no","=",invoiceNo).findAll();
                                if (all != null) {
                                    if (all.size() == 0) {
                                        InvoiceStateInfo invoiceStateInfo = new InvoiceStateInfo();
                                        invoiceStateInfo.setBillNo(invoiceNo);
                                        invoiceStateInfo.setCorp(crop);
                                        invoiceStateInfo.setStatus(0);
                                        UserModel model = TMSCommonUtils.getUserInfoByCorp(InvoiceStateActivity.this, crop);
                                        invoiceStateInfo.setUserID(model.getID());
                                        invoiceStateInfo.setUserName(model.getNameChinese());
                                        switch (cachePosition) {
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
                                                invoiceStateInfo.setStaticCode("RCO  RCO9 ");
                                                invoiceStateInfo.setStaticType("DELY ");
                                                break;
                                            case 12:
                                                invoiceStateInfo.setStaticCode("RCO  RCOO ");
                                                invoiceStateInfo.setStaticType("DELY ");
                                                break;
                                            case 13:
                                                invoiceStateInfo.setStaticCode("SD   SD3  ");
                                                invoiceStateInfo.setStaticType("DELY ");
                                                break;
                                            case 14:
                                                invoiceStateInfo.setStaticCode("RCO  RCOA ");
                                                invoiceStateInfo.setStaticType("DELY ");
                                                break;
                                        }
                                        String str = invoiceStateInfo.toString();
                                        TMSApplication.db.save(invoiceStateInfo);
                                    } else {
                                        UserModel model = TMSCommonUtils.getUserInfoByCorp(InvoiceStateActivity.this, crop);
                                        WhereBuilder b = WhereBuilder.b();
                                        b.and("bill_no","=", invoiceNo); //构造修改的条件
                                        KeyValue name2 = null;
                                        switch (cachePosition) {
                                            case 0:
                                                name2 = new KeyValue("static_code", "SD   SD0  ");
                                                break;
                                            case 1:
                                                name2 = new KeyValue("static_code", "RCO  RCO1 ");
                                                break;
                                            case 2:
                                                name2 = new KeyValue("static_code", "RCO  RCO4 ");
                                                break;
                                            case 3:
                                                name2 = new KeyValue("static_code", "RCO  RCS3 ");
                                                break;
                                            case 4:
                                                name2 = new KeyValue("static_code", "RCS  RCS4 ");
                                                break;
                                            case 5:
                                                name2 = new KeyValue("static_code", "RCO  RCO5 ");
                                                break;
                                            case 6:
                                                name2 = new KeyValue("static_code", "RCS  RCS5 ");
                                                break;
                                            case 7:
                                                name2 = new KeyValue("static_code", "RCO  RCO6 ");
                                                break;
                                            case 8:
                                                name2 = new KeyValue("static_code", "RCO  RCO8 ");
                                                break;
                                            case 9:
                                                name2 = new KeyValue("static_code", "RCS  RCS6 ");
                                                break;
                                            case 10:
                                                name2 = new KeyValue("static_code", "RCO  RCO7 ");
                                                break;
                                            case 11:
                                                name2 = new KeyValue("static_code", "RCO  RCO9 ");
                                                break;
                                            case 12:
                                                name2 = new KeyValue("static_code", "RCO  RCOO ");
                                                break;
                                            case 13:
                                                name2 = new KeyValue("static_code", "SD   SD3  ");
                                                break;
                                            case 14:
                                                name2 = new KeyValue("static_code", "RCO  RCOA ");
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
                                    UserModel model = TMSCommonUtils.getUserInfoByCorp(InvoiceStateActivity.this, crop);
                                    invoiceStateInfo.setUserID(model.getID());
                                    invoiceStateInfo.setUserName(model.getNameChinese());
                                    switch (cachePosition) {
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
                                            invoiceStateInfo.setStaticCode("RCO  RCO9 ");
                                            invoiceStateInfo.setStaticType("DELY ");
                                            break;
                                        case 12:
                                            invoiceStateInfo.setStaticCode("RCO  RCOO ");
                                            invoiceStateInfo.setStaticType("DELY ");
                                            break;
                                        case 13:
                                            invoiceStateInfo.setStaticCode("SD   SD3  ");
                                            invoiceStateInfo.setStaticType("DELY ");
                                            break;
                                        case 14:
                                            invoiceStateInfo.setStaticCode("RCO  RCOA ");
                                            invoiceStateInfo.setStaticType("DELY ");
                                            break;
                                    }
                                    String str = invoiceStateInfo.toString();
                                    TMSApplication.db.save(invoiceStateInfo);
                                }

                                Intent intent = new Intent(InvoiceStateActivity.this, SubmitFailIntentStateService.class);
                                intent.putExtra("invoiceStateBillNo", invoiceNo);
                                intent.putExtra("reason", " ");
                                startService(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(InvoiceStateActivity.this, "發票狀態保存錯誤！", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    //设置反面按钮
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                }
                //显示对话框
                dialog.show();
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
            case R.id.item_invoice_13_tv:
                saveInvoiceStateInDB(13);
                break;
            case R.id.item_invoice_14_tv:
                saveInvoiceStateInDB(14);
                break;
            case R.id.shortcut_invoice_deliver:
                Intent intent = new Intent();
                intent.putExtra("result", "1");
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.shortcut_invoice_circle_clockin:
                Intent intent1 = new Intent();
                intent1.putExtra("result", "2");
                setResult(RESULT_OK, intent1);
                finish();
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
