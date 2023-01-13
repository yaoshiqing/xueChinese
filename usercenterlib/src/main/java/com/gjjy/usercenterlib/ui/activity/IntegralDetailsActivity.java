package com.gjjy.usercenterlib.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.PubRefreshLayout;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.adapter.IntegralAdapter;
import com.gjjy.usercenterlib.mvp.presenter.IntegralDetailsPresenter;
import com.gjjy.usercenterlib.mvp.view.IntegralDetailsView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.ArrayList;
import java.util.List;

/**
 * 积分详情页面
 */
@Route(path = "/userCenter/integralDetailsActivity")
public class IntegralDetailsActivity extends BaseActivity implements IntegralDetailsView {
    @Presenter
    private IntegralDetailsPresenter mPresenter;

    private Toolbar tbToolbar;
    private ImageView ivTopBg;
    private TextView tvCount;
    private Spinner spiSelectType;
    private TextView tvNotData;
    private PubRefreshLayout prlRefreshLayout;
    private RecyclerView rvList;
    private DialogOption mLoadingDialog;

    private IntegralAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_integral_details );
        initView();
        initData();
        initListener();

        mPresenter.queryTotalCount();
        mPresenter.queryIntegralList();
    }

    @Override
    protected void onDestroy() {
        onCallLoadingDialog( false );
        super.onDestroy();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.integral_details_tb_toolbar );
        ivTopBg = findViewById( R.id.integral_details_iv_top_bg );
        tvCount = findViewById( R.id.integral_details_tv_count );
        spiSelectType = findViewById( R.id.integral_details_spi_select_type );
        tvNotData = findViewById( R.id.integral_details_tv_not_data );
        prlRefreshLayout = findViewById( R.id.integral_details_prl_refresh_layout );
        rvList = findViewById( R.id.integral_details_rv_list );
    }

    private void initData() {
        mLoadingDialog = createLoadingDialog();
        FrameLayout.LayoutParams lpToolbar = (FrameLayout.LayoutParams) tbToolbar.getLayoutParams();
        lpToolbar.topMargin = SysUtil.getStatusBarHeight();
//        setStatusBarHeightForPadding( tbToolbar );

        mAdapter = new IntegralAdapter( new ArrayList<>() );
        rvList.setLayoutManager( new LinearLayoutManager( this ) );
        rvList.addItemDecoration( new SpaceItemDecoration( Utils.dp2Px( this, 10 ) ) );
        rvList.setAdapter( mAdapter );

        prlRefreshLayout.setEnableRefresh( true );
        prlRefreshLayout.setEnableLoadMore( true );

        //默认选项
        spiSelectType.setSelection( 0 );
        switchHaveIntegralTopBg( false );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        tbToolbar.setOnClickOtherBtnListener( v -> StartUtil.startIntegralRulesActivity() );

        prlRefreshLayout.setOnRefreshListener( refreshLayout -> {
            mPresenter.updateIntegralType();
            mPresenter.queryIntegralList();
        });

        prlRefreshLayout.setOnLoadMoreListener(refreshLayout -> mPresenter.nextIntegralList());

        mAdapter.setOnQueryMonthTotalCountListener( (year, month, pos) ->
                mPresenter.queryMonthTotalList( year, month, pos )
        );

        spiSelectType.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.switchIntegralType( position );
                mPresenter.queryIntegralList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        } );
    }

    private void switchHaveIntegralTopBg(boolean haveData) {
        tbToolbar.setTitleColor( haveData ? R.color.colorWhite : R.color.color33 );
        tbToolbar.setOtherBtnColor( haveData ? R.color.colorWhite : R.color.color66 );
        tbToolbar.setBackBtnOfImg( haveData ? R.drawable.ic_white_back : R.drawable.ic_gray_back );
        tvCount.setTextColor( getResources().getColor(
                haveData ? R.color.colorWhite : R.color.colorBuyVipMain
        ));
        ivTopBg.setImageResource( haveData ? R.drawable.ic_integral_details_bg : R.drawable.ic_integral_white_bg );
        spiSelectType.setBackgroundResource( haveData ?
                R.drawable.layer_list_integral_select_type_have_data :
                R.drawable.layer_list_integral_select_type_not_data
        );

        View selectItem = spiSelectType.getSelectedView();
        if( selectItem != null ) {
            ((TextView)selectItem).setTextColor( getResources().getColor(
                    haveData ? R.color.colorWhite : R.color.color66
            ));
        }
    }

    @Override
    public void onCallTotalCount(int count) {
        tvCount.setText( String.valueOf( count ) );
    }

    @Override
    public void onCallIntegralList(List<IntegralAdapter.ItemData> list, boolean isPaging) {
        boolean isNotData = list == null || list.size() == 0;
        if( !isPaging ) mAdapter.clearItemData();
        if( !isNotData ) {
            IntegralAdapter.ItemData lastItem = mAdapter.getItemDataOfLast();
            /* 分页时，最新一条和适配器最后一条一致时，增量到最后一条数据下 */
            if( lastItem != null ) {
                IntegralAdapter.ItemData curItem = list.get( 0 );
                if( curItem.getYear() == lastItem.getYear() && curItem.getMonth() == lastItem.getMonth() ) {
                    lastItem.getList().addAll( curItem.getList() );
                    list.remove( 0 );
                }
            }
            mAdapter.addItemData( list );
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.notifyDataSetChanged();
        //关闭分页加载动画
        if( isPaging ) {
            prlRefreshLayout.finishLoadMore();
        }else {
            prlRefreshLayout.finishRefresh();
        }
        //空数据源
        post( () -> prlRefreshLayout.setVisibility(
                mAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE
        ));
        boolean haveCount = mAdapter.getItemCount() > 0;
        switchHaveIntegralTopBg( haveCount );
        tvNotData.setVisibility( haveCount ? View.GONE : View.VISIBLE );
    }

    @Override
    public void onCallMonthTotal(int incomeCount, int expendCount, int pos) {
        IntegralAdapter.ItemData item = mAdapter.getItemData( pos );
        if( item == null ) return;
        item.setIncomeCount( incomeCount );
        item.setExpendCount( expendCount );
        mAdapter.notifyItemChanged( pos );
    }

    @Override
    public void onCallLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            mLoadingDialog.show();
        }else {
            mLoadingDialog.dismiss();
        }
    }
}
