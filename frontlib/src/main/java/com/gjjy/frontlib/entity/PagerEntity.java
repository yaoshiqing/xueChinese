package com.gjjy.frontlib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.gjjy.frontlib.annotations.OptionsLayout;
import com.gjjy.frontlib.annotations.PagerType;

import java.util.Arrays;

/**
 * 答题页面实体类
 */
public class PagerEntity extends BaseEntity implements Parcelable {
    private String[] keyword;                   //关键字
    private String title;                       //标题
    private String question;                    //问题
    private int questionTypeId;                 //问题类型Id
    private String audio;                       //音频链接
    private String audioUrl;                    //解释链接
    private String video;                       //视频链接
    @PagerType
    private int pagerType;                      //题目类型
    @OptionsLayout
    private int optLayout = OptionsLayout.LINEAR;   //选项的布局
    private OptionsEntity[] opts;                   //选项
    private String answerId;                    //答案id(选项id)
    private String explain;                     //解释

    public PagerEntity() {}


    protected PagerEntity(Parcel in) {
        keyword = in.createStringArray();
        title = in.readString();
        question = in.readString();
        questionTypeId = in.readInt();
        audio = in.readString();
        audioUrl = in.readString();
        video = in.readString();
        pagerType = in.readInt();
        optLayout = in.readInt();
        opts = in.createTypedArray( OptionsEntity.CREATOR);
        answerId = in.readString();
        explain = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(keyword);
        dest.writeString(title);
        dest.writeString(question);
        dest.writeInt(questionTypeId);
        dest.writeString(audio);
        dest.writeString(audioUrl);
        dest.writeString(video);
        dest.writeInt(pagerType);
        dest.writeInt(optLayout);
        dest.writeTypedArray(opts, flags);
        dest.writeString(answerId);
        dest.writeString(explain);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<PagerEntity> CREATOR = new Creator<PagerEntity>() {
        @Override
        public PagerEntity createFromParcel(Parcel in) {
            return new PagerEntity(in);
        }

        @Override
        public PagerEntity[] newArray(int size) {
            return new PagerEntity[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "PagerEntity{" +
                "keyword='" + Arrays.toString(keyword) + '\'' +
                ", title='" + title + '\'' +
                ", question='" + question + '\'' +
                ", questionTypeId='" + questionTypeId + '\'' +
                ", audio='" + audio + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", video='" + video + '\'' +
                ", pagerType=" + pagerType +
                ", optLayout=" + optLayout +
                ", opts=" + Arrays.toString(opts) +
                ", answerId='" + answerId + '\'' +
                ", explain='" + explain + '\'' +
                '}';
    }

    public String[] getKeyword() { return keyword; }
    public void setKeyword(String[] keyword) { this.keyword = keyword; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public int getQuestionTypeId() { return questionTypeId; }
    public void setQuestionTypeId(int questionTypeId) { this.questionTypeId = questionTypeId; }

    public String getAudio() { return audio; }
    public void setAudio(String audio) { this.audio = audio; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String voiceUrl) { this.audioUrl = voiceUrl; }

    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }

    @PagerType
    public int getPagerType() { return pagerType; }
    public void setPagerType(@PagerType int type) { this.pagerType = type; }

    public int getOptLayout() { return optLayout; }
    public void setOptLayout(@OptionsLayout int optLayout) { this.optLayout = optLayout; }

    public OptionsEntity[] getOpts() { return opts; }
    public void setOpts(OptionsEntity[] opts) { this.opts = opts; }

    public String getAnswerId() { return answerId; }
    public int getAnswerIdOfInt() {
        try {
            return Integer.parseInt( answerId );
        } catch (NumberFormatException ignored) { }
        return -1;
    }
    public void setAnswerId(String answerId) { this.answerId = answerId; }
    public void setAnswerId(int answerId) { this.answerId = String.valueOf( answerId ); }

    public String getExplain() { return explain; }
    public void setExplain(String explain) { this.explain = explain; }
}