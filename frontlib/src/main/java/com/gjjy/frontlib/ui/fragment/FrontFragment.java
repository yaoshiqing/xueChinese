package com.gjjy.frontlib.ui.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.entity.AnswerBaseEntity;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.adapter.FrontListAdapter;
import com.gjjy.frontlib.adapter.FrontMenuAdapter;
import com.gjjy.frontlib.mvp.presenter.FrontPresenter;
import com.gjjy.frontlib.mvp.view.FrontView;
import com.gjjy.frontlib.widget.FrontCurrencyView;
import com.gjjy.frontlib.widget.FrontMenuView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.ybear.ybutils.utils.FrameAnimation;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.ResUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.DateTimeType;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY;
import static com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_TEST_LOGIN;

/**
 * 首页
 */
public class FrontFragment extends BaseFragment implements FrontView, OnVisibleChangedListener {
    @Presenter
    private FrontPresenter mPresenter;

    private View vLightningBtn;
    private View vHeartBtn;
    private TextView tvMenuBtn;
    private TextView tvUpgradeBtn;
    private TextView tvReViewBtn;
    private TextView tvLightningText;
    private TextView tvHeartText;
    private ImageView ivLightningIcon;
    private ImageView ivHeartIcon;
    private RecyclerView rvList;
    private FrontMenuView hmvMenu;
    private PopupWindow pwMenuPopup;
    private PopupWindow pwLockTestPopup;
    private PopupWindow pwLockModelPopup;
    private FrontCurrencyView hcvCurrency;
    private ImageView ivFrontBg;

    private FrontListAdapter mAdapter;

    private DialogOption mLoadingDialog;
    private DialogOption mTestLoginDialog;

    private ImageView arrowUp;
    private FrameAnimation.FrameControl mLightningFrameCtrl;
    private FrameAnimation.FrameControl mHeartFrameCtrl;
    private ValueAnimator mLightningValAnim, mHeartValAnim;
    private Drawable[] mBgDrawsChanged;
    private int mOldScrollPos = -1;
    private int mChangedDrawBgIndex = -1;
    private final int[] mCurrencyViewXY = new int[ 2 ];
    private boolean isListBackTop = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_front, container, false );
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyVipUI();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        //考试登录成功
        if( requestCode == REQUEST_CODE_TEST_LOGIN ) {
            boolean result = resultCode == Activity.RESULT_OK;
            mPresenter.buriedPointLearnSignUpPopupPageOfSignUpLoginButton( result );
            if( result && mTestLoginDialog != null ) mTestLoginDialog.dismiss();
//            startAnswerActivityOfTest( mTestItemData );
            return;
        }
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        BaseActivity activity = (BaseActivity) context;

        notifyVipUI();

        activity.setNetworkShowViewGroup( (ViewGroup) getView() );
        activity.setOnNetworkErrorRefreshClickListener( v -> {
            activity.hideNetworkErrorView();
            mPresenter.setOnlyLoadingDialog( false );
            mPresenter.notifyAllCategory();
        });
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        BaseActivity activity = (BaseActivity) context;
        activity.onFragmentHide();
    }

    private void initView() {
        vLightningBtn = findViewById( R.id.front_bar_ll_lightning );
        vHeartBtn = findViewById( R.id.front_bar_ll_heart );
        tvMenuBtn = findViewById( R.id.front_bar_tv_menu_btn );
        tvUpgradeBtn = findViewById( R.id.front_tv_upgrade_btn );
        tvReViewBtn = findViewById( R.id.front_tv_review_btn );
        tvLightningText = findViewById( R.id.front_bar_tv_lightning_text );
        tvHeartText = findViewById(R.id.front_bar_tv_heart_text );
        ivHeartIcon = findViewById( R.id.front_bar_iv_heart_icon );
        ivLightningIcon = findViewById( R.id.front_bar_iv_lightning_icon );
        rvList = findViewById( R.id.front_rv_list );
        ivFrontBg = findViewById( R.id.front_iv_bg );
        hmvMenu = new FrontMenuView( getContext() );
        hcvCurrency = new FrontCurrencyView( getContext() );
        arrowUp = findViewById(R.id.arrow_img_up);
    }

    private void initData() {
        BaseActivity activity = (BaseActivity) getActivity();
        if( activity == null ) return;
        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(activity, R.anim.front_arrow_up_anim);
        arrowUp.startAnimation(animationSet);

        mLoadingDialog = activity.createLoadingDialog();

        mHeartFrameCtrl = FrameAnimation
                .create()
                .loop( 1 )
                .time( 64 )
                .load( getContext(), "ic_front_heart_", 0, 6 )
                .into( ivHeartIcon );

        mLightningFrameCtrl = FrameAnimation
                .create()
                .loop( 1 )
                .time( 64 )
                .load( getContext(), "ic_front_lightning_", 0, 6 )
                .into( ivLightningIcon );

        mAdapter = new FrontListAdapter( Glide.with( this ), new ArrayList<>() );
        mAdapter.setStateRestorationPolicy( PREVENT_WHEN_EMPTY );
        mAdapter.setEnableTouchStyle( false );
        LinearLayoutManager llm = new LinearLayoutManager( activity, RecyclerView.VERTICAL, true );
//        llm.setInitialPrefetchItemCount( 10 );
        rvList.setLayoutManager( llm );

//        DividerItemDecoration decor = new DividerItemDecoration(
//                activity, DividerItemDecoration.VERTICAL
//        );
//
//        decor.setDrawable( getResources().getDrawable( R.drawable.ic_front_list_divider ) );
//        rvList.addItemDecoration( decor );
        rvList.setAdapter( mAdapter );

        int[] bgResId = {
                R.drawable.ic_front_list_bg_1,
                R.drawable.ic_front_list_bg_1,
                R.drawable.ic_front_list_bg_2,
                R.drawable.ic_front_list_bg_3,
                R.drawable.ic_front_list_bg_4,
                R.drawable.ic_front_list_bg_5,
        };
        mBgDrawsChanged = new Drawable[ bgResId.length ];
        for( int i = 0; i < mBgDrawsChanged.length; i++ ) {
            if( getContext() == null ) continue;
            mBgDrawsChanged[ i ] = ResUtil.getDrawable( getContext(), bgResId[ i ] );
        }
    }

