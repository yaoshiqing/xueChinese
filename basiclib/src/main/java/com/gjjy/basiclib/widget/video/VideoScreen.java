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
            //启动屏幕旋转监听
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

        //视频尺寸尚未初始化时可能会调用updateOrientation()方法，所以获取视频尺寸后更新一次
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
        /* viewpager等控件滑动时不会出现白屏现象 */
        holder.setFormat( PixelFormat.TRANSPARENT );
        //显示在最顶层
        setZOrderOnTop( true );
        /* 允许被点击 */
        setFocusable( true );
        setClickable( true );
    }

    private void initValue() {
        mPlayObjectList.clear();
        //默认播放第一条
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
     * 设置资源
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
     * 设置视频资源
     * @param paths 资源
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
     * 设置视频资源
     * @param uris  资源
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
     * 播放视频
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
            //自旋等待
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
            //回调视频信息
            for ( OnVideoInfoListener l : mOnVideoInfoListener ) {
                l.onInfo( mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration() );
            }
            //回调播放
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
     * 暂停视频
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
            //自旋等待
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
     * 停止播放
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
            //自旋等待
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
     * 上一个视频
     */
    @Override
    public void on() {
        if( mPlayIndex - 1 < 0 ) return;
        mCurrentObject = mPlayObjectList.get( --mPlayIndex );
        setSource( mCurrentObject );
        play();
    }

    /**
     * 下一个视频
     */
    @Override
    public void next() {
        if( mPlayIndex + 1 > mPlayObjectList.size() - 1 ) return;
        mCurrentObject = mPlayObjectList.get( ++mPlayIndex );
        setSource( mCurrentObject );
        play();
    }

    /**
     * 恢复播放。当调用过 {@link #resumePause()} 时，会恢复之前的播放状态。
     * 例如：上次是用户发起暂停，恢复时也会暂停。当Activity发起暂停，恢复时播放。
     * 但一般情况下用不上，因为播放器会在暂停和恢复时自动调用。
     */
    public void resumePlay() {
        if( !isResume ) return;
        isResume = false;
        play();
    }

    /**
     * 配合{@link #resumePlay()}一起使用。
     */
    public void resumePause() {
        if( isResume ) return;
        isResume = true;
        try { Thread.sleep( 24 ); } catch(InterruptedException ignored) { }
        pause();
    }

    /**
     * 重置
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
     * 释放资源
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
     * 滑动视频进度到指定位置
     * @param progress  进度
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
     * 是否循环播放视频，多个视频时从第一个视频开始循环
     * @param enable    是否启用
     */
    @Override
    public void setLooping(boolean enable) {
        isLooping = enable;
    }

    /**
     * 是否在循环播放
     * @return  结果
     */
    @Override
    public boolean isLooping() {
        if( mMediaPlayer == null ) return false;
        return mMediaPlayer.isLooping();
    }

    /**
     * 是否在播放
     * @return  结果
     */
    @Override
    public boolean isPlaying() { return isPlaying; }

    /**
     * 添加视频状态监听器
     * @param l     监听器
     */
    @Override
    public void addVideoStatusListener(OnVideoStatusListener l) {
        mOnVideoStatusListener.add( l );
    }

    /**
     * 移除视频状态监听器
     * @param l     监听器
     */
    @Override
    public void removeVideoStatusListener(OnVideoStatusListener l) {
        mOnVideoStatusListener.remove( l );
    }

    /**
     * 添加设置屏幕方向改变监听
     * @param l     监听器
     */
    @Override
    public void addOrientationChangedListener(ScreenOrientationChangeListener l) {
        mScreenOrientationChangeListener.add( l );
    }

    /**
     * 移除设置屏幕方向改变监听
     * @param l     监听器
     */
    @Override
    public void removeOrientationChangedListener(ScreenOrientationChangeListener l) {
        mScreenOrientationChangeListener.remove( l );
    }

    /**
     * 添加视频信息监听器
     * @param l  监听器
     */
    public void addVideoInfoListener(OnVideoInfoListener l) { mOnVideoInfoListener.add( l ); }

    /**
     * 移除视频信息监听器
     * @param l  监听器
     */
    public void removeVideoInfoListener(OnVideoInfoListener l) { mOnVideoInfoListener.remove( l ); }

    /**
     * 跟随系统旋转
     * @param enable    是否启用
     */
    @Override
    public void setFollowSystemRotation(boolean enable) {
        mScreenOrientationEventHelper.setFollowSystemRotation( enable );
    }

    /**
     * 设置屏幕方向
     * @param orientation   方向
     * @param angle         角度
     */
    public void setOrientationChange(int orientation, int angle) {
        changeOrientation( orientation, angle, angle == 90 || angle == 270 );
    }

    /**
     * 设置音量大小
     * @param lVolume   左声道
     * @param rVolume   右声道
     */
    public void setVolume(float lVolume, float rVolume) {
        if( mMediaPlayer != null ) mMediaPlayer.setVolume( lVolume, rVolume );
    }

    /**
     * 设置音量大小
     * @param volume    音量大小
     */
    public void setVolume(float volume) { setVolume( volume, volume ); }

    /**
     * 当前视频播放进度
     * @return  进度
     */
    public int getCurrentProgress() { return mCurrentProgress; }

    public int getVideoWidth() { return mVideoWidth; }

    public int getVideoHeight() { return mVideoHeight; }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        //屏幕保持常亮
        setKeepScreenOn( true );
//        //传入空数据时取消初始化
//        if( mPaths == null || mPaths.size() == 0 ) return;
        //防止当前方法重复调用
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
            //设置资源
            if( mCurrentObject != null ) setSource( mCurrentObject );
//            if( mPaths.size() > 0 ) setSource( mPaths.get( mPlayIndex ) );

        } catch (Exception e) {
            e.printStackTrace();
            for ( OnVideoStatusListener l : mOnVideoStatusListener ) l.onError( -1, -1 );
        }
        //错误监听器
        mMediaPlayer.setOnErrorListener( this );
        //视频尺寸发生改变
        mMediaPlayer.setOnVideoSizeChangedListener( this );
        //添加完成监听器
        mMediaPlayer.setOnCompletionListener( this );
        //流媒体缓冲进度
        mMediaPlayer.setOnBufferingUpdateListener( this );
        //准备就绪时回调
        mMediaPlayer.setOnPreparedListener(mp -> {
            int prepared = mPrepared.incrementAndGet();
            if( prepared == 1 ) {
                //自旋等待
                preparedSpinWait();
            }

//            //展示第一帧
//            if( mPaths.size() > 0 ) {
//                playSync();
//                pauseSync();
//            }
        });

        if( mTimerProgress == null ) {
            //进度计时器
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
                        //回调视频信息
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
     * mPrepared > 1 时播放和暂停的自旋等待才会停止，而此回调接口有时候只返回一次，这边补齐。
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
     * 屏幕旋转发生改变
     * @param orientation   方向
     * @param angle         角度
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
     * 改变旋转方向
     * @param orientation   方向
     * @param angle         角度
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
        //是否为横版视频
        boolean isLandscapeVideo = mVideoWidth > mVideoHeight;
        //是否为横屏
        isLandscapeView = angle == 0 || angle == 180 || mWidth > mHeight;

        int maxP = Math.max( mWidthOfPortrait, mHeightOfPortrait );
        int minP = Math.min( mWidthOfPortrait, mHeightOfPortrait );
        int maxL = Math.max( mWidthOfLandscape, mHeightOfLandscape );
        int minL = Math.min( mWidthOfLandscape, mHeightOfLandscape );

        int max = isLandscapeScreen() && mEnableFullScreenOfLandscape ? maxL : maxP;
        int min = isLandscapeScreen() && mEnableFullScreenOfLandscape ? minL : minP;

        //-1为平放状态
        if( angle == -1 ) angle = mOldAngle;

        /* 屏幕旋转一共四种结果：竖屏横视频、竖屏竖视频、横屏横视频、横屏竖视频
         * 其次，这是经过一段很长的代码精简而来，请不要尝试去理解这些代码。
         * 大概意思是横竖屏时判断当前视频的横竖，对高宽进行等比缩放 */

        if( (isLandscapeVideo && isLandscapeView) || (!isLandscapeVideo && !isLandscapeView) ) {
            /* 横屏 横视频 和 竖屏 竖视频 */
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
            /* 竖屏 横视频 和 横屏 竖视频 */
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
     * 视频播放完成
     * @param mp    播放器
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        if( mPlayObjectList.size() == 1 ) {
            //是否循环播放
            if( isLooping ) play();
            if( mMediaPlayer != null ) mMediaPlayer.setLooping( isLooping );
        }else {
            //是否循环播放
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
     * 在线视频缓冲时进度
     * @param mp        播放器
     * @param percent   缓冲百分比
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
     * 视频尺寸发生改变。高度宽度为0时表示不可用，-1表示尚未初始化完成
     * @param mp        {@link MediaPlayer}
     * @param width     视频宽度
     * @param height    视频高度
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