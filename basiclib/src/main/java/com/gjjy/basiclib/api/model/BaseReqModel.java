package com.gjjy.basiclib.api.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.gjjy.basiclib.api.entity.BaseReqEntity;
import com.gjjy.basiclib.entity.BaseResp;
import com.gjjy.basiclib.utils.DOMConstant;
import com.ybear.mvp.model.MvpModel;
import com.ybear.mvp.util.MvpUtil;
import com.ybear.ybnetworkutil.call.CallReqAdapter;
import com.ybear.ybnetworkutil.http.ClientBuilder;
import com.ybear.ybnetworkutil.http.HttpClient;
import com.ybear.ybnetworkutil.http.Req;
import com.ybear.ybnetworkutil.request.Request;
import com.ybear.ybutils.utils.AESUtil;
import com.ybear.ybutils.utils.DOM;
import com.ybear.ybutils.utils.DigestUtil;
import com.ybear.ybutils.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 数据请求Model
 */
public class BaseReqModel extends MvpModel {
    public Req mReq;
    private static final String mAESKey = "8eDdWU3CKP5@jjuK";
    private static final String mSignKey = "xapa1yz6ogcayyhysn3ezw";
    private final char[] mSignSeeds = new char[]{'0', 'a', 'A'};
    private boolean isTokenOverdue = false;

