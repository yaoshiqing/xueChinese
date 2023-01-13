package com.gjjy.discoverylib.ui.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;
import com.gjjy.discoverylib.mvp.presenter.MyCoursePresenter;
import com.gjjy.discoverylib.mvp.view.MyCourseView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.ViewPagerAdapter;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.widget.MyCourseList;
import com.gjjy.basiclib.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/discovery/myCourseActivity")
public class MyCourseActivity extends BaseActivity implements MyCourseView {
    @Presenter
    private MyCoursePresenter mPresenter;

    private Toolbar tbToolbar;
    private RadioGroup rgTable;
    private ImageView ivTableDiv;
    private ViewPager vpPager;
    private TextView tvNotDataTips;
    private MyCourseList mCourseListOfCollect;
    private MyCourseList mCourseListOfHaveRead;

    private ValueAnimator vaTableDiv;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_my_course );
        initView();
        initData();
        initListener();

        rgTable.check( R.id.my_course_list_rb_collect_btn );
        post( () -> {
            switchTableDiv( R.id.my_course_list_rb_collect_btn, false );
            switchShowNotDataTips( 0 );
        }, 200);
    }

    private void initView() {
        tbToolbar = findViewById( R.id.my_course_list_tb_toolbar );
        rgTable = findViewById( R.id.my_course_list_rg_table );
        ivTableDiv = findViewById( R.id.my_course_list_iv_table_div );
        vpPager = findViewById( R.id.my_course_list_vp_page );
        tvNotDataTips = findViewById( R.id.my_course_list_tv_not_data );
    }

    private void initData() {
        setStatusBarHeightForSpace( findViewById( R.id.toolbar_height_space ) );
        mLoadingDialog = createLoadingDialog();
        tbToolbar.setTitle( R.string.stringMyCoursesMainTitle );

        vaTableDiv = new ValueAnimator();
        vaTableDiv.setDuration( 200 );

        List<MyCourseList> list = new ArrayList<>();
        list.add( mCourseListOfCollect = new MyCourseList( this ) );
        list.add( mCourseListOfHaveRead = new MyCourseList( this ) );

        mCourseListOfCollect.setVisibility( View.INVISIBLE );
        mCourseListOfHaveRead.setVisibility( View.INVISIBLE );

        mCourseListOfCollect.setUid( mPresenter.getUid(), mPresenter.getUserName() );
        mCourseListOfHaveRead.setUid( mPresenter.getUid(), mPresenter.getUserName() );

        vpPager.setAdapter( new ViewPagerAdapter<>( list ) );
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

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener( view -> onBackPressed() );

        rgTable.setOnCheckedChangeListener((group, checkedId) ->
                vpPager.setCurrentItem( checkedId == R.id.my_course_list_rb_collect_btn ? 0 : 1 )
        );

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                int id = position == 0 ?
                        R.id.my_course_list_rb_collect_btn :
                        R.id.my_course_list_rb_have_read_btn;
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

        mCourseListOfCollect.setOnRefreshListener(refreshLayout ->
                mPresenter.queryCollectDataList()
        );
        mCourseListOfCollect.setOnLoadMoreListener(refreshLayout ->
                mPresenter.nextCollectDataList()
        );

        mCourseListOfHaveRead.setOnRefreshListener(refreshLayout ->
                mPresenter.queryHaveReadDataList()
        );
        mCourseListOfHaveRead.setOnLoadMoreListener(refreshLayout ->
                mPresenter.nextHaveReadDataList()
        );
    }

    @Override
    public void onCallCancelCollectId(long id) {
        mCourseListOfCollect.removeData( id );
    }

    @Override
    public void onCallCollectDataList(int page, List<DiscoveryListAdapter.ItemData> list, boolean isVip) {
        mCourseListOfCollect.setVip( isVip );
        mCourseListOfCollect.setDataList( page, list );
        if( list != null && list.size() > 0 ) {
//            tvNotDataTips.setVisibility( View.GONE );
            mCourseListOfCollect.setVisibility( View.VISIBLE );
        }
        vpPager.setCurrentItem( 0 );
    }

    @Override
    public void onCallHaveReadDataList(int page, List<DiscoveryListAdapter.ItemData> list, boolean isVip) {
        mCourseListOfHaveRead.setVip( isVip );
        mCourseListOfHaveRead.setDataList( page, list );
        if( list != null && list.size() > 0 ) {
//            tvNotDataTips.setVisibility( View.GONE );
            mCourseListOfHaveRead.setVisibility( View.VISIBLE );
        }
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            if( !mLoadingDialog.isShowing() ) mLoadingDialog.show();
            return;
        }
        mLoadingDialog.dismiss();
    }

    private void switchShowNotDataTips(int pos) {
        int icon = -1;
        int contentId = -1;
        switch ( pos ) {
            case 0:
                if( mCourseListOfCollect.isEmpty() ) {
                    icon = R.drawable.ic_my_course_collect_not_data;
                    contentId = R.string.stringMyCourseNotDataOfCollect;
                }
                break;
            case 1:
                if( mCourseListOfHaveRead.isEmpty() ) {
                    icon = R.drawable.ic_my_course_have_read_not_data;
                    contentId = R.string.stringMyCourseNotDataOfHistory;
                }
                break;
        }
        if( icon == -1 || contentId == -1 ) {
            tvNotDataTips.setVisibility( View.GONE );
            return;
        }
        tvNotDataTips.setCompoundDrawablesWithIntrinsicBounds( 0, icon, 0, 0 );
        tvNotDataTips.setText( contentId );
        tvNotDataTips.setVisibility( View.VISIBLE );
    }
}
