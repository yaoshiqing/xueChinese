package com.gjjy.discoverylib.ui.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Space;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.source.VidAuth;
import com.aliyun.svideo.common.utils.FileUtils;
import com.gjjy.basiclib.entity.GetVodPlayAuthEntity;
import com.gjjy.basiclib.mvp.model.ReqOtherModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.ContentWebView;
import com.gjjy.basiclib.widget.ShareToolbar;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.mvp.presenter.DiscoveryDetailsPresenter;
import com.gjjy.discoverylib.mvp.view.FindDetailsView;
import com.gjjy.discoverylib.mvp.view.PopularVideosDetailsView;
import com.gjjy.speechsdk.PermManage;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.dialog.DialogOption;

import me.jessyan.autosize.internal.CancelAdapt;

@Route(path = "/discovery/hotVideoDetailsActivity")
public class PopularVideosDetailsActivity extends BaseActivity implements PopularVideosDetailsView, FindDetailsView, CancelAdapt {

//    static{
//        System.loadLibrary("RtsSDK");
//    }

    @Model
    private UserModel mUserModel;

    @Presenter
    private DiscoveryDetailsPresenter mPresenter;

    private Space sSpace;
    private ShareToolbar stbToolbar;
    private ContentWebView cwvWebView;
    // private VideoView vvVideo;
    private AliyunVodPlayerView mAliyunVodPlayerView;

    private DialogOption mLoadingDialog;

