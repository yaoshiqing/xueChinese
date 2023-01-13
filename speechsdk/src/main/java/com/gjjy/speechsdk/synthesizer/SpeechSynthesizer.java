package com.gjjy.speechsdk.synthesizer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.core.util.Consumer;

import com.gjjy.speechsdk.OnEventListener;
import com.gjjy.speechsdk.OnInitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.ybear.ybutils.utils.handler.Handler;
import com.ybear.ybutils.utils.handler.HandlerManage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 语音合成
 */
public class SpeechSynthesizer {
    public interface OnSpeechStatusListener {
        void onBegin();
        void onPaused();
        void onResumed();
        void onCompleted(int code, String msg);
    }

    public interface OnProgressListener {
        void onProgress(int progress, int beginPos, int endPos);
        void onBufferProgress(int progress, int beginPos, int endPos, String info);
    }

    private final Handler mHandler;
    private com.iflytek.cloud.SpeechSynthesizer mTts;
    private Params mParams;

    private SynthesizerListener mSynthesizerListener;
    private final List<OnSpeechStatusListener> mOnSpeechStatusList = new ArrayList<>();
    private final List<OnProgressListener> mOnProgressList = new ArrayList<>();
    private final List<OnEventListener> mOnEventList = new ArrayList<>();

    private SpeechSynthesizer() { mHandler = HandlerManage.create(); }
    public static SpeechSynthesizer get() { return HANDLER.I; }
    private static final class HANDLER {
        private static final SpeechSynthesizer I = new SpeechSynthesizer();
    }

