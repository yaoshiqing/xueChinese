package com.gjjy.basiclib.widget.initGuide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.SysUtil;
import com.gjjy.basiclib.R;

public abstract class BaseInitGuide extends FrameLayout implements IInitGuide {
    private View[] vViews;
    private ImageView ivOkBtn;

    private int mBeginnerIndex;
    private Consumer<View> mOnFinishListener;

    public BaseInitGuide( @NonNull Context context ) {
        this( context, null );
    }

    public BaseInitGuide( @NonNull Context context, @Nullable AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public BaseInitGuide( @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        initView();
        initListener();
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        setMeasuredDimension(
                SysUtil.getScreenWidth( getContext() ),
                SysUtil.getScreenHeight( getContext() )
        );
    }

    @Override
    public int onOkButtonOfBottomMargin() {
        return Utils.dp2Px( getContext(), 120 );
    }

    private void initView() {
        mBeginnerIndex = 0;
        vViews = onBeginnerViewAll();
        for( int i = 0; i < vViews.length; i++ ) {
            View v = vViews[ i ];
            v.setVisibility( i == 0 ? VISIBLE : GONE );
            addView( v );
        }
        addView( ivOkBtn = createOkBtn() );
    }

    private void initListener() {
        ivOkBtn.setOnClickListener(v -> {
            if( mBeginnerIndex >= vViews.length - 1 ) {
                if( mOnFinishListener != null ) mOnFinishListener.accept( v );
                return;
            }
            vViews[ mBeginnerIndex ].setVisibility( GONE );
            vViews[ ++mBeginnerIndex ].setVisibility( VISIBLE );
        });
    }

    public void setOnFinishListener(Consumer<View> call) {
        mOnFinishListener = call;
    }

    private ImageView createOkBtn() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, Utils.dp2Px( getContext(), 48 )
        );
        lp.bottomMargin = onOkButtonOfBottomMargin();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_init_guide_ok_btn );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );
        iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        iv.setFocusable( true );
        iv.setClickable( true );
        return iv;
    }

}
