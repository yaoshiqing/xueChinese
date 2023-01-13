package com.gjjy.discoverylib.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.TargetedLearningDetailsGrammarAdapter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 专项学习 - 语法页面
 */
public class GrammarFragment extends BaseFragment {
    private RecyclerView rvList;

    private TargetedLearningDetailsGrammarAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_grammar, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        rvList = findViewById( R.id.grammar_rv_list );
    }

    private void initData() {
        rvList.setLayoutManager( new LinearLayoutManager( getContext() ) );
        rvList.setNestedScrollingEnabled( false );
        rvList.addItemDecoration( new SpaceItemDecoration( Utils.dp2Px( getContext(), 14 ) ) );

        rvList.setAdapter(
                mAdapter = new TargetedLearningDetailsGrammarAdapter( new ArrayList<>() )
        );
    }

    private void initListener() {

    }

    public void setDataList(List<TargetedLearningDetailsGrammarAdapter.ItemData> list) {
        if( mAdapter == null ) return;
        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();
    }
}
