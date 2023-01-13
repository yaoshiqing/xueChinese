package com.gjjy.frontlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.Utils;
import com.gjjy.frontlib.R;

public class WrongQuestionSetOperationView extends LinearLayout {
    private ImageView ivRemoveBtn;
    private TextView tvContent;
    private TextView tvProgress;

    private int mProgress;
    private int mMaxProgress;

    public WrongQuestionSetOperationView(Context context) {
        this(context, null);
    }

    public WrongQuestionSetOperationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrongQuestionSetOperationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStrokeWidth( Utils.dp2Px( getContext(), 2 ) );
        mPaint.setColor( Color.parseColor( "#F2F2F4" ) );
        init();
    }

    private Paint mPaint;
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        canvas.drawLine( 0, 0, getWidth(), 0, mPaint );
    }

    private void init() {
        setOrientation( HORIZONTAL );
        setGravity( Gravity.CENTER_VERTICAL );
        addView( addRemoveBtnView() );
        addView( addContentView() );
        addView( addProgressView() );
    }

    private ImageView addRemoveBtnView() {
        ivRemoveBtn = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.leftMargin = Utils.dp2Px( getContext(), 33 );
        ivRemoveBtn.setLayoutParams( lp );
        ivRemoveBtn.setImageResource( R.drawable.ic_wrong_question_set_remove_btn );
        ivRemoveBtn.setFocusable( true );
        ivRemoveBtn.setClickable( true );
        ivRemoveBtn.setVisibility( INVISIBLE );
        return ivRemoveBtn;
    }

    private TextView addContentView() {
        tvContent = new TextView( getContext() );
        LayoutParams lp = new LayoutParams( 0, ViewGroup.LayoutParams.WRAP_CONTENT );
        lp.weight = 1;
        tvContent.setLayoutParams( lp );
        tvContent.setText( R.string.stringWrongQuestionSetLastTips );
        tvContent.setTextSize( 13 );
        tvContent.setTextColor( getResources().getColor( R.color.colorError ) );
        tvContent.setGravity( Gravity.CENTER );
        tvContent.setVisibility( INVISIBLE );
        return tvContent;
    }

    private TextView addProgressView() {
        tvProgress = new TextView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.rightMargin = Utils.dp2Px( getContext(), 33 );
        tvProgress.setLayoutParams( lp );
        tvProgress.setText( getProgressText() );
        tvProgress.setTextSize( 19 );
        tvProgress.setTextColor( getResources().getColor( R.color.colorMain ) );
        tvProgress.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tvProgress.setVisibility( INVISIBLE );
        return tvProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress + 1;
        if( mProgress >= mMaxProgress ) mProgress = mMaxProgress;
        post(() -> {
            if( ivRemoveBtn.getVisibility() != VISIBLE ) {
                AnimUtil.setAlphaAnimator( 800, ivRemoveBtn, tvProgress );
            }
            tvProgress.setText( getProgressText() );
            tvContent.setVisibility( mProgress == mMaxProgress ? VISIBLE : INVISIBLE );
        });
    }
    public int getProgress() { return mProgress; }

    public void setMax(int max) { mMaxProgress = max; }
    public int getMax() { return mMaxProgress; }

    public void setOnRemoveBtnClickListener(OnClickListener l) {
        ivRemoveBtn.setOnClickListener( l );
    }

    private Spanned getProgressText() {
        return Html.fromHtml(
                String.format( mProgress == mMaxProgress ? "%s/%s" : "%s<font color='#999999'>/%s</font>",
                        mProgress,
                        mMaxProgress
                )
        );
    }
}