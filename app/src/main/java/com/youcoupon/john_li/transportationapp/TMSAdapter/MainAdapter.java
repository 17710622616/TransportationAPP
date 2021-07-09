package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;

import java.util.List;

/**
 * Created by John_Li on 20/7/2018.
 */

public class MainAdapter extends BaseAdapter {
        private List<String> list;
        private LayoutInflater inflater;
        private Context mContext;

        public MainAdapter(Context context, List<String> photoList) {
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
            MainAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new MainAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_main_menu, null);
                holder.text = convertView.findViewById(R.id.item_main_tv);
                holder.item_main_fail_tv = convertView.findViewById(R.id.item_main_fail_tv);
                holder.warmingIv = convertView.findViewById(R.id.item_main_warming_iv);
                convertView.setTag(holder);
            } else {
                holder = (MainAdapter.ViewHolder) convertView.getTag();
            }

            holder.text.setText(list.get(i));
            if (list.get(i).equals("數據更新")) {
                if (TMSCommonUtils.getUserFor40(mContext) != null) {
                    if (TMSCommonUtils.getUserFor40(mContext).isInvoiceTbStatus() && TMSCommonUtils.getUserFor40(mContext).isCustomerTbStatus()) {
                        holder.warmingIv.setVisibility(View.GONE);
                    } else {
                        holder.warmingIv.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.warmingIv.setVisibility(View.GONE);
                }
                holder.item_main_fail_tv.setVisibility(View.GONE);
            } else if (list.get(i).equals("業務審核")) {
                int count = TMSCommonUtils.selectFailOrderCount(mContext);
                if (count == 0) {
                    holder.item_main_fail_tv.setVisibility(View.GONE);
                } else {
                    holder.item_main_fail_tv.setVisibility(View.VISIBLE);
                    holder.item_main_fail_tv.setText(String.valueOf(count));
                }
                holder.warmingIv.setVisibility(View.GONE);
            }  else if (list.get(i).equals("物料結算")) {
                int count = TMSCommonUtils.selectFailStockCount(mContext);
                if (count == 0) {
                    holder.item_main_fail_tv.setVisibility(View.GONE);
                } else {
                    holder.item_main_fail_tv.setVisibility(View.VISIBLE);
                    holder.item_main_fail_tv.setText(String.valueOf(count));
                }
                holder.warmingIv.setVisibility(View.GONE);
            } else{
                holder.item_main_fail_tv.setVisibility(View.GONE);
                holder.warmingIv.setVisibility(View.GONE);
            }
            return convertView;
        }

    class ViewHolder {
        private TextView text;
        private TextView item_main_fail_tv;
        private ImageView warmingIv;
    }

}
