package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class XScrollView extends ScrollView {
    public interface OnScrollChangeListener {
        void onScrollChanged(
                XScrollView xsv, int scrollX, int scrollY,
                int oldScrollX, int oldScrollY
        );
    }

    private OnScrollChangeListener mOnScrollChangeListener;

    public XScrollView(Context context) {
        super(context);
    }

    public XScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if( mOnScrollChangeListener != null ) {
            mOnScrollChangeListener.onScrollChanged( this, l, t, oldl, oldt );
        }
    }

    public void setScrollChangedListener(OnScrollChangeListener l) {
        mOnScrollChangeListener = l;
    }
}
