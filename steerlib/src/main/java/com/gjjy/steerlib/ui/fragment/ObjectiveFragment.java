package com.gjjy.steerlib.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybutils.utils.ResUtil;
import com.gjjy.steerlib.R;
import com.gjjy.steerlib.adapter.ListAdapter;
import com.gjjy.steerlib.ui.activity.GuideActivity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.ui.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * 学习目的页面
 */
public class ObjectiveFragment extends BaseFragment {
    private RecyclerView rvList;

    private ListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_objective, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        rvList = findViewById( R.id.objective_rv_list );
    }

    private void initData() {
        if( getContext() == null ) return;
        mAdapter = new ListAdapter( new ArrayList<>(), ListAdapter.ItemType.GOAL_TYPE );
        if( rvList != null ) {
            rvList.setLayoutManager( new LinearLayoutManager( getContext() ) );
            rvList.setAdapter( mAdapter );
        }

        for (int i = 1; i <= 7; i++) {
            ListAdapter.ItemData item = new ListAdapter.ItemData();
            item.setIcon(
                    ResUtil.getDrawable( getContext(), "ic_objective_item_bg_" + i )
            );
            item.setTitle( ResUtil.getString( getContext(), "stringObjectiveItemTitle" + i) );
            item.setType( i );
            mAdapter.addItemData( item );
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        mAdapter.setOnItemClickListener((adapter, view, data, position) -> {
            GuideActivity activity = ((GuideActivity) getActivity());
            if( activity == null ) return;
            int type = data.getType();
            activity.onSelectedType( 2, type );
            BuriedPointEvent.get().onPurposePageOfPurposeSelection( activity, type );
        });
    }
}
