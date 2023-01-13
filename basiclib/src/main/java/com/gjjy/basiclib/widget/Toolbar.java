package com.gjjy.basiclib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.ToolbarView;
import com.gjjy.basiclib.R;

public class Toolbar extends ToolbarView {
    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTitleTextSize( 16 );
        setTitleColor( R.color.color99 );
        setTitleTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        setBackBtnOfImg( R.drawable.ic_gray_back );
        setBackgroundColor( Color.TRANSPARENT );

        getTitleView().setMaxLines( 1 );
        getTitleView().setMaxEms( 12 );
        getTitleView().setEllipsize( TextUtils.TruncateAt.END );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(
                        Utils.dp2Px( getContext(), 44 ), MeasureSpec.AT_MOST
                )
        );
    }
}
