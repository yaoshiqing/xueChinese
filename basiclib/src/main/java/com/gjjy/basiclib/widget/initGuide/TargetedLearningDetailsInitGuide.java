package com.gjjy.basiclib.widget.initGuide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.SysUtil;
import com.gjjy.basiclib.R;

/**
 专项学习 - 详细页面 - 新手引导
 */
public class TargetedLearningDetailsInitGuide extends BaseInitGuide {

    public TargetedLearningDetailsInitGuide(@NonNull Context context ) {
        super( context );
    }

    public TargetedLearningDetailsInitGuide(@NonNull Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
    }

    public TargetedLearningDetailsInitGuide(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
    }

    private ImageView createBgGuide() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                SysUtil.getScreenWidth( getContext() ), SysUtil.getScreenHeight( getContext() )
        );
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_init_guide_targeted_learning_details_bg );
        iv.setScaleType( ImageView.ScaleType.FIT_XY );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );;
//        iv.setAdjustViewBounds( true );
        return iv;
    }

    @Override
    public int onOkButtonOfBottomMargin() {
        return Utils.dp2Px( getContext(), 28 );
    }

    @NonNull
    @Override
    public View [] onBeginnerViewAll() {
        return new View[] { createBgGuide() };
    }
}
