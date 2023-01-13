package com.gjjy.frontlib.ui.activity;

import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.toast.Build;
import com.ybear.ybutils.utils.toast.ToastManage;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.adapter.FastReViewAdapter;
import com.gjjy.frontlib.mvp.presenter.FastReViewPresenter;
import com.gjjy.frontlib.mvp.view.FastReViewView;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * 快速复习页面
 */
@Route(path = "/front/fastReViewActivity")
public class FastReViewActivity extends BaseActivity implements FastReViewView {
    @Presenter
    private FastReViewPresenter mPresenter;
    private Toolbar tbToolbar;
    private RecyclerView rvList;
    private FastReViewAdapter mAdapter;
//    private TextView tvNotCourses;
    private DialogOption mLoadingDialog;
    private Build mToastBuild;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_review);
        initView();
        initData();
        initListener();
    }

    //    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        showEndFastReViewDialog(v -> finish());
//    }

    private void initView() {
        tbToolbar = findViewById( R.id.fast_review_tb_toolbar );
        rvList = findViewById( R.id.fast_review_rv_list );
//        tvNotCourses = findViewById( R.id.fast_review_tv_not_courses );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();

//        List<FastReViewAdapter.ItemData> mDataList = new ArrayList<>();
        mAdapter = new FastReViewAdapter( Glide.with( this ), new ArrayList<>() );
        rvList.setLayoutManager( new LinearLayoutManager( this ) );
        rvList.setAdapter( mAdapter );

        int lrPad = Utils.dp2Px( this, 14 );
        int tbPad = Utils.dp2Px( this, 19 );
        mToastBuild = new Build()
                .setTextSize( 13 )
                .setTextColorResources( this, R.color.colorError )
                .setBackgroundResource( R.color.colorErrorBg2 )
                .setGravity( Gravity.CENTER )
                .setRadius( Utils.dp2Px( this, 5 ) )
                .setPadding( lrPad, tbPad, lrPad, tbPad );
//        ToastManage.get().showToast();

//        String[] texts = {
//                "Comprehensive",
//                "Travel",
//                "Shopping",
//                "Workplace",
//                "School"
//        };
//        for (int i = 0; i < texts.length; i++) {
//            FastReViewAdapter.ItemData data = new FastReViewAdapter.ItemData();
//            data.setTitle( texts[ i ] );
//            data.setEnable( i % 2 == 0 );
//
//            data.setIcon( ResUtil.getDrawable(
//                    this,
//                    ( data.isEnable() ?
//                            "ic_fast_review_" : "ic_fast_review_dis_" ) +
//                            ( i + 1 )
//            ));
//            mDataList.add( data );
//        }
//        mAdapter.notifyItemRangeInserted( 0, mDataList.size() );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        mAdapter.setOnItemClickListener((adapter, view, data, i) -> {
//                showToast(itemData.getTitle());
            StartUtil.startAnswerActivityOfFastReview(
                    new AnswerBaseEntity().setCategoryId( data.getId() )
            );
            mPresenter.buriedPoint( data.getId(), data.getTitle() );
        });
    }

    @Override
    public void onCallDataList(@NonNull List<FastReViewAdapter.ItemData> list) {
        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();

        boolean isShow = true;
        for (FastReViewAdapter.ItemData itemData : list) {
            if( !itemData.isEnable() ) continue;
            isShow = false;
            break;
        }
        if( isShow ) {
            ToastManage.get().showToastForLong(
                    this, R.string.stringFastReviewNotCourses, mToastBuild
            );
        }
        onCallShowLoadingDialog( false );
//        tvNotCourses.setVisibility( isShow ? View.VISIBLE : View.GONE );
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( isShow ) {
            mLoadingDialog.show();
        }else {
            mLoadingDialog.dismiss();
        }
    }
}
