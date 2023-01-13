package com.gjjy.discoverylib.mvp.view;

import com.gjjy.discoverylib.adapter.TargetedLearningDetailsDialogueAdapter;
import com.gjjy.discoverylib.adapter.TargetedLearningDetailsGrammarAdapter;

import java.util.List;

public interface TargetedLearningDetailsView extends FindDetailsView {
    void onCallIntroduction(String content);
    void onCallDialogueDataList(List<TargetedLearningDetailsDialogueAdapter.ItemData> list);
    void onCallGrammarDataList(List<TargetedLearningDetailsGrammarAdapter.ItemData> list);
    void onCallCommentInfo(long id, int topTalkId, int topInteractType);
    void onCallShowTargetedLearningDetailsInitGuide();
}
