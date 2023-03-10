package com.gjjy.basiclib.mvp.model;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.XueChineseApplication;
import com.gjjy.basiclib.api.entity.BaseReqEntity;
import com.gjjy.basiclib.api.model.BaseReqModel;
import com.gjjy.basiclib.utils.DOMConstant;
import com.ybear.ybnetworkutil.request.Request;
import com.ybear.ybutils.utils.DOM;

import java.io.File;

public class BasicGlobalReqModel extends BaseReqModel implements DOM.OnResultListener {
    private static String mUid;
    private static String mToken;

    @Override
    public void onCreateModel() {
        super.onCreateModel();
        DOM.getInstance().registerResult(this);
    }

    @Override
    public void onDestroyModel() {
        super.onDestroyModel();
        DOM.getInstance().unRegisterResult(this);
    }

    public void setUid(String uid, String token) {
        mUid = uid;
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public String getUid() {
        return mUid;
    }

    public boolean isEmptyOfUid() {
        return TextUtils.isEmpty(mUid);
    }

    public void callbackSuccess(Request api, Consumer<Boolean> call) {
        api.setCallbackString((s, isResponse) -> {
            if (call == null) {
                return;
            }
            BaseReqEntity data = toReqEntityOfBase(s);
            call.accept(data != null && data.isSuccess());
        });
    }

    @Override
    public void reqApi(Request api) {
        addParam(api);
        super.reqApi(api);
    }

    @Override
    public void reqApi(Request api, String mediaType, String fileKey, File file) {
        addParam(api);
        super.reqApi(api, mediaType, fileKey, file);
    }

    @Override
    public void reqApiOfGet(Request api) {
        addParam(api);
        super.reqApiOfGet(api);
    }

    private void addHeader() {
        if (mReq == null) {
            return;
        }
        mReq.enableEveryRequestClearHeader(false);
        // server??????????????? ??????header
        mReq.putHeader("device_id", Settings.System.getString(XueChineseApplication.application.getContentResolver(), Settings.Secure.ANDROID_ID));
        mReq.putHeader("platform", "android");
        mReq.putHeader("lang", Config.getLang());
        mReq.putHeader("version", Config.mVersion);
        if (!TextUtils.isEmpty(mUid)) {
            mReq.putHeader("uid", mUid);
        }
        if (!TextUtils.isEmpty(mToken)) {
            mReq.putHeader("token", mToken);
        }
    }

    private void addParam(Request api) {
        addHeader();

        // body params ???????????????????????????
        //?????????????????????uid
        if (!TextUtils.isEmpty(mUid)) {
            api.addParam("uid", mUid);
        }
        //??????????????????
        if (!TextUtils.isEmpty(mToken)) {
            api.addParam("token", mToken);
        }
        //?????????1?????????2??????
        // api.addParam("system", "1");
        //???????????????????????????????????????default
        // api.addParam("mark", Config.mUpdatedChannel);
        //?????????????????????X.X.X
        // api.addParam("version", Config.mVersion);
        //???????????????ko????????????
        api.addParam("lang", Config.getLang());
    }

    @Override
    public void onResult(int id, Object data) {
        if (id == DOMConstant.LOG_OFF) {
            mToken = null;
            mUid = null;
        }
    }
}
