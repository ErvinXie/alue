package com.ervinxie.alue_client.util;

import android.graphics.Color;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class DrawableGen {
    private DrawableGen(){};

    static public CircularProgressDrawable getCircularProgressDrawable(){
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(Contract.context);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }
    static public CircularProgressDrawable getCircularProgressDrawable(int radius,int... colors){
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(Contract.context);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setColorSchemeColors(colors);
        circularProgressDrawable.setCenterRadius(radius);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }
}
