package com.gjjy.basiclib.dao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class SkipAnswerTypeEntity{
    @Id(autoincrement = true)
    private Long id;
    private boolean hearing = true;
    private boolean voice = true;
    private boolean translate = true;
    private boolean sound = true;
    private boolean snail = false;

    @Generated(hash = 1976032185)
    public SkipAnswerTypeEntity(Long id, boolean hearing, boolean voice,
            boolean translate, boolean sound, boolean snail) {
        this.id = id;
        this.hearing = hearing;
        this.voice = voice;
        this.translate = translate;
        this.sound = sound;
        this.snail = snail;
    }

    @Generated(hash = 384382526)
    public SkipAnswerTypeEntity() {
    }

    public void init() {
        hearing = true;
        voice = true;
        translate = true;
        snail = false;
    }

    public boolean isHearing() { return hearing; }
    public void setHearing(boolean hearing) { this.hearing = hearing; }

    public boolean isVoice() { return voice; }
    public void setVoice(boolean voice) { this.voice = voice; }

    public boolean isTranslate() { return translate; }
    public void setTranslate(boolean translate) { this.translate = translate; }

    public boolean isSound() { return sound; }
    public void setSound(boolean sound) { this.sound = sound; }

    public boolean isSnail() { return snail; }
    public void setSnail(boolean snail) { this.snail = snail; }

    public boolean getHearing() { return this.hearing; }

    public boolean getVoice() { return this.voice; }

    public boolean getTranslate() { return this.translate; }

    public boolean getSound() { return this.sound; }

    public boolean getSnail() { return this.snail; }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}