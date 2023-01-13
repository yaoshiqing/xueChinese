package com.gjjy.frontlib.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 遮罩图片控件
 */
public class MaskImageView extends AppCompatImageView {
    private Canvas mMaskCanvas;
    private Paint mPaint;
    private RectF mRectArc;
    private PorterDuffXfermode mXfermode;

    private Bitmap mMaskBackgroundBitmap;
    private Bitmap mMakeTopBitmap;
    private Bitmap mMakeBottomBitmap;
    private Bitmap mImageBitmap;
    private Rect mRectSrc, mRectDst;
    private ValueAnimator mValueAnim;

    private int mStartAngle = 270;
    private int mSweepAngle = 360;
    private long mDuration = 3000;
    private boolean isBackRunMask = false;
    private boolean isReverseSweep = false;

    private int mMakeTopWidth = -1;
    private int mMakeTopHeight = -1;
    private int mMakeBottomWidth = -1;
    private int mMakeBottomHeight = -1;
    private int mImageWidth = -1;
    private int mImageHeight = -1;

    public MaskImageView(Context context) {
        this(context, null);
    }

    public MaskImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMaskCanvas = new Canvas();

        mPaint = new Paint();
        mRectArc = new RectF();
        mXfermode = new PorterDuffXfermode( PorterDuff.Mode.DST_OUT );//DST_OUT

        mRectSrc = new Rect();
        mRectDst = new Rect();

        mValueAnim = new ValueAnimator();
        //动画事件监听器
        mValueAnim.addUpdateListener(
                animation -> startTo( (int) animation.getAnimatedValue() )
        );