    /**
     * 是否鉴权过期
     */
    private boolean mIsTimeExpired = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_popular_videos_details );
        mPresenter.initIntent( getIntent() );
        //防录屏截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initView();
        initData();
        initListener();

        //权限检查
        PermManage.Perm perm = PermManage.create( this );
        perm.reqExternalStoragePerm((isGranted, name, shouldShowRequestPermissionRationale) -> {
            if( isGranted ) {
                mPresenter.requestData();
                return;
            }
            finish();
        });
    }

    @Override
    public void onBackPressed() {
//        if( vvVideo.isLandscapeScreen() ) {
//            vvVideo.exitLandscapeScreen();
//            return;
//        }

        // 横盘的情况下，变成竖屏，不退出
        if(mAliyunVodPlayerView != null && mAliyunVodPlayerView.getScreenMode() == AliyunScreenMode.Full){
            mAliyunVodPlayerView.changeScreenMode(AliyunScreenMode.Small, false);
            return;
        }

        if(mAliyunVodPlayerView != null){
            mAliyunVodPlayerView.onDestroy();
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(true);
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        vvVideo.pause();
        if(mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(false);
            mAliyunVodPlayerView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
//        mPresenter.saveCurrentProgress( vvVideo.getCurrentProgress() );
//        vvVideo.release();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
        }
        mPresenter.buriedPointDurationOfPopularVideos();
        onCallShowLoadingDialog( false );
        super.onDestroy();
    }

    private void initCacheConfig(){
        CacheConfig cacheConfig = new CacheConfig();
        GlobalPlayerConfig.PlayCacheConfig.mDir = FileUtils.getDir(this) + GlobalPlayerConfig.CACHE_DIR_PATH;
        cacheConfig.mEnable = GlobalPlayerConfig.PlayCacheConfig.mEnableCache;
        cacheConfig.mDir = GlobalPlayerConfig.PlayCacheConfig.mDir;
        cacheConfig.mMaxDurationS = GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS;
        cacheConfig.mMaxSizeMB = GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB;
        if(mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setCacheConfig(cacheConfig);
        }
    }

    private void initView() {
        sSpace = findViewById( R.id.toolbar_height_space );
        stbToolbar = findViewById( R.id.popular_videos_detail_stb_toolbar );
        cwvWebView = findViewById( R.id.popular_videos_detail_cwv_web_view );
        // vvVideo = findViewById( R.id.popular_videos_detail_vv_video );
        mAliyunVodPlayerView = findViewById(R.id.video_view);
    }

    private void initData() {
        setStatusBarHeightForSpace( sSpace );
        stbToolbar.setTitle( R.string.stringPopularVideosMainTitle );
        mLoadingDialog = createLoadingDialog();
//        vvVideo.setScreenSizeOfPortrait(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                Utils.dp2Px( getContext(), 210 )
//        ).setEnableFullScreenOfLandscape( true );

//        mPresenter.requestData();
    }

    private void initListener() {
        stbToolbar.setOnClickBackBtnListener( view -> onBackPressed() );

        stbToolbar.setOnCollectBtnClickListener(isCollect -> {
            mPresenter.buriedPointCollectionOfPopularVideos( this, isCollect );
            return mPresenter.editCollectStatus( isCollect );
        });

        stbToolbar.setOnShareBtnClickListener( v -> {
            mPresenter.share();
            mAliyunVodPlayerView.pause();
//            vvVideo.pause();
        } );

//        vvVideo.setOnFinishListener(r -> finish());

//        vvVideo.addOrientationChangedListener((orientation, angle, isPortrait) -> {
//            int vis = isPortrait ? View.VISIBLE : View.GONE;
//            setRequestedOrientation( orientation );
//            setEnableFullScreen( !isPortrait );
//            sSpace.setVisibility( vis );
//            stbToolbar.setVisibility( vis );
//            cwvWebView.setVisibility( vis );
//
////            ViewGroup.LayoutParams lp = vvVideo.getLayoutParams();
////            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
////            lp.height = isPortrait ? Utils.dp2Px( this, 209 ) : ViewGroup.LayoutParams.MATCH_PARENT;
////            vvVideo.setLayoutParams( lp );
//        });
    }

    @Override
    public void onCallTitle(String title) {
        stbToolbar.setTitle( title );
    }

    @Override
    public void onCallHtmlUrl(String url) {
        cwvWebView.loadUrl( url );
    }

    @Override
    public void onCallVideoId(String videoId, int progress, int totalSecond) {
        ReqOtherModel reqOtherModel = new ReqOtherModel();
        reqOtherModel.getVodPlayAuth(videoId, new Consumer<GetVodPlayAuthEntity>() {
            @Override
            public void accept(GetVodPlayAuthEntity getVodPlayAuthEntity) {
                if(getVodPlayAuthEntity != null){
                    String playAuth = getVodPlayAuthEntity.getPlayAuth();
                    int expiredTime = getVodPlayAuthEntity.getExpiredTime();

                    GlobalPlayerConfig.mPlayAuth = playAuth;
                    GlobalPlayerConfig.mVid = videoId;
                    GlobalPlayerConfig.mRegion = "ap-southeast-1";// cn-hongkong  ap-southeast-1

                    initCacheConfig();
                    initAliyunPlayerView();

                    // 需要在主线程做出来
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mAliyunVodPlayerView != null) {
                                VidAuth vidAuth = getVidAuth(GlobalPlayerConfig.mVid);
                                mAliyunVodPlayerView.setAuthInfo(vidAuth);
                            }
                        }
                    });
                }
            }
        });
//        vvVideo.setProgress( progress );
//        vvVideo.setDataSource( videoId );
    }

    private void initAliyunPlayerView() {
        //保持屏幕敞亮
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.enableNativeLog();
        mAliyunVodPlayerView.startNetWatch();
    }

    /**
     * 获取VidAuth
     * @param vid   videoId
     */
    private VidAuth getVidAuth(String vid){
        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(vid);
        vidAuth.setRegion(GlobalPlayerConfig.mRegion);
        vidAuth.setPlayAuth(GlobalPlayerConfig.mPlayAuth);
        return vidAuth;
    }

    @Override
    public void onCallShowShareButton(boolean isShow) {
        stbToolbar.showShareBtn( isShow );
    }

    @Override
    public void onCallCollectStatus(boolean isCollect, boolean isShowToast) {
        stbToolbar.setCollectStatus( isCollect, isShowToast );
    }

    @Override
    public void onCallShareResult(boolean result) {
        stbToolbar.showShareResult( result );
        mPresenter.buriedPointShareOfPopularVideos( this, result );
        mAliyunVodPlayerView.setAutoPlay(true);
//        vvVideo.play();
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
}
