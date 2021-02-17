package com.john.materialdatatest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class PositionTrackActivity extends FragmentActivity {
    MapView mMapView = null;
    AMap aMap;
    TextView backTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_track);//获取地图控件引用
        backTv = findViewById(R.id.pt_head_back);
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMapView = (MapView) findViewById(R.id.pt_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        List<TimingPositionInfo> all = null;
        try {
            all = MaterialDataAPP.db.selector(TimingPositionInfo.class).where("creatTime",">=", CommonUtils.getTimeToday()).findAll();

            if (all != null) {

                List<LatLng> latLngs = new ArrayList<LatLng>();
                for (TimingPositionInfo info : all) {
                    GPS gps = GPSConverterUtils.gps84_To_Gcj02(Double.parseDouble(info.getLantitude()), Double.parseDouble(info.getLongtitude()));
                    latLngs.add(new LatLng(gps.getLat(), gps.getLon()));
                }

                Polyline polyline = aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
