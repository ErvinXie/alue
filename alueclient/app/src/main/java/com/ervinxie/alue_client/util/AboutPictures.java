package com.ervinxie.alue_client.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class AboutPictures {
    private AboutPictures(){};

    static public Bitmap imageViewToBitmap(ImageView view){
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        return bitmap;
    }

    public static Bitmap bitmapCut(Bitmap bitmap,int x,int y,int w,int h){

        Bitmap newbmp = (Bitmap) Bitmap.createBitmap(bitmap,x,y,w,h,null,false);
        return newbmp;
    }
}
