package com.gjjy.speechsdk.evaluator.parser.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Sentence {
	/**
	 * 开始帧位置，每帧相当于10ms
	 */
	private int begPos;
	/**
	 * 结束帧位置
	 */
	private int endPos;
	/**
	 * 句子内容
	 */
	private String content;
	/**
	 * 总得分
	 */
	private float totalScore;
	/**
	 * 时长（单位：帧，每帧相当于10ms）（cn）
	 */
	private int timeLen;
	/**
	 * 句子的索引（en）
	 */
	private int index;
	/**
	 * 单词数（en）
	 */
	private int wordCount;
	/**
	 * sentence包括的word
	 */
	private ArrayList<Word> words;

	@NonNull
	@Override
	public String toString() {
		return "Sentence{" +
				"begPos=" + begPos +
				", endPos=" + endPos +
				", content='" + content + '\'' +
				", totalScore=" + totalScore +
				", timeLen=" + timeLen +
				", index=" + index +
				", wordCount=" + wordCount +
				", words=" + Arrays.toString( words.toArray(new Word[0]) ) +
				'}';
	}

	public int getBegPos() { return begPos; }
	public Sentence setBegPos(int begPos) {
		this.begPos = begPos;
		return this;
	}

	public int getEndPos() { return endPos; }
	public Sentence setEndPos(int endPos) {
		this.endPos = endPos;
		return this;
	}

	public String getContent() { return content; }
	public Sentence setContent(String content) {
		this.content = content;
		return this;
	}

	public float getTotalScore() { return totalScore; }
	public Sentence setTotalScore(float totalScore) {
		this.totalScore = totalScore;
		return this;
	}

	public int getTimeLen() { return timeLen; }
	public Sentence setTimeLen(int timeLen) {
		this.timeLen = timeLen;
		return this;
	}

	public int getIndex() { return index; }
	public Sentence setIndex(int index) {
		this.index = index;
		return this;
	}

	public int getWordCount() { return wordCount; }
	public Sentence setWordCount(int wordCount) {
		this.wordCount = wordCount;
		return this;
	}

	public ArrayList<Word> getWords() { return words; }
	public Sentence setWords(ArrayList<Word> words) {
		this.words = words;
		return this;
	}
}
