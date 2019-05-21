package com.ervinxie.alue_client.adapter;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.ervinxie.alue_client.AlueMainActivity;
import com.ervinxie.alue_client.Contract;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.data.Pictures;
import com.ervinxie.alue_client.util.AboutPictures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageArrayAdapter extends RecyclerView.Adapter<ImageArrayAdapter.PicturesViewHolder> {

    static final String TAG = "ImageArrayAdapter";

    private List<Pictures> picturesList;

    public ImageArrayAdapter(List<Pictures> picturesList) {
        this.picturesList = picturesList;
    }

    @NonNull
    @Override
    public PicturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_card_view, parent, false);
        return new PicturesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PicturesViewHolder holder, int position) {
        Pictures picture = picturesList.get(position);
        holder.pictureInfo.setText(picture.info());
        String urlSmall = picture.getUrlSmall();
        String urlThumb = picture.getUrlThumb();
        String urlRegular = picture.getUrlRegular();
        GlideApp
                .with(Contract.context)
                .load(urlThumb)
                .into(holder.picture);


        holder.setWallpaper.setOnClickListener(v -> {
            new Thread(() -> {
                Log.d(TAG, "setting wallpaper");
                Bitmap bitmap = null;
                try {
                    Log.d(TAG, "downloading wallpaper");
                    bitmap = GlideApp
                            .with(Contract.context)
                            .asBitmap()
                            .load(urlRegular)
                            .submit().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(Contract.context);
                    wallpaperManager.setBitmap(bitmap);
                    Log.d(TAG, "wallpaper set");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        });

        holder.saveImage.setOnClickListener(v -> {
            new Thread(() -> {
                Log.d(TAG, "saving wallpaper");
                Bitmap bitmap = null;
                try {
                    bitmap = GlideApp
                            .with(Contract.context)
                            .asBitmap()
                            .load(urlRegular)
                            .submit().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ContextCompat.checkSelfPermission(Contract.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"requesting permission");
                    ActivityCompat.requestPermissions(Contract.alueMainActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            2);

                } else {
                    MediaStore.Images.Media.insertImage(Contract.context.getContentResolver(), bitmap, picture.getTitle(), picture.getDescription());
                    Log.d(TAG, "wallpaper saved");
                }
            }).start();

        });

    }

    @Override
    public int getItemCount() {
        return picturesList.size();
    }

    public class PicturesViewHolder extends RecyclerView.ViewHolder {
        public TextView pictureInfo;
        public ImageView picture;

        public Button saveImage;
        public Button setWallpaper;

        public PicturesViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureInfo = itemView.findViewById(R.id.picture_info);
            picture = itemView.findViewById(R.id.picture);
            saveImage = itemView.findViewById(R.id.save_image);
            setWallpaper = itemView.findViewById(R.id.set_wall_paper);
        }
    }


}