package com.youcoupon.john_li.transportationapp.TMSUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.TMSActivity.CloseAccountHistoryActivity;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.CurrentTrunkNoViewModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostStockMovementModel;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

public class TMSBussinessUtils {
    /**
     * 拉取当前最新车次
     */
    public static void asyncLastTrunkNo(Context context,Thread t) {
        long maxTimes = TMSCommonUtils.searchTrainsInfoMaxTimes();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSCommonUtils.getUserFor40(context).getCorp());
        paramsMap.put("userid", TMSCommonUtils.getUserFor40(context).getID());
        paramsMap.put("driverID", TMSCommonUtils.getUserFor40(context).getDriverID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.GET_TRUNKNO_NOW + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setAsJsonContent(true);
        params.setConnectTimeout(30 * 1000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel commonModel = new Gson().fromJson(result, CommonModel.class);
                if (commonModel.getCode() == 0) {
                    String resultStr = TMSCommonUtils.decode(commonModel.getData().toString());
                    CurrentTrunkNoViewModel model = new Gson().fromJson(resultStr, CurrentTrunkNoViewModel.class);
                    if (model.getTruckNo() != maxTimes) {
                        // 当最大车次不等于服务器的车次时直接新增一条
                        try {
                            TrainsInfo trainsInfo = new TrainsInfo();
                            trainsInfo.setTrainsTimes(model.getTruckNo());
                            //trainsInfo.setTodayDepositBody(new Gson().toJson(movementDepositModel));
                            //trainsInfo.setTodayRefundBody(new Gson().toJson(movementRefundModel));
                            trainsInfo.setTodayDate(TMSCommonUtils.getTimeNow());
                            trainsInfo.setTodayDepositStatus(0);
                            trainsInfo.setTodayRefundStatus(0);

                            TMSApplication.db.saveOrUpdate(trainsInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //TrainsInfo depositfirst = TMSApplication.db.findFirst(TrainsInfo.class);
                } else {
                    getTrunkNoErrorUpdateTrunkNo(maxTimes);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                getTrunkNoErrorUpdateTrunkNo(maxTimes);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.d("", "");
            }
        });
    }

    /**
     * 当获取车次失败时
     * @param maxTimes
     */
    public static void getTrunkNoErrorUpdateTrunkNo(long maxTimes) {
        if (maxTimes == 0) {
            // 当为无记录时又获取当前车次失败默认为第一车
            try {
                TrainsInfo trainsInfo = new TrainsInfo();
                trainsInfo.setTrainsTimes(1);
                trainsInfo.setTodayDepositBody("");
                trainsInfo.setTodayRefundBody("");
                trainsInfo.setTodayDate(TMSCommonUtils.getTimeNow());
                trainsInfo.setTodayDepositStatus(0);
                trainsInfo.setTodayRefundStatus(0);

                TMSApplication.db.saveOrUpdate(trainsInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 当为第不为第一车时不处理
    }
}
