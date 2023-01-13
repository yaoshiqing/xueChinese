package com.gjjy.discoverylib.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.bumptech.glide.Glide;
import com.gjjy.discoverylib.mvp.presenter.TargetedLearningPresenter;
import com.gjjy.discoverylib.mvp.view.TargetedLearningView;
import com.ybear.mvp.annotations.Presenter;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.TargetedLearningListAdapter;
import com.gjjy.discoverylib.utils.StartUtil;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.DOMConstant;

import java.util.ArrayList;
import java.util.List;

/**
 探索 - 专项学习
 */
public class TargetedLearningFragment extends BaseFragment implements TargetedLearningView {
    @Presenter
    private TargetedLearningPresenter mPresenter;

    private TextView tvMoreBtn;
    private RecyclerView rvList;
    private TargetedLearningListAdapter mAdapter;

    private TargetedLearningListAdapter.ItemData mSelectedItemData = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_targeted_learning, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if( requestCode == com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_BUY_VIP ) {
            if( mSelectedItemData == null ) return;
            boolean isResult = resultCode == Activity.RESULT_OK;
            if( isResult ) {
                StartUtil.startTargetedLearningDetailsActivity( mSelectedItemData.getId(),mSelectedItemData.getVideoId() );
            }
            mPresenter.buriedPointUnlockCourse(mSelectedItemData.getId(), mSelectedItemData.getTitle(), isResult);
        }
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        if( id == DOMConstant.NOTIFY_DISCOVERY_LIST && data != null && (int)data == 1 ) {
            post( () -> mPresenter.queryDataList( false ), 1200 );
        }
    }

    private void initView() {
        rvList = findViewById( R.id.targeted_learning_rv_list );
        tvMoreBtn = findViewById( R.id.targeted_learning_tv_more_btn );
    }

    private void initData() {
        mAdapter = new TargetedLearningListAdapter( Glide.with( this ), new ArrayList<>() );

        rvList.setLayoutManager( new LinearLayoutManager( getContext() ) );
        rvList.setAdapter( mAdapter );
    }

    private void initListener() {
        tvMoreBtn.setOnClickListener( view -> {
            StartUtil.startTargetedLearningVoiceMoreListActivity();
            mPresenter.buriedPointOpenMore();
        });

        mAdapter.setOnItemClickListener( (adapter, view, data, i) -> {
            BaseActivity activity = (BaseActivity) getActivity();
            if( activity == null ) return;

            mSelectedItemData = data;

            mPresenter.buriedPointSelectedCourse( data.getId(), data.getTitle() );

            //Vip课程需要开通会员
            if( data.isVip() && !mPresenter.isVip() ) {
                activity.showUnlockVipDialog(
                        mPresenter.getUid(), mPresenter.getUserName(),
                        1003, data.getId(), data.getTitle(),
                        data.getImgUrl(), data.getTitle(), data.getContent()
                );
                return;
            }
            StartUtil.startTargetedLearningDetailsActivity( data.getId(),data.getVideoId() );
        } );

        //数据发生改变
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onChanged() {
                super.onChanged();
                FragmentActivity activity = getActivity();
                if( activity == null ) return;

                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                boolean isNotData = mAdapter.getItemCount() == 0;
                if( isNotData ) {
                    ft.hide( getThis() );
                }else {
                    ft.show( getThis() );
                }
                ft.commitAllowingStateLoss();
                rvList.setVisibility( isNotData ? View.GONE : View.VISIBLE );
            }
        });
    }

    public void notifyUpdatedData() {
        mPresenter.queryDataList( false );
    }

    @Override
    public void onCallDataList(List<TargetedLearningListAdapter.ItemData> list) {
        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();
    }
}
