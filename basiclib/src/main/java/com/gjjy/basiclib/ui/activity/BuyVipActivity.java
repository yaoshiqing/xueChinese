package com.gjjy.basiclib.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.adapter.BuyVipAdapter;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.BuyVipEvaluationEntity;
import com.gjjy.basiclib.entity.BuyVipOptionEntity;
import com.gjjy.basiclib.mvp.presenter.BuyVipPresenter;
import com.gjjy.basiclib.mvp.view.BuyVipView;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.googlebillinglib.annotation.PurchaseState;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.listener.OnMultiSelectChangeAdapter;
import com.ybear.ybcomponent.widget.SpaceItemDecoration;
import com.ybear.ybcomponent.widget.ViewPager;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.ArrayList;
import java.util.List;

/**
 * 开通VIP页面
 */
@Route(path = "/vip/buyVipActivity")
public class BuyVipActivity extends BaseActivity implements BuyVipView {
    @Presenter
    private BuyVipPresenter mPresenter;

    private ImageView closeImg;
    private NestedScrollView nsvLayout;
    private RecyclerView rvNormalVipOption;
    private TextView tvBuyBtn;

    private ViewGroup vgEvalLayout;
    private ViewPager vpEvalPager;

    private BuyVipAdapter mNormalVipAdapter;
    private DialogOption mLoadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_vip);

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        onCallLoadingDialog(false);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //未登录时退出页面
        if (requestCode == StartUtil.REQUEST_CODE_LOGIN && resultCode != RESULT_OK) {
            finish();
        }
        if (requestCode == StartUtil.REQUEST_CODE_BUY_VIP_OF_FREE) {
            BuriedPointEvent.get().onMePageOfEndTryPopupOfContinueTryButton(this, resultCode == RESULT_OK);
        }
    }

    private void initView() {
        closeImg = findViewById(R.id.buy_vip_close_img);
        nsvLayout = findViewById(R.id.buy_vip_nsv_layout);
        rvNormalVipOption = findViewById(R.id.buy_vip_rv_normal_vip_option);
        tvBuyBtn = findViewById(R.id.buy_vip_tv_buy_btn);

        vgEvalLayout = findViewById(R.id.buy_vip_ll_eval_layout);
        vpEvalPager = findViewById(R.id.buy_vip_vp_eval_list);
    }

    private void initData() {
        setStatusBarHeight(R.id.toolbar_height_space);
        mLoadingDialog = createLoadingDialog();
//        mLoadingDialog.setCanceledOnTouchOutside( false );
//        mLoadingDialog.setCancelable( false );
        onCallLoadingDialog(true);

        //正确价格适配器
        mNormalVipAdapter = initBuyVipList(rvNormalVipOption, false);

        /* 评价列表 */
//        rvEvalList.setLayoutManager(
//                new LinearLayoutManager( this, RecyclerView.HORIZONTAL, false )
//        );
//        rvEvalList.addItemDecoration(
//                new SpaceItemDecoration( Utils.dp2Px( this, 14 ) )
//        );
//        rvEvalList.setAdapter( mEvalAdapter = new BuyVipEvaluationAdapter( new ArrayList<>() ) );
        setResult(RESULT_CANCELED);
    }

    private BuyVipAdapter initBuyVipList(RecyclerView rv, boolean isDiscount) {
        BuyVipAdapter adapter = new BuyVipAdapter(new ArrayList<>(), isDiscount);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new SpaceItemDecoration(Utils.dp2Px(this, 10)));
        rv.setAdapter(adapter);
        return adapter;
    }

    private void initListener() {
        closeImg.setOnClickListener(v -> finish());

        tvBuyBtn.setOnClickListener(v -> {
            //检查是否已登录，未登录时自动打开登录页面
            if (!mPresenter.checkIsLogin()) {
                return;
            }
            if (TextUtils.isEmpty(mPresenter.getCurrentSkuId())) {
                mPresenter.queryBuySubList();
                return;
            }
            tvBuyBtn.setEnabled(false);
            mPresenter.play(mPresenter.getCurrentSkuId());
        });

        mNormalVipAdapter.setOnMultiSelectChangeListener(new OnMultiSelectChangeAdapter<BuyVipAdapter.BuyVipHolder>() {
            @Override
            public void onMultiSelectChange(RecyclerView.Adapter<BuyVipAdapter.BuyVipHolder> adapter,
                                            @Nullable BuyVipAdapter.BuyVipHolder holder,
                                            int position, boolean isChecked, boolean fromUser) {
                super.onMultiSelectChange(adapter, holder, position, isChecked, fromUser);
//                checkMinSelectCount();
                if (!isChecked || !fromUser) {
                    return;
                }
                BuyVipOptionEntity data = mNormalVipAdapter.getItemData(position);
                if (data != null) {
                    mPresenter.setCurrentSkuId(data.getSkuId());
                }
//                LogUtil.e( "BuyVipIntroduce -> OnMultiSelectChange -> pos:" + position );
            }
        });
    }

    @Override
    public void onCallNormalVipOptionData(List<BuyVipOptionEntity> list) {
        if (mNormalVipAdapter.getItemCount() > 0) {
            return;
        }
        mNormalVipAdapter.clearItemData();
        mNormalVipAdapter.addItemData(list);
        mNormalVipAdapter.notifyDataSetChanged();
        //特惠没有可以勾选项时默认勾选第一个正常列表的选项
        if (mNormalVipAdapter.getItemCount() > 0) {
            post(() -> mNormalVipAdapter.setMultiSelectStatus(0, true), 20);
        }
        //列表的上拉回弹
        post(() -> {
            nsvLayout.smoothScrollBy(0, 300);
            post(() -> nsvLayout.smoothScrollBy(0, -300), 800);
        }, 500);
        //付费按钮
        tvBuyBtn.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCallEvalListData(List<BuyVipEvaluationEntity> list) {
//        mEvalAdapter.clearItemData();
//        mEvalAdapter.addItemData( list );
//        mEvalAdapter.notifyDataSetChanged();

        List<View> evalViewList = new ArrayList<>();
        for (BuyVipEvaluationEntity data : list) {
            evalViewList.add(createEvalItem(data));
        }
        vpEvalPager.addViewAllOfPager(evalViewList);
        vpEvalPager.notifyAdapter();
        vgEvalLayout.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);

        nextEval();
    }

    private View createEvalItem(BuyVipEvaluationEntity data) {
        View v = View.inflate(this, R.layout.item_buy_vip_evaluation, null);

        ShapeImageView sivUserPhoto = v.findViewById(R.id.buy_vip_evaluation_siv_user_photo);
        TextView tvUserName = v.findViewById(R.id.buy_vip_evaluation_tv_user_name);
        TextView tvLearnedDays = v.findViewById(R.id.buy_vip_evaluation_tv_learned_days);
        TextView tvContent = v.findViewById(R.id.buy_vip_evaluation_tv_eval_content);

        if (sivUserPhoto != null) {
            sivUserPhoto.setImageResource(data.getUserPhotoResId());
        }
        if (tvUserName != null) {
            tvUserName.setText(data.getUserName());
        }
        if (tvLearnedDays != null) {
            tvLearnedDays.setText(data.getLearnedDaysContent());
        }
        if (tvContent != null) {
            tvContent.setText(data.getEvalContent());
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                Utils.dp2Px(this, 320),
                Utils.dp2Px(this, 187)
        );
        v.setLayoutParams(lp);
        return v;
    }

    @Override
    public void onCallBuyVipResult(@PurchaseState int state, boolean result, String errMsg) {
        setResult(result ? RESULT_OK : RESULT_CANCELED);
        if (!result && state == PurchaseState.UNSPECIFIED_STATE) {
            showToast(R.string.stringError);
        }
        LogUtil.e("GooglePlaySub -> onCallBuyVipResult -> " +
                "state:" + state + " | " +
                "result:" + result + " | " +
                "errMsg:" + errMsg
        );
        finish();
    }

    @Override
    public void onCallLoadingDialog(boolean isShow) {
        if (isShow) {
            mLoadingDialog.show();
        } else {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public int onStatusBarIconColor() {
        return SysUtil.StatusBarIconColor.WHITE;
    }

    private void nextEval() {
        int position = vpEvalPager.getCurrentItem() + 1;
        if (position >= vpEvalPager.getViewCount()) {
            position = 0;
        }
        vpEvalPager.setCurrentItem(position);

        post(this::nextEval, 2000);
    }
}
