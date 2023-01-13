package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.FrameAnimation;
import com.ybear.ybmediax.audio.AudioX;
import com.ybear.ybmediax.audio.Config;
import com.ybear.ybmediax.audio.EncodingType;
import com.ybear.ybmediax.audio.OnAudioStatusAdapter;
import com.ybear.ybmediax.audio.RateInHzType;
import com.gjjy.frontlib.R;

public class AudioPlayBackButton extends AppCompatImageView {
    private FrameAnimation.FrameControl mFrameCtrl;
    private AudioX mAudioX;

    public AudioPlayBackButton(Context context) {
        this(context, null);
    }

    public AudioPlayBackButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioPlayBackButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setMinimumWidth( Utils.dp2Px( getContext(), 46 ) );
        setMinimumHeight( Utils.dp2Px( getContext(), 51 ) );

        mAudioX = new AudioX();

        mAudioX.setConfig(
                Config.newConfig()
                        .setSampleRateInHz( RateInHzType.RATE_8000 )
                        .setEncoding( EncodingType.ENCODING_PCM_16BIT )
        );

        mFrameCtrl = FrameAnimation.create()
                .endFrameIndex( 0 )
                .time( 150 )
                .load(
                        R.drawable.ic_audio_play_back_1,
                        R.drawable.ic_audio_play_back_2
                ).into( this );

        setFocusable( true );
        setClickable( true );

        setOnClickListener(v -> {
            if( mAudioX.isPlaying() ) {
                mAudioX.pause();
            }else {
                mAudioX.play();
            }
        });

        mAudioX.setOnAudioStatusListener(new OnAudioStatusAdapter() {
            @Override
            public void onPlayed() { mFrameCtrl.play( getContext() ); }

            @Override
            public void onPaused() { mFrameCtrl.pause(); }

            @Override
            public void onCompleted() { mFrameCtrl.stop(); }

            @Override
            public boolean onError(Exception e) {
                return true;
            }
        });
    }

    public void setData(String path) { mAudioX.setData( path ); }

    public void stop() {
        mAudioX.stop();
    }

    public void release() { mAudioX.release(); }
}
