package com.youcoupon.john_li.transportationapp.TMSUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.youcoupon.john_li.transportationapp.TMSService.PostPositionService;
import com.youcoupon.john_li.transportationapp.TMSService.TimingPositionService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // 打开地图定位服务
            if (!TMSCommonUtils.isServiceRunning(context, "com.youcoupon.john_li.transportationapp.TMSService.TimingPositionService")) {
                Intent intent0 = new Intent(context, TimingPositionService.class);
                context.startService(intent0);
            }

            // 打开定时提交定位信息服务
            if (!TMSCommonUtils.isServiceRunning(context, "com.youcoupon.john_li.transportationapp.TMSService.PostPositionService")) {
                Intent intent1 = new Intent(context, PostPositionService.class);
                context.startService(intent1);
            }
            // 启动activity
             /* Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent); */
        }
    }
}
