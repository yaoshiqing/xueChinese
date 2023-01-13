package com.gjjy.speechsdk.evaluator.parser.entity;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class Phone {
	/**
	 * 讯飞音标-标准音标映射表（en）
	 */
	private static HashMap<String, String> phoneMap = new HashMap<>();
	
	static {
		phoneMap.put("aa", "ɑ:");
		phoneMap.put("oo", "ɔ");
		phoneMap.put("ae", "æ");
		phoneMap.put("ah", "ʌ");
		phoneMap.put("ao", "ɔ:");
		phoneMap.put("aw", "aʊ");
		phoneMap.put("ax", "ə");
		phoneMap.put("ay", "aɪ");
		phoneMap.put("eh", "e");
		phoneMap.put("er", "ə:");
		phoneMap.put("ey", "eɪ");
		phoneMap.put("ih", "ɪ");
		phoneMap.put("iy", "i:");
		phoneMap.put("ow", "əʊ");
		phoneMap.put("oy", "ɔɪ");
		phoneMap.put("uh", "ʊ");
		phoneMap.put("uw", "ʊ:");
		phoneMap.put("ch", "tʃ");
		phoneMap.put("dh", "ð");
		phoneMap.put("hh", "h");
		phoneMap.put("jh", "dʒ");
		phoneMap.put("ng", "ŋ");
		phoneMap.put("sh", "ʃ");
		phoneMap.put("th", "θ");
		phoneMap.put("zh", "ʒ");
		phoneMap.put("y", "j");
		phoneMap.put("d", "d");
		phoneMap.put("k", "k");
		phoneMap.put("l", "l");
		phoneMap.put("m", "m");
		phoneMap.put("n", "n");
		phoneMap.put("b", "b");
		phoneMap.put("f", "f");
		phoneMap.put("g", "g");
		phoneMap.put("p", "p");
		phoneMap.put("r", "r");
		phoneMap.put("s", "s");
		phoneMap.put("t", "t");
		phoneMap.put("v", "v");
		phoneMap.put("w", "w");
		phoneMap.put("z", "z");
		phoneMap.put("ar", "eə");
		phoneMap.put("ir", "iə");
		phoneMap.put("ur", "ʊə");
		phoneMap.put("tr", "tr");
		phoneMap.put("dr", "dr");
		phoneMap.put("ts", "ts");
		phoneMap.put("dz", "dz");
	}
	
	/**
	 * 开始帧位置，每帧相当于10ms
	 */
	public int begPos;
	/**
	 * 结束帧位置
	 */
	public int endPos;
	/**
	 * 音素内容
	 */
	public String content;
	/**
	 * 增漏读信息：0（正确），16（漏读），32（增读），64（回读），128（替换）
	 */
	public int dpMessage;
	/**
	 * 时长（单位：帧，每帧相当于10ms）（cn）
	 */
	public int timeLen;
	
	/**
	 * 得到content对应的标准音标（en）
	 */
	public String getStdSymbol() { return getStdSymbol(content); }
	
	public static String getStdSymbol(String content) {
		String std = phoneMap.get(content);
		return (null == std)? content: std;
	}

	@NonNull
	@Override
	public String toString() {
		return "Phone{" +
				"begPos=" + begPos +
				", endPos=" + endPos +
				", content='" + content + '\'' +
				", dpMessage=" + dpMessage +
				", timeLen=" + timeLen +
				'}';
	}


	public int getBegPos() { return begPos; }
	public Phone setBegPos(int begPos) {
		this.begPos = begPos;
		return this;
	}

	public int getEndPos() { return endPos; }
	public Phone setEndPos(int endPos) {
		this.endPos = endPos;
		return this;
	}

	public String getContent() { return content; }
	public Phone setContent(String content) {
		this.content = content;
		return this;
	}

	public int getDpMessage() { return dpMessage; }
	public Phone setDpMessage(int dpMessage) {
		this.dpMessage = dpMessage;
		return this;
	}

	public int getTimeLen() { return timeLen; }
	public Phone setTimeLen(int timeLen) {
		this.timeLen = timeLen;
		return this;
	}
}
