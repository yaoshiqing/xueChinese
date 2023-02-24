package com.gjjy.discoverylib.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.PubRefreshLayout;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.CommentListAdapter;
import com.gjjy.discoverylib.mvp.presenter.TargetedLearningCommentsPresenter;
import com.gjjy.discoverylib.mvp.view.TargetedLearningCommentsView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.ybear.ybutils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 专项学习 - Comments页面
 */
public class TargetedLearningCommentsFragment extends BaseFragment implements TargetedLearningCommentsView {
    @Presenter
    private TargetedLearningCommentsPresenter mPresenter;

    //    private RadioGroup rgTable;
    private PubRefreshLayout prlRefreshLayout;
    private RecyclerView rvList;
    private ShapeImageView sivUserPhoto;
    private TextView tvCommentsClick;
    private ImageView ivNotData;

    private CommentListAdapter.CommentData mCommentDataOfHot;
    private CommentListAdapter.CommentData mCommentDataOfNew;
    private CommentListAdapter mAdapterOfHot;
    private CommentListAdapter mAdapterOfNew;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_targeted_learning_details_comments, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initView() {
//        rgTable = findViewById( R.id.targeted_learning_details_comments_rg_table_btn );
        ivNotData = findViewById(R.id.targeted_learning_details_comments_iv_not_data);
        prlRefreshLayout = findViewById(R.id.targeted_learning_details_comments_prl_refresh_layout);
        rvList = findViewById(R.id.targeted_learning_details_comments_rv_list);
        sivUserPhoto = findViewById(R.id.comments_siv_user_photo);
        tvCommentsClick = findViewById(R.id.comments_tv_comment_click);

        findViewById(R.id.comments_ll_edit_layout).setBackgroundResource(R.drawable.shape_comments_send_layout_bg);
        findViewById(R.id.comments_et_comment).setVisibility(View.GONE);
        findViewById(R.id.comments_iv_send_btn).setVisibility(View.GONE);
        sivUserPhoto.setVisibility(View.VISIBLE);
    }

    private void initData() {
        if (getActivity() == null) {
            return;
        }

        prlRefreshLayout.setEnableRefresh(false);
        prlRefreshLayout.setEnableLoadMore(true);

        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.getItemAnimator().setChangeDuration(0);

        mCommentDataOfHot = new CommentListAdapter.CommentData();
        mCommentDataOfHot.setItemList(new ArrayList<>());
        mAdapterOfHot = new CommentListAdapter(Glide.with(this), mCommentDataOfHot);
        mAdapterOfHot.switchTrendingType();

        mCommentDataOfNew = new CommentListAdapter.CommentData();
        mCommentDataOfNew.setItemList(new ArrayList<>());
        mAdapterOfNew = new CommentListAdapter(Glide.with(this), mCommentDataOfNew);

        rvList.setAdapter(mPresenter.isShowHotComments() ? mAdapterOfHot : mAdapterOfNew);
    }

