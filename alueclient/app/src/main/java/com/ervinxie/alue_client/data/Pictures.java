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
//    @Ignore
//    Bitmap picture;
//
//    static final String TAG = "Pictures:";
//
//
//    @Ignore
//    public Bitmap getPicture() {
//        if (FilePath == null) {
//            String Url="";
//            try {
//                picture = GlideApp
//                        .with(Contract.context)
//                        .asBitmap()
//                        .load(Url)
//                        .submit()
//                        .get();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            FilePath = getPicDir();
//            new DiskReader(new SaveAgent(picture));
//
//        } else {
//            GlideApp
//        }
//        return picture;
//    }
//
//    public String getPicDir(){return Contract.context.getFilesDir() + "/"+PictureId+".png";}
//
//    class ReadAgent implements DiskReader.DiskReadInterface {
//        ReadAgent() {}
//        @Override
//        public String getDir() {
//            return getPicDir();
//        }
//
//        @Override
//        public void readed(Bitmap bitmap) {
//            picture=bitmap;
//        }
//    }
//    class SaveAgent implements DiskReader.DiskSaveInterface {
//        Bitmap bitmap;
//
//
//        SaveAgent(Bitmap bitmap1) {
//            bitmap = bitmap1;
//        }
//
//
//        @Override
//        public Bitmap getBitmap() {
//            return bitmap;
//        }
//
//        @Override
//        public void saved(Boolean saved) {
//            if (saved) {
//                Log.d(TAG,PictureId+" 保存成功");
//            } else {
//                Log.d(TAG,PictureId+" 保存失败");
//            }
//        }
//
//        @Override
//        public String getDir() {
//            return getPicDir();
//        }
}