    public BaseReqModel() {
        if (mReq != null) return;
        mReq = HttpClient
                .create()
                .writeTimeout(30)
                .readTimeout(30)
                .connectTimeout(30)
                /*.setHttpReboot( HttpReboot.get() )*/
                .build()
                .addReqDataListener(new CallReqAdapter() {
                    @Override
                    public void onRequest(@NonNull Request r) {
                        super.onRequest(r);
                        String url = r.toUrl();
                        String paramStr = r.toParams();
                        LogUtil.d("[" + r.getMethod() + "] " + (TextUtils.isEmpty(paramStr) ? url : url + "?" + AESUtil.decrypt(paramStr, mAESKey)));
                    }

                    @Override
                    public void onResult(String url, String result) {
                        super.onResult(url, result);
                        BaseReqEntity data = toReqEntityOfBase(result);

                        LogUtil.d("[Result] Url:" + url + " Result:" + data);
                        // 方便正式服与测试服之间切换
                        boolean isNoUser = LogUtil.isDebug() && "用户信息不存在".equals(data.getMsg());
                        int errNum = data.getErrorNumber();
                        // 只允许被调用一次
                        if (isTokenOverdue) {
                            return;
                        }
                        // 未知错误、验证码已失效、验证码错误
                        if (isTokenOverdue = isNoUser || errNum == 1006 || errNum == 1007) {
                            DOM.getInstance().setResult(DOMConstant.LOG_OFF);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        super.onFailure(call, e);
                        e.printStackTrace();
                        LogUtil.d("[Failure] ErrorMsg:" + e.getMessage());
                    }
                });
    }

    private <R extends Request> ClientBuilder getReqBuilder(R api) {
        // 签名
        api.addParam("time_stamp", System.currentTimeMillis() / 1000L);
        api.addParam("random_str", getRandomStr());
        api.addParam("sign", DigestUtil.toMD5(api.getParam().toParamString().toLowerCase() + mSignKey));
        // 加密请求参数
        api.setParam(AESUtil.encrypt(JSONObject.toJSONString(api.getParam().getMap()), mAESKey));
        return mReq.req(api).mediaTypeJson().requestBody();
    }

    private String getRandomStr() {
        StringBuilder ret = new StringBuilder();
        int randomCount = 8;
        for (int i = 0; i < randomCount; i++) {
            int index = (int) (Math.random() * 3D);
            int max = index == 0 ? 10 : 26;
            ret.append((char) (mSignSeeds[index] + Math.random() * max));
        }
        return ret.toString();
    }

    /**
     * Api请求
     * @param api 请求的api
     */
    public void reqApi(Request api) {
        getReqBuilder(api).post();
    }

    /**
     * Api请求 - 附带一个文件
     * @param api       请求的api
     * @param mediaType 文件类型
     * @param fileKey   key
     * @param file      文件
     */
    public void reqApi(Request api, String mediaType, String fileKey, File file) {
        getReqBuilder(api)
                .mediaTypeForm()
                .multipartBodyOfFileAndParam(mediaType, fileKey, file)
                .post();
    }

    public void reqApiOfGet(Request api) {
        mReq.req(api).mediaTypeForm().formBody().get();
    }

    /**
     * 基础请求。与基础请求一致，只增加了一个extRootNode用于请求数据中的指定数据源
     * eg：{"status": 0,"data": { "ext":{...}} }，其中ext就是指定获取的数据源
     *
     * @param s           请求到的数据源
     * @param extRootNode 额外的根标签
     * @param cls         需要转换的实体类
     * @param <T>         泛型的实体
     * @return 返回转换后的实体
     */
    @Nullable
    public <T extends BaseReqEntity> T toReqEntity(@Nullable String s, @Nullable String extRootNode, @NonNull Class<T> cls) {
        T ret = null;
        BaseReqEntity baseEntity = toReqEntityOfBase(s);
        String data = baseEntity.getData();
        String extData = null;
        JSONObject jObj;
        if (!TextUtils.isEmpty(data)) {
            try {
                //根节点是为了防止data中存在额外的节点，否则转换会失败
                jObj = JSONObject.parseObject(data);
                if (jObj.containsKey(extRootNode)) {
                    extData = jObj.getString(extRootNode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //转换为传入的泛型class
        try {
            ret = JSONObject.parseObject(extData != null ? extData : data, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ret == null) ret = MvpUtil.newClass(cls);
        if (ret != null) {
            ret.setCode(baseEntity.getCode());
            ret.setMsg(baseEntity.getMsg());
            ret.setData(baseEntity.getData());
            ret.setErrorNumber(baseEntity.getErrorNumber());
            ret.setErrCode(baseEntity.getErrCode());
            ret.setErrMsg(baseEntity.getErrMsg());
        }
        return ret;
    }

    @Nullable
    public <E extends BaseReqEntity> E toReqEntity(@Nullable String s, @NonNull Class<E> cls) {
        return toReqEntity(s, null, cls);
    }

    @Nullable
    public <T> T toReqEntityNotBase(@Nullable String s, @NonNull Class<T> cls) {
        BaseReqEntity data = toReqEntityOfBase(s);
        try {
            if (data != null) {
                if (data.getData() != null) {
                    return JSONObject.parseObject(data.getData(), cls);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Object toReqEntityOfValue(@Nullable String s, @NonNull String key) {
        BaseReqEntity data = toReqEntityOfBase(s);
        try {
            return JSONObject.parseObject(data.getData()).get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 基础请求转换
     * @param s           请求到的数据
     * @param extRootNode 额外的根节点，即data数据中存在额外的根节点，例如：weixinInfo，没有填：null
     * @param cls         继承的实体class
     * @param <T>         泛型
     * @return 返回处理的实体
     */
    @NonNull
    public <T> List<T> toReqEntityOfList(@Nullable String s, @Nullable String extRootNode, @NonNull Class<T> cls) {
        List<T> callList = new ArrayList<>();
        List<T> tmpList = null;
        BaseReqEntity reqE = toReqEntityOfBase(s);
        String data = reqE.getData();
        String extData = null;
        try {
            //根节点是为了防止data中存在额外的节点，否则转换会失败
            if (extRootNode != null) {
                try {
                    extData = JSONObject.parseObject(data).getString(extRootNode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //转换为传入的泛型class列表
            try {
                tmpList = JSONObject.parseArray(extData != null ? extData : data, cls);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tmpList != null) {
                callList.addAll(tmpList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("toReqEntityOfList -> Error:" + e.getMessage());
        }
        return callList;
    }

    @NonNull
    public <T> List<T> toReqEntityOfList(@Nullable String s, @NonNull Class<T> cls) {
        return toReqEntityOfList(s, null, cls);
    }

    /**
     * 基础请求。请求的数据源转换成{@link BaseReqEntity}
     *
     * @param s 请求到的数据源
     * @return 返回转换后的实体
     */
    @NonNull
    public BaseReqEntity toReqEntityOfBase(@Nullable String s) {
        BaseReqEntity data = toReqEntityOfBase(s, BaseReqEntity.class);
        if (data == null) {
            data = new BaseReqEntity();
        }

        // 新增 返回字段处理模式，优先处理errCode 字段
        if (data.getErrCode() == 0) {
            // 解密
            if (data.getData() != null) {
                data.setData(AESUtil.decrypt(data.getData(), mAESKey));
            }
        } else {
            BaseResp baseResp = toReqEntityOfBase(s, BaseResp.class);
            if (baseResp != null) {
                data.setErrMsg(baseResp.getErrMsg());
                data.setErrCode(baseResp.getErrCode());
            }
        }

        if (data.getCode() == 0) {
            // 未知错误
            LogUtil.e("toReqEntityOfBase -> " + data.toString());
        } else {
            // 解密
            if (data.getData() != null) {
                data.setData(AESUtil.decrypt(data.getData(), mAESKey));
            }
        }
        return data;
    }

    @Nullable
    public <T> T toReqEntityOfBase(@Nullable String s, Class<T> clazz) {
        try {
            return JSONObject.parseObject(s, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public <T> T toReqEntityOfBaseData(@Nullable String s, Class<T> clazz) {
        BaseReqEntity data = toReqEntityOfBase(s, BaseReqEntity.class);
        if (data == null) {
            return null;
        }
        try {
            if (data.getData() != null) {
                return JSONObject.parseObject(data.getData(), clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}