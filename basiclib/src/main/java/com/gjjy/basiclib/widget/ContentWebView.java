package com.gjjy.basiclib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gjjy.basiclib.statistical.StatisticalManage;
import com.ybear.commonlib.NestedScrollWebView;
import com.ybear.ybnetworkutil.request.Request;
import com.ybear.ybutils.utils.LogUtil;

import java.util.Map;
import java.util.concurrent.Callable;

import static android.webkit.WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING;

public class ContentWebView extends NestedScrollWebView {
    public interface OnScrollChangeListener {
        void onScrollChange(ContentWebView v, int x, int y, int oldX, int oldY);
    }

    public static class Cookie {
        private String url;
        private Map<String, String> data;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public Map<String, String> getData() { return data; }
        public void setData(Map<String, String> data) { this.data = data; }
    }

    private String mUrl;
    private boolean isInit = false;
    private boolean enableDefaultTouchLoad = true;
    private boolean error = false;
    private Callable<Cookie> mCookieCall;
    private WebViewClient mWebViewClientListener;
    private OnScrollChangeListener mOnScrollChangeListener;

    public ContentWebView(Context context) {
        super(context);
        init();
    }
    public ContentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ContentWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        //设置client
        setWebViewClient(new WebViewClient() {
            /**
             * Url加载时
             * @param view          this
             * @param url           链接
             * @return              是否响应
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //加载点击的链接
                if( enableDefaultTouchLoad && isAccessedWeb( url ) ) view.loadUrl( url );

                if( mWebViewClientListener != null ) {
                    return mWebViewClientListener.shouldOverrideUrlLoading( view, url );
                }
                return true;
            }

            /**
             * 页面刷新结束时监
             * @param view          this
             * @param url           链接
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                if( mWebViewClientListener != null ) {
                    mWebViewClientListener.onPageFinished( view, url );
                }
                //加载完毕后显示
                if( !error ) setVisibility(View.VISIBLE);
            }

            /**
             * 发生错误时
             * @param webView       this
             * @param errorCode     错误码
             * @param description   描述
             * @param failingUrl    链接
             */
            @Override
            public void onReceivedError(WebView webView,
                                        int errorCode,
                                        String description,
                                        String failingUrl) {
                super.onReceivedError(webView, errorCode, description, failingUrl);
                if( mWebViewClientListener != null ) {
                    mWebViewClientListener.onReceivedError(
                            webView, errorCode, description, failingUrl
                    );
                }
                error = true;
                setVisibility(View.INVISIBLE);//加载失败时隐藏
            }
        });

    }
    /**
     * 初始化WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initLoad() {
//        //允许被调试
//        setWebContentsDebuggingEnabled( true );

//        setVisibility(View.INVISIBLE);                  //加载前隐藏

        //初始化Cookie
        initCookie();

        WebSettings webSet = getSettings();
        webSet.setJavaScriptEnabled( true );            //开启JavaScript
//        webSet.setLayoutAlgorithm( NARROW_COLUMNS );    //适应屏幕，内容将自动缩放（X5内核）
        webSet.setLayoutAlgorithm( TEXT_AUTOSIZING );   //适应屏幕，内容将自动缩放（系统内核）
        webSet.setLoadWithOverviewMode( true );         //缩放至屏幕大小
        webSet.setUseWideViewPort( true );              //将图片调整到适合webview的大小
        //webView埋点
        StatisticalManage.get().registerWebView( this );
//        /* 禁用所有存储 */
//        webSet.setDatabaseEnabled( false );
//        webSet.setAppCacheEnabled( false );
//        webSet.setDomStorageEnabled( false );
//        //用于测试
//        setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
//                jsResult.confirm();
//                return super.onJsAlert(webView, s, s1, jsResult);
//            }
//        });
    }

    public void setWebViewClientListener(WebViewClient l) { mWebViewClientListener = l; }


    public void initCookie(Callable<Cookie> call) { mCookieCall = call; }

    public <R extends Request> void loadUrl(R request) { loadUrl( request.toFullUrl() ); }

    @Override
    public void loadUrl(String s) {
        mUrl = s;
        LogUtil.i("loadUrl -> " + s);
        if( !isInit ) {
            isInit = true;
            initLoad();
        }
        super.loadUrl(s);
        error = false;
    }

    @Override
    public void reload() {
        super.reload();
        error = false;
        initCookie();
    }

    public void reloadUrl() {
        loadUrl( mUrl );
    }

    public boolean onBackPressed() {
        boolean isGoBack = canGoBack();
        if( canGoBack() ) goBack();
        return !isGoBack;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if( mOnScrollChangeListener != null ) {
            mOnScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        mOnScrollChangeListener = listener;
    }

    /**
     * 是否启用默认的点击加载
     * 默认启用，启用后，当点击Url后会在当前WebView中打开
     * @param enable    是否启用
     */
    public void setEnableDefaultTouchLoad(boolean enable) {
        enableDefaultTouchLoad = enable;
    }

    /**
     * 初始化Cookie
     */
    private void initCookie() {
        if( mCookieCall == null ) return;
        try {
            Cookie cookie = mCookieCall.call();
            createCookie( cookie.getUrl(), cookie.getData() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCookie(String url, Map<String, String> data) {
        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie( true );
        cm.removeAllCookie();
        for( String key : data.keySet() ) {
            cm.setCookie( url, key + "=" + data.get( key ) );
        }
        CookieSyncManager.createInstance( getContext() ).sync();
    }

    /**
     * 是否为可访问的链接
     * @param url   链接
     * @return      是否可访问
     */
    private boolean isAccessedWeb(String url) {
        return url != null && ( url.startsWith("http") || url.startsWith("https") );
    }
}
