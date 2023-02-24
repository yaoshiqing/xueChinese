package com.gjjy.discoverylib.mvp.model;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.entity.CommentsDataChildEntity;
import com.gjjy.basiclib.api.entity.CommentsDataEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailEntity;
import com.gjjy.basiclib.api.entity.DiscoveryDetailJsonRecordEntity;
import com.gjjy.basiclib.api.entity.DiscoveryListEntity;
import com.gjjy.basiclib.api.entity.DiscoveryMorePageEntity;
import com.gjjy.basiclib.mvp.model.ReqDiscoverArticleModel;
import com.gjjy.discoverylib.adapter.DiscoveryListAdapter;
import com.gjjy.discoverylib.adapter.TargetedLearningListAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.DateTimeType;

import java.util.ArrayList;
import java.util.List;

public class DiscoverArticleModel extends MvpModel {
    @Model
    private ReqDiscoverArticleModel mDisArtModel;
    private boolean isExistListenDailyListCache;
    private boolean isExistPopularVideosListCache;
    private boolean isExistTargetedLearningListCache;
    private boolean isExistCommentsOfHotCache;
    private boolean isExistCommentsOfNewCache;

    //被取消收藏的id
    private long mCancelCollectId = -1;

    public void setUid(String uid, String token) {
        mDisArtModel.setUid(uid, token);
    }

    public void setCancelCollectId(long id) {
        mCancelCollectId = id;
    }

    public long getCancelCollectId() {
        return mCancelCollectId;
    }

    public void reqListenDailyList(boolean isCache, Consumer<List<DiscoveryListAdapter.ItemData>> call) {
        if (isExistListenDailyListCache && isCache) {
            return;
        }
        mDisArtModel.reqListenDailyList(list -> {
            List<DiscoveryListAdapter.ItemData> callList = toListenDailyList(list);
            isExistListenDailyListCache = callList.size() > 0;
            if (call != null) {
                call.accept(callList);
            }
        });
    }

    public void reqPopularVideosList(boolean isCache, Consumer<DiscoveryMorePageEntity> call) {
        if (isExistPopularVideosListCache && isCache) {
            return;
        }
        mDisArtModel.reqPopularVideosList(list -> {
            DiscoveryMorePageEntity data = new DiscoveryMorePageEntity();
            data.setData(list);
            data.setCurrentPage(1);
            data.setLastPage(1);
            data.setPerPage(1);
            data.setTotal(1);
            isExistPopularVideosListCache = list.size() > 0;
            if (call != null) {
                call.accept(data);
            }
        });
    }

    public void reqTargetedLearningList(boolean isCache, Consumer<List<TargetedLearningListAdapter.ItemData>> call) {
        if (isExistTargetedLearningListCache && isCache) {
            return;
        }
        mDisArtModel.reqTargetedLearningList(list -> {
            List<TargetedLearningListAdapter.ItemData> callList = new ArrayList<>();
            for (DiscoveryListEntity data : list) {
                TargetedLearningListAdapter.ItemData item = new TargetedLearningListAdapter.ItemData();
                item.setId(data.getDiscoverArticleId());
                item.setVip(data.isVip());
                item.setNew(data.isNew());
                item.setImgUrl(data.getBigImgUrl());
                item.setTitle(data.getTitle());
                item.setContent(data.getSummary());
                item.setVideoId(data.getVideoId());
                callList.add(item);
            }
            isExistTargetedLearningListCache = callList.size() > 0;
            if (call != null) {
                call.accept(callList);
            }
        });
    }

    public void reqListenDailyMoreList(int page, Consumer<DiscoveryMorePageEntity> call) {
        mDisArtModel.reqListenDailyMoreList(page, data -> {
            if (call != null) {
                call.accept(data);
            }
        });
    }

    public void reqPopularVideosMoreList(int page, Consumer<DiscoveryMorePageEntity> call) {
        LogUtil.e("PopularVideo", "DiscoverArticleModel reqPopularVideosMoreList page =" + page);
        mDisArtModel.reqPopularVideosMoreList(page, data -> {
            if (call != null) {
                call.accept(data);
            }
        });
    }

    public void reqTargetedLearningMoreList(int page, Consumer<DiscoveryMorePageEntity> call) {
        mDisArtModel.reqTargetedLearningMoreList(page, data -> {
            if (call != null) {
                call.accept(data);
            }
        });
    }

    public void reqCollectionList(int page, Consumer<DiscoveryMorePageEntity> call) {
        mDisArtModel.reqCollectionList(page, data -> {
            if (call != null) {
                call.accept(data);
            }
        });
    }

    public void reqHaveReadList(int page, Consumer<DiscoveryMorePageEntity> call) {
        mDisArtModel.reqHaveReadList(page, data -> {
            if (call != null) {
                call.accept(data);
            }
        });
    }

    public void reqFindDetail(long id, Consumer<DiscoveryDetailEntity> call) {
        mDisArtModel.reqFindDetail(id, list -> {
            if (call != null) {
                call.accept(list);
            }
        });
    }

    public void editCollectStatus(long id, boolean isCollect) {
        setCancelCollectId(isCollect ? -1 : id);
        mDisArtModel.editCollectStatus(id, isCollect, r -> {
        });
    }

