package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSModel.DeliverInvoiceModel;

import java.util.List;

/**
 * Created by John_Li on 20/7/2018.
 */

public class DeliverGoodsAdapter extends BaseAdapter {
        private List<DeliverInvoiceModel> list;
        private LayoutInflater inflater;
        private Context mContext;

        public DeliverGoodsAdapter(Context context, List<DeliverInvoiceModel> photoList) {
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
            DeliverGoodsAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new DeliverGoodsAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_deliver_googds, null);
                holder.item_deliver_goods_material = convertView.findViewById(R.id.item_deliver_goods_material);
                holder.item_deliver_goods_send_out = convertView.findViewById(R.id.item_deliver_goods_send_out);
                holder.item_deliver_goods_recycle = convertView.findViewById(R.id.item_deliver_goods_recycle);
                convertView.setTag(holder);
            } else {
                holder = (DeliverGoodsAdapter.ViewHolder) convertView.getTag();
            }

            holder.item_deliver_goods_material.setText(list.get(i).getMaterialName());
            holder.item_deliver_goods_send_out.setText(String.valueOf(list.get(i).getSendOutNum()));
            holder.item_deliver_goods_recycle.setText(String.valueOf(list.get(i).getRecycleNum()));
            return convertView;
        }

    class ViewHolder {
        private TextView item_deliver_goods_material;
        private TextView item_deliver_goods_send_out;
        private TextView item_deliver_goods_recycle;
    }

}
