package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceOutModel;

import java.util.List;

/**
 * Created by John_Li on 20/7/2018.
 */

public class TodayInvoiceListAdapter extends BaseAdapter {
        private List<SubmitInvoiceInfo> list;
        private LayoutInflater inflater;
        private Context mContext;

        public TodayInvoiceListAdapter(Context context, List<SubmitInvoiceInfo> photoList) {
            this.list = photoList;
            inflater = LayoutInflater.from(context);
            mContext = context;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View convertView = view;
            TodayInvoiceListAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new TodayInvoiceListAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_today_invoice_list, null);
                holder.item_today_invoice_name = convertView.findViewById(R.id.item_today_invoice_name);
                holder.item_today_invoice_status = convertView.findViewById(R.id.item_today_invoice_status);
                convertView.setTag(holder);
            } else {
                holder = (TodayInvoiceListAdapter.ViewHolder) convertView.getTag();
            }

            holder.item_today_invoice_name.setText(list.get(i).getCustomerID());
            switch (list.get(i).getStatus()) {
                case 0:
                    holder.item_today_invoice_status.setText("提交中");
                    holder.item_today_invoice_status.setBackgroundResource(R.drawable.shape_in);
                    break;
                case 1:
                    holder.item_today_invoice_status.setText("提交成功");
                    //holder.item_today_invoice_status.setTextColor(mContext.getResources().getColor((R.color.colorSubmitGreen)));
                    holder.item_today_invoice_status.setBackgroundResource(R.drawable.shape_success);
                    break;
                case 2:
                    holder.item_today_invoice_status.setText("提交失敗");
                    //holder.item_today_invoice_status.setTextColor(mContext.getResources().getColor((R.color.color_black)));
                    holder.item_today_invoice_status.setBackgroundResource(R.drawable.shape_fail);
                    break;
            }
            return convertView;
        }

    class ViewHolder {
        private TextView item_today_invoice_name;
        private TextView item_today_invoice_status;
    }

}
