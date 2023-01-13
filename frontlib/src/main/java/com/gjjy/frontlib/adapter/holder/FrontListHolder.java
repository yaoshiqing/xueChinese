package com.gjjy.frontlib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gjjy.frontlib.R;
import com.gjjy.frontlib.widget.FrontTestButton;
import com.ybear.ybcomponent.base.adapter.BaseViewHolder;
import com.ybear.ybcomponent.widget.MaskImageView;
import com.ybear.ybutils.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FrontListHolder extends BaseViewHolder {
    private final List<ChildItemHolder> mChildItemHolderList = new ArrayList<>();
    private final FrontTestButton htbTestBtn;

    public FrontListHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        ViewGroup vg = (ViewGroup) getItemView();
        htbTestBtn = vg.findViewById( R.id.item_front_list_htb_test_btn );

        for( int i = 0; i < vg.getChildCount(); i++ ) {
            View v = vg.getChildAt( i );
            if( v instanceof FrontTestButton ) continue;
            mChildItemHolderList.add( createChildItemHolder( v ) );
        }
    }

    @Nullable
    public ChildItemHolder getChildItemHolder(int index) {
        if( index < 0 || index >= mChildItemHolderList.size() ) return null;
        return mChildItemHolderList.get( index );
    }

    private ChildItemHolder createChildItemHolder(View itemView) {
        ChildItemHolder cHolder = new ChildItemHolder( itemView );
        int iconWidth = Utils.dp2Px( getContext(), 100 );
        int iconHeight = Utils.dp2Px( getContext(), 92 );
        MaskImageView mivImg = cHolder.getImg();
        mivImg.setForegroundMask( R.drawable.ic_front_child_ba_top, iconWidth, iconHeight );
        mivImg.setBackgroundMask( R.drawable.ic_front_child_ba_btm, iconWidth, iconHeight );
        mivImg.setStartOfAngle( 45 );
        mivImg.setDuration( 1000 );
        return cHolder;
    }

    public FrontTestButton getTestBtn() { return htbTestBtn; }
}
