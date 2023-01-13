package com.gjjy.frontlib.mvp.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.frontlib.annotations.PagerType;
import com.gjjy.frontlib.entity.PagerEntity;
import com.gjjy.frontlib.ui.fragment.HearingFragment;
import com.gjjy.frontlib.ui.fragment.LabelHearingFragment;
import com.gjjy.frontlib.ui.fragment.LabelTranslateFragment;
import com.gjjy.frontlib.ui.fragment.MatchFragment;
import com.gjjy.frontlib.ui.fragment.NotDataFragment;
import com.gjjy.frontlib.ui.fragment.SelectFragment;
import com.gjjy.frontlib.ui.fragment.TranslateFragment;
import com.gjjy.frontlib.ui.fragment.VideoFragment;
import com.gjjy.frontlib.ui.fragment.VoiceFragment;
import com.ybear.mvp.model.MvpModel;
import com.ybear.mvp.view.fragment.MvpFragment;

public class AnswerHelperModel extends MvpModel {
    @Nullable
    private MvpFragment createMvpFragment(PagerEntity pagerData) {
        if( pagerData == null ) return null;

        MvpFragment fragment = null;
        switch ( pagerData.getPagerType() ) {
            case PagerType.NOT_PAGER:           //空列表
                fragment = new NotDataFragment();
                break;
            case PagerType.HEARING:             //听力
                fragment = new HearingFragment();
                break;
            case PagerType.VOICE:               //口语
                fragment = new VoiceFragment();
                break;
            case PagerType.SELECT:              //选择
                fragment = new SelectFragment();
                break;
            case PagerType.MATCH:               //匹配
                fragment = new MatchFragment();
                break;
            case PagerType.TRANSLATE:           //翻译
                fragment = new TranslateFragment();
                break;
            case PagerType.LABEL_TRANSLATE:     //翻译标签
                fragment = new LabelTranslateFragment();
                break;
            case PagerType.LABEL_HEARING:       //听力标签
                fragment = new LabelHearingFragment();
                break;
            case PagerType.VIDEO:               //视频
                fragment = new VideoFragment();
                break;
        }
        return fragment;
    }

    @UiThread
    public MvpFragment createMvpFragmentAndData(PagerEntity data,
                                                 @NonNull Bundle arg,
                                                 AnswerBaseEntity answerBaseEntity,
                                                 int answerType) {
        MvpFragment fragment = createMvpFragment( data );
        if( fragment == null ) return null;
        //题目数据
        arg.putParcelable( Constant.PAGER_DATA, data );
        //题目类型
        arg.putInt( Constant.ANSWER_TYPE, answerType );
        //基础数据
        arg.putParcelable( Constant.ANSWER_BASE_ENTITY, answerBaseEntity );
        fragment.setArguments( arg );
        return fragment;
    }
}
