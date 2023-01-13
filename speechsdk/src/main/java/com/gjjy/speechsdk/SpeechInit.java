package com.gjjy.speechsdk;

import android.app.Application;

import androidx.annotation.NonNull;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;
import com.ybear.ybutils.utils.AppUtil;

public class SpeechInit {
    private Application mApp;
    private Build mBuild;

    private SpeechInit() {}
    public static SpeechInit get() { return HANDLER.I; }
    private static final class HANDLER { private static final SpeechInit I = new SpeechInit(); }

    public SpeechInit init(Application app) {
        mApp = app;
        mBuild = new Build();
        return this;
    }

    /**
     * 设置讯飞语音的appid
     * @param id    id
     * @return      {@link Build}
     */
    public Build setAppId(String id) { return mBuild.setAppId( id ); }

    /**
     * 通过Manifest中设置的meta-data设置id
     * @param name  meta-data的name
     * @return      {@link Build}
     */
    public Build setAppIdOfMeta(String name) { return mBuild.setAppIdOfMeta( name ); }

    /**
     * 代理地址
     * @param host  地址
     * @param port  端口
     * @return      {@link Build}
     */
    public Build setProxy(String host, int port) { return mBuild.setProxy( host, port ); }

    /**
     * 代理地址
     * @param nameHost  meta-data的name
     * @param namePort  meta-data的name
     * @return          {@link Build}
     */
    public Build setProxyOfMeta(@NonNull String nameHost, @NonNull String namePort) {
        return mBuild.setProxyOfMeta( nameHost, namePort );
    }

    public void build() { mBuild.build(); }

    /**
     * 启用日志
     * @param enable    是否启用
     * @return          this
     */
    public SpeechInit setEnableLog(boolean enable) {
        Setting.setShowLog( enable );
        return this;
    }

    /**
     * 是否启用地理位置权限
     * @param enable    是否启用
     * @return          this
     */
    public SpeechInit setEnableLocation(boolean enable) {
        Setting.setLocationEnable( enable );
        return this;
    }

    public class Build {
        private final StringBuilder mInfo = new StringBuilder();
        public Build setAppId(String id) {
            mInfo.append("appid=").append( id ).append(",");
            return this;
        }
        public Build setAppIdOfMeta(String name) {
            return setAppId( AppUtil.getMetaDataForString( mApp, name ) );
        }

        public Build setProxy(String host, int port) {
            mInfo.append("net_type=custom,")
                    .append( "proxy_ip=" ).append( host ).append(",")
                    .append( "proxy_port=" ).append( port ).append(",");
            return this;
        }
        public Build setProxyOfMeta(@NonNull String nameHost, @NonNull String namePort) {
            return setProxy(
                    AppUtil.getMetaDataForString( mApp, nameHost ),
                    AppUtil.getMetaDataForInt( mApp, namePort )
            );
        }

        public void build() {
            if( mInfo.length() <= 0 ) throw new NullPointerException();
            //删除最后一个逗号
            mInfo.deleteCharAt( mInfo.length() - 1 );
            //初始化讯飞sdk
            SpeechUtility.createUtility( mApp, mInfo.toString() );
        }
    }
}
