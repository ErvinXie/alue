package com.ervinxie.alue_client.data;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.IOException;

@Entity
public class Pictures {
    @PrimaryKey
    public int PictureId;

    public String UrlId;
    public String Cache;
    public Boolean Liked;

    @Ignore
    Bitmap picture;

    @Ignore
    public Bitmap getPicture() {
        if (Cache == null) {
//            picture = new Downloader().getBitmapFromURL(UrlId);
//            DiskReader.saveBitmap(UrlId + "_cache.png", picture);

        } else {
//            picture = DiskReader.getBitmapFromDir(Cache);
        }
        return picture;
    }
}
