package com.gjjy.frontlib.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.ResUtil;
import com.gjjy.frontlib.R;

public class AnswerBar extends LinearLayout {
    private FrameLayout flProgress;
    private ImageView ivCloseBtn, ivComplete, ivMoreBtn, ivIcon;
    private TextView tvHP;
    private SeekBar seekProgress;

    private Activity mActivity;

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private OnClickListener mOnCloseClickListener;
    private OnClickListener mOnMoreClickListener;

    @DrawableRes
    private int resCompleteOn = 0;
    @DrawableRes
    private int resCompleteOff = 0;

    public AnswerBar(Context context) {
        this(context, null);
    }

    public AnswerBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initData();
        initListener();
    }

    private void init() {
        setMinimumHeight( Utils.dp2Px( getContext(), 45 ) );
        setOrientation( HORIZONTAL );
        setGravity( Gravity.CENTER_VERTICAL );
        setClipChildren( false );

        addView( ivCloseBtn = new ImageView( getContext() ) );

        //包裹进度条和星星的布局
        flProgress = new FrameLayout( getContext() );
        LayoutParams lp = new LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.weight = 1;
        lp.leftMargin = Utils.dp2Px( getContext(), 20 );
        lp.rightMargin = Utils.dp2Px( getContext(), 20 );
        lp.gravity = Gravity.CENTER_VERTICAL;
        flProgress.setLayoutParams( lp );
        flProgress.setClipChildren( false );
        flProgress.addView( seekProgress = new SeekBar( getContext() ) );
        flProgress.addView( ivComplete = new ImageView( getContext() ) );
        flProgress.addView( ivIcon = new ImageView( getContext() ) );

        addView( flProgress );
        addView( tvHP = new TextView( getContext() ) );
        addView( ivMoreBtn = new ImageView( getContext() ) );

        initCloseBtn();
        initMoreBtn();
        initHP();
        initComplete();
        initProgress();
        initIcon();

        switchProgressStyle( 1  );
    }

    private void initData() {
        switchCloseIconOfCloseBtn();
        ivComplete.setImageResource( resCompleteOff );
        setHPImage( R.drawable.ic_answer_bar_hp );
        ivMoreBtn.setImageResource( R.drawable.ic_answer_bar_more );
    }

    private void initListener() {
        //关闭按钮点击事件监听器
        ivCloseBtn.setOnClickListener(v -> {
            if( mOnCloseClickListener != null ) {
                mOnCloseClickListener.onClick( v );
            }else {
                if( mActivity != null ) mActivity.finish();
            }
        });

        //更多按钮点击事件监听器
        ivMoreBtn.setOnClickListener(v -> {
            if( mOnMoreClickListener != null ) mOnMoreClickListener.onClick( v );
        });
    }

    private void setHPImage(@DrawableRes int res) {
        tvHP.setCompoundDrawablesWithIntrinsicBounds( res, 0, 0, 0 );
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility( visibility );
        boolean isEnable = visibility == VISIBLE;
        ivCloseBtn.setEnabled( isEnable );
        ivMoreBtn.setEnabled( isEnable );
    }

    public void switchExitIconOfCloseBtn() {
        ivCloseBtn.setImageResource( R.drawable.ic_gray_back );
    }
    public void switchCloseIconOfCloseBtn() {
        ivCloseBtn.setImageResource( R.drawable.ic_answer_bar_close );
    }

    private void initCloseBtn() {
        int p10 = Utils.dp2Px( getContext(), 10 );
        measure( 0, 0 );
        LayoutParams lp = new LayoutParams( getMeasuredHeight(), getMeasuredHeight() );
        ivCloseBtn.setLayoutParams( lp );
        ivCloseBtn.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        ivCloseBtn.setPadding( p10, p10, p10, p10 );
        ivCloseBtn.setFocusable( true );
        ivCloseBtn.setClickable( true );
    }

    private void initHP() {
        int p10 = Utils.dp2Px( getContext(), 10 );
        measure( 0, 0 );
        LayoutParams lp = new LayoutParams( getMeasuredHeight(), getMeasuredHeight() );
        lp.setMargins( 0, p10, p10, p10 );
        tvHP.setLayoutParams( lp );
        tvHP.setCompoundDrawablePadding( Utils.dp2Px( getContext(), 6 ) );
        tvHP.setTextSize( 19 );
        tvHP.setTextColor( getResources().getColor( R.color.color66 ) );
        tvHP.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tvHP.setText( "0" );
        tvHP.setGravity( Gravity.CENTER );
        tvHP.setVisibility( GONE );
    }

    private void initMoreBtn() {
        int p10 = Utils.dp2Px( getContext(), 10 );
        measure( 0, 0 );
        LayoutParams lp = new LayoutParams( getMeasuredHeight(), getMeasuredHeight() );
        ivMoreBtn.setLayoutParams( lp );
        ivMoreBtn.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        ivMoreBtn.setPadding( p10, p10, p10, p10 );
        ivMoreBtn.setFocusable( true );
        ivMoreBtn.setClickable( true );
    }

    private void initComplete() {
        int size = Utils.dp2Px( getContext(), 26 );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams( size, size );
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        ivComplete.setLayoutParams( lp );
    }

    private void initProgress() {
        int height = Utils.dp2Px( getContext(), 12 );
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, height
        );
        lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        seekProgress.setLayoutParams( lp );
        seekProgress.setEnabled( false );
        seekProgress.setTranslationX( Utils.dp2Px( getContext(), -8 ) );

        //去掉thumb白色背景
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            seekProgress.setSplitTrack( false );
        }
        seekProgress.setPadding(
                Utils.dp2Px( getContext(), 10 ),
                seekProgress.getPaddingTop(),
                Utils.dp2Px( getContext(), 6 ),
                seekProgress.getPaddingBottom()
        );
        seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changedCompleteIcon( progress );
                if( mOnSeekBarChangeListener == null ) return;
                mOnSeekBarChangeListener.onProgressChanged( seekBar, progress, fromUser );
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if( mOnSeekBarChangeListener == null ) return;
                mOnSeekBarChangeListener.onStartTrackingTouch( seekBar );
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if( mOnSeekBarChangeListener == null ) return;
                mOnSeekBarChangeListener.onStopTrackingTouch( seekBar );}
        });
        ivIcon.setVisibility( GONE );
    }

    private void initIcon() {
        ivIcon.setLayoutParams( new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        seekProgress.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        seekProgress.getViewTreeObserver()
                                .removeOnGlobalLayoutListener( this );
                        FrameLayout.LayoutParams lp =
                                (FrameLayout.LayoutParams) ivIcon.getLayoutParams();
                        lp.topMargin = Utils.dp2Px( getContext(), 2 );
                        lp.leftMargin = Utils.dp2Px( getContext(), 5 );
                        ivIcon.setLayoutParams( lp );
                        ivIcon.setX( seekProgress.getX() + seekProgress.getPaddingLeft() );
                        ivIcon.setY( seekProgress.getY() + seekProgress.getPaddingTop() );
                    }
                });
    }

    /**
     * 改变完成的Icon
     * @param progress  当前进度
     */
    private void changedCompleteIcon(int progress) {
        boolean isComplete = progress >= seekProgress.getMax();
        if( resCompleteOn != 0 && resCompleteOff != 0 ) {
            ivComplete.setImageResource(
                    isComplete ? resCompleteOn : resCompleteOff
            );
        }

        //完成动画
        if( isComplete && !isHideProgressStar() ) {
            ivComplete.startAnimation(
                    AnimationUtils.loadAnimation(getContext(), R.anim.scale_zoom)
            );
        }
        //转换为百分比
        progress = (int) ( ( (double) progress / (double) seekProgress.getMax() ) * 100D );
        if( progress >= 60 ) {
            switchProgressStyle( 3 );
        }else if( progress >= 30 ) {
            switchProgressStyle( 2 );
        }else {
            switchProgressStyle( 1 );
        }
    }

    public void showCloseBtn() { ivCloseBtn.setVisibility( VISIBLE ); }
    public void hideCloseBtn() { ivCloseBtn.setVisibility( INVISIBLE ); }
    public boolean isHideCloseBtn() { return ivCloseBtn.getVisibility() != VISIBLE; }

    public void showMoreBtn() { ivMoreBtn.setVisibility( VISIBLE ); }
    public void hideMoreBtn() { ivMoreBtn.setVisibility( INVISIBLE ); }
    public void hideMoreBtnOfGone() { ivMoreBtn.setVisibility( GONE ); }
    public boolean isHideMoreBtn() { return ivMoreBtn.getVisibility() != VISIBLE; }
    public void setEnableMoreBtn(boolean enable) { ivMoreBtn.setEnabled( enable ); }

    public void showProgressBtn() { flProgress.setVisibility( VISIBLE ); }
    public void hideProgressBtn() { flProgress.setVisibility( INVISIBLE ); }
    public boolean isHideProgressBtn() { return flProgress.getVisibility() != VISIBLE; }

    public void showProgressStar() { ivComplete.setVisibility( VISIBLE ); }
    public void hideProgressStar() { ivComplete.setVisibility( INVISIBLE ); }
    public boolean isHideProgressStar() { return ivComplete.getVisibility() != VISIBLE; }

    public void showHP() { tvHP.setVisibility( VISIBLE ); }
    public void hideHP() { tvHP.setVisibility( INVISIBLE ); }

    public boolean isHideHP() { return tvHP.getVisibility() != VISIBLE; }

    public <T extends Activity> void setActivity(T activity) {
        mActivity = activity;
    }

    public void setCompleteIcon(@DrawableRes int onRes, @DrawableRes int offRes) {
        resCompleteOn = onRes;
        resCompleteOff = offRes;
    }

    public void setCompleteIcon(@DrawableRes int res) { setCompleteIcon( res, res ); }

    public void setProgress(int progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekProgress.setProgress( progress, true );
        }else {
            seekProgress.setProgress( progress );
        }
        ivIcon.setVisibility( progress >= (float)seekProgress.getMax() / 6F ? VISIBLE : GONE );
    }

    public void setUpdatedHP(int val) {
        if( tvHP == null ) return;
        setHPImage( val > 0 ? R.drawable.ic_answer_bar_hp : R.drawable.ic_answer_bar_hp_die );
        tvHP.setText( String.valueOf( val ) );
    }

    public void setMaxProgress(int maxProgress) {
        seekProgress.setMax( maxProgress );
    }


    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    public void setOnCloseClickListener(OnClickListener l) { mOnCloseClickListener = l; }

    public void setOnMoreClickListener(OnClickListener l) { mOnMoreClickListener = l; }

    private int mCurrentProgressType;
//    private void switchProgressStyleOfAnim(@IntRange(from = 1, to = 3) int type) {
//        if( mCurrentProgressType == type ) return;
//        AnimUtil.setAlphaAnimator(0.5F, 600, animation-> {
//            AnimUtil.setAlphaAnimator(1F, 600L, seekProgress, ivIcon);
//            postDelayed(() -> switchProgressStyle( type ), 100);
//        }, seekProgress, ivIcon);
//    }

    private void switchProgressStyle(@IntRange(from = 1, to = 3) int type) {
        if( mCurrentProgressType == type ) return;
        mCurrentProgressType = type;
        seekProgress.setThumb(
                ResUtil.getDrawable( getContext(), "shape_seek_progress_thumb_" + type )
        );
        seekProgress.setProgressDrawable(
                ResUtil.getDrawable( getContext(), "layer_seek_progress_" + type )
        );
        ivIcon.setImageDrawable(
                ResUtil.getDrawable( getContext(), "ic_seek_progress_icon_" + type )
        );
    }
}
