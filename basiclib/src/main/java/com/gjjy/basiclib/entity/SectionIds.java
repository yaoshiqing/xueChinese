package com.gjjy.basiclib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SectionIds implements Parcelable {
    private int sectionId;
    private String sectionName;

    public SectionIds(int sectionId, String sectionName) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
    }

    public SectionIds(int sectionId) {
        this.sectionId = sectionId;
    }

    protected SectionIds(Parcel in) {
        sectionId = in.readInt();
        sectionName = in.readString();
    }

    @NonNull
    @Override
    public String toString() {
        return "SectionIds{" +
                "sectionId=" + sectionId +
                ", sectionName='" + sectionName + '\'' +
                '}';
    }

    public static final Creator<SectionIds> CREATOR = new Creator<SectionIds>() {
        @Override
        public SectionIds createFromParcel(Parcel in) {
            return new SectionIds(in);
        }

        @Override
        public SectionIds[] newArray(int size) {
            return new SectionIds[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sectionId);
        dest.writeString(sectionName);
    }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
}