//    private int count = 0;
    private void initListener() {
        tvMenuBtn.setOnClickListener(v -> showMenu());

        //题型分类
        hmvMenu.setOnItemClickListener((adapter, view, data, i) -> {
            isListBackTop = true;
            mPresenter.setOnlyLoadingDialog( false );
            onCallAllCategoryTitle( data.getId() );
            mPresenter.setSelectCategoryId( data.getId(), data.getTitle(), true );
            mPresenter.buriedPointCategory( data.getId(), data.getTitle() );
            hideMenu();
        });

        //货币
        View.OnClickListener currencyListener = v -> {
            boolean isLightning = ((ViewGroup)v).getChildAt( 1 ).equals( tvLightningText );
            if( isLightning ) {
                tvLightningText.getLocationOnScreen( mCurrencyViewXY );
            }else {
                tvHeartText.getLocationOnScreen( mCurrencyViewXY );
            }
            hcvCurrency.showAsDropDown( v, mCurrencyViewXY[ 0 ] );
            setTransparent( true );
        };

        hcvCurrency.setOnDismissListener(() -> setTransparent( false ));

        vLightningBtn.setOnClickListener( currencyListener );
        vHeartBtn.setOnClickListener( currencyListener );

        tvUpgradeBtn.setOnClickListener( v ->
                com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity( getActivity() )
        );

        tvReViewBtn.setOnClickListener(v -> {
            BuriedPointEvent.get().onCourseListOfReviewButton( v.getContext() );
            post(StartUtil::startReViewActivity);
        });

        //大项中的子项点击事件监听器
        mAdapter.setOnChildItemClickListener((view, itemView, data, cData,
                                              position, itemPosition, result) -> {
            //更新当前Item的数据
            mPresenter.updateCurrentItem(
                    position,
                    itemPosition,
                    data.getId(),
                    data.getTitle(),
                    cData
            );

            if( !Config.isModelFullUnlock && !result ) {
                return;
            }

            //介绍页
            if( position == 0 && itemPosition == 0 && mPresenter.isIntroduce() ) {
                StartUtil.startIntroduceActivity();
                mPresenter.buriedPointPreface(
                        data.getId(), data.getTitle(), cData.getId(), cData.getTitle()
                );
                return;
            }
            //课程详情页
            startNormalAnswerDesc( view, itemView, cData, position, itemPosition );
        });

        //开始考试按钮点击事件监听器
        mAdapter.setOnTestBtnClickListener((adapter, view, itemView, data, position) -> {
            //更新当前Item的数据
            mPresenter.updateCurrentItem(
                    position,
                    -1,
                    data.getId(),
                    data.getTitle(),
                    null
            );
            startAnswerActivityOfTest( view, itemView, data, position );
        });

        rvList.addOnScrollListener( new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if( llm == null ) return;
                int firstPos = llm.findFirstVisibleItemPosition();
                onChangedDrawBg( firstPos, llm.getItemCount() );
                mOldScrollPos = firstPos;
            }
        } );
    }

    private void onChangedDrawBg(int firstPos, int itemCount) {
        if( getContext() == null || mOldScrollPos == firstPos ) return;
        int step = (int) Math.ceil(
                ObjUtils.parseDouble( itemCount ) / ObjUtils.parseDouble( mBgDrawsChanged.length )
        );
        if( firstPos % step == 0 ) {
            boolean isUp = firstPos > mOldScrollPos;
            mChangedDrawBgIndex = isUp ? mChangedDrawBgIndex + 1 :  mChangedDrawBgIndex - 1;
            if( mChangedDrawBgIndex >= 0 && mChangedDrawBgIndex + 1 < mBgDrawsChanged.length ) {
                TransitionDrawable tdChangedBg = new TransitionDrawable( new Drawable[] {
                        mBgDrawsChanged[ isUp ? mChangedDrawBgIndex : mChangedDrawBgIndex + 1 ],
                        mBgDrawsChanged[ isUp ? mChangedDrawBgIndex + 1 : mChangedDrawBgIndex ]
                } );
//                tdChangedBg.setCrossFadeEnabled( true );
                ivFrontBg.setImageDrawable( tdChangedBg );
                tdChangedBg.startTransition( 1000 );
            }
        }
    }

    private void notifyVipUI() {

        post( () -> {
            if( tvUpgradeBtn == null ) return;
            tvUpgradeBtn.setVisibility( mPresenter.getVipStatus() == 2 ? View.GONE : View.VISIBLE );
        }, 200);
    }

    /**
     打开课程详情页
     @param view        阶段View
     @param itemView    模块View
     @param cData       模块数据
     */
    private void startNormalAnswerDesc(View view, View itemView,
                                       FrontListAdapter.ChildItem cData,
                                       int position,
                                       int itemPosition) {
        if( position == 0 ) {
            mPresenter.startNormalAnswerDesc();
            return;
        }
        //会员
        if( mPresenter.isVip() ) {
            mPresenter.startNormalAnswerDesc();
            return;
        }
        //非会员
        //冷却时间
        long unlockTimeMillis = cData.getUnlockTimeMillis();
        long millisInFuture = unlockTimeMillis - System.currentTimeMillis();
        //倒计时弹窗
        if( millisInFuture > 0 ) {
            showUnLockModelTips( view, itemView, millisInFuture );
            //体验不限时学习按钮_小测验弹窗_首页埋点
            if( itemPosition == 0 ) mPresenter.buriedPointUnLockModelTips();
            return;
        }else if( unlockTimeMillis == -1 ) {
            return;
        }
        mPresenter.startNormalAnswerDesc();
    }



    private void startAnswerActivityOfTest(View view, View itemView,
                                           FrontListAdapter.ItemData data, int position) {
        BaseActivity activity = (BaseActivity) getActivity();
        if( activity == null ) return;
        boolean isVip = mPresenter.isVip();

        //登录提示
        if( !mPresenter.isLoginResult() ) {
            if( mTestLoginDialog == null ) {
                mTestLoginDialog = activity.showLoginOfTestDialog();
            }else {
                mTestLoginDialog.show();
            }
            return;
        }
        //默认开放第一个模块
        if( position == 1 ) {
            startAnswerActivityOfTest( data );
            return;
        }

        //会员
        if( isVip ) {
            startAnswerActivityOfTest( data );
            return;
        }

        //完成了全部子模块
        if( data.isEnableTestBtn() ) {
            startAnswerActivityOfTest( data );
            return;
        }

//        int resId = isVip ?
//                R.string.stringLockTipsContentOfVip : R.string.stringLockTipsContentOfNotVip;
        int resId = R.string.stringLockTipsContentOfNotVip;
        //非会员弹窗
        showLockTips(view, itemView, resId, false, v -> startAnswerActivityOfTest( data ) );
        //埋点。非会员用户未完成本阶段的全部模块，点击本阶段小测验时，点击弹窗中按钮
        if( !data.isTestComplete() ) {
            mPresenter.buriedPointUnLockTestTips( data.getId(), data.getTitle() );
        }
    }

    private void startAnswerActivityOfTest(FrontListAdapter.ItemData data) {
        AnswerBaseEntity abeData = new AnswerBaseEntity()
                .setLevelId( data.getId() )
                .setLevelStatus( data.getLevelStatus() )
                .setScore( data.getScore() );

        //到达60分
        if( data.getScore() >= 60 ) {
            //刷新战绩页面
            StartUtil.startAnswerTestRefreshScoreActivity( abeData );
            return;
        }
        //开始考试
        StartUtil.startAnswerActivityOfTest( abeData );
        mPresenter.buriedPointTestOut( data.getId(), data.getTitle() );
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        boolean isHaveData = false;
        if (mAdapter != null) {
            isHaveData = mAdapter.getItemCount() > 0;
        }
        switch ( id ) {
            case DOMConstant.NOTIFY_FRONT_LIST:
                boolean isNorRef = ObjUtils.parseInt( data ) == 0;
                mPresenter.setOnlyLoadingDialog( !isNorRef );
                if( isNorRef && isHaveData && rvList.canScrollVertically( 1 ) ) {
                    rvList.smoothScrollToPosition( 0 );
                    break;
                }
                if( !isNorRef && isHaveData ) break;
                mPresenter.notifyAllCategory();
                break;
            case DOMConstant.NETWORK_AVAILABLE_STATUS:  //监听网络状态
                if( (Boolean) data && !isHaveData ) {
                    mPresenter.setOnlyLoadingDialog( false );
                    mPresenter.notifyAllCategory();
                }
                break;
//            case DOMConstant.NOTIFY_HOME_SECTION_NUM:
//                mPresenter.notifySectionNum( (int) data );
//                break;
//            case DOMConstant.NOTIFY_HOME_UNIT_STATUS:
//                mPresenter.notifyUnitStatus( (int) data );
//                break;
        }
    }

    private void showMenu() {
        if( getContext() == null ) return;
        if( pwMenuPopup == null ) {
            pwMenuPopup = new PopupWindow( hmvMenu );
            pwMenuPopup.setOutsideTouchable( true );
            pwMenuPopup.setOnDismissListener(() -> setTransparent( false ));
        }
        if( pwMenuPopup.isShowing() ) return;
        hmvMenu.measure( 0, 0 );
        pwMenuPopup.setWidth( hmvMenu.getMeasuredWidth() );
        pwMenuPopup.setHeight( hmvMenu.getMeasuredHeight() );
        pwMenuPopup.showAsDropDown( tvMenuBtn );
        setTransparent( true );
    }

    private final int[] mTipsRvXY = new int[ 2 ];
    private final int[] mTipsItemViewXY = new int[ 2 ];
    private void showLockTips(View view, View itemView, int contentResId, boolean isVip,
                              View.OnClickListener l) {
        if( getContext() == null ) return;

        if( pwLockTestPopup == null ) {
            pwLockTestPopup = new PopupWindow(
                    View.inflate( getContext(), R.layout.popup_front_lock, null ),
                    Utils.dp2Px( getContext(), 320 ),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            pwLockTestPopup.setOutsideTouchable( true );
        }
        if( pwLockTestPopup.isShowing() ) return;

        View contentView = pwLockTestPopup.getContentView();
        TextView tvContent = contentView.findViewById( R.id.popup_front_lock_tv_content );
        TextView tvOrText = contentView.findViewById( R.id.popup_front_lock_tv_or_text );
        TextView tvStartBtn = contentView.findViewById( R.id.popup_front_lock_tv_start_btn );

        tvOrText.setVisibility( isVip ? View.GONE : View.VISIBLE );
        tvContent.setText( contentResId );

        tvStartBtn.setText(
                isVip ? R.string.stringLockTipsBtnOfVip : R.string.stringLockTipsBtnOfNotVip
        );

        if( l != null ) {
            tvStartBtn.setOnClickListener(v -> {
                if( isVip ) {
                    l.onClick( itemView );
                }else {
                    com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity( getActivity() );
                }
                pwLockTestPopup.dismiss();
            } );
        }

        showTips( pwLockTestPopup, view, itemView, true );
    }

    private void showUnLockModelTips(View view, View itemView, long millisInFuture) {
        if( getContext() == null ) return;
        if( pwLockModelPopup == null ) {
            pwLockModelPopup = new PopupWindow(
                    View.inflate( getContext(), R.layout.popup_front_lock_model, null ),
                    Utils.dp2Px( getContext(), 320 ),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            pwLockModelPopup.setOutsideTouchable( true );
        }
        if( pwLockModelPopup.isShowing() ) return;

        View contentView = pwLockModelPopup.getContentView();
        TextView tvCountDown = contentView.findViewById( R.id.popup_front_lock_model_tv_count_down );
        TextView tvStartBtn = contentView.findViewById( R.id.popup_front_lock_model_tv_start_btn );
        CountDownTimer cdt = null;

        if( millisInFuture <= 0 ) {
            updateUnlockModelTime( tvCountDown, 0 );
        }else {
            //倒计时
            cdt = new CountDownTimer( millisInFuture, 1000 ) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateUnlockModelTime( tvCountDown, millisUntilFinished );
                }
                @Override
                public void onFinish() { if( pwLockModelPopup != null ) pwLockModelPopup.dismiss(); }
            };
            pwLockModelPopup.setOnDismissListener( cdt::cancel );
        }

        tvStartBtn.setOnClickListener( v -> {
            //vip直接进入，否则进入会员购买页面
            if( mPresenter.isVip() ) {
                mPresenter.startNormalAnswerDesc();
            }else {
                com.gjjy.basiclib.utils.StartUtil.startBuyVipActivity( getActivity() );
            }
            pwLockModelPopup.dismiss();
        } );

        showTips( pwLockModelPopup, view, itemView, true );
        if( cdt != null ) cdt.start();
    }

    /**
     更新上锁Item的倒计时
     @param tv                      更新的View
     @param millisUntilFinished     倒计时（ms）
     */
    private void updateUnlockModelTime(TextView tv, long millisUntilFinished) {
        String pattern = DateTimeType.HOUR + ":" + DateTimeType.MINUTE + ":" + DateTimeType.SECOND;
        String time = String.format(
                getString( R.string.stringModelKeepLeaning ),
                DateTime.toTimeProgressFormat( millisUntilFinished, pattern )
        );
        tv.setText( time );
    }

    private void showTips(PopupWindow pw, View v, View itemView, boolean isCtrlArrow) {
        if( getContext() == null ) return;
        if( itemView != null ) itemView.getLocationOnScreen( mTipsItemViewXY );
        if( isCtrlArrow ) {
            //控制箭头位置
            ImageView ivArrow = pw.getContentView().findViewById( R.id.popup_front_lock_iv_arrow );
            //确定实际位置
            post( () -> ivArrow.setTranslationX( mTipsItemViewXY[ 0 ] + Utils.dp2Px( getContext(), 15 ) ) );
        }

        int screenHeight = SysUtil.getScreenHeight( getContext() );
        //超出显示范围时先上移一点
        boolean isScroll = mTipsItemViewXY[ 1 ] >= (float)screenHeight / 1.8F;

//        LogUtil.e("showTips -> " +
//                v.getY() + " | " +
//                v.getHeight() + " | " +
//                SysUtil.getScreenHeight( getContext() ) + " | " +
//                Arrays.toString( mTipsItemViewXY ) + " | " +
//                pw.getHeight() + " | " +
//                isScroll
//        );
        if( isScroll ) rvList.smoothScrollBy( 0, Utils.dp2Px( getContext(), 300 ) );

        post(() -> {
            if( getActivity() == null ) return;
            //确定实际位置
            rvList.getLocationOnScreen( mTipsRvXY );
            int itemHeight = 0;
            if( itemView != null ) {
                itemHeight = itemView.getHeight();
                itemView.getLocationOnScreen( mTipsItemViewXY );
            }
            //显示位置
            pw.showAtLocation(
                    getActivity().getWindow().getDecorView(),
                    Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    0, mTipsRvXY[ 1 ] + mTipsItemViewXY[ 1 ] + itemHeight
            );
        }, isScroll? 200 : 0 );
    }

    private void hideMenu() { if( pwMenuPopup != null ) pwMenuPopup.dismiss(); }

    @Override
    public void onCallRewardMoney(int lightning, int oldLightning, boolean isLightningChanged,
                                  int heart, int oldHeart, boolean isHeartChanged) {
        if( mLightningValAnim == null ) {
            mLightningValAnim = ValueAnimator.ofInt( oldLightning, lightning );
            mLightningValAnim.setDuration( 1000 );
            mLightningValAnim.addUpdateListener(animation -> {
                int val = (Integer) animation.getAnimatedValue();
                tvLightningText.setText( String.valueOf( val ) );
                if( val == lightning && isLightningChanged ) mLightningFrameCtrl.play( getContext() );
            });
        }else {
            mLightningValAnim.setIntValues( oldLightning, lightning );
        }

        if( mHeartValAnim == null ) {
            mHeartValAnim = ValueAnimator.ofInt( oldHeart, heart );
            mHeartValAnim.setDuration( 1000 );
            mHeartValAnim.addUpdateListener(animation -> {
                int val = (Integer) animation.getAnimatedValue();
                tvHeartText.setText( String.valueOf( val ) );
                Context context = getContext();
                if( context != null && val == heart && isHeartChanged ) {
                    mHeartFrameCtrl.play( context );
                }
            });
        }else {
            mHeartValAnim.setIntValues( oldHeart, heart );
        }

        post(() -> {
            mLightningValAnim.start();
            mHeartValAnim.start();
        }, 100);
    }

    /**
     * 分类列表的数据
     * @param list      分类列表
     */
    @Override
    public void onCallAllCategory(@NonNull List<FrontMenuAdapter.ItemData> list,
                                  @Nullable FrontMenuAdapter.ItemData currentItem) {
        boolean isExistData = list.size() > 0;
        LogUtil.e("onCallAllCategory -> isExistData:" + isExistData);
        BaseActivity activity = (BaseActivity) getActivity();
        if( activity == null ) return;
        if( !isExistData ) {
//            activity.showNetworkErrorView();
            mPresenter.setSelectCategoryId( -1, "", false );
            return;
        }
//        else {
////            activity.hideNetworkErrorView();
//        }

        hmvMenu.setData( list );
        if( currentItem != null ) {
            tvMenuBtn.setText( currentItem.getTitle() );
        }
        tvMenuBtn.setVisibility( View.VISIBLE );
    }

    /**
     * 分类内容列表数据
     * @param list      内容列表
     */
    @Override
    public void onCallCategoryContent(int id, @NonNull List<FrontListAdapter.ItemData> list) {
        if( list.size() == 0 ) return;
        //介绍页
        if( mPresenter.isIntroduce() ) {
            FrontListAdapter.ItemData data = new FrontListAdapter.ItemData();
            List<FrontListAdapter.ChildItem> cItem = new ArrayList<>();
            cItem.add( mPresenter.getIntroduceItemData() );
            data.setData( cItem );
            data.setIntroduce( true );
            list.add( 0, data );
//            FrontListAdapter.ItemData data = list.get( 0 );
//            List<FrontListAdapter.ChildItem> cItem = data.getData();
//            if( cItem != null ) cItem.add( 0, mPresenter.getIntroduceItemData() );
            LogUtil.i("onCallCategoryContent -> Add introduce pager.");
        }

        mAdapter.clearItemData();
        mAdapter.addItemData( list );
        mAdapter.notifyDataSetChanged();
//        post( () -> rvList.requestLayout() );

        if( rvList.getVisibility() != View.VISIBLE ) rvList.setVisibility( View.VISIBLE );

//        mPresenter.setOnlyLoadingDialog( true );
        //隐藏网络错误
        if( list.size() > 0 && getActivity() != null ) {
            ((BaseActivity) getActivity()).hideNetworkErrorView();
        }
        if( isListBackTop ) {
            isListBackTop = false;
            rvList.scrollToPosition( 0 );
        }
//        rvList.scrollToPosition( 0 );

        //初始化首页新手引导
        setCallResult( DOMConstant.INIT_INIT_GUIDE );
        LogUtil.e( "onCallCategoryContent -> id:" + id );
    }

    @Override
    public void onCallAllCategoryTitle(int id) {
        FrontMenuAdapter.ItemData data = hmvMenu.getData( id );
        if( data == null ) return;
        tvMenuBtn.setText( data.getTitle() );
        LogUtil.i( "onCallAllCategoryTitle -> id:" + id + " | title:" + data.getTitle() );
    }

    @Override
    public void onCallLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( mPresenter.isOnlyLoadingDialog() ) {
            if( !mLoadingDialog.isShowing() ) return;
            try {
                mLoadingDialog.dismiss();
            }catch(Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            if( isShow ) {
                if( !mLoadingDialog.isShowing() ) mLoadingDialog.show();
            }else {
                if( mLoadingDialog.isShowing() ) mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}