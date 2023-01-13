package com.gjjy.steerlib.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Space;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.steerlib.mvp.presenter.GuidePresenter;
import com.gjjy.steerlib.ui.fragment.GuideFrontFragment;
import com.gjjy.steerlib.ui.fragment.LanguageLevelFragment;
import com.gjjy.steerlib.ui.fragment.NeedLoginFragment;
import com.gjjy.steerlib.ui.fragment.ObjectiveFragment;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.widget.FragmentViewPager;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.steerlib.R;
import com.gjjy.steerlib.mvp.view.GuideView;
import com.gjjy.basiclib.utils.SpIO;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 引导页面
 */
@Route(path = "/guide/guideActivity")
public class GuideActivity extends BaseActivity implements GuideView {
    @Presenter
    private GuidePresenter mPresenter;

    private Space sStatusBarHeight;
    private View vLayout;
    private Toolbar tbToolbar;
    private FragmentViewPager fvpPager;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_guide );
        mPresenter.onIntent( getIntent() );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        onCallShowLoadingDialog( false );
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if( fvpPager.getCurrentItem() == 0 ) {
            if( mPresenter.isInitPage() ) {
                getStackManage().exitApp( this );
                return;
            }
            setInitResult( false );
            super.onBackPressed();
        }else {
            int position = fvpPager.getCurrentItem() - 1;
            if( !skipFront( position ) ) fvpPager.setCurrentItem( position );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == StartUtil.REQUEST_CODE_LOGIN ) {
            //登录成功时关闭当前页面
            if( resultCode == Activity.RESULT_OK ) {
                setResult( RESULT_OK );
                mPresenter.setLoginSuccess( true );
                //1.2.4开通会员
                StartUtil.startBuyVipActivity( getActivity() );
                finish();
            }
        }
    }

    private void initView() {
        sStatusBarHeight = findViewById( R.id.toolbar_height_space );
        vLayout = findViewById( R.id.guide_ll_layout );
        tbToolbar = findViewById( R.id.guide_tb_toolbar );
        fvpPager = findViewById( R.id.guide_fvp_main_pager );
        tbToolbar.setBackgroundColor( Color.TRANSPARENT );
//        tbToolbar.setVisibility( View.GONE );
    }

    private void initData() {
        tbToolbar.setBackBtnOfImg( R.drawable.ic_guide_back_btn );
        tbToolbar.setVisibility( View.INVISIBLE );
        vLayout.setBackgroundResource( R.color.colorMain );
        mLoadingDialog = createLoadingDialog();
        fvpPager.setEnableScroll( false );
        fvpPager.setFragmentActivity( this )
                .setFragments(
                        new GuideFrontFragment(),           //引导
                        new LanguageLevelFragment(),        //语言程度
                        new ObjectiveFragment(),            //学习目的
                        new NeedLoginFragment()             //可选登录
                );
        onCallShowLoadingDialog( true );
        fvpPager.notifyAdapter();
        post(() -> {
            if( !mPresenter.isShowGuideFrontPage() ) fvpPager.setCurrentItem( 1 );
            onCallShowLoadingDialog( false );
        });
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> onBackPressed());

        fvpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                vLayout.setBackgroundResource(
                        position == 0 /*|| position == 3*/ ?
                                R.color.colorMain :
                                R.color.colorWhite
                );
                tbToolbar.setVisibility( position == 0 ? View.INVISIBLE : View.VISIBLE );

                if( position == 0 ) {
                    setCancelStatusBarHeightForSpace( sStatusBarHeight );
                }else {
                    setStatusBarHeightForSpace( sStatusBarHeight );
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    @Override
    public boolean onEnableFullScreen() { return true; }

    public void onSelectedType(int position, int type) {
        if( skipFront( position ) ) return;
        mPresenter.setSelectedType( position, type );
        boolean isEnd = fvpPager.getCurrentItem() == 2;
        //提交
        if( isEnd ) mPresenter.endSelected();
        //下一页
        if( mPresenter.isShowLoginPage() || !isEnd ) {
            fvpPager.setCurrentItem( position + 1 );
        }else {
            setInitResult( true );
            post( this::finish );
        }
        LogUtil.i("onSelectedType -> " +
                "position:" + position +
                " | type:" + type +
                " | current:" + fvpPager.getCurrentItem() +
                " | count:" + ( fvpPager.getFragmentCount() - 1 ) +
                " | isEnd:" + isEnd +
                " | isShowLoginPage:" + mPresenter.isShowLoginPage()
        );
    }

//    @Override
//    public void onCallGuidResult(boolean result) {
//        if( !result ) showToast( R.string.stringError );
//    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            if( !mLoadingDialog.isShowing() ) mLoadingDialog.show();
        } else {
            if( mLoadingDialog.isShowing() ) mLoadingDialog.dismiss();
        }
    }

    private boolean skipFront(int position) {
        //跳过引导页
        if( !mPresenter.isShowGuideFrontPage() && position == 0 ) {
            finish();
            return true;
        }
        return false;
    }

    private void setInitResult(boolean result) {
        setResult( result ? RESULT_OK : RESULT_CANCELED );
        if( result ) SpIO.clearStatusAll( this );
    }
}
