package com.gjjy.frontlib.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.buried_point.QuestionType;
import com.gjjy.basiclib.dao.entity.SkipAnswerTypeEntity;
import com.gjjy.basiclib.mvp.model.SetUpModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.AnswerSetMenuAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.util.MvpAnn;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.shape.ShapeFrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 答案设置菜单控件
 */
public class AnswerSetMenuView extends ShapeFrameLayout {
    @Model
    private UserModel mUserModel;
    @Model
    private SetUpModel mSetUpModel;
    private AnswerSetMenuAdapter mAdapter;

    public AnswerSetMenuView(Context context) {
        this(context, null);
    }

    public AnswerSetMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerSetMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initListener();
    }

    private void initListener() {
        mAdapter.setOnItemCheckedChangeListener((buttonView, isChecked, data, position) -> {
            mSetUpModel.saveSkipQuestion( position, isChecked );

            String tag = data.getTag();
            if(TextUtils.isEmpty( tag ) ) return;
            BuriedPointEvent.get().onStudyPageOfQuestionTypeSwitch(
                    getContext(),
                    isChecked,
                    tag,
                    mUserModel.isLoginResult() ?
                            PageName.QUESTION_DETAILS :
                            PageName.GUEST_QUESTION_DETAILS

            );
        });
    }

    private void init() {
        MvpAnn.instanceMvpAnn( this );

        int shadow = Utils.dp2Px( getContext(), 3 );
        setMinimumWidth( Utils.dp2Px( getContext(), 216 ) );
        setRadius( Utils.dp2Px( getContext(), 10 ) );
        setBorderSize( Utils.dp2Px( getContext(), 1 ) );
        setBorderColor( getResources().getColor( R.color.colorAnswerSetMenuLine ) );
        setShadowRadius( shadow );
        setShadowColor( getResources().getColor( R.color.colorShadow ) );
        setShadowOffsetY( shadow );
        setBackgroundColor( Color.WHITE );
        int p = Utils.dp2Px( getContext(), 13 );
        setPadding( p + p, p , p + p, p );
        List<AnswerSetMenuAdapter.ItemData> dataList = new ArrayList<>();
        mAdapter = new AnswerSetMenuAdapter( dataList );

        RecyclerView rvList = new RecyclerView(getContext());
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        rvList.setLayoutParams( lp );
        rvList.setLayoutManager( new LinearLayoutManager( getContext() ) );
        rvList.setAdapter( mAdapter );
        addView(rvList);

        //创建数据
        createData( dataList );
    }

    private void createData(@NonNull List<AnswerSetMenuAdapter.ItemData> list) {
        int[] iconRes = {
                R.drawable.ic_answer_set_hearing,
                R.drawable.ic_answer_set_voice,
                R.drawable.ic_answer_set_translate,
                R.drawable.ic_answer_set_sound,
                R.drawable.ic_answer_set_snail,
        };
        int[] strRes = {
                R.string.stringHearing,
                R.string.stringVoice,
                R.string.stringTranslate,
                R.string.stringSound,
                R.string.stringSlowAudio,
        };

        String[] tags = {
                QuestionType.LISTENING,
                QuestionType.SPEAKING,
                QuestionType.TRANSLATING,
                null,
                QuestionType.SLOW_AUDIO,
        };

        SkipAnswerTypeEntity sq = mSetUpModel.getSkipQuestion();
        boolean[] enables = {
                sq.isHearing(),
                sq.isVoice(),
                sq.isTranslate(),
                sq.isSound(),
                sq.isSnail()
        };
        for (int i = 0; i < iconRes.length; i++) {
            AnswerSetMenuAdapter.ItemData item = new AnswerSetMenuAdapter.ItemData();
            item.setIconRes( iconRes[ i ] );
            item.setName( getResources().getString( strRes[ i ] ) );
            item.setTag( tags[ i ] );
            item.setEnable( enables[ i ] );
            list.add( item );
        }
        mAdapter.notifyDataSetChanged();
    }
}
