package com.gjjy.basiclib.utils;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.gjjy.basiclib.R;

public class ColorUtils {
    public static int[] getPresetColors(@NonNull Context context) {
        Resources r = context.getResources();
        return new int[] {
                r.getColor( R.color.colorPreset1 ),
                r.getColor( R.color.colorPreset2 ),
                r.getColor( R.color.colorPreset3 ),
                r.getColor( R.color.colorPreset4 ),
                r.getColor( R.color.colorPreset5 ),
                r.getColor( R.color.colorPreset6 ),
                r.getColor( R.color.colorPreset7 ),
                r.getColor( R.color.colorPreset8 ),
                r.getColor( R.color.colorPreset9 ),
                r.getColor( R.color.colorPreset10 ),
                r.getColor( R.color.colorPreset11 ),
                r.getColor( R.color.colorPreset12 ),
                r.getColor( R.color.colorPreset13 )
        };
    }
}
