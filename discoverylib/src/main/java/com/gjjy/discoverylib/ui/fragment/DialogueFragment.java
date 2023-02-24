package com.gjjy.discoverylib.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.gjjy.discoverylib.R;
import com.gjjy.discoverylib.adapter.DialogueContentAdapter;
import com.gjjy.discoverylib.adapter.TargetedLearningDetailsDialogueAdapter;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.DOMConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 专项学习 - 对话页面
 */
public class DialogueFragment extends BaseFragment {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swiTranslBtn;
    private RecyclerView rvList;
    private PopupWindow pwContentPopup;

    private TargetedLearningDetailsDialogueAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialogue, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.release();
        }
        super.onDestroy();
    }

    private void initView() {
        swiTranslBtn = findViewById(R.id.dialogue_swi_show_translation);
        rvList = findViewById(R.id.dialogue_rv_list);
    }

    private void initData() {
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setNestedScrollingEnabled(false);
        rvList.addItemDecoration(new SpaceItemDecoration(Utils.dp2Px(getContext(), 14)));

        rvList.setAdapter(mAdapter = new TargetedLearningDetailsDialogueAdapter(getContext(), new ArrayList<>()));
        rvList.getLocationOnScreen(mTipsRvXY);
    }


    private void initListener() {
        swiTranslBtn.setOnCheckedChangeListener(
                (buttonView, isChecked) -> mAdapter.showTranslate(isChecked)
        );

        mAdapter.setOnChildContentClickListener((adapter, v, data, position) ->
                showChildContentTips(v, data)
        );
    }

    public void setDataList(List<TargetedLearningDetailsDialogueAdapter.ItemData> list) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.clearItemData();
        mAdapter.addItemData(list);
        mAdapter.notifyDataSetChanged();
    }

    public void playAudio() {
//        if( mAdapter != null ) mAdapter.playAudio();
    }

    public void pauseAudio() {
        if (mAdapter != null) mAdapter.pauseAudio();
    }

    public void setOnAudioPlayListener(Consumer<Boolean> call) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.setOnAudioPlayListener(getContext(), call);
    }

    private int mChildContentDismissCount = 0;

    private void showChildContentTips(View view, DialogueContentAdapter.ItemData data) {
        if (getContext() == null) {
            return;
        }
        boolean isInit = false;

        if (pwContentPopup == null) {
            pwContentPopup = new PopupWindow(
                    View.inflate(getContext(), R.layout.popup_dialogue_content, null),
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            pwContentPopup.setOutsideTouchable(true);
            pwContentPopup.setOnDismissListener(() -> {
                LogUtil.e("mChildContentDismissCount -> " + mChildContentDismissCount);
                if (mChildContentDismissCount > 0) {
                    mAdapter.cancelTouch();
                }
                mChildContentDismissCount++;
            });
            isInit = true;
        } else {
//            pwContentPopup.update(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT
//            );
        }
        if (pwContentPopup.isShowing()) {
            return;
        }

        View contentView = pwContentPopup.getContentView();
        ImageView ivArrow = contentView.findViewById(R.id.popup_dialogue_content_iv_arrow);
        TextView tvPinyin = contentView.findViewById(R.id.popup_dialogue_content_tv_pinyin);
        TextView tvChinese = contentView.findViewById(R.id.popup_dialogue_content_tv_chinese);
        TextView tvTranslate = contentView.findViewById(R.id.popup_dialogue_content_tv_translate);

        if (tvPinyin != null) {
            tvPinyin.setText(data.getPinyin());
        }
        if (tvChinese != null) {
            tvChinese.setText(data.getContent());
        }
        if (tvTranslate != null) {
            tvTranslate.setText(data.getTranslate());
        }

        if (isInit) {
            pwContentPopup.showAsDropDown(view);
            pwContentPopup.dismiss();
        }
        showTips(pwContentPopup, view, ivArrow);
    }

    private final int[] mTipsRvXY = new int[2];
    private final int[] mTipsItemViewXY = new int[2];

    private void showTips(PopupWindow pw, View v, View vArrow) {
        if (getContext() == null || v == null || vArrow == null) {
            return;
        }

        if (getActivity() == null) {
            return;
        }
        //确定实际位置
        rvList.getLocationOnScreen(mTipsRvXY);
        v.getLocationOnScreen(mTipsItemViewXY);

        View contentView = pw.getContentView();
        int btmY = mTipsItemViewXY[1] + v.getHeight() + contentView.getHeight();
        int dur = 0;
        if (btmY > SysUtil.getScreenHeight(getContext())) {
            setCallResult(DOMConstant.SCROLL_VIEW_Y, contentView.getHeight());
            dur = 250;
        }
//        pw.update( contentView.getWidth(), contentView.getHeight() );
        post(() -> {
            v.getLocationOnScreen(mTipsItemViewXY);
            //显示位置
            pw.showAtLocation(
                    getActivity().getWindow().getDecorView(),
                    Gravity.TOP | Gravity.START,
                    mTipsItemViewXY[0] + (v.getWidth() / 2) - (contentView.getWidth() / 2),
                    mTipsItemViewXY[1] + v.getHeight()
            );
            //首次可能会出现获取宽度为0的的情况，故重新展示一次
            if (contentView.getWidth() == 0) {
                post(() -> {
                    pw.dismiss();
                    showTips(pw, v, vArrow);
                });
            }
        }, dur);
    }
}
