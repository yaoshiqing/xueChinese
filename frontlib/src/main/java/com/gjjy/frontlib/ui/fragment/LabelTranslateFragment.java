package com.gjjy.frontlib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.LabelDragAdapter;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.mvp.presenter.LabelTranslatePresenter;
import com.gjjy.frontlib.mvp.view.LabelTranslateView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.widget.drag.DragRecyclerView;
import com.gjjy.basiclib.widget.drag.DragViewPool;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签题-翻译
 */
public class LabelTranslateFragment extends BaseFragment
        implements LabelTranslateView, OnVisibleChangedListener {
    @Presenter
    private LabelTranslatePresenter mPresenter;

    private TextView tvTitle;
    private TextView tvQuestion;
    private DragRecyclerView drvAnswer;
    private DragRecyclerView drvOptions;
    private CheckButton cbCheckBtn;

    private DragViewPool mDragPool;
    private LabelDragAdapter mAnswerAdapter;
    private LabelDragAdapter mOptionsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_label_translate, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        mPresenter.refreshOptLayout();
        mPresenter.onPagerHiddenStatus( false );
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onPagerHiddenStatus( true );
    }

    @Override
    public void dispatchTouchEvent(MotionEvent ev) {
        mDragPool.dispatchTouchEvent( ev );
    }

    private void initView() {
        tvTitle = findViewById( R.id.label_translate_tv_title );
        tvQuestion = findViewById( R.id.label_translate_tv_question );
        drvAnswer = findViewById( R.id.label_translate_drv_answer_pool );
        drvOptions = findViewById( R.id.label_translate_drv_options_pool );
        cbCheckBtn = findViewById( R.id.label_translate_cb_check_btn );
    }

    private void initData() {
        mDragPool = DragViewPool.create();
        mAnswerAdapter = new LabelDragAdapter( new ArrayList<>() );
        mOptionsAdapter = new LabelDragAdapter( new ArrayList<>() );
        mOptionsAdapter.setIsOptions( true );

        drvAnswer.setAdapter( mAnswerAdapter );
        drvOptions.setAdapter( mOptionsAdapter );

        mDragPool.registerView((ViewGroup) getView(), drvAnswer, drvOptions );
        mDragPool.setEnablePoolDrag( false );

        drvAnswer.setMinLineCount( 2 );

        drvOptions.bindMoveView( drvAnswer );
        drvOptions.setEnableDrag( false );
    }

    private void initListener() {
        cbCheckBtn.setOnClickListener(v -> mPresenter.check());

        mDragPool.setOnItemStatusListener((data, isShowItem) -> {
            if( data == null ) return;
            ((LabelDragAdapter.ItemData)data).setShow( isShowItem );
        });

        mAnswerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if( mPresenter.isShowCheck() ) {
                    if( mAnswerAdapter.getItemCount() > 0 && !cbCheckBtn.isEnabled() ) {
                        cbCheckBtn.setEnabled( true );
                    }
                }else {
                    check();
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if( mAnswerAdapter.getItemCount() == 0 && cbCheckBtn.isEnabled() ) {
                    cbCheckBtn.setEnabled( false );
                }
            }
        });

        drvOptions.setOnDoBindViewListener((data, position) -> {
            if( mPresenter.isShowCheck() ) return true;

            if( mAnswer == null ) mAnswer = mPresenter.getAnswer();
            LabelDragAdapter.ItemData item = (LabelDragAdapter.ItemData) data;
            String strData = item.getDataString();
            if( mAnswer.startsWith( strData ) ) {
                mAnswer = mAnswer.substring( strData.length() );
                post(() -> {
                    LabelDragAdapter.ItemData selectItem = mAnswerAdapter.getItemDataOfLast();
                    if( selectItem == null ) return;
                    selectItem.setCorrectTouchStyle();
                    mAnswerAdapter.notifyItemChanged( mAnswerAdapter.getItemCount() - 1 );
                });
                return true;
            }

            ((LabelDragAdapter.ItemData) data).setErrorTouchStyle();
            mOptionsAdapter.notifyItemChanged( position );
            post(() -> {
                ((LabelDragAdapter.ItemData) data).setDefTouchStyle();
                mOptionsAdapter.notifyItemChanged( position );
            }, 1000);
            return false;
        });

        mOptionsAdapter.setOnItemClickListener(
                (adapter, view, itemData, i) -> mOptionsAdapter.play( itemData )
        );
    }

    private void check() {
        mAnswerAdapter.stopNow();
        mOptionsAdapter.stopNow();
        mPresenter.check();
    }

    private String mAnswer;

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        mPresenter.doArguments( args );
    }

    @Override
    public void onCallTitle(String title) {
        if( !TextUtils.isEmpty( title ) ) tvTitle.setText( title );
    }

    @Override
    public void onCallQuestion(String question) { tvQuestion.setText( question ); }

    @Override
    public void onCallOptionsLayout(int layout, OptionsEntity... opts) {
        if( opts == null || opts.length == 0 ) return;
        List<LabelDragAdapter.ItemData> list = new ArrayList<>();
        for ( OptionsEntity opt : opts) list.add( new LabelDragAdapter.ItemData( opt ) );
        mOptionsAdapter.clearItemData();
        mOptionsAdapter.addItemData( list );
        mOptionsAdapter.notifyDataSetChanged();
    }

    @Override
    public List<LabelDragAdapter.ItemData> onCallDataList() {
        return mAnswerAdapter.getDataList();
    }

    @Override
    public void onCallOptionsError() {  }

    @Override
    public void onCheckVisibility(int visibility) {
        cbCheckBtn.setVisibility( visibility );
        if( mPresenter.isShowCheck() ) {
            drvAnswer.bindMoveView( drvOptions );
        }else {
            drvAnswer.setEnableDrag( false );
        }
    }
}