    public SpeechSynthesizer init(Context context, OnInitListener l) {
        if( mTts != null ) return this;
        //初始化引擎
        mTts = com.iflytek.cloud.SpeechSynthesizer.createSynthesizer(context, i -> {
            if( l != null ) l.onInit( i == 0, i );
        });
        mParams = new Params( context, this );
        //初始化监听器
        mSynthesizerListener = new SynthesizerListener() {
            /**
             * 开始播放。SDK回调此函数，通知应用层，将要进行播放。
             * 在第一次回调了{@link SynthesizerListener#onBufferProgress(int, int, int, String)}后，
             * 便会回调此函数，告知应用层，将要进行播放。此函数在一次会话中，只会被调用一次，当出现错误时，可能
             * 不会回调此函数。
             */
            @Override
            public void onSpeakBegin() {
                for( OnSpeechStatusListener l : mOnSpeechStatusList ) {
                    if( l != null ) l.onBegin();
                }
            }

            /**
             * 暂停播放。SDK回调此接口，通知应用，将暂停播放。
             * 仅在当前缓冲音频已播完，下一段音频未到时，SDK回调此函数，告知 应用层，将暂停播放。而当应用主动调用
             * {@link com.iflytek.cloud.SpeechSynthesizer#pauseSpeaking()}暂停播放时，不会回调此函数。
             */
            @Override
            public void onSpeakPaused() {
                for( OnSpeechStatusListener l : mOnSpeechStatusList ) {
                    if( l != null ) l.onPaused();
                }
            }

            /**
             * 恢复播放。SDK回调此接口，通知应用，将恢复播放。
             * 仅在当前暂停由于音频缓存未到而引起的暂停，再恢复播放时，SDK回调此 函数，告知应用层，将恢复播放。
             * 而当应用主动调用 {@link com.iflytek.cloud.SpeechSynthesizer#pauseSpeaking()}暂停播放后，
             * 再调用{@link com.iflytek.cloud.SpeechSynthesizer#resumeSpeaking()}恢复播放时，
             * 不会回调此函数。
             */
            @Override
            public void onSpeakResumed() {
                for( OnSpeechStatusListener l : mOnSpeechStatusList ) {
                    if( l != null ) l.onResumed();
                }
            }

            /**
             * 播放进度
             * SDK回调此接口，通知应用，当前的播放进度。
             * 在回调了{@link SynthesizerListener#onSpeakBegin()}后，便会回调此函数，告知应用层，
             * 当前的播放进度。此函数在一次会话中，可能会被回调多次，且被调用次数与
             * {@link SynthesizerListener#onBufferProgress(int, int, int, String)}的被用次数一样。
             * 且在同一次被调用时，此函数的回调中的文本开始位置和结束位置，与对应那次被调用的
             * {@link SynthesizerListener#onBufferProgress(int, int, int, String)}的值一致。然而，
             * 除了第一次外，播放进度，总是比缓冲进度慢，即可能全部文本已缓冲完成，播放进度还没开始下一次的回调。
             * 当出现错误时，可能不会回调此函数。
             * @param progress      当前待播放音频，占已合成音频数据长度的百分比
             * @param beginPos      文本开始位置
             * @param endPos        文本结束位置
             */
            @Override
            public void onSpeakProgress(int progress, int beginPos, int endPos) {
                for( OnProgressListener l : mOnProgressList ) {
                    if( l != null ) l.onProgress( progress, beginPos, endPos );
                }
            }

            /**
             * 缓冲进度。SDK回调此函数，通知应用层，当前合成音频的缓冲进度。
             * @param progress      当前已合成文本占当前会话全部文本的百分比
             * @param beginPos      文本开始位置，从0开始计数
             * @param endPos        文本结束位置，最大值为（textLen-1）
             * @param info          信息，暂不支持。
             */
            @Override
            public void onBufferProgress(int progress, int beginPos, int endPos, String info) {
                for( OnProgressListener l : mOnProgressList ) {
                    if( l != null ) l.onBufferProgress( progress, beginPos, endPos, info );
                }
            }

            /**
             * 播放完成
             * SDK回调此接口，通知应用，将结束会话。
             *
             * 在音频播放完成，或会话出现错误时，将回调此函数。若应用主动调用
             * {@link com.iflytek.cloud.SpeechSynthesizer#stopSpeaking()}停止会话，则不会回调此函数。
             * @param err   错误信息，若为null，则没有出现错误。
             */
            @Override
            public void onCompleted(SpeechError err) {
                int code = err != null ? err.getErrorCode() : 0;
                String msg = err != null ? err.getErrorDescription() : null;

                for( OnSpeechStatusListener l : mOnSpeechStatusList ) {
                    if( l != null ) l.onCompleted( code, msg );
                }
            }

            /**
             * 合成会话事件。扩展用接口，由具体业务进行约定。
             * @param eventType     消息类型
             * @param arg1          参数1
             * @param arg2          参数2
             * @param b             扩展参数
             */
            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle b) {
                for( OnEventListener l : mOnEventList ) {
                    if( l != null ) l.onEvent( eventType, arg1, arg2, b );
                }
            }
        };
        return this;
    }
    public SpeechSynthesizer init(@NonNull Context context) {
        return init( context, null );
    }

    /**
     * 释放资源
     * @return  结果
     */
    public boolean release() {
        boolean ret = mTts != null && mTts.destroy();
        mTts = null;
        return ret;
    }

    /**
     * 开始合成。开始合成文本并播放音频。
     * @param data  合成数据
     * @return      this
     */
    public SpeechSynthesizer start(String data, Comparable<Integer> call) {
        mHandler.post(() -> getTts(ss -> {
            int r = ss.startSpeaking( data, mSynthesizerListener );
            if( call != null ) call.compareTo( r );
        }));
        return this;
    }
    public SpeechSynthesizer start(String data) { return start( data, null ); }

    /**
     * 停止合成。取消当前合成会话，并停止音频播放。
     * @return      this
     */
    public SpeechSynthesizer stop() {
        mHandler.post( () -> getTts(com.iflytek.cloud.SpeechSynthesizer::stopSpeaking) );
        return this;
    }

    /**
     * 暂停播放。仅在合成播放模式下有效，并不会暂停音频的获取过程，只是在把播放器暂停。
     * @return      this
     */
    public SpeechSynthesizer pause() {
        mHandler.post( () -> getTts(com.iflytek.cloud.SpeechSynthesizer::pauseSpeaking) );
        return this;
    }

    /**
     * 恢复播放。在暂停后，在当前暂停位置开始播放合成的音频。
     * @return      this
     */
    public SpeechSynthesizer resume() {
        mHandler.post( () -> getTts(com.iflytek.cloud.SpeechSynthesizer::resumeSpeaking) );
        return this;
    }

    /**
     * 合成到文件。合成文本到一个音频文件，不播放。
     * @param data  合成数据
     * @param uri   保存的文件路径（含文件名）
     */
    public void synthesizeToUri(String data, String uri, Consumer<Integer> call) {
        getTts(ss -> {
            if( call == null ) return;
            call.accept( ss.synthesizeToUri( data, uri, mSynthesizerListener ) );
        });
    }

    /**
     * 是否在合成状态。包括是否在播放状态，音频从服务端获取完成后，若未播放完成，依然处于当前会话的合成中。
     */
    public void isSpeaking(Consumer<Boolean> call) {
        getTts(ss -> {
            if( call != null ) call.accept( ss.isSpeaking() );
        });
    }

    /**
     * 监听语音状态监听器
     * @param l     监听器
     */
    public void addOnSpeechStatusListener(OnSpeechStatusListener l) {
        mOnSpeechStatusList.add( l );
    }

    /**
     * 移除语音状态监听器
     * @param l     监听器
     */
    public void removeOnSpeechStatusListener(OnSpeechStatusListener l) {
        mOnSpeechStatusList.remove( l );
    }

    /**
     * 监听播放进度监听器
     * @param l     监听器
     */
    public void addOnProgressListener(OnProgressListener l) {
        mOnProgressList.add( l );
    }

    /**
     * 移除播放进度监听器
     * @param l     监听器
     */
    public void removeOnProgressListener(OnProgressListener l) {
        mOnProgressList.remove( l );
    }

    /**
     * 监听自定义事件监听器
     * @param l     监听器
     */
    public void addOnEventListener(OnEventListener l) { mOnEventList.add( l ); }

    /**
     * 移除自定义事件监听器
     * @param l     监听器
     */
    public void removeOnEventListener(OnEventListener l) { mOnEventList.remove( l ); }

    /**
     * 设置引擎类型
     * @param type      在线 {@link Params.EngineType#CLOUD}
     *                  离线 {@link Params.EngineType#LOCAL}
     * @return          参数
     */
    public Params setEngineType(@Params.EngineType String type) {
        return mParams.setEngineType( type );
    }

    /**
     * 设置发声人
     * @param voice     小燕    {@link Params.VoiceName#XIAOYAN}
     *                  许久    {@link Params.VoiceName#XUJIU}
     *                  小萍    {@link Params.VoiceName#XIAOPING}
     *                  小婧    {@link Params.VoiceName#XIAOJING}
     *                  许小宝  {@link Params.VoiceName#XUXIAOBAO}
     *                  小露    {@link Params.VoiceName#V2_XIAOLU}
     * @return          参数
     */
    public Params setVoiceName(@Params.VoiceName String voice) {
        return mParams.setVoiceName( voice );
    }

    /**
     * 采样率
     * @param rate      在线 {@link Params.SampleRate#RATE_8000}
     *                  离线 {@link Params.SampleRate#RATE_16000}
     * @return          参数
     */
    public Params setSampleRate(@Params.SampleRate int rate) {
        return mParams.setSampleRate( rate );
    }

    /**
     * 设置语速
     * @param speed     0~100。默认：50
     * @return          参数
     */
    public Params setSpeed(int speed) { return mParams.setSpeed( speed ); }

    /**
     * 设置音量
     * @param volume    0~100。默认：50
     * @return          参数
     */
    public Params setVolume(int volume) { return mParams.setVolume( volume ); }

    /**
     * 设置语调
     * @param pitch     0~100。默认：50
     * @return          参数
     */
    public Params setPitch(int pitch) { return mParams.setPitch( pitch ); }

