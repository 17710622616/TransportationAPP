package com.youcoupon.john_li.transportationapp.TMSService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.TMSActivity.CloseAccountActivity;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostStockMovementModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.SpuUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSApplication;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by John_Li on 1/12/2018.
 */

public class SubmitFailStockIntentService extends IntentService
{
    private static final String ACTION_UPLOAD_IMG = "com.youcoupon.john_li.transportationapp.action.UPLOAD_FIAL_STOCK";
    //private SubmitInvoiceInfo mSubmitInvoiceInfo;

    public SubmitFailStockIntentService()
    {
        super("SubmitFailStockIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            List<TrainsInfo> all = TMSApplication.db.selector(TrainsInfo.class).findAll();
            Log.d("物料結算列表", new Gson().toJson(all));
            for (TrainsInfo trainsInfo : all) {
                if (trainsInfo.getTodayDepositStatus() != 1) {
                    PostStockMovementModel depositModel = new Gson().fromJson(trainsInfo.getTodayDepositBody(), PostStockMovementModel.class);
                    int qty = 0;
                    for (PostStockMovementModel.Line lines : depositModel.getLines()) {
                        qty += lines.getQuantity();
                    }
                    if (qty != 0) {
                        callNetSubmitMaterialsAndSettlement(depositModel, true);
                    }
                }

                if (trainsInfo.getTodayRefundStatus() != 1) {
                    PostStockMovementModel refundModel = new Gson().fromJson(trainsInfo.getTodayRefundBody(), PostStockMovementModel.class);
                    int qty = 0;
                    for (PostStockMovementModel.Line lines : refundModel.getLines()) {
                        qty += lines.getQuantity();
                    }
                    if (qty != 0) {
                        callNetSubmitMaterialsAndSettlement(refundModel, false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交物料结算状态
     */
    private void callNetSubmitMaterialsAndSettlement(final PostStockMovementModel movementModel, final boolean driverout) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(this).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(this).getSalesmanID());
        paramsMap.put("driverout", String.valueOf(driverout));
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.SUBMIT_MATERIALS_SETTLEMENT + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setAsJsonContent(true);
        String json = new Gson().toJson(movementModel);
        params.setBodyContent(json);
        String uri = params.getUri();
        params.setConnectTimeout(30 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    try {
                        String resultStr = TMSCommonUtils.decode(commonModel.getData().toString());
                        WhereBuilder b = WhereBuilder.b();
                        b.and("trains_times", "=", movementModel.getHeader().getTruckNo()); //构造修改的条件V
                        KeyValue name = null;
                        if (driverout) {
                            name = new KeyValue("today_deposit_status", 1);
                        } else {
                            name = new KeyValue("today_refund_status", 1);
                        }
                        TMSApplication.db.update(TrainsInfo.class, b, name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        WhereBuilder b = WhereBuilder.b();
                        b.and("trains_times", "=", movementModel.getHeader().getTruckNo()); //构造修改的条件
                        KeyValue name = null;
                        if (driverout) {
                            name = new KeyValue("today_deposit_status", 2);
                        } else {
                            name = new KeyValue("today_refund_status", 2);
                        }
                        TMSApplication.db.update(TrainsInfo.class, b, name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                try {
                    WhereBuilder b = WhereBuilder.b();
                    b.and("trains_times", "=", movementModel.getHeader().getTruckNo()); //构造修改的条件
                    KeyValue name = null;
                    if (driverout) {
                        name = new KeyValue("today_deposit_status", 2);
                    } else {
                        name = new KeyValue("today_refund_status", 2);
                    }
                    TMSApplication.db.update(TrainsInfo.class, b, name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                EventBus.getDefault().post("REFRESH_BUSINESS");
            }
        });
    }
}