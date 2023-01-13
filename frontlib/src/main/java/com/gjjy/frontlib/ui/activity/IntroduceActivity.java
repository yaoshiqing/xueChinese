package com.gjjy.frontlib.ui.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.frontlib.mvp.presenter.IntroducePresenter;
import com.gjjy.frontlib.ui.fragment.AnswerEndFragment;
import com.gjjy.frontlib.ui.fragment.ContentWebViewFragment;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.mvp.view.fragment.MvpFragment;
import com.ybear.ybcomponent.base.adapter.OnViewPagerAdapterListener;
import com.ybear.ybcomponent.widget.FragmentViewPager;
import com.ybear.ybcomponent.widget.ViewPager;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.view.IntroduceView;
import com.gjjy.frontlib.widget.AnswerBar;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 综合类 第一模块介绍页
 */
@Route(path = "/front/introduceActivity")
public class IntroduceActivity extends BaseActivity implements IntroduceView {
    @Presenter
    private IntroducePresenter mPresenter;
    private AnswerBar abAnswerBar;
    private FragmentViewPager vgPager;
    private CheckButton cbCheckBtn;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_introduce );
        initView();
        initData();
        initListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch ( vgPager.getCurrentItem() ) {
            case 0:
                mPresenter.buriedPointCloseButton1();
                break;
            case 1:
                mPresenter.buriedPointCloseButton2();
                break;
            case 2:
                mPresenter.buriedPointCloseButton3();
                break;
        }
    }

    private void initView() {
        abAnswerBar = findViewById( R.id.introduce_ab_answer_bar );
        vgPager = findViewById( R.id.introduce_fvp_page );
        cbCheckBtn = findViewById( R.id.introduce_cb_check_btn );
    }

    private void initData() {
        setStatusBarHeightForPadding( abAnswerBar );
        mLoadingDialog = createLoadingDialog();
        abAnswerBar.setActivity( this );
        abAnswerBar.showProgressBtn();
        abAnswerBar.showCloseBtn();
        abAnswerBar.hideProgressStar();
        abAnswerBar.hideMoreBtn();

        List<MvpFragment> list = new ArrayList<>();
        String[] urls = mPresenter.getHtmlUrls();
        for (String url : urls) {
            list.add(new ContentWebViewFragment().setUrl(url));
        }

        vgPager.setEnableScroll( false );
        vgPager.addFragmentAll( list );
        vgPager.setEnableVisibleChanged( true );
        vgPager.setFragmentActivity( this );
        vgPager.notifyAdapter();

        abAnswerBar.setCompleteIcon( R.drawable.ic_progress_lightning );
        abAnswerBar.setMaxProgress( mPresenter.getMaxProgress() );
        abAnswerBar.setProgress( 1 );

        cbCheckBtn.setText( R.string.stringContinue );
        cbCheckBtn.setEnabled( true );

        //查询领取状态
        mPresenter.reqHaveOpenLightningStatus();
    }

    private void initListener() {
        //关闭按钮点击事件监听器
        abAnswerBar.setOnCloseClickListener(v -> onBackPressed());

        vgPager.setOnViewPagerAdapterListener(new OnViewPagerAdapterListener() {
            @Nullable
            @Override
            public Object instantiateItem(@NonNull ViewGroup viewGroup, int i) {
                if( i >= 0 && i < mPresenter.getMaxProgress() ) {
                    ContentWebViewFragment f = (ContentWebViewFragment) vgPager.getFragment( i );
                    f.setUrl( mPresenter.getHtmlUrls()[ i ] );
                }
                return null;
            }

            @Override
            public boolean destroyItem(@NonNull ViewGroup viewGroup, int i, @NonNull Object o) {
                return false;
            }
        });

        vgPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                //设置进度
                if( position < mPresenter.getMaxProgress() ) {
                    abAnswerBar.setProgress( position + 1 );
                    return;
                }
                //介绍页走完时隐藏进度条
                if( position == mPresenter.getMaxProgress() ) {
                    abAnswerBar.hideProgressBtn();
                    abAnswerBar.hideProgressStar();
                    mPresenter.reqProvideUnitOpenLightning();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        cbCheckBtn.setOnClickListener( v -> {
            int nextItem = vgPager.getCurrentItem() + 1;
            if( nextItem >= vgPager.getFragmentCount() ) {
                finish();
                return;
            }
            vgPager.setCurrentItem( nextItem );
        } );
    }

    @Override
    public void onCallIsHaveOpenLightning(boolean isHave) {
        if( isHave ) {
            abAnswerBar.hideProgressStar();
        }else {
            abAnswerBar.showProgressStar();
        }
    }

    @Override
    public void onCallAddEndPager(String url, int endPage) {
        switch ( endPage ) {
            case 0:     //结束页
                vgPager.addFragment( createIntroduceEndPage() );
                break;
            case 1:     //闪电页
                vgPager.addFragment( createOpenLightningEndPage() );
                break;
        }
        vgPager.notifyAdapter();
    }

    @Override
    public void onCallLoadingDialog(boolean isShow) {
        if( isShow ) {
            mLoadingDialog.show();
            return;
        }
        mLoadingDialog.dismiss();
    }

    private MvpFragment createOpenLightningEndPage() {
        MvpFragment f = new AnswerEndFragment();
        Bundle arg = new Bundle();
        //题目类型
        arg.putInt( Constant.ANSWER_TYPE, Constant.AnswerType.NORMAL );
        //是否完成答题
        arg.putBoolean( Constant.ANSWER_RESULT, true );
        //不显示检查按钮
        arg.putBoolean( Constant.IS_SHOW_BUTTON, false );
        f.setArguments( arg );
        return f;
    }

    private MvpFragment createIntroduceEndPage() {
        MvpFragment f = new AnswerEndFragment();
        Bundle arg = new Bundle();
        arg.putInt( Constant.ANSWER_TYPE, Constant.AnswerType.INTRODUCE );
        arg.putBoolean( Constant.ANSWER_RESULT, false );
        //不显示检查按钮
        arg.putBoolean( Constant.IS_SHOW_BUTTON, false );
        f.setArguments( arg );
        return f;
    }
}
