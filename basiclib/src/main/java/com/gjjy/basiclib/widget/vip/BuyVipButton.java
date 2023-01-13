package com.gjjy.basiclib.widget.vip;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gjjy.basiclib.R;
import com.gjjy.basiclib.entity.BuyVipOptionEntity;
import com.gjjy.googlebillinglib.SkuUtils;
import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.shape.ShapeLinearLayout;
import com.ybear.ybutils.utils.ObjUtils;

import java.util.Locale;

public class BuyVipButton extends FrameLayout{
    private ShapeLinearLayout sllButtonLayout;
    private TextView tvPricePerMonth;
    private TextView tvFlag;
    private TextView tvInDate;
    private TextView tvPriceUnit;
    private TextView tvPriceView;
    private TextView tvPriceInfo;

    private String mPrice;
    private String mIntroductoryPrice;

    private boolean isDiscountButton = false;
    private boolean isSelected = false;
    private boolean isCancelTouch = true;
    private boolean isTouchChangedSelected = true;

    public BuyVipButton(Context context) {
        this( context, null );
    }

    public BuyVipButton(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public BuyVipButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );
        init();
        initView();

        setSelected( false );
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
//        setMeasuredDimension(
//                Utils.dp2Px( getContext(), 100 ),
//                Utils.dp2Px( getContext(), 120 )
//        );
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if( isSelected && !isCancelTouch ) return super.dispatchTouchEvent( ev );

