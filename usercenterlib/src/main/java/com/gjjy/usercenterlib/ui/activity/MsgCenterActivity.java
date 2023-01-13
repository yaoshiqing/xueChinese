package com.gjjy.usercenterlib.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.bumptech.glide.Glide;
import com.gjjy.usercenterlib.adapter.MsgCenterAdapter;
import com.gjjy.usercenterlib.mvp.presenter.MsgCenterPresenter;
import com.gjjy.usercenterlib.mvp.view.MsgCenterView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.usercenterlib.R;
import com.gjjy.basiclib.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息中心页面
 */
@Route(path = "/message/msgCenterActivity")
public class MsgCenterActivity extends BaseActivity implements MsgCenterView {
    @Presenter
    private MsgCenterPresenter mPresenter;

    private Toolbar tbToolbar;
    private RecyclerView rvList;

    private MsgCenterAdapter mAdapter;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_msg_center );
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
        tbToolbar = findViewById( R.id.msg_center_tb_toolbar );
        rvList = findViewById( R.id.msg_center_rv_list );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();
        tbToolbar.setBackgroundColor( Color.WHITE );

        mAdapter = new MsgCenterAdapter( Glide.with( this ), new ArrayList<>() );
        rvList.setLayoutManager( new LinearLayoutManager( getContext() ) );
        DividerItemDecoration dec = new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL
        );
        dec.setDrawable( getResources().getDrawable( R.drawable.shape_divide_1dp_setting ) );
        rvList.addItemDecoration( dec );
        rvList.setAdapter( mAdapter );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        mAdapter.setOnItemClickListener((adapter, view, itemData, position) ->
                mPresenter.startNotifyListActivity( itemData.getType() )
        );
    }

    @Override
    public void onCallMsgList(List<MsgCenterAdapter.ItemData> list) {
        rvList.setVisibility( list.size() == 0 ? View.GONE : View.VISIBLE );
        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();
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