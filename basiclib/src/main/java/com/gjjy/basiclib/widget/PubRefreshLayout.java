package com.gjjy.basiclib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.R;
import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.ybear.ybcomponent.base.adapter.BaseRecyclerViewAdapter;
import com.ybear.ybcomponent.base.adapter.IItemData;

import java.util.List;

public class PubRefreshLayout extends SmartRefreshLayout {
    private boolean mEnableRefresh;
    private boolean mEnableLoadMore;

    public PubRefreshLayout(Context context) {
        this( context, null );
    }

    public PubRefreshLayout(Context context, AttributeSet attrs) {
        super( context, attrs );
        super.setEnableRefresh( false );
        super.setEnableLoadMore( false );
//        setEnableRefresh( true );
//        setEnableLoadMore( false );
    }

    @Override
    public com.scwang.smart.refresh.layout.api.RefreshLayout setEnableRefresh(boolean enabled) {
        mEnableRefresh = enabled;
        if( enabled ) {
            setRefreshHeader( new MaterialHeader( getContext() )
                    .setColorSchemeResources( R.color.colorMain, R.color.colorWhite )
            );
        }
        return super.setEnableRefresh( enabled );
    }

    @Override
    public com.scwang.smart.refresh.layout.api.RefreshLayout setEnableLoadMore(boolean enabled) {
        mEnableLoadMore = enabled;
        if( enabled ) {
            setRefreshFooter( new BallPulseFooter( getContext() )
                    .setAnimatingColor( getResources().getColor( R.color.colorMain ) )
                    .setNormalColor( getResources().getColor( R.color.colorMain ) )
            );
        }
        return super.setEnableLoadMore( enabled );
    }

    public <T extends IItemData> void callAdapterData(@NonNull BaseRecyclerViewAdapter<T, ?> adapter,
                                                      @Nullable List<T> list,
                                                      boolean isPaging) {
        if( list != null && list.size() > 0 ) {
            if( isPaging ) {
                //空数据源
                int posStart = adapter.getItemCount();
                adapter.addItemData( list );
                adapter.notifyItemRangeInserted( posStart, list.size() );
            }else {
                adapter.clearItemData();
                adapter.addItemData( list );
                adapter.notifyItemRangeChanged( 0, list.size() );
            }
        }
        //空数据源
        int ret = adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE;
        //关闭分页加载动画
        post(() -> {
            if( isPaging ) {
                if( mEnableLoadMore ) finishLoadMore();
                return;
            }
            if( mEnableRefresh ) finishRefresh();
        });
    }
}