        if( isTouchChangedSelected && ev.getAction() == MotionEvent.ACTION_UP ) {
            setSelected( !isSelected );
        }
        return super.dispatchTouchEvent( ev );
    }

    private void init() {
        setFocusable( true );
        setClickable( true );
    }

    private void initView() {
        sllButtonLayout = (ShapeLinearLayout) View.inflate(
                getContext(), R.layout.block_buy_vip_button, null
        );
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );

        lp.topMargin = Utils.dp2Px( getContext(), 9 );
        addView( sllButtonLayout, lp );

        addView( tvFlag = createFlagView() );

        tvPricePerMonth = sllButtonLayout.findViewById( R.id.buy_vip_button_tv_price_per_month );
        tvInDate = sllButtonLayout.findViewById( R.id.buy_vip_button_tv_date );
        tvPriceUnit = sllButtonLayout.findViewById( R.id.buy_vip_button_tv_price_unit );
        tvPriceView = sllButtonLayout.findViewById( R.id.buy_vip_button_tv_price );
        tvPriceInfo = sllButtonLayout.findViewById( R.id.buy_vip_button_tv_original_price );
    }

    public BuyVipButton setTouchChangedSelected(boolean touchChangedSelected) {
        isTouchChangedSelected = touchChangedSelected;
        return this;
    }

    private TextView createFlagView() {
        TextView tv = new TextView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams( lp );
        tv.setTextColor( Color.WHITE );
        tv.setGravity( Gravity.CENTER );
        tv.setRotation( -45 );
        return tv;
    }
    
    private void setFlagIcon(boolean isDiscountButton) {
        if( tvFlag == null ) return;
        tvFlag.setTextSize( isDiscountButton ? 11 : 9 );
        tvFlag.setBackgroundResource(
                isDiscountButton ? R.drawable.ic_buy_vip_flag : R.drawable.ic_buy_vip_flag_of_small
        );
        tvFlag.setTranslationX( Utils.dp2Px( getContext(), isDiscountButton ? -20 : -18 ) );
        tvFlag.setTranslationY( Utils.dp2Px( getContext(), isDiscountButton ? -10 : -6 ) );
    }

    @Override
    public void setSelected(boolean isSelected) {
        if( this.isSelected == isSelected ) return;
        this.isSelected = isSelected;
        sllButtonLayout.setBorderColor( getResources().getColor(
                isSelected ? R.color.colorBuyVipMain : R.color.colorEF )
        );
        sllButtonLayout.postInvalidate();
    }

    @Override
    public boolean isSelected() { return isSelected; }

    public boolean isCancelTouch() { return isCancelTouch; }
    public void setCancelTouch(boolean cancelTouch) { isCancelTouch = cancelTouch; }

    public void setDiscountButton(boolean discountButton) {
        isDiscountButton = discountButton;
    }

    public void setData(@Nullable BuyVipOptionEntity data) {
        if( data == null ) return;
        setInDate( data.getInDate() );
        setPrice( data );

        showHotPrice( data.isHotPrice() ? 1 : data.isDiscount() ? 0 : -1 );
        setSelected( data.isHotPrice() );
    }

    /**
     展示热门标签
     @param hotPriceStatus  1：展示热门，0：展示特惠，-1：隐藏
     */
    private void showHotPrice(int hotPriceStatus) {
        if( tvFlag == null ) return;

        switch( hotPriceStatus ) {
            case 1:     //展示热门
                tvFlag.setText( getResources().getString( R.string.stringHot ) );
                break;
            case 0:     //展示特惠
                if( TextUtils.isEmpty( mIntroductoryPrice ) ) {
                    tvFlag.setVisibility( INVISIBLE );
                    return;
                }
                //折扣力度
                String rebate = String.format(
                        Locale.getDefault(),
                        "%.0f",
                        Math.ceil( ( ObjUtils.parseDouble( mIntroductoryPrice ) / ObjUtils.parseDouble( mPrice ) ) * 100D )
                );
                tvFlag.setText(
                        ( rebate + getResources().getString( R.string.stringPriceRebate ) )
                );
                break;
            default:    //隐藏
                tvFlag.setVisibility( INVISIBLE );
                return;
        }
        //大标签 or 小标签
        setFlagIcon( isDiscountButton );
        tvFlag.setVisibility( VISIBLE );
    }

    private void setInDate(String s) {
        if( TextUtils.isEmpty( s ) ) {
            tvInDate.setVisibility( INVISIBLE );
            return;
        }
        tvInDate.setText( s );
        tvInDate.setVisibility( VISIBLE );
    }

    private void setPrice(@Nullable BuyVipOptionEntity data) {
        if( data == null ) return;
        String unit = data.getPriceUnit();
        mPrice = data.getPrice();
        mIntroductoryPrice = data.getIntroductoryPrice();

        if( TextUtils.isEmpty( unit ) ) {
            tvPriceUnit.setVisibility( GONE );
        }else {
            tvPriceUnit.setText( unit );
        }

        boolean isHaveDiscount = !TextUtils.isEmpty( mIntroductoryPrice );

        /* 次x恢复$价格 or $价格(删除线) */
        setPriceInfo( data );

        LinearLayout.LayoutParams lpInDate = (LinearLayout.LayoutParams) tvInDate.getLayoutParams();

        //有折扣价
        if( isHaveDiscount ) {
            //价格区展示特惠
            tvPriceView.setText( mIntroductoryPrice );
            tvPriceUnit.setTextSize( 20 );
            if( lpInDate != null ) lpInDate.setMarginStart( Utils.dp2Px( getContext(), 29 ) );
        }else {
            //价格区展示原价
            tvPriceView.setText( mPrice );
            tvPriceUnit.setTextSize( 16 );
            if( lpInDate != null ) lpInDate.setMarginStart( 0 );
        }
        //原价(删除线) or 每月多少钱
        setPricePerMonth(
                isDiscountButton ? data.getPricePerMonth() : getPriceInfoText( data ),
                data.isFirstDiscount()
        );
        tvPriceView.setVisibility( VISIBLE );
    }

    /**
     获取次x恢复价格文案
     @param data        会员的商品数据
     @return            价格文案    eg:次年恢复 VND₫ 57362.50
     */
    private String getPriceInfoText(BuyVipOptionEntity data) {
        String fullPrice = data.getPriceUnit() + " " + data.getPrice();
        if( TextUtils.isEmpty( data.getIntroductoryPrice() ) ) {
            return null;
        }
        if( !data.isFirstDiscount() ) {
            return fullPrice;
        }

        Resources res = getResources();
        String firstDisStr = null;
        switch( data.getSubscriptionDateSymbol() ) {
            case SkuUtils.DateSymbol.YEAR:      //年
                firstDisStr = res.getString( R.string.stringFirstDiscountOfYear );
                break;
            case SkuUtils.DateSymbol.MONTH:     //月
                firstDisStr = res.getString( R.string.stringFirstDiscountOfMonth );
                firstDisStr = String.format( firstDisStr, data.getSubscriptionDate() );
                break;
            case SkuUtils.DateSymbol.WEEK:      //星期
                firstDisStr = res.getString( R.string.stringFirstDiscountOfWeek );
                break;
        }
        return firstDisStr + " " + fullPrice;
    }

    private void setPriceInfo(BuyVipOptionEntity data) {
        if( data == null ) {
            tvPriceInfo.setVisibility( GONE );
            return;
        }
        String text;
        if( isDiscountButton ) {
            boolean isFirstDiscount = data.isFirstDiscount();
            //展示 原价(删除线) or 首次优惠原价
            String priceInfo = getPriceInfoText( data );

            if( TextUtils.isEmpty( priceInfo ) ) {
                tvPriceInfo.setVisibility( GONE );
                return;
            }

            //添加/移除删除线
            setStrikeThrough( tvPriceInfo, !isFirstDiscount );
            text = priceInfo;
        }else {
            text = data.getPricePerMonth();
        }
        tvPriceInfo.setText( text );
        //是否展示
        tvPriceInfo.setVisibility(
                !TextUtils.isEmpty( text ) && data.isShowPriceInfo() ? VISIBLE : GONE
        );
    }

    private void setStrikeThrough(TextView tv, boolean isStrikeThrough) {
        TextPaint tp = tv.getPaint();
        //是否添加删除线
        if( isStrikeThrough ) {
            //添加删除线
            tp.setFlags( tp.getFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }else {
            //取消删除线
            tp.setFlags( tp.getFlags() & ( ~Paint.STRIKE_THRU_TEXT_FLAG ) );
        }
    }

    private void setPricePerMonth(String s, boolean isFirstDiscount) {
        //添加/移除删除线
        setStrikeThrough( tvPricePerMonth, !isFirstDiscount );
        tvPricePerMonth.setText( s );
        tvPricePerMonth.setVisibility( TextUtils.isEmpty( s ) ? GONE : VISIBLE );
    }
}
