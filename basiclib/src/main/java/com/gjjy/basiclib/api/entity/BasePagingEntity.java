package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 分页
 */
public class BasePagingEntity<T> {
    private int total;                          //当前查询结果总数
    private int perPage;                        //每页显示数量
    private int currentPage;                    //当前页
    private int lastPage;                       //当前查询结果总数
    private List<T> data;

    @NonNull
    @Override
    public String toString() {
        return getClass().getName() +
                "{total=" + total +
                ", perPage=" + perPage +
                ", currentPage=" + currentPage +
                ", lastPage=" + lastPage +
                ", data=" + ( data != null ? Arrays.toString( data.toArray() ) : "" ) +
                '}';
    }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getPerPage() { return perPage; }
    public void setPerPage(int perPage) { this.perPage = perPage; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getLastPage() { return lastPage; }
    public void setLastPage(int lastPage) { this.lastPage = lastPage; }

    public List<T> getData() {
        return data == null ? new ArrayList<>() : data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
