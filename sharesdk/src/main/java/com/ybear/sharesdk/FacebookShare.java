package com.ybear.sharesdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMedia;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacebookShare {
    private CallbackManager mCallbackManager;

    private FacebookShare() {

    }

    public static FacebookShare get() { return HANDLER.I; }
    private static final class HANDLER { private static final FacebookShare I = new FacebookShare(); }

    public boolean share(Activity activity,
                         ShareContent content,
                         FacebookCallback<Sharer.Result> call) {
        return share( new ShareDialog( activity), content, call );
    }

    public boolean share(Fragment fragment,
                         ShareContent content,
                         FacebookCallback<Sharer.Result> call) {
        return share( new ShareDialog( fragment ), content, call );
    }

    private boolean share(ShareDialog dialog,
                          ShareContent content,
                          FacebookCallback<Sharer.Result> call) {
        ShareDialog.Mode mode = ShareDialog.Mode.AUTOMATIC;
        boolean isCanShow = dialog.canShow( content, mode );
        if( !isCanShow ) return false;
        dialog.show( content, mode );

        dialog.registerCallback(
                mCallbackManager = CallbackManager.Factory.create(),
                call
        );
        return true;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if( mCallbackManager == null ) return false;
        return mCallbackManager.onActivityResult( requestCode, resultCode, data );
    }

    public ShareLinkContent createShareLink(Uri uri, String quote) {
        return new ShareLinkContent.Builder()
                .setContentUrl( uri )
                .setQuote( quote )
                .build();
    }
    public ShareLinkContent createShareLink(Uri uri) {
        return createShareLink( uri, null );
    }
    public ShareLinkContent createShareLink(String uri, String quote) {
        return createShareLink( Uri.parse( uri ), quote );
    }
    public ShareLinkContent createShareLink(String uri) {
        return createShareLink( uri, null );
    }

    public SharePhotoContent createSharePhoto(SharePhoto... photos) {
        return new SharePhotoContent.Builder()
                .setPhotos( Arrays.asList( photos ) )
                .build();
    }

    public SharePhotoContent createSharePhoto(Bitmap... bitmaps) {
        return createSharePhoto( toSharePhoto( bitmaps ).toArray( new SharePhoto[ 0 ] ) );
    }

    public ShareVideoContent createShareVideo(ShareVideo video) {
        return new ShareVideoContent.Builder()
                .setVideo( video )
                .build();
    }
    public ShareVideoContent createShareVideo(Uri uri) {
        return createShareVideo( toShareVideo( uri ) );
    }
    public ShareVideoContent createShareVideo(String url) {
        return createShareVideo( toShareVideo( url ) );
    }

    public ShareMediaContent createShareMedium(ShareMedia... medias) {
        return new ShareMediaContent.Builder()
                .addMedia( Arrays.asList( medias ) )
                .build();
    }

    public ShareVideo toShareVideo(Uri uri) {
        return new ShareVideo.Builder().setLocalUrl( uri ).build();
    }

    public ShareVideo toShareVideo(String url) {
        return toShareVideo( Uri.parse( url ) );
    }

    public SharePhoto toSharePhoto(Bitmap bitmap) {
        return new SharePhoto.Builder().setBitmap( bitmap ).build();
    }

    public SharePhoto toSharePhoto(Uri uri) {
        return new SharePhoto.Builder().setImageUrl( uri ).build();
    }

    public SharePhoto toSharePhoto(String url) {
        return toSharePhoto( Uri.parse( url ) );
    }

    public List<SharePhoto> toSharePhoto(Bitmap... bitmaps) {
        List<SharePhoto> list = new ArrayList<>();
        for ( Bitmap bmp : bitmaps ) list.add( toSharePhoto( bmp ) );
        return list;
    }

    public List<SharePhoto> toSharePhoto(Uri... bitmapUris) {
        List<SharePhoto> list = new ArrayList<>();
        for ( Uri uri : bitmapUris ) list.add( toSharePhoto( uri ) );
        return list;
    }
}
