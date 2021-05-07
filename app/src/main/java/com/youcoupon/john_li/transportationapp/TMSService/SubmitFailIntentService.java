package com.youcoupon.john_li.transportationapp.TMSService;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.TMSActivity.DeliverGoodsActivity;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by John_Li on 1/12/2018.
 */

public class SubmitFailIntentService extends IntentService
{
    private static final String ACTION_UPLOAD_IMG = "com.youcoupon.john_li.transportationapp.action.UPLOAD_FIAL_INVOICE";
    //private SubmitInvoiceInfo mSubmitInvoiceInfo;

    public SubmitFailIntentService()
    {
        super("SubmitFailIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /*if (intent != null) {
            SubmitInvoiceInfo mSubmitInvoiceInfo = new Gson().fromJson(intent.getStringExtra("SubmitInvoiceInfo"), SubmitInvoiceInfo.class);
            List<SubmitInvoiceInfo> list = new ArrayList<>();
            List<SubmitInvoiceInfo> all = null;
            try {
                all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
                if (all != null) {
                    if (all.size() > 0) {
                        for (SubmitInvoiceInfo info : all) {
                            list.add(info);
                        }
                    }
                }
            } catch (Exception e) {
                TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
            }

            for (SubmitInvoiceInfo info : list) {
                checkInvoiceType(info);
            }
        }*/

        //List<SubmitInvoiceInfo> all = null;
        try {
            List<SubmitInvoiceInfo> list = new ArrayList<>();
            List<SubmitInvoiceInfo> all = TMSApplication.db.selector(SubmitInvoiceInfo.class).findAll();
            if (all != null) {
                if (all.size() > 0) {
                    list.addAll(all);

                    // 遍历订单表
                    for (SubmitInvoiceInfo info : list) {
                        int depositNum = 0;
                        int refundNum = 0;
                        List<DeliverInvoiceModel> mDeliverInvoiceModelList = new Gson().fromJson(info.getOrderBody(), new TypeToken<List<DeliverInvoiceModel>>() {}.getType());
                        for (DeliverInvoiceModel model : mDeliverInvoiceModelList) {
                            depositNum += model.getSendOutNum();
                            refundNum += model.getRecycleNum();
                        }

                        // 存在提交失败订单重新提交
                        if (info.getRefundStatus() != 1) {
                            if (refundNum > 0) {
                                invoiceResult = invoiceResult + 1;
                                submitTimes ++;
                                callNetSubmitInvoice(mDeliverInvoiceModelList, depositNum, refundNum, 1, info);
                            } else {
                                try {
                                    TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",info.getRefrence()),new KeyValue("refundStatus", 1));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (info.getDepositStatus() != 1) {
                            if (depositNum > 0) {
                                invoiceResult = invoiceResult + 1;
                                submitTimes ++;
                                callNetSubmitInvoice(mDeliverInvoiceModelList, depositNum, refundNum, 0, info);
                            } else {
                                try {
                                    TMSApplication.db.update(SubmitInvoiceInfo.class, WhereBuilder.b().and("refrence","=",info.getRefrence()),new KeyValue("depositStatus", 1));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            TMSCommonUtils.writeTxtToFile(TMSCommonUtils.getTimeNow() + "異常信息：" + e.getStackTrace(), new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TMSFolder").getPath(), TMSCommonUtils.getTimeToday() + "Eoor");
            Toast.makeText(this, "訂單查詢失敗！", Toast.LENGTH_SHORT).show();
        }
    }

    int invoiceResult;
    int submitTimes;

    /**
     * 检查发票类型
     */
    private void checkInvoiceType(SubmitInvoiceInfo mSubmitInvoiceInfo) {
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
            callNetSubmitInvoice(mDeliverInvoiceModelList, depositNum, refundNum, 1, mSubmitInvoiceInfo);
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
            callNetSubmitInvoice(mDeliverInvoiceModelList, depositNum, refundNum, 0, mSubmitInvoiceInfo);
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
     * @param refundNum
     * @param type 0: deposit送出，1:refund回收
     */
    private void callNetSubmitInvoice(List<DeliverInvoiceModel> mDeliverInvoiceModelList, final int depositNum, final int refundNum, final int type, SubmitInvoiceInfo mSubmitInvoiceInfo) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.SUBMIT_DELEIVER_INVOICE + TMSCommonUtils.createLinkStringByGet(paramsMap));
        PostInvoiceModel postInvoiceModel = new PostInvoiceModel();
        com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel.Header header = new PostInvoiceModel.Header();
        header.setCustomerID(mSubmitInvoiceInfo.getCustomerID());
        // 判断是否为主单
        if (type == 0) {
            header.setReference(mSubmitInvoiceInfo.getRefrence());
        } else {
            // 当为发票类型为回收物料时，判断送出物料是否为0，不为0则以送出物料为主单不修改发票号码(即不处理订单号码以送出单的编码为发票编码)，当送出物料数量为0时以回收物料为主单直接修以回收物料发票编码为发票编码)
            if (depositNum == 0) {
                // 主单
                header.setReference(mSubmitInvoiceInfo.getRefrence());
            } else {
                // 子单
                //String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()).replace("-","");
                //time = time.replace(":","");
                //time = time.replace(" ","");
                //header.setReference(TMSShareInfo.IMEI + time);
                header.setReference(mSubmitInvoiceInfo.getRefrence() + "S");
            }
        }
        header.setSalesmanID(TMSCommonUtils.getUserFor40(this).getDriverID());
        header.setDriverID(TMSCommonUtils.getUserFor40(this).getDriverID());
        header.setTruckID(TMSCommonUtils.getUserFor40(this).getTruckID());
        postInvoiceModel.setHeader(header);
        List<PostInvoiceModel.Line> lineList = new ArrayList<>();
        for (DeliverInvoiceModel deliverInvoiceModel : mDeliverInvoiceModelList) {
            int deposit = deliverInvoiceModel.getSendOutNum();
            int refund = deliverInvoiceModel.getRecycleNum();
            if (deliverInvoiceModel.getSendOutNum() != 0 || deliverInvoiceModel.getRecycleNum() != 0) {
                if (type == 0) {
                    if (deliverInvoiceModel.getSendOutNum() != 0) {
                        com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel.Line line = new PostInvoiceModel.Line();
                        line.setMerchandiseID(deliverInvoiceModel.getMaterialId());
                        line.setQuantity(deliverInvoiceModel.getSendOutNum());
                        lineList.add(line);
                    }
                } else {
                    if (deliverInvoiceModel.getRecycleNum() != 0) {
                        com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel.Line line = new PostInvoiceModel.Line();
                        line.setMerchandiseID(deliverInvoiceModel.getMaterialId());
                        line.setQuantity(deliverInvoiceModel.getRecycleNum() * (-1));
                        lineList.add(line);
                    }
                }
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
                    String orderNo = TMSCommonUtils.decode(commonModel.getData().toString());

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
                    String data = TMSCommonUtils.decode(commonModel.getData().toString());
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

                /*try {
                    // 计数重新提交队列不可超过5条
                    int failNum = (int) SpuUtils.get(SubmitFailIntentService.this, "failNum", 0);
                    if (failNum < 5) {
                        // 提交發票失敗時重新遞交
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TMSCommonUtils.resubmitFailOrder(SubmitFailIntentService.this);
                            }
                        }, 10 * 1000);
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }*/
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

                try {
                    // 计数重新提交队列不可超过5条
                    int failNum = (int) SpuUtils.get(SubmitFailIntentService.this, "failNum", 0);
                    SpuUtils.put(SubmitFailIntentService.this, "failNum", failNum-1);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
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