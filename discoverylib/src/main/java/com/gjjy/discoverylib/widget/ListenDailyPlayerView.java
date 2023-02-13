package com.gjjy.discoverylib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.DateTimeType;
import com.gjjy.discoverylib.R;
import com.ybear.ybmediax.media.Data;
import com.ybear.ybmediax.media.MediaX;
import com.ybear.ybmediax.media.MediaXStatusAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ListenDailyPlayerView extends LinearLayout {
    public interface OnCurrentProgressListener {
        void onProgress(int progress);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Speed {
        float X_0_8 = 0.8F;
        float X_1_0 = 1.0F;
        float X_1_2_5 = 1.25F;
        float X_1_5 = 1.5F;
    }

    private final String mTimePattern = DateTimeType.MINUTE + ":" + DateTimeType.SECOND;
    private TextView tvPlayerCurTime;
    private TextView tvPlayerTotalTime;
    private SeekBar sbPlayerSeek;
    private ImageView ivPlayerFallBackBtn;
    private ImageView ivPlayerGoAheadBtn;
    private ImageView ivPlayerPlayBtn;
    private ImageView ivPlayerSpeedBtn;

    private MediaX mMediaX;

    @Speed
    private float mSpeed = Speed.X_1_0;
    private int mCurrentProgress;
    private int mMaxProgress;
    private boolean isCompletion;

    private OnCurrentProgressListener mOnCurrentProgressListener;

    public ListenDailyPlayerView(Context context) {
        this(context, null);
    }

    public ListenDailyPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListenDailyPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), Utils.dp2Px(getContext(), 126));
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        mMediaX = new MediaX(getContext());

        initView();
        initListener();
        switchPlayBtnStatus(false);
        switchSpeedBtnStatus(0);
        ivPlayerSpeedBtn.setTag(1);
    }

    private void initView() {
        View div = new View(getContext());
        div.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2Px(getContext(), 1)));
        div.setBackgroundColor(getResources().getColor(R.color.colorMainBG));
        addView(div);

        ViewGroup vgPlayerSeekBar = createSeekBar();
        ViewGroup vgPlayControlView = createPlayControlView();
        //当前时长
        tvPlayerCurTime = vgPlayerSeekBar.findViewById(
                R.id.listen_daily_progress_bar_tv_current_time
        );
        //总时长
        tvPlayerTotalTime = vgPlayerSeekBar.findViewById(
                R.id.listen_daily_progress_bar_tv_total_time
        );
        //进度条
        sbPlayerSeek = vgPlayerSeekBar.findViewById(
                R.id.listen_daily_progress_bar_sb_seek
        );
        //后退
        ivPlayerFallBackBtn = vgPlayControlView.findViewById(
                R.id.listen_daily_play_control_iv_step_fall_back_btn
        );
        //前进
        ivPlayerGoAheadBtn = vgPlayControlView.findViewById(
                R.id.listen_daily_play_control_iv_step_go_ahead_btn
        );
        //播放
        ivPlayerPlayBtn = vgPlayControlView.findViewById(
                R.id.listen_daily_play_control_iv_play_btn
        );
        //播放倍数
        ivPlayerSpeedBtn = vgPlayControlView.findViewById(
                R.id.listen_daily_play_control_iv_speed_btn
        );
    }

    private void initListener() {
        //播放/暂停
        ivPlayerPlayBtn.setOnClickListener(v -> {
            boolean isPlay = (Boolean) v.getTag();
            playStatus(isPlay);
        });

        //倒退
        ivPlayerFallBackBtn.setOnClickListener(
                v -> seekTo(mCurrentProgress - 15000)
        );

        //前进
        ivPlayerGoAheadBtn.setOnClickListener(
                v -> seekTo(mCurrentProgress + 15000)
        );

        //播放倍数
        ivPlayerSpeedBtn.setOnClickListener(v -> {
            int index;
            switchSpeedBtnStatus((index = (int) ivPlayerSpeedBtn.getTag() + 1) < 4 ? index : 0);
        });

        //进度条滑动
        sbPlayerSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMediaX.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                mMediaX.play();
            }
        });

        mMediaX.setVideoInfoListener(new MediaX.CallVideoInfo() {
            @Override
            public void onSource(@NonNull Data data) {
            }

            @Override
            public void onInfo(int progress, int total) {
                post(() -> setTotalProgress(total));
            }

            @Override
            public void onProgress(int progress) {
                post(() -> setCurrentProgress(progress));
            }
        });

        mMediaX.setOnMediaStatusListener(new MediaXStatusAdapter() {
            @Override
            public void onPlay() {
                super.onPlay();
                switchPlayBtnStatus(true);
            }

            @Override
            public void onPause() {
                super.onPause();
                switchPlayBtnStatus(false);
            }

            @Override
            public void onStop() {
                super.onStop();
                switchPlayBtnStatus(false);
            }

            @Override
            public void onCompletion(int currentPlayNum, int playTotal, boolean isCom) {
                super.onCompletion(currentPlayNum, playTotal, isCom);
//
                if (!isCom) {
                    return;
                }
                switchPlayBtnStatus(false);
                setCurrentProgress(mMaxProgress);
            }
        });
    }

    public void release() {
        if (mMediaX != null) {
            mMediaX.pause();
            mMediaX.release();
        }
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setCurrentProgress(int progress, boolean fromUser) {
        if (progress < 0) {progress = 0;}
        isCompletion = progress >= mMaxProgress;
        mCurrentProgress = progress;
        tvPlayerCurTime.setText(DateTime.toTimeProgressFormat(progress, mTimePattern));
        if (!fromUser) {
            sbPlayerSeek.setProgress(progress);
        }
        if (mOnCurrentProgressListener != null) {mOnCurrentProgressListener.onProgress(progress);}

        int diff = mMaxProgress - progress;
        if (diff > 0 && diff <= 1000) {
            final long finalProgress = progress;
            postDelayed(() -> {
                if (mCurrentProgress != finalProgress) {return;}
                setCurrentProgress(mMaxProgress);
            }, 1000);
        }
    }

    public void setCurrentProgress(int progress) {
        setCurrentProgress(progress, false);
    }

    public void setOnCurrentProgressListener(OnCurrentProgressListener l) {
        this.mOnCurrentProgressListener = l;
    }

    public void setTotalProgress(int progress) {
        mMaxProgress = progress;
        tvPlayerTotalTime.setText(DateTime.toTimeProgressFormat(progress, mTimePattern));
        sbPlayerSeek.setMax(progress);
    }

    public int getTotalProgress() {
        return mMaxProgress;
    }


    public void setDataUrl(String url) {
        mMediaX.setDataSource(url);
    }

    public void playStatus(boolean isPlay) {
        switchPlayBtnStatus(isPlay);
        seekTo(isCompletion ? 0 : mCurrentProgress, isPlay);
//        if( isPlay ) {
//            seekTo( isCompletion ? 0 : mCurrentProgress, isPlay );
////            mMediaX.play();
//            return;
//        }
//        if( isCompletion ) seekTo( 0, isPlay );
//        mMediaX.pause();
    }

    public boolean isPlaying() {
        return mMediaX.isPlaying();
    }

    @Speed
    public float getSpeed() {
        return mSpeed;
    }

    public void seekTo(int progress, boolean isPlay) {
        if (progress == mMaxProgress) {
            mCurrentProgress = 0;
            playStatus(true);
            return;
        }
        if (progress < 0) progress = 0;
        if (progress > mMaxProgress) progress = mMaxProgress;
        mMediaX.seekTo(mCurrentProgress = progress);
        setCurrentProgress(progress, false);

        if (isPlay && !isPlaying()) {
            switchPlayBtnStatus(true);
            mMediaX.play();
        }
    }

    public void seekTo(int progress) {
        seekTo(progress, true);
    }

    private void switchPlayBtnStatus(boolean isPlay) {
        ivPlayerPlayBtn.setImageResource(isPlay ?
                R.drawable.ic_listen_daily_stop_btn :
                R.drawable.ic_listen_daily_play_btn
        );
        ivPlayerPlayBtn.setTag(!isPlay);
    }

    private void switchSpeedBtnStatus(int index) {
        int resId;
        switch (index) {
            case 1:
                resId = R.drawable.ic_listen_daily_speed_1_btn;
                mSpeed = Speed.X_0_8;
                break;
            case 2:
                resId = R.drawable.ic_listen_daily_speed_3_btn;
                mSpeed = Speed.X_1_2_5;
                break;
            case 3:
                resId = R.drawable.ic_listen_daily_speed_4_btn;
                mSpeed = Speed.X_1_5;
                break;
            default:
                resId = R.drawable.ic_listen_daily_speed_2_btn;
                mSpeed = Speed.X_1_0;
                break;
        }
        ivPlayerSpeedBtn.setImageResource(resId);
        ivPlayerSpeedBtn.setTag(index);
        doSpeedBtnListener(mSpeed);
    }

    private void doSpeedBtnListener(float speed) {
        mMediaX.setSpeed(speed);
    }

    private ViewGroup createSeekBar() {
        return (ViewGroup) LayoutInflater
                .from(getContext())
                .inflate(R.layout.block_listen_daily_progress_bar, this);
    }

    private ViewGroup createPlayControlView() {
        return (ViewGroup) LayoutInflater
                .from(getContext())
                .inflate(R.layout.block_listen_daily_play_control, this);
    }
}
