package com.gjjy.usercenterlib.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.PubRefreshLayout;
import com.gjjy.usercenterlib.StartUtil;
import com.gjjy.usercenterlib.adapter.FriendsAdapter;
import com.gjjy.usercenterlib.mvp.presenter.FriendsPresenter;
import com.gjjy.usercenterlib.mvp.view.FriendsView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.gjjy.usercenterlib.R;
import com.gjjy.basiclib.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友页面
 */
@Route(path = "/userCenter/friendsActivity")
public class FriendsActivity extends BaseActivity implements FriendsView {
    @Presenter
    private FriendsPresenter mPresenter;

    private Toolbar tbToolbar;
    private PubRefreshLayout prlRefreshLayout;
    private RecyclerView rvList;
    private TextView tvInviteFriendsBtn;

    private FriendsAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_friends );
        initView();
        initData();
        initListener();

        mPresenter.queryDataList();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.friends_tb_toolbar );
        prlRefreshLayout = findViewById( R.id.friends_prl_refresh_layout );
        rvList = findViewById( R.id.friends_rv_list );
        tvInviteFriendsBtn = findViewById( R.id.friends_tv_invite_friends_btn );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );

        mAdapter = new FriendsAdapter( Glide.with( this ), new ArrayList<>() );
        rvList.setLayoutManager( new LinearLayoutManager( this ) );
        rvList.addItemDecoration( new SpaceItemDecoration( Utils.dp2Px( this, 10 ) ) );
        rvList.setAdapter( mAdapter );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        tvInviteFriendsBtn.setOnClickListener( v -> StartUtil.startInviteFriendsActivity() );

        prlRefreshLayout.setEnableRefresh( true );
        prlRefreshLayout.setEnableLoadMore( true );
        prlRefreshLayout.setOnRefreshLoadMoreListener( new OnRefreshLoadMoreListener(){
            @Override
            public void onLoadMore(@NonNull RefreshLayout rl) { mPresenter.nextDataList(); }

            @Override
            public void onRefresh(@NonNull RefreshLayout rl) { mPresenter.queryDataList(); }
        } );
    }

    @Override
    public void onCallFriendsList(List<FriendsAdapter.ItemData> list, boolean isPaging) {
        prlRefreshLayout.callAdapterData( mAdapter, list, isPaging );
    }
}
