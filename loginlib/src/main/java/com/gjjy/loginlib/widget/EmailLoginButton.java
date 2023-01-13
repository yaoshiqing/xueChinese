package com.gjjy.loginlib.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.gjjy.loginlib.R;

public class EmailLoginButton extends BaseLoginButton {


    public EmailLoginButton(Context context) {
        this(context, null);
    }

    public EmailLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmailLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIcon( R.drawable.ic_auth_emall );
        setText( R.string.textEmail );
        setTextColor( Color.BLACK );
        setBackgroundResource( R.drawable.ic_login_google_bg_btn );
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
