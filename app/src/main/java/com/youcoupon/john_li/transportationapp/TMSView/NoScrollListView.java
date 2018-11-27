package com.youcoupon.john_li.transportationapp.TMSView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by John_Li on 26/10/2017.
 */

public class NoScrollListView extends ListView{
    public NoScrollListView(Context context, AttributeSet attrs){

        super(context, attrs);

    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, mExpandSpec);

    }
}