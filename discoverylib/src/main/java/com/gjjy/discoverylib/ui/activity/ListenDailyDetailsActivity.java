package com.gjjy.discoverylib.ui.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.ShareToolbar;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.mvp.presenter.DiscoveryDetailsPresenter;
import com.gjjy.discoverylib.mvp.view.FindDetailsView;
import com.gjjy.discoverylib.mvp.view.ListenDailyDetailsView;
import com.gjjy.discoverylib.widget.ListenDailyPlayerView;
import com.gjjy.discoverylib.widget.VerticalScrollSubtitlesView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.List;

@Route(path = "/discovery/listenDailyDetailsActivity")
public class ListenDailyDetailsActivity extends BaseActivity implements ListenDailyDetailsView, FindDetailsView {
    @Presenter
    private DiscoveryDetailsPresenter mPresenter;

    private ShareToolbar stbToolbar;
    private VerticalScrollSubtitlesView vssSubtitlesView;
    private ListenDailyPlayerView evpvPlayerView;

    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_daily_details);
        mPresenter.initIntent(getIntent());
        //防录屏截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        mPresenter.saveCurrentProgress(evpvPlayerView.getCurrentProgress());
        evpvPlayerView.release();
        mPresenter.buriedPointDurationOfListenDaily(this);
        onCallShowLoadingDialog(false);
        super.onDestroy();
    }

    private void initView() {
        stbToolbar = findViewById(R.id.listen_daily_detail_stb_toolbar);
        vssSubtitlesView = findViewById(R.id.listen_daily_detail_vss_subtitles_view);
        evpvPlayerView = findViewById(R.id.listen_daily_detail_evpv_player_view);
    }

    private void initData() {
        setStatusBarHeightForSpace(findViewById(R.id.toolbar_height_space));
        stbToolbar.setTitle(R.string.stringListenDailyMainTitle);
        mLoadingDialog = createLoadingDialog();
        mPresenter.requestData();
    }

    private void initListener() {
        stbToolbar.setOnClickBackBtnListener(view -> onBackPressed());

        stbToolbar.setOnCollectBtnClickListener(isCollect -> {
            boolean result = mPresenter.editCollectStatus(isCollect);
            if (result) {
                mPresenter.buriedPointCollectionOfListenDaily(this, isCollect);
            } else {
                evpvPlayerView.playStatus(false);
            }
            return result;
        });

        stbToolbar.setOnShareBtnClickListener(v -> {
            mPresenter.share();
            evpvPlayerView.playStatus(false);
        });

        evpvPlayerView.setOnCurrentProgressListener(progress -> {
            if (progress >= evpvPlayerView.getTotalProgress()) {
                evpvPlayerView.playStatus(false);
                return;
            }
            vssSubtitlesView.setProgress(progress, false);
        });

        vssSubtitlesView.setOnPositionChangedListener((pos, dur, fromUser) -> {
            if (!fromUser) return;
            evpvPlayerView.seekTo(dur);
            if (!evpvPlayerView.isPlaying()) evpvPlayerView.playStatus(true);
        });
    }

    @Override
    public void onCallTitle(String title) {
//        stbToolbar.setTitle( title );
    }

    @Override
    public void onCallSubtitlesData(String imgUrl, List<VerticalScrollSubtitlesView.VssAdapter.ItemData> list) {
        VerticalScrollSubtitlesView.VssAdapter.ItemData topImgItem = new VerticalScrollSubtitlesView.VssAdapter.ItemData();
        topImgItem.setTopImgUrl(imgUrl);
        list.add(0, topImgItem);
        //字幕
        vssSubtitlesView.setData(list);
    }

    @Override
    public void onCallVideoId(String videoId, int progress, int totalSecond) {
        vssSubtitlesView.setProgress(progress, false);

        evpvPlayerView.setTotalProgress(totalSecond);
        evpvPlayerView.setCurrentProgress(progress);
        evpvPlayerView.setDataUrl(videoId);

        evpvPlayerView.playStatus(true);
    }

    @Override
    public void onCallShowShareButton(boolean isShow) {
        stbToolbar.showShareBtn(isShow);
    }

    @Override
    public void onCallCollectStatus(boolean isCollect, boolean isShowToast) {
        stbToolbar.setCollectStatus(isCollect, isShowToast);
    }

    @Override
    public void onCallShareResult(boolean result) {
        stbToolbar.showShareResult(result);
        mPresenter.buriedPointShareOfListenDaily(this, result);
        evpvPlayerView.playStatus(true);
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if (mLoadingDialog == null) return;
        if (isShow) {
            mLoadingDialog.show();
            return;
        }
        mLoadingDialog.dismiss();
    }
}