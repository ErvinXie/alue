package com.ervinxie.alue_client.Activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.util.DrawableGen;
import com.ervinxie.alue_client.util.FullscreenActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ImageInspectorActivity extends FullscreenActivity {

    public static final String TAG = "ImageInspectorActivity:";
    Button setWallpaper;
    Button saveImage;
    Button finish;
    Button scale;
    PhotoView photoView;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_inspector_view);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        linearLayout = findViewById(R.id.contentPanel);
        mContentView = linearLayout;
        AUTO_HIDE = true;



        setWallpaper = findViewById(R.id.set_wall_paper);
        saveImage = findViewById(R.id.save_image);
        photoView = findViewById(R.id.photoView);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        scale = findViewById(R.id.scale);

        finish = findViewById(R.id.finish);

        finish.setOnClickListener(v -> {
            finish();
        });

        String urlRaw = getIntent().getStringExtra("urlRaw");
        String urlFull = getIntent().getStringExtra("urlFull");
        String urlRegular = getIntent().getStringExtra("urlRegular");
        String urlSmall = getIntent().getStringExtra("urlSmall");
        String pictureTitle = getIntent().getStringExtra("title");
        String pictureDescription = getIntent().getStringExtra("description");




        GlideApp
                .with(Contract.context)
                .load(urlFull)
                .error(R.drawable.ic_clear_white_24dp)
                .placeholder(DrawableGen.getCircularProgressDrawable())
                .thumbnail(GlideApp.with(Contract.context).load(urlSmall))
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
                            .load(urlFull)
                            .submit().get();
                } catch (ExecutionException e) {
                    ToastShort("未能设置壁纸");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    ToastShort("未能设置壁纸");
                    e.printStackTrace();
                }
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(Contract.context);
                    wallpaperManager.setBitmap(bitmap);
                    ToastShort("设置壁纸成功");
                    Log.d(TAG, "wallpaper set");
                } catch (IOException e) {
                    ToastShort("未能设置壁纸");
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
                            .load(urlFull)
                            .submit().get();
                } catch (ExecutionException e) {
                    ToastShort("未能保存图片");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    ToastShort("未能保存图片");
                    e.printStackTrace();
                }

                if (ContextCompat.checkSelfPermission(Contract.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "requesting permission");
                    ActivityCompat.requestPermissions(Contract.alueMainActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            2);

                } else {
                    MediaStore.Images.Media.insertImage(Contract.context.getContentResolver(), bitmap, pictureTitle, pictureDescription);
                    ToastShort("保存图片成功");
                    Log.d(TAG, "wallpaper saved");
                }
            }).start();

        });

        scale.setOnClickListener(v->{

            float pictureWeight = photoView.getDrawable().getBounds().width();
            float pictureHeight = photoView.getDrawable().getBounds().height();

            float viewWeight = photoView.getWidth();
            float viewHeight = photoView.getHeight();

            Log.d(TAG,"pictureWeight: "+pictureWeight+" pictureHeight: "+pictureHeight);
            Log.d(TAG,"viewWeight: "+viewWeight+" viewHeight: "+viewHeight);

            float minimumscale = Math.max(viewHeight/pictureHeight,viewWeight/pictureWeight);
            photoView.setMaximumScale(minimumscale+2.0f);
            photoView.setMediumScale(minimumscale+1.0f);
            photoView.setMinimumScale(minimumscale);
            photoView.setScale(minimumscale);

        });
    }





}
