package com.gjjy.googlebillinglib.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 Sku详细信息
 */
public class SkuList extends ResultEntity {
    private List<SkuDetails> skuDetailsList;

    public SkuList() { }

    public SkuList(BillingResult result, List<SkuDetails> skuDetailsList) {
        super( result );
        this.skuDetailsList = skuDetailsList;
    }

    @NonNull
    @Override
    public String toString() {
        return "SkuList{" +
                "code=" + getCode() +
                ", debugMessage='" + getDebugMessage() + '\'' +
                ", skuDetailsList=" + toSkuDetailsListString() +
                '}';
    }

    @NonNull
    public String  toSkuDetailsListString() {
        return isEmptyList() ? "" : Arrays.toString( skuDetailsList.toArray( new SkuDetails[ 0 ] ) );
    }

    public boolean isEmptyList() {
        return skuDetailsList == null || skuDetailsList.size() == 0;
    }

    @NonNull
    public List<SkuDetails> getSkuDetailsList(String... sku) {
        if( skuDetailsList == null ) return new ArrayList<>();
        if( sku == null || sku.length == 0 ) return skuDetailsList;

        List<SkuDetails> retList = new ArrayList<>();
        for( String s : sku ) {
            if( TextUtils.isEmpty( s ) ) continue;
            for( SkuDetails item : skuDetailsList ) {
                if( item == null || !s.equals( item.getSku() ) ) continue;
                retList.add( item );
                break;
            }
        }
        return retList;
    }

    @Nullable
    public SkuDetails getSkuDetails(String sku) {
        List<SkuDetails> list = getSkuDetailsList( sku );
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    public SkuList setSkuDetailsList(List<SkuDetails> skuDetailsList) {
        this.skuDetailsList = skuDetailsList;
        return this;
    }

    public void sort(String[] sku) {
        if( sku == null || sku.length != skuDetailsList.size() ) return;
        List<SkuDetails> newList = new ArrayList<>( skuDetailsList );
        skuDetailsList.clear();
        for( String s : sku ) {
            for( SkuDetails skuDetail : newList ) {
                if( !s.equals( skuDetail.getSku() ) ) continue;
                skuDetailsList.add( skuDetail );
                break;
            }
        }
    }
}
