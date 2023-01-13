package com.gjjy.usercenterlib.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.widget.ContentWebView;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.usercenterlib.R;
import com.ybear.mvp.annotations.Model;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

/**
 * 消息通知页面
 */
@Route(path = "/message/msgDetailsActivity")
public class MsgDetailsActivity extends BaseActivity {
    private Toolbar tbToolbar;
    private ContentWebView cwvWebView;

    @Model
    private UserModel mUserModel;
//    @Model
//    private ReqMessageModel mMsgModel;

    private long mViewDuration;
    private int mId;
    private String mTitle;
//    private String mJPushId;
//    private String mUrl;

    private DialogOption mLoadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_msg_details);
        initIntent();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        //埋点详情页
        BuriedPointEvent.get().onNotificationListPageOfNotificationDetailsPage(
                this,
                mId,
                mTitle,
                mUserModel.getUid(),
                mUserModel.getUserName( getResources() ),
                System.currentTimeMillis() - mViewDuration
        );
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        dismissLoadDialog();
    }

    private void initIntent() {
        Intent intent = getIntent();
        if( checkIsFinish( intent ) ) return;

        mId = intent.getIntExtra( Constant.ID_FOR_INT, -1 );
        mTitle = intent.getStringExtra( Constant.NAME );
//        mUrl = intent.getStringExtra( Constant.URL );

//        if( mId == -1 || TextUtils.isEmpty( mUrl ) ) {
//            checkIsFinish( mJPushId = intent.getStringExtra( Constant.ID_FOR_STRING ) );
//        }
    }

    private void initView() {
        tbToolbar = findViewById( R.id.msg_details_tb_toolbar );
        cwvWebView = findViewById( R.id.msg_details_cwv_web_view );

        cwvWebView.getSettings().setMinimumFontSize( 46 );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        tbToolbar.setBackgroundColor( Color.WHITE );
        mLoadDialog = createLoadingDialog();

        mViewDuration = System.currentTimeMillis();

        refreshTitle();

        doLoadUrl();
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        cwvWebView.setBackgroundColor(getResources().getColor(R.color.colorNotifyBg));
        cwvWebView.setWebViewClientListener( new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished( view, url );
                dismissLoadDialog();
            }
        });
    }

    private boolean checkIsFinish(Object obj) {
        if( obj == null || ( ( obj instanceof String ) && TextUtils.isEmpty( (String) obj ) ) ) {
            post(this::finish);
            return true;
        }
        return false;
    }

    private void refreshTitle() {
        post(() -> {
            if( !TextUtils.isEmpty( mTitle ) ) {
                tbToolbar.setTitle( mTitle ) ;
            }
        });
    }

    private void doLoadUrl() {
        showLoadDialog();
        cwvWebView.loadUrl( Config.URL_MSG_DETAILS + mId );
        LogUtil.d( "MsgDetails -> doLoadUrl -> " + ( Config.URL_MSG_DETAILS + mId ) );
//        showLoadDialog();
//        mMsgModel.reqMsgDetailById(mId, data -> {
//            dismissLoadDialog();
//
////                mUrl = data.getContent();
////            mUrl = Config.URL_MSG_DETAILS + data.getId();
////            if( checkIsFinish( mUrl ) ) return;
//
//            mId = data.getId();
//            if( mTitle == null ) {
//                mTitle = data.getTitle();
//                refreshTitle();
//            }
//            String url = Config.URL_MSG_DETAILS + mId;
//            post(() -> {
//                cwvWebView.loadUrl( url );
//                LogUtil.d( "MsgDetails -> doLoadUrl -> " + url );
//            });
//        });
//        if( TextUtils.isEmpty( mJPushId ) ) {
//            dismissLoadDialog();
//            cwvWebView.loadUrl( mUrl );
//            LogUtil.d( "MsgDetails -> doLoadUrl -> " + mUrl );
//        }else {
//            showLoadDialog();
//            mMsgModel.reqMsgDetailById(mJPushId, data -> {
//                dismissLoadDialog();
//
////                mUrl = data.getContent();
//                mUrl = Config.URL_MSG_DETAILS + data.getId();
//                if( checkIsFinish( mUrl ) ) return;
//
//                mId = data.getId();
//                if( mTitle == null ) {
//                    mTitle = data.getTitle();
//                    refreshTitle();
//                }
//                post(() -> cwvWebView.loadUrl( mUrl ));
//
//                LogUtil.d( "MsgDetails -> doLoadUrl -> " + mUrl );
//            });
//        }
    }

    private void showLoadDialog() {
        if( mLoadDialog != null && !mLoadDialog.isShowing() ) post( () -> mLoadDialog.show() );
    }

    private void dismissLoadDialog() {
        if( mLoadDialog != null && mLoadDialog.isShowing() ) post( () -> mLoadDialog.dismiss() );
    }
}
