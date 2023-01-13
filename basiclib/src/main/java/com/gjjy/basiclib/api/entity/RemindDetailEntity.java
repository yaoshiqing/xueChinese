package com.gjjy.basiclib.api.entity;

import androidx.annotation.NonNull;

import com.ybear.ybutils.utils.ObjUtils;

/**
 * 获取所有分类
 */
public class RemindDetailEntity extends BaseReqEntity {
    private int remindSwitch;
    private int remindSun;      //星期天
    private int remindMon;      //星期一
    private int remindTue;      //星期二
    private int remindWed;      //星期三
    private int remindThu;      //星期四
    private int remindFri;      //星期五
    private int remindSat;      //星期六
    private String remindTime;  //提醒时间(时分),格式:09:30

    public RemindDetailEntity() {}

    @NonNull
    @Override
    public String toString() {
        return "RemindDetailEntity{" +
                "remindSwitch=" + isRemindSwitch() +
                ", remindSun=" + isRemindSun() +
                ", remindMon=" + isRemindMon() +
                ", remindTue=" + isRemindTue() +
                ", remindWed=" + isRemindWed() +
                ", remindThu=" + isRemindThu() +
                ", remindFri=" + isRemindFri() +
                ", remindSat=" + isRemindSat() +
                ", remindTime='" + remindTime + '\'' +
                '}';
    }

    public boolean isRemindSwitch() { return remindSwitch == 1; }
    public void setRemindSwitch(int remindSwitch) { this.remindSwitch = remindSwitch; }

    public boolean isRemindSun() { return remindSun == 1; }
    public void setRemindSun(int remindSun) { this.remindSun = remindSun; }

    public boolean isRemindMon() { return remindMon == 1; }
    public void setRemindMon(int remindMon) { this.remindMon = remindMon; }

    public boolean isRemindTue() { return remindTue == 1; }
    public void setRemindTue(int remindTue) { this.remindTue = remindTue; }

    public boolean isRemindWed() { return remindWed == 1; }
    public void setRemindWed(int remindWed) { this.remindWed = remindWed; }

    public boolean isRemindThu() { return remindThu == 1; }
    public void setRemindThu(int remindThu) { this.remindThu = remindThu; }

    public boolean isRemindFri() { return remindFri == 1; }
    public void setRemindFri(int remindFri) { this.remindFri = remindFri; }

    public boolean isRemindSat() { return remindSat == 1; }
    public void setRemindSat(int remindSat) { this.remindSat = remindSat; }

    public boolean[] getRemindWeeks() {
        return new boolean[] {
                isRemindSun(), isRemindMon(), isRemindTue(),
                isRemindWed(), isRemindThu(), isRemindFri(),
                isRemindSat()
        };
    }

    public String getRemindTime() { return remindTime; }
    public int getRemindHour() { return ObjUtils.parseInt( remindTime.split(":")[ 0 ] ); }
    public int getRemindMinute() { return ObjUtils.parseInt( remindTime.split(":")[ 1 ] ); }
    public void setRemindTime(String remindTime) { this.remindTime = remindTime; }
    public void setRemindTime(String hour, String minute) {
        setRemindTime( hour + ":" + minute );
    }
}
