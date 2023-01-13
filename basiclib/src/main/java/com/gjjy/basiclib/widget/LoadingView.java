package com.gjjy.basiclib.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

public class LoadingView extends LottieAnimationView {
//    private FrameAnimation.FrameControl mFrameCtrl;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setRepeatCount( 0 );
        setAnimation( "lottie_loading.json" );
        setSpeed( 0.5F );
//        setScaleType( ScaleType.CENTER_CROP );
//        mFrameCtrl = FrameAnimation
//                .create()
//                .time( 60 )
//                .load( getContext(), "ic_loading_", 12 )
//                .into( this );
//        startLoading();
    }

    public void startLoading() { playAnimation(); }

    public void stopLoading() { cancelAnimation(); }

    public void pauseNowLoading() { pauseAnimation(); }
}
//public class LoadingView extends AppCompatImageView {
//    private FrameAnimation.FrameControl mFrameCtrl;
//
//    public LoadingView(Context context) {
//        this(context, null);
//    }
//
//    public LoadingView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    private void init() {
//        setScaleType( ScaleType.CENTER_CROP );
//        mFrameCtrl = FrameAnimation
//                .create()
//                .time( 60 )
//                .load( getContext(), "ic_loading_", 12 )
//                .into( this );
//        startLoading();
//    }
//
//    public void startLoading() { mFrameCtrl.play( getContext() ); }
//
//    public void stopLoading() { mFrameCtrl.stopNow(); }
//
//    public void pauseNowLoading() { mFrameCtrl.pauseNow(); }
//}
