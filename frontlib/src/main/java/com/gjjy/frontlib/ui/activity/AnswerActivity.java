 package com.gjjy.frontlib.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.presenter.AnswerPresenter;
import com.gjjy.frontlib.mvp.view.AnswerView;
import com.gjjy.frontlib.widget.AnswerBar;
import com.gjjy.frontlib.widget.AnswerComboView;
import com.gjjy.frontlib.widget.AnswerSetMenuView;
import com.gjjy.frontlib.widget.WrongQuestionSetOperationView;
import com.gjjy.speechsdk.PermManage;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.mvp.view.fragment.MvpFragment;
import com.ybear.ybcomponent.OnPageDirectionChangedListener;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.FragmentViewPager;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

import java.util.List;

/**
 * 答题页面-主容器
 */
@Route(path = "/front/answerActivity")
public class AnswerActivity extends BaseActivity implements AnswerView {
    @Presenter
    private AnswerPresenter mPresenter;

    private AnswerBar abAnswerBar;
    private AnswerComboView acvComboView;
    private FragmentViewPager fvpPager;
    private PopupWindow pwSetMenu;
    private WrongQuestionSetOperationView emoErrMapOperation;

    private DialogOption mLoadingDialog;

    private boolean isCloseDialog = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_answer );
        mPresenter.initIntent( getIntent() );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        onCallShowLoadingDialog( false );
        fvpPager.clearFragment();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if( isCloseDialog ) {
            showEndLearningDialog(
                    v -> finish(),
                    r -> mPresenter.buriedPointCloseButton( false )
            );
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition( R.anim.anim_right_out, R.anim.anim_left_in );
        mPresenter.buriedPointCloseButton( true );
    }

    private void initView() {
        abAnswerBar = findViewById( R.id.answer_ab_answer_bar );
        acvComboView = findViewById( R.id.answer_acv_combo_view );
        fvpPager = findViewById( R.id.answer_fvp_pager );
        emoErrMapOperation = findViewById( R.id.answer_emo_err_map_operation );
        pwSetMenu = Utils.createPopupWindow( new AnswerSetMenuView( getContext() ) );
    }

    private void initData() {
        setStatusBarHeightForPadding( abAnswerBar );
        mLoadingDialog = createLoadingDialog();
        abAnswerBar.setActivity( this );
        abAnswerBar.showProgressBtn();
        abAnswerBar.showCloseBtn();
        abAnswerBar.showMoreBtn();

        //初始化ViewPager
        mPresenter.initFragmentViewPager( fvpPager );
        //初始化题目
        mPresenter.initAnswer();
//        //初始化进度条
//        post( () -> abAnswerBar.setProgress( 1 ), 1200 );

        int answerType = mPresenter.getAnswerType();

        if( answerType == Constant.AnswerType.ERROR_MAP ) {
            abAnswerBar.hideProgressBtn();
        }else {
            abAnswerBar.showProgressBtn();
        }
        LogUtil.e( "answerType -> " + answerType );
        switch ( answerType ) {
            case Constant.AnswerType.NORMAL:
                abAnswerBar.setCompleteIcon( R.drawable.ic_progress_star );
                break;
            case Constant.AnswerType.SKIP_TEST:
                if( mPresenter.isAnswerStatusOfComplete() ) {
                    abAnswerBar.showHP();
                    abAnswerBar.hideProgressStar();
                    abAnswerBar.hideMoreBtnOfGone();
                    break;
                }
                abAnswerBar.setCompleteIcon( R.drawable.ic_progress_lightning );
                abAnswerBar.showProgressStar();
                abAnswerBar.showMoreBtn();
                break;
            case Constant.AnswerType.TEST:
//                abAnswerBar.setCompleteIcon( R.drawable.ic_progress_heart );
                //1.2.3变更
                abAnswerBar.setCompleteIcon( R.drawable.ic_answer_bar_hp );
                abAnswerBar.showProgressStar();
                abAnswerBar.hideMoreBtnOfGone();
                break;
            case Constant.AnswerType.FAST_REVIEW:
                abAnswerBar.hideProgressStar();
                break;
            case Constant.AnswerType.ERROR_MAP:
                emoErrMapOperation.setVisibility( View.VISIBLE );
                abAnswerBar.hideMoreBtn();
                break;

        }
        //更新生命值
        abAnswerBar.setUpdatedHP( mPresenter.getWrongAnswerLastCountOfModuleTest() );
//        //开始答题
//        mPresenter.startAnswer();

        //权限检查
        PermManage.Perm perm = PermManage.create( this );
        perm.reqRecordAudioPerm(result -> {
            if( !result ) finish();
            //
        });

        //领取过奖励，隐藏闪电/爱心
        if( mPresenter.isAnswerStatusOfComplete() ) {
            abAnswerBar.hideProgressStar();
        }
    }

    private void initListener() {
        if( LogUtil.isDebug() ) {
            Consumer<Boolean> answerBarDoubleListener = isDouble -> {
                if( !isDouble ) return;
                Bundle b = new Bundle();
                b.putBoolean( Constant.NEXT_POSITION, true );
                mPresenter.nextItemOfBundle( b );
            };
            abAnswerBar.setOnClickListener(v ->
                    com.ybear.ybutils.utils.Utils.doubleClickListener( answerBarDoubleListener )
            );
        }

        //关闭按钮点击事件监听器
        abAnswerBar.setOnCloseClickListener(v -> onBackPressed() );

        //更多按钮点击事件监听器
        abAnswerBar.setOnMoreClickListener(v -> {
            pwSetMenu.showAsDropDown( v, 0, 0 );
            setAlpha( 0.8F );
            BuriedPointEvent.get().onStudyPageOfMoreSetUp( this );
        });

        pwSetMenu.setOnDismissListener(() -> {
            setAlpha( 1F );
            abAnswerBar.setEnableMoreBtn( false );
            post( () -> abAnswerBar.setEnableMoreBtn( true ), 100 );
        });

//        //设置菜单开关点击事件监听器
//        ((AnswerSetMenuView)pwSetMenu.getContentView()).setOnItemCheckedChangeListener(
//                (buttonView, isChecked, data, position) -> {
//                    //
//                    //
//                });

        //页面切换事件监听器
        fvpPager.addOnPageDirectionChangedListener(new OnPageDirectionChangedListener() {
            @Override
            public void onPageDirection(int i, float v, int i1, int i2) { }

            @Override
            public void onPageDirection(int position, int oldPosition, int direction) {
                LogUtil.e("onPageDirection -> " +
                        "Position:" + position + " | " +
                        "OldPosition:" + oldPosition
                );
                //如果是跳页，则不更新
                if( Math.abs( position - oldPosition ) == 1 ) {
                    mPresenter.onUpdatedCurrentItem( direction == 1 );
                }
                if( mPresenter.isWrongQuestionSetType() ) mPresenter.onPageSelected( position );
            }

            @Override
            public void onPageDirectionChanged(int i) { }
        });

        //错题集删除按钮点击事件监听器
        emoErrMapOperation.setOnRemoveBtnClickListener(v -> showRemoveQuestionDialog(v1 -> {
            onCallRemoveWrongQuestionSet( mPresenter.getCurrentAnswerId(), true );
            if( fvpPager.getFragmentCount() > 0 ) {
                int index = fvpPager.getCurrentItem();
                fvpPager.removeFragment( index );
                fvpPager.notifyAdapter();
                fvpPager.setCurrentItem( index > 0 ? index - 1 : index );
            }
            emoErrMapOperation.setProgress( emoErrMapOperation.getProgress() - 1 );
            emoErrMapOperation.setMax( emoErrMapOperation.getMax() - 1 );
            if( fvpPager.getFragmentCount() == 0 ) finish();
        }));
    }

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        LogUtil.e("AnswerActivity -> onCallArguments:" + args);
//        //1.1.1
//        mPresenter.saveProgressOfBundle( args );
        //跳转到下一个页面
        mPresenter.nextItemOfBundle( args );
        //toolbar的显示状态
        mPresenter.setToolbarVisibility( args );
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        if( id == DOMConstant.REFRESH_ANSWER ) {
            isCloseDialog = true;
            post( () -> onCallFragment( false, null ) );
            mPresenter.initAnswer();
        }
    }

    @Override
    public void onCallCombo(int num) {
        acvComboView.startCombo( num );
    }

    @Override
    public void onCallPageSelected(int position, @StringRes int checkText, int visibility) {
        abAnswerBar.setProgress( position );
        emoErrMapOperation.setProgress( position );
    }

    @Override
    public void onCallError(String s) {
        showToastOfLong( s );
        finish();
    }

    @Override
    public void onCallFragment(boolean isAdd, List<MvpFragment> fs) {
        if( fs == null || fs.size() == 0 ) {
            abAnswerBar.switchExitIconOfCloseBtn();
            return;
        }
        if( !isAdd ) fvpPager.clearFragment();

        if( fs.size() == 1 ) {
            fvpPager.addFragment( fs.get( 0 ) );
        }else {
            fvpPager.addFragmentAll( fs );
        }
        //初始化最大值进度条
        abAnswerBar.setMaxProgress( mPresenter.getSectionCount() );
        emoErrMapOperation.setMax( fs.size() );
        fvpPager.notifyAdapter();
    }

    @Override
    public void onCallAddFragment(int index, MvpFragment f) {
        fvpPager.addFragment( index, f );
        fvpPager.notifyAdapter();
    }

    @Override
    public void onCallAnswerBarVisibility(int visibility) {
        if( abAnswerBar.getVisibility() == visibility ) return;
        switch ( visibility ) {
            case View.VISIBLE:
                AnimUtil.loadAnimForToBottom(
                        this, abAnswerBar, AnimUtil.SHOW_STATUS.VISIBLE
                );
                break;
            case View.INVISIBLE:
                abAnswerBar.setVisibility( visibility );
                break;
            case View.GONE:
                AnimUtil.loadAnimForToTop(
                        this, abAnswerBar, AnimUtil.SHOW_STATUS.GONE
                );
                break;
        }
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( isFinishing() || mLoadingDialog == null ) return;
        try {
            if( isShow ) {
                mLoadingDialog.show();
            }else {
                mLoadingDialog.dismiss();
            }
        } catch (Exception ignored) { }
    }

    /**
     * 答题答错
     */
    @Override
    public void onCallWrongAnswer(boolean isSkip) {
        mPresenter.increaseWrongAnswerCount( isSkip );
    }


    /**
     * 答题答对
     */
    @Override
    public void onCallCorrectAnswer(boolean isSkip) {
        mPresenter.increaseCorrectAnswerCount( isSkip );
    }

    /**
     * 跳级考试答题答错
     */
    @Override
    public void onCallWrongAnswerOfModuleTest() {
        mPresenter.increaseWrongAnswerLastCountOfModuleTest();
    }

    @Override
    public void onCallAddWrongQuestionSet(int questionId, boolean isUpload) {
        mPresenter.addWrongQuestionSet( questionId, isUpload );
    }

    @Override
    public void onCallRemoveWrongQuestionSet(int questionId, boolean isUpload) {
        mPresenter.removeWrongQuestionSet( questionId, isUpload );
    }

    @Override
    public void onCallWrongQuestionSetDaoResult(boolean result) {
        LogUtil.e("onCallWrongQuestionSetDaoResult -> " + result);
        if( result ) showToast( R.string.stringWrongQuestionSetDaoTips );
    }

    @Override
    public void onCallNotAnswer(@Constant.AnswerType int type) {
        isCloseDialog = false;
        emoErrMapOperation.setVisibility( View.GONE );
//        int resId = R.string.stringNotData;
//        switch ( type ) {
//            case Constant.AnswerType.ERROR_MAP:
//                resId = R.string.stringWrongQuestionSetNotData;
//                break;
//        }
//        onCallShowLoadingDialog( false );
//        showToast( resId );
//        finish();
    }

    public void updatedPageSelected() {
        //1.1.2
//        mPresenter.updatedPageSelected( 1 );
        mPresenter.incrementTestEncourageCount();
        abAnswerBar.setUpdatedHP( mPresenter.getWrongAnswerLastCountOfModuleTest() );
    }

//    @Override
//    public boolean onEnableFullScreen() { return true; }
}