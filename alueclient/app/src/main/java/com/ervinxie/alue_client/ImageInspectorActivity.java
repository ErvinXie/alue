package com.ervinxie.alue_client;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.request.RequestOptions;
import com.ervinxie.alue_client.data.GlideApp;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ImageInspectorActivity extends AppCompatActivity {

    public static final String TAG = "ImageInspectorActivity:";
    Button setWallpaper;
    Button saveImage;
    Button finish;
    PhotoView photoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_inspector_view);
        setWallpaper = findViewById(R.id.set_wall_paper);
        saveImage = findViewById(R.id.save_image);
        photoView = findViewById(R.id.photoView);


        finish = findViewById(R.id.finish);

        finish.setOnClickListener(v -> {
            finish();
        });

        String urlRegular = getIntent().getStringExtra("urlRegular");
        String urlSmall = getIntent().getStringExtra("urlSmall");
        String pictureTitle = getIntent().getStringExtra("title");
        String pictureDescription = getIntent().getStringExtra("description");


        RequestOptions requestOptions = new RequestOptions().placeholder(R.color.colorPrimary).centerCrop();
        GlideApp
                .with(Contract.context)
                .load(urlRegular)
                .thumbnail(GlideApp.with(Contract.context).load(urlSmall))
//                .apply(requestOptions)
                .into(photoView);


        setWallpaper.setOnClickListener(v -> {
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

        saveImage.setOnClickListener(v -> {
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
                    Log.d(TAG, "requesting permission");
                    ActivityCompat.requestPermissions(Contract.alueMainActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            2);

                } else {
                    MediaStore.Images.Media.insertImage(Contract.context.getContentResolver(), bitmap, pictureTitle, pictureDescription);
                    Log.d(TAG, "wallpaper saved");
                }
            }).start();

        });


    }
}
