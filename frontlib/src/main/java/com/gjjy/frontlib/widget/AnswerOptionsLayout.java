package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.Utils;
import com.gjjy.frontlib.R;
import com.gjjy.basiclib.utils.Constant;

import java.util.Arrays;

import static com.gjjy.basiclib.utils.Constant.AnswerType.ERROR_MAP;
import static com.gjjy.basiclib.utils.Constant.AnswerType.FAST_REVIEW;

/**
 * 选项组布局
 *
 * 来自开发的建议：
 * 观看代码前请做好心理准备，建议在家长陪同下观看。
 */
public class AnswerOptionsLayout extends FrameLayout {
    public interface OnOptClickListener {
        void onClick(ViewGroup vg, View v, OptionsEntity data, int position);
    }

    private int mSelectPosition = -1;
    private OptionsEntity mSelectData = null;
    private AnswerOptions aoSelectOpt = null;
    private boolean defaultStyleIsCorrect = false;

    @Constant.AnswerType
    private int mAnswerType = Constant.AnswerType.NORMAL;

    private OnOptClickListener mOnOptClickListener;

    public AnswerOptionsLayout(Context context) {
        this(context, null);
    }

    public AnswerOptionsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerOptionsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ViewGroup vLinear, vGrid;
    @OptionsLayout
    private int mOptLayout = OptionsLayout.NONE;

    private void init() {
        addView( vLinear = (ViewGroup) LayoutInflater.from( getContext() )
                .inflate( R.layout.block_options_linear, this, false )
        );
        vLinear.setVisibility( GONE );
        addView( vGrid = (ViewGroup) LayoutInflater.from( getContext() )
                .inflate( R.layout.block_options_grid, this, false )
        );
        vGrid.setVisibility( GONE );
    }

    public void setOnOptClickListener(OnOptClickListener l) { mOnOptClickListener = l; }

    public void addData(@OptionsLayout int optionsLayout, OptionsEntity... options) {
        LogUtil.e( "addData -> layout:" + optionsLayout + " | " + Arrays.toString( options ) );
        if( options == null ) return;
        mOptLayout = optionsLayout;
        ViewGroup vg = getOptLayout( optionsLayout );
        if( vg == null ) return;
        //填充数据
        for (int i = 0; i < options.length; i++) {
            AnswerOptions aoOpt = i >= vg.getChildCount() ? null : (AnswerOptions) vg.getChildAt( i );
            switch( optionsLayout ) {
                case OptionsLayout.LINEAR:
                case OptionsLayout.LINEAR_GRID:
                    addLinearForOpt( vg, aoOpt, options[ i ], i );
                    break;
                default:
                    if( optionsLayout == OptionsLayout.VIDEO_GRID ) {
                        ViewGroup.LayoutParams lp = vg.getLayoutParams();
                        lp.height = Utils.dp2Px( getContext(), 229 );
                        vg.setLayoutParams( lp );
                        for (int j = 0; j < vg.getChildCount(); j++) {
                            View v = vg.getChildAt( j );
                            lp = v.getLayoutParams();
                            lp.height = Utils.dp2Px( getContext(), 96 );
                            v.setLayoutParams( lp );
                        }
                    }
                    if( aoOpt == null ) continue;
                    aoOpt.switchDefaultStyle( false );
                    aoOpt.setEnableClickStyle( false );
                    aoOpt.setData( options[ i ] ).build();
                    aoOpt.setEnablePinYinZoom(
                            optionsLayout == OptionsLayout.VIDEO_GRID ||
                                    optionsLayout == OptionsLayout.GRID
                    );
                    //点击事件
                    doOptClick( vg, aoOpt, options[ i ], i );
                    break;
            }
        }
        //显示指定布局
        showLayout( optionsLayout );
    }

    public void setData(@OptionsLayout int optionsLayout, OptionsEntity... options) {
        LogUtil.e( "addData setData -> layout:" + optionsLayout + " | " + Arrays.toString( options ) );
        ViewGroup vg = getOptLayout( optionsLayout );
        if( vg == null ) return;
        addData( optionsLayout, options );
    }

    public void setEnableChangedTextLayout(boolean enable) { }

    public void setAnswerType(int type) { mAnswerType = type; }

