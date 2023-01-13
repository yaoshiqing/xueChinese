package com.gjjy.frontlib.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.gjjy.frontlib.R;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.ResUtil;
import com.ybear.ybutils.utils.Utils;

public class AnswerComboView extends LinearLayout {
    private final ValueAnimator mLayoutScaleAnim = ValueAnimator
            .ofFloat( 0.8F, 1.2F, 1.0F )
            .setDuration( 200 );
    private final ValueAnimator mLayoutAlphaAnim = ValueAnimator
            .ofFloat( 1.0F, 0.0F )
            .setDuration( 800 );

    private ImageView ivComboNum1, ivComboNum2;

    public AnswerComboView(Context context) {
        this( context, null );
    }

    public AnswerComboView(Context context, @Nullable AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public AnswerComboView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );
        init();
    }

    private void init() {
        ImageView ivComboText = new ImageView( getContext() );
        ImageView ivComboX = new ImageView( getContext() );
        ivComboNum1 = new ImageView( getContext() );
        ivComboNum2 = new ImageView( getContext() );

        ivComboText.setImageResource( R.drawable.ic_answer_combo_text );
        ivComboX.setImageResource( R.drawable.ic_answer_combo_x );

        setOrientation( HORIZONTAL );
        setGravity( Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM );
        setVisibility( INVISIBLE );
        addView( ivComboText );
        addView( ivComboX );
        addView( ivComboNum1 );
        addView( ivComboNum2 );

        ivComboText.setLayoutParams( createLayoutParams( 3 ) );
        ivComboX.setLayoutParams( createLayoutParams( 2 ) );
        ivComboNum1.setLayoutParams( createLayoutParams( 0 ) );
        ivComboNum2.setLayoutParams( createLayoutParams( 0 ) );
        ivComboNum2.setTranslationX( -Utils.dp2Px( getContext(), 5 ) );

        mLayoutScaleAnim.addUpdateListener( animation -> {
            float val = ObjUtils.parseFloat( animation.getAnimatedValue() );
            setScaleX( val );
            setScaleY( val );
            if( val == 1.0F ) mLayoutAlphaAnim.start();
        });
        mLayoutAlphaAnim.addUpdateListener( animation -> {
            float val = ObjUtils.parseFloat( animation.getAnimatedValue() );
            if( val == 1.0F ) setVisibility( VISIBLE );
            setAlpha( val );
            if( val == 0.0F ) setVisibility( INVISIBLE );
        });
    }

    public void startCombo(int num) {
        if( num <= 0 || num >= 100 ) return;
        setComboNumView( num );
        setVisibility( VISIBLE );
        setAlpha( 1.0F );
        mLayoutScaleAnim.start();
    }

    private void setComboNumView(int num) {
        String strCount = ObjUtils.parseString( num );
        if( TextUtils.isEmpty( strCount ) ) return;
        char[] chrCount = strCount.toCharArray();
        if( chrCount.length == 0 ) return;
        ivComboNum1.setImageDrawable( getNumDrawable( chrCount[ 0 ] ) );
        if( chrCount.length > 1 ) {
            ivComboNum2.setImageDrawable( getNumDrawable( chrCount[ 1 ] ) );
        }
    }

    private Drawable getNumDrawable(char chr) {
        return ResUtil.getDrawable( getContext(), "ic_answer_combo_" + ( chr - '0' ) );
    }

    private LayoutParams createLayoutParams(int marginEnd) {
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if( marginEnd != 0 ) lp.setMarginEnd( Utils.dp2Px( getContext(), marginEnd ) );
        return lp;
    }
}
