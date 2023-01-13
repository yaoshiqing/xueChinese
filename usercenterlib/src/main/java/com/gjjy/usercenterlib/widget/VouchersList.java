package com.gjjy.usercenterlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.basiclib.widget.PubRefreshLayout;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.VouchersAdapter;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class VouchersList extends LinearLayout {
    private PubRefreshLayout prlRefreshLayout;
    private RecyclerView rvList;
    public OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private VouchersAdapter mAdapter;

    public VouchersList(@NonNull Context context) {
        this(context, null);
    }

    public VouchersList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from( getContext() ).inflate( R.layout.block_refresh_recycler_view, this );

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
        rvList.addItemDecoration( new SpaceItemDecoration( Utils.dp2Px( getContext(), 15 ) ) );
        mAdapter = new VouchersAdapter( new ArrayList<>() );

        rvList.setAdapter( mAdapter );


    }

    private void initListener() {
        prlRefreshLayout.setOnRefreshListener( layout -> {
            if( mOnRefreshListener != null ) mOnRefreshListener.onRefresh( layout );
        } );

        prlRefreshLayout.setOnLoadMoreListener(layout -> {
            if( mOnLoadMoreListener != null ) mOnLoadMoreListener.onLoadMore( layout );
        });
    }

    public void release() { mAdapter.release(); }

    public void scrollToTop() {
        rvList.scrollToPosition( 0 );
    }

    public VouchersAdapter getAdapter() { return mAdapter; }

    public void setOnRefreshListener(OnRefreshListener l) {
        this.mOnRefreshListener = l;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener l) {
        mOnLoadMoreListener = l;
    }

    public void setDataList(boolean isPaging, List<VouchersAdapter.ItemData> list) {
        prlRefreshLayout.callAdapterData( mAdapter, list, isPaging );
//        if( !isPaging ) mAdapter.clearItemData();
//        mAdapter.addItemData( list );
//        mAdapter.notifyDataSetChanged();
//        if( isPaging ) {
//            srlRefreshLayout.finishLoadMore();
//        }else {
//            srlRefreshLayout.finishRefresh();
//        }
    }

    public boolean isEmpty() { return mAdapter.getItemCount() == 0; }

//    public void removeData(long id) {
//        int removePosition = -1;
//        for (int i = 0; i < mAdapter.getDataList().size(); i++) {
//            if( id == mAdapter.getDataList().get( i ).getId() ) {
//                removePosition = i;
//                break;
//            }
//        }
//        if( removePosition == -1 ) return;
//        mAdapter.removeItemData( removePosition );
//        mAdapter.notifyItemRemoved( removePosition );
//    }
}