    public void reset() {
        ViewGroup vg = getOptLayout( mOptLayout );
        if( vg == null ) return;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt( i );
            if( v instanceof AnswerOptions ) ((AnswerOptions)v).reset();
        }
    }

    public void release() {
        recycle();
    }

    public void stop() { }

    public void pause() {}

    public void setSpeed(float speed) {
        ViewGroup vg = getOptLayout( mOptLayout );
        if( vg == null ) return;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt( i );
            if( v instanceof AnswerOptions ) ((AnswerOptions)v).setSpeed( speed );
        }
    }

    private void recycle() {
        ViewGroup vg = getOptLayout( mOptLayout );
        if( vg == null ) return;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt( i );
            if( v instanceof AnswerOptions ) ((AnswerOptions)v).recycle();
        }
    }

    public int getItemCount() {
        return getOptLayout( mOptLayout ).getChildCount();
    }

    public void removeItemView(int position) {
        View v = getOptLayout( mOptLayout ).getChildAt( position );
        AnimUtil.setAlphaAnimator( 0F, 100, animator -> v.setVisibility( GONE ), v );
    }
    public void removeSelectView() {
        removeItemView( mSelectPosition );
    }

    private void addLinearForOpt(ViewGroup vg, @Nullable AnswerOptions aoOpt, OptionsEntity data, int pos) {
        if( aoOpt == null ) return;
        aoOpt.switchDefaultStyle( false );
        aoOpt.setEnableClickStyle( true );
        aoOpt.setData( data ).build();
        aoOpt.setCenterTextLayout();
        //点击事件
        doOptClick( vg, aoOpt, data, pos );
    }

    public void switchDefault(AnswerOptions options, boolean isTouch) {
        if( options != null ) options.switchDefaultStyle( isTouch );
    }

    public void switchDefault(boolean isTouch) {
        switchDefault( getTouchOpt(), isTouch );
    }

    public void switchError() {
        AnswerOptions options = getTouchOpt();
        if( options != null ) options.switchErrorStyle();

        if( mAnswerType == FAST_REVIEW || mAnswerType == ERROR_MAP ) {
            postDelayed(() -> switchDefault( options, false ), 800);
        }
    }

    public void switchCorrect() {
        AnswerOptions options = getTouchOpt();
        if( options != null ) options.switchCorrectStyle();
        setEnableSelect( false );
    }

    public void switchCorrectAll() {
        ViewGroup vg = getOptLayout( mOptLayout );
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt( i );
            if( !(v instanceof AnswerOptions ) ) continue;
            ((AnswerOptions)v).switchCorrectStyle();
        }
    }

    public void cancelKeepNotSquareHeight() {}

    public OptionsEntity getSelectData() { return mSelectData; }

    public void cancelSelect() {
        ViewGroup vg = getOptLayout( mOptLayout );
        if( vg == null ) return;
        for (int i = 0; i < vg.getChildCount(); i++) {
            ((AnswerOptions)vg.getChildAt( i )).switchDefaultStyle( false );
        }
        mSelectPosition = -1;
        mSelectData = null;
        aoSelectOpt = null;
    }

    private boolean mEnableSelect = true;
    public void setEnableSelect(boolean enable) {
        mEnableSelect = enable;
        ViewGroup vg = getOptLayout( mOptLayout );
        if( vg == null ) return;
        for (int i = 0; i < vg.getChildCount(); i++) {
            ((AnswerOptions)vg.getChildAt( i )).setEnableSelect( enable );
        }
    }

    private AnswerOptions getTouchOpt() {
        ViewGroup vg = getOptLayout( mOptLayout );
        for (int i = 0; i < vg.getChildCount(); i++) {
            AnswerOptions options = null;
            try {
                options = (AnswerOptions) vg.getChildAt( i );
            }catch(Exception e) {
                e.printStackTrace();
            }
            if( options != null && options.isTouch() ) return options;
        }
        return null;
    }

    private ViewGroup getOptLayout(int optionsLayout) {
        ViewGroup vg = null;
        switch ( optionsLayout ) {
            case OptionsLayout.LINEAR:
            case OptionsLayout.LINEAR_GRID:
                vg = vLinear;
                vLinear.setVerticalScrollBarEnabled( optionsLayout != OptionsLayout.LINEAR_GRID );
                vGrid.setVisibility( View.GONE );
                break;
            case OptionsLayout.GRID:
            case OptionsLayout.VIDEO_GRID:
                vg = vGrid;
                break;
        }
        return vg;
    }

    private void showLayout(@OptionsLayout int optionsLayout) {
        switch ( optionsLayout ) {
            case OptionsLayout.LINEAR:
            case OptionsLayout.LINEAR_GRID:
                ViewGroup.LayoutParams lp = vLinear.getLayoutParams();
                if( optionsLayout == OptionsLayout.LINEAR ) {
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }else {
                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                vLinear.setLayoutParams( lp );
                vLinear.setVisibility( View.VISIBLE );
                vGrid.setVisibility( View.GONE );
                break;
            case OptionsLayout.GRID:
            case OptionsLayout.VIDEO_GRID:
                vLinear.setVisibility( View.GONE );
                vGrid.setVisibility( View.VISIBLE );
                break;
        }
    }

    public void defaultStyleIsCorrect(boolean isDefault) {
        defaultStyleIsCorrect = isDefault;
    }

    private void doOptClick(ViewGroup vg, AnswerOptions options, OptionsEntity data, int position) {
        if( options == null ) return;
        options.setOnClickListener(v -> {
            //取消没有选中的样式
            for (int i = 0; i < vg.getChildCount(); i++) {
                AnswerOptions child = (AnswerOptions) vg.getChildAt( i );
                if( child.equals( options ) ) continue;
                child.switchDefaultStyle( false );
            }

            if( !doClick( options, data, position ) ) return;
        });
    }

    private boolean doClick(AnswerOptions view, OptionsEntity itemData, int position) {
        if( !mEnableSelect ) return false;
        mSelectPosition = position;
        mSelectData = itemData;
        aoSelectOpt = view;
        if( mOptLayout != OptionsLayout.LINEAR && mOptLayout != OptionsLayout.LINEAR_GRID ) {
            if( defaultStyleIsCorrect ) {
                view.switchSelectStyle();
            }else {
                view.switchDefaultStyle( true );
            }
        }

        if( mOnOptClickListener == null ) return true;
        mOnOptClickListener.onClick(
                (ViewGroup) vLinear.getChildAt( 0 ),
                view,
                itemData,
                position
        );
        return true;
    }
}