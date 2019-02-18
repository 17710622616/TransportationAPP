package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;

import java.util.List;

/**
 * Created by John_Li on 20/7/2018.
 */

public class InvoiceStateAdapter extends BaseAdapter {
    private GridView gv;
        private List<String> list;
        private LayoutInflater inflater;
        private Context mContext;

        public InvoiceStateAdapter(Context context, List<String> photoList) {
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
            InvoiceStateAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new InvoiceStateAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_invoice_state, null);
                holder.text = convertView.findViewById(R.id.item_invoice_state_tv);
                convertView.setTag(holder);
            } else {
                holder = (InvoiceStateAdapter.ViewHolder) convertView.getTag();
            }

            holder.text.setText(list.get(i));
            return convertView;
        }

    class ViewHolder {
        private TextView text;
    }
}
