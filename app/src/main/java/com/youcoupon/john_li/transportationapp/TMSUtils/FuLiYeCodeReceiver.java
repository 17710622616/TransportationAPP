package com.youcoupon.john_li.transportationapp.TMSUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.youcoupon.john_li.transportationapp.TMSModel.Barcodemode;

/**
 * Created by John_Li on 27/11/2018.
 */

public class FuLiYeCodeReceiver extends BroadcastReceiver {
    private static final String TAG = "MycodeReceiver";
    private static final String BARCODE_ACTION = "com.barcode.sendBroadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
    }

}
