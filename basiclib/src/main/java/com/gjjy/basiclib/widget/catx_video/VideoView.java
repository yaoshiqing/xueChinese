package com.gjjy.basiclib.widget.catx_video;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationControlBar;
import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationTitleBar;
import com.gjjy.basiclib.widget.video.OnPlayerControlEventListener;
import com.ybear.ybutils.utils.Utils;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.widget.LoadingView;
import com.gjjy.basiclib.widget.video.OnPlayerTouchAdapter;
import com.gjjy.basiclib.widget.video.OnVideoInfoListener;
import com.gjjy.basiclib.widget.video.OnVideoStatusListener;
import com.gjjy.basiclib.widget.video.ScreenOrientationChangeListener;
import com.gjjy.basiclib.widget.video.VideoCallback;
import com.gjjy.basiclib.widget.video.VideoScreen;

/**
 清单文件需配置 android:configChanges="keyboardHidden|orientation|screenSize"
 */
public class VideoView extends FrameLayout implements VideoCallback, OnVideoStatusListener,
        OnVideoInfoListener {
    private VideoScreen vsVideo;
    private VideoControlView vcvControlView;
    private ViewGroup vgSpeed;
    private ViewGroup vgSpeedGroup;
    private View vSelectSpeedDiv;
    private LoadingView lvLoading;

    private ValueAnimator vaSpeedDivAnim;

    private float mCurrentSpeed = 1.0F;
    private int mProgress;
    private int mTotalProgress;

    public VideoView(@NonNull Context context) {
        this(context, null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();
        int h = vsVideo.getMeasuredHeight();

        setMeasuredDimension( w, h );
        ViewGroup.LayoutParams lp = vcvControlView.getLayoutParams();
        lp.width = w;
        lp.height = h;
        vcvControlView.setLayoutParams( lp );
    }

    private void init() {
        setBackgroundColor( Color.BLACK );

        addView( vsVideo = createVideoScreen() );
        addView( vcvControlView = createVideoControlView() );
        addView( vgSpeed = createVideoSpeedView() );
        addView( lvLoading = createLoadingView() );

        vgSpeedGroup = vgSpeed.findViewById( R.id.video_speed_ll_speed_group );
        vSelectSpeedDiv = vgSpeed.findViewById( R.id.video_speed_iv_speed_selected_div );

        initData();
        initListener();

        vcvControlView.onOrientationChange( true );
    }

    private void initData() {
        vaSpeedDivAnim = new ValueAnimator();
        vaSpeedDivAnim.setDuration( 200 );

        vsVideo.setZOrderOnTop( false );
        vsVideo.setEnableOrientation( false );
        vsVideo.addVideoStatusListener( this );
        vsVideo.addVideoInfoListener( this );
        vcvControlView.setEnableSwitchFlipStatus( false );
    }

    private void initListener() {
        vaSpeedDivAnim.addUpdateListener(animation ->
                vSelectSpeedDiv.setTranslationX( (Float) animation.getAnimatedValue() )
        );

        vcvControlView.setOnPlayerControlEventListener(new OnPlayerControlEventListener() {
            @Override
            public void onPlayerClick(ImageView iv) { }

            @Override
            public void onPlayerEvent(ImageView iv) {
//                int currentProgress = vsVideo.getCurrentProgress();
//                int progress = currentProgress >= mTotalProgress ? 0 : currentProgress;
//                seekTo( progress );
//                vcvControlView.setProgress( progress );
                if( mProgress >= mTotalProgress ) vsVideo.seekTo( 0 );
                vsVideo.play();
            }

            @Override
            public void onPauseEvent(ImageView iv) { vsVideo.pause(); }

            @Override
            public void onOnEvent(ImageView iv) { vsVideo.on(); }

            @Override
            public void onNextEvent(ImageView iv) { vsVideo.next(); }

            @Override
            public void onFlipClick(ImageView iv) {
                if( vsVideo.isLandscapeScreen() ) {
                    boolean isShow = vgSpeed.getVisibility() == VISIBLE;
                    //速度
                    vgSpeed.setVisibility( isShow ? GONE : VISIBLE );
                    switchSpeed( mCurrentSpeed );
                    if( isShow ) {
                        vcvControlView.startAutoHide();
                    }else {
                        vcvControlView.cancelAutoHide();
                    }
                }else {
                    //全屏
                    vsVideo.setOrientationChange( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, 0 );
                }
            }

            @Override
            public void onPortraitFlipEvent(ImageView iv) {}

            @Override
            public void onLandscapeFlipEvent(ImageView iv) { }

            @Override
            public void onFinishEvent(ImageView iv) {
                exitLandscapeScreen();
            }
        });

        vcvControlView.setOnPlayerTouchListener(new OnPlayerTouchAdapter() {
            @Override
            public void onClick() {
                super.onClick();
                vgSpeed.setVisibility( GONE );
            }

            @Override
            public void onControlViewDisplay(boolean isShow) {
                super.onControlViewDisplay(isShow);
                if( !isShow ) vgSpeed.setVisibility( GONE );
            }
        });

        vsVideo.addOrientationChangedListener((orientation, angle, isPortrait) -> {
            vcvControlView.changeFlipBtnImageResource(
                    isPortrait ?
                            R.drawable.ic_video_control_full_screen_btn :
                            getSpeedImageResource()
            );
            vcvControlView.onOrientationChange( isPortrait );
        });

        vcvControlView.setOnSeekChangeListener( progress -> vsVideo.seekTo( progress ) );

        for (int i = 0; i < vgSpeedGroup.getChildCount(); i++) {
            vgSpeedGroup.getChildAt( i ).setOnClickListener(v -> {
                Object tag = v.getTag();
                if( tag != null ) switchSpeed( v, Float.parseFloat( tag.toString() ) );
            });
        }
    }

    private int getSpeedImageResource() {
        if( mCurrentSpeed == 0.8F ) return R.drawable.ic_video_control_speed_1_btn;
        if( mCurrentSpeed == 1.25F ) return R.drawable.ic_video_control_speed_3_btn;
        if( mCurrentSpeed == 1.5F ) return R.drawable.ic_video_control_speed_4_btn;
        return R.drawable.ic_video_control_speed_2_btn;
    }

    public void setZOrderOnTop(boolean onTop) {
        if( vsVideo != null ) vsVideo.setZOrderOnTop( onTop );
    }
    public void exitLandscapeScreen() {
        vgSpeed.setVisibility( GONE );
        vsVideo.setOrientationChange( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, 90 );
    }

    public void switchSpeed(float speed) {
        switchSpeed( null, speed );
    }
    private void switchSpeed(View v, float speed) {
        if( v == null ) {
            for (int i = 0; i < vgSpeedGroup.getChildCount(); i++) {
                v = vgSpeedGroup.getChildAt( i );
                Object tag = vgSpeedGroup.getChildAt( i ).getTag();
                if( tag != null && Float.valueOf( tag.toString() ).equals( speed ) ) break;
            }
            if( v == null ) return;
        }

        View findView = v;
        //设置速度
        setSpeed( mCurrentSpeed = speed );

        //移动动画
        post(() -> {
            float toX = findView.getX()
                    + ( findView.getWidth() / 2F )
                    - ( vSelectSpeedDiv.getWidth() / 2F );

            vaSpeedDivAnim.setFloatValues( vSelectSpeedDiv.getTranslationX(), toX );
            vaSpeedDivAnim.start();

            vSelectSpeedDiv.setTranslationX( toX );

            vcvControlView.changeFlipBtnImageResource( getSpeedImageResource() );
        });
    }

    public PlayerOperationTitleBar getTitleBar() {
        return vcvControlView.onTitleBar();
    }

    public PlayerOperationControlBar getControlBar() {
        return vcvControlView.onControlBar();
    }

    public VideoControlView getControlView() { return vcvControlView; }

    public int getCurrentProgress() {
        return mProgress;
    }

    private VideoScreen createVideoScreen() {
        VideoScreen vs = new VideoScreen( getContext() );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        lp.gravity = Gravity.CENTER;
        vs.setLayoutParams( lp );
        return vs;
    }

    private VideoControlView createVideoControlView() {
        VideoControlView vcv = new VideoControlView( getContext() );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        lp.gravity = Gravity.CENTER;
        vcv.setLayoutParams( lp );
        vcv.setVisibility( INVISIBLE );
        return vcv;
    }

    private ViewGroup createVideoSpeedView() {
        ViewGroup vg = (ViewGroup) View.inflate( getContext(), R.layout.block_video_speed, null );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                Utils.dp2Px( getContext(), 646 ),
                Utils.dp2Px( getContext(), 50 )
        );
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp.bottomMargin = Utils.dp2Px( getContext(), 70 );
        vg.setLayoutParams( lp );
        vg.setVisibility( GONE );
        return vg;
    }

    private LoadingView createLoadingView() {
        int size = Utils.dp2Px( getContext(), 90 );
        LoadingView lv = new LoadingView( getContext() );
        LayoutParams lp = new LayoutParams( size, size );
        lp.gravity = Gravity.CENTER;
        lv.setLayoutParams( lp );
        lv.setVisibility( GONE );
        return lv;
    }

    private void startLoading() {
        post(() -> {
            lvLoading.setVisibility( VISIBLE );
            lvLoading.startLoading();
        });
    }

    private void stopLoading() {
        post(() -> {
            lvLoading.setVisibility( GONE );
            lvLoading.stopLoading();
        });
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    @Override
    public void setDataSource(@NonNull String... paths) {
        vsVideo.setDataSource( paths );
        startLoading();
    }

    @Override
    public void setDataSource(@NonNull Uri... uris) {
        vsVideo.setDataSource( uris );
        startLoading();
    }

    @Override
    public void play() {
//        post(() -> {
//            tvBuffUpdateView.setVisibility( VISIBLE );
//            llController.setVisibility( GONE );
//        });
        vsVideo.play();
    }

    @Override
    public void on() { vsVideo.on(); }

    @Override
    public void next() { vsVideo.next(); }

    @Override
    public void pause() {
        vsVideo.pause();
    }

    @Override
    public void stop() {
        vsVideo.stop();
    }

    @Override
    public void reset() {
        vsVideo.reset();
    }

    @Override
    public void release() {
        vsVideo.release();
    }

    @Override
    public void setSpeed(float speed) {
        vsVideo.setSpeed( speed );
    }

    @Override
    public void seekTo(int progress) {
        vsVideo.seekTo( progress );
    }

    @Override
    public void setLooping(boolean enable) {
        vsVideo.setLooping( enable );
    }

    @Override
    public boolean isLooping() {
        return vsVideo.isLooping();
    }

    @Override
    public boolean isPlaying() {
        return vsVideo.isPlaying();
    }

    @Override
    public boolean isLandscapeView() { return vsVideo.isLandscapeView(); }

    @Override
    public boolean isLandscapeScreen() { return vsVideo.isLandscapeScreen(); }

    @Override
    public void setEnableOrientation(boolean enable) {
        vsVideo.setEnableOrientation( enable );
    }

    @Override
    public void addVideoStatusListener(OnVideoStatusListener l) {
        vsVideo.addVideoStatusListener( l );
    }

    @Override
    public void removeVideoStatusListener(OnVideoStatusListener l) {
        vsVideo.removeVideoStatusListener( l );
    }

    @Override
    public void addOrientationChangedListener(ScreenOrientationChangeListener l) {
        vsVideo.addOrientationChangedListener( l );
    }

    @Override
    public void removeOrientationChangedListener(ScreenOrientationChangeListener l) {
        vsVideo.removeOrientationChangedListener( l );
    }

    @Override
    public void addVideoInfoListener(OnVideoInfoListener l) {
        vsVideo.addVideoInfoListener( l );
    }

    @Override
    public void removeVideoInfoListener(OnVideoInfoListener l) {
        vsVideo.removeVideoInfoListener( l );
    }

    @Override
    public void setFollowSystemRotation(boolean enable) {
        vsVideo.setFollowSystemRotation( enable );
    }

    @Override
    public VideoScreen setScreenSizeOfPortrait(int width, int height) {
        return vsVideo.setScreenSizeOfPortrait( width, height );
    }

    @Override
    public VideoScreen setScreenSizeOfLandscape(int width, int height) {
        return vsVideo.setScreenSizeOfLandscape( width, height );
    }

    @Override
    public VideoScreen setEnableFullScreenOfLandscape(boolean enable) {
        return vsVideo.setEnableFullScreenOfLandscape( enable );
    }

    @Override
    public void onVideoReady() {
        vsVideo.play();
        if( mProgress > 0 ) vsVideo.seekTo( mProgress == mTotalProgress ? 0 : mProgress );
        vcvControlView.startAutoHide();
        stopLoading();
    }

    @Override
    public void onVideoPlay() {
        post(() -> vcvControlView.switchPlayStatus( true, false ) );
    }

    @Override
    public void onVideoPause() {
        post(() -> vcvControlView.switchPlayStatus( false, false ) );
    }

    @Override
    public void onVideoStop() {
        post(() -> vcvControlView.switchPlayStatus( true, false ) );
    }

    @Override
    public void onVideoReset() { }

    @Override
    public void onVideoRelease() { }

    @Override
    public void onVideoCompletion(int currentPlayNum, int playTotal, boolean isCompletion) {
        post(() -> {
            if( isCompletion ) {
                seekTo( mProgress = mTotalProgress );
                vcvControlView.setSeekTime( 0, mTotalProgress );
                vcvControlView.setProgress( mProgress );
                vcvControlView.switchPlayStatus( false, false );
            }
            if( isLandscapeScreen() ) exitLandscapeScreen();
        });
    }

    @Override
    public void onBufferingUpdate(int percent) { }

    @Override
    public boolean onError(int what, int extra) {
        post(() -> vcvControlView.switchPlayStatus( false, false ) );
        return false;
    }

    @Override
    public void onSource(@NonNull Object data, @NonNull Class<?> dataType) { }

    @Override
    public void onInfo(int progress, int total) {
        if( mTotalProgress <= 0 ) mTotalProgress = total;
        vcvControlView.setSeekTime( progress, total );
        post(() -> vcvControlView.setVisibility( VISIBLE ));
    }

    @Override
    public void onProgress(int progress) {
        mProgress = progress;
        post(() -> vcvControlView.setProgress( progress ));
    }
}
