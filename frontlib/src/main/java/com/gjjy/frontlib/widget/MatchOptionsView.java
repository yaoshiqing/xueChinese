package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.gjjy.frontlib.R;
import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.annotations.OptionsType;
import com.gjjy.frontlib.entity.OptionsEntity;

/**
 * 匹配题的View
 */
public class MatchOptionsView extends FrameLayout {
    public interface OnMatchChangedListener {
        void changed(OptionsEntity data, int correctCount, int count, boolean isCorrect);
    }
    private OnClickListener mOnClickListener;
    private OnMatchChangedListener mOnMatchChangedListener;

    private MatchCorrectView mcvMatchCorrect;
    private AnswerOptionsLayout aolLeft;
    private AnswerOptionsLayout aolRight;

    private int mCorrectCount = 0;

    public MatchOptionsView(@NonNull Context context) {
        this(context, null);
    }

    public MatchOptionsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchOptionsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initListener();
    }

    private void init() {
        addScrollMatchLayout();
//        addFloatingView();
    }

    private void recycle() {
        if( aolLeft != null ) aolLeft.release();
        if( aolRight != null ) aolRight.release();
    }

    public void reset() {
        if( aolLeft != null ) aolLeft.reset();
        if( aolRight != null ) aolRight.reset();
    }

    public void release() {
        recycle();
    }

    public void stop() {
        if( aolLeft != null ) aolLeft.stop();
        if( aolRight != null ) aolRight.stop();
    }

    public void pause() {
        if( aolLeft != null ) aolLeft.pause();
        if( aolRight != null ) aolRight.pause();
    }

    public void setSpeed(float speed) {
        if( aolLeft != null ) aolLeft.setSpeed( speed );
        if( aolRight != null ) aolRight.setSpeed( speed );
    }

    private void addScrollMatchLayout() {
        NestedScrollView nsvScroll = new NestedScrollView( getContext() );

        nsvScroll.setLayoutParams( new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ) );
        nsvScroll.setVerticalScrollBarEnabled( false );
        nsvScroll.setOverScrollMode( OVER_SCROLL_NEVER );

        //包裹正确布局和匹配布局
//        FrameLayout llScrollMatchLayout = new FrameLayout(getContext());
        LinearLayout llScrollMatchLayout = new LinearLayout(getContext());
        llScrollMatchLayout.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        llScrollMatchLayout.setOrientation( LinearLayout.VERTICAL );

//        llScrollMatchLayout.setOrientation( LinearLayout.VERTICAL );
//        llScrollMatchLayout.setGravity( Gravity.CENTER );


        //正确布局
        mcvMatchCorrect = new MatchCorrectView( getContext() );
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        mcvMatchCorrect.setLayoutParams( lp );
        llScrollMatchLayout.addView( mcvMatchCorrect );
        //匹配布局
        addMathView( llScrollMatchLayout );

        nsvScroll.addView( llScrollMatchLayout );
        addView( nsvScroll );
    }

    private void initListener() {
        aolLeft.setOnOptClickListener((vg, v, data, position) -> check( v ));
        aolRight.setOnOptClickListener((vg, v, data, position) -> check( v ));
    }

    private void startCheckAnim(boolean isCorrect, long delayMillis) {
        postDelayed(() -> {
            if( isCorrect ) {
                aolLeft.removeSelectView();
                aolRight.removeSelectView();
                postDelayed( () -> {
                    mcvMatchCorrect.addData( aolLeft.getSelectData() );
                    aolLeft.cancelSelect();
                    aolRight.cancelSelect();
                    aolLeft.setEnableSelect( true );
                    aolRight.setEnableSelect( true );
                }, 90);
            }else {
                aolLeft.switchDefault( false );
                aolRight.switchDefault( false );
                aolLeft.cancelSelect();
                aolRight.cancelSelect();
                aolLeft.setEnableSelect( true );
                aolRight.setEnableSelect( true );
            }
        }, delayMillis);
    }
    private void check(View v) {
        if( mOnClickListener != null ) mOnClickListener.onClick( v );

        OptionsEntity lData = aolLeft.getSelectData();
        OptionsEntity rData = aolRight.getSelectData();
        if( lData == null || rData == null ) return;

        boolean isCorrect = lData.getTag() == rData.getTag();

        aolLeft.setEnableSelect(  false );
        aolRight.setEnableSelect( false );

        post(() -> {
            if( isCorrect ) {
//                aolLeft.switchCorrect();
//                aolRight.switchCorrect();
            }else {
                aolLeft.switchError();
                aolRight.switchError();
            }
            startCheckAnim( isCorrect, isCorrect ? 0 : 250 );
        });

        if( isCorrect ) mCorrectCount++;
        doListener( isCorrect );

    }

    private void doListener(boolean isCorrect) {
        if( mOnMatchChangedListener != null ) {
            mOnMatchChangedListener.changed(
                    aolLeft.getSelectData(),
                    mCorrectCount,
                    aolLeft.getItemCount(),
                    isCorrect
            );
        }
    }


