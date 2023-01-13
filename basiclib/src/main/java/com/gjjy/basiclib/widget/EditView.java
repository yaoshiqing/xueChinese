package com.gjjy.basiclib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.R;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.Utils;

public class EditView extends LinearLayout {
    private EditText etView;
    private ImageView ivCheckButtonView;
    private ImageView ivRemoveButtonView;
    private TextView tvVerificationCodeView;
    private TextView tvTipsView;

    private Drawable mIconRes;
    private Drawable mDeleteRes;
    private Drawable mCheckButtonVisibleRes;
    private Drawable mCheckButtonInvisibleRes;

    private OnClickListener mOnDeleteButtonClickListener;
    private OnClickListener mOnCheckButtonClickListener;
    private OnClickListener mOnVerificationCodeButtonClickListener;

    private int mInputType = InputType.TYPE_CLASS_TEXT;
    private boolean isCheck = true;
    private boolean isEnableCheck = true;
    private boolean isEnableDelete = true;
    private boolean isEnableVerificationCode = false;

    private String mText = "1111";
    private String mTipsText;
    @ColorInt
    private int mTextColor;
    private int mTextSize;
    private int mMaxLength;
    private String mHint;
    @ColorInt
    private int mTextColorHint;
    private String mVerificationCodeText;
    private String mVerificationCodeRegainText;
    @ColorInt
    private int mVerificationCodeColor;

    private CountDownTimer mCountDownTimer;
    private int mMillisInFuture;
    private int mCountDownInterval;
    private Consumer<Integer> mOnCountDownListener;

    public EditView(@NonNull Context context) {
        this(context, null);
    }

    public EditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeArray( context, attrs );
        init();
        initView();
        initListener();
        checkTransformationMethod();

