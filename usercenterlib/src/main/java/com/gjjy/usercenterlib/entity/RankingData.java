package com.gjjy.usercenterlib.entity;

import androidx.annotation.NonNull;

import com.gjjy.usercenterlib.adapter.RankingAdapter;

import java.util.List;

public class RankingData {
    private int currentIndex;
    private int lastIndex;
    private List<RankingAdapter.ItemData> data;

    @NonNull
    @Override
    public String toString() {
        return "RankingData{" +
                "currentIndex=" + currentIndex +
                ", lastIndex=" + lastIndex +
                ", data=" + data +
                '}';
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public RankingData setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public RankingData setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
        return this;
    }

    public List<RankingAdapter.ItemData> getData() {
        return data;
    }

    public RankingData setData(List<RankingAdapter.ItemData> data) {
        this.data = data;
        return this;
    }
}
