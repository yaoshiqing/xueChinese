package com.gjjy.usercenterlib.ui.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.usercenterlib.adapter.VouchersAdapter;
import com.gjjy.usercenterlib.mvp.presenter.VouchersPresenter;
import com.gjjy.usercenterlib.widget.VouchersList;
import com.ybear.mvp.annotations.Presenter;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.mvp.view.VouchersView;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.Toolbar;

import java.util.Arrays;
import java.util.List;

/**
 * 优惠券页面
 */
@Route(path = "/userCenter/vouchersActivity")
public class VouchersActivity extends BaseActivity implements VouchersView {
    @Presenter
    private VouchersPresenter mPresenter;

    private Toolbar tbToolbar;
    private RadioGroup rgTable;
    private com.ybear.ybcomponent.widget.ViewPager vpPager;
    private TextView tvNotDataTips;
    private ImageView ivTableDiv;

//    private VouchersList vlUnused;
//    private VouchersList vlInUse;
//    private VouchersList vlExpired;

    private ValueAnimator vaTableDiv;
    private final VouchersList[] mVouchersLists = new VouchersList[ 3 ];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_vouchers );
        initView();
        initData();
        initListener();

        rgTable.check( R.id.vouchers_rb_unused_btn );
        post( () -> switchTableDiv( R.id.vouchers_rb_unused_btn, false ), 100);
        mPresenter.queryVouchersList();
    }

    @Override
    protected void onDestroy() {
        for( VouchersList vl : mVouchersLists ) {
            if( vl != null ) vl.release();
        }
        super.onDestroy();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.vouchers_tb_toolbar );
        rgTable = findViewById( R.id.vouchers_rg_table );
        vpPager = findViewById( R.id.vouchers_vp_pager );
        tvNotDataTips = findViewById( R.id.vouchers_tv_not_data_tips );
        ivTableDiv = findViewById( R.id.vouchers_iv_table_div );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );

        vaTableDiv = new ValueAnimator();
        vaTableDiv.setDuration( 200 );

//        List<VouchersList> pagerList = new ArrayList<>();
//        pagerList.add( vlUnused = new VouchersList( this ) );
//        pagerList.add( vlInUse = new VouchersList( this ) );
//        pagerList.add( vlExpired = new VouchersList( this ) );

        for( int i = 0; i < mVouchersLists.length; i++ ) {
            if( getContext() == null ) break;
            doVouchersListener( i, mVouchersLists[ i ] = new VouchersList( getContext() ) );
        }
        vpPager.addViewAllOfPager( Arrays.asList( mVouchersLists )  );
