package com.gjjy.frontlib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.ybear.ybcomponent.base.adapter.IItemData;
import com.gjjy.frontlib.annotations.OptionsType;

import java.util.Arrays;
import java.util.List;

/**
 * 答题选项实体类
 */
public class OptionsEntity extends BaseEntity implements Parcelable, IItemData {
    private String question;        //问题
    private String[] pinyin;        //拼音
    private String[] data;          //数据（可能会出现内容拆分的情况，所以是数组）
    private String imgUrl;          //图片链接
    @DrawableRes
    private int imgRes;             //本地图片资源
    private String audioUrl;        //音频链接
    private int tag;                //可选值（匹配题会用到）
    @OptionsType
    private int optType;            //选项类型

    public OptionsEntity() {}
    public OptionsEntity(OptionsEntity data) {
        question = data.getQuestion();
        pinyin = data.getPinyin();
        this.data = data.getData();
        imgUrl = data.getImgUrl();
        imgRes = data.getImgRes();
        audioUrl = data.getAudioUrl();
        tag = data.getTag();
        optType = data.getOptType();
    }
    protected OptionsEntity(Parcel in) {
        question = in.readString();
        pinyin = in.createStringArray();
        data = in.createStringArray();
        imgUrl = in.readString();
        imgRes = in.readInt();
        audioUrl = in.readString();
        tag = in.readInt();
        optType = in.readInt();
    }

    public static final Creator<OptionsEntity> CREATOR = new Creator<OptionsEntity>() {
        @Override
        public OptionsEntity createFromParcel(Parcel in) { return new OptionsEntity(in); }
        @Override
        public OptionsEntity[] newArray(int size) { return new OptionsEntity[size]; }
    };
    @Override
    public int describeContents() { return 0; }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringArray(pinyin);
        dest.writeStringArray(data);
        dest.writeString(imgUrl);
        dest.writeInt(imgRes);
        dest.writeString(audioUrl);
        dest.writeInt(tag);
        dest.writeInt(optType);
    }

    @NonNull
    @Override
    public String toString() {
        return "OptionsEntity{" +
                "question=" + question +
                ", pinyin=" + Arrays.toString(pinyin) +
                ", data=" + Arrays.toString(data) +
                ", imgUrl='" + imgUrl + '\'' +
                ", imgRes=" + imgRes +
                ", audioUrl='" + audioUrl + '\'' +
                ", tag=" + tag +
                ", optType=" + optType +
                '}';
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getPinyinString() {
        if( pinyin == null ) return "";
        StringBuilder ret = new StringBuilder();
        for( String s : pinyin ) ret.append( s ).append(" ");
        return ret.toString();
    }
    public String[] getPinyin() { return pinyin; }
    public int getPinyinLength() { return pinyin == null ? 1 : pinyin.length; }
    public OptionsEntity setPinyin(String[] pinyin) {
        this.pinyin = pinyin;
        return this;
    }
    public OptionsEntity setPinyin(List<String> pinyin) {
        if( pinyin == null || pinyin.size() == 0 ) return this;
        return setPinyin( pinyin.toArray( new String[0] ) );
    }

    public String getDataString() {
        StringBuilder ret = new StringBuilder();
        for( String s : data ) ret.append( s );
        return ret.toString();
    }

    public String[] getData() { return data; }
    public OptionsEntity setData(String[] data) {
        this.data = data;
        return this;
    }
    public OptionsEntity setData(List<String> data) {
        if( data == null || data.size() == 0 ) return this;
        return setData( data.toArray( new String[0] ) );
    }
    public OptionsEntity setData(String data) {
        if( data == null ) return this;
        String[] arr = new String[ data.length() ];
        for (int i = 0; i < arr.length; i++) arr[ i ] = String.valueOf( data.charAt( i ) );
        return setData( arr );
    }

    public String getImgUrl() { return imgUrl; }
    public OptionsEntity setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    @DrawableRes
    public int getImgRes() { return imgRes; }
    public OptionsEntity setImgRes(@DrawableRes int res) {
        imgRes = res;
        return this;
    }

    public String getAudioUrl() { return audioUrl; }
    public OptionsEntity setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
        return this;
    }

    public int getTag() { return tag; }
    public OptionsEntity setTag(int tag) {
        this.tag = tag;
        return this;
    }

    public int getOptType() { return optType == OptionsType.NONE ? OptionsType.CENTER_TEXT : optType; }
    public OptionsEntity setOptType(@OptionsType int optType) {
        this.optType = optType;
        return this;
    }
}