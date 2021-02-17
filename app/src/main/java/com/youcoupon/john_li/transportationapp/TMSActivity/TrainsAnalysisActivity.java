package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.TrainsAnalysisAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CarSplitInvoiceDetialInfo;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.TrainsAnalysisInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CarSplitInvoiceVM;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import java.util.ArrayList;
import java.util.List;

public class TrainsAnalysisActivity extends BaseActivity implements View.OnClickListener {
    private TMSHeadView headView;
    private RadioGroup mRg;
    //private SmartTable<TrainsAnalysisInfo> mTableView;
    private ListView mLv;

    private List<TrainsAnalysisInfo> tableList;
    private String mTrains = "NOT_DIVIDED";
    private String START_WAY = "GOODS";
    private String mInvoiceListJson;
    private TrainsAnalysisAdapter mTrainsAnalysisAdapter;
    // 實際數據
    private List<CarSplitInvoiceVM> carSplitInvoiceVMList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trains_analysis);
        initView();
        setListener();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                finish();
                break;
        }
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.trains_analysis_hv);
        mRg = findViewById(R.id.trains_analysis_rg);
        //mTableView = findViewById(R.id.trains_analysis_table);
        mLv = findViewById(R.id.trains_analysis_lv);
    }

    @Override
    public void setListener() {
        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getTrainsAnalysisData();
                //mTableView.refreshDrawableState();           //不要忘记刷新表格，否则选中效果会延时一步
                //mTableView.invalidate();
                mTrainsAnalysisAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        // 準備數據
        mTrains = getIntent().getStringExtra("Trains");
        mInvoiceListJson = getIntent().getStringExtra("InvoiceList");

        List<CarSplitInvoiceVM> totalCarSplitInvoiceList = new Gson().fromJson(mInvoiceListJson, new TypeToken<List<CarSplitInvoiceVM>>() {}.getType());
        carSplitInvoiceVMList = filterCarSplitInvoice(totalCarSplitInvoiceList);

        if(mTrains == null){
            mTrains = "NOT_DIVIDED";
        }
        START_WAY = getIntent().getStringExtra("START_WAY");
        if(START_WAY == null){
            START_WAY = "GOODS";
        }

        headView.setLeft(this);

        // 設置標題
        List<String> typeArr = getAllType();
        for (int i = 0; i < typeArr.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            //设置RadioButton边距 (int left, int top, int right, int bottom)
            lp.setMargins(15,10,0,10);
            //设置文字距离四周的距离
            radioButton.setPadding(10, 15, 10, 15);
            //设置文字
            radioButton.setText(typeArr.get(i));
            radioButton.setTextSize(18);
            radioButton.setBackgroundResource(R.drawable.selector_type);
            //将radioButton添加到radioGroup中
            mRg.addView(radioButton);
        }

        tableList = new ArrayList<>();

        // 加載初始車次
        switch (mTrains) {
            case "NOT_DIVIDED":
                headView.setTitle("未車詳情");
                break;
            case "FIRST":
                headView.setTitle("1車次詳情");
                break;
            case "SECOND":
                headView.setTitle("2車次詳情");
                break;
            case "THIRD":
                headView.setTitle("3車次詳情");
                break;
            case "FOURTH":
                headView.setTitle("4車次詳情");
                break;
            case "FIFTH":
                headView.setTitle("5車次詳情");
                break;
            case "NOT_ARRANGE":
                headView.setTitle("未能安排車詳情");
                break;
        }

        /*Column<String> noColumn = new Column<>("編號", "merchandiseCode");
        Column<String> nameColumn = new Column<>("品名", "merchandiseName");
        Column<String> packingColumn = new Column<>("分類", "type");
        Column<String> qtyColumn = new Column<>("數量", "qty");

        TableData<TrainsAnalysisInfo> tableData = new TableData<>("表格名",tableList,noColumn,nameColumn,packingColumn,qtyColumn);*/

        mTrainsAnalysisAdapter = new TrainsAnalysisAdapter(this, tableList);
        mLv.setAdapter(mTrainsAnalysisAdapter);

        switch (START_WAY) {
            case "GOODS":
                mRg.check(mRg.getChildAt(0).getId());
                break;
            case "CUSTOMER":
                mRg.check(mRg.getChildAt(0).getId());
                break;
            case "CANNED":
                mRg.check(mRg.getChildAt(3).getId());
                break;
            case "PLASTIC":
                mRg.check(mRg.getChildAt(5).getId());
                break;
            case "PAPER":
                mRg.check(mRg.getChildAt(4).getId());
                break;
            case "BOTTLE":
                mRg.check(mRg.getChildAt(3).getId());
                break;
        }

        // 是否顯示標題
        /*mTableView.getConfig().setShowTableTitle(false);
        mTableView.setTableData(tableData);// 填充
        mTableView.setZoom(false);//缩放
        mTableView.getConfig().setFixedYSequence(false);//Y序号列
        mTableView.getConfig().setFixedXSequence(false);//X序号列
        mTableView.getConfig().setFixedCountRow(false);//列标题
        mTableView.getConfig().setContentStyle(new FontStyle().setTextSize(30));       //设置表格主题字体样式
        mTableView.getConfig().setColumnTitleStyle(new FontStyle().setTextSize(30));  //设置表格标题字体样式*/
    }

    private List<String> getAllType() {
        List<String> list = new ArrayList<>();
        list.add("全部");
        list.add("未定義");
        list.add("樽裝");
        list.add("罐裝");
        list.add("紙包");
        list.add("膠裝");
        list.add("糖漿");
        list.add("桶裝水");
        list.add("二氧化碳");
        list.add("包裝物");
        list.add("貝達產品");
        list.add("其他");
        return list;
    }

    /**
     * 篩選車次
     * @param totalCarSplitInvoiceList
     * @return
     */
    private List<CarSplitInvoiceVM> filterCarSplitInvoice(List<CarSplitInvoiceVM> totalCarSplitInvoiceList) {
        List<CarSplitInvoiceVM> list = new ArrayList<>();
        for (CarSplitInvoiceVM vm : totalCarSplitInvoiceList) {
            int trunkNo = 0;
            switch (mTrains) {
                case "NOT_DIVIDED":
                    trunkNo = 0;
                    break;
                case "FIRST":
                    trunkNo = 1;
                    break;
                case "SECOND":
                    trunkNo = 2;
                    break;
                case "THIRD":
                    trunkNo = 3;
                    break;
                case "FOURTH":
                    trunkNo = 4;
                    break;
                case "FIFTH":
                    trunkNo = 5;
                    break;
                case "NOT_ARRANGE":
                    trunkNo = 255;
                    break;
            }

            if (vm.getHeader().getTruckNo() == trunkNo) {
                list.add(vm);
            }
        }
        return list;
    }

    /**
     * 轉換數據
     * @return
     */
    private List<TrainsAnalysisInfo> getTrainsAnalysisData() {
        tableList.clear();

        int position = 0;
        for (int i = 0; i < mRg.getChildCount(); i++) {
            RadioButton rb = (RadioButton) mRg.getChildAt(i);
            if (rb.isChecked()) {
                position = i;
                break;
            }
        }

        String type = "99999";
        switch (position) {
            case 0:
                type = "99999";
                break;
            case 1:
                type = "00000";
                break;
            case 2:
                type = "00001";
                break;
            case 3:
                type = "00002";
                break;
            case 4:
                type = "00003";
                break;
            case 5:
                type = "00004";
                break;
            case 6:
                type = "00005";
                break;
            case 7:
                type = "00006";
                break;
            case 8:
                type = "00007";
                break;
            case 9:
                type = "00008";
                break;
            case 10:
                type = "00009";
                break;
            case 11:
                type = "00099";
        }
        for (int i = 0; i < carSplitInvoiceVMList.size(); i++) {
            for (CarSplitInvoiceVM.Line line : carSplitInvoiceVMList.get(i).getLine()) {
                if (type.equals("99999")) {
                    // 全部
                    TrainsAnalysisInfo invoiceInfo = new TrainsAnalysisInfo();
                    invoiceInfo.setMerchandiseCode(line.getMerchandiseID());
                    invoiceInfo.setMerchandiseName(line.getMerchandiseName());
                    invoiceInfo.setQty(String.format("%.0f", Math.ceil(line.getQty()/ line.getPacking())));
                    invoiceInfo.setType(line.getLoadingclassifyName());
                    int index = tableList.indexOf(invoiceInfo);
                    if (index == -1) {
                        tableList.add(invoiceInfo);
                    } else {
                        tableList.get(index).setQty(String.format("%.0f", Math.ceil(Double.parseDouble(tableList.get(index).getQty()) + (Double.parseDouble(invoiceInfo.getQty())))));
                    }
                } else {
                    // 按分類
                    if (type.equals(line.getLoadingClassifyID())) {
                        // 判斷是否存在，存在則疊加數量即可
                        TrainsAnalysisInfo invoiceInfo = new TrainsAnalysisInfo();
                        invoiceInfo.setMerchandiseCode(line.getMerchandiseID());
                        invoiceInfo.setMerchandiseName(line.getMerchandiseName());
                        invoiceInfo.setQty(String.valueOf(Math.ceil(line.getQty()/ line.getPacking())));
                        invoiceInfo.setType(line.getLoadingclassifyName());
                        int index = tableList.indexOf(invoiceInfo);
                        if (index == -1) {
                            tableList.add(invoiceInfo);
                        } else {
                            tableList.get(index).setQty(String.format("%.0f",Math.ceil(Double.parseDouble(tableList.get(index).getQty()) + (Double.parseDouble(invoiceInfo.getQty())))));
                        }
                    }
                }
            }
        }
        return tableList;
    }
}
