package com.youcoupon.john_li.transportationapp.TMSAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class OrderDetialPageAdapter extends PagerAdapter {
    private List<WebView> mViewList;

    public OrderDetialPageAdapter(List<WebView> mViewList2) {
        this.mViewList = mViewList2;
    }

    public int getCount() {
        return this.mViewList.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView((View) this.mViewList.get(position));
        return this.mViewList.get(position);
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) this.mViewList.get(position));
    }
}
