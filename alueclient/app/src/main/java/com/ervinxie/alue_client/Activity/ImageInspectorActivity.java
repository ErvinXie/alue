package com.ervinxie.alue_client.Activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.DataManager;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.data.Pictures;
import com.ervinxie.alue_client.util.AboutPictures;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.util.DrawableGen;
import com.ervinxie.alue_client.util.FullscreenActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ImageInspectorActivity extends FullscreenActivity {

    public static final String TAG = "ImageInspectorActivity:";
    ImageButton setWallpaper, saveImage, scale, like;
    //    Button finish;
    PhotoView photoView;

    Boolean isScaled;
    LinearLayout linearLayout;

    Boolean liked;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_inspector_view);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        linearLayout = findViewById(R.id.contentPanel);
        mContentView = linearLayout;
        AUTO_HIDE = true;


        setWallpaper = findViewById(R.id.set_wallpaper);
        saveImage = findViewById(R.id.download_wallpaper);
        scale = findViewById(R.id.scale_wallpaper);
        like = findViewById(R.id.like);
        photoView = findViewById(R.id.photoView);
        isScaled = false;


        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
                if (AUTO_HIDE) {
                    delayedHide(3000);
                }
            }
        });


//        finish = findViewById(R.id.finish);
//        finish.setOnClickListener(v -> {
//            finish();
//        });

        liked = getIntent().getBooleanExtra("liked", false);
        if (liked) {
            GlideApp
                    .with(Contract.context)
                    .load(R.drawable.ic_baseline_favorite_144px)
                    .error(R.drawable.ic_clear_white_144dp)
                    .into(like);
        }

        String Id = getIntent().getStringExtra("id");
        String urlRaw = getIntent().getStringExtra("urlRaw");
        String urlFull = getIntent().getStringExtra("urlFull");
        String urlRegular = getIntent().getStringExtra("urlRegular");
        String urlSmall = getIntent().getStringExtra("urlSmall");
        String pictureTitle = getIntent().getStringExtra("title");
        String pictureDescription = getIntent().getStringExtra("description");


        GlideApp
                .with(Contract.context)
                .load(urlFull)
                .error(R.drawable.ic_clear_white_144dp)
                .transition(DrawableTransitionOptions.withCrossFade())

                .placeholder(DrawableGen.getCircularProgressDrawable())
                .thumbnail(GlideApp.with(Contract.context).load(urlSmall))
                .into(photoView);


        like.setOnClickListener(v -> {
            if (liked) {
                GlideApp
                        .with(Contract.context)
                        .load(R.drawable.ic_baseline_favorite_border_144px)
                        .error(R.drawable.ic_clear_white_144dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(like);
            } else {
                GlideApp
                        .with(Contract.context)
                        .load(R.drawable.ic_baseline_favorite_144px)
                        .error(R.drawable.ic_clear_white_144dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(like);
            }

            new Thread(() -> {
                Pictures pictures = DataManager.database.picturesDao().getPicturesById(Id);
                liked = !liked;
                pictures.setLiked(liked);
                DataManager.database.picturesDao().update(pictures);
            }).start();


        });

        setWallpaper.setOnClickListener(v -> {
            delayedHide(3000);

            float minimumscale = getMinimunScale();
            if (photoView.getScale() < minimumscale) {
                ToastShort("必须缩放至图片填充屏幕");
            } else {

                float pictureWidth = photoView.getDrawable().getBounds().width();
                float pictureHeight = photoView.getDrawable().getBounds().height();
                float viewWidth = photoView.getWidth();
                float viewHeight = photoView.getHeight();


                RectF rectF = photoView.getDisplayRect();

                GlideApp
                        .with(Contract.context)
                        .load(DrawableGen.getCircularProgressDrawable(20, Color.WHITE))
                        .error(R.drawable.ic_clear_white_144dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(setWallpaper);

                new Thread(() -> {
                    Log.d(TAG, "setting wallpaper");
                    Bitmap bitmap = null;
                    Boolean ok = false;
                    try {
                        Log.d(TAG, "downloading wallpaper");
                        bitmap = GlideApp
                                .with(Contract.context)
                                .asBitmap()
                                .load(urlFull)
                                .submit().get();
                    } catch (ExecutionException e) {

                        e.printStackTrace();
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int height = metrics.heightPixels;
                    int width = metrics.widthPixels;

                    float hScale = pictureHeight / (rectF.bottom - rectF.top) * bitmap.getHeight() / pictureHeight;
                    float wScale = pictureWidth / (rectF.right - rectF.left) * bitmap.getWidth() / pictureWidth;


                    Log.d(TAG, "recf: "+rectF.left + " " + rectF.right + " " + rectF.top + " " + rectF.bottom);

                    Rect rect = new Rect();

                    rect.top = (int) (((0 - rectF.top) * hScale));
                    rect.bottom = (int) ((rect.top + viewHeight * hScale));
                    rect.left = (int) (((0 - rectF.left) * wScale));
                    rect.right = (int) ((rect.left + viewWidth * wScale));


                    Log.d(TAG,"viewWidth:"+viewWidth+" viewHeight:"+viewHeight);
                    Log.d(TAG,"pictureWidth:"+pictureWidth+" pictureHeight:"+pictureHeight);
                    Log.d(TAG,"bitmapWidth:"+bitmap.getWidth()+" bitmapHeight:"+bitmap.getHeight());
                    Log.d(TAG, "rec: "+rect.left + " " + rect.right + " " + rect.top + " " + rect.bottom);


                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(Contract.alueMainActivity);
                    wallpaperManager.setWallpaperOffsetSteps(1, 1);
                    wallpaperManager.suggestDesiredDimensions(width, height);
                    try {
                        wallpaperManager.setBitmap(bitmap,rect,true);
                        ok=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (ok) {
                        runOnUiThread(() -> {
                            GlideApp
                                    .with(Contract.context)
                                    .load(R.drawable.ic_baseline_cloud_done_144px)
                                    .error(R.drawable.ic_clear_white_144dp)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(setWallpaper);
                            ToastShort("壁纸设置成功");
                        });
                    } else {
                        runOnUiThread(() -> {
                            GlideApp
                                    .with(Contract.context)
                                    .load(R.drawable.ic_clear_white_144dp)
                                    .error(R.drawable.ic_clear_white_144dp)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(setWallpaper);
                            ToastShort("壁纸设置失败");
                        });
                    }

                    new Thread(() -> {
                        try {
                            Thread.sleep(2250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            GlideApp
                                    .with(Contract.context)
                                    .load(R.drawable.ic_baseline_smartphone_144px)
                                    .error(R.drawable.ic_clear_white_144dp)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(setWallpaper);
                        });
                    }).start();
                }).start();
            }
        });

        saveImage.setOnClickListener(v -> {
            delayedHide(3000);
            GlideApp
                    .with(Contract.context)
                    .load(DrawableGen.getCircularProgressDrawable(20, Color.WHITE))
                    .error(R.drawable.ic_clear_white_144dp)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(saveImage);
            new Thread(() -> {
                Log.d(TAG, "saving wallpaper");
                Bitmap bitmap = null;
                Boolean ok = false;
                try {
                    bitmap = GlideApp
                            .with(Contract.context)
                            .asBitmap()
                            .load(urlFull)
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
                    ok = true;
                }
                if (ok) {
                    runOnUiThread(() -> {
                        GlideApp
                                .with(Contract.context)
                                .load(R.drawable.ic_baseline_cloud_done_144px)
                                .error(R.drawable.ic_clear_white_144dp)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(saveImage);
                        ToastShort("保存图片成功");
                    });
                } else {
                    runOnUiThread(() -> {
                        GlideApp
                                .with(Contract.context)
                                .load(R.drawable.ic_outline_cloud_download_144px)
                                .error(R.drawable.ic_clear_white_144dp)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(saveImage);
                        ToastShort("保存图片失败");
                    });
                }
            }).start();

        });


        scale.setOnClickListener(v -> {
            delayedHide(3000);
            if (isScaled) {
                Log.d(TAG, "to not Scaled");
                photoView.setMinimumScale(1.0f);
                photoView.setMaximumScale(8.0f);
                photoView.setMediumScale(2.0f);
                photoView.setScale(1.0f);
                isScaled = false;
                GlideApp
                        .with(Contract.context)
                        .load(R.drawable.ic_outline_center_focus_strong_144px)
                        .error(R.drawable.ic_clear_white_144dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(scale);

            } else {
                Log.d(TAG, "to Scaled");
                float minimumscale = getMinimunScale();
                photoView.setMaximumScale(minimumscale + 5.0f);
                photoView.setMediumScale(minimumscale + 2.0f);
                photoView.setMinimumScale(minimumscale);
                photoView.setScale(minimumscale);

                isScaled = true;
                GlideApp
                        .with(Contract.context)
                        .load(R.drawable.ic_baseline_center_focus_strong_144px)
                        .error(R.drawable.ic_clear_white_144dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(scale);
            }

        });
    }

    float getMinimunScale() {
        float pictureWidth = photoView.getDrawable().getBounds().width();
        float pictureHeight = photoView.getDrawable().getBounds().height();

        float viewWidth = photoView.getWidth();
        float viewHeight = photoView.getHeight();

        Log.d(TAG, "pictureWidth: " + pictureWidth + " pictureHeight: " + pictureHeight);
        Log.d(TAG, "viewWidth: " + viewWidth + " viewHeight: " + viewHeight);


        float minimumscale;
        if (pictureWidth / viewWidth > pictureHeight / viewHeight) {
            minimumscale = viewHeight / (viewWidth / pictureWidth * pictureHeight);
        } else {
            minimumscale = viewWidth / (viewHeight / pictureHeight * pictureWidth);
        }
        return minimumscale;
    }

}
