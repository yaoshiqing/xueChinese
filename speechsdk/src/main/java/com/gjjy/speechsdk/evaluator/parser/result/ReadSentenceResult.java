package com.gjjy.speechsdk.evaluator.parser.result;

import androidx.annotation.NonNull;

import com.gjjy.speechsdk.evaluator.parser.util.ResultFormatUtil;

public class ReadSentenceResult extends Result {
	
	public ReadSentenceResult() {
		setCategory( "read_sentence" );
	}

	@NonNull
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		if ("cn".equals( getLanguage() )) {
			buffer.append("[总体结果]\n")
					.append("评测内容：")
					.append( getContent() ).append("\n")
					.append("朗读时长：")
					.append( getTimeLen() ).append("\n")
					.append("总分：")
					.append( getTotalScore() ).append("\n\n")
					.append("[朗读详情]")
					.append(ResultFormatUtil.formatDetails_CN( getSentences() ));
		} else {
			if ( isRejected() ) {
				// except_info代码说明详见《语音评测参数、结果说明文档》
				buffer.append("检测到乱读，")
						.append("except_info:")
						.append( getExceptInfo() ).append("\n\n");
			}
			
			buffer.append("[总体结果]\n")
					.append("评测内容：")
					.append( getContent() ).append("\n")
					.append("总分：")
					.append( getTotalScore() ).append("\n\n")
					.append("[朗读详情]")
					.append(ResultFormatUtil.formatDetails_EN( getSentences() ));
		}
		return buffer.toString();
	}
}
