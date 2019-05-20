package com.ervinxie.alue_client.data;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Update;

import com.bumptech.glide.Glide;
import com.ervinxie.alue_client.Contract;

import java.util.concurrent.ExecutionException;

@Entity
public class Pictures {
    @PrimaryKey
    public int PictureId;

    public String UrlId;
    public String FilePath;
    public Boolean Liked;


    public Pictures(){}
    @Ignore
    public Pictures(int pictureId, String urlId, String filePath, Boolean liked) {
        PictureId = pictureId;
        UrlId = urlId;
        FilePath = filePath;
        Liked = liked;
    }

    @Ignore
    public String info() {
        return " PictureId:" + PictureId + " UrlId:" + UrlId + " FilePath:" + FilePath + " Liked:" + Liked + "\n";
    }

}
