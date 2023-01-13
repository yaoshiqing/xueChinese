package com.gjjy.usercenterlib.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gjjy.basiclib.buried_point.BuriedPointEvent;
import com.gjjy.basiclib.dao.entity.UserDetailEntity;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.utils.StartUtil;
import com.gjjy.basiclib.widget.PhotoView;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.osslib.OSS;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.mvp.presenter.SetUpPresenter;
import com.gjjy.usercenterlib.mvp.view.SetUpView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.AppUtil;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.dialog.DialogOption;

/**
 * 设置页面
 */
@Route(path = "/userCenter/setUpActivity")
public class SetUpActivity extends BaseActivity implements SetUpView {
    @Presenter
    private SetUpPresenter mPresenter;

    private Toolbar tbToolbar;
    private LinearLayout llList;
    private TextView tvRemindSwitch;
    private PhotoView pvPhoto;
    private TextView tvLogOutBtn;
    private TextView tvName;
    private TextView tvPlanOfWordCount;
    private TextView tvPlanOfDaysCount;

    private DialogOption mLoadingDialog;
    private DialogOption mEditUserNameDialog;

    private TextView tvUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_setting );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        dismissDialog( mLoadingDialog );
        dismissDialog( mEditUserNameDialog );
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pvPhoto.onActivityResult( requestCode, resultCode, data );
        //退出账号的后续操作
        mPresenter.logOutOfOnActivityResult( requestCode, resultCode );
        if( requestCode == StartUtil.REQUEST_CODE_EXIT_LOGIN_RESULT && resultCode == RESULT_OK ) {
            setResult( RESULT_OK );
            finish();
        }
    }

    private void initView() {
        tbToolbar = findViewById( R.id.setting_tb_toolbar );
        llList = findViewById( R.id.setting_ll_list );
        tvRemindSwitch = findViewById( R.id.setting_tv_remind_switch );
        pvPhoto = findViewById( R.id.setting_pv_photo );
        tvLogOutBtn = findViewById( R.id.setting_tv_log_out_btn );
        tvName = findViewById( R.id.setting_stv_user_name );
        tvUserId = findViewById( R.id.setting_tv_user_id );
        tvPlanOfWordCount = findViewById( R.id.setting_tv_plan_words_count );
        tvPlanOfDaysCount = findViewById( R.id.setting_tv_plan_days_count );

        FrameLayout.LayoutParams lpName = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lpName.bottomMargin = Utils.dp2Px( this, 12 );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );
        mLoadingDialog = createLoadingDialog();
        onCallShowLoadingDialog( true );
        //初始化列表
        initListData();
        switchLogOutMode();

        OSS.get().reqExternalStoragePerm( this );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        //退出登录
        tvLogOutBtn.setOnClickListener(v -> showLogOutDialog(v1 -> mPresenter.logOut() ));

        //学习通知按钮点击事件监听器
        llList.getChildAt( 0 ).setOnClickListener(v -> mPresenter.startLearningReminderActivity());

        //编辑名称
        tvName.setOnClickListener(v -> {
            if( !mPresenter.isLoginResult() ) return;
            BuriedPointEvent.get().onSetUpPageOfEditName( this );
            mEditUserNameDialog = showEditUserNameDialog(tvName.getText(), newName -> {
                mPresenter.editUserName( newName );
            });
        });

        pvPhoto.setCallPhotoUriListener(bmp -> mPresenter.updateUserPhoto( bmp ));
        pvPhoto.setOnClickListener(v ->
                BuriedPointEvent.get().onSetUpPageOfEditPicture( getActivity() )
        );


        ViewGroup vg3 =  (ViewGroup) llList.getChildAt( 3 );
        for( int i = 0; i < vg3.getChildCount(); i++ ) {
            View v = vg3.getChildAt( i );
            switch( i ) {
                case 0:     //reset pwd
                    v.setOnClickListener( v12 -> mPresenter.startEmailResetPasswordActivity() );
                    break;
                case 2:     //terms
                    v.setOnClickListener( v12 -> StartUtil.startTermsActivity() );
                    break;
                case 4:     //evaluation
                    v.setOnClickListener( v12 -> mPresenter.startEvaluationActivity() );
                    break;
            }
        }
    }

    private void initListData() {
        int[] strResIds = {
                R.string.stringSetUpLearningReminder,
                R.string.stringHearing,
                R.string.stringVoice,
                R.string.stringTranslate,
                R.string.stringSound,
                R.string.stringSlowAudio,
                R.string.stringSetUpExit,
                R.string.stringSetUpVer,
        };
        int[] imgResIds = {
                R.drawable.ic_answer_set_hearing,
                R.drawable.ic_answer_set_voice,
                R.drawable.ic_answer_set_translate,
                R.drawable.ic_answer_set_sound,
                R.drawable.ic_answer_set_snail
        };
        ViewGroup vg;
        for (int i = 0; i < llList.getChildCount(); i++) {
            View v = llList.getChildAt( i );
            switch ( i ) {
                case 0:         //通知
                    ((TextView)((ViewGroup)v).getChildAt( 0 )).setText( strResIds[ 0 ] );
                    break;
                case 1:         //题型开关
                case 2:         //音效等设置
                    for (int j = 0; j < ((ViewGroup)v).getChildCount(); j++) {
                        vg = (ViewGroup)((ViewGroup)((ViewGroup)v)
                                .getChildAt( j ))
                                .getChildAt(0);
                        TextView tv = (TextView) vg.getChildAt( 0 );
                        @SuppressLint("UseSwitchCompatOrMaterialCode")
                        Switch swi = (Switch) vg.getChildAt( 1 );

                        int index = i == 1 ? j : i + j + 1;
                        int nameRes = strResIds[ i == 1 ? 1 + j : 4 + j ];

                        tv.setCompoundDrawablesWithIntrinsicBounds(
                                imgResIds[ index ], 0, 0, 0
                        );
                        tv.setText( nameRes );
                        boolean[] enables = mPresenter.getQuestionSwitchIsEnable();
                        swi.setSelected( enables[ index ] );
                        swi.setChecked( enables[ index ] );
                        swi.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            mPresenter.onQuestionSwitch(index, isChecked);
                            //跳过音效开启/关闭的埋点
                            if( index == 3 ) return;
                            mPresenter.onBuriedPointSwitchQuestionType(
                                    isChecked, getString( nameRes )
                            );
                        });
                    }
                    break;
                case 4:         //退出
                    ((TextView)v).setText( strResIds[ 6 ] );
                    break;
                case 5:         //版本号
                    ((TextView)v).setText( String.format(
                            getResources().getString( strResIds[ 7 ] ),
                            AppUtil.getVerName( this )
                    ));
                    break;
            }
        }
    }

    /**
     * 切换到已登录状态
     */
    private void switchLogInMode(boolean isVip) {
        tvName.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_setting_edit, 0, 0, 0
        );
        tvName.setCompoundDrawablePadding( Utils.dp2Px( this, 11 ) );
        tvName.setTextColor( getResources().getColor( R.color.colorMain ) );
        tvLogOutBtn.setVisibility( View.VISIBLE );
        pvPhoto.setEnableEditModel( true );
        pvPhoto.setBorderColor(
                isVip ?  getResources().getColor( R.color.colorBuyVipMain ) : Color.WHITE
        );
        pvPhoto.setIsVip( isVip );
    }

    /**
     * 切换到未登录状态
     */
    private void switchLogOutMode() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvName.getLayoutParams();
        lp.topMargin = Utils.dp2Px( this, 9 );
        tvName.setLayoutParams( lp );
        tvName.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0 );
        tvName.setTextColor( getResources().getColor( R.color.color66 ) );
        tvLogOutBtn.setVisibility( View.GONE );
        pvPhoto.setEnableEditModel( false );
        pvPhoto.setBorderColor( Color.WHITE );
        pvPhoto.setIsVip( false );

    }

    @Override
    public void onCallRemindSwitch(boolean enable) {
        tvRemindSwitch.setText( getString( enable ? R.string.stringOn : R.string.stringOff ) );
    }

    /**
     * 退出登录
     * @param result    退出结果
     */
    @Override
    public void onCallLogOut(boolean result) {
        if( result ) {
            onSignOff( mPresenter.getLoginId() );
//            getStackManage().exitActivityAll( this );
        }
    }

    @Override
    public void onCallShowResetPassword(boolean isShow) {
        ((ViewGroup)llList.getChildAt( 3 ))
                .getChildAt( 0 )
                .setVisibility( isShow ? View.VISIBLE : View.GONE );
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onCallUpdateUserData(UserDetailEntity data) {
        pvPhoto.setPhotoUrl( data.getAvatarUrl() );
        if( data.getIsBindAccount() ) {
            switchLogInMode( data.getIsVip() );
        }else {
            switchLogOutMode();
        }

        //处理名字
        String name = data.getNickname();
        if( TextUtils.isEmpty( name ) ) {
            tvName.setText( R.string.stringSignature );
        }else {
            tvName.setText( data.getNickname() );
        }

        String userId = getString( R.string.stringUserId );
        tvUserId.setText( String.format( userId, data.getUserId() ) );

        tvPlanOfWordCount.setText( String.valueOf( data.getWordCount() ) );
        tvPlanOfDaysCount.setText( String.valueOf( data.getVisitCount() ) );

        onCallShowLoadingDialog( false );
        LogUtil.i( "onCallUpdateUserData -> " + data );
    }

    /**
     * 登录结果
     * @param result        结果
     */
    @Override
    public void onCallLoginResult(boolean result) {
        //是否显示退出登录按钮
        tvLogOutBtn.setVisibility( result ? View.VISIBLE : View.GONE );
        if( result ) {
            switchLogInMode( mPresenter.isVip() );
        }else {
            switchLogOutMode();
        }
    }

    @Override
    public void onCallShowLoadingDialog(boolean isShow) {
        if( isShow ) {
            mLoadingDialog.show();
        } else {
            mLoadingDialog.dismiss();
        }
    }
}