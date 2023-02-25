package com.gjjy.basiclib.widget.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationControlBar;
import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationSlideProgressBar;
import com.gjjy.basiclib.widget.catx_video.entity.PlayerOperationTitleBar;
import com.ybear.ybcomponent.Utils;

import java.util.Formatter;
import java.util.Locale;

public abstract class PlayerControlView extends RelativeLayout implements PlayerOperationLayoutIF {
    private ImageView ivBackBtn;
    private ImageView ivPlayBtn;
    private ImageView ivFlipBtn;
    private TextView tvSeekStart;
    private TextView tvSeekTotal;
    private TextView tvSlideProgressLayout;
    private SeekBar sbSeek;
    private SeekBar sbSlideSeek;
    @Nullable
    private OnPlayerTouchListener mOnPlayerTouchListener;
    private OnPlayerControlEventListener mOnPlayerControlEventListener;
    private OnSeekChangeListener mOnSeekChangeListener;
    private final StringBuilder mFormatBuilder;
    private final Formatter mFormatter;
    private int mTouchCount;
    private int mCurrentProgress;
    private long mTouchTime;
    private boolean isShowSlideProgress;
    private View vTitleLayout;
    private View vControlLayout;
    private View vSlideProgressLayout;
    private float mOldSlidingX;
    private float mOldSlidingY;
    private Comparable<Float> mCallVolume;
    private Window mWindow;
    private boolean isShow;
    private boolean isAutoHide;
    private long mAutoHideDelayMillis;
    private boolean mEnableSwitchPlayStatus;
    private boolean mEnableSwitchFlipStatus;

