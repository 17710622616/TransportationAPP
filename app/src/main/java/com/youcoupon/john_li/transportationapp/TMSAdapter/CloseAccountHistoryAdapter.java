package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.SubmitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.PostStockMovementModel;

import java.util.List;

/**
 * Created by John_Li on 20/7/2018.
 */

public class CloseAccountHistoryAdapter extends BaseAdapter {
        private List<TrainsInfo> list;
        private LayoutInflater inflater;
        private Context mContext;

        public CloseAccountHistoryAdapter(Context context, List<TrainsInfo> photoList) {
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
            CloseAccountHistoryAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new CloseAccountHistoryAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_close_account_history_list, null);
                holder.item_close_history_times = convertView.findViewById(R.id.item_close_history_times);
                holder.item_close_history_sendout = convertView.findViewById(R.id.item_close_history_sendout);
                holder.item_close_history_recycle = convertView.findViewById(R.id.item_close_history_recycle);
                holder.item_close_history_deposit_status = convertView.findViewById(R.id.item_close_history_deposit_status);
                holder.item_close_history_refund_status = convertView.findViewById(R.id.item_close_history_refund_status);
                convertView.setTag(holder);
            } else {
                holder = (CloseAccountHistoryAdapter.ViewHolder) convertView.getTag();
            }

            holder.item_close_history_times.setText(String.valueOf(list.get(i).getTrainsTimes()));
            PostStockMovementModel depositBody = new Gson().fromJson(list.get(i).getTodayDepositBody(), PostStockMovementModel.class);
            PostStockMovementModel refundBody = new Gson().fromJson(list.get(i).getTodayRefundBody(), PostStockMovementModel.class);
            int deposit = 0;
            int refund = 0;
            if (depositBody != null) {
                for (PostStockMovementModel.Line info : depositBody.getLines()) {
                    deposit += info.getQuantity();
                }
            }

            if (refundBody != null) {
                for (PostStockMovementModel.Line info : refundBody.getLines()) {
                    refund += info.getQuantity();
                }
            }

            holder.item_close_history_sendout.setText(String.valueOf(deposit));
            holder.item_close_history_recycle.setText(String.valueOf(refund));
            switch (list.get(i).getTodayDepositStatus()) {
                case 0:
                    holder.item_close_history_deposit_status.setText("提交中");
                    holder.item_close_history_deposit_status.setBackgroundResource(R.drawable.shape_in);
                    break;
                case 1:
                    holder.item_close_history_deposit_status.setText("成功");
                    holder.item_close_history_deposit_status.setBackgroundResource(R.drawable.shape_success);
                    break;
                case 2:
                    holder.item_close_history_deposit_status.setText("失敗");
                    holder.item_close_history_deposit_status.setBackgroundResource(R.drawable.shape_fail);
                    break;
            }

            switch (list.get(i).getTodayRefundStatus()) {
                case 0:
                    holder.item_close_history_refund_status.setText("提交中");
                    holder.item_close_history_refund_status.setBackgroundResource(R.drawable.shape_in);
                    break;
                case 1:
                    holder.item_close_history_refund_status.setText("成功");
                    holder.item_close_history_refund_status.setBackgroundResource(R.drawable.shape_success);
                    break;
                case 2:
                    holder.item_close_history_refund_status.setText("失敗");
                    holder.item_close_history_refund_status.setBackgroundResource(R.drawable.shape_fail);
                    break;
            }
            return convertView;
        }

    class ViewHolder {
        private TextView item_close_history_times;
        private TextView item_close_history_sendout;
        private TextView item_close_history_recycle;
        private TextView item_close_history_deposit_status;
        private TextView item_close_history_refund_status;
    }

}
