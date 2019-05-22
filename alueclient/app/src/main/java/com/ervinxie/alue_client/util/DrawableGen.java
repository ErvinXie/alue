package com.ervinxie.alue_client.util;

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
}
