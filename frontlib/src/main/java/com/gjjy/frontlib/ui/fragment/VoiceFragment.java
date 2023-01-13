package com.gjjy.frontlib.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.ui.fragment.BaseFragment;
import com.gjjy.basiclib.utils.Constant;
import com.gjjy.basiclib.widget.drag.FlowLayoutManager;
import com.gjjy.frontlib.R;
import com.gjjy.frontlib.adapter.ChinesePinYinAdapter;
import com.gjjy.frontlib.entity.OptionsEntity;
import com.gjjy.frontlib.mvp.presenter.VoicePresenter;
import com.gjjy.frontlib.mvp.view.VoiceView;
import com.gjjy.frontlib.widget.AudioPlayBackButton;
import com.gjjy.frontlib.widget.AudioPlayButton;
import com.gjjy.frontlib.widget.CheckButton;
import com.gjjy.speechsdk.evaluator.parser.entity.Syll;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.pager.OnVisibleChangedListener;
import com.ybear.ybcomponent.widget.shape.ShapeLinearLayout;
import com.ybear.ybutils.utils.FrameAnimation;
import com.ybear.ybutils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 口语题
 */
public class VoiceFragment extends BaseFragment
        implements VoiceView, OnVisibleChangedListener {
    @Presenter
    private VoicePresenter mPresenter;

    private RecyclerView rvQuestion;
    private TextView tvTitle;
    private ShapeLinearLayout sllAudioLayout;
    private LinearLayout llOptLayout;
    private TextView tvRecordingBtn;
    private AudioPlayBackButton apbbPlayBackBtn;
    private View vVoicePrintLayout;
    private ImageView ivVoicePrintImg;
    private AudioPlayButton apbAudioBtn;
    private AudioPlayButton apbBtmAudioBtn;
    private CheckButton cbCheckBtn;
    private View vNextBtn;

    private ChinesePinYinAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_voice, container, false );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
