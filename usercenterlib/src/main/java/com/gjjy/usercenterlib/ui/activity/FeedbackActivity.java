package com.gjjy.usercenterlib.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.usercenterlib.mvp.presenter.FeedbackPresenter;
import com.gjjy.usercenterlib.mvp.view.FeedbackView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybutils.utils.AppUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;
import com.ybear.ybutils.utils.toast.ToastManage;
import com.gjjy.usercenterlib.R;
import com.gjjy.speechsdk.PermManage;
import com.gjjy.basiclib.widget.CollectImagesView;
import com.gjjy.basiclib.widget.Toolbar;

/**
 * 意见反馈页面
 */
@Route(path = "/userCenter/feedbackActivity")
public class FeedbackActivity extends BaseActivity implements FeedbackView {
    @Presenter
    private FeedbackPresenter mPresenter;
    private Toolbar tbToolbar;
    private CollectImagesView civAddImage;
    private TextView tvMaxImgCount;
    private EditText etContact;
    private EditText etContent;
    private TextView tvCallEmail;
    private TextView tvCallFacebook;
    private DialogOption mLoadingDialog;

    private final String mEmail = Config.mEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_feedback );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        civAddImage.onActivityResult( requestCode, resultCode, data );
    }

    private void initView() {
        tbToolbar = findViewById( R.id.feedback_tb_toolbar );
        etContact = findViewById( R.id.feedback_et_contact );
        etContent = findViewById( R.id.feedback_et_content );
        civAddImage = findViewById( R.id.feedback_civ_add_image );
        tvMaxImgCount = findViewById( R.id.feedback_tv_max_img_count );
        tvCallEmail = findViewById( R.id.feedback_tv_call_email );
        tvCallFacebook = findViewById( R.id.feedback_tv_call_facebook );
    }

    private void initData() {
        tbToolbar.setOtherBtnTextSize( 17 );
        tbToolbar.setOtherBtnColor( R.color.colorBtnDisable );
        tbToolbar.setOtherBtnTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        mLoadingDialog = createLoadingDialog();
        //最大上传图片数量
        int maxCount = 4;

        setStatusBarHeight( R.id.toolbar_height_space );

        civAddImage.setMaxCollectCount( maxCount );
        notifyImgCountText( maxCount );
        PermManage.create( this ).reqExternalStoragePerm();

        tvCallEmail.setText( getCallEmailText() );
    }

    private Spanned getCallEmailText() {
        return Html.fromHtml(
                String.format(
                        getString( R.string.stringCallEmailTips),
                        AppUtil.getAppName( this ),
                        "<font color='#559BFD'>" + mEmail + "</font>"
                )
        );
    }

    private void startEmail() {
        Intent intent = new Intent( Intent.ACTION_SENDTO );
        intent.setData( Uri.parse( "mailto:" + mEmail ) );
        intent.putExtra( Intent.EXTRA_EMAIL, mPresenter.getUserEmail() );     //接收人
//        intent.putExtra( Intent.EXTRA_CC, email );    //抄送人
//        intent.putExtra( Intent.EXTRA_BCC, email );   //密送人
        intent.putExtra( Intent.EXTRA_SUBJECT, "Feedback" ); // 主题
        startActivity( Intent.createChooser( intent, "Choose Email Client" ) );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        tbToolbar.setOnClickOtherBtnListener(v -> {
            if( !isEnableSendBtn() ) return;
            String email = etContact.getText().toString();
            if( !mPresenter.checkEmail( email.trim() ) ) {
                ToastManage.get().showToast( this, R.string.stringEmailTips );
                return;
            }
            mLoadingDialog.show();
            //提交
            mPresenter.submit(
                    etContent.getText().toString(),
                    email,
                    civAddImage.getImageFileAll()
            );
//            showToast("感谢您的反馈");
//            List<Uri> list = civAddImage.getImageUriAll();
//            for (Uri file : list) {
//                LogUtil.e(file.toString() );
//            }
//            finish();
        });

        civAddImage.setOnImageChangedListener(new Consumer<Integer>() {
            @Override
            public void accept(Integer count) {
                notifyImgCountText( count );
            }
        });

        TextWatcher twListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIsEnableSendBtn();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        etContact.addTextChangedListener( twListener );
        etContent.addTextChangedListener( twListener );

        tvCallEmail.setOnClickListener( v -> {
            mPresenter.copyEmail( mEmail );
            startEmail();
        } );

        tvCallEmail.setOnLongClickListener( v -> {
            mPresenter.copyEmail( mEmail );
            return true;
        } );

        tvCallFacebook.setOnClickListener( v -> mPresenter.startFacebook() );
    }

    /**
     * 更新当前图片上传数量
     */
    private void notifyImgCountText(int count) {
        tvMaxImgCount.setText(String.format(
                getResources().getString( R.string.stringMaxImageCount ),
                String.valueOf( count )
        ));
        tvMaxImgCount.setVisibility( count > 0 ? View.VISIBLE : View.GONE );
    }

    private void checkIsEnableSendBtn() {
        tbToolbar.setOtherBtnColor(
                isEnableSendBtn() ? R.color.colorMain : R.color.colorBtnDisable
        );
    }

    private boolean isEnableSendBtn() {
        return !TextUtils.isEmpty( etContact.getText() ) &&
                !TextUtils.isEmpty( etContent.getText() );
    }

    @Override
    public void onCallContactHintColor(@ColorInt int color) {
//        etContact.setHintTextColor( color );
    }

    @Override
    public void onCallSubmitResult(boolean result) {
        mLoadingDialog.dismiss();
        if( result ) {
            showToast( R.string.stringFeedbackSubmitSuccess );
            finish();
        }else {
            showToast(R.string.stringError );
        }
    }

    @Override
    public void onCallAutoFillInEmail(@NonNull String email) {
        etContact.setText( email );
    }

    @Override
    public void onCallCopyEmailResult(boolean result) {
        ToastManage.get().showToast(
                this,
                getString( result ? R.string.stringCopyEmailSuccess : R.string.stringCopyEmailFailed )
        );
    }
}
