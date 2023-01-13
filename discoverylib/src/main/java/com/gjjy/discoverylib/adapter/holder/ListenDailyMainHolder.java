package com.gjjy.discoverylib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.gjjy.discoverylib.R;

public class ListenDailyMainHolder extends BaseFindHolder {

    public ListenDailyMainHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        View v = getItemView();

        setNewFlag( v.findViewById( R.id.listen_daily_iv_new_flag ) );
        setImg( v.findViewById( R.id.listen_daily_iv_img ) );
        setToViewCount( v.findViewById( R.id.listen_daily_tv_to_view_count ) );
        setTitle( v.findViewById( R.id.listen_daily_tv_title ) );
        setClassifyTag( v.findViewById( R.id.listen_daily_tv_classify_tag ) );

    }
}