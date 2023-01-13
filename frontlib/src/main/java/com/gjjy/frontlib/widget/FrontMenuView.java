package com.gjjy.frontlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gjjy.frontlib.adapter.FrontMenuAdapter;
import com.gjjy.frontlib.adapter.holder.FrontMenuHolder;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.listener.OnItemClickListener;
import com.gjjy.frontlib.R;

import java.util.ArrayList;
import java.util.List;

public class FrontMenuView extends FrameLayout {

    private List<FrontMenuAdapter.ItemData> mDataList;
    private FrontMenuAdapter mAdapter;
    private OnItemClickListener<FrontMenuAdapter.ItemData, FrontMenuHolder> mOnItemClickListener;
    
    public FrontMenuView(Context context) {
        this(context, null);
    }

    public FrontMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrontMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource( R.drawable.ic_front_menu_bg );

        mDataList = new ArrayList<>();
        mAdapter = new FrontMenuAdapter( Glide.with( getContext() ), mDataList );
        
        RecyclerView rvList = new RecyclerView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lp.setMargins(
                Utils.dp2Px( getContext(), 10 ),
                Utils.dp2Px( getContext(), 14 ),
                Utils.dp2Px( getContext(), 10 ),
                0
        );
        rvList.setLayoutParams( lp );
        rvList.setLayoutManager( new LinearLayoutManager( getContext() ) );
        DividerItemDecoration div = new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL
        );
        div.setDrawable( getResources().getDrawable( R.drawable.shape_divider_1dp_ef) );
        rvList.addItemDecoration( div );
        rvList.setOverScrollMode( OVER_SCROLL_NEVER );
        rvList.setAdapter( mAdapter );
        addView( rvList );
        
        mAdapter.setOnItemClickListener((adapter, view, itemData, i) -> {
            if( mOnItemClickListener == null ) return;
            mOnItemClickListener.onItemClick( adapter, view, itemData, i );
        });
    }

    public void setOnItemClickListener(
            OnItemClickListener<FrontMenuAdapter.ItemData, FrontMenuHolder> l) {
        mOnItemClickListener = l;
    }

    public FrontMenuView addData(List<FrontMenuAdapter.ItemData> list) {
        if( list == null || list.size() == 0 ) return this;
        mDataList.addAll( list );
        mAdapter.notifyDataSetChanged();
        return this;
    }
    
    public FrontMenuView setData(List<FrontMenuAdapter.ItemData> list) {
        if( list == null || list.size() == 0 ) return this;
        mDataList.clear();
        return addData( list );
    }

    @Nullable
    public FrontMenuAdapter.ItemData getData(int id) {
        for ( FrontMenuAdapter.ItemData data : mDataList) {
            if( data.getId() == id ) return data;
        }
        return null;
    }
}
