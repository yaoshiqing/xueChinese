package com.gjjy.discoverylib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.PubRefreshLayout;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MyCourseList extends LinearLayout {
    private PubRefreshLayout prlRefreshLayout;
    private RecyclerView rvList;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private DiscoveryListAdapter mAdapter;

    private String mUid, mUserName;

    public MyCourseList(@NonNull Context context) {
        this(context, null);
    }

    public MyCourseList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundResource( R.color.colorMainBG );

        View.inflate( getContext(), R.layout.block_refresh_recycler_view, this );

        prlRefreshLayout = findViewById( R.id.refresh_prl_refresh_layout );
        rvList = findViewById(R.id.refresh_rv_list);

        initData();
        initListener();
    }

    private void initData() {
        prlRefreshLayout.setEnableRefresh( true );
        prlRefreshLayout.setEnableLoadMore( true );

        rvList.setOverScrollMode( OVER_SCROLL_NEVER );
        rvList.setLayoutManager( new LinearLayoutManager( getContext() ) );

        mAdapter = new DiscoveryListAdapter(
                (BaseActivity) getContext(),
                Glide.with( this ),
                DiscoveryListAdapter.ListType.MY_COURSE_LIST,
                new ArrayList<>(),
                mUid,
                mUserName
        );

        rvList.setAdapter( mAdapter );


    }

    private void initListener() {
        prlRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if( mOnRefreshListener != null ) mOnRefreshListener.onRefresh( refreshLayout );
        });
        prlRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if( mOnLoadMoreListener != null ) mOnLoadMoreListener.onLoadMore( refreshLayout );
        });
    }

    public void setUid(String uid, String name) {
        mUid = uid;
        mUserName = name;
    }
    public void setVip(boolean isVip) {
        if( mAdapter != null ) mAdapter.setVip( isVip );
    }

    public void scrollToTop() {
        rvList.scrollToPosition( 0 );
    }

    public void setOnRefreshListener(OnRefreshListener l) {
        mOnRefreshListener = l;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener l) {
        mOnLoadMoreListener = l;
    }

    public void setDataList(int page, List<DiscoveryListAdapter.ItemData> list) {
        prlRefreshLayout.callAdapterData( mAdapter, list, page == -1 || page > 1 );
//        if( page == 1 ) mAdapter.clearItemData();
//        mAdapter.addItemData( list );
//        mAdapter.notifyDataSetChanged();
//        if( page == 1 ) {
//            srlRefreshLayout.finishRefresh();
//        }else {
//            srlRefreshLayout.finishLoadMore();
//        }
//        LogUtil.e("MyCourseList -> setDataList -> page:" + page + " | size:" + list.size());
    }

    public boolean isEmpty() {
        return mAdapter.getItemCount() == 0;
    }

    public void removeData(long id) {
        int removePosition = -1;
        for (int i = 0; i < mAdapter.getDataList().size(); i++) {
            if( id == mAdapter.getDataList().get( i ).getId() ) {
                removePosition = i;
                break;
            }
        }
        if( removePosition == -1 ) return;
        mAdapter.removeItemData( removePosition );
        mAdapter.notifyItemRemoved( removePosition );
    }
}