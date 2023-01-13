package com.gjjy.speechsdk.evaluator;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gjjy.speechsdk.OnEventListener;
import com.gjjy.speechsdk.OnInitListener;
import com.gjjy.speechsdk.OnResultListener;
import com.gjjy.speechsdk.PermManage;
import com.gjjy.speechsdk.evaluator.parser.result.Result;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.handler.Handler;
import com.ybear.ybutils.utils.handler.HandlerManage;
import com.gjjy.speechsdk.OnSpeechStatusListener;
import com.gjjy.speechsdk.OnVolumeChangedListener;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 语音评测
 */
public class SpeechEvaluator {
    private PermManage.Perm mPerm;
    private final Handler mHandler;
    private com.iflytek.cloud.SpeechEvaluator mIse;
    private Params mParams;
    private XmlResultParser mXmlParser;

    private EvaluatorListener mEvaluatorListener;
    private List<OnVolumeChangedListener> mOnVolumeChangedList;
    private List<OnSpeechStatusListener> mOnSpeechStatusList;
    private List<OnEventListener> mOnEventList;
    private List<OnResultListener> mOnResultList;

    private SpeechEvaluator() { mHandler = HandlerManage.create(); }
    public static SpeechEvaluator get() { return HANDLER.I; }
    private static final class HANDLER {
        private static final SpeechEvaluator I = new SpeechEvaluator();
    }

