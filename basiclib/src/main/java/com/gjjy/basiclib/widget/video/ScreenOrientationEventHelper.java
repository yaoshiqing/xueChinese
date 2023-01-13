package com.gjjy.basiclib.widget.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.view.OrientationEventListener;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class ScreenOrientationEventHelper extends OrientationEventListener {
    private final Context mContext;
    private boolean enableFollowSystemRotation;
    private int oldOrientationAngle = -1, orientationAngle = -1, orientation = -1;
    private ScreenOrientationChangeListener mOrientationChanged;

    public ScreenOrientationEventHelper(Context context) {
        super(context, 0);
        mContext = context;
        //默认不跟随系统旋转
        enableFollowSystemRotation = false;
        disable();
    }

    @Override
    public void enable() {
        super.enable();
        //手机静置时不会有任何回调（因为没有发生旋转），此时回调一次，防止出现异常
        if( mOrientationChanged != null && orientation == -1 && orientationAngle == -1 ) {
            mOrientationChanged.onOrientationChange(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, 90, true
            );
        }
    }

    /**
     * 系统旋转是否被启用
     * @return  结果
     */
    public boolean isSystemRotation() {
        return Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0
        ) == 1;
    }

    /**
     * 旋转是否被启用
     * @return  结果
     */
    public boolean isRotation() {
        return !enableFollowSystemRotation || isSystemRotation();
    }

    /**
     * 获取屏幕方向
     * @return  {@link ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE}
     *          {@link ActivityInfo#SCREEN_ORIENTATION_REVERSE_LANDSCAPE}
     *          {@link ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}
     *          {@link ActivityInfo#SCREEN_ORIENTATION_REVERSE_PORTRAIT}
     */
    public int getOrientation() { return orientation; }

    /**
     * 获取旋转角度
     * @return  0、90、180、270
     */
    public int getOrientationAngle() { return orientationAngle; }

    public void setFollowSystemRotation(boolean enable) { enableFollowSystemRotation = enable; }

    /**
     * 设置屏幕方向改变事件监听器
     * @param l     监听器
     */
    public void setScreenOrientationChangeListener(ScreenOrientationChangeListener l) {
        mOrientationChanged = l;
    }

    /**
     * 屏幕方向发生改变时
     * @param orientation   0 ~ 360
     */
    @Override
    public void onOrientationChanged(int orientation) {
        //315~45  45~135  135~225   225~315
        if( !isRotation() || mOrientationChanged == null || orientation == -1 ) return;
        boolean isPortrait = false;

        if( orientation >= 0 && orientation < 22.5 || orientation > 337.5 && orientation < 360 ) {
            orientationAngle = 90;
            this.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            isPortrait = true;
        }
        if( orientation > 67.5 && orientation < 112.5 ) {
            orientationAngle = 0;
            this.orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
        if( orientation > 157.5 && orientation < 202.5 ) {
            orientationAngle = 270;
            this.orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            isPortrait = true;
        }
        if( orientation > 247.5 && orientation < 292.5 ) {
            orientationAngle = 180;
            this.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        if( oldOrientationAngle == orientationAngle ) return;
        mOrientationChanged.onOrientationChange( this.orientation, orientationAngle, isPortrait );
        oldOrientationAngle = orientationAngle;
    }

    public boolean isLandscapeScreen() {
        return mContext.getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
    }
}
