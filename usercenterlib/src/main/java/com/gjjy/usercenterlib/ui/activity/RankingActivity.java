package com.gjjy.usercenterlib.ui.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.usercenterlib.adapter.RankingAdapter;
import com.gjjy.usercenterlib.mvp.presenter.RankingPresenter;
import com.gjjy.usercenterlib.mvp.view.RankingView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.ybear.ybcomponent.widget.shape.ShapeRecyclerView;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.usercenterlib.R;
import com.gjjy.basiclib.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排行榜页面
 */
@Route(path = "/userCenter/rankingActivity")
public class RankingActivity extends BaseActivity implements RankingView {
    @Presenter
    private RankingPresenter mPresenter;

    private Toolbar tbToolbar;
    private TextView tvHaveRankingTips;
    private LinearLayout llTopRanking1;
    private TextView tvTopRanking1;
    private LinearLayout llTopRanking2;
    private TextView tvTopRanking2;
    private LinearLayout llTopRanking3;
    private TextView tvTopRanking3;
    private ShapeRecyclerView srvList;

    private RankingAdapter mAdapter;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ranking );
        initView();
        initData();
        initListener();

        mPresenter.queryRankingList();
    }

    @Override
    protected void onDestroy() {
        onCallShowLoadingDialog( false );
        super.onDestroy();
    }

    private void initView() {
        tbToolbar = findViewById( R.id.ranking_tb_toolbar );
        tvHaveRankingTips = findViewById( R.id.ranking_tv_have_ranking_tips );
        llTopRanking1 = findViewById( R.id.ranking_inc_ll_top_ranking_1 );
        tvTopRanking1 = findViewById( R.id.ranking_tv_top_ranking_1 );
        llTopRanking2 = findViewById( R.id.ranking_inc_ll_top_ranking_2 );
        tvTopRanking2 = findViewById( R.id.ranking_tv_top_ranking_2 );
        llTopRanking3 = findViewById( R.id.ranking_inc_ll_top_ranking_3 );
        tvTopRanking3 = findViewById( R.id.ranking_tv_top_ranking_3 );
        srvList = findViewById( R.id.ranking_srv_list );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();
        tbToolbar.setTitleColor( R.color.colorWhite );

        mAdapter = new RankingAdapter( Glide.with( this ), new ArrayList<>() );
        srvList.setLayoutManager( new LinearLayoutManager( this ) );
        srvList.addItemDecoration( new SpaceItemDecoration( Utils.dp2Px( this, 10 ) ) );
        srvList.setAdapter( mAdapter );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());
    }

    private void setTopRanking(int rank, RankingAdapter.ItemData data) {
        if( data == null ) return;
        LinearLayout llLayout = null;
        TextView tvXP = null;
        switch( rank ) {
            case 1:
                llLayout = llTopRanking1;
                tvXP = tvTopRanking1;
                break;
            case 2:
                llLayout = llTopRanking2;
                tvXP = tvTopRanking2;
                break;
            case 3:
                llLayout = llTopRanking3;
                tvXP = tvTopRanking3;
                break;
        }
        if( llLayout == null || tvXP == null ) return;
        TextView tvName = llLayout.findViewById( R.id.item_user_photo_tv_name );
        ShapeImageView sivImg = llLayout.findViewById( R.id.item_user_photo_siv_img );
        ImageView ivVipIcon = llLayout.findViewById( R.id.item_user_photo_iv_vip_icon );

        //名字
        if( !TextUtils.isEmpty( data.getName() ) ) {
            tvName.setText( data.getName() );
            tvName.setVisibility( View.VISIBLE );
        }
        //图片链接。空链接时为默认图标
        if( TextUtils.isEmpty( data.getImgUrl() ) ) {
            sivImg.setImageResource( R.drawable.ic_photo_user );
        }else {
            Glide.with( this ).load( data.getImgUrl() ).into( sivImg );
        }
        //经验值
        tvXP.setText( ( data.getXp() + "XP" ) );
        //vip小图标
        ivVipIcon.setVisibility( data.isVip() ? View.VISIBLE : View.GONE );

        AnimUtil.setAlphaAnimator( 800, llLayout );
    }

    @Override
    public void onCallRankingList(List<RankingAdapter.ItemData> list, int currentIndex, int lastIndex) {
        if( list.size() == 0 ) return;
        int maxTopCount = 3;
        //列表排名的真实当前排名和上一次排名
        int listCurIndex = currentIndex - maxTopCount;
        int listLastIndex = lastIndex - maxTopCount;
        //当前用户是否在前三名
        boolean isUserTopRanking = listCurIndex < 0 || listLastIndex < 0;
        //不在前三名先交换位置(后台数据为最新数据，需要将新排名交换到上一次排名的位置)
        if( !isUserTopRanking && currentIndex >= 0 && lastIndex >= 0 ) {
            Collections.swap( list, currentIndex, lastIndex );
        }
        tvHaveRankingTips.setVisibility( currentIndex > 0 ? View.VISIBLE : View.GONE );
        //添加前三名到领奖台
        for( int i = 0; i < list.size(); i++ ) {
            int rank = ( i + 1 );
            setTopRanking( rank, list.get( i ) );
            if( rank == maxTopCount ) break;
        }
        //截取前三名之后的排名
        if( list.size() > maxTopCount ) {
            list = list.subList( maxTopCount, list.size() );
        }
        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyItemRangeChanged(0, list.size() );
        //不展示动画
        if( list.size() == 0 || list.size() < maxTopCount ) return;
        post( () -> {
            //前三名动画
            if( isUserTopRanking ) {
                startRankingAnimOfTop3( currentIndex );
                return;
            }
            //列表排名动画
            startRankingAnimOfList( listCurIndex, listLastIndex );
        }, 600);

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

    private void startRankingAnimOfTop3(int curIndex) {
        View[] topRankings = { llTopRanking1, llTopRanking2, llTopRanking3 };
        View vTopRanking = curIndex >= 0 && curIndex < topRankings.length ?
                topRankings[ curIndex ] : null;
        if( vTopRanking == null ) return;

        float y = -Utils.dp2Px( this, 25 );
        ValueAnimator valAnim = ValueAnimator.ofFloat( y, vTopRanking.getTranslationY() );
        valAnim.setDuration( 1000 );
        //小球插值器
        valAnim.setInterpolator( new BounceInterpolator() );
        valAnim.addUpdateListener( anim ->
                vTopRanking.setTranslationY( (float) anim.getAnimatedValue() )
        );
        valAnim.start();
    }

    private void startRankingAnimOfList(int curIndex, int lastIndex) {
        if( lastIndex < mAdapter.getItemCount() ) {
            RankingAdapter.ItemData removeData = mAdapter.getItemData( lastIndex );
            int height = Utils.dp2Px( this, 70 );// Item高度 -> 60：item高 + 10：间距
            int lastScrollY = getCenterItemY( lastIndex, height );
            //移动到旧排名
            srvList.scrollBy( 0, lastScrollY );
            post( () -> {
                //移除旧排名
                mAdapter.removeItemData( removeData );
                mAdapter.notifyItemRemoved( lastIndex );
                post( () -> {
                    //滑动到新的排名
                    srvList.smoothScrollBy(
                            0,
                            getCenterItemY( curIndex, height ) - lastScrollY - height,
                            null, 900
                    );
                    //添加到新的排名
                    post( () -> {
                        mAdapter.addItemData( curIndex, removeData );
                        mAdapter.notifyItemInserted( curIndex );
                        if( curIndex <= mAdapter.getHolderCount() ) {
                            srvList.smoothScrollBy( 0, -height );
                        }
                        //更新排名
                        post( () -> mAdapter.notifyDataSetChanged(), 250 );
                    }, 900);
                }, 900);

            }, 100);
        }
    }

    private int getCenterItemY(int index, int itemHeight) {
        int showCount = mAdapter.getHolderCount();
        if( showCount > 0 ) showCount = ( (int)Math.ceil( (double)showCount / 2D ) ) - 1;
        return ( itemHeight * ( index - showCount ) ) - ( itemHeight / 2 );//(70*(0-2))-(70/2)=-175
    }

    @Override
    public int onStatusBarIconColor() {
        return SysUtil.StatusBarIconColor.WHITE;
    }
}
