package com.gjjy.usercenterlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybcomponent.widget.shape.ShapeLinearLayout;
import com.ybear.ybcomponent.widget.shape.ShapeTextView;
import com.gjjy.usercenterlib.R;

public class RewardItemView extends ShapeLinearLayout {
    private TextView tvTitle, tvNumber, tvContent;
    private int mType;

    public RewardItemView(Context context) {
        this(context, null);
    }

    public RewardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RewardItemView);
        mType = typedArray.getInt( R.styleable.RewardItemView_rivType, 0 );
        typedArray.recycle();

        init();
    }

    private void init() {
        setOrientation( HORIZONTAL );
        setMinimumHeight( Utils.dp2Px( getContext(), 98 ) );
        setRadius( Utils.dp2Px( getContext(), 10 ) );
//        setBorderSize( Utils.dp2Px( getContext(), 2 ) );
//        setBorderColor( getResources().getColor( R.color.colorEF ) );

        addTextLayout();
        ImageView ivImg = addImgView();

        int iconResId = 0;
        int titleResId = 0;
        int contentResId = 0;
        int bgColor = 0;
        switch( mType ) {
            case 0:
                iconResId = R.drawable.ic_reward_item_heart_icon;
                titleResId = R.string.stringRewardItemHeartTitle;
                contentResId = R.string.stringRewardItemHeartContent;
                bgColor = Color.parseColor( "#3AAAFF" );
                break;
            case 1:
                iconResId = R.drawable.ic_reward_item_lightning_icon;
                titleResId = R.string.stringRewardItemLightningTitle;
                contentResId = R.string.stringRewardItemLightningContent;
                bgColor = Color.parseColor( "#2DD88A" );
                break;
            case 2:
                iconResId = R.drawable.ic_reward_item_xp_icon;
                titleResId = R.string.stringRewardItemXPTitle;
                contentResId = R.string.stringRewardItemXPContent;
                bgColor = Color.parseColor( "#FF8636" );
                break;
        }
        if( iconResId != 0 ) ivImg.setImageResource( iconResId );
        if( titleResId != 0 ) tvTitle.setText( titleResId );
        if( contentResId != 0 ) tvContent.setText( contentResId );
        if( bgColor != 0 ) setBackgroundColor( bgColor );
    }

//    public void setIcon(@DrawableRes int resId) { ivImg.setImageResource( resId ); }

//    public void setTitle(@StringRes int resId) { tvTitle.setText( resId ); }
//    public void setTitle(CharSequence s) { tvTitle.setText( s ); }

    public void setNumber(int num) {
        tvNumber.setText( String.valueOf( num ) );
    }

//    public void setContent(@StringRes int resId) { tvContent.setText( resId ); }
//    public void setContent(CharSequence s) { tvContent.setText( s ); }

    private ImageView addImgView() {
        ImageView iv = new ImageView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 20 ) );
        lp.gravity = Gravity.CENTER_VERTICAL;
        iv.setLayoutParams( lp );
        addView( iv );
        return iv;
    }

    private void addTextLayout() {
        LinearLayout ll = new LinearLayout( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = Utils.dp2Px( getContext(), 20 );
        lp.weight = 1;
        lp.setMargins( margin, margin, 0, margin );
        lp.gravity = Gravity.CENTER_VERTICAL;
//        lp.setMarginStart( Utils.dp2Px( getContext(), 18 ) );
        ll.setLayoutParams( lp );
        ll.setOrientation( VERTICAL );

        ll.addView( createTitleLayout() );
        ll.addView( tvContent = createContent() );
        addView( ll );
    }


    private LinearLayout createTitleLayout() {
        LinearLayout ll = new LinearLayout( getContext() );
        ll.setOrientation( HORIZONTAL );
        ll.setGravity( Gravity.CENTER_VERTICAL );

        tvTitle = createTitle();
        tvNumber = createNumber();
        if( mType == 2 ) {
            ll.addView( tvNumber );
            ll.addView( tvTitle );
            return ll;
        }
        ll.addView( tvTitle );
        ll.addView( tvNumber );
        return ll;
    }

    private TextView createTitle() {
        TextView tv = new TextView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 10 ) );
        tv.setLayoutParams( lp );

        tv.setTextSize( 16 );
        tv.setTextColor( Color.WHITE );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        return tv;
    }

    private ShapeTextView createNumber() {
        ShapeTextView tv = new ShapeTextView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMarginEnd( Utils.dp2Px( getContext(), 10 ) );
        tv.setLayoutParams( lp );

        tv.setMinWidth( Utils.dp2Px( getContext(), 43 ) );
        tv.setTextSize( 18 );
        tv.setTextColor( Color.WHITE );
        tv.setTypeface( Typeface.defaultFromStyle( Typeface.BOLD ) );
        tv.setBackgroundResource( R.color.colorItemAchievementNumberBg );
        tv.setGravity( Gravity.CENTER );
        tv.setRadius( Utils.dp2Px( getContext(), 10 ) );
        int pLR = Utils.dp2Px( getContext(), 11 );
//        int pTB = Utils.dp2Px( getContext(), 4 );
        tv.setPadding( pLR, 0, pLR, 0 );
        return tv;
    }

    private TextView createContent() {
        TextView tv = new TextView( getContext() );
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.topMargin = Utils.dp2Px( getContext(), 12 );
        tv.setLayoutParams( lp );

        tv.setTextSize( 14 );
        tv.setTextColor( Color.WHITE );
        return tv;
    }
}