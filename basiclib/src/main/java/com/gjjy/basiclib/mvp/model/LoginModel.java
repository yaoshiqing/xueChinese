package com.gjjy.basiclib.mvp.model;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.SysUtil;

public class LoginModel extends MvpModel{
    public @interface TimeOut {
        int DEFAULT = 500;
        int LONG_TIME = 3000;
    }
    private final int[] mFocusViewXY = new int[ 2 ];

    private long mClickIntervalTime = -1;

    public boolean checkTouchIntervalTimeOut(int millis) {
        boolean isInterval = System.currentTimeMillis() - mClickIntervalTime <= millis;
        if( isInterval ) return true;
        mClickIntervalTime = System.currentTimeMillis();
        return false;
    }

    public void hideKeyboard(Activity activity, @NonNull MotionEvent ev) {
        SysUtil.autoHideKeyboardAndClearFocus( activity, ev, mFocusViewXY );
    }

    public Spanned getUnderlineTextStyle(String s) {
        return Html.fromHtml( getUnderlineHtml( s ) );
    }

    private String getUnderlineHtml(String s) {
        return String.format( "<font color='#0DD692'><u>%s</u></font>", s );
    }

    public Spanned getAccountTipsTextStyle(String left, String right) {
        return Html.fromHtml(String.format(
                "%s&nbsp;%s",
                left,
                getUnderlineHtml( right )
        ));
    }

    public boolean checkName(@NonNull String s) {
        return !TextUtils.isEmpty( s.trim() );
    }

    public boolean checkEmail(@NonNull String s) {
        boolean check = checkName( s ) && s.contains("@");
        if( !check ) return false;
        String[] sp = s.split("@");
        return sp.length > 1 &&
                sp[ 0 ].length() >= 6 &&
                sp[ 1 ].length() >= 3 &&
                sp[ 1 ].contains( "." );
    }

    public boolean checkPassword(@NonNull String s) { return checkName( s ) && s.length() >= 6; }

    public boolean checkPassword(@NonNull String s1, String s2) { return s1.equals( s2 ); }
}