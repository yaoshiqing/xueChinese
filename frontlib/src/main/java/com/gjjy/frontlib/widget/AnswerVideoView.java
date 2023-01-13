package com.gjjy.frontlib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.Utils;
import com.gjjy.frontlib.R;
import com.gjjy.basiclib.widget.video.OnVideoInfoListener;
import com.gjjy.basiclib.widget.video.OnVideoStatusListener;
import com.gjjy.basiclib.widget.video.ScreenOrientationChangeListener;
import com.gjjy.basiclib.widget.video.VideoCallback;
import com.gjjy.basiclib.widget.video.VideoScreen;

public class AnswerVideoView extends FrameLayout implements VideoCallback, OnVideoStatusListener,
        OnVideoInfoListener {
    private VideoScreen vsVideo;
    private ImageView ivFirstFrame;
    private LinearLayout llController;
    private TextView tvBuffUpdateView;
    private OnClickListener mDelayOnClickListener;
    private OnClickListener mPlayOnClickListener;

    private RequestManager mReqManage;

    public AnswerVideoView(Context context) {
        this(context, null);
    }

    public AnswerVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mReqManage = Glide.with( this );

        vsVideo = createVideoScreen();
        ivFirstFrame = createFirstFrameView();
        llController = createControllerButton();
        tvBuffUpdateView = createBuffUpdateView();

        FrameLayout.LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dp2Px( getContext(), 50 )
        );
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM;
        llController.setLayoutParams( lp );

        vsVideo.setZOrderOnTop( false );
        vsVideo.setEnableOrientation( false );
        vsVideo.addVideoStatusListener( this );
        vsVideo.addVideoInfoListener( this );

        addView( vsVideo );
        addView( ivFirstFrame );
        addView( llController );
        addView( tvBuffUpdateView );

        setBackgroundColor( Color.BLACK );
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

    private ImageView createFirstFrameView() {
        ImageView iv = new ImageView( getContext() );
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        iv.setLayoutParams( lp );
        iv.setScaleType( ImageView.ScaleType.CENTER_CROP );
        iv.setBackgroundColor( Color.BLACK );
        iv.setVisibility( GONE );
        return iv;
    }

    private LinearLayout createControllerButton() {
        LinearLayout ll = new LinearLayout( getContext() );
        ImageView ivDelayBtn = new ImageView( getContext() );
        ImageView ivPlayBtn = new ImageView( getContext() );
        Space sSpace1 = new Space( getContext() );
        Space sSpace2 = new Space( getContext() );
        Space sSpace3 = new Space( getContext() );

        LinearLayout.LayoutParams lpSpace1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lpSpace1.weight = 1;
        sSpace1.setLayoutParams( lpSpace1 );
        sSpace2.setLayoutParams(new ViewGroup.LayoutParams(
                Utils.dp2Px( getContext(), 19 ),
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        sSpace3.setLayoutParams(new ViewGroup.LayoutParams(
                Utils.dp2Px( getContext(), 12 ),
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ivDelayBtn.setImageResource( R.drawable.ic_video_delay_btn );
        ivPlayBtn.setImageResource( R.drawable.ic_video_play_btn );

        ivDelayBtn.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        ivPlayBtn.setScaleType( ImageView.ScaleType.CENTER_INSIDE );

        ivDelayBtn.setFocusable( true );
        ivDelayBtn.setClickable( true );

        ivPlayBtn.setFocusable( true );
        ivPlayBtn.setClickable( true );

        ivDelayBtn.setOnClickListener(v -> {
            setSpeed( 0.75F );
            play();
            if( mDelayOnClickListener != null ) mDelayOnClickListener.onClick( v );
        });
        ivPlayBtn.setOnClickListener(v -> {
            setSpeed( 1.0F );
            play();
            if( mPlayOnClickListener != null ) mPlayOnClickListener.onClick( v );
        });

        ll.setOrientation( LinearLayout.HORIZONTAL );
        ll.setBackgroundResource( R.color.color3000 );
        ll.setGravity( Gravity.CENTER_VERTICAL );
        ll.addView( sSpace1 );
        ll.addView( ivDelayBtn );
        ll.addView( sSpace2 );
        ll.addView( ivPlayBtn );
        ll.addView( sSpace3 );
        return ll;
    }

    private TextView createBuffUpdateView() {
        TextView tv = new TextView( getContext() );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.CENTER;
        tv.setLayoutParams( lp );
        tv.setText( "0%" );
        tv.setTextSize( 16 );
        tv.setTextColor( Color.WHITE );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tv.setGravity( Gravity.CENTER );
        tv.setVisibility( GONE );
        return tv;
    }

    public void setDelayOnClickListener(OnClickListener l) { mDelayOnClickListener = l; }

    public void setPlayOnClickListener(OnClickListener l) { mPlayOnClickListener = l; }

    @Override
    public void setDataSource(@NonNull String... paths) {
        vsVideo.setDataSource( paths );
    }

    @Override
    public void setDataSource(@NonNull Uri... uris) {
        vsVideo.setDataSource( uris );
    }

    @Override
    public void play() {
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
    public boolean isLandscapeScreen() {
        return vsVideo.isLandscapeScreen();
    }

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
        post(() -> {
            tvBuffUpdateView.setVisibility( VISIBLE );
            llController.setVisibility( GONE );
        } );
    }

    @Override
    public void onVideoPlay() {
        post(() -> {
            AnimUtil.setAlphaAnimator(800,
                    animator -> ivFirstFrame.setVisibility( GONE ),
                    ivFirstFrame
            );

            tvBuffUpdateView.setVisibility( GONE );
        });
    }

    @Override
    public void onVideoPause() {
        post(() -> {
            tvBuffUpdateView.setVisibility( GONE );
            llController.setVisibility( VISIBLE );
        });
    }

    @Override
    public void onVideoStop() {
        post(() -> {
            tvBuffUpdateView.setVisibility( GONE );
            llController.setVisibility( VISIBLE );
        });
    }

    @Override
    public void onVideoReset() {

    }

    @Override
    public void onVideoRelease() {

    }

    @Override
    public void onVideoCompletion(int currentPlayNum, int playTotal, boolean isCompletion) {
        post(() -> llController.setVisibility( VISIBLE ));
    }

    @Override
    public void onBufferingUpdate(int percent) {
        LogUtil.e("onBufferingUpdate -> " + percent + " | " +tvBuffUpdateView.getVisibility());
        if( tvBuffUpdateView.getVisibility() == VISIBLE ) {
            LogUtil.e("onBufferingUpdate -> " + percent);
            post(() -> tvBuffUpdateView.setText( ( percent + "%" ) ));
        }
    }

    @Override
    public boolean onError(int what, int extra) {
        return false;
    }

    @Override
    public void onSource(@NonNull Object data, @NonNull Class<?> dataType) {
        mReqManage.load( data ).into( ivFirstFrame );
        post( () -> ivFirstFrame.setVisibility( VISIBLE ) );
    }

    @Override
    public void onInfo(int progress, int total) {

    }

    @Override
    public void onProgress(int progress) {

    }
}