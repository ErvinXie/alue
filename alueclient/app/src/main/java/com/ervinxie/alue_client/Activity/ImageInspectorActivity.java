package com.ervinxie.alue_client.Activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.DataManager;
import com.ervinxie.alue_client.util.AboutPictures;
import com.ervinxie.alue_client.util.myglide.GlideApp;
import com.ervinxie.alue_client.data.Pictures;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.util.DrawableGen;
import com.ervinxie.alue_client.util.FullscreenActivity;
import com.ervinxie.alue_client.util.myglide.GlideImageLoader;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ImageInspectorActivity extends FullscreenActivity {

    public static final String TAG = "ImageInspectorActivity:";
    ImageButton setWallpaper_button, saveImage, like_button;
    //    Button finish;
    PhotoView photoView;
    ProgressBar progressBar;

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


        setWallpaper_button = findViewById(R.id.set_wallpaper);
        saveImage = findViewById(R.id.download_wallpaper);
        like_button = findViewById(R.id.like);
        photoView = findViewById(R.id.photoView);
        progressBar = findViewById(R.id.progressBar);


        photoView.setOnClickListener(v -> toggle());

        liked = getIntent().getBooleanExtra("liked", false);
        UIHandler.sendEmptyMessage(liked ? Like : DisLike);


        String Id = getIntent().getStringExtra("id");
        String urlRaw = getIntent().getStringExtra("urlRaw");
        String urlFull = getIntent().getStringExtra("urlFull");
        String urlRegular = getIntent().getStringExtra("urlRegular");
        String urlSmall = getIntent().getStringExtra("urlSmall");
        String pictureTitle = getIntent().getStringExtra("title");
        String pictureDescription = getIntent().getStringExtra("description");


        new GlideImageLoader(photoView, progressBar) {
            public void onSuccess() {
                UIHandler.sendEmptyMessageDelayed(Scale, 100);
            }
        }.load(urlRegular, new RequestOptions()
                .error(R.drawable.ic_clear_white_144dp)
                .placeholder(DrawableGen.getCircularProgressDrawable(100,Color.WHITE))
                .priority(Priority.HIGH));


        new GlideImageLoader(photoView,null).preload(urlFull, new RequestOptions());


        like_button.setOnClickListener(v -> {
            like_button.setClickable(false);
            new Thread(() -> {
                Pictures pictures = DataManager.database.picturesDao().getPicturesById(Id);
                liked = !liked;
                pictures.setLiked(liked);
                DataManager.database.picturesDao().update(pictures);

                UIHandler.sendEmptyMessage(liked ? Like : DisLike);

                runOnUiThread(() -> {
                    like_button.setClickable(true);
                });
            }).start();
        });


        setWallpaper_button.setOnClickListener(v -> {
            UIHandler.sendEmptyMessage(SettingWallPaper);

            float pictureWidth = photoView.getDrawable().getBounds().width();
            float pictureHeight = photoView.getDrawable().getBounds().height();
            float viewWidth = photoView.getWidth();
            float viewHeight = photoView.getHeight();
            RectF rectF = photoView.getDisplayRect();

            new Thread(() -> {
                Log.d(TAG, "setting wallpaper");
                Bitmap bitmap = null;
                Boolean setOk = false;
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

                if (bitmap != null) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int height = metrics.heightPixels;
                    int width = metrics.widthPixels;

                    float hScale = pictureHeight / (rectF.bottom - rectF.top) * bitmap.getHeight() / pictureHeight;
                    float wScale = pictureWidth / (rectF.right - rectF.left) * bitmap.getWidth() / pictureWidth;


                    Log.d(TAG, "recf: " + rectF.left + " " + rectF.right + " " + rectF.top + " " + rectF.bottom);

                    Rect rect = new Rect();

                    rect.top = (int) (((0 - rectF.top) * hScale));
                    rect.bottom = (int) ((rect.top + viewHeight * hScale));
                    rect.left = (int) (((0 - rectF.left) * wScale));
                    rect.right = (int) ((rect.left + viewWidth * wScale));


                    Log.d(TAG, "viewWidth:" + viewWidth + " viewHeight:" + viewHeight);
                    Log.d(TAG, "pictureWidth:" + pictureWidth + " pictureHeight:" + pictureHeight);
                    Log.d(TAG, "bitmapWidth:" + bitmap.getWidth() + " bitmapHeight:" + bitmap.getHeight());
                    Log.d(TAG, "rec: " + rect.left + " " + rect.right + " " + rect.top + " " + rect.bottom);


                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(Contract.alueMainActivity);
                    wallpaperManager.setWallpaperOffsetSteps(1, 1);
                    wallpaperManager.suggestDesiredDimensions(width, height);
                    try {
                        wallpaperManager.setBitmap(bitmap, rect, true);
                        setOk = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                UIHandler.sendEmptyMessage(setOk ? SucceedSettingWallPaper : FailedSettingWallPaper);
            }).start();
        });

        saveImage.setOnClickListener(v -> {
            UIHandler.sendEmptyMessage(SavingImage);
            new Thread(() -> {
                Log.d(TAG, "saving wallpaper");
                Bitmap bitmap = null;
                Boolean savingOk = false;
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
                    if (bitmap != null) {
                        MediaStore.Images.Media.insertImage(Contract.context.getContentResolver(), bitmap, pictureTitle, pictureDescription);
                        Log.d(TAG, "wallpaper saved");
                        savingOk = true;
                    }
                }
                UIHandler.sendEmptyMessage(savingOk ? SucceedSavingImage : FailedSavingImage);
            }).start();

        });

    }


    private static final int Like = 0;
    private static final int DisLike = 1;
    private static final int SettingWallPaper = 2;
    private static final int SucceedSettingWallPaper = 3;
    private static final int FailedSettingWallPaper = 4;
    private static final int WallPaperSet = 5;
    private static final int Scale = 6;

    private static final int SavingImage = 7;
    private static final int SucceedSavingImage = 8;
    private static final int FailedSavingImage = 9;