//        switchStartUI( false );
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    @Override
    public void onDestroy() {
        if( apbAudioBtn != null ) apbAudioBtn.release();
        if( apbBtmAudioBtn != null ) apbBtmAudioBtn.release();
        if( apbbPlayBackBtn != null ) apbbPlayBackBtn.release();
        super.onDestroy();
    }

    @Override
    public void onFragmentShow(@NonNull Context context, int position) {
        post(() -> {
            switchStartUI( false );
            if( apbAudioBtn != null ) apbAudioBtn.initMediaX();
            if( apbBtmAudioBtn != null ) apbBtmAudioBtn.initMediaX();
        });
        mPresenter.onPagerHiddenStatus( false );

        BaseActivity activity = (BaseActivity) context;
        activity.setOnNetworkErrorRefreshClickListener( v -> {} );
    }

    @Override
    public void onFragmentHidden(@NonNull Context context, int position) {
        mPresenter.onPagerHiddenStatus( true );
        pause();
        post(() -> {
            if( apbAudioBtn != null ) apbAudioBtn.release();
            if( apbBtmAudioBtn != null ) apbBtmAudioBtn.release();
        });
    }

    private void pause() {
        try { apbAudioBtn.pauseNow(); } catch(Exception e) { e.printStackTrace(); }
        try { apbBtmAudioBtn.pauseNow(); } catch(Exception e) { e.printStackTrace(); }
        try { apbbPlayBackBtn.stop(); } catch(Exception e) { e.printStackTrace(); }
    }

    private void initView() {
        rvQuestion = findViewById( R.id.voice_rv_question );
        tvTitle = findViewById( R.id.voice_tv_title );
        sllAudioLayout = findViewById( R.id.voice_sll_audio );
        llOptLayout = findViewById( R.id.voice_ll_opt );
        tvRecordingBtn = findViewById( R.id.voice_tv_recording_btn );
        apbbPlayBackBtn = findViewById( R.id.voice_apbb_audio_play_back_btn );
        vVoicePrintLayout = findViewById( R.id.voice_ll_voice_print );
        ivVoicePrintImg = findViewById( R.id.voice_iv_voice_print_img );
        apbAudioBtn = findViewById( R.id.voice_apb_audio_btn );
        apbBtmAudioBtn = findViewById( R.id.voice_apb_bottom_audio_btn );
        cbCheckBtn = findViewById( R.id.voice_cb_check_btn );
        vNextBtn = findViewById( R.id.voice_ll_next_btn );
    }

    private FrameAnimation.FrameControl mVoicePrintCtrl;
    private void initData() {
//        apbAudioBtn.switchRoundRect();
//        try {
//            apbAudioBtn.setSpeechSynthesizer( mPresenter.getTts() );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        apbBtmAudioBtn.switchOval();
//        try {
//            apbBtmAudioBtn.setSpeechSynthesizer( mPresenter.getTts() );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        mVoicePrintCtrl = FrameAnimation
                .create()
                .time( 25 )
                .load( getContext(), "ic_play_recording_", 20 )
                .into( ivVoicePrintImg );

//        onCallSetLayoutManager( -1 );
        rvQuestion.setLayoutManager( new FlowLayoutManager() );
        rvQuestion.setAdapter( mAdapter = new ChinesePinYinAdapter( new ArrayList<>() ) );

        cbCheckBtn.setText( R.string.stringContinue );
        cbCheckBtn.setEnabled( true );
    }

    private void initListener() {
        tvRecordingBtn.setOnClickListener(v -> {
            mAdapter.switchNormalStatus();
            mPresenter.onStartRecording();
            switchStartUI( true );
        });

        vVoicePrintLayout.setOnClickListener(v -> {
            stopAll();
            mPresenter.onStopRecording();
//            switchEndUI();
        });

        cbCheckBtn.setOnClickListener(v -> check());
        vNextBtn.setOnClickListener(v -> check());

        mAdapter.setOnItemClickListener((adapter, view, itemData, position) -> {
            //
            Log.e("TAG", itemData.toString());
        });


        View.OnClickListener audioListener = v -> {
            apbbPlayBackBtn.stop();
            mPresenter.refreshSnail();
        };

        apbAudioBtn.setOnAudioPlayClickListener( audioListener );
        apbBtmAudioBtn.setOnAudioPlayClickListener( audioListener );
    }

    private void check() {
        stopAll();
        mPresenter.check();
    }

    private void stopAll() {
        if( apbAudioBtn != null ) apbAudioBtn.stopNow();
        if( apbBtmAudioBtn != null ) apbBtmAudioBtn.stopNow();
        if( apbbPlayBackBtn != null ) apbbPlayBackBtn.stop();
    }

    private void switchStartUI(boolean isRun) {
//        setRecordingBtnSize( Utils.dp2Px( getContext(), 61.5 ) );
        apbAudioBtn.setVisibility( View.VISIBLE );

        LinearLayout.LayoutParams lpQue = (LinearLayout.LayoutParams) rvQuestion.getLayoutParams();
        lpQue.gravity = Gravity.START;
        rvQuestion.setLayoutParams( lpQue );
//        rvQuestion.setX( 0 );

        tvTitle.setGravity( Gravity.START );

        tvRecordingBtn.setText( R.string.stringTouchStartRecording );

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) sllAudioLayout.getLayoutParams();
        lp.topMargin = Utils.dp2Px( getContext(), 24 );
        lp.leftMargin = lp.rightMargin = 0;
        sllAudioLayout.setLayoutParams( lp );
        sllAudioLayout.setBackgroundColor( Color.WHITE );

//        sllAudioLayout.setShadowRadius( 0 );
//        sllAudioLayout.setBackground( null );
//        sllAudioLayout.requestLayout();
        apbAudioBtn.setEnabled( !isRun );
        if( isRun ) {
            llOptLayout.setVisibility( View.GONE );
            vVoicePrintLayout.setVisibility( View.VISIBLE );
        }else {
            llOptLayout.setVisibility( View.VISIBLE );
            vVoicePrintLayout.setVisibility( View.GONE );
            apbbPlayBackBtn.setVisibility( View.GONE );
            apbBtmAudioBtn.setVisibility( View.GONE );
        }
