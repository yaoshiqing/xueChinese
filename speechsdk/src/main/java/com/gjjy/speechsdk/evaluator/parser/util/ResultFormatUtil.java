/**
 * 
 */
package com.gjjy.speechsdk.evaluator.parser.util;

import com.gjjy.speechsdk.evaluator.parser.entity.Sentence;
import com.gjjy.speechsdk.evaluator.parser.entity.Phone;
import com.gjjy.speechsdk.evaluator.parser.entity.Syll;
import com.gjjy.speechsdk.evaluator.parser.entity.Word;

import java.util.ArrayList;

public class ResultFormatUtil {

	public static String formatDetails_EN(ArrayList<Sentence> sentences) {
		StringBuilder buffer = new StringBuilder();
		if (null == sentences) return buffer.toString();
		
		for (Sentence sentence: sentences ) {
			if( "噪音".equals(ResultTranslateUtil.getContent( sentence.getContent() ))
					|| "静音".equals(ResultTranslateUtil.getContent(sentence.getContent())) ) {
				continue;
			}
			
			if( null == sentence.getWords() ) continue;
			for ( Word word: sentence.getWords() ) {
				if ("噪音".equals(ResultTranslateUtil.getContent(word.getContent()))
						|| "静音".equals(ResultTranslateUtil.getContent(word.getContent()))) {
					continue;
				}
				
				buffer.append("\n单词[")
						.append(ResultTranslateUtil.getContent(word.getContent()))
						.append("] ")
						.append("朗读：")
						.append(ResultTranslateUtil.getDpMessageInfo(word.getDpMessage()))
						.append(" 得分：")
						.append(word.getTotalScore());
				if ( null == word.getSylls() ) {
					buffer.append("\n");
					continue;
				}
				
				for ( Syll syll: word.getSylls() ) {
					buffer.append("\n└音节[")
							.append(ResultTranslateUtil.getContent(syll.getStdSymbol()))
							.append("] ");
					if ( null == syll.getPhones() ) continue;
					
					for ( Phone phone: syll.getPhones() ) {
						buffer.append("\n\t└音素[")
								.append(ResultTranslateUtil.getContent(phone.getStdSymbol()))
								.append("] ")
								.append(" 朗读：")
								.append(ResultTranslateUtil.getDpMessageInfo(phone.getDpMessage()));
					}
				}
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}

	public static String formatDetails_CN(ArrayList<Sentence> sentences) {
		StringBuilder buffer = new StringBuilder();
		if (null == sentences) {
			return buffer.toString();
		}
		
		for (Sentence sentence: sentences ) {
			if (null == sentence.getWords()) continue;
			
			for ( Word word: sentence.getWords()) {
				buffer.append("\n词语[")
						.append(ResultTranslateUtil.getContent(word.getContent()))
						.append("] ")
						.append(word.getSymbol())
						.append(" 时长：")
						.append(word.getTimeLen());
				if (null == word.getSylls()) {
					continue;
				}
				
				for (Syll syll: word.getSylls()) {
					if ("噪音".equals(ResultTranslateUtil.getContent(syll.getContent()))
							|| "静音".equals(ResultTranslateUtil.getContent(syll.getContent()))) {
						continue;
					}
					
					buffer.append("\n└音节[")
							.append(ResultTranslateUtil.getContent(syll.getContent()))
							.append("] ")
							.append(syll.getSymbol())
							.append(" 时长：")
							.append(syll.getTimeLen());
					if (null == syll.getPhones()) {
						continue;
					}
					
					for (Phone phone: syll.getPhones()) {
						buffer.append("\n\t└音素[")
								.append(ResultTranslateUtil.getContent(phone.content))
								.append("] ")
								.append("时长：")
								.append(phone.getTimeLen())
								.append(" 朗读：")
								.append(ResultTranslateUtil.getDpMessageInfo(phone.getDpMessage()));
					}
				}
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}
}
