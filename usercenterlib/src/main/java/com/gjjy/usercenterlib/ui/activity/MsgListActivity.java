package com.gjjy.usercenterlib.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.PubRefreshLayout;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.MsgCenterAdapter;
import com.gjjy.usercenterlib.adapter.MsgListAdapter;
import com.gjjy.usercenterlib.mvp.presenter.MsgListPresenter;
import com.gjjy.usercenterlib.mvp.view.MsgListView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息列表页面
 */
@Route(path = "/message/msgListActivity")
public class MsgListActivity extends BaseActivity implements MsgListView {
    @Presenter
    private MsgListPresenter mPresenter;

    private Toolbar tbToolbar;
    private PubRefreshLayout prlRefreshLayout;
    private RecyclerView rvList;

    private MsgListAdapter mAdapter;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_msg_list );
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        onCallShowLoadingDialog( false );
        super.onDestroy();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.msg_list_tb_toolbar );
        prlRefreshLayout = findViewById( R.id.msg_list_prl_refresh_layout );
        rvList = findViewById( R.id.msg_list_rv_list );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();
        if( mPresenter.getMsgCenterType() == MsgCenterAdapter.Type.MESSAGE ) {
            tbToolbar.setTitle( R.string.stringMsgCenterMsgNotify );
            rvList.setPadding( 0, 0, 0, 0 );
            rvList.setBackgroundColor( Color.WHITE );
            prlRefreshLayout.setBackgroundColor( Color.WHITE );
        }
        tbToolbar.setBackgroundColor( Color.WHITE );
        tbToolbar.setOtherBtnOfImg( R.drawable.ic_msg_list_clear_unread_icon );
        tbToolbar.getOtherBtnOfImg().setVisibility(
                mPresenter.getMsgCenterType() == MsgCenterAdapter.Type.MESSAGE ? View.GONE : View.VISIBLE
        );

        mAdapter = new MsgListAdapter(
                Glide.with( this ), new ArrayList<>(), mPresenter.getMsgCenterType()
        );
        mAdapter.setUserName( mPresenter.getUserName() );
        prlRefreshLayout.setEnableRefresh( false );

        rvList.setLayoutManager( new LinearLayoutManager( this ) );
        rvList.setAdapter( mAdapter );

        //获取消息列表
        mPresenter.refreshMsgList();
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        tbToolbar.setOnClickOtherBtnListener(v ->
                showMarkNotifyMsgDialog(v1 -> mPresenter.unreadMsgAll())
        );

        prlRefreshLayout.setOnLoadMoreListener(refreshLayout -> mPresenter.nextMsgList());


        mAdapter.setOnItemClickListener((adapter, view, itemData, position) -> {
            //标记已读
            mPresenter.unreadMsg( itemData.getId(), position );
            //打开互动消息页
            if( mPresenter.getMsgCenterType() == MsgCenterAdapter.Type.MESSAGE ) {
                StartUtil.startTargetedLearningDetailsActivity(
                        itemData.getId(),
                        itemData.getTalkId(),
                        itemData.getInteractType()
                );
                return;
            }
            String title = getString( itemData.getMsgType() == 2 ?
                    R.string.stringMsgCenterVipNotify :
                    R.string.stringMsgCenterSystemNotify
            );
            //打开详情页
            StartUtil.startMsgDetailsActivity(
                    this,
                    itemData.getId(),
                    title
            );
        });
    }

    @Override
    public void onCallMsgList(List<MsgListAdapter.ItemData> list, boolean isPaging) {
        boolean isNotData = list.size() == 0;
        int vis = mAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE;
        if( isPaging ) {
            if( !isNotData ) {
                mAdapter.addItemData( list );
                mAdapter.notifyItemRangeInserted(
                        mAdapter.getItemCount() - list.size(),
                        list.size()
                );
            }
            //关闭分页加载动画
            prlRefreshLayout.finishLoadMore();
        }else {
            mAdapter.clearItemData();
            mAdapter.addItemData( list );
            mAdapter.notifyDataSetChanged();
        }

        //空数据源
        if( isNotData ) {
            prlRefreshLayout.setVisibility( vis );
            tbToolbar.getOtherBtnOfImg().setVisibility( vis );
        }
    }

    @Override
    public void onCallUnread(int position) {
        MsgListAdapter.ItemData item = mAdapter.getItemData( position );
        if( item == null ) return;
        item.setNews( false );
        mAdapter.setItemData( position, item );
        mAdapter.notifyItemChanged( position );
    }

    @Override
    public void onCallUnreadAll() {
        mPresenter.refreshMsgList();
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            mLoadingDialog.show();
            return;
        }
        mLoadingDialog.dismiss();
    }
}
