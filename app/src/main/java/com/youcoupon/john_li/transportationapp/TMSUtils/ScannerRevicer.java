package com.youcoupon.john_li.transportationapp.TMSUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.youcoupon.john_li.transportationapp.TMSActivity.InvoiceStateActivity;

/**
 * Created by John_Li on 27/12/2018.
 */

public class ScannerRevicer extends BroadcastReceiver {
    private InvoiceStateActivity invoiceStateActivity;
    public ScannerRevicer(InvoiceStateActivity invoiceStateActivity) {
        this.invoiceStateActivity = invoiceStateActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.barcode.sendBroadcast")) {
            String barcode = intent.getStringExtra("BARCODE");
            invoiceStateActivity.scanCallback(barcode);
        } else {
            Toast.makeText(context.getApplicationContext(), "未找到该發票，请确认后在扫描！", Toast.LENGTH_SHORT).show();
        }
    }
}
