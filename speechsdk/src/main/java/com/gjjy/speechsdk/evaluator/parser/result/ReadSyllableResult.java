package com.gjjy.speechsdk.evaluator.parser.result;

import androidx.annotation.NonNull;

import com.gjjy.speechsdk.evaluator.parser.util.ResultFormatUtil;

public class ReadSyllableResult extends Result {
	
	public ReadSyllableResult() {
		setLanguage( "cn" );
		setCategory( "read_syllable" );
	}

	@NonNull
	@Override
	public String toString() {
		return "[总体结果]\n" +
				"评测内容：" + getContent() + "\n" +
				"朗读时长：" + getTimeLen() + "\n" +
				"总分：" + getTotalScore() + "\n\n" +
				"[朗读详情]" + ResultFormatUtil.formatDetails_CN( getSentences() );
	}
}
