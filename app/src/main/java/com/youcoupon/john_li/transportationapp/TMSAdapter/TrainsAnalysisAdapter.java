package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsAnalysisInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.PostStockMovementModel;

import java.util.List;

/**
 * Created by John_Li on 20/7/2018.
 */

public class TrainsAnalysisAdapter extends BaseAdapter {
        private List<TrainsAnalysisInfo> list;
        private LayoutInflater inflater;
        private Context mContext;

        public TrainsAnalysisAdapter(Context context, List<TrainsAnalysisInfo> photoList) {
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
            TrainsAnalysisAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new TrainsAnalysisAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_trains_analysis, null);
                holder.item_trains_analysis_no = convertView.findViewById(R.id.item_trains_analysis_no);
                holder.item_trains_analysis_name = convertView.findViewById(R.id.item_trains_analysis_name);
                holder.item_trains_analysis_classify = convertView.findViewById(R.id.item_trains_analysis_classify);
                holder.item_trains_analysis_qty = convertView.findViewById(R.id.item_trains_analysis_qty);
                convertView.setTag(holder);
            } else {
                holder = (TrainsAnalysisAdapter.ViewHolder) convertView.getTag();
            }

            holder.item_trains_analysis_no.setText(String.valueOf(list.get(i).getMerchandiseCode()));
            holder.item_trains_analysis_name.setText(String.valueOf(list.get(i).getMerchandiseName()));
            holder.item_trains_analysis_classify.setText(list.get(i).getType());
            holder.item_trains_analysis_qty.setText(String.valueOf(list.get(i).getQty()));
            return convertView;
        }

    class ViewHolder {
        private TextView item_trains_analysis_no;
        private TextView item_trains_analysis_name;
        private TextView item_trains_analysis_classify;
        private TextView item_trains_analysis_qty;
    }

}
