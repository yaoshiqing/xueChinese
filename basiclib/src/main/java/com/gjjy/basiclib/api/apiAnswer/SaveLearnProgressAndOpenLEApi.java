package com.gjjy.basiclib.api.apiAnswer;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 保存开始学习进度并领取闪电和经验值
 */
public class SaveLearnProgressAndOpenLEApi extends BaseApiServer {
    @Override
    public String api() { return "study-home/user_unit/saveLearnProgressAndOpenLE"; }
}
