package com.gjjy.frontlib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.gjjy.basiclib.widget.drag.FlowLayoutManager;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.ChinesePinYinAdapter;
import com.gjjy.frontlib.annotations.OptionsType;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.ybmediax.media.MediaXC;
import com.gjjy.speechsdk.synthesizer.SpeechSynthesizer;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.shape.ShapeFrameLayout;
import com.ybear.ybutils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 答题选项控件
 */
public class AnswerOptions extends ShapeFrameLayout {
    private ImageView ivImg;
    private RecyclerView rvCenterText;
    private LinearLayout llBottomTextLayout;
    private LinearLayout llCenter;
    private ImageView ivBottomImg;
    private TextView tvBottomText;

    private OptionsEntity mData;
    private List<ChinesePinYinAdapter.ItemData> mCnPinYinList;
    private ChinesePinYinAdapter mCnPinYinAdapter;

    private RequestManager mGlide;
//    private MediaX mMediaX;
    private MediaXC mMediaXC;
    private int mMediaXCTag;
//    private SpeechSynthesizer mTts;
    private boolean mEnableTouchStyle = true;
    private boolean mEnableSelect = true;
    private boolean mEnableClick = true;
    private boolean isTouch = false;
    @OptionsType
    private int mOptType = OptionsType.CENTER_TEXT;

    private String mText;

    private SpeechSynthesizer.OnSpeechStatusListener mOnSpeechStatusListener;

    public AnswerOptions(Context context) { this(context, null); }

    public AnswerOptions(Context context, AttributeSet attrs) { this(context, attrs, 0); }

    public AnswerOptions(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        build();
    }

