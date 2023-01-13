package com.gjjy.frontlib.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.basiclib.utils.Constant;

/**
 * 鼓励页
 */
@Route(path = "/front/encourageActivity")
public class EncourageActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_encourage );

        CheckButton cbCheckBtn = findViewById(R.id.answer_encourage_cb_check_btn);
        cbCheckBtn.setText( R.string.stringContinue );
        cbCheckBtn.setEnabled( true );
        cbCheckBtn.setOnClickListener(v -> finish());

        post( () -> setCallResult( Constant.SoundType.SOUND_ENCOURAGE ), 250 );
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition( R.anim.anim_right_in, R.anim.anim_left_out );
    }

    @Override
    public boolean onEnableFullScreen() { return true; }
}
