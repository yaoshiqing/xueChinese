/**
 * 
 */
package com.gjjy.speechsdk.evaluator.parser.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Syll {
	/**
	 * 开始帧位置，每帧相当于10ms
	 */
	private int begPos;
	/**
	 * 结束帧位置
	 */
	private int endPos;
	/**
	 * 音节内容
	 */
	private String content;
	/**
	 * 拼音（cn），数字代表声调，5表示轻声，如fen1
	 */
	private String symbol;
	/**
	 * 增漏读信息：0（正确），16（漏读），32（增读），64（回读），128（替换）
	 */
	private int dpMessage;
	/**
	 * 时长（单位：帧，每帧相当于10ms）（cn）
	 */
	private int timeLen;
	/**
	 * Syll包含的音节
	 */
	private ArrayList<Phone> phones;
	
	/**
	 * 获取音节的标准音标（en）
	 * 
	 * @return 标准音标
	 */
	public String getStdSymbol() {
		StringBuilder sb = new StringBuilder();
		String[] symbols = content.split(" ");
		for (String s : symbols) sb.append( Phone.getStdSymbol(s) );
		return sb.toString();
	}

	@NonNull
	@Override
	public String toString() {
		return "Syll{" +
				"begPos=" + begPos +
				", endPos=" + endPos +
				", content='" + content + '\'' +
				", symbol='" + symbol + '\'' +
				", dpMessage=" + dpMessage +
				", timeLen=" + timeLen +
				", phones=" + Arrays.toString( phones.toArray(new Phone[0]) ) +
				'}';
	}

	public int getBegPos() { return begPos; }
	public Syll setBegPos(int begPos) {
		this.begPos = begPos;
		return this;
	}

	public int getEndPos() { return endPos; }
	public Syll setEndPos(int endPos) {
		this.endPos = endPos;
		return this;
	}

	public String getContent() { return TextUtils.isEmpty( content ) ? "" : content; }
	public Syll setContent(String content) {
		this.content = content;
		return this;
	}

	public String getSymbol() { return symbol; }
	public Syll setSymbol(String symbol) {
		this.symbol = symbol;
		return this;
	}

	public int getDpMessage() { return dpMessage; }
	public Syll setDpMessage(int dpMessage) {
		this.dpMessage = dpMessage;
		return this;
	}

	public int getTimeLen() { return timeLen; }
	public Syll setTimeLen(int timeLen) {
		this.timeLen = timeLen;
		return this;
	}

	public ArrayList<Phone> getPhones() { return phones; }
	public Syll setPhones(ArrayList<Phone> phones) {
		this.phones = phones;
		return this;
	}
}