    private void init() {
        setLayoutParams( new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
//        setShape( Shape.ROUND_RECT );
//        setRadius( Utils.dp2Px( getContext(), 15 ) );
//        setBorderSize(Utils.dp2Px( getContext(), 4 ) );
//        setBorderColor( Color.WHITE );
//        setShadowRadius( Utils.dp2Px( getContext(), 8 ) );
//        setShadowColor( getResources().getColor( R.color.colorShadow ) );
//        setShadowOffsetX(  Utils.dp2Px( getContext(), 4 ) );
//        setShadowOffsetY(  Utils.dp2Px( getContext(), 4 ) );
//        //最小高度
//        setMinimumHeight( Utils.dp2Px( getContext(), 70 ) );
        setBackgroundColor( Color.WHITE );
        setFocusable( true );
        setClickable( true );
        initView();
        initData();
    }

    private void initView() {
        initImg();
        initCenterLayout();
        initBottomName();
    }

//    private static final AnswerMediaXManage mAMXM = new AnswerMediaXManage();
    public void recycle() {
//        mAMXM.recycle( mMediaX );
    }
    private void initData() {
        mCnPinYinList = new ArrayList<>();
        mCnPinYinAdapter = new ChinesePinYinAdapter( mCnPinYinList );
        mGlide = Glide.with( this );
//        mMediaX = new MediaX( getContext() );
        mMediaXC = MediaXC.get();
        mMediaXCTag = mMediaXC.createTag();

        mCnPinYinAdapter.setEnableTouchStyle( false );
        rvCenterText.setAdapter( mCnPinYinAdapter );

        mCnPinYinAdapter.setOnItemTouchListener((view, ev) -> {
            int action = ev.getAction();
            boolean isUp = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL;
            //触发点击效果
            setPressed( !isUp );
            if( isUp ) performClick();
            return false;
        });
    }

    private void initImg() {
        ivImg = new ImageView( getContext() );
        int mar = Utils.dp2Px( getContext(), 8 );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        lp.setMargins( mar, mar, mar, mar );
        lp.gravity = Gravity.CENTER;
        ivImg.setLayoutParams( lp );
        ivImg.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        ivImg.setVisibility( GONE );
        addView( ivImg );
    }

    private void initCenterLayout() {
        rvCenterText = new RecyclerView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        
        llCenter = new LinearLayout( getContext() );
        llCenter.setLayoutParams( lp );
        llCenter.setOrientation( LinearLayout.VERTICAL );
        llCenter.setGravity( Gravity.CENTER_HORIZONTAL );
        setCenterTextLayout();

        rvCenterText.setLayoutParams( new ViewGroup.LayoutParams( lp ) );
        rvCenterText.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        rvCenterText.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        rvCenterText.setLayoutManager( new FlowLayoutManager() );
//        rvCenterText.setLayoutManager( new LinearLayoutManager(
//                getContext(), RecyclerView.HORIZONTAL, false )
//        );
        llCenter.addView( rvCenterText );
        llCenter.setVisibility( GONE );
        addView( llCenter );
    }

//    public AnswerOptions initMediaX(MediaX mediaX) {
//        mMediaX = mediaX;
//        return this;
//    }

    public void reset() {
//        if( mMediaX != null ) mMediaX.reset();
        mMediaXC.reset( mMediaXCTag );
    }

    public void release() {
//        if( mMediaX != null ) mMediaX.release();
    }

    public void stop() {
//        if( mMediaX != null ) mMediaX.stop();
//        mMediaXC.stop( mMediaXCTag );
    }

    public void pause() {
//        if( mMediaX != null ) mMediaX.pause();
//        mMediaXC.pause( mMediaXCTag );
    }

    public void setSpeed(float speed) {
//        if( mMediaX != null ) mMediaX.setSpeed( speed );
        mMediaXC.setSpeed( speed );
    }

    public void setLeftTextLayout() {
        LayoutParams lp = (LayoutParams) llCenter.getLayoutParams();
        lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        lp.setMarginStart( Utils.dp2Px( getContext(), 5 ) );
        llCenter.requestLayout();
        if( mCnPinYinAdapter != null ) mCnPinYinAdapter.switchTextGravityOfLeft();
    }

    public void setCenterTextLayout() {
        LayoutParams lp = (LayoutParams) llCenter.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        lp.setMarginStart( 0 );
        llCenter.requestLayout();
        if( mCnPinYinAdapter != null ) mCnPinYinAdapter.switchTextGravityOfDefault();
    }

    private void initBottomName() {
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        int mar = Utils.dp2Px( getContext(), 8 );

        llBottomTextLayout = (LinearLayout)
                View.inflate( getContext(), R.layout.block_options_img_btm_text, null );
        ivBottomImg = llBottomTextLayout.findViewById( R.id.opt_img_btm_text_iv_img );
        tvBottomText = llBottomTextLayout.findViewById( R.id.opt_img_btm_text_tv_name );
        lp.setMargins( mar, mar, mar, 0 );
        addView( llBottomTextLayout, lp );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if( !mEnableSelect ) return super.dispatchTouchEvent(ev);
        if( ev.getAction() == MotionEvent.ACTION_UP ) {
//            performClick();
            if( mEnableClick ) switchDefaultStyle( true );
            if( mOnClickListener != null ) mOnClickListener.onClick( this );
            mMediaXC.play( mMediaXCTag, mText );
//            if( mMediaX != null && !TextUtils.isEmpty( mText ) ) {
//                mMediaX.seekTo( 0 );
//                post( () -> mMediaX.play() );
//            }

//            if( mText.startsWith("http") ) {
////            mMediaX.setDataSource( mText );
//                mMediaX.play();
//            }else {
////                if( mTts == null ) mTts = SpeechSynthesizer.get().init( getContext() ).build();
////                mTts.start( mText );
//            }

//            if( LogUtil.isDebug() && !TextUtils.isEmpty( mText ) ) {
//                LogUtil.e( "AnswerOptions -> Url:" + mText);
//                ToastManage.get().showToastForLong(
//                        getContext(), "[Debug]音频内容：" +
//                                (
//                                        mText.startsWith("http") ?
//                                                mText.substring( mText.lastIndexOf("/") + 1 ) :
//                                                mText
//                                ),
//                        new Build().setGravity( Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM )
//                );
//            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private OnClickListener mOnClickListener;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
//        super.setOnClickListener( l );
    }

    public AnswerOptions setEnablePinYinZoom(boolean enable) {
        mCnPinYinAdapter.setEnablePinYinZoom( enable );
        return this;
    }

    @ColorRes
    private int mCurrentColor;
    private void switchStyle(boolean isTouch, @ColorRes int color, @DrawableRes int bg) {
        if( !mEnableTouchStyle ) return;
        this.isTouch = isTouch;
        if( mData == null ) return;
        mCurrentColor = color;
        if( bg != 0 ) setBackgroundResource( bg );
        if( bg == R.drawable.ic_answer_options_select_bg) {
            mCnPinYinAdapter.switchCorrectStatus();
        }else if( bg == R.drawable.ic_answer_options_error_bg ) {
            mCnPinYinAdapter.switchErrorStatus();
        }else {
            if( isTouch ) {
                mCnPinYinAdapter.switchCorrectStatus();
            }else {
                mCnPinYinAdapter.switchNormalStatus();
            }
        }
        tvBottomText.setTextColor( getResources().getColor( color ) );
    }

    @ColorRes
    public int getCurrentColor() { return mCurrentColor; }

    public void switchDefaultStyle(boolean isTouch, boolean isShowSelect) {
//        if( !isTouch && mCurrentColor == R.color.colorMain ) return;
        switchStyle(
                isTouch,
                isTouch ? R.color.colorMain : R.color.color66,
                isShowSelect ? ( isTouch ?
                        R.drawable.ic_answer_options_select_bg :
                        R.drawable.ic_answer_options_def_bg ) :
                        0
        );
    }

    public void switchDefaultStyle(boolean isTouch) {
        switchDefaultStyle( isTouch, true );
    }

    public void switchCompleteStyle() {
        switchDefaultStyle( true, false );
    }

    public void switchSelectStyle() { switchDefaultStyle( true ); }

    public void switchCorrectStyle() {
        switchStyle( true, R.color.colorMain, R.drawable.ic_answer_options_correct_bg );
    }

    public void switchErrorStyle() {
        switchStyle( true, R.color.colorError, R.drawable.ic_answer_options_error_bg );
    }

    public boolean isEnableTouchStyle() { return mEnableTouchStyle; }
    public AnswerOptions setEnableTouchStyle(boolean enable) {
        mEnableTouchStyle = enable;
        return this;
    }

    public boolean isEnableSelect() { return mEnableSelect; }
    public AnswerOptions setEnableSelect(boolean enable) {
        mEnableSelect = enable;
        return this;
    }

    public boolean isEnableClickStyle() { return mEnableClick; }
    public AnswerOptions setEnableClickStyle(boolean enable) {
        mEnableClick = enable;
        return this;
    }

    public AnswerOptions asItemType() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ivBottomImg.getLayoutParams();
        lp.topMargin = Utils.dp2Px( getContext(), 20 );
        lp.weight = 2;
        ivBottomImg.setLayoutParams( lp );
        ivBottomImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE );
        tvBottomText.setTextSize( 18 );
        return this;
    }

