package com.gjjy.speechsdk.evaluator.parser.result;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gjjy.speechsdk.evaluator.parser.entity.Sentence;
import com.gjjy.speechsdk.evaluator.parser.entity.Syll;
import com.gjjy.speechsdk.evaluator.parser.entity.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Result {
	/**
	 * 评测语种：en（英文）、cn（中文）
	 */
	private String language;
	/**
	 * 评测种类：read_syllable（cn单字）、read_word（词语）、read_sentence（句子） 
	 */
	private String category;
	/**
	 * 开始帧位置，每帧相当于10ms
	 */
	private int begPos;
	/**
	 * 结束帧位置
	 */
	private int endPos;
	/**
	 * 评测内容
	 */
	private String content;
	/**
	 * 总得分
	 */
	private float totalScore;
	/**
	 * 时长（cn）
	 */
	private int timeLen;
	/**
	 * 异常信息（en）
	 */
	private String exceptInfo;
	/**
	 * 是否乱读（cn）
	 */
	private boolean isRejected;
	/**
	 * xml结果中的sentence标签
	 */
	private ArrayList<Sentence> sentences;

	@NonNull
	public String toResultString() {
		return "Result{" +
				"language='" + language + '\'' +
				", category='" + category + '\'' +
				", begPos=" + begPos +
				", endPos=" + endPos +
				", content='" + content + '\'' +
				", totalScore=" + totalScore +
				", timeLen=" + timeLen +
				", exceptInfo='" + exceptInfo + '\'' +
				", isRejected=" + isRejected +
				", sentences=" + Arrays.toString( sentences.toArray(new Sentence[0]) ) +
				'}';
	}

	public List<Syll> toSyll(String content) {
		List<Syll> retList = new ArrayList<>();
		for( Sentence sentence : getSentences() ) {
			for( Word word : sentence.getWords() ) {
				for (Syll syll : word.getSylls()) {
					//可能会返回一些不属于文本的内容，例如：sil
					if( TextUtils.isEmpty( content ) || !content.contains( syll.getContent() ) ) {
						continue;
					}
					retList.add( syll );
				}
			}
		}
		return retList;
	}

	public String getLanguage() { return language; }
	public Result setLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getCategory() { return category; }
	public Result setCategory(String category) {
		this.category = category;
		return this;
	}

	public int getBegPos() { return begPos; }
	public Result setBegPos(int begPos) {
		this.begPos = begPos;
		return this;
	}

	public int getEndPos() { return endPos; }
	public Result setEndPos(int endPos) {
		this.endPos = endPos;
		return this;
	}

	public String getContent() { return content; }
	public Result setContent(String content) {
		this.content = content;
		return this;
	}

	public float getTotalScore() { return totalScore; }
	public Result setTotalScore(float totalScore) {
		this.totalScore = totalScore;
		return this;
	}

	public int getTimeLen() { return timeLen; }
	public Result setTimeLen(int timeLen) {
		this.timeLen = timeLen;
		return this;
	}

	public String getExceptInfo() { return exceptInfo; }
	public Result setExceptInfo(String exceptInfo) {
		this.exceptInfo = exceptInfo;
		return this;
	}

	public boolean isRejected() { return isRejected; }
	public Result setRejected(boolean rejected) {
		isRejected = rejected;
		return this;
	}

	public ArrayList<Sentence> getSentences() { return sentences; }
	public Result setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
		return this;
	}
}
