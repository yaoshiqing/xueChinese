package com.gjjy.discoverylib.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView;
import com.aliyun.player.source.VidAuth;
import com.gjjy.basiclib.entity.GetVodPlayAuthEntity;
import com.gjjy.basiclib.mvp.model.ReqOtherModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.utils.SpIO;
import com.gjjy.basiclib.widget.ShareToolbar;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.TargetedLearningDetailsDialogueAdapter;
import com.gjjy.discoverylib.adapter.TargetedLearningDetailsGrammarAdapter;
import com.gjjy.discoverylib.mvp.presenter.DiscoveryDetailsPresenter;
import com.gjjy.discoverylib.mvp.view.FindDetailsView;
import com.gjjy.discoverylib.mvp.view.TargetedLearningDetailsView;
import com.gjjy.discoverylib.ui.fragment.DialogueFragment;
import com.gjjy.discoverylib.ui.fragment.GrammarFragment;
import com.gjjy.discoverylib.ui.fragment.TargetedLearningCommentsFragment;
import com.gjjy.speechsdk.PermManage;
import com.google.android.material.appbar.AppBarLayout;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.List;

@Route(path = "/discovery/TargetedLearningDetailsActivity")
public class TargetedLearningDetailsActivity extends BaseActivity
        implements TargetedLearningDetailsView, FindDetailsView {
    @Presenter
    private DiscoveryDetailsPresenter mPresenter;

    private Space sSpace;
    private ShareToolbar stbToolbar;
    private AppBarLayout ablLayout;
    // private VideoView vvVideo;
    private AliyunVodPlayerView mAliyunVodPlayerView;
    private TextView tvTitle;
    private TextView tvContent;
    private RadioGroup rgTable;
    private ImageView ivTableDiv;

    private DialogueFragment fDialogue;
    private GrammarFragment fGrammar;
    private TargetedLearningCommentsFragment fTargetedLearningComments;
    
    private ValueAnimator vaTableDiv;
    
    private DialogOption mTargetedLearningDetailsInitGuideDialog;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_targeted_learning_details );
        mPresenter.initIntent( getIntent() );
        //防录屏截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initView();
        initData();
        initFragment();
        initListener();

        //默认Dialogue页面。0：Dialogue，1：Grammar，2：Comments
       switchPage( "2".equals( mPresenter.getType() ) ?
               R.id.targeted_learning_details_rb_comments_btn : R.id.targeted_learning_details_rb_dialogue_btn,
               true
       );
    }

    @Override
    public void onBackPressed() {
//        if( vvVideo.isLandscapeScreen() ) {
//            vvVideo.exitLandscapeScreen();
//            return;
//        }
        if(mAliyunVodPlayerView != null){
            mAliyunVodPlayerView.onDestroy();
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        vvVideo.seekTo( vvVideo.getCurrentProgress() );


//        vvVideo.play();
//        post( () -> vvVideo.pause() );
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.setAutoPlay(true);
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        vvVideo.pause();
        mAliyunVodPlayerView.pause();
    }

    @Override
    protected void onDestroy() {
//        mPresenter.saveCurrentProgress( vvVideo.getCurrentProgress() );
//        vvVideo.release();

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
        }
        mPresenter.buriedPointViewDurationOfTargetedLearning( this );
        onCallShowLoadingDialog( false );
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent( ev );
        if( getContext() == null || ev.getAction() != MotionEvent.ACTION_UP ) return ret;
        if( ev.getY() <= SysUtil.getScreenTrueHeight( getContext() ) / 1.25F ) {
            SysUtil.hideKeyboard( getActivity() );
        }
        return ret;
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult( id, data );

        if( id == DOMConstant.targeted_learning_DETAIL_ABL_EXPANDED ) {
            ablLayout.setExpanded( false, true );
        }
    }

    private void initView() {
        sSpace = findViewById( R.id.toolbar_height_space );
        stbToolbar = findViewById( R.id.targeted_learning_details_stb_toolbar );
        ablLayout = findViewById( R.id.targeted_learning_details_abl_layout );
        // vvVideo = findViewById( R.id.targeted_learning_details_vv_video );
        mAliyunVodPlayerView = findViewById(R.id.targeted_learning_details_vv_video);
        rgTable = findViewById( R.id.targeted_learning_details_rg_table );
        ivTableDiv = findViewById( R.id.targeted_learning_details_iv_table_div );

        tvTitle = findViewById( R.id.targeted_learning_details_tv_title );
        tvContent = findViewById( R.id.targeted_learning_details_tv_content );
    }

    private void initData() {
        setStatusBarHeightForSpace( sSpace );
        mLoadingDialog = createLoadingDialog();
        stbToolbar.showTitle( false );

        stbToolbar.setOnCollectBtnClickListener(isCollect -> {
            mPresenter.buriedPointCollectionOfListenDaily(  this, isCollect );
            return mPresenter.editCollectStatus( isCollect );
        });

        stbToolbar.setOnShareBtnClickListener( v -> {
//            vvVideo.pause();
            mAliyunVodPlayerView.pause();
            fDialogue.pauseAudio();
            mPresenter.share();
        } );

//        vvVideo.setZOrderOnTop( false );

//        vvVideo.setScreenSizeOfPortrait(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                Utils.dp2Px( getContext(), 210 )
//        ).setEnableFullScreenOfLandscape( true );

        vaTableDiv = new ValueAnimator();
        vaTableDiv.setDuration( 200 );
    }

    private void initFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add( R.id.targeted_learning_details_fl_table_layout, fTargetedLearningComments = new TargetedLearningCommentsFragment() );
        ft.hide( fTargetedLearningComments );
        ft.add( R.id.targeted_learning_details_fl_table_layout, fGrammar = new GrammarFragment() );
        ft.hide( fGrammar );
        ft.add( R.id.targeted_learning_details_fl_table_layout, fDialogue = new DialogueFragment() );
        ft.commitAllowingStateLoss();
        //音频暂停
        post(() -> {
            fDialogue.setOnAudioPlayListener( isPlay -> {
                if( isPlay ){
//                    vvVideo.pause();
                    mAliyunVodPlayerView.pause();
                }
            });
            //权限检查
            PermManage.Perm perm = PermManage.create( this );
            perm.reqExternalStoragePerm((isGranted, name, shouldShowRequestPermissionRationale) -> {
                if( isGranted ) {
                    mPresenter.requestData();
                    return;
                }
                finish();
            });
        });
    }

    private void initListener() {
        stbToolbar.setOnClickBackBtnListener( view -> onBackPressed() );

        stbToolbar.setOnCollectBtnClickListener(isCollect -> {
            mPresenter.buriedPointCollectionOfTargetedLearning( this, isCollect );
            return mPresenter.editCollectStatus( isCollect );
        });

        stbToolbar.setOnShareBtnClickListener( v -> mPresenter.share() );

//        vvVideo.addOrientationChangedListener((orientation, angle, isPortrait) -> {
//            int vis = isPortrait ? View.VISIBLE : View.GONE;
//            setEnableFullScreen( !isPortrait );
//            setRequestedOrientation( orientation );
//            post( () -> {
//                sSpace.setVisibility( vis );
//                stbToolbar.setVisibility( vis );
//                rgTable.setVisibility( vis );
//                ivTableDiv.setVisibility( vis );
//                findViewById( R.id.targeted_learning_details_v_table_line_div ).setVisibility( vis );
//                findViewById( R.id.targeted_learning_details_fl_table_layout ).setVisibility( vis );
////                clCoordinatorLayout.setVisibility( vis );
//            } );
//        });

//        vvVideo.addVideoStatusListener( new OnVideoStatusListenerAdapter() {
//            @Override
//            public void onVideoReady() {
//                super.onVideoReady();
//                vvVideo.pause();
//            }
//
//            @Override
//            public void onVideoPlay() {
//                super.onVideoPlay();
//                //暂停对话的音频
//                fDialogue.pauseAudio();
//
//                if( mTargetedLearningDetailsInitGuideDialog != null &&
//                        mTargetedLearningDetailsInitGuideDialog.isShowing() ) {
//                    vvVideo.pause();
//                }
//                mPresenter.buriedPointPlayVideoBtn( getContext() );
//            }
//        } );

        rgTable.setOnCheckedChangeListener( (group, checkedId) -> switchPage( checkedId, false ) );

        vaTableDiv.addUpdateListener(animation ->
                ivTableDiv.setTranslationX( (float) animation.getAnimatedValue() )
        );
        vaTableDiv.addListener( new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd( animation );
                if( ivTableDiv.getVisibility() != View.VISIBLE ) {
                    ivTableDiv.setVisibility( View.VISIBLE );
                }
            }
        } );
    }

    private void switchPage(int checkedId, boolean expanded) {
        post( () -> switchTableDiv( checkedId ), 200);
        post( () -> {
            ablLayout.setExpanded( expanded, true );
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if( checkedId == R.id.targeted_learning_details_rb_dialogue_btn ) {
                /* 对话 */
                ft.show( fDialogue );
                ft.hide( fGrammar );
                ft.hide( fTargetedLearningComments );
                mPresenter.updateBuriedPointTargetedLearningViewDur( 0 );
            }else if( checkedId == R.id.targeted_learning_details_rb_grammar_btn ){
                /* 语法 */
                ft.show( fGrammar );
                ft.hide( fDialogue );
                ft.hide( fTargetedLearningComments );
                mPresenter.updateBuriedPointTargetedLearningViewDur( 1 );
            }else {
                /* 评论 */
                ft.show( fTargetedLearningComments );
                ft.hide( fDialogue );
                ft.hide( fGrammar );
                mPresenter.updateBuriedPointTargetedLearningViewDur( 2 );
            }
            ft.commitAllowingStateLoss();
        } );
    }

    private void switchTableDiv(int id) {
        post(() -> {
            View v = rgTable.findViewById( id );
            float toX = v.getX() + ( v.getWidth() / 2F ) - ( ivTableDiv.getWidth() / 2F );
            vaTableDiv.setFloatValues( ivTableDiv.getTranslationX(), toX );
            vaTableDiv.start();
        }, 10);
    }

    @Override
    public void onCallTitle(String title) {
        if( tvTitle != null ) tvTitle.setText( title );
    }

    @Override
    public void onCallIntroduction(String content) {
        if( tvContent != null ) tvContent.setText( content );
    }

    @Override
    public void onCallDialogueDataList(List<TargetedLearningDetailsDialogueAdapter.ItemData> list) {
        fDialogue.setDataList( list );
    }

    @Override
    public void onCallGrammarDataList(List<TargetedLearningDetailsGrammarAdapter.ItemData> list) {
        fGrammar.setDataList( list );
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
                    GlobalPlayerConfig.mRegion = "ap-southeast-1";

                    // 需要在主线程做出来
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            VidAuth vidAuth = getVidAuth(GlobalPlayerConfig.mVid);
                            mAliyunVodPlayerView.setAuthInfo(vidAuth);
                        }
                    });
                }
            }
        });

//        vvVideo.setProgress( progress );
//        vvVideo.setDataSource( videoId );
    }

    private VidAuth getVidAuth(String vid) {
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
    public void onCallCommentInfo(long id, int topTalkId, int topInteractType) {
        fTargetedLearningComments.queryDataList( id, topTalkId, topInteractType );
    }

    @Override
    public void onCallShareResult(boolean result) {
        stbToolbar.showShareResult( result );
        mPresenter.buriedPointShareOfTargetedLearning( this, result );
    }

    @Override
    public void onCallShowTargetedLearningDetailsInitGuide() {
        if( mTargetedLearningDetailsInitGuideDialog != null ) return;
        mTargetedLearningDetailsInitGuideDialog = showTargetedLearningDetailsInitGuideDialog(view ->
                SpIO.saveTargetedLearningDetailsInitGuideStatus( this )
        );
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
