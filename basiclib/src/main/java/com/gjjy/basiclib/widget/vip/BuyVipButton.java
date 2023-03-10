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
     ??????????????????
     @param hotPriceStatus  1??????????????????0??????????????????-1?????????
     */
    private void showHotPrice(int hotPriceStatus) {
        if( tvFlag == null ) return;

        switch( hotPriceStatus ) {
            case 1:     //????????????
                tvFlag.setText( getResources().getString( R.string.stringHot ) );
                break;
            case 0:     //????????????
                if( TextUtils.isEmpty( mIntroductoryPrice ) ) {
                    tvFlag.setVisibility( INVISIBLE );
                    return;
                }
                //????????????
                String rebate = String.format(
                        Locale.getDefault(),
                        "%.0f",
                        Math.ceil( ( ObjUtils.parseDouble( mIntroductoryPrice ) / ObjUtils.parseDouble( mPrice ) ) * 100D )
                );
                tvFlag.setText(
                        ( rebate + getResources().getString( R.string.stringPriceRebate ) )
                );
                break;
            default:    //??????
                tvFlag.setVisibility( INVISIBLE );
                return;
        }
        //????????? or ?????????
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

        /* ???x??????$?????? or $??????(?????????) */
        setPriceInfo( data );

        LinearLayout.LayoutParams lpInDate = (LinearLayout.LayoutParams) tvInDate.getLayoutParams();

        //????????????
        if( isHaveDiscount ) {
            //?????????????????????
            tvPriceView.setText( mIntroductoryPrice );
            tvPriceUnit.setTextSize( 20 );
            if( lpInDate != null ) lpInDate.setMarginStart( Utils.dp2Px( getContext(), 29 ) );
        }else {
            //?????????????????????
            tvPriceView.setText( mPrice );
            tvPriceUnit.setTextSize( 16 );
            if( lpInDate != null ) lpInDate.setMarginStart( 0 );
        }
        //??????(?????????) or ???????????????
        setPricePerMonth(
                isDiscountButton ? data.getPricePerMonth() : getPriceInfoText( data ),
                data.isFirstDiscount()
        );
        tvPriceView.setVisibility( VISIBLE );
    }

    /**
     ?????????x??????????????????
     @param data        ?????????????????????
     @return            ????????????    eg:???????????? VND??? 57362.50
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
            case SkuUtils.DateSymbol.YEAR:      //???
                firstDisStr = res.getString( R.string.stringFirstDiscountOfYear );
                break;
            case SkuUtils.DateSymbol.MONTH:     //???
                firstDisStr = res.getString( R.string.stringFirstDiscountOfMonth );
                firstDisStr = String.format( firstDisStr, data.getSubscriptionDate() );
                break;
            case SkuUtils.DateSymbol.WEEK:      //??????
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
            //?????? ??????(?????????) or ??????????????????
            String priceInfo = getPriceInfoText( data );

            if( TextUtils.isEmpty( priceInfo ) ) {
                tvPriceInfo.setVisibility( GONE );
                return;
            }

            //??????/???????????????
            setStrikeThrough( tvPriceInfo, !isFirstDiscount );
            text = priceInfo;
        }else {
            text = data.getPricePerMonth();
        }
        tvPriceInfo.setText( text );
        //????????????
        tvPriceInfo.setVisibility(
                !TextUtils.isEmpty( text ) && data.isShowPriceInfo() ? VISIBLE : GONE
        );
    }

    private void setStrikeThrough(TextView tv, boolean isStrikeThrough) {
        TextPaint tp = tv.getPaint();
        //?????????????????????
        if( isStrikeThrough ) {
            //???????????????
            tp.setFlags( tp.getFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }else {
            //???????????????
            tp.setFlags( tp.getFlags() & ( ~Paint.STRIKE_THRU_TEXT_FLAG ) );
        }
    }

    private void setPricePerMonth(String s, boolean isFirstDiscount) {
        //??????/???????????????
        setStrikeThrough( tvPricePerMonth, !isFirstDiscount );
        tvPricePerMonth.setText( s );
        tvPricePerMonth.setVisibility( TextUtils.isEmpty( s ) ? GONE : VISIBLE );
    }
}
