package com.gjjy.basiclib;

import android.content.res.AssetFileDescriptor;
import android.text.TextUtils;
import android.view.Gravity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gjjy.basiclib.dao.SQLAction;
import com.gjjy.basiclib.dao.entity.SkipAnswerTypeEntity;
import com.gjjy.basiclib.handler.CrashHandler;
import com.gjjy.basiclib.statistical.StatisticalManage;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.pushlib.Push;
import com.gjjy.speechsdk.SpeechInit;
import com.ybear.mvp.view.MvpApplication;
import com.ybear.ybcomponent.widget.shape.ShapeConfig;
import com.ybear.ybmediax.media.MediaXC;
import com.ybear.ybnetworkutil.annotations.NetworkType;
import com.ybear.ybnetworkutil.network.NetworkChangeManage;
import com.ybear.ybnetworkutil.network.NetworkConfig;
import com.ybear.ybutils.utils.AESUtil;
import com.ybear.ybutils.utils.AppUtil;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.notification.NotificationX;
import com.ybear.ybutils.utils.toast.Build;
import com.ybear.ybutils.utils.toast.ToastManage;

import java.io.IOException;

/**
 * Application
 */
public class XueChineseApplication extends MvpApplication {
    private MediaXC mSoundMediaXC;
    public static XueChineseApplication application;
    public int mSoundMediaXCTag;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Config.mVersion = AppUtil.getVerName(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(application);

        //启用日志
//        LogUtil.setDebugEnable( this, true );
        LogUtil.setDebugEnableOfAuto(this);

        ShapeConfig.get().enableHardware();

        NotificationX.get().init(this);

        /* 加密设置 */
        AESUtil.setMode(AESUtil.Mode.CBC);
        AESUtil.setPadding(AESUtil.Padding.NO_PADDING);

        //监听网络是否发生改变
        onNetworkChange();

//        //重连机制
//        HttpReboot.get()
//                .init( this )
//                .setTriggers(
//                        ReCountTrigger.NETWORK_AVAILABLE,
//                        ReCountTrigger.NETWORK_SWITCH
//                );

        //统计
        onBuriedPoint();

        //初始化推送
        onPush();

        //SQL
        SQLAction.get().init(this);

        //ARouter
        onARouter();

        //初始化讯飞语音SDK
        onSpeech();

        //初始化 Toast
        onToast();

//        //初始化Oss信息
//        onOss();

        //初始化音效
        onSound();

        if (LogUtil.isDebug()) ToastManage.get().showToastForLong(this, "Mode:DEBUG");
    }

    private void onNetworkChange() {
        NetworkConfig.get().setSmallIcon(R.drawable.ic_push_notification_icon);
        NetworkConfig.get().setForegroundTitle(
                String.format(getResources().getString(R.string.stringNetworkServiceTitle),
                        AppUtil.getAppName(this))
        );
        NetworkChangeManage ncm = NetworkChangeManage.get();
        ncm.init(this);
        ncm.registerNetworkChange((isAvailable, type) -> {
            //
            LogUtil.e("onNetworkChange -> isAvailable:" + isAvailable + " | type:" + type);
            if (!NetworkType.NONE.equals(type)) {
                DOM.getInstance().setResult(DOMConstant.NETWORK_AVAILABLE_STATUS, isAvailable);
            }
        });
        //注册在MainActivity下
//        ncm.registerService();
    }

    private void onBuriedPoint() {
        StatisticalManage.get().init(this, LogUtil.isDebug());
    }

    private void onARouter() {
        if (LogUtil.isDebug()) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    private void onSpeech() {
        SpeechInit.get()
                .init(this)
                .setEnableLog(true)
                .setAppIdOfMeta("SPEECH_SDK_APP_ID")
//                .setProxy(  )
                .build();
    }

    private void onPush() {
        Push.get().init(LogUtil.isDebug());
    }

    private void onToast() {
        ToastManage.get().setBuild(new Build()
                .setGravity(Gravity.CENTER)
                .setTextSize(16)
        );
    }

    private String mAudioFileName;

    public void onSound() {
        MediaXC.get().init(this);
        mSoundMediaXC = MediaXC.newGet();
        mSoundMediaXC.init(this);
        mSoundMediaXCTag = mSoundMediaXC.createTag();

        DOM.getInstance().registerResult((id, o) -> {
            switch (id) {
                case Constant.SoundType.SOUND_CORRECT:                  //正确答案弹窗弹出时
                    mAudioFileName = "as_sound_answer_correct";
                    break;
                case Constant.SoundType.SOUND_ERROR:                    //错误答案弹窗弹出时
                    mAudioFileName = "as_sound_answer_error";
                    break;
                case Constant.SoundType.SOUND_NODE_COMPLETE:            //小节完成点亮星星时
                    mAudioFileName = "as_sound_answer_node_complete";
                    break;
                case Constant.SoundType.SOUND_GET_LIGHTNING:            //获得闪电页
                    mAudioFileName = "as_sound_get_lightning";
                    break;
                case Constant.SoundType.SOUND_ANSWER_END_COMPLETE:      //小测验鼓励页
                    mAudioFileName = "as_sound_answer_end_complete";
                    break;
                case Constant.SoundType.SOUND_ENCOURAGE:                //连错鼓励页
                    mAudioFileName = "as_sound_answer_encourage";
                    break;
                case Constant.SoundType.SOUND_ANSWER_NODE_ENCOURAGE:    //小测验鼓励页
                    mAudioFileName = "as_sound_answer_node_encourage";
                    break;
                default:
                    mAudioFileName = null;
                    break;
            }

            SkipAnswerTypeEntity data = null;
            try {
                data = SQLAction.get()
                        .getSkipAnswerTypeEntityDao()
                        .queryBuilder()
                        .unique();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(mAudioFileName) || data == null || !data.isSound()) return;

            try {
                AssetFileDescriptor afd = getAssets().openFd(mAudioFileName += ".mp3");
//                MediaX mediaX = new MediaX( this );
                mSoundMediaXC.setDataSource(afd);
//                mSoundMediaXC.setOnMediaStatusListener( new MediaXStatusAdapter() {
//                    @Override
//                    public void onCompletion(int currentPlayNum, int playTotal, boolean isCompletion) {
//                        super.onCompletion( currentPlayNum, playTotal, isCompletion );
//                        mediaX.release();
//                    }
//                } );
                mSoundMediaXC.play(mSoundMediaXCTag);
//                if( isLowVal ) {
//                    mMediaX.setDataSource( afd ).play();
//                }else {
//                    mAudioX.setData( afd ).play();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LogUtil.e("Application -> AudioX -> mAudioFileName:" + mAudioFileName);
        });
    }
}
