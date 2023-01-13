package com.gjjy.xuechinese.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.util.Consumer;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.utils.SpIO;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.xuechinese.R;
import com.gjjy.xuechinese.mvp.model.PathType;
import com.gjjy.xuechinese.mvp.presenter.MainPresenter;
import com.gjjy.xuechinese.mvp.view.MainView;
import com.gjjy.discoverylib.ui.fragment.DiscoveryFragment;
import com.gjjy.frontlib.ui.fragment.FrontFragment;
import com.gjjy.usercenterlib.ui.fragment.UserCenterFragment;
import com.ybear.mvp.annotations.BindView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.ItemStyleBuild;
import com.ybear.ybcomponent.widget.FragmentViewPager;
import com.ybear.ybcomponent.widget.menu.MenuItem;
import com.ybear.ybcomponent.widget.menu.MenuView;
import com.ybear.ybnetworkutil.network.NetworkChangeManage;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.DialogOption;

@Route(path = "/main/mainActivity")
public class MainActivity extends BaseActivity implements MainView {
    @Presenter
    private MainPresenter mPresenter;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.main_fvp_main_view_pager)
    private FragmentViewPager fvpPager;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.main_mv_menu_view)
    private MenuView mvMenuView;
    //默认进入的页面
    private final int mDefItemPos = 1;
    