    public boolean isTouch() { return isTouch; }

//    public AnswerOptions setSpeechSynthesizer(SpeechSynthesizer synthesizer) {
//        mTts = synthesizer;
//        return this;
//    }

    public AnswerOptions setData(OptionsEntity data) {
        if( data == null ) return this;
        mData = data;
//        mMediaXC.addOnMediaStatusListener( mMediaXCTag, new MediaXStatusAdapter(){
//            @Override
//            public boolean onError(int what, int extra) {
//                LogUtil.e( "AnswerOptions -> onError -> " +
//                        "what:" + what + " | " +
//                        "extra:" + extra + " " +
//                        "text:" + mText
//                );
//                if( TextUtils.isEmpty( mText ) ) {
//                    post( () -> ToastManage.get().showToast( getContext(), R.string.stringError ) );
//                    return super.onError( what, extra );
//                }
//                return super.onError( what, extra );
//            }
//        });
//        if( mMediaX == null ) {
//            mMediaX = mAMXM.get( getContext() );
//            mMediaX.setOnMediaStatusListener(new MediaXStatusAdapter(){
//                @Override
//                public boolean onError(int what, int extra) {
//                    LogUtil.e( "AnswerOptions -> onError -> " +
//                            "what:" + what + " | " +
//                            "extra:" + extra + " " +
//                            "text:" + mText
//                    );
//                    if( TextUtils.isEmpty( mText ) ) {
//                        post( () -> ToastManage.get().showToast( getContext(), R.string.stringError ) );
//                        return super.onError( what, extra );
//                    }
////                    if( mMediaX != null ) mMediaX.reset();
//                    if( mMediaX == null ) {
//                        post(() -> {
//                            try {
//                                mMediaX.setDataSource( mText );
//                                mMediaX.play();
//                            }catch(Exception e) {
//                                e.printStackTrace();
//                            }
//                        });
//                    }else {
//                        LogUtil.e( "AnswerOptions -> setData -> mediaX is null" );
//                    }
//                    return super.onError( what, extra );
//                }
//            });
//        }

        mText = mData.getAudioUrl();
        if( TextUtils.isEmpty( mText ) ) {
            switch ( mData.getOptType() ) {
                case OptionsType.CENTER_TEXT:
                case OptionsType.CENTER_TEXT_AND_IMAGE:
                case OptionsType.CENTER_TEXT_AND_PINYIN:
                case OptionsType.BOTTOM_TEXT_AND_IMAGE:
                    mText = mData.getDataString();
                    break;
            }
        } else {
//            if( mMediaX != null && !TextUtils.isEmpty( mText ) ) {
//                mMediaX.setDataSource( mText );
//            }
        }
        if( TextUtils.isEmpty( mText ) ) {
            LogUtil.e( "AnswerOptions -> setData -> Null data:" + mData );
        }
//        if( mMediaX != null ) mMediaX.setDataSource( mText );
//        LogUtil.e( "AnswerOptions -> setData -> mData:" + mData);
        return this;
    }

