package com.gjjy.basiclib.widget.initGuide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.ybcomponent.Utils;
import com.gjjy.basiclib.R;

/**
 首页 - 新手引导
 */
public class FrontInitGuide extends BaseInitGuide {

    public FrontInitGuide( @NonNull Context context ) {
        super( context );
    }

    public FrontInitGuide( @NonNull Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
    }

    public FrontInitGuide( @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
    }

    private ImageView createDailyLifeGuide() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.topMargin = Utils.dp2Px( getContext(), 30 );
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_init_guide_daily_life );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );;
        return iv;
    }

    private ImageView createRightListGuide(@DrawableRes int res, int top) {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.END;
        lp.topMargin = Utils.dp2Px( getContext(), top );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 6 ) );
        iv.setLayoutParams( lp );
        iv.setImageResource( res );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );;
        return iv;
    }

    @NonNull
    @Override
    public View [] onBeginnerViewAll() {
        return new View[] {
                createDailyLifeGuide(),
                createRightListGuide( R.drawable.ic_init_guide_vip, 84 ),
                createRightListGuide( R.drawable.ic_init_guide_review, 157 )
        };
    }
}
