package com.gjjy.speechsdk.evaluator.parser.result;

import androidx.annotation.NonNull;

public class FinalResult extends Result {
	private int ret;
	private float totalScore;
	
	@NonNull
    @Override
	public String toString() { return "返回值：" + ret + "，总分：" + totalScore; }

	public int getRet() { return ret; }
	public FinalResult setRet(int ret) {
		this.ret = ret;
		return this;
	}

	public float getTotalScore() { return totalScore; }
	public FinalResult setTotalScore(float totalScore) {
		this.totalScore = totalScore;
		return this;
	}
}
