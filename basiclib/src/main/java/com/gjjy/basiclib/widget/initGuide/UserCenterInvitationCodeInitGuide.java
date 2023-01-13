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
 个人中心 - 填写邀请码 - 新手引导
 */
public class UserCenterInvitationCodeInitGuide extends BaseInitGuide {

    public UserCenterInvitationCodeInitGuide(@NonNull Context context ) {
        super( context );
    }

    public UserCenterInvitationCodeInitGuide(@NonNull Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
    }

    public UserCenterInvitationCodeInitGuide(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
    }

    private ImageView createBgGuide(int bgRes) {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                SysUtil.getScreenWidth( getContext() ), SysUtil.getScreenHeight( getContext() )
        );
        iv.setLayoutParams( lp );
        iv.setImageResource( bgRes );
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
        return new View[] {
                createBgGuide( R.drawable.ic_init_guide_user_center_invitation_code_1_bg ),
                createBgGuide( R.drawable.ic_init_guide_user_center_invitation_code_2_bg )
        };
    }
}
