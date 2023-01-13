//package com.gjjy.basiclib;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.os.Message;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import BaseActivity;
//import com.ybear.mvp.annotations.BindView;
//import com.ybear.ybcomponent.widget.ToolbarView;
//import com.ybear.ybmvp.annotations.BindView;
//import com.ybear.ybmvp.annotations.Presen;
//import ContentWebView;
//
///**
// * Web内容页面
// */
//public class ContentActivity extends BaseActivity {
//    @BindView(R.id.content_srwv_refresh_web_web)
//    private SwipeRefreshWebView srwvRefreshView;
//    private ContentWebView webView;
//    @BindView(R.id.content_tbv_toolbar)
//    private ToolbarView tbToolbar;
//
//    private String title, url;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_content);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        initIntent();
//        initView();
//        initData();
//        initListener();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        webView.finish();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if( webView.canGoBack() ) {
//            webView.goBack();
//        }else {
//            finish();
//        }
//    }
//
//    private void initIntent() {
//        Bundle b = getIntent().getExtras();
//        if( b == null ) return;
//        title = b.getString( "TITLE" );
//        url = b.getString( "Config" );
//    }
//
//    /**
//     * 初始化视图
//     */
//    private void initView() {
//        webView = srwvRefreshView.getWebView();
//        //显示toolbar返回按钮
//        tbToolbar.showBackBtnOfImg( true );
//        //设置状态栏高度的top padding
//        Utils.setPaddingStatusBarHeight( findViewById(R.id.content_layout) );
//    }
//
//    /**
//     * 初始化数据
//     */
//    private void initData() {
//        setEnableSwipeBack( true );
//        setToolbarTitle();
//        getThisHandler().sendEmptyMessageDelayed(10, 400);
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void initListener() {
//        //关闭页面
//        tbToolbar.setOnClickBackBtnListener(view -> onBackPressed());
//
//        //监听链接加载来重新打开点击的链接
//        webView.setShouldOverrideUrlLoadingListener((v, url) -> {
//            //拦截指定打开的url
//            mPresen.getShare().interceptStartUrl(v, url);
//            return true;
//        });
//
//        webView.setOnTouchListener((v, event) -> {
//            //侧滑退出用到的Touch监听
//            onSwipeBackTouchEvent( event );
//            return false;
//        });
//    }
//
//    private void setToolbarTitle() {
//        tbToolbar.setTitle( title == null ? "" : title );
//        tbToolbar.setTitleColor(R.color.colorWhite);
//        tbToolbar.setTitleTextSize( 36 );
//        tbToolbar.setBackgroundResource( R.color.transparent );
//        tbToolbar.setBackBtnOfImg(R.drawable.ic_toolbar_while_back);
//        tbToolbar.showBackBtnOfImg( true );
//        tbToolbar.setTitleTypeface( FontUtil.getSourceHanSansCNForMedium(getContext()) );
//    }
//
//    @Override
//    public boolean handleMessage(@NonNull Message msg) {
//        if( msg.what == 10 ) webView.loadUrl( url );
//        return super.handleMessage(msg);
//    }
//
//    @Override
//    protected int statusBarColor() {
//        return STATUS_BAR_BLACK;
//    }
//}