//        llTextLayout.setGravity( Gravity.START );
        mVoicePrintCtrl.play( getContext() );

        vNextBtn.setVisibility( View.GONE );
        cbCheckBtn.setVisibility( View.GONE );
        stopAll();
    }

    private void switchEndUI() {
//        setRecordingBtnSize( Utils.dp2Px( getContext(), 50 ) );
//        sllAudioLayout.setShadowRadius( Utils.dp2Px( getContext(), 4 ) );
//        sllAudioLayout.setShadowOffsetX( Utils.dp2Px( getContext(), 4 ) );
//        sllAudioLayout.setShadowOffsetY( Utils.dp2Px( getContext(), 4 ) );
//        sllAudioLayout.setShadowColor( getResources().getColor( R.color.colorShadow ) );
//        sllAudioLayout.setBackgroundColor( Color.WHITE );
//        sllAudioLayout.requestLayout();

        LinearLayout.LayoutParams lpQue = (LinearLayout.LayoutParams) rvQuestion.getLayoutParams();
        lpQue.gravity = Gravity.CENTER_HORIZONTAL;
        rvQuestion.setLayoutParams( lpQue );
//        rvQuestion.setX( ( llQuestionLayout.getWidth() / 2F ) - ( rvQuestion.getWidth() / 2F ) );

        tvTitle.setGravity( Gravity.CENTER );

        tvRecordingBtn.setText( "" );
        tvRecordingBtn.setTranslationY( Utils.dp2Px( getContext(), 10 ) );

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) sllAudioLayout.getLayoutParams();
        lp.topMargin = Utils.dp2Px( getContext(), 49 );
        lp.leftMargin = lp.rightMargin = Utils.dp2Px( getContext(), 33 );
        sllAudioLayout.setLayoutParams( lp );
        llOptLayout.setVisibility( View.VISIBLE );
        apbbPlayBackBtn.setVisibility( View.VISIBLE );
        apbBtmAudioBtn.setVisibility( View.VISIBLE );
        apbAudioBtn.setVisibility( View.GONE );
        vVoicePrintLayout.setVisibility( View.GONE );
//        llTextLayout.setGravity( Gravity.CENTER );

        stopAll();

        switch( mPresenter.getAnswerType() ) {
            case Constant.AnswerType.FAST_REVIEW:
                vNextBtn.setVisibility( View.VISIBLE );
                cbCheckBtn.setVisibility( View.GONE );
                break;
            case Constant.AnswerType.ERROR_MAP:
                vNextBtn.setVisibility( View.GONE );
                cbCheckBtn.setVisibility( View.GONE );
                break;
                default:
                    cbCheckBtn.setVisibility( View.VISIBLE );
                    break;
        }
        LogUtil.e("switchEndUI");
    }

//    private void setRecordingBtnSize(int size) {
//        ViewGroup.LayoutParams lp = tvRecordingBtn.getLayoutParams();
//        lp.width = lp.height = size;
//        tvRecordingBtn.setLayoutParams( lp );
//    }

    @Override
    public void onCallArguments(@Nullable Bundle args) {
        super.onCallArguments(args);
        mPresenter.doArguments( args );
    }

//    @Override
//    public RecyclerView getRecyclerView() { return rvQuestion; }

    @Override
    public void onCallTitle(String title) { if( tvTitle != null ) tvTitle.setText( title ); }

    @Override
    public void onCallAudioUrl(String url) {
        try {
            apbAudioBtn.setAudio( url );
            apbAudioBtn.play();
            apbBtmAudioBtn.setAudio( url );
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCallOptionsLayout(int layout, OptionsEntity... opts) { }

    @Override
    public void onCallData(String[] pinyin, String[] data) {
        mAdapter.setChinesePinYin( pinyin, data );
    }

    @Override
    public void onCallStatus(List<Syll> statusList) {
        mAdapter.switchStatus( statusList );
    }

    @Override
    public void onEndRecording() {
        switchEndUI();
        apbbPlayBackBtn.setData( mPresenter.getSaveSpeechFilePath() );

    }

    @Override
    public void onResult(boolean result) {
        sllAudioLayout.setBackgroundResource(
                result ? R.color.colorCorrectBg : R.color.colorErrorBg
        );
    }

    @Override
    public void onCheckVisibility(int visibility) { cbCheckBtn.setVisibility( visibility ); }

    @Override
    public void onShowAudioBtn(boolean isShow) {
        apbAudioBtn.setVisibility( isShow ? View.VISIBLE : View.GONE );
    }

    @Override
    public void onCallSpeed(float speed) {
        apbAudioBtn.setSpeed( speed );
        apbBtmAudioBtn.setSpeed( speed );
    }

    @Override
    public void onPlay() {
        if( apbAudioBtn != null ) apbAudioBtn.performClick();
    }
}
