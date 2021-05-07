package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.google.gson.Gson;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CarSplitInvoiceDetialInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CarSplitInvoiceVM;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CarSplitInvoiceDetialActiviy extends BaseActivity implements View.OnClickListener {
    private TMSHeadView headView;
    private SmartTable<CarSplitInvoiceDetialInfo> tableView;

    private String invoiceNo;
    private CarSplitInvoiceVM mCarSplitInvoiceVM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_split_invoice_detial);
        initView();
        setListener();
        initData();
        TMSCommonUtils.checkTimeByUrl(this);
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.cs_detial_head);
        tableView = findViewById(R.id.cs_order_detial_table);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        mCarSplitInvoiceVM = new Gson().fromJson(getIntent().getStringExtra("InvoiceData"), CarSplitInvoiceVM.class);

        headView.setLeft(this);
        headView.setTitle(mCarSplitInvoiceVM.getHeader().getInvoiceNo() + "," + mCarSplitInvoiceVM.getHeader().getCustomerName() + ",發票詳情");

        Column<String> noColumn = new Column<>("編號", "merchandiseCode");
        Column<String> nameColumn = new Column<>("品名", "merchandiseName");
        Column<String> packingColumn = new Column<>("規格", "packing");
        Column<String> uintPriceColumn = new Column<>("單價", "unitPrice");
        Column<String> qtyColumn = new Column<>("數量", "qty");
        Column<String> giveColumn = new Column<>("贈送", "give");
        Column<String> totalColumn = new Column<>("總價", "totalAmount");

        List<CarSplitInvoiceDetialInfo> list =  trans2TableData();
        TableData<CarSplitInvoiceDetialInfo> tableData = new TableData<>("表格名",list,noColumn,nameColumn,packingColumn,uintPriceColumn,qtyColumn,giveColumn,totalColumn);

        // 是否顯示標題
        tableView.getConfig().setShowTableTitle(false);
        tableView.setTableData(tableData);// 填充
        tableView.setZoom(true);//缩放
        tableView.getConfig().setFixedYSequence(false);//Y序号列
        tableView.getConfig().setFixedXSequence(false);//X序号列
        tableView.getConfig().setFixedCountRow(false);//列标题
        tableView.getConfig().setContentStyle(new FontStyle().setTextSize(30));       //设置表格主题字体样式
        tableView.getConfig().setColumnTitleStyle(new FontStyle().setTextSize(30));   //设置表格标题字体样式
    }

    private List<CarSplitInvoiceDetialInfo> trans2TableData() {
        List<CarSplitInvoiceDetialInfo> resultList = new ArrayList<>();
        List<CarSplitInvoiceVM.Line> newList = TMSCommonUtils.checkLoginList(mCarSplitInvoiceVM.getLine());
        for (CarSplitInvoiceVM.Line line : newList) {
            CarSplitInvoiceDetialInfo info = new CarSplitInvoiceDetialInfo();
            info.setMerchandiseCode(line.getMerchandiseID());
            info.setInvoiceNo(String.valueOf(mCarSplitInvoiceVM.getHeader().getInvoiceNo()));
            info.setUnitPrice(String.valueOf(line.getPrice()));
            info.setPacking(line.getPackageName());
            info.setMerchandiseName(line.getMerchandiseName());
            resultList.add(info);
        }
 
        for (CarSplitInvoiceDetialInfo info : resultList) {
            for (CarSplitInvoiceVM.Line line : mCarSplitInvoiceVM.getLine()) {
                if (info.getMerchandiseCode().equals(line.getMerchandiseID())) {
                    if (info.getQty() != null) {
                        info.setQty(String.valueOf(Integer.parseInt(info.getQty()) + (line.getQty() / line.getPacking())));
                    } else {
                        info.setQty(String.valueOf(line.getQty() / line.getPacking()));
                    }
                }
            }
        }

        return resultList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
        }
    }

    /**
     * 從數據庫中拉取發票信息
     * @return
     */
    private List<CarSplitInvoiceDetialInfo> getInvocieDetialData() {
        List<CarSplitInvoiceDetialInfo> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            CarSplitInvoiceDetialInfo invoiceInfo = new CarSplitInvoiceDetialInfo();
            invoiceInfo.setGive(String.valueOf(i));
            invoiceInfo.setMerchandiseCode("code" + i);
            invoiceInfo.setMerchandiseName("商品名稱" + i);
            invoiceInfo.setPacking(i + "x24x330ml");
            invoiceInfo.setQty(String.valueOf(i));
            invoiceInfo.setTotalAmount(String.valueOf(i * 99));
            invoiceInfo.setUnitPrice(String.valueOf(99));
            list.add(invoiceInfo);
        }
        return list;
    }
}