    private void initListener() {
        prlRefreshLayout.setOnLoadMoreListener(refreshLayout -> mPresenter.nextDataList());

        RadioGroup.OnCheckedChangeListener onTableCheckChangeListener = (group, checkedId) -> {
            mPresenter.setShowHotComments(checkedId == R.id.targeted_learning_details_comments_rb_trending_btn);
            mPresenter.queryDataList(mPresenter.getDiscoverArticleId(), 1, false);
        };
        mAdapterOfHot.setOnCheckedChangeListener(onTableCheckChangeListener);
        mAdapterOfNew.setOnCheckedChangeListener(onTableCheckChangeListener);

        //评论Item中的点击事件监听器
        CommentListAdapter.OnCommentsClickBtnListener onCommentsClick =
                (v, mainData, data, type, mainPos, childPos) -> {
                    mPresenter.setSendInfo(data, mainPos, childPos);
                    switch (type) {
                        case CommentListAdapter.ClickType.REPLY_MSG:        //回复
                            showKeyboard("@" + data.getUserName());
                            break;
                        case CommentListAdapter.ClickType.LIKE:             //喜欢
                            if (data != null && !data.isLike()) {
                                mPresenter.like();
                                data.setLike(true);
                                CommentListAdapter adapter = mPresenter.isShowHotComments() ? mAdapterOfHot : mAdapterOfNew;
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case CommentListAdapter.ClickType.VIEW_COMMENTS:    //展开更多二级评论
                            int talkId = mainData == null ? data.getTalkId() : mainData.getTalkId();
                            mPresenter.nextChildDataList(talkId, mainPos);
                            break;
                    }
                };
        mAdapterOfNew.setOnCommentsClickBtnListener(onCommentsClick);
        mAdapterOfHot.setOnCommentsClickBtnListener(onCommentsClick);

        tvCommentsClick.setOnClickListener(v -> showKeyboard(null));
    }

    private void showKeyboard(String hintText) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity == null) {
            return;
        }
        if (!mPresenter.isLoginResult()) {
            StartUtil.startLoginActivity(getActivity(), PageName.COMMENTS);
            return;
        }
        setCallResult(DOMConstant.targeted_learning_DETAIL_ABL_EXPANDED);
        //展示评论区键盘
        activity.showCommentsKeyboard(hintText, s -> {
            if (TextUtils.isEmpty(s)) {
                mPresenter.setSendInfo(null, -1, -1);
            } else {
                mPresenter.sendMsg(s);
            }
        });
    }

    public void queryDataList(long discoverArticleId, int topTalkId, int topInteractType) {
        mPresenter.queryDataListAndTopData(discoverArticleId, topTalkId, topInteractType);
    }

    @Override
    public void onCallCommentData(CommentListAdapter.CommentData data, int page, boolean isHotComments, boolean isTopComment) {
        boolean isNotData = data == null || data.getItemList().size() == 0;
//        int pos = isTopComment ? 0 : -1;
        CommentListAdapter.CommentData commentData = isHotComments ? mCommentDataOfHot : mCommentDataOfNew;
        CommentListAdapter adapter = isHotComments ? mAdapterOfHot : mAdapterOfNew;
        int lastCount = commentData.getItemList().size();
        if (isHotComments) {
            adapter.switchTrendingType();
        } else {
            adapter.switchNewType();
        }
        if (!isNotData) {
            if (page == 1 && !mPresenter.isHaveTopComment()) {
                commentData.clearCommentData();
            }
            //热门和最新的Item
            if (commentData.getItemList().size() == 0) {
                CommentListAdapter.ItemData itemData = new CommentListAdapter.ItemData();
                itemData.setTalkId(Integer.MIN_VALUE);
                data.getItemList().add(0, itemData);
            }
            //数据源
            commentData.setCommentData(data);
            if (!isTopComment) {
                //查询二级评论
                mPresenter.startQueryChildDataList(data.getItemList(), 0);
            }
            if (page > 1) {
                adapter.notifyItemRangeChanged(lastCount, commentData.getItemList().size());
            } else {
                rvList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        post(() -> ivNotData.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE));

        LogUtil.e("comment -> " + isNotData + " | " + data);
//        prlRefreshLayout.setVisibility( adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE );
        //关闭分页加载动画
        prlRefreshLayout.finishLoadMore();
    }

    @Override
    public void onCallCommentChildData(int talkId, CommentListAdapter.CommentData data, boolean isHotComments, boolean isNotify) {
        CommentListAdapter adapter = isHotComments ? mAdapterOfHot : mAdapterOfNew;
        CommentListAdapter.ItemData itemData = null;
        for (CommentListAdapter.ItemData findData : adapter.getDataList()) {
            if (findData.getTalkId() != talkId) {
                continue;
            }
            itemData = findData;
            break;
        }
//        if( itemData == null || data.getItemList().size() == 0 ) return;
        if (itemData != null && data.getItemList().size() > 0) {
            itemData.setReplyMsgData(data);
        }
        if (isNotify) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCallUserPhoto(String url) {
        if (TextUtils.isEmpty(url)) {
            sivUserPhoto.setImageResource(R.drawable.ic_photo_user);
            return;
        }
        Glide.with(this).load(url).into(sivUserPhoto);
    }

    @Override
    public void onCallSendMsgResult(CommentListAdapter.ItemData data, int mainPos, int childPos, boolean result) {
        if (getActivity() == null) {
            return;
        }

        LogUtil.e("onCallSendMsgResult -> " +
                "mainPos:" + mainPos + " | " +
                "childPos:" + childPos + " | " +
                "result:" + result + " | " +
                "data:" + data
        );

        CommentListAdapter adapter = (CommentListAdapter) rvList.getAdapter();
        if (adapter != null && data != null) {
            int pos;
            if (mainPos == -1 && childPos == -1) {
                //一级评论
                adapter.addItemData(1, data);
//                adapter.notifyDataSetChanged();
                post(() -> adapter.notifyItemInserted(0));
            } else {
                //二级评论
                pos = mainPos == -1 ? childPos : mainPos;
                CommentListAdapter.ItemData itemData = adapter.getItemData(pos);
                if (itemData != null) {
                    List<CommentListAdapter.ItemData> replyMsgList = itemData.getReplyMsgList();
                    replyMsgList.add(data);
                    adapter.notifyDataSetChanged();
//                    if( replyMsgList.size() == 0 ) {
//                        adapter.notifyItemInserted( pos );
//                    }else {
//                        adapter.notifyItemChanged( pos );
//                    }
                }
            }
        }
    }

    @Override
    public void onCallLikeResult(int mainPos, int childPos, boolean result) {
        CommentListAdapter adapter = (CommentListAdapter) rvList.getAdapter();
        if (adapter == null || childPos == -1) {
            return;
        }
        int pos = mainPos == -1 ? childPos : mainPos;
        CommentListAdapter.ItemData itemData = adapter.getItemData(pos);
        if (itemData == null) {
            return;
        }
        if (mainPos != -1) {
            List<CommentListAdapter.ItemData> replyList = itemData.getReplyMsgList();
            itemData = replyList.get(childPos);
        }
        if (itemData == null) {
            return;
        }
        itemData.setLike(result);
        if (result) {
            itemData.setLikeCount(itemData.getLikeCount() + 1);
        }
        adapter.notifyItemChanged(pos);
    }
}