    public OptionsEntity getData() { return mData; }

    public void build() {
        refreshData();
        refreshUI();
        switchDefaultStyle( false );
    }

    public void postBuild() {
        postRefreshData();
        postRefreshUI();
        switchDefaultStyle( false );
    }

    void postRefreshData() { post(this::refreshData); }
    void refreshData() {
        if( mData == null ) return;
        //选项类型
        mOptType = mData.getOptType();
        switch ( mOptType ) {
            case OptionsType.CENTER_TEXT:                       //中间文本
            case OptionsType.CENTER_PINYIN:                     //中间拼音
            case OptionsType.CENTER_TEXT_AND_PINYIN:            //中间文本和拼音
                doCenterData(
                        mOptType != OptionsType.CENTER_TEXT,
                        mOptType != OptionsType.CENTER_PINYIN
                );
                break;
            case OptionsType.IMAGE:                             //纯图片
            case OptionsType.CENTER_PINYIN_AND_IMAGE:           //中间拼音和图片
            case OptionsType.CENTER_TEXT_AND_IMAGE:             //中间文本和图片
            case OptionsType.BOTTOM_TEXT_AND_IMAGE:             //底部文本和图片
            case OptionsType.BOTTOM_PINYIN_AND_IMAGE:           //底部拼音和图片
                switch ( mOptType ) {
                    case OptionsType.CENTER_PINYIN_AND_IMAGE:
                    case OptionsType.CENTER_TEXT_AND_IMAGE:
                        doCenterData(
                                mOptType == OptionsType.CENTER_PINYIN_AND_IMAGE,
                                mOptType == OptionsType.CENTER_TEXT_AND_IMAGE
                        );
                        break;
                    case OptionsType.BOTTOM_TEXT_AND_IMAGE:
                    case OptionsType.BOTTOM_PINYIN_AND_IMAGE:
                        doBottomData( mOptType == OptionsType.BOTTOM_PINYIN_AND_IMAGE );
                        break;
                }
                doImage();
                break;
        }
    }

