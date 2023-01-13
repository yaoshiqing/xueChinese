package com.gjjy.basiclib.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Consumer;

import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.mvp.model.JsCallModel;
import com.gjjy.basiclib.statistical.IStatisticalEvent;
import com.gjjy.basiclib.statistical.StatisticalManage;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.utils.DOMConstant;
import com.gjjy.basiclib.utils.SpIO;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.ContentWebView;
import com.gjjy.basiclib.widget.LoadingView;
import com.gjjy.basiclib.widget.NetworkErrorView;
import com.gjjy.basiclib.widget.initGuide.BaseInitGuide;
import com.gjjy.basiclib.widget.initGuide.FindInitGuide;
import com.gjjy.basiclib.widget.initGuide.FrontInitGuide;
import com.gjjy.basiclib.widget.initGuide.TargetedLearningDetailsInitGuide;
import com.gjjy.basiclib.widget.initGuide.UserCenterInvitationCodeInitGuide;
import com.ybear.baseframework.BaseAppCompatActivity;
import com.gjjy.basiclib.Config;
import com.gjjy.basiclib.R;
import com.ybear.ybcomponent.widget.shape.ShapeImageView;
import com.ybear.ybnetworkutil.network.NetworkChangeManage;
import com.ybear.ybnetworkutil.network.NetworkUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.ObjUtils;
import com.ybear.ybutils.utils.ResUtil;
import com.ybear.ybutils.utils.StackManage;
import com.ybear.ybutils.utils.SysUtil;
import com.ybear.ybutils.utils.Utils;
import com.ybear.ybutils.utils.dialog.Dialog;
import com.ybear.ybutils.utils.dialog.DialogOption;

public class BaseActivity extends BaseAppCompatActivity implements IStatisticalEvent {
    private NetworkErrorView nevNetworkError;
    private View.OnClickListener mOnNetworkErrorRefreshClickListener;

    private DialogOption mLogOutDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //网络监听服务没有启动时
        NetworkChangeManage ncm = NetworkChangeManage.get();
        if( !ncm.isRunningService() ) {
            ncm.registerService();
        }
        // adjust 浏览页面
        StatisticalManage.get().reviewAppPage(this.getLocalClassName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        post(() -> {
            //回到桌面时，解除注册网络监听服务
            if( StackManage.get().isGotoHome( this ) ) {
                NetworkChangeManage.get().unregisterService();
            }
//            ActivityManager am = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
//            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks( 1 );
//
//            if( list != null && list.size() > 0 ) {
//                ActivityManager.RunningTaskInfo info = list.get( 0 );
//
//                String topPackName = info.topActivity.getPackageName();
//                if( TextUtils.isEmpty( topPackName ) ) return;
//
//                if( !topPackName.contains( AppUtil.getPackageName( this ) ) ) {
//                    NetworkChangeManage.get().unregisterService();
//                }
////                boolean isEqPackName = topPackName.contains( AppUtil.getPackageName( this ) );
////                boolean isHaveAct = StackManage.get().isHaveExistActivityOfSkip( "MainActivity" );
////
////                LogUtil.e( "topPackName -> " + topPackName + " | " + AppUtil.getPackageName( this ) );
////                if( !isEqPackName && !isHaveAct ) {
////                    NetworkChangeManage.get().unregisterService();
////                }
//            }
        }, 300);
    }

    @Override
    protected void onDestroy() {
        onFragmentHide();
        super.onDestroy();
    }

    private ViewGroup mNetworkShowViewGroup;
    public ViewGroup onNetworkShowViewGroup() {
        if( mNetworkShowViewGroup == null ) {
            mNetworkShowViewGroup = getWindow().findViewById( Window.ID_ANDROID_CONTENT );
        }
        return mNetworkShowViewGroup;
    }
    public void setNetworkShowViewGroup(ViewGroup vg) {
        mNetworkShowViewGroup = vg;
    }

    @Override
    public void onSignIn(String method, String id) {
        StatisticalManage.get().signIn( method, id );
    }

    @Override
    public void onSignOff(String id) { StatisticalManage.get().onProfileSignOff( id ); }

    @Override
    public void onExitApp() {
        StatisticalManage.get().onKillProcess();
    }

    @Override
    public void onResult(int id, Object data) {
        super.onResult(id, data);
        if( id == DOMConstant.NETWORK_AVAILABLE_STATUS ) {
            doNetworkStatus( (Boolean) data );
        }
    }

    /**
     * 网络状态
     * @param networkStatus     网络状态
     */
    private void doNetworkStatus(Boolean networkStatus) {
        if( mOnNetworkErrorRefreshClickListener == null ) return;
        BaseActivity activity = (BaseActivity) getActivity();
        if( activity == null ) return;

        LogUtil.e("doNetworkStatus -> networkStatus:" + networkStatus);
        if( networkStatus ) {
            activity.hideNetworkErrorView();
        }else {
            activity.showNetworkErrorView();
            if( nevNetworkError != null ) nevNetworkError.callOnClick();
        }
    }

