package com.gjjy.basiclib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.arch.core.util.Function;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.toast.Build;
import com.ybear.ybutils.utils.toast.ToastManage;
import com.gjjy.basiclib.R;

public class ShareToolbar extends Toolbar {
    private ImageView ivCollectBtn;
    private ImageView ivShareBtn;

    private Function<Boolean, Boolean> mOnCollectBtnClickListener;
    private OnClickListener mOnShareBtnClickListener;

    private Build mCollectToastBuild;
    private boolean isCollect;

    public ShareToolbar(Context context) {
        this(context, null);
    }

    public ShareToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int padding = Utils.dp2Px( getContext(), 16 );
        mCollectToastBuild = new Build();
        mCollectToastBuild.setTextColor( Color.WHITE );
        mCollectToastBuild.setTextSize( 16 );
        mCollectToastBuild.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        mCollectToastBuild.setBackgroundResource( R.color.colorBlack60 );
        mCollectToastBuild.setGravity( Gravity.CENTER );
        mCollectToastBuild.setPadding( padding, padding, padding, padding );
        mCollectToastBuild.setRadius( Utils.dp2Px( getContext(), 5 ) );

        initView();
        initListener();

        showShareBtn( true );
        showCollectBtn( true );
//        showShareBtn( mReqConfigModel.isEnableBuyVipEval() );
    }

    private void initView() {
        ivCollectBtn = createCollectBtn();
        ivShareBtn = createShareBtn();

        addView( ivCollectBtn );
        addView( ivShareBtn );
    }

    private void initListener() {
        ivCollectBtn.setOnClickListener(v -> {
            //回调
            if( mOnCollectBtnClickListener == null ) return;
            boolean isChanged = mOnCollectBtnClickListener.apply( isCollect = !isCollect );
            LogUtil.e("isChanged -> " + isChanged);
            //改变状态
            if( isChanged ) setCollectStatus( isCollect, true );
        });

        ivShareBtn.setOnClickListener(v -> {
            if( mOnShareBtnClickListener == null ) return;
            mOnShareBtnClickListener.onClick( v );
        });
    }

    public void setOnCollectBtnClickListener(Function<Boolean, Boolean> l) {
        mOnCollectBtnClickListener = l;
    }

    public void setOnShareBtnClickListener(OnClickListener l) {
        mOnShareBtnClickListener = l;
    }

    public void setCollectStatus(boolean isCollect, boolean isShowToast) {
        this.isCollect = isCollect;
        ivCollectBtn.setImageResource(
                isCollect ?
                        R.drawable.ic_share_bar_collect_selected_btn :
                        R.drawable.ic_share_bar_collect_unselected_btn
        );

        if( !isShowToast ) return;
        mCollectToastBuild.setCompoundDrawablesPadding( Utils.dp2Px( getContext(), 8 ) );
        mCollectToastBuild.setCompoundDrawablesWithIntrinsicBounds(
                isCollect ? R.drawable.ic_share_bar_collect_selected_btn : 0,
                0,
                0,
                0
        );

        showToast( isCollect ? R.string.stringCollectSuccess : R.string.stringCollectCancel );
    }

    public void showShareResult(boolean isSuccess) {
        mCollectToastBuild.setCompoundDrawablesPadding( 0 );
        mCollectToastBuild.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0 );
        showToast( isSuccess ? R.string.stringShareSuccess : R.string.stringShareFailure );
    }

    public void showCollectBtn(boolean isShow) {
        if( ivShareBtn == null || ivCollectBtn == null ) return;
        LayoutParams lpCollect = (LayoutParams) ivCollectBtn.getLayoutParams();
        if( isShow ) {
            if( ivShareBtn.getVisibility() == VISIBLE ) {
                lpCollect.addRule( RelativeLayout.START_OF, ivShareBtn.getId() );
            }else {
                lpCollect.addRule( RelativeLayout.ALIGN_PARENT_END );
            }
        }
        ivCollectBtn.setVisibility( isShow ? VISIBLE : GONE );
    }

    public void showShareBtn(boolean isShow) {
        if( ivShareBtn == null || ivCollectBtn == null ) return;

        LayoutParams lpShare = (LayoutParams) ivShareBtn.getLayoutParams();
        LayoutParams lpCollect = (LayoutParams) ivCollectBtn.getLayoutParams();
        if( isShow ) {
            lpShare.addRule( RelativeLayout.ALIGN_PARENT_END );
            lpCollect.addRule( RelativeLayout.START_OF, ivShareBtn.getId() );
        }else {
            lpCollect.addRule( RelativeLayout.ALIGN_PARENT_END );
        }
        ivShareBtn.setVisibility( isShow ? VISIBLE : GONE );
    }

    private void showToast(@StringRes int resId) {
        ToastManage.get().showToast(
                getContext(),
                resId,
                mCollectToastBuild
        );
    }

    private ImageView createCollectBtn() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.addRule( RelativeLayout.CENTER_IN_PARENT );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 13 ) );

        iv.setId( View.generateViewId() );
        iv.setLayoutParams( lp );
        iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        iv.setImageResource( R.drawable.ic_share_bar_collect_unselected_btn);
        iv.setFocusable( true );
        iv.setClickable( true );
        return iv;
    }

    private ImageView createShareBtn() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.addRule( RelativeLayout.CENTER_VERTICAL );
        lp.addRule( RelativeLayout.ALIGN_PARENT_END );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 10 ) );

        iv.setId( View.generateViewId() );
        iv.setLayoutParams( lp );
        iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        iv.setImageResource( R.drawable.ic_share_bar_share_btn );
        iv.setFocusable( true );
        iv.setClickable( true );
        return iv;
    }
}
