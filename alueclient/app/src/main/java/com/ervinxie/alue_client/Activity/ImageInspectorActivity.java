package com.ervinxie.alue_client.Activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.DataManager;
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
    ImageButton setWallpaperButton, saveImageButton, likeButton;
    //    Button finish;
    PhotoView photoView;
    ProgressBar progressBar;

    LinearLayout linearLayout;

    Boolean liked;

    String Id;
    String urlRaw;
    String urlFull;
    String urlRegular;
    String urlSmall;
    String pictureTitle;
    String pictureDescription;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_inspector_view);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        linearLayout = findViewById(R.id.contentPanel);
        mContentView = linearLayout;


        setWallpaperButton = findViewById(R.id.set_wallpaper);
        saveImageButton = findViewById(R.id.download_wallpaper);
        likeButton = findViewById(R.id.like);
        photoView = findViewById(R.id.photoView);
        progressBar = findViewById(R.id.progressBar);
        photoView.setOnClickListener(v -> toggle());
        GlideApp
                .with(Contract.context)
                .load(R.drawable.ic_baseline_favorite_border_144px)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(likeButton);


        liked = getIntent().getBooleanExtra("liked", false);
        UIHandler.sendEmptyMessage(liked ? Like : DisLike);


        Id = getIntent().getStringExtra("id");
        urlRaw = getIntent().getStringExtra("urlRaw");
        urlFull = getIntent().getStringExtra("urlFull");
        urlRegular = getIntent().getStringExtra("urlRegular");
        urlSmall = getIntent().getStringExtra("urlSmall");
        pictureTitle = getIntent().getStringExtra("title");
        pictureDescription = getIntent().getStringExtra("description");


        new GlideImageLoader(photoView, progressBar) {
            public void onSuccess() {
                UIHandler.sendEmptyMessageDelayed(Scale, 100);
            }
        }.load(urlRegular, new RequestOptions()
                .error(R.drawable.ic_clear_white_144dp)
                .placeholder(DrawableGen.getCircularProgressDrawable(100, Color.WHITE))
                .priority(Priority.HIGH));


        new GlideImageLoader(photoView, null).preload(urlFull, new RequestOptions());


        likeButton.setOnClickListener(v -> {
            likeButton.setClickable(false);
            new Thread(() -> {
                Pictures pictures = DataManager.database.picturesDao().getPicturesById(Id);
                liked = !liked;
                pictures.setLiked(liked);
                DataManager.database.picturesDao().update(pictures);

                UIHandler.sendEmptyMessage(liked ? Like : DisLike);

                runOnUiThread(() -> {
                    likeButton.setClickable(true);
                });
            }).start();
        });


        setWallpaperButton.setOnClickListener(v -> {
            UIHandler.sendEmptyMessage(SettingWallPaper);

            float pictureWidth  = photoView.getDrawable().getBounds().width();
            float pictureHeight = photoView.getDrawable().getBounds().height();
            float viewWidth     = photoView.getWidth();
            float viewHeight    = photoView.getHeight();
            RectF rectF         = photoView.getDisplayRect();

            new Thread(() -> {
                Log.d(TAG, "setting wallpaper");
                Bitmap  bitmap = null;
                Boolean setOk  = false;
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
                    int width  = metrics.widthPixels;

                    float hScale =
                            pictureHeight / (rectF.bottom - rectF.top) * bitmap.getHeight() / pictureHeight;
                    float wScale =
                            pictureWidth / (rectF.right - rectF.left) * bitmap.getWidth() / pictureWidth;


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


                    WallpaperManager wallpaperManager =
                            WallpaperManager.getInstance(Contract.alueMainActivity);
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

        saveImageButton.setOnClickListener(v -> {
            showPopup(saveImageButton);
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
    private static final int ImageSaved = 10;
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
                            .into(likeButton);
                    break;
                }
                case DisLike: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_favorite_border_144px)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(likeButton);
                    break;
                }
                case SettingWallPaper: {
                    setWallpaperButton.setClickable(false);
                    ToastShort("正在设置壁纸");
                    GlideApp
                            .with(Contract.context)
                            .load(DrawableGen.getCircularProgressDrawable(30, Color.WHITE))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(setWallpaperButton);
                    break;
                }
                case SucceedSettingWallPaper: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_done_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(setWallpaperButton);
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
                            .into(setWallpaperButton);
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
                            .into(setWallpaperButton);
                    setWallpaperButton.setClickable(true);
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
                    saveImageButton.setClickable(false);
                    GlideApp
                            .with(Contract.context)
                            .load(DrawableGen.getCircularProgressDrawable(30, Color.WHITE))
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(saveImageButton);
                    ToastShort("正在保存图片");
                    break;
                }
                case SucceedSavingImage: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_cloud_done_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(saveImageButton);
                    ToastShort("保存图片成功");
                    UIHandler.sendEmptyMessageDelayed(ImageSaved,2000);
                    break;
                }
                case FailedSavingImage: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_clear_white_144dp)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(saveImageButton);
                    ToastShort("保存图片失败");
                    UIHandler.sendEmptyMessageDelayed(ImageSaved,2000);
                    break;
                }
                case ImageSaved: {
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_outline_cloud_download_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(saveImageButton);
                    saveImageButton.setClickable(true);
                    break;
                }
                default: {
                    break;
                }

            }
        }
    };

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.download_raw: {
                    saveImage(urlRaw);
                    return true;
                }
                case R.id.download_full: {
                    saveImage(urlFull);
                    return true;
                }
                case R.id.download_regular: {
                    saveImage(urlRegular);
                    return true;
                }
                default: {
                    return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.download_menu, popup.getMenu());
        popup.show();
    }

    void saveImage(String url) {
        UIHandler.sendEmptyMessage(SavingImage);
        new Thread(() -> {
            Log.d(TAG, "saving wallpaper");
            Bitmap  bitmap   = null;
            Boolean savingOk = false;
            try {
                bitmap = GlideApp
                        .with(Contract.context)
                        .asBitmap()
                        .load(url)
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
    }

    float getMinimunScale() {
        float pictureWidth  = photoView.getDrawable().getBounds().width();
        float pictureHeight = photoView.getDrawable().getBounds().height();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //4.2开始有虚拟导航栏，增加了该方法才能准确获取屏幕高度
            this.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //displayMetrics = activity.getResources().getDisplayMetrics();//或者该方法也行
        }

        float density    = displayMetrics.density;
        float viewWidth  = displayMetrics.widthPixels;
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