//        for( int i = 0; i < mVouchersLists.length; i++ ) {
//            if( getContext() == null ) continue;
////            RecyclerView rv = new RecyclerView( this );
////            rv.setLayoutParams(new RecyclerView.LayoutParams(
////                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT
////            ));
////            rv.setLayoutManager( new LinearLayoutManager( this ) );
////            rv.setOverScrollMode( OVER_SCROLL_NEVER );
////            RecyclerView rv = (RecyclerView) View.inflate(
////                    this, R.layout.item_recycler_view, null
////            );
////            rv.setAdapter( mDataAdapters[ i ] = new VouchersAdapter( new ArrayList<>() ) );
//            mVouchersLists[ i ] = new VouchersList( getContext() );
//            pagerList.add( mVouchersLists[ i ] );
//        }
//        vpPager.setAdapter( new ViewPagerAdapter<>( pagerList ) );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        rgTable.setOnCheckedChangeListener((group, checkedId) -> {
            int position = 0;
            if( checkedId == R.id.vouchers_rb_in_use_btn ) {
                position = 1;
            }else if( checkedId == R.id.vouchers_rb_expired_btn ) {
                position = 2;
            }
            vpPager.setCurrentItem( position );
        });

        vpPager.addOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                int id = 0;
                switch( position ) {
                    case 0:
                        id = R.id.vouchers_rb_unused_btn;
                        break;
                    case 1:
                        id = R.id.vouchers_rb_in_use_btn;
                        break;
                    case 2:
                        id = R.id.vouchers_rb_expired_btn;
                        break;
                }
                if( id == 0 ) return;
                rgTable.check( id );
                switchTableDiv( id, true );
                switchShowNotDataTips( position );
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        vaTableDiv.addUpdateListener(animation ->
                ivTableDiv.setTranslationX( (Float) animation.getAnimatedValue() )
        );
    }

    private void doVouchersListener(int index, VouchersList layout) {
        layout.setOnRefreshListener( refreshLayout -> mPresenter.queryDataList( index ) );
        layout.setOnLoadMoreListener( refreshLayout -> mPresenter.queryNextDataList( index ) );

        layout.getAdapter().setOnItemClickListener( (adapter, view, data, i) -> {
            if( index == 0 ) mPresenter.BuyVipTicket( data.getId(), i );
        } );
    }

    private void switchShowNotDataTips(int pos) {
        int icon = -1;
        int contentId = -1;
//        VouchersList adapter = mVouchersLists[ pos ];
        if( mVouchersLists[ pos ].isEmpty() ) {
            switch ( pos ) {
                case 0:
                    icon = R.drawable.ic_vouchers_not_unused_icon;
                    contentId = R.string.stringVouchersOfNotUnused;
                    break;
                case 1:
                    icon = R.drawable.ic_vouchers_not_in_use_icon;
                    contentId = R.string.stringVouchersOfNotInUse;
                    break;
                case 2:
                    icon = R.drawable.ic_vouchers_not_expired_icon;
                    contentId = R.string.stringVouchersOfNotExpired;
                    break;
            }
        }
        if( icon == -1 || contentId == -1 ) {
            tvNotDataTips.setVisibility( View.GONE );
            return;
        }
        tvNotDataTips.setCompoundDrawablesWithIntrinsicBounds( 0, icon, 0, 0 );
        tvNotDataTips.setText( contentId );
        tvNotDataTips.setVisibility( View.VISIBLE );
    }

    private void switchTableDiv(int id, boolean isAnim) {
        post(() -> {
            View v = rgTable.findViewById( id );
            float toX = v.getX() + ( v.getWidth() / 2F ) - ( ivTableDiv.getWidth() / 2F );

            if( isAnim ) {
                vaTableDiv.setFloatValues( ivTableDiv.getTranslationX(), toX );
                vaTableDiv.start();
                return;
            }
            ivTableDiv.setTranslationX( toX );
        });
    }

    @Override
    public void onCallVouchersList(List<VouchersAdapter.ItemData> list, int index, boolean isPaging) {
        mVouchersLists[ index ].setDataList( isPaging, list );
        vpPager.notifyAdapter();
//        VouchersList vl = mVouchersLists[ index ];
//        vl.setDataList( 1, list );

//        vpPager.notifyAdapter();
//        VouchersAdapter adapter = mVouchersList.;
//        adapter.clearItemData();
//        adapter.addItemData( list );
//        adapter.notifyDataSetChanged();
//        mVouchersList.setDataList( 1, list );
        if( index == 0 ) switchShowNotDataTips( 0 );
    }

    @Override
    public void onCallBuyVipTicketResult(int code, int pos) {
        boolean result = false;
        int toastTextId;
        switch( code ) {
            case 0:         //开通成功
                result = true;
                toastTextId = R.string.stringVoucherOfBuyVipTicketOfSuccess;
                break;
            case 1111:      //已是正式会员
                toastTextId = R.string.stringVoucherOfBuyVipTicketOfVip;
                break;
            case 1112:      //已是试用会员
                toastTextId = R.string.stringVoucherOfBuyVipTicketOfFreeVip;
                break;
            default:
                toastTextId = R.string.stringVoucherOfBuyVipTicketOfFailure;
                break;
        }
        if( result ) {
            VouchersAdapter adapter = mVouchersLists[ 0 ].getAdapter();
            adapter.removeItemData( pos );
            adapter.notifyItemRemoved( pos );
        }
        showToastOfLong( toastTextId );
        BuriedPointEvent.get().onMePageOfCouponCentreOfUseButton( getContext(), result );
    }
}
