package com.gjjy.frontlib.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.ybear.ybutils.utils.FrameAnimation;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.mvp.view.AnswerTestNodeView;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.utils.Constant;

/**
 * 小测验鼓励页
 */
@Route(path = "/front/answerTestNodeActivity")
public class AnswerTestEncourageActivity extends BaseActivity
        implements AnswerTestNodeView {
    private ImageView ivIcon;
    private CheckButton cbCheckBtn;

    private FrameAnimation.FrameControl mFrameAnimCtrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.fragment_answer_test_node );

        initView();
        initData();
        initListener();
    }

    @Override
    public void onDestroy() {
        mFrameAnimCtrl.stopNow();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition( R.anim.anim_right_in, R.anim.anim_left_out );
    }

    private void initView() {
        ivIcon = findViewById( R.id.answer_test_node_iv_icon );
        cbCheckBtn = findViewById( R.id.answer_test_node_cb_check_btn );
    }

    private void initData() {
        cbCheckBtn.setText( R.string.stringContinue );
        cbCheckBtn.setEnabled( true );


        mFrameAnimCtrl = FrameAnimation
                .create()
                .time( 100 )
                .load( getContext(), "ic_answer_test_icon_", 0, 1 )
                .into( ivIcon );

        mFrameAnimCtrl.play( this );

        post( () -> setCallResult( Constant.SoundType.SOUND_ANSWER_NODE_ENCOURAGE ), 250 );

        setOnNetworkErrorRefreshClickListener( v -> {} );
    }

    private void initListener() {
        cbCheckBtn.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onEnableFullScreen() { return true; }
}
