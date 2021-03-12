package com.youcoupon.john_li.transportationapp.TMSService;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.InvoiceStateInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by John_Li on 1/12/2018.
 */

public class SubmitFailIntentStateService extends IntentService {

    public SubmitFailIntentStateService()
    {
        super("SubmitFailIntentStateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String billNo = intent.getStringExtra("invoiceStateBillNo");
            String reason = intent.getStringExtra("reason");
            try {
                InvoiceStateInfo invoiceStateInfo = null;
                //添加查询条件进行查询
                List<InvoiceStateInfo> all = TMSApplication.db.selector(InvoiceStateInfo.class).where("bill_no","=",billNo).and("status","!=", 1).findAll();
                if (all != null) {
                    for(InvoiceStateInfo info :all){
                        invoiceStateInfo = info;
                    }

                    callNetSubmitInvoiceState(invoiceStateInfo, reason);
                } else {
                    Toast.makeText(SubmitFailIntentStateService.this, "暫無未提交發票！", Toast.LENGTH_SHORT).show();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交发票状态
     */
    private void callNetSubmitInvoiceState(final InvoiceStateInfo invoiceStateInfo, String reason) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", invoiceStateInfo.getCorp());
        paramsMap.put("userid", invoiceStateInfo.getUserID());
        paramsMap.put("billno", invoiceStateInfo.getBillNo());
        paramsMap.put("statistictype", invoiceStateInfo.getStaticType());
        paramsMap.put("statisticcode", invoiceStateInfo.getStaticCode());
        paramsMap.put("reason", reason);
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.SUBMIT_INVOICE_STATE + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setConnectTimeout(10 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        String billno = invoiceStateInfo.getBillNo();
                        b.and("bill_no","=", invoiceStateInfo.getBillNo()); //构造修改的条件
                        KeyValue name = new KeyValue("status", 1);
                        int i = TMSApplication.db.update(InvoiceStateInfo.class,b,name);
                        Toast.makeText(SubmitFailIntentStateService.this, "提交發票成功！" + commonModel.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String data = TMSCommonUtils.decode(commonModel.getData().toString());
                        WhereBuilder b = WhereBuilder.b();
                        String billno = invoiceStateInfo.getBillNo();
                        b.and("bill_no","=", invoiceStateInfo.getBillNo()); //构造修改的条件
                        KeyValue name1 = new KeyValue("status", 2);
                        KeyValue name2 = new KeyValue("resultReson", data);
                        int i = TMSApplication.db.update(InvoiceStateInfo.class,b, name1, name2);
                        Toast.makeText(SubmitFailIntentStateService.this, "提交發票失敗！" + commonModel.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                try {
                    WhereBuilder b = WhereBuilder.b();
                    String billno = invoiceStateInfo.getBillNo();
                    b.and("bill_no","=", invoiceStateInfo.getBillNo()); //构造修改的条件
                    KeyValue name1 = new KeyValue("status", 2);
                    KeyValue name2 = new KeyValue("resultReson", "");
                    int i= TMSApplication.db.update(InvoiceStateInfo.class,b, name1, name2);
                } catch (DbException e) {
                    e.printStackTrace();
                }

                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(SubmitFailIntentStateService.this, "提交發票網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SubmitFailIntentStateService.this, "提交發票失敗！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}