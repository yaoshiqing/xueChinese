package com.gjjy.basiclib.widget.catx_video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationControlBar;
import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationSlideProgressBar;
import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationTitleBar;
import com.ybear.ybcomponent.Utils;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.widget.video.PlayerControlView;

public class VideoControlView extends PlayerControlView{
    public VideoControlView(@NonNull Context context) {
        this(context, null);
    }

    public VideoControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public PlayerOperationTitleBar onTitleBar() {
        return new PlayerOperationTitleBar( getContext(), R.layout.block_video_control_title_bar )
                .setBackButton( R.id.video_control_title_bar_iv_back_btn );
    }

    @Override
    public PlayerOperationControlBar onControlBar() {
        return new PlayerOperationControlBar( getContext(), R.layout.block_video_control_ctrl_bar )
                .setPlayButtonImageView( R.id.video_control_ctrl_bar_iv_play_btn )
                .setStartTimeTextView( R.id.video_control_ctrl_bar_tv_current_time )
                .setTotalTimeTextView( R.id.video_control_ctrl_bar_tv_total_time )
                .setProgressSeekBar( R.id.video_control_ctrl_bar_sb_seek )
                .setFlipButtonImageView( R.id.video_control_ctrl_bar_iv_multi_role_btn )
                .setPlayButtonRes(
                        R.drawable.ic_video_control_play_btn,
                        R.drawable.ic_video_control_pause_btn
                ).setFlipButtonRes(
                        R.drawable.ic_video_control_full_screen_btn,
                        R.drawable.ic_video_control_full_screen_btn
                );
    }

    @Override
    public PlayerOperationSlideProgressBar onSlideProgressBar() {
        return null;
    }

    public void onOrientationChange(boolean isPortrait) {
        View v = getControlLayout();
        int margin = isPortrait ? 0 : Utils.dp2Px( getContext(), 15 );
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        if( lp == null ) {
            lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        lp.setMarginStart( margin );
        lp.setMarginEnd( margin );
        lp.bottomMargin = isPortrait ? 0 : Utils.dp2Px( getContext(), 6 );
        v.setLayoutParams( lp );

        if( isPortrait ) {
            ((ViewGroup)getTitleLayout()).getChildAt( 0 ).setVisibility( GONE );
        }else {
            ((ViewGroup)getTitleLayout()).getChildAt( 0 ).setVisibility( VISIBLE );
        }
    }
}
