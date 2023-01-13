/**
 * 
 */
package com.gjjy.speechsdk.evaluator.parser.util;

import android.util.SparseArray;

import java.util.HashMap;

public class ResultTranslateUtil {
	
	private static SparseArray<String> mDPMsgMap = new SparseArray<>();
	private static HashMap<String, String> mTargetedLearningContentMap = new HashMap<>();
	
	static {
		mDPMsgMap.put(0, "正常");
		mDPMsgMap.put(16, "漏读");
		mDPMsgMap.put(32, "增读");
		mDPMsgMap.put(64, "回读");
		mDPMsgMap.put(128, "替换");
		
		mTargetedLearningContentMap.put("sil", "静音");
		mTargetedLearningContentMap.put("silv", "静音");
		mTargetedLearningContentMap.put("fil", "噪音");
	}
	
	public static String getDpMessageInfo(int dp_message) {
		return mDPMsgMap.get(dp_message);
	}
	
	public static String getContent(String content) {
		String val = mTargetedLearningContentMap.get(content);
		return (null == val)? content: val;
	}
}