//    private static final int Scale = 10;
//    private static final int Scale = 11;


    private static final int LoadFullImage = 100;

    private Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Like: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_favorite_144px)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(like_button);
                    break;
                }
                case DisLike: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_favorite_border_144px)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(like_button);
                    break;
                }
                case SettingWallPaper: {
                    setWallpaper_button.setClickable(false);
                    ToastShort("正在设置壁纸");
                    GlideApp
                            .with(Contract.context)
                            .load(DrawableGen.getCircularProgressDrawable(30, Color.WHITE))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(setWallpaper_button);
                    break;
                }
                case SucceedSettingWallPaper: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_done_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(setWallpaper_button);
                    ToastShort("壁纸设置成功");
                    UIHandler.sendEmptyMessageDelayed(WallPaperSet, 2000);
                    break;
                }
                case FailedSettingWallPaper: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_clear_white_144dp)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(setWallpaper_button);
                    ToastShort("壁纸设置失败");
                    UIHandler.sendEmptyMessageDelayed(WallPaperSet, 2000);
                    break;
                }
                case WallPaperSet: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_smartphone_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(setWallpaper_button);
                    setWallpaper_button.setClickable(true);
                    break;
                }
                case LoadFullImage: {


                    break;
                }
                case Scale: {
                    float minimumscale = getMinimunScale();
                    photoView.setMaximumScale(minimumscale + 5.0f);
                    photoView.setMediumScale(minimumscale + 2.0f);
                    photoView.setMinimumScale(minimumscale);
                    photoView.setScale(minimumscale);
                    Log.d(TAG, minimumscale + "photoView.getScale()" + photoView.getScale());
                    photoView.refreshDrawableState();

                    break;
                }
                case SavingImage: {
                    saveImage.setClickable(false);
                    GlideApp
                            .with(Contract.context)
                            .load(DrawableGen.getCircularProgressDrawable(30, Color.WHITE))
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(saveImage);
                    ToastShort("正在保存图片");
                    break;
                }
                case SucceedSavingImage: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_cloud_done_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(saveImage);
                    ToastShort("保存图片成功");
                    saveImage.setClickable(true);
                    break;
                }
                case FailedSavingImage: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_outline_cloud_download_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(saveImage);
                    ToastShort("保存图片失败");
                    saveImage.setClickable(true);
                    break;
                }


            }
        }
    };

    float getMinimunScale() {
        float pictureWidth = photoView.getDrawable().getBounds().width();
        float pictureHeight = photoView.getDrawable().getBounds().height();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //4.2开始有虚拟导航栏，增加了该方法才能准确获取屏幕高度
            this.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }else{
            this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //displayMetrics = activity.getResources().getDisplayMetrics();//或者该方法也行
        }

        float density = displayMetrics.density;
        float viewWidth = displayMetrics.widthPixels;
        float viewHeight = displayMetrics.heightPixels;


//        float viewWidth = photoView.getWidth();
//        float viewHeight = photoView.getHeight();

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
