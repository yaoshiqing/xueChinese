package com.gjjy.basiclib.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.gjjy.basiclib.mvp.model.DCIMModel;
import com.gjjy.basiclib.mvp.model.UserModel;
import com.gjjy.basiclib.widget.Toolbar;
import com.ybear.mvp.annotations.Model;
import com.ybear.mvp.annotations.ModelType;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.toast.ToastManage;
import com.gjjy.basiclib.R;

/**
 查看头像页面
 */
public class PictureViewerActivity extends BaseActivity {
    public @interface Extra {
        String URL = "ImageURL";
    }

    private Toolbar tbToolbar;
    private ImageView ivImg;

    private Uri mUri;
    private String mUrl;
    private DialogOption mLoadingDialog;
    @Model
    private DCIMModel mDCIMModel;
    @Model(ModelType.NEW_MODEL)
    private UserModel mUserModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_picture_viewer );
        initIntent();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        onCallShowLoadingDialog( false );
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        mDCIMModel.onActivityResult( this, requestCode, resultCode, data );
    }

    private void initIntent() {
        Intent intent = getIntent();
        if( intent == null ) return;
        mUri = intent.getData();
        if( mUri == null ) mUrl = intent.getStringExtra( Extra.URL );
    }

    private void initView() {
        tbToolbar = findViewById( R.id.picture_viewer_tb_toolbar );
        ivImg = findViewById( R.id.picture_viewer_iv_img );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();
        tbToolbar.showOtherBtnOfText( true );
        tbToolbar.setOtherBtnColor( R.color.colorWhite );

        if( mUri != null ) try {
            ivImg.setImageURI( mUri );
        }catch(Exception e) {
            e.printStackTrace();
        }
        if( mUrl != null ) Glide.with( this ).load( mUrl ).into( ivImg );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());
        tbToolbar.setOnClickOtherBtnListener( v -> mDCIMModel.startDCIM( this ) );

        mDCIMModel.setCallPhotoUriListener( bmp -> {
            if( bmp != null ) updateUserPhoto( bmp );
        } );
    }

    private void updateUserPhoto(Bitmap bmp) {
        if( getContext() == null ) return;
        onCallShowLoadingDialog( true );
        mUserModel.editUserAvatarUrl( getContext(), bmp, data -> post( () -> {
            if( data == null ) {
                post( () -> ToastManage.get().showToast( getContext(), R.string.stringError ) );
                return;
            }
            try {
                ivImg.setImageBitmap( bmp );
            }catch(Exception e) {
                e.printStackTrace();
            }
            onCallShowLoadingDialog( false );
        } ) );
    }

    private void onCallShowLoadingDialog(boolean isShow) {
        if( mLoadingDialog == null ) return;
        if( isShow ) {
            if( !mLoadingDialog.isShowing() ) mLoadingDialog.show();
        } else {
            if( mLoadingDialog.isShowing() )mLoadingDialog.dismiss();
        }
    }

//    public void saveBitmap(Bitmap bitmap, String bitName, Consumer<Boolean> call){
//        String fileName ;
//        String esdPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
//        File file ;
//        if( Build.BRAND .equals("Xiaomi") ) {
//            fileName = esdPath + "Camera/" + bitName ;
//        }else{
//            fileName = esdPath + bitName ;
//        }
//        file = new File(fileName);
//        if( file.exists() && !file.delete() ) {
//            if( call != null ) call.accept( false );
//            return;
//        }
//        FileOutputStream out;
//        try{
//            out = new FileOutputStream( file );
//            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
//            if( bitmap.compress( Bitmap.CompressFormat.JPEG, 100, out ) ) {
//                out.flush();
//                out.close();
//                // 插入图库
//                MediaStore.Images.Media.insertImage(
//                        getContentResolver(), file.getAbsolutePath(), bitName, null
//                );
//            }
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 发送广播，通知刷新图库的显示
//        sendBroadcast(
//                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse( "file://" + fileName) )
//        );
//    }

    @Override
    public int onStatusBarIconColor() { return SysUtil.StatusBarIconColor.WHITE; }
}