    public PlayerControlView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mTouchCount = 0;
        mCurrentProgress = 0;
        mTouchTime = -1L;
        mOldSlidingX = -1.0f;
        mOldSlidingY = -1.0f;
        isShow = true;
        isAutoHide = false;
        mAutoHideDelayMillis = 3000L;
        mEnableSwitchPlayStatus = true;
        mEnableSwitchFlipStatus = true;
        initView();
        initListener();
    }

    private void initView() {
        Context context = getContext();
        PlayerOperationTitleBar titleBar = onTitleBar();
        PlayerOperationControlBar controlBar = onControlBar();
        PlayerOperationSlideProgressBar slideProgressBar = onSlideProgressBar();
        if (titleBar != null) {
            addView(vTitleLayout = titleBar.getLayout(), 0, new ViewGroup.LayoutParams(-1, -2));
            ivBackBtn = titleBar.getBackButton();
        }
        if (controlBar != null) {
            RelativeLayout.LayoutParams lpBottom = new RelativeLayout.LayoutParams(-1, -2);
            lpBottom.addRule(12);
            addView(vControlLayout = controlBar.getLayout(), 1, (ViewGroup.LayoutParams) lpBottom);
            ivPlayBtn = controlBar.getPlayButtonImageView();
            sbSeek = controlBar.getProgressSeekBar();
            tvSeekStart = controlBar.getStartTimeTextView();
            tvSeekTotal = controlBar.getTotalTimeTextView();
            ivFlipBtn = controlBar.getFlipButtonImageView();
            ivPlayBtn.setTag((Object) 0);
            ivFlipBtn.setTag((Object) 0);
        }
        if (slideProgressBar != null) {
            vSlideProgressLayout = slideProgressBar.getLayout();
            RelativeLayout.LayoutParams lpSlide = new RelativeLayout.LayoutParams(Utils.dp2Px(context, 180), Utils.dp2Px(context, 90));
            lpSlide.addRule(13);
            addView(vSlideProgressLayout, 2, (ViewGroup.LayoutParams) lpSlide);
            vSlideProgressLayout.setVisibility(GONE);
            sbSlideSeek = slideProgressBar.getProgressSeekBar();
            tvSlideProgressLayout = slideProgressBar.getProgressTextView();
            sbSlideSeek.setEnabled(false);
        }
        setFocusable(true);
        setClickable(true);
        switchPlayStatus(false, false);
        switchFlipStatus(false, false);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        return doTouch(event);
    }

    public boolean doTouch(MotionEvent event) {
        long doubleTime = 250L;
        long clickTime = 50L;
        switch (event.getAction()) {
            case 0: {
                if (mTouchTime == -1L || System.currentTimeMillis() - mTouchTime > doubleTime) {
                    mTouchTime = System.currentTimeMillis();
                    mTouchCount = 0;
                }
                if (System.currentTimeMillis() - mTouchTime <= doubleTime && ++mTouchCount == 2) {
                    mTouchTime = -1L;
                    mTouchCount = 0;
                    switchPlayStatus();
                    if (mOnPlayerTouchListener != null) {
                        mOnPlayerTouchListener.onDoubleClick();
                    }
                }
                if (mOnPlayerTouchListener != null) {
                    mOnPlayerTouchListener.onDown();
                }
                mOldSlidingX = event.getX();
                mOldSlidingY = event.getY();
                break;
            }
            case 1: {
                if (mTouchTime != -1L && System.currentTimeMillis() - mTouchTime >= clickTime) {
                    if (mOnPlayerTouchListener != null) {
                        mOnPlayerTouchListener.onClick();
                    }
                    if (!isShow || (event.getY() >= getChildAt(VISIBLE).getHeight() && event.getY() <= getHeight() - getChildAt(1).getHeight())) {
                        switchDisplay();
                    }
                }
                hideSlideProgress();
                break;
            }
            case 2: {
                float diffX = event.getX() - mOldSlidingX;
                doSlidingOfProgress(event.getX(), diffX);
                break;
            }
        }
        return true;
    }

    private void doSlidingOfBrightness(float y, float diffY) {
        if (mWindow == null) {
            throw new NullPointerException("You must call setWindow() method.");
        }
        float min = 0.0f;
        float max = -500.0f;
        if (y != mOldSlidingY && diffY <= min && diffY >= max) {
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.screenBrightness = (diffY - min) / (max - min);
            mWindow.setAttributes(lp);
        }
    }

    private void doSlidingOfVolume(float y, float diffY) {
        if (mWindow == null) {
            throw new NullPointerException("You must call setWindow() method.");
        }
        float min = 0.0f;
        float max = -500.0f;
        if (y != mOldSlidingY && diffY <= min && diffY >= max && mCallVolume != null) {
            mCallVolume.compareTo((diffY - min) / (max - min));
        }
    }

    private void doSlidingOfProgress(float x, float diffX) {
        if (x == mOldSlidingX) {
            return;
        }
        showSlideProgress();
        changeSlideProgress((int) (mCurrentProgress + diffX * 10.0f), true);
        mOldSlidingX = x;
    }

    public void setVolumeListener(Comparable<Float> call) {
        mCallVolume = call;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        if (ivBackBtn != null) {
            ivBackBtn.setOnClickListener(v -> {
                if (mOnPlayerControlEventListener == null) {
                    return;
                }
                mOnPlayerControlEventListener.onFinishEvent(ivBackBtn);
            });
        }
        ivPlayBtn.setOnClickListener(v -> switchPlayStatus());
        ivFlipBtn.setOnTouchListener((v, ev) -> {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                switchFlipStatus();
            }
            return false;
        });