    /**
     * 显示反馈对话框
     */
    public DialogOption showFeedbackDialog(long userId, String email) {
        if( !SpIO.isShowFeedback( this ) ) return null;

        int height = SysUtil.getScreenTrueHeight( this );
        height -= height / 4;
        DialogOption op = Dialog
                .with( this )
                .animOfBottomTranslate()
                .transparentBackground()
                .defaultDimAmount()
                .createOfFree(
                        R.layout.dialog_feedback,
                        ViewGroup.LayoutParams.MATCH_PARENT, height
                );

        ContentWebView cwvWeb = op.findViewById( R.id.dialog_feedback_cwv_web );
        ImageView ivCloseBtn = op.findViewById( R.id.dialog_feedback_iv_close_btn );

        if( ivCloseBtn != null ) ivCloseBtn.setOnClickListener( v -> op.cancel() );
        if( cwvWeb != null ) {
            JsCallModel jcm = new JsCallModel();
            cwvWeb.addJavascriptInterface( jcm, "jsCall" );
            cwvWeb.loadUrl( String.format( Config.mFeedbackUrl, userId, email ) );
            cwvWeb.requestFocus(View.FOCUS_DOWN);
            jcm.setCallFeedback( r -> post( () -> {
                op.cancel();
                SpIO.saveFeedbackStatus( getContext(), true );
            }));
        }
        op.setCanceledOnTouchOutside( false );
        op.setCancelable( false );
        op.show();
        return op;
    }