    void postRefreshUI() { post(this::refreshUI); }
    void refreshUI() {
        switch ( mOptType ) {
            case OptionsType.CENTER_TEXT:                       //中间文本
            case OptionsType.CENTER_PINYIN:                     //中间拼音
            case OptionsType.CENTER_TEXT_AND_PINYIN:            //中间文本和拼音
            case OptionsType.CENTER_PINYIN_AND_IMAGE:           //中间拼音和图片
            case OptionsType.CENTER_TEXT_AND_IMAGE:             //中间文本和图片
                boolean enablePinYin = mOptType != OptionsType.CENTER_TEXT &&
                        mOptType != OptionsType.CENTER_TEXT_AND_IMAGE;
                boolean enableText = mOptType != OptionsType.CENTER_PINYIN &&
                        mOptType != OptionsType.CENTER_PINYIN_AND_IMAGE;

                setEnableChinesePinYin( enablePinYin, enableText );

                llCenter.setVisibility( VISIBLE );

                ivImg.setVisibility(
                        mOptType == OptionsType.CENTER_PINYIN_AND_IMAGE ||
                                mOptType == OptionsType.CENTER_TEXT_AND_IMAGE ?
                                VISIBLE : GONE
                );
                llBottomTextLayout.setVisibility( GONE );
                break;
            case OptionsType.IMAGE:                             //纯图片
                llCenter.setVisibility( GONE );
                ivImg.setVisibility( VISIBLE );
                llBottomTextLayout.setVisibility( GONE );
                break;
            case OptionsType.BOTTOM_TEXT_AND_IMAGE:             //底部文本和图片
            case OptionsType.BOTTOM_PINYIN_AND_IMAGE:           //底部拼音和图片
                llCenter.setVisibility( GONE );
                ivImg.setVisibility( VISIBLE );
                llBottomTextLayout.setVisibility( VISIBLE );
                break;
        }
    }

    private void doBottomData(boolean isPinYin) {
        String[] arr = isPinYin ? mData.getPinyin() : mData.getData();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append( arr[ i ] );
            if( isPinYin && i != arr.length - 1 ) sb.append(" ");
        }
        tvBottomText.setText( sb.toString() );
    }


    /**
     * 处理中间文本和拼音
     */
    private void doCenterData(boolean enablePinYin, boolean enableText) {
//        String[] dataArr = mData.getData();
//        String[] pinyinArr = mData.getPinyin();
//        if( dataArr == null || pinyinArr == null ) return;
//        mCnPinYinList.clear();
//        for (int i = 0; i < pinyinArr.length; i++) {
//            ChinesePinYinAdapter.ItemData itemData = new ChinesePinYinAdapter.ItemData();
//            //设置汉字
//            if( enableText && i < dataArr.length ) itemData.setText( dataArr[ i ] );
//            //设置拼音
//            if( enablePinYin ) itemData.setPinyin( pinyinArr[ i ] );
//            mCnPinYinList.add( itemData );
//        }
        mCnPinYinAdapter.clearItemData();
        mCnPinYinAdapter.setChinesePinYin(
                mData.getPinyin(), enablePinYin,
                mData.getData(), enableText
        );
        mCnPinYinAdapter.notifyDataSetChanged();
    }

    private void doImage() {
        ImageView iv;
        RequestBuilder<Drawable> imgBuild;
        switch ( mOptType ) {
            case OptionsType.BOTTOM_PINYIN_AND_IMAGE:
            case OptionsType.BOTTOM_TEXT_AND_IMAGE:
                iv = ivBottomImg;
                break;
            default:
                iv = ivImg;
                break;
        }
        if( mData.getImgRes() != 0 ) {
            imgBuild = mGlide.load( mData.getImgRes() );
        }else {
            imgBuild = mGlide.load( mData.getImgUrl() );
        }
        imgBuild.into( iv );
    }

    private void setEnableChinesePinYin(boolean enablePinYin, boolean enableText) {
        if( mCnPinYinList.size() == 0 ) return;
        for( ChinesePinYinAdapter.ItemData data : mCnPinYinList ) {
            data.setEnablePinYin( enablePinYin );
            data.setEnableText( enableText );
        }
        mCnPinYinAdapter.notifyDataSetChanged();
    }
}