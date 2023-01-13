package com.gjjy.basiclib.api.apiPopupNotice;

import com.gjjy.basiclib.api.BaseApiServer;

/**
 * 检查公告信息
 */
public class PopupNoticeApi extends BaseApiServer {
    @Override
    public String url() { return super.url() + "/popup_notice/"; }

    @Override
    public String api() { return "detail"; }
}
