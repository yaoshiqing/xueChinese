package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.gjjy.frontlib.annotations.OptionsType;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.AnimUtil;

public class MatchCorrectView extends LinearLayout {
    public MatchCorrectView(Context context) {
        this(context, null);
    }

    public MatchCorrectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchCorrectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation( VERTICAL );
    }

    public void addData(OptionsEntity data) {
        OptionsEntity optData = new OptionsEntity( data );
        AnswerOptions lOpt = new AnswerOptions( getContext() )/*.initMediaX( mMediaX )*/;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dp2Px( getContext(), 76 )
        );
//        lp.leftMargin = lp.rightMargin = Utils.dp2Px( getContext(), 33 );
        lp.bottomMargin = Utils.dp2Px( getContext(), 20 );
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lOpt.setLayoutParams( lp );

        addView( lOpt );

        lOpt.setData( optData.setOptType( OptionsType.CENTER_TEXT_AND_PINYIN ) ).build();

        lOpt.setEnableSelect( false );
        lOpt.setEnableClickStyle( false );
        lOpt.switchCompleteStyle();

        lOpt.setVisibility( GONE );

        post(() -> AnimUtil.setAlphaAnimator( 350, lOpt ) );
    }
}
