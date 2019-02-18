package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;

import org.greenrobot.eventbus.EventBus;
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
 * Created by John_Li on 1/12/2018.
 */

public class SubmitFailIntentService extends IntentService
{
    private static final String ACTION_UPLOAD_IMG = "com.youcoupon.john_li.transportationapp.action.UPLOAD_FIAL_INVOICE";
    private SubmitInvoiceInfo mSubmitInvoiceInfo;

    public SubmitFailIntentService()
    {
        super("SubmitFailIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            mSubmitInvoiceInfo = new Gson().fromJson(intent.getStringExtra("SubmitInvoiceInfo"), SubmitInvoiceInfo.class);
            checkInvoiceType();
        }
    }

    int invoiceResult;
    int submitTimes;

    /**
     * 检查发票类型
     */
    private void checkInvoiceType() {
        int depositNum = 0;
        int refundNum = 0;
        List<DeliverInvoiceModel> mDeliverInvoiceModelList = new Gson().fromJson(mSubmitInvoiceInfo.getOrderBody(), new TypeToken<List<DeliverInvoiceModel>>() {}.getType());
        for (DeliverInvoiceModel model : mDeliverInvoiceModelList) {
            depositNum += model.getSendOutNum();
            refundNum += model.getRecycleNum();
        }

        if (refundNum > 0) {
            invoiceResult = invoiceResult + 1;
            submitTimes ++;
            callNetSubmitInvoice(mDeliverInvoiceModelList, depositNum, refundNum, 1);
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
            callNetSubmitInvoice(mDeliverInvoiceModelList, depositNum, refundNum, 0);
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
     * @param mDeliverInvoiceModelList
     * @param depositNum
     */
    private void callNetSubmitInvoice(List<DeliverInvoiceModel> mDeliverInvoiceModelList, final int depositNum, final int refundNum, final int type) {
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
                    line.setQuantity(deliverInvoiceModel.getSendOutNum() * (-1));
                } else {
                    line.setQuantity(deliverInvoiceModel.getRecycleNum());
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
                    orderNo = "0" + orderNo;

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
                    Toast.makeText(SubmitFailIntentService.this, "提交發票失敗！" + data, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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
                if (ex instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(SubmitFailIntentService.this, "提交發票網絡連接超時，請重試", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SubmitFailIntentService.this, "提交發票失敗！", Toast.LENGTH_SHORT).show();
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
                    }
                } catch (Exception e) {
                    Toast.makeText(SubmitFailIntentService.this, e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
                }
                EventBus.getDefault().post("SUBMIT_FAIL_INVOICE");
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