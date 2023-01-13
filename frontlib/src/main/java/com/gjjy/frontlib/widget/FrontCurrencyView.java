package com.gjjy.frontlib.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.ybear.ybcomponent.Utils;
import com.ybear.ybutils.utils.LogUtil;
import com.ybear.ybutils.utils.SysUtil;
import com.gjjy.frontlib.R;

public class FrontCurrencyView extends PopupWindow {
    private final Context mContext;
    private final ImageView ivArrow;

    public FrontCurrencyView(Context context) {
        super( View.inflate( context, R.layout.popup_front_currency, null ) );
        mContext = context;
        ivArrow = getContentView().findViewById( R.id.popup_front_currency_iv_arrow );
        setOutsideTouchable( true );
    }

    public void showAsDropDown(View anchor, float x) {
        setWidth( Utils.dp2Px( mContext, 270 ) );
        setHeight( Utils.dp2Px( mContext, 144 ) );
        float sW = SysUtil.getScreenWidth( mContext ) - getWidth();
        ivArrow.setX( x - sW );
        LogUtil.e("showAsDropDown -> ivArrow_X:" + ivArrow.getX() + " | x:" + x + " | sW:" + sW);
        super.showAsDropDown( anchor );
    }
}