    /**
     * 显示积分兑换对话框
     */
    public DialogOption showIntegralPointsRedeemDialog(int integralCount,
                                                       Consumer<Integer> callOk) {
        DialogOption op = Dialog
                .with( this )
                .animOfCenterBottomTranslate()
                .transparentBackground()
                .createOfFree( R.layout.dialog_integral_points_redeem );
        TextView tvIntegralCount = op.findViewById( R.id.dialog_integral_points_redeem_tv_integral_count );
        EditText etLightningNum = op.findViewById( R.id.dialog_integral_points_redeem_et_lightning_num );
        TextView tvIntegralNum = op.findViewById( R.id.dialog_integral_points_redeem_tv_integral_num );
        TextView tvInsufficientPoints = op.findViewById(
                R.id.dialog_integral_points_redeem_tv_insufficient_points
        );
        TextView tvNowBtn = op.findViewById( R.id.dialog_integral_points_redeem_tv_now_btn );
        int baseNum = 5;

        if( tvIntegralCount != null ) {
            tvIntegralCount.addTextChangedListener( new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int defLightning = Math.min( integralCount >= baseNum ? integralCount / baseNum : 0, 10 );
                    int defIntegral = defLightning > 0 ? defLightning * baseNum : 0;
                    if( etLightningNum != null ) {
                        etLightningNum.setText( String.valueOf( defLightning ) );
                    }
                    if( tvIntegralNum != null ) {
                        tvIntegralNum.setText( String.valueOf( defIntegral ) );
                    }
                    changedIntegralPointsRedeemStatus(
                            defLightning == 0, tvInsufficientPoints, tvNowBtn
                    );
                }

                @Override
                public void afterTextChanged(Editable s) {}
            } );
            tvIntegralCount.setText( String.valueOf( integralCount ) );
        }
        if( etLightningNum != null ) {
            etLightningNum.addTextChangedListener( new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    s = TextUtils.isEmpty( s ) ? "-1" : s;
                    int numCount = ObjUtils.parseInt( s );
                    if( numCount < 0 ) {
                        etLightningNum.setText( String.valueOf( 0 ) );
                        return;
                    }
                    //验证例如：01,02,03这种操作
                    if( s.length() > 1 && s.toString().startsWith( "0" ) ) {
                        etLightningNum.setText( String.valueOf( numCount ) );
                    }
                    int val = Math.max( numCount * baseNum, 0 );
                    //更新兑换状态（兑换按钮，兑换不足）
                    changedIntegralPointsRedeemStatus(
                            val > integralCount, tvInsufficientPoints, tvNowBtn
                    );
                    if( val > integralCount ) return;
                    //更新兑换的积分数量
                    if( tvIntegralNum != null &&
                            ObjUtils.parseInt( tvIntegralNum.getText().toString() ) != val ) {
                        tvIntegralNum.setText( String.valueOf( val ) );
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            } );
        }
        if( tvNowBtn != null ) {
            tvNowBtn.setOnClickListener( v -> {
                if( etLightningNum == null ) return;
                int val = ObjUtils.parseInt( etLightningNum.getText().toString() );
                if( callOk == null || val <= 0 ) return;
                callOk.accept( val );
            } );
        }
        op.show();
        return op;
    }
    private void changedIntegralPointsRedeemStatus(boolean isInsufficientPoints,
                                                   TextView tvInsufficientPoints,
                                                   TextView tvNowBtn) {
        if( tvInsufficientPoints != null ) {
            tvInsufficientPoints.setVisibility( isInsufficientPoints ? View.VISIBLE : View.INVISIBLE );
        }
        if( tvNowBtn != null ) {
            tvNowBtn.setEnabled( !isInsufficientPoints );
            tvNowBtn.setBackgroundResource(
                    isInsufficientPoints ? R.drawable.ic_touch_btn_false : R.drawable.ic_touch_btn_true
            );
        }
    }

    /**
     展示评论区键盘
     @param defHintText     默认的Hint文本
     @param call            输入内容
     */
    public void showCommentsKeyboard(@Nullable String defHintText, Consumer<String> call) {
        DialogOption op = Dialog.with( this, R.style.CommentsDialogStyle )
                .transparentBackground()
                .setGravity( Gravity.BOTTOM )
                .createOfFree( R.layout.block_comments_edit, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        com.ybear.ybcomponent.Utils.dp2Px( this, 61 )
                ));

        EditText etComments = op.findViewById( R.id.comments_et_comment );
        ImageView ivSendBtn = op.findViewById( R.id.comments_iv_send_btn );
        ShapeImageView sivUserPhoto = op.findViewById( R.id.comments_siv_user_photo );
        TextView tvCommentClick = op.findViewById( R.id.comments_tv_comment_click );
        LinearLayout llEditLayout = op.findViewById( R.id.comments_ll_edit_layout );

        if( sivUserPhoto != null ) sivUserPhoto.setVisibility( View.GONE );
        if( tvCommentClick != null ) tvCommentClick.setVisibility( View.GONE );
        if( etComments != null ) {
            //默认的Hint文本
            if( !TextUtils.isEmpty( defHintText ) ) etComments.setHint( defHintText );
            etComments.addTextChangedListener(new TextWatcher(){
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if( ivSendBtn == null ) return;
                    boolean isEmpty = TextUtils.isEmpty( s );
                    ivSendBtn.setEnabled( !isEmpty );
                    ivSendBtn.setImageResource( isEmpty ?
                            R.drawable.ic_comments_send_empty_btn :
                            R.drawable.ic_comments_send_btn
                    );
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            etComments.setVisibility( View.VISIBLE );
        }
        if( llEditLayout != null ) {
            llEditLayout.setBackgroundResource( R.drawable.shape_comments_send_layout_popup_bg );
        }

        if( ivSendBtn != null ) {
            ivSendBtn.setVisibility( View.VISIBLE );
            ivSendBtn.setTag( 0 );
            ivSendBtn.setOnClickListener(v -> {
                if( !ivSendBtn.isEnabled() ) return;
                ivSendBtn.setTag( 1 );
                op.dismiss();
            });
        }

        op.setOnShowListener( dialog -> post(() -> SysUtil.showKeyboard( etComments )) );
        op.setOnDismissListener(dialog -> {
            boolean isCall = ivSendBtn != null && ObjUtils.parseInt( ivSendBtn.getTag() ) == 1;
            if( etComments != null ) {
                if( call != null && isCall ) {
                    String s = etComments.getText().toString();
                    call.accept( TextUtils.isEmpty( s ) ? null : s );
                }
                etComments.setFocusable( false );
            }
            SysUtil.hideKeyboard( this );
        });
        op.show();
    }

    public DialogOption showFrontInitGuideDialog(Consumer<View> call) {
        return showInitGuideDialog( 0, call );
    }

    public DialogOption showFindInitGuideDialog(Consumer<View> call) {
        return showInitGuideDialog( 1, call );
    }

    public DialogOption showTargetedLearningDetailsInitGuideDialog(Consumer<View> call) {
        return showInitGuideDialog( 2, call );
    }

    public DialogOption showMeInvitationCodeInitGuideDialog(Consumer<View> call) {
        return showInitGuideDialog( 3, call );
    }

    private DialogOption showInitGuideDialog(int type, Consumer<View> call) {
        BaseInitGuide v = null;
        switch( type ) {
            case 0:
                v = new FrontInitGuide( this );
                break;
            case 1:
                v = new FindInitGuide( this );
                break;
            case 2:
                v = new TargetedLearningDetailsInitGuide( this );
                break;
            case 3:
                v = new UserCenterInvitationCodeInitGuide( this );
                break;
        }
        if( v == null ) return null;

        DialogOption op = Dialog
                .with( this )
                .transparentBackground()
                .setDimAmount( 0.8F )
                .animOfCenterAlpha()
                .createOfMatchAndFree( v );
        v.setOnFinishListener( view -> {
            if( call != null ) call.accept( view );
            op.dismiss();
        } );
        op.setCancelable( false );
        op.setCanceledOnTouchOutside( false );
        op.show();
        return op;
    }

    public void showNetworkErrorView() {
        if( nevNetworkError == null ) {
            nevNetworkError = new NetworkErrorView( getContext() );
            nevNetworkError.setOnRefreshClickListener(v -> {
                LogUtil.e( "nevNetworkError -> Listener:" + mOnNetworkErrorRefreshClickListener );
                if( mOnNetworkErrorRefreshClickListener == null ) return;
                mOnNetworkErrorRefreshClickListener.onClick( v );
            });
        }else {
            hideNetworkErrorView();
        }
        ViewGroup vg = onNetworkShowViewGroup();
        if( vg.indexOfChild( nevNetworkError ) == -1 ) {
            vg.addView( nevNetworkError );
        }
        nevNetworkError.show();
    }

    public void hideNetworkErrorView() {
        if( nevNetworkError != null && nevNetworkError.isShowing() ) nevNetworkError.hide();
    }

    public void setOnNetworkErrorRefreshClickListener(View.OnClickListener l) {
        mOnNetworkErrorRefreshClickListener = l;

        //可能会出现断网后才设置监听器的情况
        if( !NetworkUtil.isNetwork( this ) ) showNetworkErrorView();
    }

    public void onFragmentHide() {
        hideNetworkErrorView();
        ViewGroup vg = onNetworkShowViewGroup();
        if( vg.indexOfChild( nevNetworkError ) != -1 ) {
            vg.removeView( nevNetworkError );
        }
        setNetworkShowViewGroup( null );
        mOnNetworkErrorRefreshClickListener = null;
    }

    @Override
    public boolean onEnableImmersive() { return true; }

    @Override
    public boolean onEnableFullScreen() {
        return super.onEnableFullScreen();
    }

    @Override
    public int onStatusBarIconColor() { return SysUtil.StatusBarIconColor.BLACK; }

    public void dismissDialog(@Nullable DialogOption op) { if( op != null ) op.dismiss(); }

    /**
     * 显示免费优惠券对话框
     */
    public DialogOption showFreeVouchersDialog(boolean isInvite,
                                               View.OnClickListener callOk,
                                               View.OnClickListener callClose) {
        DialogOption op = Dialog
                .with( this )
                .transparentBackground()
                .animOfCenterBottomTranslate()
                .createOfFree( R.layout.dialog_free_vouchers );
        TextView tvTitle = op.findViewById( R.id.free_vouchers_tv_title );
        TextView tvOkBtn = op.findViewById( R.id.free_vouchers_tv_ok_btn );
        ImageView ivCloseBtn = op.findViewById( R.id.free_vouchers_iv_close_btn );

        if( tvTitle != null ) {
            tvTitle.setText( isInvite ?
                    R.string.stringFreeVouchersTitleOfInvited :
                    R.string.stringFreeVouchersTitleOfSent
            );
        }
        if( ivCloseBtn != null ) {
            ivCloseBtn.setOnClickListener( v -> {
                op.dismiss();
                showToast( R.string.stringFreeVouchersTipsOfBeginClose );
                BuriedPointEvent.get().onMePageOfCouponPopupOfCloseButton( this );
                if( callClose != null ) callClose.onClick( v );
            } );
        }
        if( tvOkBtn != null  ) tvOkBtn.setOnClickListener( v -> {
            if( callOk != null ) callOk.onClick( v );
            tvOkBtn.setEnabled( false );
            tvOkBtn.setBackgroundResource( R.drawable.ic_touch_btn_false );
        } );

        op.setCancelable( false );
        op.setCanceledOnTouchOutside( false );
        op.show();
        return op;
    }

    /**
     * 显示免费会员过期对话框
     */
    public void showFreeVipEndDialog() {
        DialogOption op = Dialog
                .with( this )
                .transparentBackground()
                .animOfCenterBottomTranslate()
                .create( R.layout.dialog_free_vip_end );
        TextView tvOkBtn = op.findViewById( R.id.free_vip_tv_ok_btn );
        ImageView ivCloseBtn = op.findViewById( R.id.free_vip_iv_close_btn );

        if( ivCloseBtn != null ) {
            ivCloseBtn.setOnClickListener( v -> {
                BuriedPointEvent.get().onMePageOfEndTryPopupOfCloseButton( this );
                op.dismiss();
                showToast( R.string.stringFreeVouchersTipsOfEndClose );
            } );
        }
        if( tvOkBtn != null  ) {
            tvOkBtn.setOnClickListener( v -> {
                StartUtil.startBuyVipActivity( this, true );
                op.dismiss();
            } );
        }

        op.setCancelable( false );
        op.setCanceledOnTouchOutside( false );
        op.show();
    }

    /**
     显示未登录丢失风险提示
     */
    public DialogOption showLoginOfTestDialog() {
        DialogOption op = Dialog
                .with( this )
                .transparentBackground()
                .animOfCenterBottomTranslate()
                .createOfFree( R.layout.dialog_login_tips );
        TextView tvLoginBtn = op.findViewById( R.id.dialog_login_tips_tv_login_btn );
        ImageView ivCloseBtn = op.findViewById( R.id.dialog_login_tips_iv_close_btn );
        if( tvLoginBtn != null  ) {
            tvLoginBtn.setOnClickListener( v -> StartUtil.startLoginActivity(
                    this,
                    StartUtil.REQUEST_CODE_TEST_LOGIN,
                    PageName.COURSE_DESC_LOGIN
            ) );
        }
        if( ivCloseBtn != null ) {
            ivCloseBtn.setOnClickListener( v -> op.dismiss() );
        }

        op.setCancelable( false );
        op.setCanceledOnTouchOutside( false );
        op.show();
        return op;
    }

    /**
     显示积分丢失风险提示
     */
    public void showIntegralLostDialog() {
        DialogOption op = Dialog
                .with( this )
                .transparentBackground()
                .animOfCenterBottomTranslate()
                .create( R.layout.dialog_integral_lost );
        TextView tvOkBtn = op.findViewById( R.id.integral_lost_tv_ok_btn );

        if( tvOkBtn != null  ) {
            tvOkBtn.setOnClickListener( v -> op.dismiss() );
        }

        op.setCancelable( false );
        op.setCanceledOnTouchOutside( false );
        op.show();
    }

    /**
     * 显示公告对话框
     * @param content           内容
     */
    public DialogOption showAnnouncementDialog(String content) {
        DialogOption op = Dialog
                .with( this )
                .transparentBackground()
                .animOfCenterBottomTranslate()
                .create( R.layout.dialog_announcement );
        ContentWebView cwvContent = op.findViewById( R.id.announcement_cwv_content );
        ImageView ivCloseBtn = op.findViewById( R.id.announcement_iv_close_btn );

        if( cwvContent != null && !TextUtils.isEmpty( content ) ) {
            cwvContent.loadUrl( content );
        }
        if( ivCloseBtn != null ) {
            ivCloseBtn.setOnClickListener( v -> op.dismiss() );
        }

        op.setCancelable( false );
        op.setCanceledOnTouchOutside( false );
        op.show();
        return op;
    }

    /**
     * 显示更新对话框
     * @param title             标题
     * @param content           内容
     * @param isCancelable      允许取消
     * @param l                 点击事件
     */
    public DialogOption showUpdateDialog(String title,
                                 String content,
                                 boolean isCancelable,
                                 View.OnClickListener l) {
        DialogOption op = Dialog
                .with( this )
                .transparentBackground()
                .animOfCenterBottomTranslate()
                .create( R.layout.dialog_check_version );
        TextView tvTitle = op.findViewById( R.id.dialog_check_version_tv_title );
        TextView tvContent = op.findViewById( R.id.dialog_check_version_tv_content );
        ImageView ivCloseBtn = op.findViewById( R.id.dialog_check_version_iv_close_btn );
        TextView tvUpdateBtn = op.findViewById( R.id.dialog_check_version_tv_update_btn );

        if( tvTitle != null && !TextUtils.isEmpty( title ) ) tvTitle.setText( title );
        if( tvContent != null && !TextUtils.isEmpty( content ) ) {
            content = content.replaceAll("\n", "<br>");
            tvContent.setText( ResUtil.fromHtmlOfResImg( this, content, R.drawable.class ) );
            tvContent.setMovementMethod( ScrollingMovementMethod.getInstance() );
        }
        if( ivCloseBtn != null ) {
            ivCloseBtn.setVisibility( isCancelable ? View.VISIBLE : View.GONE );
            if( isCancelable ) ivCloseBtn.setOnClickListener( v -> op.dismiss() );
        }
        if( tvUpdateBtn != null ) {
            tvUpdateBtn.setOnClickListener(v -> {
                if( isCancelable ) op.dismiss();
                if( l != null ) l.onClick( v );
            });
        }

        op.setCancelable( isCancelable );
        op.setCanceledOnTouchOutside( isCancelable );
        op.show();
        return op;
    }

    /**
     展示未解锁vip对话框
     @param typeId           分类id
     @param lessonId        课程id
     @param lessonName      课程名称
     @param imgUrl          图片链接
     @param title           标题
     @param content         内容
     */
    public void showUnlockVipDialog(String uid,
                                    String name,
                                    long typeId,
                                    long lessonId,
                                    String lessonName,
                                    String imgUrl,
                                    String title,
                                    String content,
                                    Consumer<Boolean> onDismissListener) {
        DialogOption op = Dialog
                .with( this )
                .animOfCenterBottomTranslate()
                .setCornerRadius( 10 )
                .create( R.layout.dialog_unlock_vip );

        ImageView ivImg = op.findViewById( R.id.unlock_vip_iv_img );
        TextView tvTitle = op.findViewById( R.id.unlock_vip_tv_title );
        TextView tvContent = op.findViewById( R.id.unlock_vip_tv_content );
        TextView tvUnlockBtn = op.findViewById( R.id.unlock_vip_tv_unlock_btn );

        if( ivImg != null ) {
            if( TextUtils.isEmpty( imgUrl ) ) {
                ivImg.setVisibility( View.GONE );
            }else {
                Glide.with( this ).load( imgUrl ).into( ivImg );
            }
        }
        if( tvTitle != null && !TextUtils.isEmpty( title ) ) {
            tvTitle.setText( title );
        }
        if( tvContent != null && !TextUtils.isEmpty( content ) ) {
            tvContent.setText( content );
//            tvContent.setMovementMethod( ScrollingMovementMethod.getInstance() );
        }
        if( tvUnlockBtn != null ) {
            tvUnlockBtn.setOnClickListener( v -> {
                tvUnlockBtn.setTag( "startBuyVip" );
                StartUtil.startBuyVipActivity( getActivity() );
                op.dismiss();
            } );
        }

        op.setCancelable( true );
        op.setCanceledOnTouchOutside( true );
        op.setOnDismissListener( dialog -> {
            boolean result = tvUnlockBtn == null || !"startBuyVip".equals( tvUnlockBtn.getTag() );
            if( onDismissListener != null ) onDismissListener.accept( !result );
        } );
        op.show();

        String cName = "未知";
        switch( (int) typeId ) {
            case 1001:
                cName = "每日聆听";
                break;
            case 1002:
                cName = "热门视频";
                break;
            case 1003:
                cName = "专项学习";
                break;
        }
        BuriedPointEvent.get().onDiscoveryPageOfCourseUnlockPopupOfUnlockButton(
                this, uid, name, typeId, cName, lessonId, lessonName
        );
    }

    public void showUnlockVipDialog(String uid,
                                    String name,
                                    long typeId,
                                    long lessonId,
                                    String lessonName,
                                    String imgUrl,
                                    String title,
                                    String content) {
        showUnlockVipDialog(
                uid, name, typeId, lessonId, lessonName, imgUrl, title, content, null
        );
    }

    /**
     * 显示编辑名字对话框
     * @param oldName       之前的名字
     * @param l             确定按钮
     */
    public DialogOption showEditUserNameDialog(CharSequence oldName, Consumer<String> l) {
        int maxLength = 25;
        DialogOption op = Dialog
                .with( this )
                .setDimAmount( 0.3F )
                .animOfCenterAlpha()
                .setBackgroundDrawableResource( R.drawable.ic_dialog_shadow_bg )
                .create( R.layout.dialog_edit_user_name );

        EditText etName = op.findViewById( R.id.dialog_edit_user_name_et_name );
        TextView tvTips = op.findViewById( R.id.dialog_edit_user_name_tv_max_len_tips );
        LinearLayout llLayout = op.findViewById( R.id.dialog_edit_user_name_ll_btn );

        if( etName != null ) {
            etName.setHint( oldName );
            etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if( tvTips == null ) return;
                    tvTips.setVisibility( s.length() > maxLength ? View.VISIBLE : View.INVISIBLE );
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if( llLayout != null ) {
            TextView tvCancel = (TextView) llLayout.getChildAt( 0 );
            TextView tvConfirm = (TextView) llLayout.getChildAt( 2 );

            tvCancel.setText( R.string.stringCancelA );
            tvCancel.setTextColor( getResources().getColor( R.color.colorMain ) );
            tvCancel.setBackgroundResource( R.drawable.ic_touch_skip_btn );
            tvCancel.setOnClickListener(v -> op.dismiss());

            tvConfirm.setText( R.string.stringConfirm );
            tvConfirm.setTextColor( Color.WHITE );
            tvConfirm.setBackgroundResource( R.drawable.ic_touch_btn_true );
            tvConfirm.setOnClickListener(v -> {
                boolean isClick = etName != null && etName.length() <= maxLength && l != null;
                boolean isEq =
                        etName != null && !TextUtils.isEmpty( etName.getText() ) &&
                        !etName.getText().equals( oldName );

                if( tvTips != null ) {
                    tvTips.setVisibility( isClick ? View.INVISIBLE : View.VISIBLE );
                }
                if( isClick ) {
                    if( isEq ) l.accept( etName.getText().toString() );
                    op.dismiss();
                }
            });
        }
        op.show();
        return op;
    }

    /**
     答题 - 回答正确
     @param content     上下文
     @param l           下一题按钮
     @return            对话框
     */
    public DialogOption showCorrectDialog(@Nullable String content, View.OnClickListener l) {
        setCallResult( Constant.SoundType.SOUND_CORRECT );
        DialogOption op = getCheckDialog(
                R.drawable.ic_dialog_correct_icon,
                R.drawable.ic_dialog_correct_bg,
                getString( R.string.stringDialogCorrect ),
                content,
                getResources().getColor( R.color.colorDialogCorrect ),
                l
        );
        op.show();
        return op;
    }

    /**
     答题 - 回答错误
     @param content     上下文
     @param l           下一题按钮
     @return            对话框
     */
    public DialogOption showErrorDialog(@Nullable String content, View.OnClickListener l) {
        setCallResult( Constant.SoundType.SOUND_ERROR );
        DialogOption op = getCheckDialog(
                R.drawable.ic_dialog_error_icon,
                R.drawable.ic_dialog_error_bg,
                getString( R.string.stringDialogError ),
                content,
                getResources().getColor( R.color.colorDialogError ),
                l
        );
        op.show();
        return op;
    }

    /**
     * 显示结束学习对话框  @param l    退出后的操作

     */
    public void showEndLearningDialog(View.OnClickListener l, Consumer<Boolean> cancel) {
        DialogOption op = getTipsDialog(
                getString( R.string.stringDialogEndLearning ),
                getResources().getString( R.string.contentDialogEndLearning ),
                R.string.stringDialogClose,
                R.color.colorMain,
                R.drawable.ic_touch_skip_btn,
                l,
                R.string.stringContinue,
                R.color.colorWhite,
                R.drawable.ic_touch_btn_true,
                null
        );
        TextView tvTitle = op.findViewById( R.id.dialog_check_tv_title );
        if( tvTitle != null ) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
            lp.topMargin = 0;
        }
        op.setOnCancelListener( dialog -> {
            if( cancel != null ) cancel.accept( true );
        } );
        op.show();
    }

    /**
     跳级考试对话框
     @param isFastMode      true：速学。false：跳级
     @param lLeft           取消考试点击事件监听器
     @param lRight          开始考试点击事件监听器
     */
    public void showSkipTestDialog(boolean isFastMode,
                                   View.OnClickListener lLeft,
                                   View.OnClickListener lRight) {
        int textRes = isFastMode ? R.string.contentDialogFastTest : R.string.contentDialogSkipTest;
        DialogOption op = getTipsDialog(
                null,
                getResources().getString( textRes ),
                R.string.stringDialogClose,
                R.color.colorMain,
                R.drawable.ic_touch_skip_btn,
                lLeft,
                R.string.stringStart,
                R.color.colorWhite,
                R.drawable.ic_touch_btn_true,
                lRight
        );
        TextView tvContent = op.findViewById( R.id.dialog_check_tv_content );
        if( tvContent != null ) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvContent.getLayoutParams();
            lp.topMargin = Utils.dp2Px( this, 31 );
        }
        op.show();
    }

    /**
     * 显示移除题目对话框
     * @param l    退出后的操作
     */
    public void showRemoveQuestionDialog(View.OnClickListener l) {
        getTipsDialog( getString( R.string.stringDialogRemoveQuestion ), l ).show();
    }

    /**
     * 显示已读消息提示对话框
     * @param l    退出后的操作
     */
    public void showMarkNotifyMsgDialog(View.OnClickListener l) {
        getTipsDialog( getString( R.string.stringDialogMarkNotifyMsg ), l ).show();
    }

    /**
     * 显示未登录无法打开学习提醒对话框
     * @param l     退出后的操作
     */
    public void showRemindOfLoginDialog(View.OnClickListener l) {
        getTipsDialog( getString( R.string.stringRemindOfLogin ), l ).show();
    }

    /**
     * 显示退出登录对话框
     * @param l    退出后的操作
     */
    public void showLogOutDialog(View.OnClickListener l) {
        if( mLogOutDialog == null ) {
            mLogOutDialog = getTipsDialog(
                    getString( R.string.stringDialogLogOut ),
                    R.string.stringCancelA,
                    R.string.stringConfirm,
                    l
            );
        }
        if( !mLogOutDialog.isShowing() ) mLogOutDialog.show();
    }

    public DialogOption createLoadingDialog() {
        LoadingView v = new LoadingView( this );
        v.setRepeatCount( LottieDrawable.INFINITE );
        int size = Utils.dp2Px( this, 120 );
        DialogOption op = Dialog
                .with( this )
                .setDimAmount( .3F )
                .transparentBackground()
                .createOfFree( v, size, size )
                .setCancelable( true )
                .setCanceledOnTouchOutside( true );
        op.setOnShowListener( dialog -> v.startLoading() );
        op.setOnDismissListener(dialog -> {
            onReChangedWindowState();
            v.stopLoading();
        } );
//        //全屏
//        op.getWindow().getDecorView().setSystemUiVisibility( 6662 );
        return op;
    }

    private DialogOption getCheckDialog(@DrawableRes int icon,
                                        @DrawableRes int bg,
                                        String title,
                                        @Nullable String content,
                                        @ColorInt int textColor,
                                        View.OnClickListener l) {

        DialogOption op = getDefDialog(
                icon,
                bg,
                title,
                TextUtils.isEmpty( content ) ? "" : content,
                textColor,
                textColor
        );
        TextView tvTitle = op.findViewById( R.id.dialog_check_tv_title );
        if( tvTitle != null ) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
            lp.topMargin = Utils.dp2Px( this, 8 );
        }
