package com.gjjy.discoverylib.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.PubRefreshLayout;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;
import com.gjjy.discoverylib.mvp.presenter.PopularVideosPresenter;
import com.gjjy.discoverylib.mvp.view.PopularVideosView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/discovery/hotVideoMoreListActivity")
public class PopularVideosMoreListActivity extends BaseActivity implements PopularVideosView {
    @Presenter
    private PopularVideosPresenter mPresenter;

    private Toolbar tbToolbar;
    private PubRefreshLayout prlRefreshLayout;
    private RecyclerView rvList;

    private DiscoveryListAdapter mAdapter;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_popular_videos_list );
        initView();
        initData();
        initListener();

        mLoadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        onCallShowLoadingDialog( false );
        super.onDestroy();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.popular_videos_list_tb_toolbar );
        prlRefreshLayout = findViewById( R.id.refresh_prl_refresh_layout );
        rvList = findViewById( R.id.refresh_rv_list );
    }

    private void initData() {
        setStatusBarHeightForSpace( findViewById( R.id.toolbar_height_space ) );
        mLoadingDialog = createLoadingDialog();
        mPresenter.setIsMoreList( true );
        tbToolbar.setTitle( R.string.stringPopularVideosMainTitle );

        prlRefreshLayout.setEnableRefresh( true );
        prlRefreshLayout.setEnableLoadMore( true );

        mAdapter = new DiscoveryListAdapter(
                (BaseActivity) getActivity(),
                Glide.with( this ),
                DiscoveryListAdapter.ListType.MORE_LIST,
                new ArrayList<>(),
                mPresenter.getUid(),
                mPresenter.getUserName()
        );

        rvList.setLayoutManager( new LinearLayoutManager( this ) );
        rvList.setAdapter( mAdapter );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener( view -> onBackPressed() );

        prlRefreshLayout.setOnRefreshListener( layout -> mPresenter.queryDataList( false ) );
        prlRefreshLayout.setOnLoadMoreListener(refreshLayout -> mPresenter.nextDataList( false ));
    }

    @Override
    public void onCallDataList(List<DiscoveryListAdapter.ItemData> list, boolean isPaging) {
        prlRefreshLayout.callAdapterData( mAdapter, list, isPaging );
//        if( page == 1 ) mAdapter.clearItemData();
//        mAdapter.addItemData( list );
//        mAdapter.notifyDataSetChanged();
//        if( page == 1 ) {
//            srlListLayout.finishRefresh();
//        }else {
//            srlListLayout.finishLoadMore();
//        }
        onCallShowLoadingDialog( false );
    }

    @Override
    public void onCallIsVip(boolean isVip) {
        mAdapter.setVip( isVip );
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow && !mLoadingDialog.isShowing() ) {
            mLoadingDialog.show();
            return;
        }
        mLoadingDialog.dismiss();
    }
}