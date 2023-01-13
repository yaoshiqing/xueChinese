package com.gjjy.speechsdk;

public interface OnVolumeChangedListener {
    void onChanged(int volume, byte[] data);
}