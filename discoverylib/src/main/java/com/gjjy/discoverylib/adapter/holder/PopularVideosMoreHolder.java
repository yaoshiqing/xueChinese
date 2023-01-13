package com.gjjy.discoverylib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.gjjy.discoverylib.R;

public class PopularVideosMoreHolder extends BaseFindHolder {
    public PopularVideosMoreHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        View v = getItemView();

        setNewFlag( v.findViewById( R.id.popular_videos_iv_new_flag ) );
        setImg( v.findViewById( R.id.popular_videos_iv_img ) );
        setToViewCount( v.findViewById( R.id.popular_videos_tv_to_view_count ) );
        setTitle( v.findViewById( R.id.popular_videos_tv_title ) );
        setDuration( v.findViewById( R.id.popular_videos_tv_duration ) );
        
    }
}
