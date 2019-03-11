package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.DeliverGoodsAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CustomerInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.MaterialNumberInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.Barcodemode;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.ScanHelper;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
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
import java.util.List;
import java.util.Map;

/**
 * Created by John_Li on 26/11/2018.
 */

public class DeliverGoodsActivity extends BaseActivity implements View.OnClickListener{
    private TMSHeadView headView;
    private TextView delivergoods_customer_no,delivergoods_customer_name, delivergoods_customer_address, delivergoods_tel;
    private ListView mLv;
    private ProgressDialog dialog;

    private SubmitInvoiceInfo mSubmitInvoiceInfo;
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
    }

    @Override
    public void setListener() {
    }

    @Override
    public void initData() {
        headView.setTitle("送貨");
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

    private void getData() {
        DeliverInvoiceModel model = new DeliverInvoiceModel();
        model.setMaterialId("1");
        model.setMaterialName("木卡板");
        model.setSendOutNum(0);
        model.setRecycleNum(0);
        mDeliverInvoiceModelList.add(model);
        DeliverInvoiceModel model1 = new DeliverInvoiceModel();
        model1.setMaterialId("1");
        model1.setMaterialName("膠卡板(大)");
        model1.setSendOutNum(0);
        model1.setRecycleNum(0);
        mDeliverInvoiceModelList.add(model1);
        DeliverInvoiceModel model2 = new DeliverInvoiceModel();
        model2.setMaterialId("013D");
        model2.setMaterialName("專用膠卡板");
        model2.setSendOutNum(0);
        model2.setRecycleNum(0);
        mDeliverInvoiceModelList.add(model2);
        DeliverInvoiceModel model3 = new DeliverInvoiceModel();
        model3.setMaterialId("1");
        model3.setMaterialName("膠片(5加侖)");
        model3.setSendOutNum(0);
        model3.setRecycleNum(0);
        mDeliverInvoiceModelList.add(model3);
        DeliverInvoiceModel model4 = new DeliverInvoiceModel();
        model4.setMaterialId("1");
        model4.setMaterialName("5加侖吉膠桶");
        model4.setSendOutNum(0);
        model4.setRecycleNum(0);
        mDeliverInvoiceModelList.add(model4);
    }

    @RequiresApi(api = 26)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
            case R.id.head_right_tv:
                if (mDeliverInvoiceModelList.get(2).getSendOutNum() > 0 || mDeliverInvoiceModelList.get(2).getRecycleNum() > 0) {
                    try {
                        mSubmitInvoiceInfo.setOrderBody(new Gson().toJson(mDeliverInvoiceModelList));
                        String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()).replace("-","");
                        time = time.replace(":","");
                        time = time.replace(" ","");
                        mSubmitInvoiceInfo.setRefrence(TMSShareInfo.IMEI + time);
                        mSubmitInvoiceInfo.setSalesmanId(TMSCommonUtils.getUserFor40(this).getSalesmanID());
                        mSubmitInvoiceInfo.setDepositStatus(0);
                        mSubmitInvoiceInfo.setRefundStatus(0);
                        TMSApplication.db.save(mSubmitInvoiceInfo);
                        //callNetSubmitInvoice(0);
                        checkInvoiceType();
                    } catch (DbException e) {
                        e.printStackTrace();
                        Toast.makeText(DeliverGoodsActivity.this, "訂單存儲失敗，請重試！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DeliverGoodsActivity.this, "暫無物料需要提交！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 检查发票类型
     */
    private void checkInvoiceType() {
        int depositNum = 0;
        int refundNum = 0;
        for (DeliverInvoiceModel model : mDeliverInvoiceModelList) {
            depositNum += model.getSendOutNum();
            refundNum += model.getRecycleNum();
        }

        if (refundNum > 0) {
            invoiceResult = invoiceResult + 1;
            submitTimes ++;
            try {
                MaterialNumberInfo first = TMSApplication.db.findFirst(MaterialNumberInfo.class);
                if (first != null) {
                    first.setMaterialRefundNum(first.getMaterialRefundNum() + refundNum);
                    TMSApplication.db.saveOrUpdate(first);
                } else {
                    MaterialNumberInfo materialNumberInfo = new MaterialNumberInfo();
                    materialNumberInfo.setId(1);
                    materialNumberInfo.setMaterialName("專用膠卡板");
                    materialNumberInfo.setMaterialRefundNum(refundNum);
                    TMSApplication.db.save(materialNumberInfo);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            callNetSubmitInvoice(depositNum, refundNum, 1);
        } else {
            try {
                TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("refundStatus", 1));
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        if (depositNum > 0) {
            invoiceResult = invoiceResult + 1;
            submitTimes ++;
            try {
                MaterialNumberInfo first = TMSApplication.db.findFirst(MaterialNumberInfo.class);
                if (first != null) {
                    first.setMaterialDepositeNum(first.getMaterialDepositeNum() + depositNum);
                    TMSApplication.db.saveOrUpdate(first);
                } else {
                    MaterialNumberInfo materialNumberInfo = new MaterialNumberInfo();
                    materialNumberInfo.setId(1);
                    materialNumberInfo.setMaterialName("專用膠卡板");
                    materialNumberInfo.setMaterialDepositeNum(depositNum);
                    TMSApplication.db.save(materialNumberInfo);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            callNetSubmitInvoice(depositNum, refundNum, 0);
        } else {
            try {
                TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("depositStatus", 1));
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交发票
     * @param depositNum
     */
    private void callNetSubmitInvoice(final int depositNum, final int refundNum, final int type) {
        dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在更新資料......");
        dialog.setCancelable(false);
        dialog.show();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.SUBMIT_DELEIVER_INVOICE + TMSCommonUtils.createLinkStringByGet(paramsMap));
        PostInvoiceModel postInvoiceModel = new PostInvoiceModel();
        com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel.Header header = new PostInvoiceModel.Header();
        header.setCustomerID(mSubmitInvoiceInfo.getCustomerID());
        header.setReference(mSubmitInvoiceInfo.getRefrence());
        header.setSalesmanID(TMSCommonUtils.getUserFor40(this).getSalesmanID());
        postInvoiceModel.setHeader(header);
        List<PostInvoiceModel.Line> lineList = new ArrayList<>();
        for (DeliverInvoiceModel deliverInvoiceModel : mDeliverInvoiceModelList) {
            com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel.Line line = new PostInvoiceModel.Line();
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
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("invoice_no", orderNo));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }


                        try {
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("depositStatus", 1));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (depositNum == 0) {
                            try {
                                TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("invoice_no", orderNo));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }


                        try {
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("refundStatus", 1));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    String data = TMSCommonUtils.decode(commonModel.getData());
                    if (type == 0) {
                        try {
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("depositStatus", 2));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("refundStatus", 2));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(DeliverGoodsActivity.this, "提交發票失敗！" + data, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                try {
                    TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",mSubmitInvoiceInfo.getRefrence()),new KeyValue("status", 2));
                } catch (DbException e) {
                    e.printStackTrace();
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
                        Intent intent = new Intent(DeliverGoodsActivity.this, TestPrintWebActivity.class);
                        intent.putExtra("ReferenceNo", mSubmitInvoiceInfo.getRefrence());
                        startActivityForResult(intent, 1);
                    }
                    dialog.dismiss();
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
        List<InvoiceInfo> all = null;
        try {
            all = TMSApplication.db.selector(InvoiceInfo.class).where("invoice_no","=", code.getBarcode().substring(0, code.getBarcode().length() - 1)).findAll();
            InvoiceInfo invoiceInfo = null;
            if (all != null) {
                for(InvoiceInfo info : all){
                    invoiceInfo = info;
                }
            }

            if (invoiceInfo != null) {
                CustomerInfo customerInfo = null;
                List<CustomerInfo> customerInfoList = TMSApplication.db.selector(CustomerInfo.class).where("customer_id","=",invoiceInfo.getCustomerID()).findAll();
                for(CustomerInfo customerInfo1 : customerInfoList){
                    customerInfo = customerInfo1;
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
                            if (position == 2) {
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
                        }
                    });
                } else {
                    Toast.makeText(DeliverGoodsActivity.this, "未找到該該發票對應的客戶！", Toast.LENGTH_SHORT).show();
                    IshavaCustomerCode(code);
                }
            } else {
                Toast.makeText(DeliverGoodsActivity.this, "未找到該該發票！", Toast.LENGTH_SHORT).show();
                IshavaCustomerCode(code);
            }
        } catch (DbException e) {
            e.printStackTrace();
            Toast.makeText(DeliverGoodsActivity.this, "查找發票錯誤！", Toast.LENGTH_SHORT).show();
            IshavaCustomerCode(code);
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
                        if (position == 2) {
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
