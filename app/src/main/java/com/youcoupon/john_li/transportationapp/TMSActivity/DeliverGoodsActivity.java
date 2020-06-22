package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.DeliverGoodsAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialCorrespondenceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialNumberInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.Barcodemode;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.InvoiceViewModel;
import com.youcoupon.john_li.transportationapp.TMSModel.MaterialCorrespondenceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.ScanHelper;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSView.NoScrollListView;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by John_Li on 26/11/2018.
 */

public class DeliverGoodsActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private TextView delivergoods_customer_no,delivergoods_customer_name, delivergoods_customer_address, delivergoods_tel, scanInvoiceTv, circleClockInTv, invoiceTv;
    private LinearLayout invoiceLL;
    private NoScrollListView mLv;
    private ProgressDialog dialog;

    // 物料订单的数据库类
    private SubmitInvoiceInfo mSubmitInvoiceInfo;
    // 当前发票的物料信息记录
    private List<DeliverInvoiceModel> mDeliverInvoiceModelList;
    private DeliverGoodsAdapter mDeliverGoodsAdapter;
    private MyCodeReciver myCodeReciver;
    private static final String TAG = "MycodeReceiver";
    private static final String BARCODE_ACTION = "com.barcode.sendBroadcast";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_goods);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.delivergoods_head);
        mLv = findViewById(R.id.deliver_goods_lv);
        delivergoods_customer_no = findViewById(R.id.delivergoods_customer_no);
        delivergoods_customer_name = findViewById(R.id.delivergoods_customer_name);
        delivergoods_customer_address = findViewById(R.id.delivergoods_customer_address);
        delivergoods_tel = findViewById(R.id.delivergoods_tel);
        scanInvoiceTv = findViewById(R.id.delivergoods_invoiceno);
        circleClockInTv = findViewById(R.id.shortcut_delivergoods_circle_clockin);
        invoiceTv = findViewById(R.id.shortcut_delivergoods_invoice);
        invoiceLL = findViewById(R.id.delivergoods_invoiceno_ll);
    }

    @Override
    public void setListener() {
        circleClockInTv.setOnClickListener(this);
        invoiceTv.setOnClickListener(this);
    }

    @Override
    public void initData() {
        headView.setTitle("物料回收");
        headView.setLeft(this);

        mSubmitInvoiceInfo = new SubmitInvoiceInfo();
        mDeliverInvoiceModelList = new ArrayList<>();
        getData();
        mDeliverGoodsAdapter = new DeliverGoodsAdapter(this, mDeliverInvoiceModelList);
        mLv.setAdapter(mDeliverGoodsAdapter);

        ScanHelper.setScanSwitchLeft(DeliverGoodsActivity.this, true);
        ScanHelper.setScanSwitchRight(DeliverGoodsActivity.this, true);
        ScanHelper.setBarcodeReceiveModel(DeliverGoodsActivity.this, 2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCodeReciver = new MyCodeReciver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BARCODE_ACTION);
        registerReceiver(myCodeReciver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myCodeReciver);
    }

    /**
     * 获取所有物料用作当前发票的物料记录
     */
    private void getData() {
        try {
            List<MaterialNumberInfo> all = TMSApplication.db.selector(MaterialNumberInfo.class).findAll();
            if(all != null) {
                for(MaterialNumberInfo model : all){
                    DeliverInvoiceModel deliverInvoiceModel = new DeliverInvoiceModel();
                    deliverInvoiceModel.setMaterialId(model.getMaterialID());
                    deliverInvoiceModel.setMaterialName(model.getNameChinese());
                    deliverInvoiceModel.setSendOutNum(0);
                    deliverInvoiceModel.setRecycleNum(0);
                    mDeliverInvoiceModelList.add(deliverInvoiceModel);
                }
            } else {
                Toast.makeText(this, "暫無物料信息，請檢查並更新！", Toast.LENGTH_SHORT).show();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = 26)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.head_right_tv:
                dialog = new ProgressDialog(this);
                dialog.setTitle("提示");
                dialog.setMessage("正在提交發票......");
                dialog.setCancelable(false);
                dialog.show();

                boolean b = false;
                for(DeliverInvoiceModel model : mDeliverInvoiceModelList) {
                    if (model.getSendOutNum() > 0 || model.getRecycleNum() > 0) {
                        b = true;
                    }
                }
                // 判断有物料数量不为空的则生成一条物料记录存入数据库
                if (b) {
                    try {
                        // 物料送出/回收记录
                        mSubmitInvoiceInfo.setOrderBody(new Gson().toJson(mDeliverInvoiceModelList));
                        String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()).replace("-","");
                        time = time.replace(":","");
                        time = time.replace(" ","");
                        mSubmitInvoiceInfo.setRefrence(TMSShareInfo.IMEI + time);
                        mSubmitInvoiceInfo.setSalesmanId(TMSCommonUtils.getUserFor40(this).getSalesmanID());
                        mSubmitInvoiceInfo.setDepositStatus(0);
                        mSubmitInvoiceInfo.setRefundStatus(0);
                        TMSApplication.db.save(mSubmitInvoiceInfo);
                        checkInvoiceType(TMSShareInfo.IMEI + time);
                    } catch (DbException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(DeliverGoodsActivity.this, "訂單存儲失敗，請重試！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(DeliverGoodsActivity.this, "暫無物料需要提交！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.shortcut_delivergoods_invoice:
                Intent intent1 = new Intent();
                intent1.putExtra("result", "1");
                setResult(RESULT_OK, intent1);
                finish();
                break;
            case R.id.shortcut_delivergoods_circle_clockin:
                Intent intent = new Intent();
                intent.putExtra("result", "2");
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    /**
     * 检查发票类型，并存入物料总数量表中
     */
    private void checkInvoiceType(String reference) {
        // 本发票的所有送出及回收数量
        int totalDepositNum = 0;
        int totalRefundNum = 0;
        // 物料总数记录
        for (int i = 0; i < mDeliverInvoiceModelList.size(); i++) {
            int depositNum = 0;
            int refundNum = 0;
            // 查询历史数量
            try {
                List<MaterialNumberInfo> all = TMSApplication.db.selector(MaterialNumberInfo.class).where("material_number_id","=",mDeliverInvoiceModelList.get(i).getMaterialId()).findAll();
                for(MaterialNumberInfo model : all) {
                    depositNum = model.getMaterialDepositeNum();
                    refundNum = model.getMaterialRefundNum();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            int sendOutNum = mDeliverInvoiceModelList.get(i).getSendOutNum();
            int recycleNum = mDeliverInvoiceModelList.get(i).getRecycleNum();
            // 当数量大于0时记录到记录物料总数的表中(MaterialNumberInfo)
            if (mDeliverInvoiceModelList.get(i).getSendOutNum() > 0) {
                try {
                    WhereBuilder b = WhereBuilder.b();
                    b.and("material_number_id","=", mDeliverInvoiceModelList.get(i).getMaterialId()); //构造修改的条件
                    KeyValue name = new KeyValue("material_deposite_num", depositNum + mDeliverInvoiceModelList.get(i).getSendOutNum());
                    TMSApplication.db.update(MaterialNumberInfo.class,b,name);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            if (mDeliverInvoiceModelList.get(i).getRecycleNum() > 0) {
                try {
                    WhereBuilder b = WhereBuilder.b();
                    b.and("material_number_id","=", mDeliverInvoiceModelList.get(i).getMaterialId()); //构造修改的条件
                    KeyValue name = new KeyValue("material_refund_num", refundNum + mDeliverInvoiceModelList.get(i).getRecycleNum());
                    TMSApplication.db.update(MaterialNumberInfo.class,b,name);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            totalDepositNum += depositNum + mDeliverInvoiceModelList.get(i).getSendOutNum();
            totalRefundNum += refundNum + mDeliverInvoiceModelList.get(i).getRecycleNum();

            /*depositNum += mDeliverInvoiceModelList.get(i).getSendOutNum();
            refundNum += mDeliverInvoiceModelList.get(i).getRecycleNum();*/
        }

        // 将物料回收的资料提交到服务器
        if (totalRefundNum > 0) {
            invoiceResult = invoiceResult + 1;
            submitTimes ++;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 提交物料回收发票
            callNetSubmitInvoice(totalDepositNum, totalRefundNum, 1, reference);
        } else {
            try {
                // 当没有回收物料时将回收物料状态记录为提交成功
                TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("refundStatus", 1));
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        if (totalDepositNum > 0) {
            invoiceResult = invoiceResult + 1;
            submitTimes ++;
            // 提交物料送出发票
            callNetSubmitInvoice(totalDepositNum, totalRefundNum, 0, reference);
        } else {
            try {
                // 当没有回收物料时将送出物料状态记录为提交成功
                TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("depositStatus", 1));
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交发票
     * @param depositNum
     * @param refundNum
     * @param type 0: deposit送出，1:refund回收
     * @param reference
     */
    private void callNetSubmitInvoice(final int depositNum, final int refundNum, final int type, final String reference) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.SUBMIT_DELEIVER_INVOICE + TMSCommonUtils.createLinkStringByGet(paramsMap));
        SubmitInvoiceInfo submitInvoiceInfo = new SubmitInvoiceInfo();
        try {
            List<SubmitInvoiceInfo> all = TMSApplication.db.selector(SubmitInvoiceInfo.class).where("refrence","=", reference).findAll();
            for(SubmitInvoiceInfo model : all) {
                submitInvoiceInfo = model;
            }
            PostInvoiceModel postInvoiceModel = new PostInvoiceModel();
            // 发票表头
            PostInvoiceModel.Header header = new PostInvoiceModel.Header();
            header.setCustomerID(submitInvoiceInfo.getCustomerID());
            // 判断是否为主单
            if (type == 0) {
                header.setReference(submitInvoiceInfo.getRefrence());
            } else {
                // 当为发票类型为回收物料时，判断送出物料是否为0，不为0则以送出物料为主單
                // 主单不修改发票号码(即不处理订单号码以送出单的编码为发票编码)，当送出物料数量为0时以回收物料为主单直接修以回收物料发票编码为发票编码)
                if (depositNum == 0) {
                    header.setReference(submitInvoiceInfo.getRefrence());
                } else {
                    String sunReference = TMSShareInfo.IMEI + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()).replace("-", "").replace(":", "").replace(" ", "");
                    mSubmitInvoiceInfo.setSunRefrence(sunReference);
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("refrence", "=", reference);//条件
                        KeyValue keyValue = new KeyValue("sun_refrence", sunReference);
                        TMSApplication.db.update(SubmitInvoiceInfo.class, b, keyValue);
                    } catch (Exception e) {
                        Toast.makeText(this, "666", Toast.LENGTH_SHORT).show();
                    }
                    header.setReference(sunReference);

                    /*String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()).replace("-","");
                    time = time.replace(":","");
                    time = time.replace(" ","");
                    header.setReference(TMSShareInfo.IMEI + time);*/
                }
            }

            header.setSalesmanID(TMSCommonUtils.getUserFor40(this).getSalesmanID());
            postInvoiceModel.setHeader(header);
            // 发票表体
            List<PostInvoiceModel.Line> lineList = new ArrayList<>();
                List<DeliverInvoiceModel> deliverInvoiceModelList = new Gson().fromJson(mSubmitInvoiceInfo.getOrderBody(), new TypeToken<List<DeliverInvoiceModel>>() {}.getType());
                for (DeliverInvoiceModel deliverInvoiceModel : deliverInvoiceModelList) {
                    PostInvoiceModel.Line line = new PostInvoiceModel.Line();
                    line.setMerchandiseID(deliverInvoiceModel.getMaterialId());
                    if (deliverInvoiceModel.getSendOutNum() != 0 || deliverInvoiceModel.getRecycleNum() != 0) {
                        if (type == 0) {
                            line.setQuantity(deliverInvoiceModel.getSendOutNum());
                        } else {
                            line.setQuantity(deliverInvoiceModel.getRecycleNum() * (-1));
                        }
                        lineList.add(line);
                    }
                }
                postInvoiceModel.setLine(lineList);
                params.setAsJsonContent(true);
                String body = new Gson().toJson(postInvoiceModel);
                params.setBodyContent(body);
        } catch (DbException e) {
            e.printStackTrace();
        }
        params.setConnectTimeout(10 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    String orderNo = TMSCommonUtils.decode(commonModel.getData());

                    invoiceResult = invoiceResult - 1;
                    if (type == 0) {
                        try {
                            // 修改发票号码
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=", reference),new KeyValue("invoice_no", orderNo));
                            // 修改发票送出物料状态
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("depositStatus", 1));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 当为发票类型为回收物料时，判断送出物料是否为0，不为0则以送出物料为主单不修改发票号码(即不处理订单号码以送出单的编码为发票编码)，当送出物料数量为0时以回收物料为主单直接修以回收物料发票编码为发票编码)
                        if (depositNum == 0) {
                            try {
                                TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("invoice_no", orderNo));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("sun_invoice_no", orderNo));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("refundStatus", 1));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String data = TMSCommonUtils.decode(commonModel.getData());
                    if (type == 0) {
                        try {
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("depositStatus", 2));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("refundStatus", 2));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(DeliverGoodsActivity.this, "提交發票失敗！" + data, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (type == 0) {
                    try {
                        TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("depositStatus", 2));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",reference),new KeyValue("refundStatus", 2));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(DeliverGoodsActivity.this, "提交發票網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeliverGoodsActivity.this, "提交發票失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                try {
                    submitTimes --;

                    if (submitTimes == 0) {
                        // 当提交完成
                        Intent intent = new Intent(DeliverGoodsActivity.this, TestPrintWebActivity.class);
                        intent.putExtra("ReferenceNo", reference);
                        startActivityForResult(intent, 1);
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    Toast.makeText(DeliverGoodsActivity.this, e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                finish();
            }
        }
    }

    int invoiceResult;
    int submitTimes;

    class MyCodeReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BARCODE_ACTION)) {
                String str = intent.getStringExtra("BARCODE");
                if (!"".equals(str)) {
                    Barcodemode code = new Barcodemode();
                    code.setBarcode(str);
                    code.setNumber("");
                    invoiceLL.setVisibility(View.GONE);
                    IshavaInvoiceCode(code);
                }
            }
        }
    }

    /**
     * 有brcode
     * @param code
     */
    private void IshavaInvoiceCode(Barcodemode code) {
        //添加查询条件进行查询
        try {
            List<InvoiceInfo> all = TMSApplication.db.selector(InvoiceInfo.class).where("invoice_no","=", code.getBarcode().substring(0, code.getBarcode().length() - 1)).findAll();
            InvoiceInfo invoiceInfo = null;
            if (all != null) {
                for(InvoiceInfo info : all){
                    invoiceInfo = info;
                }
            }

            if (invoiceInfo != null) {
                invoiceLL.setVisibility(View.VISIBLE);
                scanInvoiceTv.setText(invoiceInfo.getInvoiceNo());
                CustomerInfo customerInfo = null;
                List<CustomerInfo> customerInfoList = TMSApplication.db.selector(CustomerInfo.class).where("customer_id","=",invoiceInfo.getCustomerID()).findAll();
                for(CustomerInfo info : customerInfoList){
                    customerInfo = info;
                }

                if (customerInfo != null) {
                    mSubmitInvoiceInfo.setCustomerID(customerInfo.getCustomerID());
                    mSubmitInvoiceInfo.setCustomerName(customerInfo.getCustomerName());
                    delivergoods_customer_no.setText(customerInfo.getCustomerID());
                    delivergoods_customer_name.setText(customerInfo.getCustomerName());
                    delivergoods_customer_address.setText(customerInfo.getCustomerAddress());
                    delivergoods_tel.setText(customerInfo.getTelephone());
                    // 填充發票中對應的物料
                    fillInvoiceMaterial(invoiceInfo);
                    headView.setRightText("提交", this);
                    mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(DeliverGoodsActivity.this);
                            dialog1.setCancelable(false);
                            LayoutInflater inflater = LayoutInflater.from(DeliverGoodsActivity.this);
                            View view1 = inflater.inflate(R.layout.dialog_deliver_goods, null);
                            dialog1.setView(view1);//设置使用View
                            //设置控件应该用v1.findViewById 否则出错
                            TextView name = view1.findViewById(R.id.dialog_dg_material_name);
                            final EditText numEt = view1.findViewById(R.id.dialog_dg_num_et);
                            TextView submit = view1.findViewById(R.id.dialog_dg_submit_tv);
                            TextView cancel = view1.findViewById(R.id.dialog_dg_cancel_tv);
                            final RadioButton rb1 = view1.findViewById(R.id.dialog_dg_send_out_rb);
                            final RadioButton rb2 = view1.findViewById(R.id.dialog_dg_recycle_rb);

                            final Dialog d = dialog1.create();
                            name.setText(mDeliverInvoiceModelList.get(position).getMaterialName());
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if (!numEt.getText().equals("")) {
                                            if ((rb1.isChecked() || rb2.isChecked() ) && !(rb1.isChecked() && rb2.isChecked())) {
                                                if (rb1.isChecked()) {
                                                    mDeliverInvoiceModelList.get(position).setSendOutNum(Integer.parseInt(numEt.getText().toString()));
                                                } else if (rb2.isChecked()) {
                                                    mDeliverInvoiceModelList.get(position).setRecycleNum(Integer.parseInt(numEt.getText().toString()));
                                                }
                                                mDeliverGoodsAdapter.notifyDataSetChanged();
                                                d.dismiss();
                                            } else {
                                                Toast.makeText(DeliverGoodsActivity.this, "請選擇送出或回收", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(DeliverGoodsActivity.this, "請輸入數量", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(DeliverGoodsActivity.this, "請輸入數量", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.dismiss();
                                }
                            });
                            d.show();
                        }
                    });
                } else {
                    Toast.makeText(DeliverGoodsActivity.this, "未找到該該發票對應的客戶！", Toast.LENGTH_SHORT).show();
                    IshavaCustomerCode(code);
                }
            } else {
                //Toast.makeText(DeliverGoodsActivity.this, "未找到該該發票！", Toast.LENGTH_SHORT).show();
                IshavaCustomerCode(code);
            }
        } catch (DbException e) {
            e.printStackTrace();
            Toast.makeText(DeliverGoodsActivity.this, "查找發票錯誤！", Toast.LENGTH_SHORT).show();
            IshavaCustomerCode(code);
        }
    }

    // 填充發票貨品對應物料
    private void fillInvoiceMaterial(InvoiceInfo invoiceInfo) {
        // 發票Model轉ViewModel
        List<InvoiceViewModel.InvoiceLine> lineList = new Gson().fromJson(invoiceInfo.getLines(), new TypeToken<List<InvoiceViewModel.InvoiceLine>>() {}.getType());
        try {
            List<MaterialCorrespondenceInfo> materialCorrespondenceList = TMSApplication.db.selector(MaterialCorrespondenceInfo.class).findAll();
            List<MaterialCorrespondenceModel> materialCorrespondenceModelList = new ArrayList<>();
            for (MaterialCorrespondenceInfo info : materialCorrespondenceList) {
                MaterialCorrespondenceModel model = new MaterialCorrespondenceModel();
                model.setMerchandiseID(info.getMerchandiseID().trim());
                List<MaterialCorrespondenceModel.CorrespondingMaterial> materialList = new Gson().fromJson(info.getMaterialListJson(), new TypeToken<List<MaterialCorrespondenceModel.CorrespondingMaterial>>() {}.getType());
                for (MaterialCorrespondenceModel.CorrespondingMaterial correspondingMaterial: materialList) {
                    correspondingMaterial.setMaterialID(correspondingMaterial.getMaterialID().trim());
                }
                model.setMaterial(materialList);
                materialCorrespondenceModelList.add(model);
            }

            for (InvoiceViewModel.InvoiceLine line: lineList){
                for (MaterialCorrespondenceModel materialCorrespondenceModel : materialCorrespondenceModelList) {
                    if (line.getMerchandiseID().equals(materialCorrespondenceModel.getMerchandiseID())) {

                        for (DeliverInvoiceModel model : mDeliverInvoiceModelList){
                            for (MaterialCorrespondenceModel.CorrespondingMaterial correspondingMaterialModel: materialCorrespondenceModel.getMaterial()) {
                                if (model.getMaterialId().equals(correspondingMaterialModel.getMaterialID())) {
                                    model.setSendOutNum(model.getSendOutNum() + (line.getQuantity() * correspondingMaterialModel.getQuantity()));
                                    model.setRecycleNum(model.getRecycleNum() + (line.getQuantity() * correspondingMaterialModel.getQuantity()));
                                }
                            }
                        }
                    }
                }
            }

            mDeliverGoodsAdapter.notifyDataSetChanged();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 有brcode
     * @param code
     */
    private void IshavaCustomerCode(Barcodemode code) {
        //添加查询条件进行查询
        List<CustomerInfo> all = null;
        try {
            all = TMSApplication.db.selector(CustomerInfo.class).where("customer_id","=", code.getBarcode()).findAll();
            CustomerInfo customerInfo = null;
            if (all != null) {
                for(CustomerInfo info :all){
                    customerInfo = info;
                }
            }

            if (customerInfo != null) {
                mSubmitInvoiceInfo.setCustomerID(customerInfo.getCustomerID());
                mSubmitInvoiceInfo.setCustomerName(customerInfo.getCustomerName());
                delivergoods_customer_no.setText(customerInfo.getCustomerID());
                delivergoods_customer_name.setText(customerInfo.getCustomerName());
                delivergoods_customer_address.setText(customerInfo.getCustomerAddress());
                delivergoods_tel.setText(customerInfo.getTelephone());
                headView.setRightText("提交", this);
                mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(DeliverGoodsActivity.this);
                        dialog1.setCancelable(false);
                        LayoutInflater inflater = LayoutInflater.from(DeliverGoodsActivity.this);
                        View view1 = inflater.inflate(R.layout.dialog_deliver_goods, null);
                        dialog1.setView(view1);//设置使用View
                        //设置控件应该用v1.findViewById 否则出错
                        TextView name = view1.findViewById(R.id.dialog_dg_material_name);
                        final EditText numEt = view1.findViewById(R.id.dialog_dg_num_et);
                        TextView submit = view1.findViewById(R.id.dialog_dg_submit_tv);
                        TextView cancel = view1.findViewById(R.id.dialog_dg_cancel_tv);
                        final RadioButton rb1 = view1.findViewById(R.id.dialog_dg_send_out_rb);
                        final RadioButton rb2 = view1.findViewById(R.id.dialog_dg_recycle_rb);
                        final RadioButton rb3 = view1.findViewById(R.id.dialog_dg_send_recycle_rb);

                        final Dialog d = dialog1.create();
                        name.setText(mDeliverInvoiceModelList.get(position).getMaterialName());
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (!numEt.getText().equals("")) {
                                        if ((rb1.isChecked() || rb2.isChecked() || rb3.isChecked())) { //&& !(rb1.isChecked() && rb2.isChecked())
                                            if (rb1.isChecked()) {
                                                mDeliverInvoiceModelList.get(position).setSendOutNum(Integer.parseInt(numEt.getText().toString()));
                                            } else if (rb2.isChecked()) {
                                                mDeliverInvoiceModelList.get(position).setRecycleNum(Integer.parseInt(numEt.getText().toString()));
                                            } else if (rb3.isChecked()) {
                                                mDeliverInvoiceModelList.get(position).setSendOutNum(Integer.parseInt(numEt.getText().toString()));
                                                mDeliverInvoiceModelList.get(position).setRecycleNum(Integer.parseInt(numEt.getText().toString()));
                                            }
                                            mDeliverGoodsAdapter.notifyDataSetChanged();
                                            d.dismiss();
                                        } else {
                                            Toast.makeText(DeliverGoodsActivity.this, "請選擇送出或回收", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(DeliverGoodsActivity.this, "請輸入數量", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(DeliverGoodsActivity.this, "請輸入數量", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                });
            } else {
                Toast.makeText(DeliverGoodsActivity.this, "未找到該客戶！", Toast.LENGTH_SHORT).show();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
