package com.ybear.baseframework;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Space;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.ybear.mvp.BaseVP;
import com.ybear.mvp.handler.DelegateHandler;
import com.ybear.mvp.handler.Handler;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.StackManage;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.toast.Build;
import com.ybear.ybutils.utils.toast.ToastManage;
import com.ybear.ybutils.utils.toast.ToastX;

import static android.os.Build.VERSION.SDK_INT;

final class ActivityImpl {
    private final String KEY_TOAST_STRING = "KEY_TOAST_STRING";
    private final String KEY_TOAST_DURATION = "KEY_TOAST_DURATION";
    private final String KEY_TOAST_BUILD = "KEY_TOAST_BUILD";
    private final int WHAT_TOAST = -80003;

    private final StackManage mStackManage = StackManage.get();
    private final ToastManage mToastManage = ToastManage.get();
    private final SparseArray<DOM.OnResultListener> mOnResultList = new SparseArray<>();

    @Nullable
    private IActivity mIActivity;
    private Handler mHandler;

    private DelegateHandler.OnMessageListener mOnHandleMessageListener;

    private boolean mEnableFullScreen = false;
    private boolean mEnableImmersive = false;
    private boolean isAddHandlerListener = true;
    @SysUtil.StatusBarIconColor
    private int mStatusBarIconColor = SysUtil.StatusBarIconColor.WHITE;
    private static final int[] mDefOverridePendingTransition = new int[4];

    private View.OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;

    public static ActivityImpl create() {
        return HANDLER.I;
    }

    private static final class HANDLER {
        private static final ActivityImpl I = new ActivityImpl();
    }

    private <VP extends BaseVP> void doHandlerMessage(@NonNull VP vp, Message msg) {
        Context context = vp.getContext();
        if (context == null) return;
        if (msg.what == WHAT_TOAST) {
            Bundle data = (Bundle) msg.obj;
            String s = data.getString(KEY_TOAST_STRING);
            Build b = data.getParcelable(KEY_TOAST_BUILD);
            if (data.getInt(KEY_TOAST_DURATION) == ToastX.LENGTH_SHORT) {
                getToast().showToast(context, s, b);
            } else {
                getToast().showToastForLong(context, s, b);
            }
        }
    }

    void onReChangedWindowState(@Nullable Activity activity) {
        if (activity == null || mIActivity == null) {
            return;
        }
        mHandler.post(() -> {
            Window w = getWindow(activity);
            View v = null;
            if (w != null) {
                v = w.getDecorView();
                /* 全屏模式和沉浸式只能二选一 */
                boolean isFullScreen = mIActivity.onEnableFullScreen();
                //是否启用全屏模式
                SysUtil.setFullScreen(w, isFullScreen);
                //是否启用沉浸模式
                if (!isFullScreen && mIActivity.onEnableImmersive()) {
                    SysUtil.immersiveStatusBar(w);
                }
            }

            //添加系统UI界面发生改变监听器
            if (v != null && SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                if (mOnSystemUiVisibilityChangeListener == null) {
                    mOnSystemUiVisibilityChangeListener = visibility -> {
                        onSystemUiVisibilityChange(visibility);
                        if (visibility != View.VISIBLE) {
                            onReChangedWindowState(activity);
                        }
                    };
                    v.setOnSystemUiVisibilityChangeListener(mOnSystemUiVisibilityChangeListener);
                }
            }
            //设置状态栏颜色
            setStatusBarIconColor(activity, mIActivity.onStatusBarIconColor());
        });
        LogUtil.i("ActivityImpl -> onReChangedWindowState");
    }

    @Nullable
    private Window getWindow(@NonNull Activity activity) {
        return activity.getWindow();
    }

    /**
     * 进入和退出动画：退出动画
     *
     * @param defResId 默认的动画。
     *                 进入：0：进入动画，1：退出动画
     *                 退出：2：进入动画，3：退出动画
     * @return 默认返回 defResId
     */
    @NonNull
    @Size(min = 4, max = 4)
    int[] onOverridePendingTransition(@NonNull @Size(min = 4, max = 4) int[] defResId) {
        defResId[0] = R.anim.anim_right_in;
        defResId[1] = R.anim.anim_left_out;
        defResId[2] = 0;
        defResId[3] = R.anim.anim_right_out;
        return defResId;
    }

    /**
     * 启用进入和退出动画
     *
     * @return 是否启用
     */
    boolean onEnableOverridePendingTransition() {
        return true;
    }

    private void setOverridePendingTransition(@Nullable Activity activity, boolean isEnter) {
        if (activity == null || !onEnableOverridePendingTransition()) {
            return;
        }
        int[] overPendTrans = onOverridePendingTransition(mDefOverridePendingTransition);
        activity.overridePendingTransition(
                isEnter ? overPendTrans[0] : overPendTrans[1],
                isEnter ? overPendTrans[2] : overPendTrans[3]
        );
    }

