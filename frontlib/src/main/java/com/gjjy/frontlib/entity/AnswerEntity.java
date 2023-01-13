package com.gjjy.frontlib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * 题目实体类
 */
public class AnswerEntity extends BaseEntity implements Parcelable {

    private List<PagerEntity> content;      //题目列表
//    private int nodeErrorMaxCount = 2;      //小节最大错误数量
//    private int currentNode;                //当前小节
//    private int nodeCount = 5;              //小节数量
//    private boolean isTestComplete;         //是否完成测试

    private int questionId;                 //题目id
    private String keyword;                 //关键词
    private int questionTypeId;             //题目类型id
    private int optionTypeId;               //选择题选项类型id

    public  AnswerEntity() {}
    protected AnswerEntity(Parcel in) {
        content = in.createTypedArrayList(PagerEntity.CREATOR);
        questionId = in.readInt();
        keyword = in.readString();
        questionTypeId = in.readInt();
        optionTypeId = in.readInt();
//        nodeErrorMaxCount = in.readInt();
//        currentNode = in.readInt();
//        nodeCount = in.readInt();
//        isTestComplete = in.readByte() != 0;
    }

    public static final Creator<AnswerEntity> CREATOR = new Creator<AnswerEntity>() {
        @Override
        public AnswerEntity createFromParcel(Parcel in) { return new AnswerEntity(in); }
        @Override
        public AnswerEntity[] newArray(int size) { return new AnswerEntity[size]; }
    };
    @Override
    public int describeContents() { return 0; }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(content);
        dest.writeInt(questionId);
        dest.writeString(keyword);
        dest.writeInt(questionTypeId);
        dest.writeInt(optionTypeId);
//        dest.writeInt(nodeErrorMaxCount);
//        dest.writeInt(currentNode);
//        dest.writeInt(nodeCount);
//        dest.writeByte((byte) (isTestComplete ? 1 : 0));
    }

//    @NonNull
//    @Override
//    public String toString() {
//        return "AnswerEntity{" +
//                "id=" + getId() +
//                ", dataList=" + dataList +
////                ", nodeErrorMaxCount=" + nodeErrorMaxCount +
////                ", currentNode=" + currentNode +
////                ", nodeCount=" + nodeCount +
////                ", isTestComplete=" + isTestComplete +
//                ", hash='" + getHash() + '\'' +
//                '}';
//    }

    @NonNull
    @Override
    public String toString() {
        return "AnswerEntity{" +
                "content=" + ( content != null ? Arrays.toString( content.toArray() ) : null ) +
                ", questionId=" + questionId +
                ", keyword='" + keyword + '\'' +
                ", questionTypeId=" + questionTypeId +
                ", optionTypeId=" + optionTypeId +
                '}';
    }

    public List<PagerEntity> getContent() { return content; }
    public void setDataList(List<PagerEntity> dataList) { this.content = dataList; }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(int questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public int getOptionTypeId() {
        return optionTypeId;
    }

    public void setOptionTypeId(int optionTypeId) {
        this.optionTypeId = optionTypeId;
    }

    public static Creator<AnswerEntity> getCREATOR() {
        return CREATOR;
    }

    //    public int getNodeErrorMaxCount() { return nodeErrorMaxCount; }
//    public void setNodeErrorMaxCount(int count) { nodeErrorMaxCount = count; }
//
//    public int getCurrentNode() { return currentNode; }
//    public void setCurrentNode(int count) { currentNode = count; }
//
//    public int getNodeCount() { return nodeCount; }
//    public void setNodeCount(int count) { nodeCount = count; }
//
//    public boolean isTestComplete() { return isTestComplete; }
//    public void setTestComplete(boolean testComplete) { isTestComplete = testComplete; }
}