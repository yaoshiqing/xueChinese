package com.gjjy.usercenterlib.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gjjy.basiclib.api.entity.VouchersPopupChildEntity;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.dao.entity.UserDetailEntity;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.widget.PhotoView;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.StartUtil;
import com.gjjy.usercenterlib.adapter.MeOptAdapter;
import com.gjjy.usercenterlib.adapter.RankingAdapter;
import com.gjjy.usercenterlib.mvp.presenter.UserCenterPresenter;
import com.gjjy.usercenterlib.mvp.view.UserCenterView;
import com.gjjy.usercenterlib.widget.PlateView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.ybear.ybcomponent.widget.shape.ShapeLinearLayout;
import com.ybear.ybcomponent.widget.shape.ShapeRecyclerView;
import com.ybear.ybcomponent.widget.shape.ShapeTextView;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.toast.ToastManage;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心
 */
public class UserCenterFragment extends BaseFragment implements UserCenterView, OnVisibleChangedListener {
    @Presenter
    private UserCenterPresenter mPresenter;

    private PhotoView pvPhoto;
    private TextView tvPlanOfWordCount;
    private TextView tvPlanOfDaysCount;
    private TextView tvUserName;
    private TextView tvUserId;
    private LinearLayout llInviteCodeLayout;
    private TextView tvVipTime;
    private TextView tvInviteCode;
    private ImageView ivInviteCodeTips;
    //    private TextView tvLoginTips;
    private ImageView ivNotifyBtn;
    private ShapeRecyclerView srvOptList;
    //    private LinearLayout llPlateLayout;
//    private ShapeTextView stvLogInBtn;
    private ShapeTextView stvNotLoggedTips;
    private TextView tvBuyVipBtn;
    //    private ImageView ivPhotoVip;
//    private ShapeLinearLayout sllUserInfoLayout;
    private ShapeLinearLayout sllAchievement;
    private ShapeLinearLayout sllRanking;
    private ShapeLinearLayout sllFriends;
    private ShapeLinearLayout sllInvitation;

    private LinearLayout llRankingNotLoginTips;
    private FrameLayout flRankingLockTips;

    private NestedScrollView nsvLayout;
    private PlateView pvExp;
    private PlateView pvHeart;
    private PlateView pvLightning;
    private RecyclerView rvRankingList;
    private TextView tvInviteFriendsBtn;
    private EditText etInviteFriendsCode;
    private TextView tvInviteFriendsCodeBtn;
    private ShapeTextView stvRankingTips;
    private ShapeTextView stvIntegral;

    private RankingAdapter mRankingAdapter;
    private MeOptAdapter mOptAdapter;

    private DialogOption mLoadingDialog;
    private int xpNum = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();

        switchLogOutMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvBuyVipBtn != null) {
            tvBuyVipBtn.setVisibility(mPresenter.getVipStatus() == 2 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        onCallShowLoadingDialog(false);
        super.onDestroy();
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        mPresenter.onVisibleChanged(true);
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onVisibleChanged(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_BUY_VIP) {
            mPresenter.buriedPointBuyVipResult(resultCode == Activity.RESULT_OK);
        }
    }

    private void initView() {
        nsvLayout = findViewById(R.id.user_center_nsv_scroll_layout);
        pvPhoto = findViewById(R.id.user_center_pv_photo);
        ivNotifyBtn = findViewById(R.id.user_center_iv_notify_btn);
        tvPlanOfWordCount = findViewById(R.id.user_center_tv_plan_words_count);
        tvPlanOfDaysCount = findViewById(R.id.user_center_tv_plan_days_count);
        tvUserName = findViewById(R.id.user_center_tv_user_name);
        tvUserId = findViewById(R.id.user_center_tv_user_id);
        llInviteCodeLayout = findViewById(R.id.user_center_ll_invite_code_layout);
        tvVipTime = findViewById(R.id.user_center_tv_vip_time);
        tvInviteCode = findViewById(R.id.user_center_tv_invite_code);
        ivInviteCodeTips = findViewById(R.id.user_center_iv_invite_code_tips);
//        tvLoginTips = findViewById( R.id.user_center_tv_login_tips );
        srvOptList = findViewById(R.id.user_center_srv_options_list);
//        llPlateLayout = findViewById( R.id.user_center_ll_plate );
//        stvLogInBtn = findViewById( R.id.user_center_stv_log_in_btn );
        stvNotLoggedTips = findViewById(R.id.user_center_inc_stv_not_logged_tips);
        tvBuyVipBtn = findViewById(R.id.user_center_tv_buy_vip_btn);
//        ivPhotoVip = findViewById( R.id.user_center_iv_photo_vip );
//        sllUserInfoLayout = findViewById( R.id.user_center_user_info_sll_info_layout );
        sllAchievement = findViewById(R.id.user_center_inc_sll_achievement);
        sllRanking = findViewById(R.id.user_center_inc_sll_ranking);
        sllFriends = findViewById(R.id.user_center_inc_sll_friends);
        sllInvitation = findViewById(R.id.user_center_inc_sll_invitation);
        pvExp = findViewById(R.id.user_center_achievement_pv_exp);
        pvHeart = findViewById(R.id.user_center_achievement_pv_heart);
        pvLightning = findViewById(R.id.user_center_achievement_pv_lightning);
        rvRankingList = findViewById(R.id.user_center_ranking_rv_ranking_list);
        tvInviteFriendsBtn = findViewById(R.id.user_center_friends_tv_invite_friends_btn);
        etInviteFriendsCode = findViewById(R.id.user_center_invitation_et_invite_code);
        tvInviteFriendsCodeBtn = findViewById(R.id.user_center_invitation_tv_invite_friends_code_btn);

        llRankingNotLoginTips = findViewById(R.id.user_center_ranking_ll_not_login_tips);
        flRankingLockTips = findViewById(R.id.user_center_ranking_fl_ranking_lock_tips);
        stvRankingTips = findViewById(R.id.user_center_ranking_stv_ranking_tips);
        stvIntegral = findViewById(R.id.user_center_stv_integral);
    }

    private void initData() {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity == null) {
            return;
        }
        //编辑框不会被键盘遮挡
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        pvWordCount.setIconResource( R.drawable.ic_user_center_words_count );
//        pvStickDays.setIconResource( R.drawable.ic_user_center_stick_days );

//        pvWordCount.setTitle( R.string.stringWordCount );
//        pvStickDays.setTitle( R.string.stringStickDays );
        mLoadingDialog = activity.createLoadingDialog();
        pvPhoto.setEnableEditModel(false);
        tvInviteFriendsCodeBtn.setEnabled(false);

        ((TextView) sllAchievement.findViewById(R.id.user_center_model_tv_title)).setText(R.string.stringReward);
        ((TextView) sllRanking.findViewById(R.id.user_center_model_tv_title)).setText(R.string.stringRanking);
        ((TextView) sllFriends.findViewById(R.id.user_center_model_tv_title)).setText(R.string.stringFriends);

        pvExp.setIconResource(R.drawable.ic_user_center_achievement_xp_icon);
        pvHeart.setIconResource(R.drawable.ic_user_center_achievement_heart_icon);
        pvLightning.setIconResource(R.drawable.ic_user_center_achievement_lightning_icon);

        int[] icons = {
                R.drawable.ic_user_center_vouchers,
                R.drawable.ic_user_center_setting,
                R.drawable.ic_user_center_feedback
        };
        int[] titles = {
                // R.string.stringVouchers,
                R.string.stringSetUp,
                R.string.stringFeedback
        };

        List<MeOptAdapter.ItemData> list = new ArrayList<>();

        for (int i = 0; i < titles.length; i++) {
            MeOptAdapter.ItemData data = new MeOptAdapter.ItemData();
            data.setIconRes(icons[i]);
            data.setTitle(getResources().getString(titles[i]));
            list.add(data);
        }

        if (getContext() == null) {
            return;
        }
        mOptAdapter = new MeOptAdapter(list);
        srvOptList.setLayoutManager(new LinearLayoutManager(getContext()));
//        DividerItemDecoration dec = new DividerItemDecoration(
//                getContext(), DividerItemDecoration.VERTICAL
//        );
//        dec.setDrawable( getResources().getDrawable( R.drawable.shape_divide_1dp_setting ) );
//        srvOptList.addItemDecoration( dec );
        srvOptList.setAdapter(mOptAdapter);

        mRankingAdapter = new RankingAdapter(Glide.with(this), new ArrayList<>());
        rvRankingList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRankingList.addItemDecoration(new SpaceItemDecoration(Utils.dp2Px(getContext(), 10)));
        rvRankingList.setAdapter(mRankingAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        View.OnClickListener loginListener = v -> mPresenter.onTouchUserPhoto();
        pvPhoto.setOnClickListener(v -> {
            if (!pvPhoto.isShowImage()) {
                loginListener.onClick(v);
            }
        });
//        stvLogInBtn.setOnClickListener( loginListener );
        tvUserName.setOnClickListener(loginListener);
        //积分入口
        stvIntegral.setOnClickListener(v -> mPresenter.startIntegralActivity());
        //通知按钮点击事件监听器
        ivNotifyBtn.setOnClickListener(v -> mPresenter.startMsgCenterActivity());
        //列表Item点击事件监听器
        mOptAdapter.setOnItemClickListener((adapter, view, itemData, position) -> {
            switch (position) {
//                case 0:         //Vouchers
//                    mPresenter.startVouchers();
//                    break;
                case 0:         //Setting
                    StartUtil.startSettingActivity(getActivity());
                    BuriedPointEvent.get().onMePageOfSetUpButton(getContext());
                    break;
                case 1:         //Feedback
                    StartUtil.startFeedbackActivity();
                    break;
            }
        });

        //复制id
        tvUserId.setOnClickListener(v -> toClipboard(
                String.valueOf(tvInviteCode.getTag()),
                R.string.stringUserIdCopySuccess,
                R.string.stringUserIdCopyFailed
        ));

        //邀请码介绍按钮
        ivInviteCodeTips.setOnClickListener(this::showInvitationCodeTips);
        //复制邀请码
        tvInviteCode.setOnClickListener(v -> toClipboard(
                String.valueOf(tvInviteCode.getTag()),
                R.string.stringInviteCodeCopySuccess,
                R.string.stringInviteCodeCopyFailed
        ));

        //开通会员
        tvBuyVipBtn.setOnClickListener(
                v -> com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity(getActivity())
        );

        // 我的成就
        View.OnClickListener achievementClickListener = v -> {
            StartUtil.startAchievementActivity(getActivity(), xpNum);
            mPresenter.buriedPointToViewAchievement();
        };
        pvExp.setOnClickListener(achievementClickListener);
        pvLightning.setOnClickListener(achievementClickListener);
        pvHeart.setOnClickListener(achievementClickListener);
        sllAchievement.findViewById(R.id.user_center_model_tv_view_btn).setOnClickListener(achievementClickListener);
        //排行榜
        sllRanking.findViewById(R.id.user_center_model_tv_view_btn).setOnClickListener(v -> mPresenter.startRankingActivity());
        //邀请好友
        sllFriends.findViewById(R.id.user_center_model_tv_view_btn).setOnClickListener(v -> mPresenter.startFriendsActivity());
        //邀请好友
        tvInviteFriendsBtn.setOnClickListener(v -> mPresenter.startInviteFriendsActivity());
        //排行榜 - 未登录状态
        llRankingNotLoginTips.setOnClickListener(v -> mPresenter.startRankingActivity());
        //邀请码填写
        etInviteFriendsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = !TextUtils.isEmpty(s) && s.length() >= 6;
                tvInviteFriendsCodeBtn.setEnabled(enable);
                tvInviteFriendsCodeBtn.setBackgroundResource(enable ? R.drawable.ic_touch_btn_true : R.drawable.ic_touch_btn_false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //邀请码输入框被点击
        etInviteFriendsCode.setOnClickListener(
                v -> onCallEditFriendInviteResult(null, -1)
        );
        //邀请码按钮
        tvInviteFriendsCodeBtn.setOnClickListener(v -> {
            if (etInviteFriendsCode.getText().length() <= 0) {
                return;
            }
            tvInviteFriendsCodeBtn.setEnabled(false);
            mPresenter.editFriendInvite(etInviteFriendsCode.getText().toString());
        });

        nsvLayout.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (pwInvitationCodePopup != null && pwInvitationCodePopup.isShowing()) {
                        pwInvitationCodePopup.dismiss();
                    }
                });
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        switch (id) {
            case DOMConstant.NOTIFY_USER_CENTER_LIST:            //刷新
                mPresenter.refreshDetail();
                break;
            case DOMConstant.NETWORK_AVAILABLE_STATUS:  //网络状态
                if ((Boolean) data) mPresenter.refreshDetail();
                break;
        }
    }

    /**
     * 复制文本到剪贴板
     *
     * @param s            文本
     * @param successResId 成功提示文本
     * @param failedResId  失败提示文本
     */
    private void toClipboard(String s, @StringRes int successResId, @StringRes int failedResId) {
        //复制邀请码到剪贴板
        boolean result = SysUtil.copyTextToClipboard(getContext(), s);
        //剪贴结果
        ToastManage.get().showToast(getContext(), result ? successResId : failedResId);
    }

    /**
     * 切换到未登录状态
     */
    private void switchLogOutMode() {
        int dp27 = Utils.dp2Px(getContext(), 27);
        int dp3 = Utils.dp2Px(getContext(), 3);

        LinearLayout.LayoutParams lpUser = (LinearLayout.LayoutParams) tvUserName.getLayoutParams();
        lpUser.topMargin = Utils.dp2Px(getContext(), 5);
        lpUser.bottomMargin = Utils.dp2Px(getContext(), 10);
        tvUserName.setText(R.string.stringRegister);
        tvUserName.setTextSize(15);
        tvUserName.setBackgroundResource(R.drawable.shape_user_center_register);
        tvUserName.setPadding(dp27, dp3, dp27, dp3);

        pvPhoto.setEnableShowImage(false);
        pvPhoto.setBorderColor(Color.WHITE);
        pvPhoto.setIsVip(false);
        llInviteCodeLayout.setVisibility(View.GONE);
        tvVipTime.setVisibility(View.GONE);
        stvNotLoggedTips.setVisibility(View.VISIBLE);
    }

    /**
     * 切换到已登录状态
     *
     * @param vipStatus 1试用会员、2正式会员、0不是会员
     */
    private void switchLogInMode(int vipStatus) {
        boolean isVip = vipStatus != 0;

        LinearLayout.LayoutParams lpUser = (LinearLayout.LayoutParams) tvUserName.getLayoutParams();
        lpUser.topMargin = Utils.dp2Px(getContext(), 5);
        lpUser.bottomMargin = Utils.dp2Px(getContext(), 20);
        tvUserName.setText(null);
        tvUserName.setTextSize(18);
        tvUserName.setBackgroundColor(Color.TRANSPARENT);
        tvUserName.setPadding(0, 0, 0, 0);

        pvPhoto.setEnableShowImage(true);
        pvPhoto.setBorderColor(isVip ? getResources().getColor(R.color.colorBuyVipMain) : Color.WHITE);
        pvPhoto.setIsVip(isVip);
        stvNotLoggedTips.setVisibility(View.GONE);
    }

    @Override
    public void onCallShowMessageCenter(boolean enable) {
        ivNotifyBtn.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取积分总数
     *
     * @param count 积分
     */
    @Override
    public void onCallIntegralTotalCount(int count) {
        stvIntegral.setText(String.valueOf(count));
        //积分丢失风险提示
        if (!mPresenter.isLoginResult() && count > 0) {
            BaseActivity activity = (BaseActivity) getActivity();
            if (activity != null) {
                activity.showIntegralLostDialog();
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onCallUpdateUserData(UserDetailEntity data) {
        if (data == null || data.getUserId() == 0) {
            return;
        }
        boolean isLoginResult = mPresenter.isLoginResult();

        pvPhoto.setPhotoUrl(data.getAvatarUrl());
        int vipStatus = data.getVipStatus();
        if (data.getIsBindAccount()) {
            //已登录
            switchLogInMode(vipStatus);
            //名字
            tvUserName.setText(data.getNickname());
        } else {
            //未登录
            switchLogOutMode();
        }

        //学习进度
        tvPlanOfWordCount.setText(String.valueOf(data.getWordCount()));
        tvPlanOfDaysCount.setText(String.valueOf(data.getVisitCount()));

        //开通vip会员按钮
        tvBuyVipBtn.setText(
                vipStatus == 0 ? R.string.stringUpgradeOfNotVip : R.string.stringUpgradeOfFreeVip
        );
        tvBuyVipBtn.setVisibility(vipStatus == 2 ? View.GONE : View.VISIBLE);
        //用户ID
        tvUserId.setText(String.format(getString(R.string.stringUserId), data.getUserId()));
        tvUserId.setVisibility(data.getUserId() > 0 ? View.VISIBLE : View.GONE);

        //邀请码
        String inviteCode = data.getInvitationCode();
        if (isLoginResult && !TextUtils.isEmpty(inviteCode)) {
            tvInviteCode.setText(String.format(getString(R.string.stringInviteCode), inviteCode));
            tvInviteCode.setTag(inviteCode);
            llInviteCodeLayout.setVisibility(View.VISIBLE);
        } else {
            llInviteCodeLayout.setVisibility(View.GONE);
        }

        //会员开通时间/过期时间
        if (data.getIsVip()) {
            tvVipTime.setText(String.format(
                    getString(R.string.stringVipTime),
                    DateTime.parse(data.getVipTime() * 1000L),
                    DateTime.parse(data.getVipEndTime())
            ));
        }
        tvVipTime.setVisibility(data.getIsVip() ? View.VISIBLE : View.GONE);

        // 货币
        xpNum = data.getExpCount();
        pvExp.setTitle(xpNum + getResources().getString(R.string.stringRewardItemXPTitle));
        pvHeart.setTitle(String.valueOf(data.getHeart()));
        pvLightning.setTitle(String.valueOf(data.getLightning()));
        //登录状态下才能展示邀请码
        sllInvitation.setVisibility(isLoginResult ? View.VISIBLE : View.GONE);
        //填写邀请码
        String friendInvitationCode = data.getFriendInvitationCode();
        onCallEditFriendInviteResult(
                friendInvitationCode,
                TextUtils.isEmpty(friendInvitationCode) ? -1 : 1
        );

//        //之前开过会员，但是过期了
//        if( mPresenter.isVipOfBefore() ) {
//            BaseActivity activity = (BaseActivity) getActivity();
//            if( activity != null ) activity.showFreeVipEndDialog();
//        }
        //排行榜状态
        changedRankingStatus();
        LogUtil.i("onCallUpdateUserData -> " + data);
    }

    @Override
    public void onCallRankingList(List<RankingAdapter.ItemData> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        mRankingAdapter.clearItemData();
        mRankingAdapter.addItemData(list);
        mRankingAdapter.notifyDataSetChanged();
        LogUtil.e("onCallRankingList -> " + list.size());
    }

    @Override
    public void onCallEditFriendInviteResult(@Nullable String code, int result) {
        Resources res = getResources();
        boolean isSuccess = result == 1;
        boolean isInit = result == -1;
        if (!TextUtils.isEmpty(code)) {
            etInviteFriendsCode.setText(code);
        }
        etInviteFriendsCode.setEnabled(!isSuccess);
        tvInviteFriendsCodeBtn.setEnabled(!isSuccess);
        if (isSuccess) {
            int color = res.getColor(R.color.color99);
            etInviteFriendsCode.setTextColor(color);
            etInviteFriendsCode.setHint(R.string.stringDefaultInvitationCode);
            etInviteFriendsCode.setHintTextColor(color);
            tvInviteFriendsCodeBtn.setTextColor(color);
            tvInviteFriendsCodeBtn.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tvInviteFriendsCodeBtn.setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        if (isInit) {
            etInviteFriendsCode.setHint(R.string.stringInvitationEditHint);
            etInviteFriendsCode.setHintTextColor(res.getColor(R.color.color99));
            return;
        }
        etInviteFriendsCode.setText(null);
        etInviteFriendsCode.setHint(R.string.stringIncorrectInvitationCode);
        etInviteFriendsCode.setHintTextColor(res.getColor(R.color.colorDialogError));
        tvInviteFriendsCodeBtn.setTextColor(Color.WHITE);
        tvInviteFriendsCodeBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvInviteFriendsCodeBtn.setBackgroundResource(R.drawable.ic_touch_btn_false);
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if (isShow) {
            mLoadingDialog.show();
            return;
        }
        mLoadingDialog.dismiss();
    }

    private DialogOption mFreeVouchersDialog;

    @Override
    public void onCallVouchersPopup(VouchersPopupChildEntity data) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity == null || data == null) {
            return;
        }
        //已结束
        if (data.getStatus() == 2) {
            //会员已结束
            activity.showFreeVipEndDialog();
        } else {
            mFreeVouchersDialog = activity.showFreeVouchersDialog(
                    data.getRole() == 1,
                    v -> mPresenter.BuyVipTicket(data.getTicketId(), result -> {
                        if (mFreeVouchersDialog != null) {
                            mFreeVouchersDialog.dismiss();
                        }
                        post(() -> mPresenter.nextVouchersPopup(), 500);
                    }),
                    v -> {
                        mPresenter.editIsRead(data.getTicketId());
                        post(() -> mPresenter.nextVouchersPopup(), 500);
                    }
            );
        }
    }

    private void changedRankingStatus() {
        //显示未登录状态
        if (!mPresenter.isLoginResult()) {
            llRankingNotLoginTips.setVisibility(View.VISIBLE);
            flRankingLockTips.setVisibility(View.GONE);
            LogUtil.e("changedRankingStatus -> Not login");
            return;
        }
        //未达到展示经验值
        if (!mPresenter.isEnableRanking()) {
            stvRankingTips.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_user_center_ranking_lock_icon, 0, 0);
            stvRankingTips.setText(R.string.stringRankingLock);
            llRankingNotLoginTips.setVisibility(View.GONE);
            flRankingLockTips.setVisibility(View.VISIBLE);
            LogUtil.e("changedRankingStatus -> Not enable ranking");
            return;
        }
        boolean isUser = false;
        for (int i = 0; i < mRankingAdapter.getItemCount(); i++) {
            RankingAdapter.ItemData item = mRankingAdapter.getItemData(i);
            if (item != null && item.isUser()) {
                isUser = true;
                break;
            }
        }
        if (!isUser) {
            stvRankingTips.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            stvRankingTips.setText(R.string.stringRankingNotMeOfUser);
            flRankingLockTips.setVisibility(View.VISIBLE);
            llRankingNotLoginTips.setVisibility(View.GONE);
            LogUtil.e("changedRankingStatus -> Not on the ranking");
            return;
        }
        llRankingNotLoginTips.setVisibility(View.GONE);
        flRankingLockTips.setVisibility(View.GONE);
        LogUtil.e("changedRankingStatus -> In the ranking");
    }

    private final int[] mTipsInvitationCodeViewXY = new int[2];
    private PopupWindow pwInvitationCodePopup;

    private void showInvitationCodeTips(View v) {
        if (getContext() == null) {
            return;
        }

        if (pwInvitationCodePopup == null) {
            pwInvitationCodePopup = new PopupWindow(
                    View.inflate(getContext(), R.layout.popup_invitation_code_tips, null),
                    com.ybear.ybutils.utils.Utils.dp2Px(getContext(), 320),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            pwInvitationCodePopup.setOutsideTouchable(true);
        }
        if (pwInvitationCodePopup.isShowing()) {
            return;
        }
        PopupWindow pw = pwInvitationCodePopup;
        //控制箭头位置
        ImageView ivArrow = pw.getContentView().findViewById(R.id.popup_invitation_code_iv_arrow);
        //确定实际位置
        v.getLocationOnScreen(mTipsInvitationCodeViewXY);
        post(() -> ivArrow.setTranslationX(
                mTipsInvitationCodeViewXY[0] - v.getWidth() + Utils.dp2Px(getContext(), 14)
        ));
        post(() -> {
            if (getActivity() == null) {
                return;
            }
            pw.showAtLocation(
                    getActivity().getWindow().getDecorView(),
                    Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    0, v.getHeight() + mTipsInvitationCodeViewXY[1]);
        });
    }
}