    void onCreate(@Nullable Activity activity, int hashCode, @Nullable DOM.OnResultListener l) {
        if (activity == null) {
            return;
        }
        //设置进入动画
        setOverridePendingTransition(activity, true);
        //在退出栈中添加当前Activity
        mStackManage.addExitStack(activity);

        //添加DOM监听
        if (l == null) {
            return;
        }
        mOnResultList.put(hashCode, l);
        //注册DOM监听
        DOM.getInstance().registerResult(l);
    }

    <VP extends BaseVP> void onResume(@NonNull VP vp, @Nullable IActivity iActivity) {
        Activity activity = vp.getActivity();
        if (activity == null) {
            return;
        }

        mHandler = vp.getHandler();
        if (iActivity != null) {
            mIActivity = iActivity;
        }

        if (isAddHandlerListener) {
            if (mOnHandleMessageListener == null) {
                mOnHandleMessageListener = msg -> doHandlerMessage(vp, msg);
            }
            mHandler.addOnHandleMessageListener(mOnHandleMessageListener);
        }
        isAddHandlerListener = false;

        //重新改变Window状态
        onReChangedWindowState(activity);
    }

    void onPause() {
        isAddHandlerListener = true;
        //移除handle消息监听
        mHandler.removeOnHandleMessageListener(mOnHandleMessageListener);
    }

    void onDestroy(@Nullable Activity activity, int hashCode) {
        //在退出栈中移除当前Activity
        if (activity != null) {
            mStackManage.removeExitStack(activity);
        }
        //解除DOM监听
        DOM.getInstance().unRegisterResult(mOnResultList.get(hashCode));
        //移除DOM监听
        mOnResultList.remove(hashCode);
        //移除系统UI界面发生改变监听器
        mOnSystemUiVisibilityChangeListener = null;
    }

