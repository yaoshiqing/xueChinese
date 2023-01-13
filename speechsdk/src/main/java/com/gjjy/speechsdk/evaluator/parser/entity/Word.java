package com.gjjy.speechsdk.evaluator.parser.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Word{
	/**
	 * 开始帧位置，每帧相当于10ms
	 */
	private int begPos;
	/**
	 * 结束帧位置
	 */
	private int endPos;
	/**
	 * 单词内容
	 */
	private String content;
	/**
	 * 增漏读信息：0（正确），16（漏读），32（增读），64（回读），128（替换）
	 */
	private int dpMessage;
	/**
	 * 单词在全篇索引（en）
	 */
	private int globalIndex;
	/**
	 * 单词在句子中的索引（en）
	 */
	private int index;
	/**
	 * 拼音（cn），数字代表声调，5表示轻声，如fen1
	 */
	private String symbol;
	/**
	 * 时长（单位：帧，每帧相当于10ms）（cn）
	 */
	private int timeLen;
	/**
	 * 单词得分（en）
	 */
	private float totalScore;
	/**
	 * Words包含的Syll
	 */
	private ArrayList<Syll> sylls;

	@NonNull
	@Override
	public String toString() {
		return "Word{" +
				"begPos=" + begPos +
				", endPos=" + endPos +
				", content='" + content + '\'' +
				", dpMessage=" + dpMessage +
				", globalIndex=" + globalIndex +
				", index=" + index +
				", symbol='" + symbol + '\'' +
				", timeLen=" + timeLen +
				", totalScore=" + totalScore +
				", sylls=" + Arrays.toString( sylls.toArray(new Syll[0]) ) +
				'}';
	}

	public int getBegPos() { return begPos; }
	public Word setBegPos(int begPos) {
		this.begPos = begPos;
		return this;
	}

	public int getEndPos() { return endPos; }
	public Word setEndPos(int endPos) {
		this.endPos = endPos;
		return this;
	}

	public String getContent() { return content; }
	public Word setContent(String content) {
		this.content = content;
		return this;
	}

	public int getDpMessage() { return dpMessage; }
	public Word setDpMessage(int dpMessage) {
		this.dpMessage = dpMessage;
		return this;
	}

	public int getGlobalIndex() { return globalIndex; }
	public Word setGlobalIndex(int globalIndex) {
		this.globalIndex = globalIndex;
		return this;
	}

	public int getIndex() { return index; }
	public Word setIndex(int index) {
		this.index = index;
		return this;
	}

	public String getSymbol() { return symbol; }
	public Word setSymbol(String symbol) {
		this.symbol = symbol;
		return this;
	}

	public int getTimeLen() { return timeLen; }
	public Word setTimeLen(int timeLen) {
		this.timeLen = timeLen;
		return this;
	}

	public float getTotalScore() { return totalScore; }
	public Word setTotalScore(float totalScore) {
		this.totalScore = totalScore;
		return this;
	}

	public ArrayList<Syll> getSylls() { return sylls; }
	public Word setSylls(ArrayList<Syll> sylls) {
		this.sylls = sylls;
		return this;
	}
}