//    /**
//     * 获取保存的音频文件路径
//     * @return          音频路径
//     */
//    public String getSaveSpeechFilePath() {
//        return mParams.getSaveSpeechFilePath();
//    }

    public SpeechSynthesizer build() { return mParams.build(); }

    private void getTts(Consumer<com.iflytek.cloud.SpeechSynthesizer> call) {
        if( mTts == null || call == null ) return;
        call.accept( mTts );
    }

    public static class Params {
        /**
         * 引擎类型
         */
        @StringDef({ EngineType.CLOUD, EngineType.LOCAL })
        @Retention(RetentionPolicy.SOURCE)
        public @interface EngineType {
            String CLOUD = SpeechConstant.TYPE_CLOUD;     //在线
            String LOCAL = SpeechConstant.TYPE_LOCAL;     //离线
        }

        /**
         * 发音人
         */
        @Retention(RetentionPolicy.SOURCE)
        public @interface VoiceName {
            String XIAOYAN = "xiaoyan";         //小燕
            String XUJIU = "aisjiuxu";          //许久
            String XIAOPING = "aisxping";       //小萍
            String XIAOJING = "aisjinger";      //小婧
            String XUXIAOBAO = "aisbabyxu";     //许小宝
            String V2_XIAOLU = "x2_yezi";       //小露
        }

        /**
         * 采样率
         */
        @Retention(RetentionPolicy.SOURCE)
        public @interface SampleRate {
            int RATE_8000 = 8000;
            int RATE_16000 = 16000;
        }

        private Params mThis;
        private final SpeechSynthesizer mSpSyn;
        private Context mContext;
        private String mEngineType = EngineType.CLOUD;      //默认：在线引擎
        private String mVoiceName = VoiceName.XIAOYAN;      //默认：小露
        private int mSampleRate = SampleRate.RATE_16000;    //默认：16000采样率
        private int mSpeed = 50;        //语速。0~100，默认：50
        private int mVolume = 50;       //音量。0~100，默认：50
        private int mPitch = 50;        //语调。0~100，默认：50

        public Params(Context context, @NonNull SpeechSynthesizer ss) {
            mContext = context;
            mSpSyn = ss;
            mThis = this;
        }

        public String getEngineType() { return mEngineType; }
        public Params setEngineType(@EngineType String mEngineType) {
            this.mEngineType = mEngineType;
            return this;
        }

        public String getVoiceName() { return mVoiceName; }
        public Params setVoiceName(String mVoiceName) {
            this.mVoiceName = mVoiceName;
            return this;
        }

        public int getSampleRate() { return mSampleRate; }
        public Params setSampleRate(int mSampleRate) {
            this.mSampleRate = mSampleRate;
            return this;
        }

        public int getSpeed() { return mSpeed; }
        public Params setSpeed(int mSpeed) {
            this.mSpeed = mSpeed;
            return this;
        }

        public int getVolume() { return mVolume; }
        public Params setVolume(int mVolume) {
            this.mVolume = mVolume;
            return this;
        }

        public int getPitch() { return mPitch; }
        public Params setPitch(int mPitch) {
            this.mPitch = mPitch;
            return this;
        }

        public SpeechSynthesizer build() {
            mSpSyn.getTts(ss -> setParam( ss, mThis ));
            return mSpSyn;
        }

        private void setParam(com.iflytek.cloud.SpeechSynthesizer tts, Params param) {
            // 清空参数
            tts.setParameter(SpeechConstant.PARAMS, null);
            // 根据合成引擎设置相应参数
            if( param.getEngineType().equals( SpeechConstant.TYPE_CLOUD ) ) {
                tts.setParameter( SpeechConstant.ENGINE_TYPE, param.getEngineType() );
                //支持实时音频返回，仅在synthesizeToUri条件下支持
                tts.setParameter( SpeechConstant.TTS_DATA_NOTIFY, "1" );
                //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");
                // 设置在线合成发音人
                tts.setParameter( SpeechConstant.VOICE_NAME, param.getVoiceName() );
                //采样率
                tts.setParameter( SpeechConstant.SAMPLE_RATE, String.valueOf( param.getSampleRate() ) );
                //设置合成语速
                tts.setParameter( SpeechConstant.SPEED, String.valueOf( param.getSpeed() ) );
                //设置合成音调
                tts.setParameter( SpeechConstant.PITCH, String.valueOf( param.getPitch() ) );
                //设置合成音量
                tts.setParameter( SpeechConstant.VOLUME, String.valueOf( param.getVolume() ) );
            }else {
                tts.setParameter( SpeechConstant.ENGINE_TYPE, param.getEngineType() );
                tts.setParameter( SpeechConstant.VOICE_NAME, param.getVoiceName() );
            }

            //设置播放器音频流类型
            tts.setParameter( SpeechConstant.STREAM_TYPE, "3" );
//            // 设置播放合成音频打断音乐播放，默认为true
//            tts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
//
//            //支持实时音频返回，仅在synthesizeToUri条件下支持
//            tts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
//            // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//            tts.setParameter( SpeechConstant.AUDIO_FORMAT, "pcm" );
//            tts.setParameter( SpeechConstant.TTS_AUDIO_PATH, getSaveSpeechFilePath() );
        }

        //        public String getSaveSpeechFilePath() {
//            return String.format(
//                    "%s%smsc%stts.pcm",
//                    mContext.getExternalFilesDir( null),
//                    File.separator,
//                    File.separator
//            );
//        }
    }
}
