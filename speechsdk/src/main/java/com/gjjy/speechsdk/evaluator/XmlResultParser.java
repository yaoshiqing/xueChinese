package com.gjjy.speechsdk.evaluator;

import android.text.TextUtils;
import android.util.Xml;

import com.gjjy.speechsdk.evaluator.parser.entity.Phone;
import com.gjjy.speechsdk.evaluator.parser.entity.Sentence;
import com.gjjy.speechsdk.evaluator.parser.entity.Syll;
import com.gjjy.speechsdk.evaluator.parser.entity.Word;
import com.gjjy.speechsdk.evaluator.parser.result.FinalResult;
import com.gjjy.speechsdk.evaluator.parser.result.ReadSentenceResult;
import com.gjjy.speechsdk.evaluator.parser.result.ReadSyllableResult;
import com.gjjy.speechsdk.evaluator.parser.result.ReadWordResult;
import com.gjjy.speechsdk.evaluator.parser.result.Result;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class XmlResultParser {
	private String mData;
	public Result parse(String xml) {
		mData = xml;
		if (TextUtils.isEmpty(xml)) {
			return null;
		}

		XmlPullParser parser = Xml.newPullParser();

		try {
			InputStream ins = new ByteArrayInputStream(xml.getBytes());
			parser.setInput(ins, "utf-8");
			FinalResult result = null;

			int eventType = parser.getEventType();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ( "FinalResult".equals(parser.getName()) ) {
						// 只有一个总分的结果
						result = new FinalResult();
					} else if ( result != null &&  "ret".equals(parser.getName()) ) {
						result.setRet( getInt(parser, "value" ) );
					} else if ( result != null && "total_score".equals(parser.getName()) ) {
						result.setTotalScore( getFloat(parser, "value") );
					} else if ("xml_result".equals(parser.getName())) {
						// 详细结果
						return parseResult(parser);
					}

					break;
				case XmlPullParser.END_TAG:
					if ("FinalResult".equals(parser.getName())) {
						return result;
					}
					break;

				default:
					break;
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Result parseResult(XmlPullParser parser) {
		Result result = null;
		// <rec_paper>标签是否已扫描到
		boolean isPaper = false;
		Sentence sentence = null;
		Word word = null;
		Syll syll = null;
		Phone phone = null;

		int eventType;
		try {
			eventType = parser.getEventType();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("rec_paper".equals(parser.getName())) {
						isPaper = true;
					} else if ("read_syllable".equals(parser.getName())) {
						if( !isPaper ) {
							result = new ReadSyllableResult();
						} else {
							if( result != null ) readTotalResult(result, parser);
						}
					} else if ("read_word".equals(parser.getName())) {
						if( !isPaper ) {
							result = new ReadWordResult();
							String lan = getLanguage(parser);
							result.setLanguage( lan == null ? "cn": lan );
						} else {
							if( result != null ) readTotalResult(result, parser);
						}
					} else if( "read_sentence".equals(parser.getName()) ||
							"read_chapter".equals(parser.getName()) ) {
						if( !isPaper ) {
							result = new ReadSentenceResult();
							String lan = getLanguage(parser);
							result.setLanguage( lan == null ? "cn": lan );
						} else {
							if( result != null ) readTotalResult(result, parser);
						}
					} else if( "sentence".equals(parser.getName()) ) {
						if( result != null && result.getSentences() == null ) {
							result.setSentences( new ArrayList<>() );
						}
						sentence = createSentence(parser);
					} else if( "word".equals(parser.getName()) ) {
						if( sentence != null && sentence.getWords() == null ) {
							sentence.setWords( new ArrayList<>() );
						}
						word = createWords(parser);
					} else if ( "syll".equals(parser.getName()) ) {
						if( word != null && word.getSylls() == null ) {
							word.setSylls( new ArrayList<>() );
						}
						syll = createSyll(parser);
					} else if( "phone".equals(parser.getName()) ) {
						if( syll != null && syll.getPhones() == null ) {
							syll.setPhones( new ArrayList<>() );
						}
						phone = createPhone(parser);
					}

					break;
				case XmlPullParser.END_TAG:
					if ("phone".equals(parser.getName())) {
						if( syll != null ) syll.getPhones().add( phone );
					} else if ("syll".equals(parser.getName())) {
						if( word != null ) word.getSylls().add( syll );
					} else if ( "word".equals( parser.getName() ) ) {
						if( sentence != null ) sentence.getWords().add( word );
					} else if ( "sentence".equals( parser.getName() ) ) {
						if( result != null ) result.getSentences().add(sentence);
					} else if ("read_syllable".equals(parser.getName())
							|| "read_word".equals(parser.getName())
							|| "read_sentence".equals(parser.getName())) {
						return result;
					}
					break;

				default:
					break;
				}

				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void readTotalResult(Result result, XmlPullParser parser) {
		result.setBegPos( getInt(parser, "beg_pos") );
		result.setEndPos( getInt(parser, "end_pos") );
		result.setContent( getContent(parser) );
		result.setTotalScore( getFloat(parser, "total_score") );
		result.setTimeLen( getInt(parser, "time_len") );
		result.setExceptInfo( getExceptInfo(parser) );
		result.setRejected( getIsRejected(parser) );
	}

	private Phone createPhone(XmlPullParser parser) {
		Phone phone;
		phone = new Phone();
		phone.setBegPos( getInt(parser, "beg_pos") );
		phone.setEndPos( getInt(parser, "end_pos") );
		phone.setContent( getContent(parser) );
		phone.setDpMessage( getInt(parser, "dp_message") );
		phone.setTimeLen( getInt(parser, "time_len") );
		return phone;
	}

	private Syll createSyll(XmlPullParser parser) {
		Syll syll;
		syll = new Syll();
		syll.setBegPos( getInt(parser, "beg_pos") );
		syll.setEndPos( getInt(parser, "end_pos") );
		syll.setContent( getContent(parser) );
		syll.setSymbol( getSymbol(parser) );
		syll.setDpMessage( getInt(parser, "dp_message") );
		syll.setTimeLen( getInt(parser, "time_len") );
		return syll;
	}

	private Word createWords(XmlPullParser parser) {
		Word word;
		word = new Word();
		word.setBegPos( getInt(parser, "beg_pos") );
		word.setEndPos( getInt(parser, "end_pos") );
		word.setContent( getContent(parser) );
		word.setSymbol( getSymbol(parser) );
		word.setTimeLen( getInt(parser, "time_len") );
		word.setDpMessage( getInt(parser, "dp_message") );
		word.setTotalScore( getFloat(parser, "total_score") );
		word.setGlobalIndex( getInt(parser, "global_index") );
		word.setIndex( getInt(parser, "index") );
		return word;
	}

	private Sentence createSentence(XmlPullParser parser) {
		Sentence sentence;
		sentence = new Sentence();
		sentence.setBegPos( getInt(parser, "beg_pos") );
		sentence.setEndPos( getInt(parser, "end_pos") );
		sentence.setContent( getContent(parser) );
		sentence.setTimeLen( getInt(parser, "time_len") );
		sentence.setIndex( getInt(parser, "index") );
		sentence.setWordCount( getInt(parser, "words_count") );
		return sentence;
	}

	private String getLanguage(XmlPullParser parser) {
		return parser.getAttributeValue(null, "lan");
	}

	private String getExceptInfo(XmlPullParser parser) {
		return parser.getAttributeValue(null, "except_info");
	}

	private boolean getIsRejected(XmlPullParser parser) {
		String isRejected = parser.getAttributeValue(null, "is_rejected");
		if (null == isRejected) {
			return false;
		}

		return Boolean.parseBoolean(isRejected);
	}

	private String getSymbol(XmlPullParser parser) {
		return parser.getAttributeValue(null, "symbol");
	}

	private float getFloat(XmlPullParser parser, String attrName) {
		String val = parser.getAttributeValue(null, attrName);
		if (null == val) {
			return 0f;
		}
		return Float.parseFloat(val);
	}

	private String getContent(XmlPullParser parser) {
		return parser.getAttributeValue(null, "content");
	}

	private int getInt(XmlPullParser parser, String attrName) {
		String val = parser.getAttributeValue(null, attrName);
		if (null == val) {
			return 0;
		}
		return Integer.parseInt(val);
	}
}
