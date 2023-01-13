package com.gjjy.frontlib.mvp.model;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.api.entity.WordsExplainEntity;
import com.gjjy.basiclib.mvp.model.ReqWordsExplainModel;
import com.gjjy.frontlib.adapter.WordsListChildAdapter;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class WordsUnitModel extends MvpModel {
    private final SparseArray<List<WordsExplainEntity>> mWordsExplainList = new SparseArray<>();

    @Model
    private ReqWordsExplainModel mReqWordsModel;

    public void setUid(String uid, String token) {
        mReqWordsModel.setUid( uid, token );
    }

    public void searchWordsExplain(String kw, Consumer<List<WordsExplainEntity>> call) {
        mReqWordsModel.reqSearchWordsExplain( kw, call );
    }

    public void getWordsExplainAll(int unitId,
                                   boolean isCache,
                                   Consumer<List<WordsExplainEntity>> call) {
        int index = mWordsExplainList.indexOfKey( unitId );
        if( isCache && index >= 0 ) {
            if( call != null ) call.accept( mWordsExplainList.valueAt( index ) );
            return;
        }
        mReqWordsModel.reqWordsExplainAll(unitId, list -> {
            mWordsExplainList.put( unitId, list );
            if( call != null ) call.accept( list );
        });
    }

    @NonNull
    public List<WordsListChildAdapter.ItemData> toWordsListChildDataList(
            List<WordsExplainEntity> list,
            Consumer<WordsListChildAdapter.ItemData> call) {
        LogUtil.e("toWordsListChildDataList -> list:" + ( list == null ? null : list.size() ) );

        List<WordsListChildAdapter.ItemData> childList = new ArrayList<>();
        if( list == null ) return childList;
        String lang = Config.getLang();
        for( WordsExplainEntity data : list ) {
            WordsListChildAdapter.ItemData item = new WordsListChildAdapter.ItemData();
            item.setPinYin( data.getPinyin() );
            item.setChinese( data.getWord() );
            item.setWordsUrl( data.getAudioUrl() );
            item.setExplain( "ko".equals( lang ) ? data.getKorean() : data.getEnglish() );
            if( call != null ) call.accept( item );
            childList.add( item );
        }
        return childList;
    }
}
