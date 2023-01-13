package com.gjjy.basiclib.mvp.model;

import android.text.TextUtils;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.apiWordsExplain.ReqSearchWordsExplainApi;
import com.gjjy.basiclib.api.apiWordsExplain.ReqWordExplainAllApi;
import com.gjjy.basiclib.api.entity.WordsExplainEntity;
import com.ybear.ybnetworkutil.request.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * 词语表
 */
public class ReqWordsExplainModel extends BasicGlobalReqModel{
    /**
     * 搜索词语表
     * @param keyword       关键词
     * @param call          请求结果
     */
    public void reqSearchWordsExplain(String keyword, Consumer<List<WordsExplainEntity>> call) {
        if( TextUtils.isEmpty( keyword ) ) {
            if( call != null ) call.accept( new ArrayList<>() );
            return;
        }
        ReqSearchWordsExplainApi api = new ReqSearchWordsExplainApi();
        api.addParam( "keyword", keyword );
        callWordsExplainList(  api, call );
        reqApi( api );
    }

    /**
     * 获取指定模块所有词语
     * @param unitId    	模块id
     * @param call          请求结果
     */
    public void reqWordsExplainAll(int unitId, Consumer<List<WordsExplainEntity>> call) {
        ReqWordExplainAllApi api = new ReqWordExplainAllApi();
        api.addParam( "unit_id", unitId );
        callWordsExplainList(  api, call );
        reqApi( api );
    }

    private void callWordsExplainList(Request api, Consumer<List<WordsExplainEntity>> call) {
        api.setCallbackString((s, isResponse) -> {
            if( call != null ) call.accept( toReqEntityOfList( s,  WordsExplainEntity.class) );
        });
    }
}
