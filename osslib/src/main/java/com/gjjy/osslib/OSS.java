package com.gjjy.osslib;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.gjjy.osslib.entity.OssData;
import com.gjjy.speechsdk.PermManage;
import com.ybear.ybutils.utils.AppUtil;
import com.ybear.ybutils.utils.DigestUtil;
import com.ybear.ybutils.utils.IOUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.time.DateTime;
import com.ybear.ybutils.utils.time.DateTimeType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OSS {
    private final List<OssData> mOSSQueue = new ArrayList<>();
    private final ExecutorService mOSSPool = Executors.newSingleThreadExecutor();
    private final List<OnOSSCompletedListener> mOnOSSCompletedList = new ArrayList<>();

    private Application mApp;
    private com.alibaba.sdk.android.oss.OSS mOSS;
    private String mOSSBucket;
    private String mOSSObjectName;
    private String mOSSRootUrl;
    private boolean isOSSQueueRunning;
    private int mOSSQueueIndex;
    private int mOSSQueueCount;

    private OSS() {
    }

    public static OSS get() {
        return HANDLER.I;
    }

    private static final class HANDLER {
        private static final OSS I = new OSS();
    }

    /**
     * OSS初始化
     *
     * @param accessKeyId     ID
     * @param accessKeySecret 秘钥
     * @param endpoint        访问域名
     * @param bucket          存储空间
     * @return this
     */
    public OSS init(Application app,
                    String accessKeyId,
                    String accessKeySecret,
                    String endpoint,
                    String bucket,
                    String url,
                    String securityToken,
                    boolean isEnableLog) {
        mApp = app;
        if (mOSS != null) {
            return this;
        }

        if (isEmpty(accessKeyId)) {
            logE("accessKeyId=null");
            return this;
        }
        if (isEmpty(accessKeySecret)) {
            logE("accessKeySecret=null");
            return this;
        }
        if (isEmpty(endpoint)) {
            logE("endpoint=null");
            return this;
        }
        if (isEmpty(bucket)) {
            logE("bucket=null");
            return this;
        }
        if (isEmpty(url)) {
            logE("url=null");
            return this;
        }
        if (isEmpty(securityToken)) {
            logE("securityToken=null");
            return this;
        }

        setEnableLog(isEnableLog);

        ClientConfiguration conf = new ClientConfiguration();
        // 连接超时，默认15秒
        conf.setConnectionTimeout(15 * 1000);
        // socket超时，默认15秒
        conf.setSocketTimeout(15 * 1000);
        // 最大并发请求数，默认5个
        conf.setMaxConcurrentRequest(5);
        // 失败后最大重试次数，默认2次
        conf.setMaxErrorRetry(2);

        OSSCredentialProvider credentialProvider = null;
        try {
            credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken) {
                @Override
                public OSSFederationToken getFederationToken() {
                    // 可实现自动获取token
                    return super.getFederationToken();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        OSSLog.enableLog();
        if (credentialProvider == null) {
            return this;
        }
        mOSS = new OSSClient(app, endpoint, credentialProvider, conf);
        mOSSBucket = bucket;
        mOSSObjectName = AppUtil.getMetaDataForString(app, "ALI_OBJECT_OSS_NAME");
        mOSSRootUrl = url;
        reset();
        return this;
    }

    public void setEnableLog(boolean enable) {
        if (enable) {
            OSSLog.enableLog();
        } else {
            OSSLog.disableLog();
        }
    }

    public OSS init(Application app,
                    String accessKeyId, String accessKeySecret,
                    String endpoint, String bucket, String url, String securityToken) {
        return init(app, accessKeyId, accessKeySecret, endpoint, bucket, url, securityToken, false);
    }

    public boolean isInit() {
        return mOSS != null;
    }

    private void putQueue(String objectName, File... fs) {
        if (isEmpty(objectName) || fs == null || fs.length == 0) return;
        logE("putQueue -> ObjectName:" + objectName);

        for (File f : fs) {
            if (!f.exists()) continue;
            mOSSQueue.add(new OssData(getOSSObjectName(objectName, f.getName()), f.getAbsolutePath()));
        }
        mOSSQueueCount = mOSSQueue.size();

        if (!isOSSQueueRunning) {
            isOSSQueueRunning = true;
            mOSSPool.execute(this::runQueue);
        }
    }

    private void runQueue() {
        if (mOSSQueue.size() == 0) {
            reset();
            return;
        }
        //取出队列
        OssData data = mOSSQueue.remove(0);
        final String filePath = data.getFilePath();
        //上传文件
        uploadFile(data.getObjectName(), filePath, new OnOSSCompletedListener() {
            @Override
            public void onSuccess(String url, String objectName, int index, int count) {
                dispatchQueue(url, objectName, true, filePath);
            }

            @Override
            public void onFailure(String url, String objectName, int index, int count) {
                dispatchQueue(url, objectName, false, filePath);
            }
        });
    }

    /**
     * 分发队列
     *
     * @param isSuccess 处理结果
     */
    private void dispatchQueue(String url, String objectName, boolean isSuccess, String filePath) {
        logE("dispatchQueue -> Url:" + url + " | ObjName:" + objectName + " | isSuccess:" + isSuccess);
        for (OnOSSCompletedListener l : mOnOSSCompletedList) {
            if (l == null) continue;
            if (isSuccess) {
                l.onSuccess(url, objectName, mOSSQueueIndex, mOSSQueueCount);
            } else {
                l.onFailure(url, objectName, mOSSQueueIndex, mOSSQueueCount);
            }
        }
        if (mApp != null) {
            IOUtil.deleteFile(mApp, filePath);
        }
        mOSSQueueIndex++;
        mOSSPool.execute(this::runQueue);
    }

    private void reset() {
        isOSSQueueRunning = false;
        mOSSQueueIndex = 0;
        mOSSQueueCount = 0;
        mOSSQueue.clear();
    }

    private void uploadFile(String objectName, String path, OnOSSCompletedListener l) {
        logE("UploadFile -> ObjectName:" + objectName + " | Path:" + path);
        PutObjectRequest putObjectRequest = new PutObjectRequest(mOSSBucket, objectName, path);

        mOSS.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                logD("asyncPutObject -> onSuccess");
                logD("asyncPutObject -> ETag: " + result.getETag());
                logD("asyncPutObject -> RequestId: " + result.getRequestId());
                if (l == null) return;
                l.onSuccess(mOSSRootUrl + objectName, objectName, 0, 0);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                //本地异常，如网络异常等。
                if (clientException != null) clientException.printStackTrace();
                if (serviceException != null) {
                    // 服务异常
                    logE("asyncPutObject -> onFailure");
                    logE("asyncPutObject -> ErrorCode: " + serviceException.getErrorCode());
                    logE("asyncPutObject -> RequestId: " + serviceException.getRequestId());
                    logE("asyncPutObject -> HostId: " + serviceException.getHostId());
                    logE("asyncPutObject -> RawMessage: " + serviceException.getRawMessage());
                }
                if (l == null) return;
                l.onFailure(mOSSRootUrl + objectName, objectName, 0, 0);
            }
        });
    }

    public void uploadAvatar(File f) {
        putQueue("avatar", f);
    }

    public void uploadFeedback(File... fs) {
        putQueue("feedback", fs);
    }

    public OSS addOnOSSCompletedListener(OnOSSCompletedListener l) {
        mOnOSSCompletedList.add(l);
        return this;
    }

    public OSS removeOnOSSCompletedListener(OnOSSCompletedListener l) {
        mOnOSSCompletedList.remove(l);
        return this;
    }

    public void reqExternalStoragePerm(FragmentActivity activity) {
        PermManage.create(activity).reqExternalStoragePerm();
    }

    private String getOSSObjectName(@NonNull String pathName, @NonNull String fileName) {
        String[] spFileNames = null;
        try {
            spFileNames = fileName.split("\\.");
        } catch (Exception ignored) {
        }
        boolean checkFileName = !(spFileNames == null || spFileNames.length == 0);
        return format(
                pathName,
                !checkFileName ? fileName : spFileNames[0],
                !checkFileName ? "" : spFileNames[1]
        );
    }

    private void logD(String s) {
        LogUtil.d("OSS", s);
    }

    private void logE(String s) {
        LogUtil.e("OSS", s);
    }

    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    private String nowTime() {
        return DateTime.nows(DateTimeType.YEAR, DateTimeType.MONTH, DateTimeType.DAY);
    }

    private String format(String pathName, String name, String suffix) {
        return String.format(
                mOSSObjectName + "/%s/%s/%s",
                pathName,
                nowTime(),
                DigestUtil.toMD5(name + "_" + System.currentTimeMillis()) + "." + suffix
        );
    }
}
