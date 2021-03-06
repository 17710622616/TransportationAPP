package com.youcoupon.john_li.transportationapp.TMSActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youcoupon.john_li.transportationapp.R;
import com.youcoupon.john_li.transportationapp.TMSAdapter.CarSpliteTableAdapter;
import com.youcoupon.john_li.transportationapp.TMSDBInfo.CarSplitInvoiceInfo;
import com.youcoupon.john_li.transportationapp.TMSModel.CarSplitInvoiceVM;
import com.youcoupon.john_li.transportationapp.TMSModel.CommonModel;
import com.youcoupon.john_li.transportationapp.TMSModel.PostTrunkSplitModel;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSCommonUtils;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSConfigor;
import com.youcoupon.john_li.transportationapp.TMSUtils.TMSShareInfo;
import com.youcoupon.john_li.transportationapp.TMSView.InterraptorLinnearView;
import com.youcoupon.john_li.transportationapp.TMSView.TMSHeadView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarSplitActivity1 extends  BaseActivity implements View.OnClickListener, CarSpliteTableAdapter.CheckItemListener, CarSpliteTableAdapter.InfoClickListener {
    private TMSHeadView headView;
    private RecyclerView rv;
    private RadioGroup trainsRg;
    private RadioButton notRb, firstRb, secondRb, thridRb, fourthRb, fifthRb, noArrangeRb;
    private TextView goodsQtyTv, customerQtyTv, cannedTv, plasticTv, paperTv, bottleTv ,moveTv;
    private LinearLayout shrinkLL;
    private ImageView unfoldIv;
    private RelativeLayout analysisRl;
    private ScrollView sv;
    private ProgressDialog mLoadDialog;
    private InterraptorLinnearView mLoadingLL;
    // tableView數據
    private List<CarSplitInvoiceInfo> tableList;
    private CarSpliteTableAdapter mCarSpliteTableAdapter;
    // 實際數據
    private List<CarSplitInvoiceVM> carSplitInvoiceVMList;
    private String mInvoiceListJson;
    //选中后的数据
    private List<CarSplitInvoiceInfo> checkedList;

    private String trains = "NOT_DIVIDED";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_split1);
        initView();
        setListener();
        initData();
    }

    @Override
    public void initView() {
        headView = findViewById(R.id.car_split1_head_view);
        rv = findViewById(R.id.car_split1_rv);
        trainsRg = findViewById(R.id.car_split1_rb);
        notRb = findViewById(R.id.car_split1_not_divided);
        firstRb = findViewById(R.id.car_split1_first);
        secondRb = findViewById(R.id.car_split1_second);
        thridRb = findViewById(R.id.car_split1_thrid);
        fourthRb = findViewById(R.id.car_split1_fourth);
        fifthRb = findViewById(R.id.car_split1_fifth);
        noArrangeRb = findViewById(R.id.car_split1_not_arrange);
        goodsQtyTv = findViewById(R.id.car_split1_goods_qty);
        customerQtyTv = findViewById(R.id.car_split1_customer_qty);
        cannedTv = findViewById(R.id.car_split1_canned_qty);
        plasticTv = findViewById(R.id.car_split1_plastic_qty);
        paperTv = findViewById(R.id.car_split1_paper_qty);
        bottleTv = findViewById(R.id.car_split1_bottle_qty);
        moveTv = findViewById(R.id.car_split1_move);
        sv = findViewById(R.id.car_split1_sv);
        shrinkLL = findViewById(R.id.car_split1_arrow_ll);
        unfoldIv = findViewById(R.id.car_split1_unfold);
        analysisRl = findViewById(R.id.car_split1_analysis_rl);
        mLoadingLL = findViewById(R.id.car_split1_loading);
    }

    @Override
    public void setListener() {
        trainsRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.car_split1_not_divided:
                        /*if(!mLoadDialog.isShowing()) {
                            mLoadDialog.show();
                        }*/
                        mLoadingLL.setVisibility(View.VISIBLE);
                        trains = "NOT_DIVIDED";
                        refreshTable();
                        break;
                    case R.id.car_split1_first:
                        /*if(!mLoadDialog.isShowing()) {
                            mLoadDialog.show();
                        }*/
                        mLoadingLL.setVisibility(View.VISIBLE);
                        trains = "FIRST";
                        refreshTable();
                        break;
                    case R.id.car_split1_second:
                        /*if(!mLoadDialog.isShowing()) {
                            mLoadDialog.show();
                        }*/
                        mLoadingLL.setVisibility(View.VISIBLE);
                        trains = "SECOND";
                        refreshTable();
                        break;
                    case R.id.car_split1_thrid:
                        /*if(!mLoadDialog.isShowing()) {
                            mLoadDialog.show();
                        }*/
                        mLoadingLL.setVisibility(View.VISIBLE);
                        trains = "THIRD";
                        refreshTable();
                        break;
                    case R.id.car_split1_fourth:
                        /*if(!mLoadDialog.isShowing()) {
                            mLoadDialog.show();
                        }*/
                        mLoadingLL.setVisibility(View.VISIBLE);
                        trains = "FOURTH";
                        refreshTable();
                        break;
                    case R.id.car_split1_fifth:
                        /*if(!mLoadDialog.isShowing()) {
                            mLoadDialog.show();
                        }*/
                        mLoadingLL.setVisibility(View.VISIBLE);
                        trains = "FIFTH";
                        refreshTable();
                        break;
                    case R.id.car_split1_not_arrange:
                        /*if(!mLoadDialog.isShowing()) {
                            mLoadDialog.show();
                        }*/
                        mLoadingLL.setVisibility(View.VISIBLE);
                        trains = "NOT_ARRANGE";
                        refreshTable();
                        break;
                }
            }
        });
        goodsQtyTv.setOnClickListener(this);
        customerQtyTv.setOnClickListener(this);
        cannedTv.setOnClickListener(this);
        plasticTv.setOnClickListener(this);
        paperTv.setOnClickListener(this);
        bottleTv.setOnClickListener(this);
        moveTv.setOnClickListener(this);
        shrinkLL.setOnClickListener(this);
        unfoldIv.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mInvoiceListJson = getIntent().getStringExtra("InvoiceList");
        carSplitInvoiceVMList = new Gson().fromJson(mInvoiceListJson, new TypeToken<List<CarSplitInvoiceVM>>() {}.getType());
        Toast.makeText(CarSplitActivity1.this, "" + carSplitInvoiceVMList.size(), Toast.LENGTH_LONG).show();
        trains = getIntent().getStringExtra("Trains");
        if(trains == null){
            trains = "NOT_DIVIDED";
        }

        headView.setTitle("今日分車");
        headView.setLeft(this);
        if (TMSShareInfo.mUserModelList.get(0).getSalesmanID().substring(0,1).equals("D")) {
            headView.setRightText("提交", this);
        }

        tableList = new ArrayList<>();
        checkedList = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        mCarSpliteTableAdapter = new CarSpliteTableAdapter(this, tableList, this, this);
        rv.setAdapter(mCarSpliteTableAdapter);;

        if (!TMSShareInfo.mUserModelList.get(0).getSalesmanID().substring(0,1).equals("D")) {
            moveTv.setVisibility(View.INVISIBLE);
        }

        // 加載初始車次
        switch (trains) {
            case "NOT_DIVIDED":
                notRb.setChecked(true);
                break;
            case "FIRST":
                firstRb.setChecked(true);
                break;
            case "SECOND":
                secondRb.setChecked(true);
                break;
            case "THIRD":
                thridRb.setChecked(true);
                break;
            case "FOURTH":
                fourthRb.setChecked(true);
                break;
            case "FIFTH":
                fifthRb.setChecked(true);
                break;
            case "NOT_ARRANGE":
                noArrangeRb.setChecked(true);
                break;
        }
    }

    private void refreshTable() {
        if (null != tableList) {
            tableList.clear();
        }

        List<CarSplitInvoiceInfo> newList = transToView();
        tableList.addAll(newList);// 填充
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCarSpliteTableAdapter.notifyDataSetChanged();
                mLoadingLL.setVisibility(View.GONE);
            }
        }, 1 * 1000);
    }

    /**
     * 轉換為顯示樣式
     * @return
     */
    private List<CarSplitInvoiceInfo> transToView() {
        List<CarSplitInvoiceInfo> newtableList = new ArrayList<>();
        long goodsQTY = 0;
        List<String> customerIDs = new ArrayList<>();
        long cannedQTY = 0;
        long plasticQTY = 0;
        long paperQTY = 0;
        long bottleQTY = 0;
        int trunkNo = 0;
        switch (trains) {
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

        for (int i = 0; i < carSplitInvoiceVMList.size(); i++) {
            if (trunkNo == carSplitInvoiceVMList.get(i).getHeader().getTruckNo()) {
                long qty = 0;
                for (CarSplitInvoiceVM.Line line : carSplitInvoiceVMList.get(i).getLine()) {
                    qty +=line.getQty() / line.getPacking();
                    goodsQTY += line.getQty() / line.getPacking();
                    switch (line.getLoadingClassifyID()) {
                        case "00002":
                            cannedQTY += line.getQty() / line.getPacking();
                            break;
                        case "00004":
                            plasticQTY += line.getQty()/ line.getPacking();
                            break;
                        case "00003":
                            paperQTY += line.getQty()/ line.getPacking();
                            break;
                        case "00001":
                            bottleQTY += line.getQty()/ line.getPacking();
                            break;
                    }
                }

                CarSplitInvoiceInfo invoiceInfo = new CarSplitInvoiceInfo();
                invoiceInfo.setQty(String.valueOf(qty));
                if (!customerIDs.contains(carSplitInvoiceVMList.get(i).getHeader().getCustomerID())) {
                    customerIDs.add(carSplitInvoiceVMList.get(i).getHeader().getCustomerID());
                }
                if (TMSCommonUtils.isEmptyString(carSplitInvoiceVMList.get(i).getHeader().getCustomerAddress())) {
                    invoiceInfo.setCustomerAddress(carSplitInvoiceVMList.get(i).getHeader().getCustomerAddress());
                } else {
                    invoiceInfo.setCustomerAddress("客戶地址");
                }
                if (TMSCommonUtils.isEmptyString(carSplitInvoiceVMList.get(i).getHeader().getCustomerName())) {
                    invoiceInfo.setCustomerName(carSplitInvoiceVMList.get(i).getHeader().getCustomerName());
                } else {
                    invoiceInfo.setCustomerName("客戶名");
                }
                if (TMSCommonUtils.isEmptyString(carSplitInvoiceVMList.get(i).getHeader().getDistrict())) {
                    invoiceInfo.setDistrict(carSplitInvoiceVMList.get(i).getHeader().getDistrict());
                } else {
                    invoiceInfo.setDistrict("-1");
                }
                invoiceInfo.setInvoiceNo(String.valueOf(carSplitInvoiceVMList.get(i).getHeader().getInvoiceNo()));
                switch (carSplitInvoiceVMList.get(i).getHeader().getDocumentType()) {
                    case 'I':
                        invoiceInfo.setInvoiceType("發票");
                        break;
                    case 'C':
                        invoiceInfo.setInvoiceType("退貨單");
                        break;
                    case 'D':
                        invoiceInfo.setInvoiceType("按金單");
                        break;
                    case 'R':
                        invoiceInfo.setInvoiceType("回樽單");
                        break;
                    default:
                        invoiceInfo.setInvoiceType("發票");
                        break;
                }
                invoiceInfo.setOperation(false);
                newtableList.add(invoiceInfo);
            }
        }

        // 修改底部貨量等數據
        goodsQtyTv.setText("货量：" + String.valueOf(goodsQTY));
        customerQtyTv.setText("客户：" + String.valueOf(customerIDs.size()));
        cannedTv.setText("罐\n" + String.valueOf(cannedQTY));
        plasticTv.setText("膠\n" + String.valueOf(plasticQTY));
        paperTv.setText("紙\n" + String.valueOf(paperQTY));
        bottleTv.setText("樽\n" + String.valueOf(bottleQTY));
        return newtableList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left:
                // 確認退出，並釋放登錄狀態
                doCheckOut();
                break;
            case R.id.head_right_tv:
                // 確認提交，並釋放登錄狀態
                submitCarSplit();
                break;
            case R.id.car_split1_arrow_ll:
                analysisRl.setVisibility(View.GONE);
                unfoldIv.setVisibility(View.VISIBLE);
                break;
            case R.id.car_split1_unfold:
                analysisRl.setVisibility(View.VISIBLE);
                unfoldIv.setVisibility(View.GONE);
                break;
            case R.id.car_split1_goods_qty:
                Intent intent1 = new Intent(CarSplitActivity1.this, TrainsAnalysisActivity.class);
                intent1.putExtra("START_WAY", "GOODS");
                intent1.putExtra("Trains", trains);
                intent1.putExtra("InvoiceList", new Gson().toJson(carSplitInvoiceVMList));
                startActivity(intent1);
                break;
            case R.id.car_split1_customer_qty:
                Intent intent2 = new Intent(CarSplitActivity1.this, TrainsAnalysisActivity.class);
                intent2.putExtra("START_WAY", "CUSTOMER");
                intent2.putExtra("Trains", trains);
                intent2.putExtra("InvoiceList", new Gson().toJson(carSplitInvoiceVMList));
                startActivity(intent2);
                break;
            case R.id.car_split1_canned_qty:
                Intent intent3 = new Intent(CarSplitActivity1.this, TrainsAnalysisActivity.class);
                intent3.putExtra("START_WAY", "CANNED");
                intent3.putExtra("Trains", trains);
                intent3.putExtra("InvoiceList", new Gson().toJson(carSplitInvoiceVMList));
                startActivity(intent3);
                break;
            case R.id.car_split1_plastic_qty:
                Intent intent4 = new Intent(CarSplitActivity1.this, TrainsAnalysisActivity.class);
                intent4.putExtra("START_WAY", "PLASTIC");
                intent4.putExtra("Trains", trains);
                intent4.putExtra("InvoiceList", new Gson().toJson(carSplitInvoiceVMList));
                startActivity(intent4);
                break;
            case R.id.car_split1_paper_qty:
                Intent intent5 = new Intent(CarSplitActivity1.this, TrainsAnalysisActivity.class);
                intent5.putExtra("START_WAY", "PAPER");
                intent5.putExtra("Trains", trains);
                intent5.putExtra("InvoiceList", new Gson().toJson(carSplitInvoiceVMList));
                startActivity(intent5);
                break;
            case R.id.car_split1_bottle_qty:
                Intent intent6 = new Intent(CarSplitActivity1.this, TrainsAnalysisActivity.class);
                intent6.putExtra("START_WAY", "BOTTLE");
                intent6.putExtra("Trains", trains);
                intent6.putExtra("InvoiceList", new Gson().toJson(carSplitInvoiceVMList));
                startActivity(intent6);
                break;
            case R.id.car_split1_move:
                showMoveInvoiceToOtherTrains();
                break;
        }
    }

    /**
     * 提交分車數據
     */
    private void submitCarSplit() {
        mLoadDialog = new ProgressDialog(this);
        mLoadDialog.setTitle("請稍等");
        mLoadDialog.setMessage("正在提交分車數據......");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
        List<PostTrunkSplitModel> trunkSplitModelList = transToPost();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        paramsMap.put("isSD", "true");
        //paramsMap.put("salesmanid", TMSShareInfo.mUserModelList.get(0).getSalesmanID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.POST_TRUNK_SPLIT + TMSCommonUtils.createLinkStringByGet(paramsMap));
        params.setBodyContent(new Gson().toJson(trunkSplitModelList));
        params.setAsJsonContent(true);
        params.setConnectTimeout(10 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel model = new Gson().fromJson(result, CommonModel.class);
                if (model.getCode() == 0) {
                    String str = TMSCommonUtils.decode(model.getData().toString());
                    Toast.makeText(CarSplitActivity1.this, "提交分車成功！" + String.valueOf(model.getMessage()), Toast.LENGTH_LONG).show();
                    mLoadDialog.dismiss();
                    doCheckOut();
                } else {
                    Toast.makeText(CarSplitActivity1.this, "提交分車失敗！" + String.valueOf(model.getMessage()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(CarSplitActivity1.this, "提交分車異常！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (mLoadDialog != null) {
                    if (mLoadDialog.isShowing()) {
                        mLoadDialog.dismiss();
                    }
                }
            }
        });
    }
    /**
     * 登出操作
     */
    private void doCheckOut() {
        mLoadDialog = new ProgressDialog(this);
        mLoadDialog.setTitle("加載");
        mLoadDialog.setMessage("正在退出操作......");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("corp", TMSShareInfo.mUserModelList.get(0).getCorp());
        paramsMap.put("userid", TMSShareInfo.mUserModelList.get(0).getID());
        paramsMap.put("DriverID", TMSShareInfo.mUserModelList.get(0).getDriverID());
        RequestParams params = new RequestParams(TMSConfigor.BASE_URL + TMSConfigor.CHECK_OUT_OCCUPY + TMSCommonUtils.createLinkStringByGet(paramsMap));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CommonModel model = new Gson().fromJson(result, CommonModel.class);
                if (model.getCode() == 0) {
                    if (mLoadDialog != null) {
                        if (mLoadDialog.isShowing()) {
                            mLoadDialog.dismiss();
                        }
                    }
                    finish();
                } else {
                    if (mLoadDialog != null) {
                        if (mLoadDialog.isShowing()) {
                            mLoadDialog.dismiss();
                        }
                    }
                    Toast.makeText(CarSplitActivity1.this,  String.valueOf(model.getMessage()) + "，退出操作失败，请重试！", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadDialog != null) {
                    if (mLoadDialog.isShowing()) {
                        mLoadDialog.dismiss();
                    }
                }
                Toast.makeText(CarSplitActivity1.this, "退出操作異常，请重试或聯繫IT人員！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (mLoadDialog != null) {
                    if (mLoadDialog.isShowing()) {
                        mLoadDialog.dismiss();
                    }
                }
            }
        });
    }

    /**
     * 點擊顯示移動車組彈窗
     */
    private void showMoveInvoiceToOtherTrains() {
        AlertDialog.Builder dialog1 = new AlertDialog.Builder(CarSplitActivity1.this);
        LayoutInflater inflater = LayoutInflater.from(CarSplitActivity1.this);
        View view1 = inflater.inflate(R.layout.dialog_car_split_move_list, null);
        dialog1.setView(view1);//设置使用View
        //设置控件应该用v1.findViewById 否则出错
        final TextView notDivided = view1.findViewById(R.id.dialog_not_divided);
        TextView first = view1.findViewById(R.id.dialog_first);
        TextView second = view1.findViewById(R.id.dialog_second);
        TextView thrid = view1.findViewById(R.id.dialog_thrid);
        final TextView fourth = view1.findViewById(R.id.dialog_fourth);
        final TextView fifth = view1.findViewById(R.id.dialog_fifth);
        final TextView notArrange = view1.findViewById(R.id.dialog_not_arrange);

        final Dialog d = dialog1.create();
        notDivided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveInvoiceToOtherTrains(0);
            }
        });
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveInvoiceToOtherTrains(1);
            }
        });
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveInvoiceToOtherTrains(2);
            }
        });
        thrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveInvoiceToOtherTrains(3);
            }
        });
        fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveInvoiceToOtherTrains(4);
            }
        });
        fifth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveInvoiceToOtherTrains(5);
            }
        });
        notArrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                moveInvoiceToOtherTrains(255);
            }
        });
        d.show();
    }

    /**
     * 移動時選擇移動至車組
     */
    private void moveInvoiceToOtherTrains(int moveTo) {
        mLoadingLL.setVisibility(View.VISIBLE);
        int trunkNo = 0;
        switch (trains) {
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

        if (trunkNo == moveTo) {
            // 不可移動到當前車次
            Toast.makeText(CarSplitActivity1.this, "不可移動至當前車次", Toast.LENGTH_LONG).show();
            mLoadingLL.setVisibility(View.GONE);
        } else {
            // 遍歷界面分車表，修改車次
            for (CarSplitInvoiceInfo info : tableList) {
                if (info.isChecked()) {
                    CarSplitInvoiceVM cacheCarSplitInvoiceVM = new CarSplitInvoiceVM();
                    CarSplitInvoiceVM.Header cacheHeader = new CarSplitInvoiceVM.Header();
                    cacheHeader.setInvoiceNo(Long.parseLong(info.getInvoiceNo()));
                    cacheCarSplitInvoiceVM.setHeader(cacheHeader);
                    int index = carSplitInvoiceVMList.indexOf(cacheCarSplitInvoiceVM);
                    carSplitInvoiceVMList.get(index).getHeader().setTruckNo(moveTo);
                }
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshTable();
                    mLoadingLL.setVisibility(View.GONE);
                }
            }, 1 * 1000);
        }
    }

    /**
     * 轉換model至提交
     * @return
     */
    private List<PostTrunkSplitModel> transToPost() {
        List<PostTrunkSplitModel> postList = new ArrayList<>();
        for (CarSplitInvoiceVM carSplitInvoiceVM : carSplitInvoiceVMList) {
            PostTrunkSplitModel model = new PostTrunkSplitModel();
            model.setTruckNo(carSplitInvoiceVM.getHeader().getTruckNo());
            model.setInvoiceNo(carSplitInvoiceVM.getHeader().getInvoiceNo());
            postList.add(model);
        }
        return postList;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK

                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mLoadDialog!=null) {
            if(mLoadDialog.isShowing()) {
                mLoadDialog.dismiss();
            }
        }
        super.onDestroy();
    }

    @Override
    public void itemChecked(CarSplitInvoiceInfo checkBean, boolean isChecked) {
        //处理Item点击选中回调事件
        if (isChecked) {
            //选中处理
            if (!checkedList.contains(checkBean)) {
                checkedList.add(checkBean);
            }
        } else {
            //未选中处理
            if (checkedList.contains(checkBean)) {
                checkedList.remove(checkBean);
            }
        }
        //判断列表数据是否全部选中
        /*if (tableList.size() == dataArray.size()) {
            tableList.setChecked(true);
        } else {
            tableList.setChecked(false);
        }*/
    }

    @Override
    public void infoClick(int position) {
        Intent intent = new Intent(CarSplitActivity1.this, CarSplitInvoiceDetialActiviy.class);
        CarSplitInvoiceVM vm = getInvoiceData(tableList.get(position).getInvoiceNo());
        if (vm != null) {
            intent.putExtra("InvoiceData", new Gson().toJson(vm));
            startActivity(intent);
        } else {
            Toast.makeText(CarSplitActivity1.this, "查看訂單詳情失敗！", Toast.LENGTH_LONG).show();
        }
    }

    private CarSplitInvoiceVM getInvoiceData(String invoiceNo) {
        for (CarSplitInvoiceVM model : carSplitInvoiceVMList) {
            if (String.valueOf(model.getHeader().getInvoiceNo()).equals(invoiceNo)) {
                return model;
            }
        }

        return null;
    }
}
