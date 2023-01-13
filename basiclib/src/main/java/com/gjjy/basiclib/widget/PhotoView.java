package com.gjjy.basiclib.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.util.MvpAnn;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.shape.IShape;
import com.ybear.ybcomponent.widget.shape.ShapeFrameLayout;
import com.ybear.ybutils.utils.LogUtil;
import com.gjjy.basiclib.R;
import com.gjjy.basiclib.mvp.model.DCIMModel;
import com.gjjy.basiclib.utils.StartUtil;

public class PhotoView extends LinearLayout implements View.OnClickListener {
    private ImageView ivPhoto;
    private ImageView ivAngle;
    private ImageView ivEdit;
    private ImageView ivVipIcon;

    private boolean isEditModel = false;
    private boolean isShowImage;
    private Uri mImgUri;
    private String mImgUrl;
    private OnClickListener mOnClickListener;
    private Consumer<Bitmap> mCallPhotoUriListener;
    @Model
    private DCIMModel mDCIMModel;

    public PhotoView(@NonNull Context context) {
        this(context, null);
    }

    public PhotoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onClick(View v) {
        if( isEditModel ) {
            mDCIMModel.startDCIM( (FragmentActivity) getContext() );
        }else {
            if( mOnClickListener != null ) mOnClickListener.onClick( v );

            if( !isShowImage ) return;
            if( mImgUri != null ) {
                StartUtil.startPictureViewerActivity( getContext(), mImgUri );
            }else if( mImgUrl != null ) {
                StartUtil.startPictureViewerActivity( getContext(), mImgUrl );
            }
        }
    }

    private ShapeFrameLayout sflShapeLayout;
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) { mOnClickListener = l; }

    private void init() {
        //绑定Model
        MvpAnn.instanceMvpAnn( this );

        mDCIMModel.setCallPhotoUriListener( bmp -> {
            //设置在相册中选择的图片
            if( bmp != null ) ivPhoto.setImageBitmap( bmp );
            if( mCallPhotoUriListener != null ) mCallPhotoUriListener.accept( bmp );
        } );

        setOrientation( VERTICAL );
        setGravity( Gravity.CENTER_HORIZONTAL );
        int size = Utils.dp2Px( getContext(), 62 );
        sflShapeLayout = new ShapeFrameLayout( getContext() );
        sflShapeLayout.setLayoutParams( new ViewGroup.LayoutParams( size, size ) );
        sflShapeLayout.setShape( IShape.Shape.OVAL );
        sflShapeLayout.setBorderSize( Utils.dp2Px( getContext(), 2 ) );

        sflShapeLayout.addView( ivPhoto = createPhotoView() );
        sflShapeLayout.addView( ivEdit = createEditView() );
//        ivAngle = addAngleView();
        addView( ivVipIcon = createVipIcon() );
        addView( sflShapeLayout );

        setDefaultPhoto();
        setEnableEditModel( false );
        setIsVip( true );
        super.setOnClickListener( this );

    }

    private ImageView createPhotoView() {
        ImageView iv = new ImageView( getContext() );
//        int size = Utils.dp2Px( getContext(), 62 );
//        int margin = Utils.dp2Px( getContext(), 10 );
//        LayoutParams lp = new LayoutParams( size, size );
//        lp.topMargin = margin;
//        lp.setMarginEnd( margin );
        iv.setLayoutParams( new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ) );
        iv.setScaleType( ImageView.ScaleType.CENTER_CROP );
//        iv.setBackgroundColor( Color.BLACK );
//        iv.setBackgroundColor( Color.parseColor("#BBBFC2") );
        return iv;
    }
//    private ImageView addAngleView() {
//        ImageView iv = new ImageView( getContext() );
//        int size = Utils.dp2Px( getContext(), 26 );
//        LayoutParams lp = new LayoutParams( size, size );
//        lp.gravity = Gravity.TOP | Gravity.END;
//        iv.setLayoutParams( lp );
//        iv.setImageResource( R.drawable.ic_dialog_correct_bg );
//
//        addView( iv );
//        return iv;
//    }

    private ImageView createEditView() {
        measure( 0, 0 );
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                getMeasuredWidth(),
                Utils.dp2Px( getContext(), 20 )
        );
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_edit_user_photo );
        iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        iv.setBackgroundResource( R.color.colorBlack30 );
        iv.setTranslationY( Utils.dp2Px( getContext(), -2 ) );
        iv.setVisibility( INVISIBLE );
        return iv;
    }

    private ImageView createVipIcon() {
        int height = Utils.dp2Px( getContext(), 22 );
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                height
        );
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        iv.setLayoutParams( lp );
        iv.setImageResource( R.drawable.ic_photo_vip );
//        iv.setScaleType( ImageView.ScaleType.CENTER_INSIDE );
        iv.setVisibility( View.INVISIBLE );
        iv.setTranslationY( Utils.dp2Px( getContext(), 3 ) );
//        iv.setTranslationY( -( height - Utils.dp2Px( getContext(), 4 ) ) );
        return iv;
    }

    public void setDefaultPhoto() {
        setPhotoResource( R.drawable.ic_photo_user );
    }

    public void setPhotoUrl(String url) {
        if( TextUtils.isEmpty( url ) ) {
            setDefaultPhoto();
            LogUtil.i("setPhotoUrl -> default");
            return;
        }
        mImgUrl = url;
        Glide.with( getContext() )
                .load( url )
                .error( R.drawable.ic_photo_user )
                .fallback( R.drawable.ic_photo_user )
                .into( ivPhoto );
        LogUtil.i("setPhotoUrl -> " + url);
    }

    public void setIsVip(boolean isVip) {
        ivVipIcon.setVisibility( isVip ? VISIBLE : INVISIBLE );
        setBorderColor(
                getResources().getColor( isVip ? R.color.colorBuyVipMain : R.color.colorWhite )
        );
//        int topMargin = Utils.dp2Px( getContext(), 20 );
//            LayoutParams lpPhoto = (LayoutParams) ivPhoto.getLayoutParams();
//            lpPhoto.topMargin = isVip ? topMargin : 0;
//        LayoutParams lpEdit = (LayoutParams) ivEdit.getLayoutParams();
//        lpEdit.topMargin = isVip ? topMargin : 0;

    }

    public void setBorderColor(@ColorInt int color) {
        sflShapeLayout.setBorderColor( color );
    }

    public void setPhotoResource(@DrawableRes int resId) {
        ivPhoto.setImageResource( resId );
    }

    public void setEnableEditModel(boolean enable) {
        isEditModel = enable;
        ivEdit.setVisibility( enable ? VISIBLE : INVISIBLE );
    }

    public void setEnableShowImage(boolean enable) { isShowImage = enable; }

    public boolean isShowImage() { return isShowImage; }

    public void setCallPhotoUriListener(Consumer<Bitmap> l) { mCallPhotoUriListener = l; }

    public void setRequestCode(int code) { mDCIMModel.setRequestCode( code ); }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mDCIMModel.onActivityResult( getContext(), requestCode, resultCode, data );
    }

    //    public void setShowAngleView(boolean enable) {
//        ivAngle.setVisibility( enable ? VISIBLE : GONE );
//    }
}