    /**
     * 初始化评测
     * @param context   上下文
     * @return          this
     */
    private SpeechEvaluator init(Context context, OnInitListener l) {
        if( mIse != null ) return this;
        //初始化引擎
        mIse = com.iflytek.cloud.SpeechEvaluator.createEvaluator(context, i -> {
            if( l != null ) l.onInit( i == 0, i );
        });

        mOnVolumeChangedList = new ArrayList<>();
        mOnSpeechStatusList = new ArrayList<>();
        mOnResultList = new ArrayList<>();
        mOnEventList = new ArrayList<>();

        mParams = new Params( context, this );
        mXmlParser = new XmlResultParser();
        //初始化监听器
        mEvaluatorListener = new EvaluatorListener() {
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
                for (OnVolumeChangedListener l : mOnVolumeChangedList) {
                    if( l != null ) l.onChanged(volume, data);
                }
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
                for (OnSpeechStatusListener l : mOnSpeechStatusList) {
                    if( l != null ) l.onBegin();
                }
            }

            /**
             * 结束说话
             *  在SDK检测到音频的静音端点时，回调此函数（在录音模式或写音频模式下都会回调，
             *  应用层主动调用{@link com.iflytek.cloud.SpeechEvaluator#stopEvaluating()}
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
                for (OnSpeechStatusListener l : mOnSpeechStatusList) {
                    if( l != null ) l.onEnd();
                }
            }

            /**
             * 返回结果
             *  返回的结果可能为null，请增加判断处理。
             *
             * 一次会话的结果可能会多次返回（即多次回调此函数），通过参数2，判断是否是最后 一个结果，
             * true时为最后一个结果，否则不是。当最后一个结果返回时，本次会话结束，录音也会停止，在重新调用
             * {@link com.iflytek.cloud.SpeechEvaluator#startEvaluating(String, String, EvaluatorListener)}
             * 开启新的会话前，
             * 停止调用{@link com.iflytek.cloud.SpeechEvaluator#writeAudio(byte[], int, int)}
             * 写入音频(当音频源设置为音频流时 （{@link SpeechConstant#AUDIO_SOURCE}为-1时)。
             * 当出现错误，或应用层调用SpeechEvaluator.cancel()取消当次会话时，在当次 会话过程可能不会回调此函数。
             *
             * 会话过程采用边录边上传的分次上传音频数据方式，可能在结束录音前，就有结果返回。
             * @param result    结果数据
             * @param isLast    是否最后一次结果标记
             */
            @Override
            public void onResult(EvaluatorResult result, boolean isLast) {
                for (OnResultListener l : mOnResultList) {
                    if( l == null ) continue;
                    Result r = mXmlParser.parse( result.getResultString() );
                    l.onResult( r, isLast );
                    LogUtil.e( "SpeechEvaluator -> onResult:" + r.toResultString() );
                }
            }

            /**
             * 错误回调
             * 当此函数回调时，说明当次会话出现错误，会话自动结束，录音也会停止。应在再次调用
             * {@link com.iflytek.cloud.SpeechEvaluator#startEvaluating(String, String, EvaluatorListener)}
             * 开启新的会话前，
             * 停止调用{@link com.iflytek.cloud.SpeechEvaluator#writeAudio(byte[], int, int)}
             * 写入音频(当音频源设置为音频流时 （{@link SpeechConstant#AUDIO_SOURCE}为-1时)。
             * @param err       错误类型
             */
            @Override
            public void onError(SpeechError err) {
                for (OnResultListener l : mOnResultList) {
                    if( l == null ) continue;
                    l.onError( err.getErrorCode(), err.getErrorDescription() );
                }
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
                for (OnEventListener l : mOnEventList) {
                    if( l != null ) l.onEvent( eventType, arg1, arg2, b );
                }
            }
        };
        return this;
    }
    public SpeechEvaluator init(@NonNull Fragment fragment, OnInitListener l) {
        mPerm = PermManage.create( fragment );
        return init( fragment.getContext(), l );
    }
    public SpeechEvaluator init(@NonNull FragmentActivity activity, OnInitListener l) {
        mPerm = PermManage.create( activity );
        return init( (Context) activity, l );
    }
    public SpeechEvaluator init(@NonNull Fragment fragment) {
        return init( fragment, null );
    }
    public SpeechEvaluator init(@NonNull FragmentActivity activity) {
        return init( activity, null );
    }

    /**
     * 释放资源
     * @return  结果
     */
    public boolean release() {
        boolean ret = mIse != null && mIse.destroy();
        mIse = null;
        return ret;
    }

    /**
     * 开始评测
     * @param data  评测数据
     * @param call  回调结果
     */
    public void start(byte[] data, Comparable<Integer> call) {
        authAudio(i -> {
            //未授权麦克风权限
            if( i == -1 ) return 0;
            mHandler.post(() -> {
                int r = 0;
                try {
                    r = getIse().startEvaluating( data, null, mEvaluatorListener );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if( call != null ) call.compareTo( r );
            });
            return 0;
        });
    }
    public void start(byte[] data) { start( data, null ); }

    /**
     * 开始评测
     * @param data  评测数据
     * @param call  回调结果
     */
    public void start(String data, Comparable<Integer> call) {
        authAudio(i -> {
            //未授权麦克风权限
            if( i == -1 ) return 0;
            mHandler.post(() -> {
                int r = 0;
                try {
                    r = getIse().startEvaluating( data, null, mEvaluatorListener );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if( call != null ) call.compareTo( r );
            });
            return 0;
        });
    }
    public void start(String data) { start( data, null ); }

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
     * 停止录入评测语音
     */
    public void stop() {
        mHandler.post(() -> {
            try {
                if( !isEvaluating() ) return;
                getIse().stopEvaluating();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 取消评测
     */
    public void cancel() {
        mHandler.post(() -> {
            try {
                getIse().cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 是否在会话中
     * @return      结果
     */
    public boolean isEvaluating() { return getIse().isEvaluating(); }

    /**
     * 设置语音发生改变事件监听器
     * @param l     监听器
     */
    public void addOnVolumeChangedListener(OnVolumeChangedListener l) {
        mOnVolumeChangedList.add( l );
    }
    public void removeOnVolumeChangedListener(OnVolumeChangedListener l) {
        mOnVolumeChangedList.remove( l );
    }

    /**
     * 设置语音状态事件监听器
     * @param l     监听器
     */
    public void addOnSpeechStatusListener(OnSpeechStatusListener l) {
        mOnSpeechStatusList.add( l );
    }
    public void removeOnSpeechStatusListener(OnSpeechStatusListener l) {
        mOnSpeechStatusList.remove( l );
    }

    /**
     * 设置返回结果事件监听器
     * @param l     监听器
     */
    public void addOnResultListener(OnResultListener l) { mOnResultList.add( l ); }
    public void removeOnResultListener(OnResultListener l) { mOnResultList.remove( l ); }

    /**
     * 设置自定义事件监听器
     * @param l     监听器
     */
    public void addOnEventListener(OnEventListener l) { mOnEventList.add( l ); }
    public void removeOnEventListener(OnEventListener l) { mOnEventList.remove( l ); }

    /**
     * 评测语种
     * @param language  汉语      {@link Params.Language#ZH_CN}
     *                  英语      {@link Params.Language#EN_US}
     * @return          this
     */
    public Params setLanguage(@Params.Language String language) {
        return mParams.setLanguage( language );
    }

    /**
     * 评测题型
     * @param category  单字，汉语专有  {@link Params.Category#SYLLABLE}
     *                  词语          {@link Params.Category#WORD}
     *                  句子          {@link Params.Category#SENTENCE}
     *                  篇章          {@link Params.Category#CHAPTER}
     * @return          this
     */
    public Params setCategory(@Params.Category String category) {
        return mParams.setCategory( category );
    }

    /**
     * 上传的试题编码格式
     * 当进行汉语评测时，必须设置成{@link Params.TextEncode#UTF8}，
     * 建议所有试题都使用{@link Params.TextEncode#UTF8}编码
     *
     * @param encode    {@link Params.TextEncode#GB2312}
     *                  {@link Params.TextEncode#UTF8}
     * @return          this
     */
    public Params setTextEncode(@Params.TextEncode String encode) {
        return mParams.setTextEncode( encode );
    }

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

    /**
     * 录音超时，当录音达到时限将自动触发
     * vad停止录音，
     * @param ms        毫秒。默认-1（无超时）
     * @return          this
     */
    public Params setSpeechTimeout(int ms) { return mParams.setSpeechTimeout( ms ); }

    /**
     * 评测结果等级
     * @param level           {@link Params.Level#Plain}
     *                  默认为：{@link Params.Level#Complete}
     * @return          this
     */
    public Params setLevel(@Params.Level String level) { return mParams.setLevel( level ); }

    /**
     * 全维度功能。更加详细的评分
     * @param enable    是否开通。默认：false
     * @return          this
     */
    public Params setPlev(boolean enable) { return mParams.setPlev( enable ); }

    public SpeechEvaluator build() { return mParams.build(); }

    /**
     * 获取保存的音频文件路径
     * @return          音频路径
     */
    public String getSaveSpeechFilePath() {
        return mParams == null ? "" : mParams.getSaveSpeechFilePath();
    }

    private com.iflytek.cloud.SpeechEvaluator getIse() {
        if( mIse != null ) return mIse;
        throw new NullPointerException("Has not been initialized, please call the init(Context)");
    }

    public static class Params {
        /**
         * 评测语种
         */
        @Retention(RetentionPolicy.SOURCE)
        public @interface Language {
            String ZH_CN = "zh_cn";     //中文
            String EN_US = "en_us";     //英文
        }

        /**
         * 评测题型
         */
        @StringDef({ Category.SYLLABLE, Category.WORD, Category.SENTENCE, Category.CHAPTER })
        @Retention(RetentionPolicy.SOURCE)
        public @interface Category {
            String SYLLABLE = "read_syllable";    //单字，汉语专有
            String WORD = "read_word";            //词语
            String SENTENCE = "read_sentence";    //句子
            String CHAPTER = "read_chapter";      //篇章，需联系商务开通后才可使用
        }

        /**
         * 上传的试题编码格式
         */
        @StringDef({ TextEncode.GB2312, TextEncode.UTF8 })
        @Retention(RetentionPolicy.SOURCE)
        public @interface TextEncode {
            String GB2312 = "gb2312";
            String UTF8 = "utf-8";
        }

        /**
         * 评测结果等级
         */
        @StringDef({ Level.Plain, Level.Complete })
        @Retention(RetentionPolicy.SOURCE)
        public @interface Level {
            String Plain = "plain";
            String Complete = "complete";
        }

        private final SpeechEvaluator mSpEva;
        private Context mContext;
        private String mLanguage = Params.Language.ZH_CN;
        private String mCategory = Params.Category.SENTENCE;
        private String mTextEncode = Params.TextEncode.UTF8;
        private String mLevel = Params.Level.Complete;
        private int mVadBos = 5000;
        private int mVadEos = 2000;
        private int mSpeechTimeout = -1;
        private int mPlev = -1;
        private String mFormat = "pcm";

        public Params(Context context, @NonNull SpeechEvaluator se) {
            mContext = context;
            mSpEva = se;
        }

        public String getLanguage() { return mLanguage; }
        public Params setLanguage(String mLanguage) {
            this.mLanguage = mLanguage;
            return this;
        }

        public String getCategory() { return mCategory; }
        public Params setCategory(String mCategory) {
            this.mCategory = mCategory;
            return this;
        }

        public String getTextEncode() { return mTextEncode; }
        public Params setTextEncode(String mTextEncode) {
            this.mTextEncode = mTextEncode;
            return this;
        }

        public String getLevel() { return mLevel; }
        public Params setLevel(String mLevel) {
            this.mLevel = mLevel;
            return this;
        }

        public int getVadBos() { return mVadBos; }
        public Params setVadBos(int mVadBos) {
            this.mVadBos = mVadBos;
            return this;
        }

        public int getVadEos() { return mVadEos; }
        public Params setVadEos(int mVadEos) {
            this.mVadEos = mVadEos;
            return this;
        }

        public int getSpeechTimeout() { return mSpeechTimeout; }
        public Params setSpeechTimeout(int mSpeechTimeout) {
            this.mSpeechTimeout = mSpeechTimeout;
            return this;
        }

        public int getPlev() { return mPlev; }
        public Params setPlev(boolean enable) {
            this.mPlev = enable ? 0 : -1;
            return this;
        }

        public SpeechEvaluator build() {
            com.iflytek.cloud.SpeechEvaluator ise = null;
            try {
                ise = mSpEva.getIse();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if( ise == null ) return mSpEva;
            //设置评测语言
            ise.setParameter( SpeechConstant.LANGUAGE, mLanguage );
            //设置需要评测的类型
            ise.setParameter( SpeechConstant.ISE_CATEGORY, mCategory );
            //编码格式
            ise.setParameter( SpeechConstant.TEXT_ENCODING, mTextEncode );
            //设置语音前端点:0~10000。静音超时时间，即用户多长时间不说话则当做超时处理
            ise.setParameter( SpeechConstant.VAD_BOS, String.valueOf( mVadBos ) );
            //设置语音后端点:0~10000。后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
            ise.setParameter( SpeechConstant.VAD_EOS, String.valueOf( mVadEos ) );
            //语音输入超时时间，即用户最多可以连续说多长时间；
            ise.setParameter( SpeechConstant.KEY_SPEECH_TIMEOUT, String.valueOf( mSpeechTimeout ) );
            //设置结果等级（中文仅支持complete）
            ise.setParameter( SpeechConstant.RESULT_LEVEL, mLevel );
            //全维度功能。评测更加详细
            ise.setParameter( SpeechConstant.AUDIO_FORMAT_AUE, String.valueOf( mPlev ) );

            //保存的音频格式：pcm、wav
            ise.setParameter( SpeechConstant.AUDIO_FORMAT, mFormat );
            //设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
            ise.setParameter( SpeechConstant.ISE_AUDIO_PATH, getSaveSpeechFilePath() );
            return mSpEva;
        }


        public String getSaveSpeechFilePath() {
            return String.format(
                    "%s%smsc%stts." + mFormat,
                    mContext.getExternalFilesDir( null),
                    File.separator,
                    File.separator
            );
        }
    }
}
