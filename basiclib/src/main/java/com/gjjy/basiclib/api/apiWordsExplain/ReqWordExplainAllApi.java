package com.gjjy.basiclib.api.apiWordsExplain;

import com.gjjy.basiclib.api.BaseWordsExplainServer;

/**
 * 获取指定模块所有词语
 */
public class ReqWordExplainAllApi extends BaseWordsExplainServer {
    @Override
    public String api() { return "getAllWordExplain"; }
}