        setDeleteButtonIcon( R.drawable.ic_edit_delete_btn );
        setCheckButtonIcon(
                R.drawable.ic_edit_visible_btn,
                R.drawable.ic_edit_invisible_btn
        );
        setIconRes();
        setTipsText( mTipsText );
        setText( mText );
        setTextColor( mTextColor );
        setTextSize( mTextSize );
        setMaxLength( mMaxLength );
        setHint( mHint );
        setTextColorHint( mTextColorHint );
        setVerificationCodeButtonText();
        setVerificationCodeColor( mVerificationCodeColor );
    }

    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes( attrs, R.styleable.EditView );

        mIconRes = typedArray.getDrawable( R.styleable.EditView_evIcon );

        mDeleteRes = typedArray.getDrawable( R.styleable.EditView_evDeleteButton );

        mCheckButtonVisibleRes = typedArray.getDrawable(
                R.styleable.EditView_evCheckButtonVisible
        );

        mCheckButtonInvisibleRes = typedArray.getDrawable(
                R.styleable.EditView_evCheckButtonInvisible
        );

        isEnableCheck = isCheck = typedArray.getBoolean(
                R.styleable.EditView_evEnableCheckButton, true
        );

        isEnableDelete = typedArray.getBoolean(
                R.styleable.EditView_evEnableDeleteButton, true
        );

        isEnableVerificationCode = typedArray.getBoolean(
                R.styleable.EditView_evEnableVerificationCodeButton, false
        );

        mTipsText = typedArray.getString( R.styleable.EditView_evTipsText );

        mText = typedArray.getString( R.styleable.EditView_evText );

        mTextColor = typedArray.getColor(
                R.styleable.EditView_evTextColor,
                getResources().getColor( R.color.color34 )
        );

        mTextSize = typedArray.getDimensionPixelSize( R.styleable.EditView_evTextSize, 16 );

        mMaxLength = typedArray.getInt( R.styleable.EditView_evMaxLength, Integer.MAX_VALUE );

        mHint = typedArray.getString( R.styleable.EditView_evHint );

        mTextColorHint = typedArray.getColor(
                R.styleable.EditView_evTextColorHint,
                getResources().getColor( R.color.color9A )
        );

        mVerificationCodeText = typedArray.getString( R.styleable.EditView_evVerificationCodeText );

        mVerificationCodeRegainText = typedArray.getString(
                R.styleable.EditView_evVerificationCodeRegainText
        );

        mVerificationCodeColor = typedArray.getColor(
                R.styleable.EditView_evVerificationCodeColor,
                getResources().getColor( R.color.colorMain )
        );

        mMillisInFuture = typedArray.getInt(
                R.styleable.EditView_evVerificationMillisInFuture, 60 * 1000
        );

        mCountDownInterval = typedArray.getInt(
                R.styleable.EditView_evVerificationCountDownInterval, 1000
        );

        typedArray.recycle();
    }

    public void setMaxLength(int max) {
        mMaxLength = max;
        if( etView == null ) return;
        etView.setFilters( new InputFilter[] { new InputFilter.LengthFilter( max ) } );
    }

    private void init() {
        setOrientation( VERTICAL );
//        setFocusableInTouchMode( true );
    }

    private void initView() {
        LinearLayout llEdit = new LinearLayout( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dp2Px( getContext(), 48 )
        );
//        lp.weight = 1;
        llEdit.setLayoutParams( lp );
        llEdit.setOrientation( HORIZONTAL );
        llEdit.setBackgroundResource( R.drawable.ic_edit_border_bg );

        llEdit.setPadding(
                Utils.dp2Px( getContext(), 13 ),
                0,
                Utils.dp2Px( getContext(), 9 ),
                0
        );
        llEdit.setGravity( Gravity.CENTER_VERTICAL );

        llEdit.addView( etView = createEditView() );
        llEdit.addView( ivRemoveButtonView = createRemoveBtn() );
        llEdit.addView( ivCheckButtonView = createCheckBtn() );
        llEdit.addView( tvVerificationCodeView = createVerificationCodeBtn() );

        addView( llEdit );
        addView( tvTipsView = createTipsView() );

    }

    private void initListener() {
        etView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( ivRemoveButtonView == null ) return;
                ivRemoveButtonView.setVisibility( s.length() > 0 ? VISIBLE : GONE );
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivRemoveButtonView.setOnClickListener(v -> {
            etView.setText( null );
            if( mOnDeleteButtonClickListener != null ) mOnDeleteButtonClickListener.onClick( v );
        });

        ivCheckButtonView.setOnClickListener(v -> {
            isCheck = !isCheck;

            checkTransformationMethod();
            setCheckButtonRes();
            if( mOnCheckButtonClickListener != null ) mOnCheckButtonClickListener.onClick( v );
        });

        tvVerificationCodeView.setOnClickListener(v -> {
            if( mOnVerificationCodeButtonClickListener == null ) return;
            mOnVerificationCodeButtonClickListener.onClick( v );
        });
    }

    public void callOnClickOfVerificationCodeBtn() {
        tvVerificationCodeView.callOnClick();
    }

    public void cancelCountDownTimer() {
        if( mCountDownTimer != null ) mCountDownTimer.cancel();
    }

    public void startCountDownTimer() {
        if( mCountDownTimer != null ) mCountDownTimer.start();
    }

    public EditText getView() { return etView; }

    public Editable getText() { return etView.getText(); }

    public String getTextString() { return getText().toString(); }
    
    public void showTips() {
        if( tvTipsView == null || tvTipsView.getVisibility() == VISIBLE ) return;
        AnimUtil.setAlphaAnimator(
                200,
                animator -> tvTipsView.setVisibility( VISIBLE ),
                tvTipsView
        );
    }

    public void hideTips() {
        if( tvTipsView == null || tvTipsView.getVisibility() == GONE ) return;
        AnimUtil.setAlphaAnimator(
                200,
                animator -> tvTipsView.setVisibility( GONE ),
                tvTipsView
        );
    }

    public void setIcon(@DrawableRes int id) {
        mIconRes = toDrawable( id );
        setIconRes();
    }
    
    /**
     * 输入类型
     * @param type  {@link InputType}
     */
    public void setInputType(int type) {
        mInputType = type;
        if( etView != null ) etView.setInputType( type );
    }

    public void setDeleteButtonIcon(@DrawableRes int id) {
        mDeleteRes = toDrawable( id );
        setDeleteButtonRes();
    }

    public void setCheckButtonIcon(@DrawableRes int visibleResId, @DrawableRes int invisibleResId) {
        mCheckButtonVisibleRes = toDrawable( visibleResId );
        mCheckButtonInvisibleRes = toDrawable( invisibleResId );
        setCheckButtonRes();
    }

    public void setTipsText(CharSequence s) {
        if( tvTipsView == null ) return;
        tvTipsView.setText( s );
    }

    public void setTipsText(@StringRes int resId) {
        if( tvTipsView == null ) return;
        tvTipsView.setText( resId );
    }

    public void setText(String s) {
        mText = s;
        if( etView != null ) etView.setText( s );
    }

    public void setTextColor(@ColorInt int resId) {
        mTextColor = resId;
        if( etView != null ) etView.setTextColor( resId );
    }

    public void setTextSize(int size) {
        this.mTextSize = size;
        if( etView != null ) etView.setTextSize( size );
    }

    public void setHint(String s) {
        this.mHint = s;
        if( etView != null ) etView.setHint( s );
    }

    public void setTextColorHint(@ColorInt int resId) {
        mTextColorHint = resId;
        if( etView != null ) etView.setHintTextColor( resId );
    }

    public void setVerificationCodeText(String s) {
        mVerificationCodeText = s;
        setVerificationCodeButtonText();
    }

    public void setVerificationCodeColor(int resId) { mVerificationCodeColor = resId; }

    public void setEnableCheck(boolean enable) {
        isEnableCheck = isCheck = enable;
        checkTransformationMethod();
    }

    public void setEnableDelete(boolean enable) { isEnableDelete = enable; }

    public void setEnableVerificationCode(boolean enable) { isEnableVerificationCode = enable; }

    public void setMillisInFuture(int millis) { mMillisInFuture = millis; }

    public void setCountDownInterval(int millis) { mCountDownInterval = millis; }

    public void setOnDeleteButtonClickListener(OnClickListener l) {
        mOnDeleteButtonClickListener = l;
    }

    public void setOnCheckButtonClickListener(OnClickListener l) {
        mOnCheckButtonClickListener = l;
    }

    public void setOnVerificationCodeButtonClickListener(OnClickListener l) {
        mOnVerificationCodeButtonClickListener = l;
    }

    public void setOnCountDownListener(Consumer<Integer> mOnCountDownListener) {
        this.mOnCountDownListener = mOnCountDownListener;
    }

    private void checkTransformationMethod() {
        etView.setTransformationMethod(isCheck ?
                PasswordTransformationMethod.getInstance() :
                HideReturnsTransformationMethod.getInstance()
        );
    }

    private void setIconRes() {
        if( etView == null ) return;
        etView.setCompoundDrawablesWithIntrinsicBounds(
                mIconRes,
                null,
                null,
                null
        );
    }

    private void setDeleteButtonRes() {
        if( ivRemoveButtonView == null ) return;
        ivRemoveButtonView.setVisibility( isEnableDelete ? VISIBLE : GONE );
        if( !isEnableDelete ) return;
        ivRemoveButtonView.setImageDrawable( mDeleteRes );
    }

    private void setCheckButtonRes() {
        if( ivCheckButtonView == null ) return;
        ivCheckButtonView.setVisibility( isEnableCheck ? VISIBLE : GONE );
        if( !isEnableCheck ) return;
        if( !isCheck ) {
            ivCheckButtonView.setImageDrawable( mCheckButtonVisibleRes );
        }else {
            ivCheckButtonView.setImageDrawable( mCheckButtonInvisibleRes );
        }
    }

    private void setVerificationCodeButtonText() {
        if( tvVerificationCodeView == null ) return;
        tvVerificationCodeView.setVisibility( isEnableVerificationCode ? VISIBLE : GONE );
        if( !isEnableVerificationCode ) return;
        tvVerificationCodeView.setText( mVerificationCodeText );
        if( mCountDownTimer == null ) doCountDownTimer();
    }

    private void doCountDownTimer() {
        mCountDownTimer = new CountDownTimer( mMillisInFuture, mCountDownInterval ) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvVerificationCodeView.setEnabled( false );
                tvVerificationCodeView.setText( String.valueOf( millisUntilFinished / 1000 ) );
                if( mOnCountDownListener == null ) return;
                mOnCountDownListener.accept( (int) millisUntilFinished );
            }

            @Override
            public void onFinish() {
                tvVerificationCodeView.setText( mVerificationCodeRegainText );
                tvVerificationCodeView.setEnabled( true );
                if( mOnCountDownListener != null ) mOnCountDownListener.accept( -1 );
            }
        };
    }

    private Drawable toDrawable(@DrawableRes int id) { return getResources().getDrawable( id ); }

    private EditText createEditView() {
//        int dp10 = Utils.dp2Px( getContext(), 10 );
        EditText et = new EditText( getContext() );
        LayoutParams lp = new LayoutParams( 0, ViewGroup.LayoutParams.MATCH_PARENT );
        lp.weight = 1;
        et.setLayoutParams( lp );
        et.setCompoundDrawablePadding( Utils.dp2Px( getContext(), 14 ) );
        et.setInputType( mInputType );
//        et.setPadding( 0, dp10, 0, dp10 );
        et.setBackground( null );

        setIconRes();
        return et;
    }

    private ImageView createRemoveBtn() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, Utils.dp2Px( getContext(), 48 )
        );
        iv.setPadding( Utils.dp2Px( getContext(), 5 ), 0, 0, 0 );
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_edit_delete_btn );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );
        iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        iv.setFocusable( true );
        iv.setClickable( true );

        iv.setVisibility( GONE );

        setDeleteButtonRes();
        return iv;
    }

    private ImageView createCheckBtn() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Utils.dp2Px( getContext(), 48 )
        );
        iv.setPadding(
                Utils.dp2Px( getContext(), 15 ), 0,
                Utils.dp2Px( getContext(), 9 ), 0
        );
        iv.setLayoutParams( lp );
        iv.setContentDescription( getResources().getString( R.string.stringEmpty ) );
        iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        setCheckButtonRes();
        return iv;
    }

    private TextView createVerificationCodeBtn() {
        TextView tv = new TextView( getContext() );
        LayoutParams lp = new LayoutParams(
                Utils.dp2Px( getContext(), 88 ),
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        tv.setLayoutParams( lp );
        tv.setTextSize( 17 );
        tv.setTextColor( mVerificationCodeColor );
        tv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.shape_verification_code_line,
                0,
                0,
                0
        );
        tv.setCompoundDrawablePadding( Utils.dp2Px( getContext(), 10 ) );
        tv.setGravity( Gravity.CENTER );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tv.setVisibility( GONE );
        tv.setFocusable( true );
        tv.setClickable( true );
        return tv;
    }

    private TextView createTipsView() {
        TextView tv = new TextView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.topMargin = Utils.dp2Px( getContext(), 8 );
        lp.leftMargin = Utils.dp2Px( getContext(), 12 );
        tv.setLayoutParams( lp );
        tv.setTextColor( getResources().getColor( R.color.colorDialogError ) );
        tv.setTextSize( 13 );
        tv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_edit_caveat,
                0,
                0,
                0
        );
        tv.setCompoundDrawablePadding( Utils.dp2Px( getContext(), 6 ) );
        tv.setVisibility( GONE );
        return tv;
    }


}
