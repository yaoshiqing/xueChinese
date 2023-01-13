package com.gjjy.basiclib.mvp.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentActivity;

import com.gjjy.basiclib.utils.StartUtil;
import com.ybear.mvp.model.MvpModel;
import com.ybear.ybutils.utils.Utils;

public class DCIMModel extends MvpModel {
    private int mReqCode = 660;
    private Consumer<Bitmap> mCallPhotoUriListener;

    public DCIMModel() { }

    public void setRequestCode(int code) { mReqCode = code; }

    public void startDCIM(FragmentActivity activity) {
        StartUtil.startDCIM( activity, mReqCode );
    }

    public Bitmap toThumbnail(Context context, Uri uri) {
        return com.ybear.ybutils.utils.Utils.getThumbnail(
                Utils.uriToBitmap( context, uri ), 512, true
        );
    }

    public void setCallPhotoUriListener(Consumer<Bitmap> call) {
        mCallPhotoUriListener = call;
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode != mReqCode || resultCode != Activity.RESULT_OK ) return;
        Uri uri;
        if( data == null || ( uri = data.getData() ) == null ) return;
        Bitmap bmp = toThumbnail( context, uri );
        if( mCallPhotoUriListener != null ) mCallPhotoUriListener.accept( bmp );
    }
}