        //启用硬件加速，否则会出现一些异常（比如黑边，设计器和模拟器可能依旧会存在黑边）
        setLayerType( View.LAYER_TYPE_HARDWARE, null );
//        setLayerType( View.LAYER_TYPE_SOFTWARE, null );
    }

    /**
     * 动画绘制
     * @param canvas        画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if( mImageBitmap != null ) {
            drawBitmap( canvas, mImageBitmap, mImageWidth, mImageHeight );
        }
        if( mMakeBottomBitmap != null ) {
            drawBitmap( canvas, mMakeBottomBitmap, mMakeBottomWidth, mMakeBottomHeight );
        }

        if( mMakeTopBitmap != null ) {
            drawBitmap( mMaskCanvas, mMakeTopBitmap, mMakeTopWidth, mMakeTopHeight );
        }

        drawArc( mMaskCanvas, mStartAngle, mSweepAngle );


        if( mMaskBackgroundBitmap != null ) {
            canvas.drawBitmap( mMaskBackgroundBitmap, 0, 0, mPaint );
        }

//        drawBitmap( canvas, mImageBitmap, mImageWidth, mImageHeight );
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
//        if( mImageBitmap != null ) {
//            drawBitmap( canvas, mImageBitmap, mImageWidth, mImageHeight );
//        }
//        if( mMakeBottomBitmap != null ) {
//            drawBitmap( canvas, mMakeBottomBitmap, mMakeBottomWidth, mMakeBottomHeight );
//        }
//
//        if( mMakeTopBitmap != null ) {
//            drawBitmap( mMaskCanvas, mMakeTopBitmap, mMakeTopWidth, mMakeTopHeight );
//        }
//
//        drawArc( mMaskCanvas, mStartAngle, mSweepAngle );
//
//
//        if( mMaskBackgroundBitmap != null ) {
//            canvas.drawBitmap( mMaskBackgroundBitmap, 0, 0, mPaint );
//        }
//    }

    public void startAnimTo(@IntRange(from = 0, to = 360) int angle, long duration) {
        updatedValues( angle, duration );
        mValueAnim.start();
    }

    public void startAnimTo(@IntRange(from = 0, to = 360) int angle) {
        startAnimTo( angle, mDuration );
    }

    public void startAnim(long duration) { startAnimTo( 360, duration ); }

    public void startAnim() { startAnim( mDuration  ); }


    public void startAnimOfProgressTo(@IntRange(from = 0, to = 100) int progress, long duration) {
        startAnimTo( toAngle( progress ), duration );
    }

    public void startAnimOfProgressTo(@IntRange(from = 0, to = 100) int progress) {
        startAnimOfProgressTo( progress, mDuration );
    }

    public void startAnimOfProgress(long duration) {
        startAnimOfProgressTo( 100, duration );
    }

    public void startAnimOfProgress() { startAnimOfProgress( mDuration ); }

    public void startTo(@IntRange(from = 0, to = 360) int angle) {
        mSweepAngle = angle;
        postInvalidate();
    }

    /**
     * 设置开始进度
     * @param angle             角度
     */
    public void setStartOfAngle(int angle) { mStartAngle = angle; }

    /**
     * 设置开始进度
     * @param progress          百分比进度
     */
    public void setStartOfProgress(int progress) { setStartOfAngle( toAngle( progress ) ); }

    /**
     * 动画播放时长
     * @param duration          时长
     */
    public void setDuration(long duration) { mDuration = duration; }

    /**
     * 前景遮罩图
     * @param bitmap            图片
     */
    public void setBackgroundMakeTop(Bitmap bitmap, int width, int height) {
        mMakeTopWidth = width;
        mMakeTopHeight = height;
        mMakeTopBitmap = bitmap;
        postInvalidate();
    }

    public void setBackgroundMakeTop(Bitmap bitmap) {
        setBackgroundMakeTop( bitmap, -1, -1 );
    }

    /**
     * 前景遮罩图
     * @param resId             图片资源id
     */
    public void setBackgroundMakeTop(@DrawableRes int resId, int width, int height) {
        mMakeTopWidth = width;
        mMakeTopHeight = height;
        mMakeTopBitmap = resIdToBitmap( resId );
        postInvalidate();
    }

    public void setBackgroundMakeTop(@DrawableRes int resId) {
        setBackgroundMakeTop( resId, -1, -1 );
    }

    /**
     * 背景遮罩图
     * @param bitmap            图片
     */
    public void setBackgroundMakeBottom(Bitmap bitmap, int width, int height) {
        mMakeBottomWidth = width;
        mMakeBottomHeight = height;
        mMakeBottomBitmap = bitmap;
        createMaskBackgroundBitmap();
        postInvalidate();
    }

    public void setBackgroundMakeBottom(Bitmap bitmap) {
        setBackgroundMakeBottom( bitmap, -1, -1 );
    }

    /**
     * 背景遮罩图
     * @param resId             图片资源id
     */
    public void setBackgroundMakeBottom(@DrawableRes int resId, int width, int height) {
        mMakeBottomWidth = width;
        mMakeBottomHeight = height;
        mMakeBottomBitmap = resIdToBitmap( resId );
        createMaskBackgroundBitmap();
        postInvalidate();
    }

    public void setBackgroundMakeBottom(@DrawableRes int resId) {
        setBackgroundMakeBottom( resId, -1, -1 );
    }

    public void setImageDrawable(@Nullable Drawable drawable, int width, int height) {
//        super.setImageDrawable(drawable);
        if ( drawable == null ) return;
        mImageWidth = width;
        mImageHeight = height;

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ?
                        Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565
        );
        Canvas canvas = new Canvas( bitmap );
        drawable.setBounds(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight()
        );
        drawable.draw( canvas );
        mImageBitmap = bitmap;
        postInvalidate();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        setImageDrawable( drawable, -1, -1 );
    }

    public void setImageBitmap(Bitmap bm, int width, int height) {
//        super.setImageBitmap(bm);
        mImageWidth = width;
        mImageHeight = height;
        mImageBitmap = bm;
        postInvalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setImageBitmap( bm, -1, -1 );
    }

    public void setImageResource(int resId, int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
        mImageBitmap = resIdToBitmap( resId );
        postInvalidate();
    }

    @Override
    public void setImageResource(int resId) {
//        super.setImageResource(resId);
        setImageResource( resId, -1, -1 );
    }

    /**
     * 倒放遮罩
     * @param isBackRunMask     是否倒放
     */
    public void setBackRunMask(boolean isBackRunMask) { this.isBackRunMask = isBackRunMask; }

    /**
     * 翻转遮罩
     * @param isReverseSweep    是否翻转
     */
    private void setReverseSweep(boolean isReverseSweep) {
        this.isReverseSweep = isReverseSweep;
    }

    /**
     * 创建绘制遮罩背景墙
     */
    private void createMaskBackgroundBitmap() {
        if( mMaskBackgroundBitmap != null ) return;
        measure( 0, 0 );
        try {
            mMaskCanvas.setBitmap(
                    mMaskBackgroundBitmap = Bitmap.createBitmap(
                            getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制扇形
     * @param canvas            画布
     * @param start             开始位置
     * @param sweep             当前位置
     */
    private void drawArc(Canvas canvas, float start, float sweep) {
        if( sweep == 0 ) return;
        mRectArc.set( -getWidth(), -getHeight(), getWidth() * 2, getHeight() * 2 );
        mPaint.setXfermode( mXfermode );
        sweep = isReverseSweep ? sweep : -sweep;
        canvas.drawArc( mRectArc, start, sweep, true, mPaint );
        mPaint.setXfermode( null );
//        LogUtil.e( "drawArc -> " + start + " | " + sweep);
    }

    /**
     * 绘制图片
     * @param canvas            画布
     * @param bitmap            图片
     */
    private void drawBitmap(@NonNull Canvas canvas, @Nullable Bitmap bitmap,
                            int width, int height) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if( bitmap == null ) return;
        if( width == -1 ) width = w;
        if( height == -1 ) height = h;
        int l = width == -1 ? 0 : ( w - width ) / 2;
        int t = height == -1 ? 0 : ( h - height ) / 2;
        mRectSrc.set( 0, 0, bitmap.getWidth(), bitmap.getHeight() );
        mRectDst.set( l, t, l + width, t + height );
        canvas.drawBitmap( bitmap, mRectSrc, mRectDst, mPaint );
    }

    /**
     * 资源id转Bitmap
     * @param resId             图片资源id
     * @return                  Bitmap
     */
    private Bitmap resIdToBitmap(@DrawableRes int resId) {
        return BitmapFactory.decodeResource( getResources(), resId );
    }

    /**
     * 更新动画值
     * @param angle             角度
     * @param duration          时长
     */
    private void updatedValues(@IntRange(from = 0, to = 360) int angle, long duration) {
        angle = 360 - angle;
        if( isBackRunMask ) {
            mValueAnim.setIntValues( angle, mSweepAngle );
        }else {
            mValueAnim.setIntValues( mSweepAngle, angle );
        }
        mValueAnim.setDuration( duration );
    }

    /**
     * 百分比转角度
     * @param progress          百分比
     * @return                  角度
     */
    private int toAngle(int progress) { return (int)( ( 360F / 100F ) * progress ); }
}
