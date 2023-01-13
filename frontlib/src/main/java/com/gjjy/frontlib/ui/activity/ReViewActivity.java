package com.gjjy.frontlib.ui.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.widget.RedDotLayout;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.StartUtil;
import com.gjjy.frontlib.mvp.presenter.ReViewPresenter;
import com.gjjy.frontlib.mvp.view.ReViewView;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 复习页面
 */
@Route(path = "/front/reViewActivity")
public class ReViewActivity extends BaseActivity implements ReViewView {
    @Presenter
    private ReViewPresenter mPresenter;

    private ViewGroup vgOpts;
    private Toolbar tbToolbar;
    private DialogOption mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_review );
        initView();
        initData();
        initListener();
    }

    private void initView() {
        vgOpts = findViewById( R.id.review_rl_opts );
        tbToolbar = findViewById( R.id.review_tb_toolbar );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();
        int[] imgRes = {
                R.drawable.ic_fast_review, R.drawable.ic_wrong_question_set, R.drawable.ic_words_list
        };
        int[] txtRes = {
                R.string.stringFastReView, R.string.stringWrongQuestionSet, R.string.stringWordsList
        };
        for (int i = 0; i < vgOpts.getChildCount(); i++) {
            int finalI = i;
            RedDotLayout rdLayout = (RedDotLayout) vgOpts.getChildAt( i );
            TextView tvItem = (TextView) rdLayout.getChildAt( 0 );
//            if( i == 0 ) rdLayout.setText( "99+" );
            tvItem.setCompoundDrawablesWithIntrinsicBounds( 0, imgRes[ i ], 0, 0 );
            tvItem.setText( txtRes[ i ] );
            rdLayout.setOnClickListener(v -> doOptListener( finalI ));

        }
//        rdvReViewRedDot.setText("99");
    }

    private void doOptListener(int position) {
        switch ( position ) {
            case 0:         //快速复习
                StartUtil.startFastReViewActivity();
                BuriedPointEvent.get().onReviewModuleOfQuickReview( this );
                break;
            case 1:         //错题集
                StartUtil.startAnswerOfWrongQuestionSetActivity();
                BuriedPointEvent.get().onReviewModuleOfMistakes( this );
                break;
            case 2:         //词语表
                StartUtil.startWordsListActivity();
                break;
        }
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());
    }

    @Override
    public int onStatusBarIconColor() { return SysUtil.StatusBarIconColor.BLACK; }

    @Override
    public void onCallReviewNewCount(int count) {
        RedDotLayout rdl = ((RedDotLayout)vgOpts.getChildAt( 0 ));
        rdl.setText( String.valueOf( count ) );

        rdl.setEnableRedDotView( count > 0 );
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            mLoadingDialog.show();
            return;
        }
        mLoadingDialog.dismiss();
    }
}
