package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.gjjy.frontlib.R;
import com.ybear.ybmediax.media.MediaXStatusAdapter;
import com.ybear.ybmediax.media.MediaXC;
import com.gjjy.speechsdk.synthesizer.SpeechSynthesizer;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.FrameAnimation;
import com.ybear.ybutils.utils.LogUtil;

public class AudioPlayButton extends AppCompatImageView {
//    private SpeechSynthesizer mTts;
//    private MediaX mMediaX;
    private final MediaXC mMediaXC = MediaXC.get();
    private FrameAnimation.FrameControl mFrameCtrl;

    private int mMediaXCTag;
    private String mText;
    private boolean isUrl;

    private SpeechSynthesizer.OnSpeechStatusListener mOnSpeechStatusListener;

    public AudioPlayButton(Context context) {
        this(context, null);
    }

    public AudioPlayButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioPlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

//        switchOval();
    }


    private void init() {
        setMinimumWidth( Utils.dp2Px( getContext(), 46 ) );
        setMinimumHeight( Utils.dp2Px( getContext(), 51 ) );

        mFrameCtrl = FrameAnimation.create()
                .startFrameIndex( 2 )
                .time( 150 )
                .load(
                        R.drawable.ic_audio_play_1,
                        R.drawable.ic_audio_play_2,
                        R.drawable.ic_audio_play_3
                ).into( this );

//        setBackgroundResource( R.color.colorMain );

        setFocusable( true );
        setClickable( true );

        setOnClickListener(v -> {
            if( !isEnabled() /*|| !isUrl*/ ) return;
            if( mOnAudioPlayClickListener != null ) mOnAudioPlayClickListener.onClick( v );
            play();
            LogUtil.d("AudioPlayButton -> " +
                    "Url:" + mText + " | " +
                    "isUrl:" + isUrl + " | " +
                    "mOnAudioPlayClickListener:" + mOnAudioPlayClickListener
            );
//            if( isUrl ) {
////                if( mMediaX != null ) {
//////                    mMediaX.setDataSource( mText );
////                    postDelayed( () -> mMediaX.play(), 200 );
////                }
//            }
//            else {
//                if( mTts != null ) mTts.start( mText );
//            }
        });
    }


    
    public void initMediaX() {
//        mMediaX = new MediaX( getContext() );
        mMediaXCTag = mMediaXC.createTag();
        mMediaXC.addOnMediaStatusListener( mMediaXCTag, new MediaXStatusAdapter() {
            @Override
            public void onPlay() {
                super.onPlay();
                mFrameCtrl.play( getContext() );
            }

            @Override
            public void onPause() {
                super.onPause();
                mFrameCtrl.stopNow();
            }

            @Override
            public void onStop() {
                super.onStop();
                mFrameCtrl.stopNow();
            }

            @Override
            public void onCompletion(int currentPlayNum, int playTotal, boolean isCompletion) {
                super.onCompletion(currentPlayNum, playTotal, isCompletion);
                mFrameCtrl.stopNow();
            }

            @Override
            public boolean onError(int what, int extra) {
                mFrameCtrl.stopNow();
                return super.onError(what, extra);
            }
        });
    }

//    public MediaX getMediaX() { return mMediaX; }

//    public AudioPlayButton setSpeechSynthesizer(SpeechSynthesizer synthesizer) {
////        mTts = synthesizer;
////        mOnSpeechStatusListener = new SpeechSynthesizer.OnSpeechStatusListener() {
////            @Override
////            public void onBegin() { mFrameCtrl.play( getContext() ); }
////
////            @Override
////            public void onPaused() { mFrameCtrl.pause(); }
////
////            @Override
////            public void onResumed() { mFrameCtrl.play( getContext() ); }
////
////            @Override
////            public void onCompleted(int code, String msg) { mFrameCtrl.stop(); }
////        };
////        mTts.addOnSpeechStatusListener( mOnSpeechStatusListener );
//        return this;
//    }

    public void release() {
//        if( mTts != null ) mTts.removeOnSpeechStatusListener( mOnSpeechStatusListener );
//        if( mMediaX != null ) mMediaX.release();
    }

//    public void reset() {
//        if( mMediaX != null ) mMediaX.reset();
//    }

    public void setSpeed(float speed) {
//        if( mMediaX != null ) mMediaX.setSpeed( speed );
        mMediaXC.setSpeed( speed );
    }

    public void play() {
        mMediaXC.play( mMediaXCTag, mText );
//        if( /*isReadyPlay &&*/isUrl ) mMediaXC.play( mText );
    }

    public void pauseNow() {
//        mTts.pause();
//        if( mMediaX != null ) mMediaX.pause();
//        mMediaXC.pause( mMediaXCTag );
        mFrameCtrl.stopNow();
    }

    public void stopNow() {
//        mTts.stop();
//        if( mMediaX != null ) mMediaX.stop();
//        mMediaXC.pause();
//        mMediaXC.seekTo( 0 );
//        mMediaXC.stop( mMediaXCTag );
        mFrameCtrl.stopNow();
    }

//    private boolean isReadyPlay = false;
    public void setAudio(String s) {
        if( s == null ) return;
        mText = s;
//        isReadyPlay = false;
//        if( mMediaX != null && ( isUrl = s.startsWith("http") ) ) mMediaX.setDataSource( mText );
//        if( isUrl = s.startsWith( "http" ) ) {
//            mMediaXC.add( aBoolean -> {
//                if( isReadyPlay ) play();
//            }, mText );
//        }
    }

    private OnClickListener mOnAudioPlayClickListener;
    public void setOnAudioPlayClickListener(OnClickListener l) {
        mOnAudioPlayClickListener = l;
    }

//    public AudioPlayButton start() {
//        if( mTts == null ) throw new NullPointerException("You need to call setSpeechSynthesizer()");
//        mTts.start( isUrl ? mText : mText );
//        return this;
//    }

//    public AudioPlayButton switchOval() {
//        setShape( Shape.OVAL );
//        requestLayout();
//        return this;
//    }
//
//    public AudioPlayButton switchRoundRect() {
//        setShape( Shape.ROUND_RECT );
//        setRadius( Utils.dp2Px( getContext(), 10 ) );
//        requestLayout();
//        return this;
//    }
}
