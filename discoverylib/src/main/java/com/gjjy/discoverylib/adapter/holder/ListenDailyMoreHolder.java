package com.gjjy.discoverylib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.gjjy.discoverylib.R;

public class ListenDailyMoreHolder extends BaseFindHolder {
    public ListenDailyMoreHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        View v = getItemView();

        setNewFlag( v.findViewById( R.id.listen_daily_detail_iv_new_flag ) );
        setImg( v.findViewById( R.id.listen_daily_detail_iv_img ) );
        setToViewCount( v.findViewById( R.id.listen_daily_detail_tv_to_view_count ) );
        setTitle( v.findViewById( R.id.listen_daily_detail_tv_title ) );
        setContent( v.findViewById( R.id.listen_daily_detail_tv_content ) );
        setClassifyTag( v.findViewById( R.id.listen_daily_detail_tv_classify_tag ) );

    }
}
