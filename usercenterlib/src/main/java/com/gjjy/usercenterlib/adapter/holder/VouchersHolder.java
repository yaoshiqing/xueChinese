package com.gjjy.usercenterlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.gjjy.usercenterlib.R;

public class VouchersHolder extends BaseViewHolder {
    private final ImageView ivTime;
    private final ImageView ivDayVip;
    private final TextView tvRewardsStack;
    private final TextView tvUseNowBtn;
    private final LinearLayout llInUseLayout;
    private final TextView tvTimeInterval;
    private final TextView tvTimeLeft;

    public VouchersHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);

        ivTime = findViewById( R.id.item_vouchers_iv_time );
        ivDayVip = findViewById( R.id.item_vouchers_iv_day_vip );
        tvRewardsStack = findViewById( R.id.item_vouchers_tv_rewards_stack );
        tvUseNowBtn = findViewById( R.id.item_vouchers_tv_use_now_btn );
        llInUseLayout = findViewById( R.id.item_vouchers_ll_in_use );
        tvTimeInterval = findViewById( R.id.item_vouchers_tv_time_interval );
        tvTimeLeft = findViewById( R.id.item_vouchers_tv_time_left );
    }

    public ImageView getTime() { return ivTime; }

    public TextView getRewardsStack() { return tvRewardsStack; }

    public TextView getUseNowBtn() { return tvUseNowBtn; }

    public LinearLayout getInUseLayout() { return llInUseLayout; }

    public TextView getTimeInterval() { return tvTimeInterval; }

    public TextView getTimeLeft() { return tvTimeLeft; }

    public void switchUnusedType() {
        tvTimeInterval.setVisibility( View.VISIBLE );
        tvUseNowBtn.setVisibility( View.VISIBLE );
        llInUseLayout.setVisibility( View.GONE );
        ivDayVip.setImageResource( R.drawable.ic_item_vouchers_unused_dayvip );
        tvRewardsStack.setTextColor(
                tvRewardsStack.getResources().getColor( R.color.color66 )
        );
        getItemView().setBackgroundResource( R.drawable.ic_item_vouchers_unused_bg );
    }

    public void switchInUseType(boolean haveDay) {
        switchUnusedType();
        tvTimeInterval.setVisibility( View.GONE );
        tvUseNowBtn.setVisibility( View.GONE );

        FrameLayout.LayoutParams lp =  (FrameLayout.LayoutParams) llInUseLayout.getLayoutParams();
        lp.setMarginEnd( Utils.dp2Px( getContext(), haveDay ? 25 : 40 ) );
        llInUseLayout.setVisibility( View.VISIBLE );
    }

    public void switchExpiredType() {
        tvTimeInterval.setVisibility( View.GONE );
        tvUseNowBtn.setVisibility( View.GONE );
        llInUseLayout.setVisibility( View.GONE );
        ivDayVip.setImageResource( R.drawable.ic_item_vouchers_expired_dayvip );
        tvRewardsStack.setTextColor(
                tvRewardsStack.getResources().getColor( R.color.colorVouchersExpired )
        );
        getItemView().setBackgroundResource( R.drawable.ic_item_vouchers_expired_bg );
    }

    public void switchFreeExpiredType() {
        switchExpiredType();
        getItemView().setBackgroundResource( R.drawable.ic_item_vouchers_free_expired_bg );
    }
}
