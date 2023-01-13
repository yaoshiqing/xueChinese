package com.gjjy.xuechinese.mvp.model;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.core.util.Consumer;

import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;

public class SchemeModel extends MvpModel {
    private int mId;
    private String mVideoId;
    private String mName;

    @PathType
    private Consumer<Integer> mOnIntentListener;

    public void initIntent(Intent intent) {
        Uri uri = intent.getData();
        if( uri == null ) return;
        if( !"gjjy".equals( uri.getScheme() ) ) return;
        if( !"ct.main".equals( uri.getHost() ) ) return;
        if( uri.getPath() == null ) return;

        final String listenDailyPath = "/listen_daily";
        final String popularVideosPath = "/popular_videos";
        final String targetedLearningPath = "/targeted_learning";
        final String path = uri.getPath();

        try {
            mId = ObjUtils.parseInt( uri.getQueryParameter( "id" ) );
            mVideoId = uri.getQueryParameter( "videoId" );
            mName = uri.getQueryParameter( "name" );
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            //无效的传参
            if( mId <= 0 || TextUtils.isEmpty( mVideoId ) || TextUtils.isEmpty( mName ) ) {
                mId = 0;
                mName = null;
                mVideoId = null;
            }
        }

        int pathType = 0;
        switch( path ) {
            case listenDailyPath:                   //每日聆听
                pathType = PathType.LISTEN_DAILY;
                break;
            case popularVideosPath:                 //热门视频
                pathType = PathType.POPULAR_VIDEOS;
                break;
            case targetedLearningPath:              //专项学习
                pathType = PathType.TARGETED_LEARNING;
                break;
        }
        if( mOnIntentListener != null ) mOnIntentListener.accept( pathType );
        LogUtil.e( "SchemeModel -> initIntent -> " +
                "pathType:" + pathType + " | " +
                "id:" + mId + " | " +
                "videoId:" + mVideoId + " | " +
                "name:" + mName
        );
    }

    public int getId() { return mId; }

    public String getVideoId() { return mVideoId; }

    public String getName() { return mName; }

    public void setOnIntentListener(@PathType Consumer<Integer> call) {
        mOnIntentListener = call;
    }
}