//    private FrameAnimation.FrameRequest mMenuSelectFrameAnim;
//    private FrameAnimation.FrameRequest mMenuUnSelectFrameAnim;
    private DialogOption mFrontInitGuideDialog;
    private DialogOption mDiscoveryInitGuideDialog;
    private DialogOption mMeInvitationCodeInitGuideDialog;
    private DialogOption mFeedbackDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //注册网络监听
        NetworkChangeManage.get().registerService();
        mPresenter.initIntent( getIntent() );
        //初始化引导页
        mPresenter.startLoginCheckActivity();
        initData();
        initListener();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        String exitStr = mPresenter.getDoubleBackPressedExitString();
        getStackManage().doubleBackPressedExit( this, exitStr, result -> {
            if( result ) mPresenter.unNetworkService();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeStatusBarIconColor( fvpPager.getCurrentItem() );
    }

    @Override
    protected void onDestroy() {
        if( mFeedbackDialog != null && mFeedbackDialog.isShowing() ) mFeedbackDialog.cancel();
        if( mFrontInitGuideDialog != null ) mFrontInitGuideDialog.dismiss();
        onExitApp();
        super.onDestroy();
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        switch( id ) {
            case DOMConstant.LOG_OFF:                               //退出登录
                post(() -> mPresenter.logOut());
                break;
            case DOMConstant.INIT_INIT_GUIDE:                       //引导页
                mPresenter.checkVersion();
                break;
            case DOMConstant.ANSWER_NEXT_SAVE_PROGRESS_SUCCESS:     //答题进度保存结果
                if( !ObjUtils.parseBoolean( data ) ) break;
                mFeedbackDialog = showFeedbackDialog( mPresenter.getUserId(), mPresenter.getEmail() );
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fvpPager.getFragment( fvpPager.getCurrentItem() ).onActivityResult( requestCode, resultCode, data );
        LogUtil.e("MainActivity -> onActivityResult -> " +
                "requestCode:" + requestCode + " | " +
                "resultCode:" + resultCode
        );
        //退出账号的后续操作
        mPresenter.logOutOfOnActivityResult( requestCode, resultCode );

        switch ( requestCode ) {
            case StartUtil.REQUEST_CODE_LOGIN_ERROR:
                if( data == null ) break;
                //未绑定账号的情况下启动引导页
                int result = data.getIntExtra( Constant.LOGIN_CALL_RESULT, 1 );
                if( result == 0 ) {
                    StartUtil.startGuideActivity(this, true, false);
                }
                break;
            case StartUtil.REQUEST_CODE_GO_TO_HOME:
                if( resultCode == RESULT_OK ) post( this::onCallToFront );
                break;
        }
    }

    private void initData() {
//        setStatusBarHeightForPadding( fvpPager );
        fvpPager.setFragmentActivity( this )
                .setEnableVisibleChanged( true )
                .setFragments(
                        new FrontFragment(),                //主页
                        new DiscoveryFragment(),            //发现
//                        new TargetedLearningFragment(),     //专项学习
                        new UserCenterFragment()            //个人中心
                );
        fvpPager.setOffscreenPageLimit( 3 );
        fvpPager.notifyAdapter();

        mvMenuView.setItemStyle(new ItemStyleBuild()
                .setIconWidth( 22 )
                .setIconHeight( 22 )
                .setTextIncludeFontPadding( false )
                .setTextColor( R.color.color95 )
                .setTextSelectColor( R.color.colorMain )
                .setTextSize( 13 )
//                .setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) )
        );

        mvMenuView.setMenuItem(
                new MenuItem( getResources(),
                        R.drawable.ic_menu_front_def_0, R.string.menuFront,
                        R.drawable.ic_menu_front_touch_0, R.string.menuFront
                ),
                new MenuItem( getResources(),
                        R.drawable.ic_menu_discover_def_0, R.string.menuDiscovery,
                        R.drawable.ic_menu_discover_touch_0, R.string.menuDiscovery
                ),
                new MenuItem( getResources(),
                        R.drawable.ic_menu_user_center_def_0, R.string.menuUserCenter,
                        R.drawable.ic_menu_user_center_touch_0, R.string.menuUserCenter
                )
        );
        mvMenuView.setBackgroundResource( com.gjjy.frontlib.R.color.colorWhite );

        mvMenuView.setEnableTopLine( true );
        mvMenuView.setTopLineColor( getResources(), R.color.colorMainBG );
        mvMenuView.notifyMenuChanged();

        /* 默认进入的页面 */
        fvpPager.setCurrentItem( mDefItemPos );
        mvMenuView.setSelectItem( mDefItemPos, false );
    }

    private void initListener() {
        fvpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                mvMenuView.setSelectItem( position, false );
                changeStatusBarIconColor( position );
                switch( position ) {
                    case 0:     //初始化发现页新手引导
                        mPresenter.checkFrontInitGuide();
                        break;
                    case 1:     //Find埋点
                        mPresenter.buriedPointClickFindPage();
                        break;
                    case 2:     //用户中心新手引导页
                        mPresenter.checkMeInvitationCodeInitGuide();
                        break;
                }
//                //Find埋点
////                if( position == 1 ) {
////                    //初始化发现页新手引导
////                    mPresenter.checkFindInitGuide();
//                    mPresenter.buriedPointClickFindPage();
//                }
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        Consumer<Boolean> doubleTouchListener = isDouble -> {
            if( !isDouble ) {
                fvpPager.setCurrentItem( mvMenuView.getCurrentPosition() );
                return;
            }
            switch ( fvpPager.getCurrentItem() ) {
                case 0:
                    setCallResult( DOMConstant.NOTIFY_FRONT_LIST, 0 );
                    break;
                case 1:
                    setCallResult( DOMConstant.NOTIFY_DISCOVERY_LIST );
                    break;
//                case 2:
//                    setCallResult( DOMConstant.NOTIFY_targeted_learning_LIST );
//                    break;
                case 2:
                    setCallResult( DOMConstant.NOTIFY_USER_CENTER_LIST );
                    break;
            }
        };

        mvMenuView.setOnCheckedChangeListener((menuView, v, curPos, oldPos) -> {
            if( fvpPager.getCurrentItem() == curPos ) {
                Utils.doubleClickListener( doubleTouchListener );
            } else {
                fvpPager.setCurrentItem( curPos );
            }
        });

//        mMenuSelectFrameAnim = FrameAnimation.create()
//                .loop( 1 )
//                .time( 25 );
//        mMenuUnSelectFrameAnim = FrameAnimation.create()
//                .loop( 1 )
//                .time( 25 );

//        String[] menuItemRes = {
//                "ic_menu_front_touch_", "ic_menu_front_def_",
//                "ic_menu_discover_touch_", "ic_menu_discover_def_",
//                "ic_menu_user_center_touch_", "ic_menu_user_center_def_"
//        };
//
//        mvMenuView.setOnInterceptItemTouchListener( (menuView, v, curPosition, oldPosition) -> {
//            if( curPosition == oldPosition ) return InterceptType.IMAGE;
//            ImageView vCur = mvMenuView.getChildViewOfImageView( curPosition );
//            ImageView vOld = mvMenuView.getChildViewOfImageView( oldPosition );
//
//            if( vCur != null ) {
//                mMenuSelectFrameAnim
//                        .load( this, menuItemRes[ curPosition * 2 ], 0, 30 )
//                        .into( vCur )
//                        .play( this );
//            }
//            if( vOld != null ) {
//                mMenuUnSelectFrameAnim
//                        .load( this, menuItemRes[ ( oldPosition * 2 ) + 1 ], 0, 30, true )
//                        .into( vOld )
//                        .play( this );
//            }
//            return InterceptType.IMAGE;
//        } );
    }

    private void changeStatusBarIconColor(int position) {
        setStatusBarIconColor(
//                position != mvMenuView.getChildCount() - 1 ?
                position == 1 ?
                        SysUtil.StatusBarIconColor.BLACK :
                        SysUtil.StatusBarIconColor.WHITE
        );
    }

    private DialogOption mUpdateDialog;
    private DialogOption mAnnouncementDialog;
    @Override
    public void onCheckUpdate(String version, String content, String url, boolean isCancelable) {
        if( mUpdateDialog != null && !mUpdateDialog.isShowing() ) {
//            isInitGuide = false;
            mUpdateDialog.show();
            return;
        }

        String title = String.format( getString( R.string.stringUpdateTitle ), version );
        mUpdateDialog = showUpdateDialog(title, content, isCancelable, v -> mPresenter.updateJumpEvent());
        mUpdateDialog.setOnDismissListener( dialog -> mPresenter.checkAnnouncement() );
    }

    @Override
    public void onCheckAnnouncement(String content) {
        content = Config.mAnnouncementUrl;
        if( mAnnouncementDialog != null && !mAnnouncementDialog.isShowing() ) {
//            isInitGuide = false;
            mAnnouncementDialog.show();
            return;
        }
        mAnnouncementDialog = showAnnouncementDialog( content );
        mAnnouncementDialog.setOnDismissListener( dialog -> mPresenter.checkFindInitGuide() );
    }

    @UiThread
    @Override
    public void onCallToFront() {
        post(() -> {
            fvpPager.setCurrentItem( mDefItemPos );
            setCallResult( DOMConstant.NOTIFY_FRONT_LIST, 0 );
        }, 350 );
    }

    @Override
    public void onCallDispatchScheme(int type, int id, String videoId,String name) {
        switch( type ) {
            case PathType.LISTEN_DAILY:                //每日聆听
                com.gjjy.discoverylib.utils.StartUtil.startListenDailyDetailsActivity( id, videoId,name );
                break;
            case PathType.POPULAR_VIDEOS:              //热门视频
                com.gjjy.discoverylib.utils.StartUtil.startPopularVideosDetailsActivity( id, videoId,name );
                break;
            case PathType.TARGETED_LEARNING:          //专项学习
                com.gjjy.discoverylib.utils.StartUtil.startTargetedLearningDetailsActivity( id ,videoId);
                break;
        }
        LogUtil.e( "dsm -> onCallDispatchScheme -> type:" + type + " | id:" + id + " | videoId:" + videoId + " | name:" + name );
    }

    @Override
    public void onCallShowFrontInitGuide() {
        if( mFrontInitGuideDialog != null ) {
//            mPresenter.checkVersion();
            return;
        }
        mFrontInitGuideDialog = showFrontInitGuideDialog( view -> {
            SpIO.saveFrontInitGuideStatus( this );
//            mPresenter.checkVersion();
        });
    }

    @Override
    public void onCallShowFindInitGuide() {
        if( mDiscoveryInitGuideDialog != null ) return;
        mDiscoveryInitGuideDialog = showFindInitGuideDialog(
                view -> SpIO.saveFindInitGuideStatus( this )
        );
    }

    @Override
    public void onCallShowMeInvitationCodeInitGuide() {
        BaseActivity activity = (BaseActivity) getActivity();
        if( activity == null || mMeInvitationCodeInitGuideDialog != null ) return;
        mMeInvitationCodeInitGuideDialog = activity.showMeInvitationCodeInitGuideDialog(view ->
                SpIO.saveMeInvitationCodeInitGuideStatus( activity )
        );
    }

    @Override
    public boolean onEnableImmersive() {
        return true;
    }
}