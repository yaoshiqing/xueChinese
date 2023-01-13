package com.gjjy.frontlib.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ybear.mvp.view.fragment.MvpFragment;
import com.gjjy.frontlib.R;
import com.gjjy.basiclib.widget.ContentWebView;

public class ContentWebViewFragment extends MvpFragment {
    private String mUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_content_web_view, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ContentWebView webView = (ContentWebView) getView();
        if( webView != null ) webView.loadUrl( mUrl );
    }

    public ContentWebViewFragment setUrl(String url) {
        mUrl = url;
        return this;
    }
}
