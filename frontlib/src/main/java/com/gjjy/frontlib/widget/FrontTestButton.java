package com.gjjy.frontlib.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.gjjy.frontlib.R;
import com.ybear.ybcomponent.Utils;

public class FrontTestButton extends AppCompatTextView {
    private boolean mEnable;

    public FrontTestButton(Context context) {
        this(context, null);
    }

    public FrontTestButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrontTestButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = Utils.dp2Px( getContext(), 150 );
        int height = Utils.dp2Px( getContext(), 150 );
//        super.onMeasure(
//                MeasureSpec.makeMeasureSpec( width, MeasureSpec.AT_MOST ),
//                MeasureSpec.makeMeasureSpec( height, MeasureSpec.AT_MOST )
//        );
        setMeasuredDimension( width, height );
    }

    private void init() {
        setTextColor( getResources().getColor( R.color.colorFrontTestButtonText ) );
        setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        setBackgroundResource( R.drawable.ic_front_list_test_btn );
        setGravity( Gravity.CENTER );
        setFocusable( true );
        setClickable( true );
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(v -> {
            if( l != null ) l.onClick( v );
        });
    }

    public void setScore(int score) {
        if( score < 60 ) {
            setTextSize( 17 );
            setText( R.string.stringFrontTestButtonText );
            return;
        }
        setTextSize( 21 );
        setText( Html.fromHtml(
                "<font color='#FFFFDF'><big>" + score + "</big><small>%</small></font>"
        ));
    }

//    public void setEnableButton(boolean enable, int score) {
//        if( score == -1 ) {
//            setTextSize( 17 );
//            setText( enable ? R.string.stringFrontTestButtonText : R.string.stringEmpty );
//        }else {
//            setTextSize( 21 );
//            setText( Html.fromHtml(
//                    "<font color='#FFFFDF'><big>" + score + "</big><small>%</small></font>"
//            ));
//        }
//
//        setBackgroundResource( enable ?
//                R.drawable.ic_front_list_test_btn :
//                R.drawable.ic_front_list_test_passed_btn
//        );
//    }
//    public void setEnableButton(boolean enable) {
//        setEnableButton( enable, -1 );
//    }
}


//package com.catx.frontlib.widget;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.util.AttributeSet;
//import android.view.Gravity;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.ColorInt;
//import androidx.annotation.Nullable;
//import androidx.annotation.StringRes;
//
//import com.ybear.ybcomponent.Utils;
//import com.catx.frontlib.R;
//
//public class FrontTestButton extends LinearLayout {
//    private boolean mEnable;
//
//    public FrontTestButton(Context context) {
//        this(context, null);
//    }
//
//    public FrontTestButton(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public FrontTestButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = Utils.dp2Px( getContext(), 200 );
//        int height = Utils.dp2Px( getContext(), 62 );
//        super.onMeasure(
//                MeasureSpec.makeMeasureSpec( width, MeasureSpec.AT_MOST ),
//                MeasureSpec.makeMeasureSpec( height, MeasureSpec.AT_MOST )
//        );
//    }
//
//    private ImageView ivImg;
//    private TextView tvText;
//    private void init() {
////        int shadow = Utils.dp2Px( getContext(), 4 );
////        setMinimumWidth( Utils.dp2Px( getContext(), 200 ) );
////        setMinimumHeight( Utils.dp2Px( getContext(), 51 ) );
////        setRadius( Utils.dp2Px( getContext(), 15 ) );
////        setShadowRadius( shadow );
////        setShadowOffsetX( shadow );
////        setShadowOffsetY( shadow );
//
//        setOrientation( HORIZONTAL );
//
//        ivImg = new ImageView( getContext() );
//        tvText = new TextView( getContext() );
//        LayoutParams lpImg = new LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                Utils.dp2Px( getContext(), 31 )
//        );
//        LayoutParams lpText = new LayoutParams(
//                0,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//
//        addView( ivImg );
//        addView( tvText );
//
//        lpImg.gravity = Gravity.CENTER_VERTICAL;
//        lpImg.setMarginStart( Utils.dp2Px( getContext(), 12 ) );
//        ivImg.setLayoutParams( lpImg );
//        ivImg.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
//
//        lpText.weight = 1;
//        lpText.gravity = Gravity.CENTER;
//        lpText.rightMargin = Utils.dp2Px( getContext(), 19 );
//        tvText.setTextSize( 19 );
//        tvText.setTextColor( Color.WHITE );
//        tvText.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
//        tvText.setGravity( Gravity.CENTER );
//        tvText.setLayoutParams( lpText );
//
//        setEnableButton( true );
//    }
//
//    @Override
//    public void setOnClickListener(@Nullable OnClickListener l) {
//        super.setOnClickListener(v -> {
//            if( mEnable && l != null ) l.onClick( v );
//        });
//    }
//
//    public final void setText(@StringRes int res) { tvText.setText( res ); }
//    public final void setText(CharSequence text) { tvText.setText( text ); }
//
//    public void setTextColor(@ColorInt int color) { tvText.setTextColor( color ); }
//
//    public void setEnableButton(boolean enable) {
//        mEnable = enable;
//        setButtonStyle( enable );
//    }
//
//    public void setButtonStyle(boolean enable) {
////        setCompoundDrawablesWithIntrinsicBounds(
////                enable ? R.drawable.ic_front_child_lantern : R.drawable.ic_front_test_complete,
////                0,
////                0,
////                0
////        );
////        setTextColor(
////                getResources().getColor( R.color.colorWhite )
////        );
//        ivImg.setImageResource(
//                enable ? R.drawable.ic_front_child_lantern : R.drawable.ic_front_test_complete
//        );
////        tvText.setTextColor(
////                getResources().getColor( R.color.colorWhite )
////        );
//        setBackgroundResource(
//                enable ? R.drawable.ic_touch_btn_true : R.drawable.ic_touch_btn_false
//        );
//    }
//}
