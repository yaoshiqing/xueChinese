package com.gjjy.basiclib.mvp.model;

import androidx.core.util.Consumer;

import com.gjjy.basiclib.api.apiFrontInfo.AllCategoryApi;
import com.gjjy.basiclib.api.apiFrontInfo.CategoryContentApi;
import com.gjjy.basiclib.api.entity.CategoryAllEntity;
import com.gjjy.basiclib.api.entity.CategoryContentEntity;

import java.util.List;

/**
 * 主页信息
 */
public class ReqFrontInfoModel extends BasicGlobalReqModel{
    /**
     * 获取所有分类
     * @param call          请求结果
     */
    public void reqAllCategory(Consumer<List<CategoryAllEntity>> call) {
        AllCategoryApi api = new AllCategoryApi();
        api.setCallbackString((s, isResponse) -> {
            if( call != null ) call.accept( toReqEntityOfList(s, CategoryAllEntity.class ) );
        });
        reqApi( api );
    }

    /**
     * 获取分类下的内容
     * @param categoryId    分类id
     * @param call          请求结果
     */
    public void reqCategoryContent(int categoryId, Consumer<List<CategoryContentEntity>> call) {
        CategoryContentApi api = new CategoryContentApi();
        api.addParam( "category_id", categoryId );
        api.setCallbackString((s, isResponse) -> {
            if( call != null ) call.accept( toReqEntityOfList(s, CategoryContentEntity.class ) );
        });
        reqApi( api );
    }
}
