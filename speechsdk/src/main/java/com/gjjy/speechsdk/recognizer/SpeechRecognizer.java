package com.gjjy.speechsdk.recognizer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gjjy.speechsdk.OnInitListener;
import com.gjjy.speechsdk.PermManage;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.ybear.ybutils.utils.handler.Handler;
import com.ybear.ybutils.utils.handler.HandlerManage;
import com.gjjy.speechsdk.OnEventListener;
import com.gjjy.speechsdk.OnResultListener;
import com.gjjy.speechsdk.OnSpeechStatusListener;
import com.gjjy.speechsdk.OnVolumeChangedListener;
import com.gjjy.speechsdk.evaluator.parser.result.Result;
import com.gjjy.speechsdk.recognizer.parser.JsonParser;
import com.gjjy.speechsdk.synthesizer.SpeechSynthesizer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 语音转写
 */
public class SpeechRecognizer {
    private PermManage.Perm mPerm;
    private Handler mHandler;
    private com.iflytek.cloud.SpeechRecognizer mIat;
    private Params mParams;

    private RecognizerListener mRecognizerListener;
    private OnVolumeChangedListener mOnVolumeChangedListener;
    private OnSpeechStatusListener mOnSpeechStatusListener;
    private OnResultListener mOnResultListener;
    private OnEventListener mOnEventListener;

    private SpeechRecognizer() { mHandler = HandlerManage.create(); }
    public static SpeechRecognizer get() { return SpeechRecognizer.HANDLER.I; }
    private static final class HANDLER {
        private static final SpeechRecognizer I = new SpeechRecognizer();
    }