//        //全屏
//        op.getWindow().getDecorView().setSystemUiVisibility( 6662 );

        op.setCancelable( false );
        op.setCanceledOnTouchOutside( false );

        TextView tvCheckBtn = op.findViewById( R.id.dialog_check_tv_check_btn );
        if( tvCheckBtn != null ) {
            tvCheckBtn.setText( TextUtils.isEmpty( content ) ? R.string.stringOK : R.string.stringContinue );
            tvCheckBtn.setBackgroundResource(
                    bg == R.drawable.ic_dialog_correct_bg ?
                            R.drawable.ic_touch_btn_true :
                            R.drawable.ic_touch_btn_error
            );
            tvCheckBtn.setOnClickListener(v -> {
                if( l != null) l.onClick( v );
                op.dismiss();
            });
            tvCheckBtn.setVisibility( View.VISIBLE );
        }
        return op;
    }

    private DialogOption getTipsDialog(
            String title,
            String content,
            @StringRes int leftTextRes,
            @ColorRes int leftTextColorRes,
            @DrawableRes int leftBgRes,
            View.OnClickListener leftBtnListener,
            @StringRes int rightTextRes,
            @ColorRes int rightTextColorRes,
            @DrawableRes int rightBgRes,
            View.OnClickListener rightBtnListener) {
        DialogOption op = getDefDialog(
                -1,
                R.drawable.ic_dialog_def_bg,
                title,
                content,
                getResources().getColor( R.color.color66 ),
                getResources().getColor( R.color.color99 )
        );
//        //全屏
//        op.getWindow().getDecorView().setSystemUiVisibility( 6662 );

        TextView tvTitle = op.findViewById( R.id.dialog_check_tv_title );

        if( tvTitle != null ) {
            if( TextUtils.isEmpty( title ) ) {
                tvTitle.setVisibility( View.GONE );
            }
//            if( titleRes == 0 ) {
//                tvTitle.setVisibility( View.GONE );
//            }else {
//                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
//                lp.topMargin = Utils.dp2Px( this, 42 );
//            }
        }

        LinearLayout llTips = op.findViewById( R.id.dialog_def_ll_tips );
        //显示退出页面布局
        if( llTips == null ) return op;
        llTips.setVisibility( View.VISIBLE );
        int[] textRes = { leftTextRes, rightTextRes };
        int[] textColorRes = { leftTextColorRes, rightTextColorRes };
        int[] bgRes = { leftBgRes, rightBgRes };
        View.OnClickListener[] listeners = { leftBtnListener, rightBtnListener };
        for (int i = 0; i < llTips.getChildCount(); i++) {
            int finalI = i;
            TextView tvBtn = (TextView) llTips.getChildAt( i );
            if( tvBtn == null ) continue;
            tvBtn.setText( textRes[ i ] );
            tvBtn.setTextSize( 16 );
            tvBtn.setTextColor( getResources().getColor( textColorRes[ i ] ) );
            tvBtn.setBackgroundResource( bgRes[ i ] );
            tvBtn.setOnClickListener(v -> {
                op.dismiss();
                if( listeners[ finalI ] != null ) listeners[ finalI ].onClick( v );
            });

        }
        return op;
    }

    private DialogOption getTipsDialog(
            String title,
            @StringRes int leftTextRes,
            @StringRes int rightTextRes,
            View.OnClickListener rightBtnListener) {
        return getTipsDialog(
                title,
                null,
                leftTextRes,
                R.color.colorMain,
                R.drawable.ic_touch_skip_btn,
                null,
                rightTextRes,
                R.color.colorWhite,
                R.drawable.ic_touch_btn_true,
                rightBtnListener
        );
    }

    private DialogOption getTipsDialog(String title,
                                       View.OnClickListener rightBtnListener) {
        DialogOption op = getTipsDialog(
                title,
                R.string.stringNo,
                R.string.stringYes,
                rightBtnListener
        );
        TextView tvTitle = op.findViewById( R.id.dialog_check_tv_title );
        if( tvTitle != null ) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
            lp.topMargin = 0;
        }
        return op;


    }

    private DialogOption getDefDialog(@DrawableRes int icon, @DrawableRes int bg,
                                      String title, @Nullable String content,
                                      @ColorInt int titleColor, @ColorInt int contentColor) {
        DialogOption op = Dialog.with( this )
                .animOfBottomTranslate()
                .setDimAmount( 0.3F )
                .transparentBackground()
                .createOfFree( R.layout.dialog_def );

        View vLayout = op.findViewById( R.id.dialog_def_layout );
        ImageView ivIcon = op.findViewById( R.id.dialog_def_iv_icon );
        Space sSpace = op.findViewById( R.id.dialog_def_s_space );
//        ImageView ivBg = op.findViewById( R.id.dialog_def_iv_bg );
        TextView tvTitle = op.findViewById( R.id.dialog_check_tv_title );
        TextView tvContent = op.findViewById( R.id.dialog_check_tv_content );
        boolean isNullOfTitle = TextUtils.isEmpty( title );
        if( ivIcon != null ) {
            if( icon == -1 ) {
                ivIcon.setVisibility( View.GONE );
                if( sSpace != null && !isNullOfTitle ) {
                    sSpace.setVisibility( View.VISIBLE );
                }
            }else {
                ivIcon.setImageResource( icon );
                ivIcon.setVisibility( View.VISIBLE );
                if( sSpace != null && !isNullOfTitle ) sSpace.setVisibility( View.GONE );
            }
        }

        if( vLayout != null ) vLayout.setBackgroundResource( bg );
//        if( ivBg != null ) ivBg.setImageResource( bg );
        if( tvTitle != null ) {
            if( !isNullOfTitle ) {
                tvTitle.setText(
                        ResUtil.fromHtmlOfResImg( this, title, R.drawable.class )
                );
            }
            if( titleColor != 0 ) tvTitle.setTextColor( titleColor );
        }
        if( tvContent != null ) {
            if( content != null ) {
                content = content.replaceAll( "\r\n", "<br>" );
                tvContent.setText(
                        ResUtil.fromHtmlOfResImg( this, content, R.drawable.class )
                );
                tvContent.setTextColor( contentColor );
            }
            tvContent.setVisibility( content == null ? View.GONE : View.VISIBLE );
        }
//        SysUtil.setFullScreen( op.getWindow() );
//        op.getWindow().setWindowAnimations( R.style.dialogAnim );
        op.setOnDismissListener(dialog -> onResume());
        return op;
    }
}