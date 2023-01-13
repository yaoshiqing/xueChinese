package com.gjjy.discoverylib.mvp.view;

import androidx.annotation.Nullable;

import com.ybear.mvp.view.MvpViewable;
import com.gjjy.discoverylib.adapter.CommentListAdapter;

public interface TargetedLearningCommentsView extends MvpViewable{
    void onCallCommentData(@Nullable CommentListAdapter.CommentData data, int page, boolean isHotComments, boolean isTopComment);
    void onCallCommentChildData(int talkId, CommentListAdapter.CommentData data, boolean isHotComments, boolean isNotify);
    void onCallUserPhoto(String url);
    void onCallSendMsgResult(CommentListAdapter.ItemData data, int mainPos, int childPos, boolean result);
    void onCallLikeResult(int mainPos, int childPos, boolean result);
}
