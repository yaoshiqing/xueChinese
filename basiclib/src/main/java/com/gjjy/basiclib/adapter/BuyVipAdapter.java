package com.gjjy.basiclib.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ybear.ybcomponent.base.adapter.BaseMultiSelectAdapter;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybutils.utils.Utils;
import com.gjjy.basiclib.entity.BuyVipOptionEntity;
import com.gjjy.basiclib.widget.vip.BuyVipButton;

import java.util.List;

/**
 购买会员列表适配器
 */
public class BuyVipAdapter extends BaseMultiSelectAdapter<BuyVipOptionEntity, BuyVipAdapter.BuyVipHolder>{
    private final boolean isDiscount;

    public BuyVipAdapter(@NonNull List<BuyVipOptionEntity> list, boolean isDiscount) {
        super( list );
        this.isDiscount = isDiscount;
        setEnableTouchStyle( false );
        setMinMultiSelectCount( 0 );
        setMaxMultiSelectCount( 1 );
    }

    @NonNull
    @Override
    public BuyVipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BuyVipHolder( new BuyVipButton( parent.getContext() ), isDiscount );
    }

    @Override
    public void onBindViewHolder(@NonNull BuyVipHolder holder, int position, boolean selectStatus) {
        BuyVipButton btn = holder.getBuyVipButton();
        btn.setTouchChangedSelected( false );
        btn.setData( getItemData( position ) );
    }

    @Override
    public boolean onInitMultiSelect(int i) {
        return false;
    }

    @Override
    public void onMultiSelectChange(RecyclerView.Adapter<BuyVipHolder> adapter,
                                    @Nullable BuyVipHolder holder,
                                    int position, boolean isChecked, boolean fromUser) {
        if( holder != null ) holder.getBuyVipButton().setSelected( isChecked );
    }

    public static class BuyVipHolder extends BaseViewHolder {
        private final BuyVipButton bvbBuyVip;

        public BuyVipHolder(@NonNull View itemView, boolean isDiscount) {
            super( itemView );
            bvbBuyVip = (BuyVipButton) itemView;

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Utils.dp2Px( getContext(), isDiscount ? 103 : 86 )
            );
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    Utils.dp2Px( getContext(), 100 ),
//                    Utils.dp2Px( getContext(), 120 )
//            );
//            int marginDP = Utils.dp2Px( getContext(), 9 );
//            lp.setMarginStart( marginDP );
//            lp.setMarginEnd( marginDP );
            bvbBuyVip.setLayoutParams( lp );
            bvbBuyVip.setDiscountButton( isDiscount );
        }

        public BuyVipButton getBuyVipButton() { return bvbBuyVip; }
    }
}
