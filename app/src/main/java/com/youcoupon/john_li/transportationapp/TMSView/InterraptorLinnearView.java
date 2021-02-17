package com.youcoupon.john_li.transportationapp.TMSView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.youcoupon.john_li.transportationapp.R;

public class InterraptorLinnearView extends LinearLayout {
    public InterraptorLinnearView(Context context) {
        super(context);
        init();
    }

    public InterraptorLinnearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InterraptorLinnearView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
        return true;
    }
}
