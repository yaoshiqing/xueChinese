package com.gjjy.usercenterlib.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.gjjy.basiclib.buried_point.PageName;
import com.gjjy.basiclib.ui.activity.BaseActivity;
import com.gjjy.basiclib.widget.Toolbar;
import com.gjjy.usercenterlib.R;
import com.gjjy.usercenterlib.StartUtil;
import com.gjjy.usercenterlib.adapter.SelectWeekDaysAdapter;
import com.gjjy.usercenterlib.adapter.holder.SelectWeekDaysHolder;
import com.gjjy.usercenterlib.mvp.presenter.LearningReminderPresenter;
import com.gjjy.usercenterlib.mvp.view.LearningReminderView;
import com.contrarywind.view.WheelView;
import com.ybear.mvp.annotations.Presenter;
import com.ybear.ybcomponent.base.adapter.listener.OnMultiSelectChangeAdapter;
import com.ybear.ybutils.utils.AnimUtil;
import com.ybear.ybutils.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习通知页面
 */
@Route(path = "/userCenter/learningReminderActivity")
public class LearningReminderActivity extends BaseActivity implements LearningReminderView {
    @Presenter
    private LearningReminderPresenter mPresenter;
    private Toolbar tbToolbar;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swiReminderStatus;
    private TextView tvSelectTime;
    private RecyclerView rvWeekDays;
    private WheelView wvHour;
    private WheelView wvMinute;
    private View vWeekDays;
    private View vTimerPacker;

    private SelectWeekDaysAdapter mWeekAdapter;

    List<String> mHourList = new ArrayList<>();
    List<String> mMinuteList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_learning_reminder );
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == com.gjjy.basiclib.utils.StartUtil.REQUEST_CODE_LOGIN ) {
            if( resultCode == RESULT_OK ) {
                mPresenter.reqRemindDetail( false );
                mPresenter.setEnableRemind( true );
            }
        }
    }

    private void initView() {
        tbToolbar = findViewById( R.id.learning_reminder_tb_toolbar );
        swiReminderStatus = findViewById( R.id.learning_reminder_swi_reminder_status );
        tvSelectTime = findViewById( R.id.time_packer_tv_time );
        rvWeekDays = findViewById( R.id.learning_reminder_rv_select_week_days );
        wvHour = findViewById( R.id.time_packer_wv_hour );
        wvMinute = findViewById( R.id.time_packer_wv_minute );
        vWeekDays = findViewById( R.id.learning_reminder_sll_week_days );
        vTimerPacker = findViewById( R.id.learning_reminder_sll_time_packer );

        int outColor = getResources().getColor( R.color.colorWheelOut );


        wvHour.setItemsVisibleCount( 5 );
        wvHour.setTextSize( 17 );
        wvHour.setTextColorOut( outColor );
//        wvHour.setGravity( Gravity.RIGHT );

        wvMinute.setItemsVisibleCount( 5 );
        wvMinute.setTextSize( 17 );
        wvMinute.setTextColorOut( outColor );
//        wvMinute.setGravity( Gravity.LEFT );
    }

    private void initData() {
        setStatusBarHeight( R.id.toolbar_height_space );

        for (int i = 0; i < 24; i++) { mHourList.add( ( i <= 9 ? "0" : "" ) + i ); }
        for (int i = 0; i < 60; i++) {
            mMinuteList.add( i < mHourList.size() ? mHourList.get( i ) : String.valueOf( i ) );
        }

        wvHour.setAdapter( new ArrayWheelAdapter<>( mHourList ) );
        wvMinute.setAdapter( new ArrayWheelAdapter<>( mMinuteList ) );

        wvHour.setCurrentItem( 20 );
        wvMinute.setCurrentItem( 0 );

        List<SelectWeekDaysAdapter.ItemData> mDataList = new ArrayList<>();

        for (int id : new int[] { R.string.stringSun, R.string.stringMon, R.string.stringTue,
                R.string.stringWed, R.string.stringThu, R.string.stringFri,  R.string.stringSat } ) {
            mDataList.add( new SelectWeekDaysAdapter
                    .ItemData()
                    .setContent( id )
            );
        }

        mWeekAdapter = new SelectWeekDaysAdapter( mDataList );
        rvWeekDays.setLayoutManager(
                new GridLayoutManager( getContext(), 4 )
        );
        rvWeekDays.setAdapter( mWeekAdapter );
    }

    private void initListener() {
        tbToolbar.setOnClickBackBtnListener(v -> finish());

        //切换通知状态
        swiReminderStatus.setOnCheckedChangeListener((buttonView, isChecked) ->
                mPresenter.setEnableRemind( isChecked )
        );

        //日期
        mWeekAdapter.setOnMultiSelectChangeListener(new OnMultiSelectChangeAdapter<SelectWeekDaysHolder>() {
            @Override
            public void onMultiSelectChange(RecyclerView.Adapter<SelectWeekDaysHolder> adapter,
                                            @Nullable SelectWeekDaysHolder holder,
                                            int position, boolean isChecked, boolean fromUser) {
                super.onMultiSelectChange(adapter, holder, position, isChecked, fromUser);
                mPresenter.setWeek( position, isChecked );
            }
        });

        //小时选择时间监听器
        wvHour.setOnItemSelectedListener(index -> notifyTime());
        //分钟选择时间监听器
        wvMinute.setOnItemSelectedListener(index -> notifyTime());
    }

    /**
     * 刷新当前选择的时间
     */
    private void notifyTime() {
        mPresenter.setTime(
                mHourList.get( wvHour.getCurrentItem() ),
                mMinuteList.get( wvMinute.getCurrentItem() )
        );
        tvSelectTime.setText( mPresenter.getTime() );
    }

    @Override
    public void onCallOpenRemindError() {
        showRemindOfLoginDialog(v -> StartUtil.startLoginActivity(
                getActivity(),
                PageName.LOGIN,
                true
        ));
    }

    @Override
    public void onCallIsEnableRemind(boolean enable) {
        LogUtil.e("onCallIsEnableRemind:" + enable);
        if( swiReminderStatus.isChecked() != enable ) {
            swiReminderStatus.setChecked( enable );
            swiReminderStatus.setSelected( enable );
        }

        int vis = enable ? View.VISIBLE : View.GONE;

        if( vWeekDays.getVisibility() != vis ) {
            AnimUtil.setAlphaAnimator(100, animator ->
                    vWeekDays.setVisibility( vis ), vWeekDays
            );
        }
        if( vTimerPacker.getVisibility() != vis ) {
            AnimUtil.setAlphaAnimator(100, animator ->
                    vTimerPacker.setVisibility( vis ), vTimerPacker
            );
        }
    }

    @Override
    public void onCallTime(int hour, int minute) {
        wvHour.setCurrentItem( hour );
        wvMinute.setCurrentItem( minute );
        notifyTime();
    }

    @Override
    public void onCallWeekStatus(boolean[] weekStatus) {
        for (int i = 0; i < mWeekAdapter.getItemCount(); i++) {
            SelectWeekDaysAdapter.ItemData item = mWeekAdapter.getItemData( i );
            if( item != null ) item.setChecked( weekStatus[ i ] );
        }
        mWeekAdapter.notifyDataSetChanged();
    }
}