//        ivFlipBtn.setOnClickListener(v -> switchFlipStatus());
        sbSeek.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeSlideProgress(progress, fromUser);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                showSlideProgress();
                isAutoHide = false;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                hideSlideProgress();
                isAutoHide = true;
                startAutoHide();
            }
        });
    }

    private void changeSlideProgress(int progress, boolean fromUser) {
        mCurrentProgress = progress;
        String slide = formatProgress(progress) + " / " + formatProgress(sbSeek.getMax());
        setSeekStartTime(progress);
        if (tvSlideProgressLayout != null) {
            tvSlideProgressLayout.setText((CharSequence) slide);
        }
        if (sbSlideSeek != null) {
            sbSlideSeek.setProgress(progress);
        }
        if (mOnSeekChangeListener != null && fromUser) {
            mOnSeekChangeListener.onProgressChanged(progress);
        }
    }

    public View getTitleLayout() {
        return vTitleLayout;
    }

    public View getControlLayout() {
        return vControlLayout;
    }

    public View getSlideProgressLayout() {
        return vSlideProgressLayout;
    }

    public void showSlideProgress() {
        if (vSlideProgressLayout == null || isShowSlideProgress) {
            return;
        }
        isShowSlideProgress = true;
        vSlideProgressLayout.setVisibility(VISIBLE);
    }

    public void hideSlideProgress() {
        if (vSlideProgressLayout == null || !isShowSlideProgress) {
            return;
        }
        isShowSlideProgress = false;
        vSlideProgressLayout.setVisibility(GONE);
    }

    public void showTitleLayout() {
        if (vTitleLayout != null) {
            vTitleLayout.setVisibility(VISIBLE);
        }
    }

    public void hideTitleLayout() {
        if (vTitleLayout != null) {
            vTitleLayout.setVisibility(GONE);
        }
    }

    public void showControlLayout() {
        if (vControlLayout != null) {
            vControlLayout.setVisibility(VISIBLE);
        }
    }

    public void hideControlLayout() {
        if (vControlLayout != null) {
            vControlLayout.setVisibility(GONE);
        }
    }

    public void changePlayBtnImageResource(@DrawableRes int resId) {
        if (ivPlayBtn != null) {
            ivPlayBtn.setImageResource(resId);
        }
    }

    public void changeFlipBtnImageResource(@DrawableRes int resId) {
        if (ivFlipBtn != null) {
            ivFlipBtn.setImageResource(resId);
        }
    }

    public void setWindow(Window w) {
        mWindow = w;
    }

    @Nullable
    public OnPlayerTouchListener getOnPlayerTouchListener() {
        return mOnPlayerTouchListener;
    }

    public void setOnPlayerTouchListener(@Nullable OnPlayerTouchListener l) {
        mOnPlayerTouchListener = l;
    }

    public OnPlayerControlEventListener getOnPlayerControlEventListener() {
        return mOnPlayerControlEventListener;
    }

    public void setOnPlayerControlEventListener(OnPlayerControlEventListener l) {
        mOnPlayerControlEventListener = l;
    }

    public OnSeekChangeListener getOnSeekChangeListener() {
        return mOnSeekChangeListener;
    }

    public void setOnSeekChangeListener(OnSeekChangeListener l) {
        mOnSeekChangeListener = l;
    }

    public void show() {
        post(() -> {
            isShow = true;
            getChildAt(VISIBLE).setVisibility(VISIBLE);
            getChildAt(1).setVisibility(VISIBLE);
        });
    }

    public void hide() {
        post(() -> {
            isShow = false;
            getChildAt(VISIBLE).setVisibility(GONE);
            getChildAt(1).setVisibility(GONE);
        });
    }

    public void switchDisplay() {
        boolean isShow = isShow();
        if (mOnPlayerTouchListener != null) {
            mOnPlayerTouchListener.onControlViewDisplay(isShow);
        }
        if (isShow) {
            hide();
        } else {
            show();
            startAutoHide();
        }
    }

    public void startAutoHide() {
        if (isAutoHide) {
            return;
        }
        isAutoHide = true;
        if (mAutoHideDelayMillis <= 0L) {
            return;
        }
        postDelayed(() -> {
            if (isAutoHide) {
                hide();
            }
        }, mAutoHideDelayMillis);
    }

    public void cancelAutoHide() {
        isAutoHide = false;
    }

    public boolean isShow() {
        return isShow;
    }

    public void autoHideDelayed(long delayMillis) {
        mAutoHideDelayMillis = delayMillis;
    }

    private boolean switchStatus(ImageView iv, @DrawableRes int resFalse, @DrawableRes int resTrue, boolean status, boolean isSwitch) {
        if (iv == null) {
            return false;
        }
        if (isSwitch) {
            iv.setImageResource(status ? resTrue : resFalse);
        }
        iv.setTag((Object) (int) (status ? 0 : 1));
        return !status;
    }

    public void setEnableSwitchPlayStatus(boolean enable) {
        mEnableSwitchPlayStatus = enable;
    }

    public void setEnableSwitchFlipStatus(boolean enable) {
        mEnableSwitchFlipStatus = enable;
    }

    public boolean switchPlayStatus(boolean status, boolean isCall) {
        int resFalse = onControlBar().getPlayResId();
        int resTrue = onControlBar().getPauseResId();
        status = switchStatus(ivPlayBtn, resFalse, resTrue, status, mEnableSwitchPlayStatus);
        if (isCall && mOnPlayerControlEventListener != null) {
            mOnPlayerControlEventListener.onPlayerClick(ivPlayBtn);
            if (status) {
                mOnPlayerControlEventListener.onPauseEvent(ivPlayBtn);
            } else {
                mOnPlayerControlEventListener.onPlayerEvent(ivPlayBtn);
            }
        }
        return status;
    }

    public boolean switchPlayStatus() {
        Object status = ivPlayBtn.getTag();
        if (status == null) {
            ivPlayBtn.setTag(status = 0);
        }
        return switchPlayStatus(Integer.parseInt(status.toString()) == 1, true);
    }

    public boolean switchFlipStatus(boolean status, boolean isCall) {
        int resFalse = onControlBar().getFlipLandscapeResId();
        int resTrue = onControlBar().getFlipPortraitResId();
        status = switchStatus(ivFlipBtn, resFalse, resTrue, status, mEnableSwitchFlipStatus);
        if (isCall && mOnPlayerControlEventListener != null) {
            mOnPlayerControlEventListener.onFlipClick(ivFlipBtn);
            if (status) {
                mOnPlayerControlEventListener.onPortraitFlipEvent(ivFlipBtn);
            } else {
                mOnPlayerControlEventListener.onLandscapeFlipEvent(ivFlipBtn);
            }
        }
        return status;
    }

    public boolean switchFlipStatus() {
        Object status = ivFlipBtn.getTag();
        if (status == null) {
            ivFlipBtn.setTag(status = 0);
        }
        return switchFlipStatus(Integer.parseInt(status.toString()) == 1, true);
    }

    public void setSeekTime(int start, int end) {
        mCurrentProgress = start;
        post(() -> {
            if (sbSeek != null) {
                sbSeek.setMax(end);
            }
            if (sbSlideSeek != null) {
                sbSlideSeek.setMax(end);
            }
            setSeekStartTime(start);
            if (tvSeekTotal != null) {
                tvSeekTotal.setText((CharSequence) formatProgress(end));
            }
        });
    }

    public void setProgress(int progress) {
        post(() -> {
            mCurrentProgress = progress;
            setSeekStartTime(progress);
            if (sbSeek != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    sbSeek.setProgress(progress, true);
                } else {
                    sbSeek.setProgress(progress);
                }
            }
        });
    }

    private void setSeekStartTime(long time) {
        if (tvSeekStart == null) return;
        tvSeekStart.setText((CharSequence) formatProgress(time));
    }

    private String formatProgress(long time) {
        long totalSeconds = time / 1000L;
        long seconds = totalSeconds % 60L;
        long minutes = totalSeconds / 60L % 60L;
        long hours = totalSeconds / 3600L;
        mFormatBuilder.setLength(VISIBLE);
        if (hours > 0L) {
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        }
        return mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }

    public interface OnSeekChangeListener {
        void onProgressChanged(int p0);
    }
}