//    /**
//     * 开始匹配成功的动画
//     */
//    private void startCorrectAnim() {
//        aolCorrectLayout.measure( 0, 0 );
//        float toY = aolCorrectLayout.getHeight();
//        ValueAnimator anim = ValueAnimator.ofFloat( ivLeftFloat.getY(), toY );
//        anim.setDuration( 300 );
//        anim.addUpdateListener(animation -> {
//            float val = (float) animation.getAnimatedValue();
//            ivLeftFloat.setY( val );
//            ivRightFloat.setY( val );
//
//            if( val == toY ) {
//                aolCorrectLayout.addData( OptionsLayout.LINEAR_GRID, aolLeft.getSelectData() );
//                aolCorrectLayout.setEnableSelect( false );
//                aolCorrectLayout.switchCorrectAll();
//                aolLeft.removeSelectView();
//                aolRight.removeSelectView();
//                hideAnim();
//            }
//        });
//        AnimUtil.setAlphaAnimator( ivLeftFloat, 500 );
//        anim.start();
//    }

//    /**
//     * 开始匹配动画
//     * @param isCorrect     是否处理回答正确的动画
//     */
//    private void startMatchAnim(boolean isCorrect) {
//        AnswerOptions aoLeft, aoRight;
//        if( ( aoLeft = aolLeft.getSelectAnswerOpt() ) == null ||
//                ( aoRight = aolRight.getSelectAnswerOpt() ) == null ) return;
//
//        float toX = ( getWidth() / 2F ) - ( aolLeft.getSelectAnswerOpt().getWidth() / 2F );
//        float y = SysUtil.getScreenHeight( getContext() ) / 2F - getY();
//
//        aolLeft.setEnableSelect( false );
//        aolRight.setEnableSelect( false );
//
//        ivLeftFloat.setX( aolLeft.getX() );
//        ivLeftFloat.setY( y );
//        ivRightFloat.setX( aolRight.getX() + ( aolRight.getWidth() - aoRight.getWidth() ) );
//        ivRightFloat.setY( y );
//
//        changeFloatView();
//
//        ValueAnimator animL = ValueAnimator.ofFloat( ivLeftFloat.getX(), toX );
//        ValueAnimator animR = ValueAnimator.ofFloat( ivRightFloat.getX(), toX );
//        animL.setDuration( 500 );
//        animR.setDuration( 500 );
//        animL.addUpdateListener(animation -> {
//            float val = (float) animation.getAnimatedValue();
//            ivLeftFloat.setX( val );
//
//            if( val == toX ) {
//                if( isCorrect ) {
//                    aoLeft.switchCorrectStyle();
//                    aoRight.switchCorrectStyle();
//                }else {
//                    aoLeft.switchErrorStyle();
//                    aoRight.switchErrorStyle();
//                }
//                changeFloatView();
//                postDelayed(() -> {
//                    //开始正确答案的动画
//                    if( isCorrect ) {
//                        startCorrectAnim();
//                    }else {
//                        hideAnim();
//                    }
//                }, 600);
//            }
//        });
//        animR.addUpdateListener(animation ->
//                ivRightFloat.setX( (float) animation.getAnimatedValue() )
//        );
//        animL.start();
//        animR.start();
//
//        ivLeftFloat.setVisibility( GONE );
//        ivRightFloat.setVisibility( GONE );
//        AnimUtil.setAlphaAnimator( ivLeftFloat, 800 );
//        AnimUtil.setAlphaAnimator(ivRightFloat, 800,
//                animation -> ivRightFloat.setVisibility( GONE ));
//    }
//
//    /**
//     * 开始匹配成功的动画
//     */
//    private void startCorrectAnim() {
//        aolCorrectLayout.measure( 0, 0 );
//        float toY = aolCorrectLayout.getHeight();
//        ValueAnimator anim = ValueAnimator.ofFloat( ivLeftFloat.getY(), toY );
//        anim.setDuration( 300 );
//        anim.addUpdateListener(animation -> {
//            float val = (float) animation.getAnimatedValue();
//            ivLeftFloat.setY( val );
//            ivRightFloat.setY( val );
//
//            if( val == toY ) {
//                aolCorrectLayout.addData( OptionsLayout.LINEAR_GRID, aolLeft.getSelectData() );
//                aolCorrectLayout.setEnableSelect( false );
//                aolCorrectLayout.switchCorrectAll();
//                aolLeft.removeSelectView();
//                aolRight.removeSelectView();
//                hideAnim();
//            }
//        });
//        AnimUtil.setAlphaAnimator( ivLeftFloat, 500 );
//        anim.start();
//    }

