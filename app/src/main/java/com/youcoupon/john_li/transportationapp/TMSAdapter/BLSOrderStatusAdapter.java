package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSModel.ClockInOrderStatusModel;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;

import java.util.List;

/**
 * Created by John_Li on 20/5/2019.
 */

public class BLSOrderStatusAdapter extends BaseAdapter{
    private List<ClockInOrderStatusModel> list;
    private LayoutInflater inflater;
    private Context mContext;

    public BLSOrderStatusAdapter(Context context, List<ClockInOrderStatusModel> photoList) {
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
        BLSOrderStatusAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new BLSOrderStatusAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item_login_list, null);
            holder.item_login_list_tv = convertView.findViewById(R.id.item_login_list_tv);
            convertView.setTag(holder);
        } else {
            holder = (BLSOrderStatusAdapter.ViewHolder) convertView.getTag();
        }

        holder.item_login_list_tv.setText(list.get(i).getCustomerName());
        if (list.get(i).isClockIn()) {
            holder.item_login_list_tv.setTextColor(Color.rgb(37,198,252));
        } else {
            holder.item_login_list_tv.setTextColor(Color.BLACK);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView item_login_list_tv;
    }

    public void refreshData(List<ClockInOrderStatusModel> photoList) {
        this.list = photoList;
        notifyDataSetChanged();
    }
}
