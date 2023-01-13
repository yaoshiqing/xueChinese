package com.gjjy.discoverylib.ui.fragment;

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
import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;
import com.gjjy.discoverylib.mvp.presenter.PopularVideosPresenter;
import com.gjjy.discoverylib.mvp.view.PopularVideosView;
import com.ybear.mvp.annotations.Presenter;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.utils.StartUtil;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.DOMConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 探索 - 热门视频
 */
public class PopularVideosFragment extends BaseFragment implements PopularVideosView {
    @Presenter
    private PopularVideosPresenter mPresenter;

    private RecyclerView rvList;
    private TextView tvMoreBtn;

    private DiscoveryListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_popular_videos, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        if( id == DOMConstant.NOTIFY_DISCOVERY_LIST && data != null && (int)data == 1 ) {
            post( () -> mPresenter.queryDataList( false ), 800 );
        }
    }

    private void initView() {
        rvList = findViewById( R.id.popular_videos_rv_list );
        tvMoreBtn = findViewById( R.id.popular_videos_tv_more_btn );
    }

    private void initData() {
        mPresenter.setIsMoreList( false );
        mAdapter = new DiscoveryListAdapter(
                (BaseActivity) getActivity(),
                Glide.with( this ),
                DiscoveryListAdapter.ListType.MAIN_LIST,
                new ArrayList<>(),
                mPresenter.getUid(),
                mPresenter.getUserName()
        );

        rvList.setLayoutManager(
                new LinearLayoutManager( getContext(), RecyclerView.HORIZONTAL, false )
        );
        rvList.setAdapter( mAdapter );
    }

    private void initListener() {
        tvMoreBtn.setOnClickListener( view -> {
            StartUtil.startPopularVideosMoreListActivity();
            mPresenter.buriedPointOpenMore();
        });

        mAdapter.setOnItemClickListener((adapter, view, itemData, i) ->
                mPresenter.buriedPointOpenItem( itemData.getId(), itemData.getTitle() )
        );

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
    public void onCallDataList(List<DiscoveryListAdapter.ItemData> list, boolean isPaging) {
        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCallIsVip(boolean isVip) {
        mAdapter.setVip( isVip );
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        //Fragment不需要加载动画
    }
}
