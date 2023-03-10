package com.gjjy.basiclib.widget.video;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.Utils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class VideoScreen extends SurfaceView implements SurfaceHolder.Callback, VideoCallback,
        ScreenOrientationChangeListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnVideoSizeChangedListener {

    private final ExecutorService mPool = Executors.newFixedThreadPool( 1 );
    private final List<Object> mPlayObjectList = new ArrayList<>();

    @Nullable
    private MediaPlayer mMediaPlayer;
    private Timer mTimerProgress;
    private int mPlayIndex;
    private int mCurrentProgress;
    private int mVideoWidth, mVideoHeight;
    private int mOldAngle;
    private int mWidth, mHeight;
    private boolean isEnableOrientation = true;
    private Object mCurrentObject;
    private final AtomicInteger mPrepared = new AtomicInteger( 0 );
    private boolean isLooping;
    private boolean isPlaying;
    private boolean isResume = true;
    private boolean isLandscapeView;
    private boolean mEnableFullScreenOfLandscape = true;

    private Thread mPlayThread;
    private Thread mPauseThread;
    private Thread mStopThread;
    private Thread mPreparedThread;

    private long mPlayOutTimeMillis;
    private long mPauseOutTimeMillis;
    private long mStopOutTimeMillis;

    private ScreenOrientationEventHelper mScreenOrientationEventHelper;

    private final List<ScreenOrientationChangeListener> mScreenOrientationChangeListener = new ArrayList<>();
    private final List<OnVideoStatusListener> mOnVideoStatusListener = new ArrayList<>();
    private final List<OnVideoInfoListener> mOnVideoInfoListener = new ArrayList<>();

    public VideoScreen(Context context) {
        this(context, null);
    }
    public VideoScreen(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public VideoScreen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mWidthOfPortrait = ViewGroup.LayoutParams.MATCH_PARENT;
    private int mHeightOfPortrait = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mWidthOfLandscape = ViewGroup.LayoutParams.MATCH_PARENT;
    private int mHeightOfLandscape = ViewGroup.LayoutParams.MATCH_PARENT;

    @Override
    public VideoScreen setScreenSizeOfPortrait(int width, int height) {
        mWidthOfPortrait = width;
        mHeightOfPortrait = height;
        requestLayout();
        return this;
    }

    @Override
    public VideoScreen setScreenSizeOfLandscape(int width, int height) {
        mWidthOfLandscape = width;
        mHeightOfLandscape = height;
        requestLayout();
        return this;
    }

    @Override
    public VideoScreen setEnableFullScreenOfLandscape(boolean enable) {
        mEnableFullScreenOfLandscape = enable;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if( mWidth <= 0 || mHeight <= 0 ) {
            //????????????????????????
            if( isEnableOrientation ) mScreenOrientationEventHelper.enable();
        }

        if( mWidth == 0 ) mWidth = getMeasuredWidth();
        if( mHeight == 0 ) mHeight = getMeasuredHeight();

        if( isLandscapeView ) {
            if( mWidthOfLandscape < 0 ) {
                mWidthOfLandscape = mEnableFullScreenOfLandscape ?
                    SysUtil.getScreenTrueWidth( getContext() ) :
                        mWidth;
            }
            if( mHeightOfLandscape < 0 ) {
            mHeightOfLandscape = mEnableFullScreenOfLandscape ?
                    SysUtil.getScreenTrueHeight( getContext() ) :
                    mHeight;
            }
        }else {
            if( mWidthOfPortrait < 0 ) mWidthOfPortrait = mWidth;
            if( mHeightOfPortrait < 0 ) mHeightOfPortrait = mHeight;
        }
//        if( mWidthOfPortrait < 0 ) mWidthOfPortrait = getMeasuredWidth();
//        if( mHeightOfPortrait < 0 ) mHeightOfPortrait = getMeasuredHeight();
//
//        if( mWidthOfLandscape < 0 ) {
//            mWidthOfLandscape = mEnableFullScreenOfLandscape ?
//                    SysUtil.getScreenTrueWidth( getContext() ) :
//                    getMeasuredWidth();
//        }
//        if( mHeightOfLandscape < 0 ) {
//            mHeightOfLandscape = mEnableFullScreenOfLandscape ?
//                    SysUtil.getScreenTrueHeight( getContext() ) :
//                    getMeasuredHeight();
//        }
//        mWidth = getMeasuredWidth();
//        mHeight = getMeasuredHeight();

        //?????????????????????????????????????????????updateOrientation()????????????????????????????????????????????????
        updateOrientation( mScreenOrientationEventHelper.getOrientationAngle() );

        LogUtil.e( "VideoScreen -> onMeasure -> " +
                "mEnableFullScreenOfLandscape:" + mEnableFullScreenOfLandscape + " | " +
                "isLandscapeView -> " + isLandscapeView + " | " +
                "w:" + mWidth + " | " +
                "h:" + mHeight
        );
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        initValue();
        if( mScreenOrientationEventHelper == null ) {
            mScreenOrientationEventHelper = new ScreenOrientationEventHelper( getContext() );
            mScreenOrientationEventHelper.setScreenOrientationChangeListener( this );
        }
        holder.addCallback( this );
        /* viewpager?????????????????????????????????????????? */
        holder.setFormat( PixelFormat.TRANSPARENT );
        //??????????????????
        setZOrderOnTop( true );
        /* ??????????????? */
        setFocusable( true );
        setClickable( true );
    }

    private void initValue() {
        mPlayObjectList.clear();
        //?????????????????????
        mPlayIndex = 0;
        mCurrentProgress = 0;
        mVideoWidth = -1;
        mVideoHeight = -1;
        mOldAngle = 90;
        mWidth = 0;
        mHeight = 0;
        isLooping = false;
        isPlaying = false;
        isResume = false;
        isEnableOrientation = true;
        mPrepared.set( 0 );
        mPlayThread = null;
        mPauseThread = null;

        mScreenOrientationChangeListener.clear();
        mOnVideoStatusListener.clear();
        mOnVideoInfoListener.clear();
    }

    @Override
    public void setEnableOrientation(boolean enable) {
        isEnableOrientation = enable;
    }

    @Override
    public boolean isLandscapeView() { return isLandscapeView; }

    public boolean isLandscapeScreen() { return mScreenOrientationEventHelper.isLandscapeScreen(); }

    /**
     * ????????????
     */
    private void setSource(@NonNull Object obj) {
        if( mMediaPlayer == null ) return;
//        stop();
        reset();
        try {
            int sdkInt = Build.VERSION.SDK_INT;
            if( obj instanceof String ) {
                //String
                mMediaPlayer.setDataSource( (String)obj );
            }else if( obj instanceof FileDescriptor ) {
                //FileDescriptor
                mMediaPlayer.setDataSource( (FileDescriptor)obj );
            }else if( sdkInt >= Build.VERSION_CODES.N && obj instanceof AssetFileDescriptor ) {
                //AssetFileDescriptor
                mMediaPlayer.setDataSource( (AssetFileDescriptor)obj );
            }else if( obj instanceof Uri ) {
                //Uri
                mMediaPlayer.setDataSource( getContext(), (Uri)obj );
            }else if( sdkInt >= Build.VERSION_CODES.M && obj instanceof MediaDataSource ) {
                //MediaDataSource
                mMediaPlayer.setDataSource( (MediaDataSource)obj );
            }

//            mMediaPlayer.prepare();
            mMediaPlayer.prepareAsync();

            for ( OnVideoInfoListener l : mOnVideoInfoListener ) {
                l.onSource( obj, obj.getClass() );
            }
            LogUtil.e( "VideoScreen -> setSource -> objType:" + obj.getClass() );
//            play();
        } catch (IllegalStateException | IOException ignored) {}
    }

    /**
     * ??????????????????
     * @param paths ??????
     */
    @Override
    public void setDataSource(@NonNull String... paths) {
//        if( paths.length == 0 ) return;
//        Uri[] uris = new Uri[ paths.length ];
//        for (int i = 0; i < paths.length; i++) {
//            uris[ i ] = Uri.parse( paths[ i ] );
//        }
//        setDataSource( uris );

        if( paths.length == 0 ) return;
        mPlayObjectList.addAll( Arrays.asList( paths ) );
        if( mPlayIndex < 0 || mPlayIndex >= mPlayObjectList.size() ) return;
        mCurrentObject = mPlayObjectList.get( mPlayIndex );
        if( mMediaPlayer != null ) setSource( mCurrentObject );
    }

    /**
     * ??????????????????
     * @param uris  ??????
     */
    @Override
    public void setDataSource(@NonNull Uri... uris) {
//        if( uris.length == 0 ) return;
//        mPaths.addAll( Arrays.asList( uris ) );
//        mCurrentPath = mPaths.get( mPlayIndex );
//        if( mMediaPlayer != null ) setSource( mCurrentPath );
    }

    private boolean isPrepared() { return mPrepared.get() > 1; }

    /**
     * ????????????
     */
    @Override
    public void play() {
        if( isPlaying() ) return;
        LogUtil.e("VideoScreen -> Play -> " +
                "isPrepared:" + isPrepared() + " | " +
                "isPlaying:" + isPlaying()
        );

        if( isPrepared() ) {
            playSync();
            return;
        }
        if( mPlayThread != null ) return;
        mPlayThread = new Thread(() -> {
            mPlayOutTimeMillis = System.currentTimeMillis();
            //????????????
            while ( true ) {
                if( isOutTime( mPlayOutTimeMillis ) ) {
                    LogUtil.e("VideoScreen -> PlayOutTime");
                    break;
                }
                if( !isPrepared() ) continue;
                play();
                mPlayThread = null;
                mPlayOutTimeMillis = 0;
                break;
            }
        });
        mPlayThread.start();
    }

    private void playSync() {
        if( mMediaPlayer == null || isPlaying ) return;
        try {
            changedSpeed();
            isPlaying = true;
            mMediaPlayer.start();
            //??????????????????
            for ( OnVideoInfoListener l : mOnVideoInfoListener ) {
                l.onInfo( mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration() );
            }
            //????????????
            for ( OnVideoStatusListener l : mOnVideoStatusListener ) {
                l.onVideoPlay();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void play(long delayed) {
        if( delayed <= 0 ) {
            play();
        }else {
            postDelayed(this::play, delayed);
        }
    }

    /**
     * ????????????
     */
    @Override
    public void pause() {
        LogUtil.e("VideoScreen -> Pause -> " +
                "isPrepared:" + isPrepared() + " | " +
                "isPlaying:" + isPlaying()
        );

        if( isPrepared() && isPlaying() ) {
            pauseSync();
            return;
        }
        if( mPauseThread != null ) return;
        mPauseThread = new Thread(() -> {
            mPauseOutTimeMillis = System.currentTimeMillis();
            //????????????
            while ( true ) {
                if( isOutTime( mPauseOutTimeMillis ) ) {
                    LogUtil.e("VideoScreen -> PauseOutTime");
                    break;
                }
                if( !isPrepared() || !isPlaying() ) continue;
                pause();
                mPauseThread = null;
                mPauseOutTimeMillis = 0;
                break;
            }
        });
        mPauseThread.start();
    }

    private void pauseSync() {
        if( mMediaPlayer == null || !isPlaying ) return;
        try {
            mMediaPlayer.pause();
            isPlaying = false;
            for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onVideoPause();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     */
    @Override
    public void stop() {
        LogUtil.e("VideoScreen -> Stop -> " +
                "isPlaying:" + isPlaying()
        );

        if( isPrepared() && isPlaying() ) {
            stopSync();
            return;
        }
        if( mStopThread != null ) return;
        mStopThread = new Thread(() -> {
            mStopOutTimeMillis = System.currentTimeMillis();
            //????????????
            while ( true ) {
                if( isOutTime( mStopOutTimeMillis ) ) {
                    LogUtil.e("VideoScreen -> StopOutTime");
                    break;
                }
                if( !isPrepared() || !isPlaying() ) continue;
                stop();
                mStopThread = null;
                mStopOutTimeMillis = 0;
                break;
            }
        });
        mStopThread.start();
    }

    private void stopSync() {
        if( mMediaPlayer == null ) return;
        try {
            if( isPlaying() ) mMediaPlayer.stop();
            isPlaying = false;
            for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onVideoStop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????
     */
    @Override
    public void on() {
        if( mPlayIndex - 1 < 0 ) return;
        mCurrentObject = mPlayObjectList.get( --mPlayIndex );
        setSource( mCurrentObject );
        play();
    }

    /**
     * ???????????????
     */
    @Override
    public void next() {
        if( mPlayIndex + 1 > mPlayObjectList.size() - 1 ) return;
        mCurrentObject = mPlayObjectList.get( ++mPlayIndex );
        setSource( mCurrentObject );
        play();
    }

    /**
     * ??????????????????????????? {@link #resumePause()} ???????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????Activity?????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????
     */
    public void resumePlay() {
        if( !isResume ) return;
        isResume = false;
        play();
    }

    /**
     * ??????{@link #resumePlay()}???????????????
     */
    public void resumePause() {
        if( isResume ) return;
        isResume = true;
        try { Thread.sleep( 24 ); } catch(InterruptedException ignored) { }
        pause();
    }

    /**
     * ??????
     */
    @Override
    public void reset() {
        if( mMediaPlayer == null ) return;
        try {
            mMediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onVideoReset();
//        initValue();
    }

    /**
     * ????????????
     */
    @Override
    public void release() {
        stop();
        if( mTimerProgress != null ) {
            mTimerProgress.cancel();
            mTimerProgress = null;
        }
        if( mScreenOrientationEventHelper != null ) {
            mScreenOrientationEventHelper.disable();
            mTimerProgress = null;
        }
        if( mMediaPlayer != null ) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        Canvas canvas = getHolder().lockCanvas();
        if( canvas != null ) {
            canvas.drawColor( Color.BLACK );
            getHolder().unlockCanvasAndPost( canvas );
        }
        for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onVideoRelease();
        initValue();
    }

    private float mSpeed = 1.0F;
    public void setSpeed(float speed) {
        mSpeed = speed;
        changedSpeed();
    }

    /**
     * ?????????????????????????????????
     * @param progress  ??????
     */
    @Override
    public void seekTo(int progress) {
        if( mMediaPlayer == null ) return;
        try {
            mMediaPlayer.seekTo( progress );
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     * @param enable    ????????????
     */
    @Override
    public void setLooping(boolean enable) {
        isLooping = enable;
    }

    /**
     * ?????????????????????
     * @return  ??????
     */
    @Override
    public boolean isLooping() {
        if( mMediaPlayer == null ) return false;
        return mMediaPlayer.isLooping();
    }

    /**
     * ???????????????
     * @return  ??????
     */
    @Override
    public boolean isPlaying() { return isPlaying; }

    /**
     * ???????????????????????????
     * @param l     ?????????
     */
    @Override
    public void addVideoStatusListener(OnVideoStatusListener l) {
        mOnVideoStatusListener.add( l );
    }

    /**
     * ???????????????????????????
     * @param l     ?????????
     */
    @Override
    public void removeVideoStatusListener(OnVideoStatusListener l) {
        mOnVideoStatusListener.remove( l );
    }

    /**
     * ????????????????????????????????????
     * @param l     ?????????
     */
    @Override
    public void addOrientationChangedListener(ScreenOrientationChangeListener l) {
        mScreenOrientationChangeListener.add( l );
    }

    /**
     * ????????????????????????????????????
     * @param l     ?????????
     */
    @Override
    public void removeOrientationChangedListener(ScreenOrientationChangeListener l) {
        mScreenOrientationChangeListener.remove( l );
    }

    /**
     * ???????????????????????????
     * @param l  ?????????
     */
    public void addVideoInfoListener(OnVideoInfoListener l) { mOnVideoInfoListener.add( l ); }

    /**
     * ???????????????????????????
     * @param l  ?????????
     */
    public void removeVideoInfoListener(OnVideoInfoListener l) { mOnVideoInfoListener.remove( l ); }

    /**
     * ??????????????????
     * @param enable    ????????????
     */
    @Override
    public void setFollowSystemRotation(boolean enable) {
        mScreenOrientationEventHelper.setFollowSystemRotation( enable );
    }

    /**
     * ??????????????????
     * @param orientation   ??????
     * @param angle         ??????
     */
    public void setOrientationChange(int orientation, int angle) {
        changeOrientation( orientation, angle, angle == 90 || angle == 270 );
    }

    /**
     * ??????????????????
     * @param lVolume   ?????????
     * @param rVolume   ?????????
     */
    public void setVolume(float lVolume, float rVolume) {
        if( mMediaPlayer != null ) mMediaPlayer.setVolume( lVolume, rVolume );
    }

    /**
     * ??????????????????
     * @param volume    ????????????
     */
    public void setVolume(float volume) { setVolume( volume, volume ); }

    /**
     * ????????????????????????
     * @return  ??????
     */
    public int getCurrentProgress() { return mCurrentProgress; }

    public int getVideoWidth() { return mVideoWidth; }

    public int getVideoHeight() { return mVideoHeight; }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        //??????????????????
        setKeepScreenOn( true );
//        //?????????????????????????????????
//        if( mPaths == null || mPaths.size() == 0 ) return;
        //??????????????????????????????
        if( mMediaPlayer != null ) {
            mMediaPlayer.setDisplay( holder );
            resumePlay();
            resumePause();
            return;
        }
        try {
            mMediaPlayer = new MediaPlayer();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaPlayer.setAudioAttributes( new AudioAttributes.Builder().build() );
            }
            mMediaPlayer.setDisplay( holder );
            //????????????
            if( mCurrentObject != null ) setSource( mCurrentObject );
//            if( mPaths.size() > 0 ) setSource( mPaths.get( mPlayIndex ) );

        } catch (Exception e) {
            e.printStackTrace();
            for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onError( -1, -1 );
        }
        //???????????????
        mMediaPlayer.setOnErrorListener( this );
        //????????????????????????
        mMediaPlayer.setOnVideoSizeChangedListener( this );
        //?????????????????????
        mMediaPlayer.setOnCompletionListener( this );
        //?????????????????????
        mMediaPlayer.setOnBufferingUpdateListener( this );
        //?????????????????????
        mMediaPlayer.setOnPreparedListener(mp -> {
            int prepared = mPrepared.incrementAndGet();
            if( prepared == 1 ) {
                //????????????
                preparedSpinWait();
            }

//            //???????????????
//            if( mPaths.size() > 0 ) {
//                playSync();
//                pauseSync();
//            }
        });

        if( mTimerProgress == null ) {
            //???????????????
            mTimerProgress = new Timer();
            mTimerProgress.schedule(new TimerTask() {
                @Override
                public void run() {
                    if( !isPlaying() ) return;
                    try {
                        mCurrentProgress = mMediaPlayer.getCurrentPosition();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //??????????????????
                        for ( OnVideoInfoListener l : mOnVideoInfoListener ) {
                            l.onProgress( mCurrentProgress );
                        }
                    }
                }
            }, 0, 1000);
        }

//        updateOrientation( mScreenOrientationEventHelper.getOrientation() );
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//        stop();
//        mPrepared = 0;
        resumePause();
//        stop();
        setKeepScreenOn( false );
    }

    /**
     * mPrepared > 1 ????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void preparedSpinWait() {
        if( mPreparedThread != null || mPrepared.get() >= 2 ) return;
        mPreparedThread = new Thread(() -> {
            postDelayed(() -> {
                if( mPrepared.get() < 2 ) mPrepared.incrementAndGet();
                if( mPrepared.get() >= 2 ) {
                    for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onVideoReady();
                }

                LogUtil.d("mPrepared: " + mPrepared + " | " +
                        "isPlaying: " + isPlaying + " | " +
                        "Media isPlaying: " + ( mMediaPlayer != null && mMediaPlayer.isPlaying() )
                );
            }, 1000);
            mPreparedThread = null;
        });
        mPreparedThread.start();
    }

    /**
     * ????????????????????????
     * @param orientation   ??????
     * @param angle         ??????
     */
    @Override
    public void onOrientationChange(int orientation, int angle, boolean isPortrait) {
        mPool.execute(() -> {
            changeOrientation( orientation, angle, isPortrait );
            try {
                Thread.sleep( 1500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * ??????????????????
     * @param orientation   ??????
     * @param angle         ??????
     */
    private void changeOrientation(int orientation, int angle, boolean isPortrait) {
        if( mWidth == 0 || mHeight == 0 ) return;
        post(() -> {
            updateOrientation( angle );
            for ( ScreenOrientationChangeListener l : mScreenOrientationChangeListener ) {
                l.onOrientationChange( orientation, angle, isPortrait );
            }
            LogUtil.d("changeOrientation -> ori:" + orientation + " | angle:" + angle);
        });
    }

    private void updateOrientation(int angle) {
        if( mMediaPlayer == null || mVideoWidth <= 0 || mVideoHeight <= 0 ) return;

        ViewGroup.LayoutParams lp = getLayoutParams();
        //?????????????????????
        boolean isLandscapeVideo = mVideoWidth > mVideoHeight;
        //???????????????
        isLandscapeView = angle == 0 || angle == 180 || mWidth > mHeight;

        int maxP = Math.max( mWidthOfPortrait, mHeightOfPortrait );
        int minP = Math.min( mWidthOfPortrait, mHeightOfPortrait );
        int maxL = Math.max( mWidthOfLandscape, mHeightOfLandscape );
        int minL = Math.min( mWidthOfLandscape, mHeightOfLandscape );

        int max = isLandscapeScreen() && mEnableFullScreenOfLandscape ? maxL : maxP;
        int min = isLandscapeScreen() && mEnableFullScreenOfLandscape ? minL : minP;

        //-1???????????????
        if( angle == -1 ) angle = mOldAngle;

        /* ??????????????????????????????????????????????????????????????????????????????????????????????????????
         * ????????????????????????????????????????????????????????????????????????????????????????????????
         * ???????????????????????????????????????????????????????????????????????????????????? */

        if( (isLandscapeVideo && isLandscapeView) || (!isLandscapeVideo && !isLandscapeView) ) {
            /* ?????? ????????? ??? ?????? ????????? */
            lp.width = Utils.calcAspectRatio(
                    mVideoWidth, mVideoHeight, isLandscapeView ? min : max, false
            );
            if( lp.width > ( isLandscapeView ? max : min ) ) {
                lp.height = Utils.calcAspectRatio(
                        mVideoWidth,
                        mVideoHeight,
                        isLandscapeView ? max : min,
                        true
                );
                lp.width = isLandscapeView ? max : min;
            }else {
                lp.height = isLandscapeView ? min : max;
            }
        }else {
            /* ?????? ????????? ??? ?????? ????????? */
            int w = mVideoHeight;
            int h = mVideoWidth;
            lp.width = min + Utils.calcAspectRatio( w, h, min, isLandscapeView );
            lp.height = isLandscapeView ? min : lp.width - min;
            lp.width = isLandscapeView ? lp.width - min : min;
        }

        setMeasuredDimension( lp.width, lp.height );
        if( angle != -1 ) mOldAngle = angle;

        LogUtil.e( "updateOrientation -> " +
                "w:" + lp.width + " | " +
                "h:" + lp.height + " | " +
                "mVideoWidth:" + mVideoWidth + " | " +
                "mVideoHeight:" + mVideoHeight
        );
    }

    private void changedSpeed() {
        if( mMediaPlayer == null ) return;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if( mMediaPlayer.isPlaying() ) mMediaPlayer.pause();
            try {
                mMediaPlayer.setPlaybackParams(
                        mMediaPlayer.getPlaybackParams().setSpeed( mSpeed )
                );
            } catch (Exception ignored) {}
        }
    }

    /**
     * ??????????????????
     * @param mp    ?????????
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        if( mPlayObjectList.size() == 1 ) {
            //??????????????????
            if( isLooping ) play();
            if( mMediaPlayer != null ) mMediaPlayer.setLooping( isLooping );
        }else {
            //??????????????????
            if( isLooping && mPlayIndex >= mPlayObjectList.size() ) mPlayIndex = 0;
            if( ++mPlayIndex < mPlayObjectList.size() ) {
                setSource( mPlayObjectList.get( mPlayIndex ) );
                play();
            }
//            else {
////                release();
//            }
        }

        for ( OnVideoStatusListener l : mOnVideoStatusListener ) {
            boolean isCompletion = mPlayIndex + 1 == mPlayObjectList.size();
            l.onVideoCompletion( mPlayIndex, mPlayObjectList.size(), isCompletion );
        }
    }

    /**
     * ???????????????????????????
     * @param mp        ?????????
     * @param percent   ???????????????
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onBufferingUpdate( percent );
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onError( what, extra );
        return false;
    }

    /**
     * ??????????????????????????????????????????0?????????????????????-1???????????????????????????
     * @param mp        {@link MediaPlayer}
     * @param width     ????????????
     * @param height    ????????????
     */
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if( width <= 0 && height <= 0 ) {
            for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onError( -1, -1 );
        }
        requestLayout();
    }

    private boolean isOutTime(long millis) { return System.currentTimeMillis() - millis >= 3000L; }
}