package com.gjjy.discoverylib.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.gjjy.discoverylib.R;

public class MyCourseHolder extends BaseFindHolder {
    private final ImageView ivPlayImg;

    public MyCourseHolder(@NonNull ViewGroup parent, int itemRes) {
        super(parent, itemRes);
        View v = getItemView();

        setNewFlag( v.findViewById( R.id.course_list_iv_new_flag ) );
        setImg( v.findViewById( R.id.course_list_iv_img ) );
        setTitle( v.findViewById( R.id.course_list_tv_title ) );
        setContent( v.findViewById( R.id.course_list_tv_content ) );
        setDuration( v.findViewById( R.id.course_list_tv_progress ) );

        ivPlayImg = v.findViewById( R.id.course_list_iv_play_img );
    }

    public ImageView getPlayImg() { return ivPlayImg; }
}