    private SpeechRecognizer init(Context context, OnInitListener l) {
        if( mIat != null ) return this;
        //初始化引擎
        mIat = com.iflytek.cloud.SpeechRecognizer.createRecognizer(context, i -> {
            if( l != null ) l.onInit( i == 0, i );
        });
        mParams = new Params( this );
        //初始化监听器
        mRecognizerListener = new RecognizerListener() {
            /**
             * 当开始识别，到停止录音（停止写入音频流）或SDK返回最后一个结果自动结束识别为止，
             * SDK检测到音频数据（正在录音或写入音频流）的音量变化时，会多次通过此函数回调，告知应用层当前的音量值。
             * 应用层可通过此函数传入的值变化，改变自定义UI的画面等。
             * @param volume    当前音量值，范围[0-30]
             * @param data      录音数据，格式请参考{@link SpeechConstant#SAMPLE_RATE},
             *                  {@link SpeechConstant#AUDIO_FORMAT}。此参数返回的数据，
             *                  即使 {@link SpeechConstant#NOTIFY_RECORD_DATA}为false，也会返回。
             */
            @Override
            public void onVolumeChanged(int volume, byte[] data) {
                if( mOnVolumeChangedListener == null ) return;
                mOnVolumeChangedListener.onChanged(volume, data);
            }
            /**
             * 开始说话
             *  在录音模式(音频源参数设为 > -1时 )下， 调用开始录音函数后，会自动开启系统的录音机，并在录音机开启后，
             *  会回调此函数（这中间的过程应该在几毫秒内，可以忽略，除非系 统响应很慢）。
             *
             * 应用层可通过此函数回调，告知用户，当前可开始说话。若应用层使用的是声音提示，则应该在调用开始录音函数前，
             * 提放提示音，并在提示音非静音数据播放结束时，调用开始录音函数，开始录音。
             * 若太早调用，则可能会把提示音录进要识别的音频中，
             * 若太晚，则可能会漏掉部分用户说话的音频。
             */
            @Override
            public void onBeginOfSpeech() {
                if( mOnSpeechStatusListener != null ) mOnSpeechStatusListener.onBegin();
            }
            /**
             * 结束说话
             *  在SDK检测到音频的静音端点时，回调此函数（在录音模式或写音频模式下都会回调，
             *  应用层主动调用{@link com.iflytek.cloud.SpeechRecognizer#stopListening()}
             *  则不会回调此函数，在识别出错时，可能不会回调此函数）。
             *  在此函数回调后，当前识别会话可能并没有结束，识别结果可能还要等待一定的时间才会返回。
             *
             * 此函数回调后，应用层应立即停止调用
             * {@link com.iflytek.cloud.SpeechEvaluator#writeAudio(byte[], int, int)}
             * 写入音 频数据，(当音频源设置为音频流时（{@link SpeechConstant#AUDIO_SOURCE}为-1时)
             * 否则，再通过{@link com.iflytek.cloud.SpeechEvaluator#writeAudio(byte[], int, int)}
             * 写入的音频也会被忽略。
             *
             * 应用层可以通过此函数回调，告知用户，当次说话已结束，正在等待识别结果（若结果 未返回）等。
             */
            @Override
            public void onEndOfSpeech() {
                if( mOnSpeechStatusListener != null ) mOnSpeechStatusListener.onEnd();
            }
            /**
             * 返回结果。可能为null，请增加判断处理。
             * 一次识别会话的结果可能会多次返回（即多次回调此函数），通过参数2,判断是否是最后 一个结果，
             * true时为最后一个结果，否则不是。当最后一个结果返回时，本次会话结束，录音也会停止，在重新调用
             * {@link com.iflytek.cloud.SpeechRecognizer#startListening(RecognizerListener)}
             * 开启新的识别会话前，停止调用
             * {@link com.iflytek.cloud.SpeechRecognizer#writeAudio(byte[], int, int)}
             * 写入音频(当音频源设置为音频流时 （{@link SpeechConstant#AUDIO_SOURCE}为-1时)。
             * 当出现错误或应用层调用{@link com.iflytek.cloud.SpeechRecognizer#cancel()}取消当次识别时，
             * 在当次识 别会话过程可能不会回调此函数。
             *
             * 识别采用边录边上传的分次上传音频数据方式，可能在结束录音前，就有结果返回。
             * @param result    结果数据
             * @param isLast    是否最后一次结果标记
             */
            @Override
            public void onResult(RecognizerResult result, boolean isLast) {
                if( mOnResultListener == null ) return;
                mOnResultListener.onResult(new Result()
                                .setContent(
                                        JsonParser.parseIatResult( result.getResultString() )
                                ),
                        isLast
                );
            }
            /**
             * 错误回调
             * 当此函数回调时，说明当次会话出现错误，会话自动结束，录音也会停止。应在再次调用
             * {@link com.iflytek.cloud.SpeechRecognizer#startListening(RecognizerListener)}
             * 开启新的会话前，
             * 停止调用{@link com.iflytek.cloud.SpeechEvaluator#writeAudio(byte[], int, int)}
             * 写入音频(当音频源设置为音频流时 （{@link SpeechConstant#AUDIO_SOURCE}为-1时)。
             * @param err       错误类型
             */
            @Override
            public void onError(SpeechError err) {
                if( mOnResultListener == null ) return;
                mOnResultListener.onError( err.getErrorCode(), err.getErrorDescription() );
            }
            /**
             * 扩展用接口，由具体业务进行约定。例如eventType为0显示网络状态，agr1为网络连接值。
             * @param eventType     消息类型
             * @param arg1          参数1
             * @param arg2          参数2
             * @param b             消息内容
             */
            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle b) {
                if( mOnEventListener != null ) mOnEventListener.onEvent( eventType, arg1, arg2, b );
            }
        };
        return this;
    }
    public SpeechRecognizer init(@NonNull Fragment fragment, OnInitListener l) {
        mPerm = PermManage.create( fragment );
        return init( fragment.getContext(), l );
    }
    public SpeechRecognizer init(@NonNull FragmentActivity activity, OnInitListener l) {
        mPerm = PermManage.create( activity );
        return init( (Context) activity, l );
    }
    public SpeechRecognizer init(@NonNull Fragment fragment) {
        return init( fragment, null );
    }
    public SpeechRecognizer init(@NonNull FragmentActivity activity) {
        return init( activity, null );
    }

    /**
     * 释放资源
     * @return  结果
     */
    public boolean release() {
        boolean ret = mIat != null && mIat.destroy();
        mIat = null;
        return ret;
    }

    /**
     * 开始录制
     * @param call  回调结果
     */
    public void start(Comparable<Integer> call) {
        authAudio(i -> {
            //未授权麦克风权限
            if( i == -1 ) return 0;
            mHandler.post(() -> {
                int r = getIat().startListening( mRecognizerListener );
                if( call != null ) call.compareTo( r );
            });
            return 0;
        });
    }
    public void start() { start( null ); }

    /**
     * 麦克风授权
     * @param call  授权结果
     */
    private void authAudio(Comparable<Integer> call) {
        mPerm.reqRecordAudioPerm((isGranted, name, shouldShowRequestPermissionRationale) -> {
            if( call != null ) call.compareTo( isGranted ? 0 : -1 );
        });
    }

    /**
     * 停止录入
     */
    public void stop() {
        if( isListening() ) mHandler.post(() -> getIat().stopListening());
    }

    /**
     * 取消录入
     */
    public void cancel() { mHandler.post(() -> getIat().cancel()); }

    /**
     * 是否在会话中
     * @return      结果
     */
    public boolean isListening() { return getIat().isListening(); }

    /**
     * 设置语音发生改变事件监听器
     * @param l     监听器
     */
    public void setOnVolumeChangedListener(OnVolumeChangedListener l) {
        mOnVolumeChangedListener = l;
    }

    /**
     * 设置语音状态事件监听器
     * @param l     监听器
     */
    public void setOnSpeechStatusListener(OnSpeechStatusListener l) {
        mOnSpeechStatusListener = l;
    }

    /**
     * 设置返回结果事件监听器
     * @param l     监听器
     */
    public void setOnResultListener(OnResultListener l) {
        mOnResultListener = l;
    }

    /**
     * 设置自定义事件监听器
     * @param l     监听器
     */
    public void setOnEventListener(OnEventListener l) {
        mOnEventListener = l;
    }

    /**
     * 应用领域
     * @param accent    默认：{@link Params.Accent#IAT}
     *                  医疗：{@link Params.Accent#MEDICAL}
     * @return          参数
     */
    public Params setAccent(@Params.Accent String accent) { return mParams.setAccent( accent ); }

    /**
     * 语言区域
     * @param language  语言
     * @return          参数
     */
    public Params setLanguage(@Params.Language String language) {
        return mParams.setLanguage( language );
    }

    /**
     * 引擎类型
     * @param type      在线：{@link Params.EngineType#CLOUD}
     *                  离线：{@link Params.EngineType#LOCAL}
     * @return          参数
     */
    public Params setEngineType(@Params.EngineType String type) {
        return mParams.setEngineType( type );
    }

    /**
     * 采样率
     * @param rate      8KHZ： {@link Params.SampleRate#RATE_8000}
     *                  16KHZ：{@link Params.SampleRate#RATE_16000}
     * @return          参数
     */
    public Params setSampleRate(@Params.SampleRate int rate) { return mParams.setSampleRate( rate ); }

    /**
     * 是否启用方言
     * @param enable    是否启用
     * @return          参数
     */
    public Params setEnableAccent(boolean enable) { return mParams.setEnableAccent( enable ); }

    /**
     * 标点符号（仅限简体中文）
     * @param enable    是否启用标点符号
     * @return          参数
     */
    public Params setEnablePtt(boolean enable) { return mParams.setEnablePtt( enable ); }

    /**
     * 语音前端点。静音超时时间，即用户多长时间不说话则当做超时处理
     * @param ms    毫秒
     * @return      this
     */
    public Params setVadBos(int ms) { return mParams.setVadBos( ms ); }

    /**
     * 语音后端点。后端点静音检测时间，即用户停止说话多长时间内即认为不再输入，自动停止录音
     * @param ms    毫秒。默认2000
     * @return      this
     */
    public Params setVadEos(int ms) { return mParams.setVadEos( ms ); }

    public SpeechRecognizer build() { return mParams.build(); }

    private com.iflytek.cloud.SpeechRecognizer getIat() {
        if( mIat != null ) return mIat;
        throw new NullPointerException("Has not been initialized, please call the init(Context)");
    }

    public static class Params {
        /**
         * 应用领域
         */
        @StringDef({ Params.Domain.IAT, Params.Domain.MEDICAL })
        @Retention(RetentionPolicy.SOURCE)
        public @interface Domain {
            String IAT = "iat";             //默认
            String MEDICAL = "medical";     //医疗
        }

        /**
         * 语言区域
         */
        @Retention(RetentionPolicy.SOURCE)
        public @interface Language {
            String ZH_CN = "zh_cn";     //中文
            String EN_US = "en_us";     //英文
            String JA_JP = "ja_jp";     //日语
            String KO_KR = "ko_kr";     //韩语
            String RU_RU = "ru-ru";     //俄语
            String FR_FR = "fr_fr";     //法语
            String ES_ES = "es_es";     //西班牙语
        }

        /**
         * 应用领域
         */
        @StringDef({ Params.Domain.IAT, Params.Domain.MEDICAL })
        @Retention(RetentionPolicy.SOURCE)
        public @interface Accent {
            String IAT = "iat";             //默认
            String MEDICAL = "medical";     //医疗
        }

        /**
         * 引擎类型
         */
        @StringDef({
                SpeechSynthesizer.Params.EngineType.CLOUD,
                SpeechSynthesizer.Params.EngineType.LOCAL
        })
        @Retention(RetentionPolicy.SOURCE)
        public @interface EngineType {
            String CLOUD = SpeechConstant.TYPE_CLOUD;     //在线
            String LOCAL = SpeechConstant.TYPE_LOCAL;     //离线
        }

        /**
         * 采样率
         */
        @Retention(RetentionPolicy.SOURCE)
        public @interface SampleRate {
            int RATE_8000 = 8000;
            int RATE_16000 = 16000;
        }

        private final SpeechRecognizer mSpRec;
        private String mAccent = Accent.IAT;                 //短信和日常用语：iat (默认)，medical：医疗
        private String mLanguage = Language.ZH_CN;
        private String mEngineType = EngineType.CLOUD;       //默认：在线引擎
        private int mSampleRate = SampleRate.RATE_16000;     //默认：16000采样率
        private boolean isEnableAccent = true;               //是否启用方言（仅限简体中文）
        private boolean isEnablePtt = true;                  //标点符号（仅限简体中文）
        private int mVadBos = 5000;
        private int mVadEos = 2000;

        public Params(@NonNull SpeechRecognizer sr) { mSpRec = sr; }

        public String getAccent() { return mAccent; }
        public Params setAccent(String accent) {
            mAccent = accent;
            return this;
        }

        public String getLanguage() { return mLanguage; }
        public Params setLanguage(@Language String language) {
            mLanguage = language;
            return this;
        }

        public String getEngineType() { return mEngineType; }
        public Params setEngineType(@EngineType String type) {
            mEngineType = type;
            return this;
        }

        public int getSampleRate() { return mSampleRate; }
        public Params setSampleRate(int rate) {
            mSampleRate = rate;
            return this;
        }

        public boolean isEnableAccent() { return isEnableAccent; }
        public Params setEnableAccent(boolean enable) {
            isEnableAccent = enable;
            return this;
        }

        public boolean isEnablePtt() { return isEnablePtt; }
        public Params setEnablePtt(boolean enable) {
            isEnablePtt = enable;
            return this;
        }

        public int getVadBos() { return mVadBos; }
        public Params setVadBos(int vadBos) {
            mVadBos = vadBos;
            return this;
        }

        public int getVadEos() { return mVadEos; }
        public Params setVadEos(int vadEos) {
            mVadEos = vadEos;
            return this;
        }

        public SpeechRecognizer build() {
            com.iflytek.cloud.SpeechRecognizer iat = mSpRec.getIat();
            boolean isZhCN = Language.ZH_CN.equals( mLanguage );
            // 清空参数
            iat.setParameter( SpeechConstant.PARAMS, null );
            //短信和日常用语
            iat.setParameter( SpeechConstant.ACCENT, mAccent );
            // 设置引擎
            iat.setParameter( SpeechConstant.ENGINE_TYPE, mEngineType );
            //区域语言
            iat.setParameter( SpeechConstant.LANGUAGE, mLanguage );
            //方言
            iat.setParameter( SpeechConstant.ACCENT, isEnableAccent && isZhCN ? "mandarin" : null );
            // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
            iat.setParameter(SpeechConstant.ASR_PTT, isEnablePtt && isZhCN ? "1" : "0" );
            //采样率
            iat.setParameter( SpeechConstant.SAMPLE_RATE, String.valueOf( mSampleRate ) );
            // 设置返回结果格式
            iat.setParameter( SpeechConstant.RESULT_TYPE, "json" );
            // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
            iat.setParameter( SpeechConstant.VAD_BOS, String.valueOf( mVadBos ) );
            // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
            iat.setParameter(SpeechConstant.VAD_EOS, String.valueOf( mVadEos ) );

//            if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
//                // 设置本地识别资源
//                iat.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
//            }
//            // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//            iat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//            iat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
            return mSpRec;
        }
    }
}