    public void saveReadRecord(long id, DiscoveryDetailJsonRecordEntity json) {
        mDisArtModel.saveReadRecord(id, json, r -> {
        });
    }

    public void reqCommentsDataOfHot(long discoverArticleId, int page, boolean isCache, Consumer<CommentsDataEntity> call) {
        if (isExistCommentsOfHotCache && isCache) {
            call.accept(null);
            return;
        }
        mDisArtModel.reqCommentsData(discoverArticleId, 2, page, data -> {
            if (call != null) {
                call.accept(data);
            }
            isExistCommentsOfHotCache = data.getTotal() > 0;
        });
    }

    public void reqCommentsDataOfNew(long discoverArticleId, int page, boolean isCache, Consumer<CommentsDataEntity> call) {
        if (isExistCommentsOfNewCache && isCache) {
            call.accept(null);
            return;
        }
        mDisArtModel.reqCommentsData(discoverArticleId, 1, page, data -> {
            if (call != null) {
                call.accept(data);
            }
            isExistCommentsOfNewCache = data.getTotal() > 0;
        });
    }

    public void reqCommentsChildData(int talkId, int page, int pageSize, Consumer<CommentsDataEntity> call) {
        mDisArtModel.reqCommentsChildData(talkId, page, pageSize, call);
    }

    public void reqAddComments(long discoverArticleId, String content, Consumer<Integer> call) {
        mDisArtModel.reqAddComments(discoverArticleId, content, call);
    }

    public void reqAddChildComments(int targetTalkId, String content, Consumer<Integer> call) {
        mDisArtModel.reqAddChildComments(targetTalkId, content, call);
    }

    public void reqTopComments(int talkId, int interactType, Consumer<CommentsDataChildEntity> call) {
        mDisArtModel.reqTopCommentsList(talkId, interactType, call);
    }

    public void reqCommentsLike(int talkId, Consumer<Boolean> call) {
        mDisArtModel.reqCommentsLike(talkId, call);
    }

    public List<DiscoveryListAdapter.ItemData> toListenDailyList(List<DiscoveryListEntity> list) {
        List<DiscoveryListAdapter.ItemData> retList = new ArrayList<>();
        for (DiscoveryListEntity data : list) {
            DiscoveryListAdapter.ItemData item = new DiscoveryListAdapter.ItemData();
            item.setId(data.getDiscoverArticleId());
            item.setTypeId(data.getDiscoverTypeId());
            item.setImgUrl(data.getImgUrl());
            item.setUnlockImgUrl(data.getBigImgUrl());
            item.setTitle(data.getTitle());
            item.setContent(data.getSummary());
            item.setExplain(data.getExplain());
            item.setNew(data.isNew());
            item.setVip(data.isVip());
            item.setToViewCount(data.getReadCount());
            item.setClassifyTag("HSK" + data.getLevel());
            item.setVideoId(data.getVideoId());
            retList.add(item);
        }
        return retList;
    }

    public List<DiscoveryListAdapter.ItemData> toPopularVideosList(List<DiscoveryListEntity> list) {
        List<DiscoveryListAdapter.ItemData> retList = new ArrayList<>();
        for (DiscoveryListEntity data : list) {
            DiscoveryListAdapter.ItemData item = new DiscoveryListAdapter.ItemData();
            item.setId(data.getDiscoverArticleId());
            item.setTypeId(data.getDiscoverTypeId());
            item.setImgUrl(data.getBigImgUrl());
            item.setUnlockImgUrl(data.getBigImgUrl());
            item.setTitle(data.getTitle());
            item.setContent(data.getSummary());
            item.setExplain(data.getExplain());
            item.setNew(data.isNew());
            item.setVip(data.isVip());
            item.setVideoId(data.getVideoId());
            item.setToViewCount(data.getReadCount());
            item.setTime(DateTime.toTimeProgressFormat(
                    data.getJsonContent().getPlaySecond() * 1000,
                    DateTimeType.MINUTE + ":" + DateTimeType.SECOND
            ));
            retList.add(item);
        }
        return retList;
    }

    public List<DiscoveryListAdapter.ItemData> toMyCourseList(List<DiscoveryListEntity> list) {
        List<DiscoveryListAdapter.ItemData> retList = new ArrayList<>();
        for (DiscoveryListEntity data : list) {
            DiscoveryListAdapter.ItemData item = new DiscoveryListAdapter.ItemData();
            item.setId(data.getDiscoverArticleId());
            item.setTypeId(data.getDiscoverTypeId());
            item.setImgUrl(data.getImgUrl());
            item.setUnlockImgUrl(data.getBigImgUrl());
            item.setTitle(data.getTitle());
            item.setContent(data.getSummary());
            item.setExplain(data.getExplain());
            item.setVip(data.isVip());
            item.setNew(data.isNew());
            item.setVideoId(data.getVideoId());
            double timeProgress = 0D;
            try {
                int totalTime = data.getJsonContent().getPlaySecond();
                int curTime = data.getJsonRecord().getPlayProgressSecond();
                timeProgress = (double) curTime / (double) totalTime * 100D;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                timeProgress = timeProgress > 100 ? 100 : timeProgress;
                item.setTimeProgress((int) timeProgress + "%");
            }
            retList.add(item);
        }
        return retList;
    }
}
