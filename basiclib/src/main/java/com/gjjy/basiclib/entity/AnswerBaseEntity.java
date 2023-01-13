package com.gjjy.basiclib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AnswerBaseEntity implements Parcelable {
    private int categoryId = -1;
    private String categoryName;
    private int levelId = -1;
    private String levelName;
    private int levelStatus;
    private int unitId = -1;
    private String unitName;
    private int unitStatus;
    private int score;
    private boolean isRecordRecord;

    public AnswerBaseEntity() { }

    protected AnswerBaseEntity(Parcel in) {
        categoryId = in.readInt();
        categoryName = in.readString();
        levelId = in.readInt();
        levelName = in.readString();
        levelStatus = in.readInt();
        unitId = in.readInt();
        unitName = in.readString();
        unitStatus = in.readInt();
        score = in.readInt();
        isRecordRecord = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt( categoryId );
        dest.writeString( categoryName );
        dest.writeInt( levelId );
        dest.writeString( levelName );
        dest.writeInt( levelStatus );
        dest.writeInt( unitId );
        dest.writeString( unitName );
        dest.writeInt( unitStatus );
        dest.writeInt( score );
        dest.writeByte( (byte) ( isRecordRecord ? 1 : 0 ) );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AnswerBaseEntity> CREATOR = new Creator<AnswerBaseEntity>(){
        @Override
        public AnswerBaseEntity createFromParcel(Parcel in) {
            return new AnswerBaseEntity( in );
        }

        @Override
        public AnswerBaseEntity[] newArray(int size) {
            return new AnswerBaseEntity[ size ];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "AnswerBaseEntity{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", levelId=" + levelId +
                ", levelName='" + levelName + '\'' +
                ", levelStatus=" + levelStatus +
                ", unitId=" + unitId +
                ", unitName='" + unitName + '\'' +
                ", unitStatus=" + unitStatus +
                ", score=" + score +
                ", isRecordRecord=" + isRecordRecord +
                '}';
    }

    public int getCategoryId() {
        return categoryId;
    }

    public AnswerBaseEntity setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public AnswerBaseEntity setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public int getLevelId() {
        return levelId;
    }

    public AnswerBaseEntity setLevelId(int levelId) {
        this.levelId = levelId;
        return this;
    }

    public String getLevelName() {
        return levelName;
    }

    public AnswerBaseEntity setLevelName(String levelName) {
        this.levelName = levelName;
        return this;
    }

    public int getLevelStatus() {
        return levelStatus;
    }

    public AnswerBaseEntity setLevelStatus(int levelStatus) {
        this.levelStatus = levelStatus;
        return this;
    }

    public int getUnitId() {
        return unitId;
    }

    public AnswerBaseEntity setUnitId(int unitId) {
        this.unitId = unitId;
        return this;
    }

    public String getUnitName() {
        return unitName;
    }

    public AnswerBaseEntity setUnitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    public int getUnitStatus() {
        return unitStatus;
    }

    public AnswerBaseEntity setUnitStatus(int unitStatus) {
        this.unitStatus = unitStatus;
        return this;
    }

    public int getScore() {
        return score;
    }

    public AnswerBaseEntity setScore(int score) {
        this.score = score;
        return this;
    }

    public boolean isRecordRecord() {
        return isRecordRecord;
    }

    public void setRecordRecord(boolean recordRecord) {
        isRecordRecord = recordRecord;
    }
}
