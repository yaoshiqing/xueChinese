package com.gjjy.basiclib.widget.initGuide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.R;
import com.ybear.ybcomponent.Utils;

/**
 发现页 - 新手引导
 */
public class FindInitGuide extends BaseInitGuide {

    public FindInitGuide( @NonNull Context context ) {
        super( context );
    }

    public FindInitGuide( @NonNull Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
    }

    public FindInitGuide( @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
    }

    private ImageView createPlayListGuide() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.END;
        lp.topMargin = Utils.dp2Px( getContext(), 30 );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 6 ) );
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_init_guide_discovery_play_list );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );;
        return iv;
    }

    private ImageView createMoreGuide() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.END;
        lp.topMargin = Utils.dp2Px( getContext(), 80 );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 8 ) );
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_init_guide_discovery_more );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );;
        iv.setVisibility( GONE );
        return iv;
    }

    @NonNull
    @Override
    public View[] onBeginnerViewAll() {
        return new View[] {
                createPlayListGuide(),
                createMoreGuide()
        };
    }
}
