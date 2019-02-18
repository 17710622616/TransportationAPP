package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSModel.UserModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;

import java.util.List;

/**
 * Created by John_Li on 24/1/2019.
 */

public class LoginListAdapter extends BaseAdapter {
    private List<UserModel> list;
    private LayoutInflater inflater;
    private Context mContext;

    public LoginListAdapter(Context context, List<UserModel> photoList) {
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_login_list, null);
            holder.item_login_list_tv = convertView.findViewById(R.id.item_login_list_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item_login_list_tv.setText(list.get(i).getNameChinese() + "  公司:" + list.get(i).getCorp());
        return convertView;
    }

    class ViewHolder {
        private TextView item_login_list_tv;
    }

}