//    private void hideAnim() {
//        ivLeftFloat.setVisibility( GONE );
//        ivRightFloat.setVisibility( GONE );
//        aolLeft.cancelSelect();
//        aolRight.cancelSelect();
//        aolLeft.setEnableSelect( true );
//        aolRight.setEnableSelect( true );
//    }


//    private void changeFloatView() {
//        postDelayed(() -> {
//            ivLeftFloat.setImageBitmap( Utils.viewToBitmap( aolLeft.getSelectAnswerOpt() ) );
//            ivRightFloat.setImageBitmap( Utils.viewToBitmap( aolRight.getSelectAnswerOpt() ) );
//        }, 10);
//    }

    private void addMathView(ViewGroup vg) {
        LinearLayout layout = (LinearLayout) View.inflate(
                getContext(),
                R.layout.block_match_view,
                null
        );

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        layout.setLayoutParams( lp );

        aolLeft = (AnswerOptionsLayout) layout.getChildAt( 0 );
        aolRight = (AnswerOptionsLayout) layout.getChildAt( 1 );
//        aolLeft.setReverseLayout( true );
//        aolRight.setReverseLayout( true );

        aolLeft.defaultStyleIsCorrect( true );
        aolRight.defaultStyleIsCorrect( true );
        aolLeft.setEnableChangedTextLayout( false );
        aolRight.setEnableChangedTextLayout( false );

        vg.addView( layout );
    }

//    private ImageView ivLeftFloat, ivRightFloat;
//    private void addFloatingView() {
//        LayoutParams lp = new LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//        ivLeftFloat = new ImageView( getContext() );
//        ivRightFloat = new ImageView( getContext() );
//        ivLeftFloat.setLayoutParams( lp );
//        ivRightFloat.setLayoutParams( new LayoutParams( lp ) );
//        ivLeftFloat.setVisibility( GONE );
//        ivRightFloat.setVisibility( GONE );
//
//        addView( ivRightFloat );
//        addView( ivLeftFloat );
//    }

    public void setData(OptionsEntity... list) {
        if( list == null || list.length == 0 ) return;
        OptionsEntity[] lOpts = new OptionsEntity[ list.length ];
        OptionsEntity[] rOpts = new OptionsEntity[ list.length ];
        for (int i = 0; i < list.length; i++) {
            OptionsEntity data = list[ i ];
            int rIndex = data.getTag() - 1;
            rIndex = Math.max( rIndex, 0 );
            lOpts[ i ] = new OptionsEntity( data.setOptType( OptionsType.CENTER_TEXT ) );
            rOpts[ rIndex ] = new OptionsEntity(
                    data.setOptType( OptionsType.CENTER_PINYIN )
                            .setAudioUrl( null )
            );
        }
        aolLeft.setData( OptionsLayout.LINEAR, lOpts );
        aolRight.setData( OptionsLayout.LINEAR, rOpts );
    }

    public void setOnMatchClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    public void setOnMatchChangedListener(OnMatchChangedListener l) {
        mOnMatchChangedListener = l;
    }
}