    void finish(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }
        //设置退出动画
        setOverridePendingTransition(activity, false);
    }

    /**
     * 系统UI界面发生改变
     *
     * @param visibility 改变的意图
     */
    void onSystemUiVisibilityChange(int visibility) {
    }

    void clearTranslucentStatus(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }
        Window w = getWindow(activity);
        if (w == null) {
            return;
        }
        if (SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 沉浸式模式下view可能会被状态栏遮挡
     *
     * @param id {@link Space} 或者 {@link View}的资源id
     */
    void setStatusBarHeight(@Nullable Activity activity, @IdRes int id) {
        if (activity == null) {
            return;
        }
        View v = activity.findViewById(id);
        if (v instanceof Space) {
            setStatusBarHeightForSpace((Space) v);
        } else {
            setStatusBarHeightForPadding(v);
        }
    }

    /**
     * 沉浸式模式下，view可能会被状态栏遮挡，这里设置View的padding top下移view的位置
     *
     * @param v {@link View}
     */
    void setStatusBarHeightForPadding(View v) {
        if (v != null) {
            SysUtil.setStatusBarHeightForPadding(v);
        }
    }

    /**
     * 取消沉浸式模式下，View的padding top下移view的位置
     *
     * @param v {@link View}
     */
    void setCancelStatusBarHeightForPadding(View v) {
        SysUtil.setStatusBarHeightForPadding(v, 0);
    }

    /**
     * 沉浸式模式下，view可能会被状态栏遮挡，这里通过设置Space高度下移view的位置
     *
     * @param s {@link Space}
     */
    void setStatusBarHeightForSpace(Space s) {
        if (s != null) {
            SysUtil.setStatusBarHeightForSpace(s);
        }
    }

    /**
     * 取消沉浸式模式下，Space高度下移view的位置
     *
     * @param s {@link Space}
     */
    void setCancelStatusBarHeightForSpace(Space s) {
        SysUtil.setStatusBarHeightForSpace(s, 0);
    }

    /**
     * 设置页面透明度
     *
     * @param alpha 0.0 ~ 1.0
     */
    <A extends Activity> boolean setAlpha(@Nullable A activity, float alpha) {
        if (activity == null) {
            return false;
        }
        Window w = activity.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.alpha = alpha;
        w.setAttributes(lp);
        return true;
    }

    /**
     * 设置半透明
     *
     * @param enable 是否半透明
     */
    <A extends Activity> boolean setTransparent(@Nullable A activity, boolean enable) {
        return setAlpha(activity, enable ? 0.5F : 1F);
    }

    /**
     * 设置全透明
     *
     * @param enable 是否全透明
     */
    <A extends Activity> boolean setFullyTransparent(@Nullable A activity, boolean enable) {
        return setAlpha(activity, enable ? 0F : 1F);
    }

    /**
     * 显示/隐藏活动栏
     *
     * @param enable 是否显示
     */
    void setShowActionBar(@Nullable Activity activity, boolean enable) {
        if (activity == null) {
            return;
        }
        if (activity instanceof AppCompatActivity) {
            ActionBar bar = ((AppCompatActivity) activity).getSupportActionBar();
            if (bar == null) {
                return;
            }
            if (enable) {
                bar.show();
            } else {
                bar.hide();
            }
        } else {
            android.app.ActionBar bar = activity.getActionBar();
            if (bar == null) {
                return;
            }
            if (enable) {
                bar.show();
            } else {
                bar.hide();
            }
        }
    }

    void setCallResult(int id) {
        DOM.getInstance().setResult(id);
    }

    void setCallResult(int id, Object data) {
        DOM.getInstance().setResult(id, data);
    }

    ToastManage getToast() {
        return mToastManage;
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       活动页
     * @param statusBarColor {@link com.ybear.ybutils.utils.SysUtil.StatusBarIconColor}
     */
    void setStatusBarIconColor(@Nullable Activity activity, @SysUtil.StatusBarIconColor int statusBarColor) {
        mStatusBarIconColor = statusBarColor;
        Window w = null;
        if (activity != null) {
            w = getWindow(activity);
        }
        if (w != null) {
            SysUtil.setStatusBarIconColor(w, statusBarColor);
        }
    }

    /**
     * 状态栏颜色
     *
     * @return {@link com.ybear.ybutils.utils.SysUtil.StatusBarIconColor}
     */
    @SysUtil.StatusBarIconColor
    int onStatusBarIconColor() {
        return mStatusBarIconColor;
    }

    /**
     * 是否启用全屏
     *
     * @param activity 启用全屏的Activity
     * @param enable   是否启用
     */
    void setEnableFullScreen(Activity activity, boolean enable) {
        mEnableFullScreen = enable;
        onReChangedWindowState(activity);
    }

    /**
     * 是否启用全屏
     * 当{@link #onEnableImmersive()}为true时无效
     *
     * @return 是否启用
     */
    boolean onEnableFullScreen() {
        return mEnableFullScreen;
    }

    /**
     * 是否启用沉浸式
     *
     * @param activity 启用沉浸式的Activity
     * @param enable   是否启用
     */
    void setEnableImmersive(Activity activity, boolean enable) {
        mEnableImmersive = enable;
        onReChangedWindowState(activity);
    }

    /**
     * 是否启用沉浸式
     *
     * @return 是否启用
     */
    boolean onEnableImmersive() {
        return mEnableImmersive;
    }

    /**
     * 获取栈管理器（用于关闭页面，返回桌面，双击返回桌面等一系列操作）
     *
     * @return {@link StackManage}
     */
    StackManage getStackManage() {
        return mStackManage;
    }

    /**
     * 显示一个以Handler启动的Toast
     *
     * @param s        内容
     * @param duration 时长
     */
    void showToast(String s, int duration, @Nullable Build b) {
        Message msg = Message.obtain();
        Bundle obj = new Bundle();
        obj.putString(KEY_TOAST_STRING, s);
        obj.putInt(KEY_TOAST_DURATION, duration);
        if (b != null) {
            obj.putParcelable(KEY_TOAST_BUILD, b);
        }
        msg.what = WHAT_TOAST;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    void showToast(String s, int duration) {
        showToast(s, duration, null);
    }

    void showToast(String s, @Nullable Build b) {
        showToast(s, ToastX.LENGTH_SHORT, b);
    }

    void showToast(String s) {
        showToast(s, ToastX.LENGTH_SHORT);
    }

    void showToastOfLong(String s, @Nullable Build b) {
        showToast(s, ToastX.LENGTH_LONG, b);
    }

    void showToastOfLong(String s) {
        showToastOfLong(s, null);
    }

    void showToast(@NonNull Resources res, @StringRes int id, int duration, @Nullable Build b) {
        showToast(res.getString(id), duration, b);
    }

    void showToast(@NonNull Resources res, @StringRes int id, int duration) {
        showToast(res, id, duration, null);
    }

    void showToast(@NonNull Resources res, @StringRes int id, @Nullable Build b) {
        showToast(res, id, ToastX.LENGTH_SHORT, b);
    }

    void showToast(@NonNull Resources res, @StringRes int id) {
        showToast(res, id, null);
    }

    void showToastOfLong(@NonNull Resources res, @StringRes int id, @Nullable Build b) {
        showToast(res, id, ToastX.LENGTH_LONG, b);
    }

    void showToastOfLong(@NonNull Resources res, @StringRes int id) {
        showToastOfLong(res, id, null);
    }
}