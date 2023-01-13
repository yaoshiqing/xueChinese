package com.gjjy.discoverylib.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.discoverylib.R;

public class TargetedLearningDetailsDialogueHolder extends BaseViewHolder {
    private final TextView tvTitle;
    private final RecyclerView rvContent;
    private final TextView tvTranslate;
    private final ImageView ivAudioPlayBtn;

    public TargetedLearningDetailsDialogueHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        tvTitle = findViewById( R.id.item_targeted_learning_details_dialogue_tv_title );
        rvContent = findViewById( R.id.item_targeted_learning_details_dialogue_rv_content );
        tvTranslate = findViewById( R.id.item_targeted_learning_details_dialogue_tv_transl );
        ivAudioPlayBtn = findViewById( R.id.item_targeted_learning_details_dialogue_iv_audio_play_btn );
    }

    public TextView getTitle() {
        return tvTitle;
    }

    public RecyclerView getContent() {
        return rvContent;
    }

    public TextView getTranslate() {
        return tvTranslate;
    }

    public ImageView getAudioPlayBtn() {
        return ivAudioPlayBtn;
    }
}
