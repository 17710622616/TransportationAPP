package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSActivity.CarSplitActivity;
import com.youcoupon.john_li.transportationapp.TMSActivity.CarSplitInvoiceDetialActiviy;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CarSplitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CarSplitInvoiceVM;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;

import java.util.List;

public class CarSpliteTableAdapter extends RecyclerView.Adapter<CarSpliteTableAdapter.ViewHolder> {
    private Context context;
    private List<CarSplitInvoiceInfo> list;
    private LayoutInflater mInflater;
    private CheckItemListener mCheckListener;
    private InfoClickListener mInfoClickListener;

    public CarSpliteTableAdapter(Context context, List<CarSplitInvoiceInfo> list, CheckItemListener mCheckListener,InfoClickListener mInfoClickListener) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
        this.mCheckListener = mCheckListener;
        this.mInfoClickListener = mInfoClickListener;
    }

    @NonNull
    @Override
    public CarSpliteTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_car_split1, parent, false);
        CarSpliteTableAdapter.ViewHolder vh = new CarSpliteTableAdapter.ViewHolder(view);
        vh.invoiceLL = view.findViewById(R.id.item_invoice_ll);
        vh.invoiceCb = view.findViewById(R.id.item_invoice_cb);
        vh.invoiceNoTv = view.findViewById(R.id.item_invoice_no);
        vh.invoiceTypeTv = view.findViewById(R.id.item_invoice_type);
        vh.invoiceQTYTv = view.findViewById(R.id.item_invoice_qty);
        vh.invoiceCNameTv = view.findViewById(R.id.item_invoice_cname);
        vh.invoiceCAddressTv = view.findViewById(R.id.item_invoice_caddress);
        vh.invoicedistrictTv = view.findViewById(R.id.item_invoice_district);
        //view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CarSpliteTableAdapter.ViewHolder holder, int position) {
        holder.invoiceNoTv.setText(list.get(position).getInvoiceNo());
        holder.invoiceTypeTv.setText(list.get(position).getInvoiceType());
        holder.invoiceQTYTv.setText(list.get(position).getQty());
        holder.invoiceCNameTv.setText(list.get(position).getCustomerName());
        holder.invoiceCAddressTv.setText(list.get(position).getCustomerAddress());
        holder.invoicedistrictTv.setText(list.get(position).getDistrict());
        holder.invoiceCb.setChecked(list.get(position).isChecked());
        if (!TMSShareInfo.mUserModelList.get(0).getSalesmanID().substring(0,1).equals("D")) {
            holder.invoiceCb.setVisibility(View.INVISIBLE);
        }
        //点击实现选择功能，当然可以把点击事件放在item_cb对应的CheckBox上，只是焦点范围较小
        holder.invoiceNoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(position).setChecked(!list.get(position).isChecked());
                holder.invoiceCb.setChecked(list.get(position).isChecked());
                if (null != mCheckListener) {
                    mCheckListener.itemChecked(list.get(position), holder.invoiceCb.isChecked());
                }
                notifyDataSetChanged();
            }
        });
        holder.invoiceTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(position).setChecked(!list.get(position).isChecked());
                holder.invoiceCb.setChecked(list.get(position).isChecked());
                if (null != mCheckListener) {
                    mCheckListener.itemChecked(list.get(position), holder.invoiceCb.isChecked());
                }
                notifyDataSetChanged();
            }
        });
        holder.invoiceQTYTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(position).setChecked(!list.get(position).isChecked());
                holder.invoiceCb.setChecked(list.get(position).isChecked());
                if (null != mCheckListener) {
                    mCheckListener.itemChecked(list.get(position), holder.invoiceCb.isChecked());
                }
                notifyDataSetChanged();
            }
        });
        holder.invoiceLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfoClickListener.infoClick(position);
            }
        });
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface CheckItemListener {
        void itemChecked(CarSplitInvoiceInfo checkBean, boolean isChecked);
    }

    public interface InfoClickListener {
        void infoClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout invoiceLL;
        public CheckBox invoiceCb;
        public TextView invoiceNoTv;
        public TextView invoiceTypeTv;
        public TextView invoiceQTYTv;
        public TextView invoiceCNameTv;
        public TextView invoiceCAddressTv;
        public TextView invoicedistrictTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}