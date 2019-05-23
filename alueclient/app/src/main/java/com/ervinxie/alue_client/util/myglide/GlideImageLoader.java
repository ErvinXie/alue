package com.ervinxie.alue_client.util.myglide;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ervinxie.alue_client.util.Contract;


import java.util.concurrent.ExecutionException;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class GlideImageLoader {

    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public GlideImageLoader(ImageView imageView, ProgressBar progressBar) {
        mImageView = imageView;
        mProgressBar = progressBar;
    }



    private void dealProgressBar(final String url) {
        //set Listener & start
        ProgressAppGlideModule.expect(url, new ProgressAppGlideModule.UIonProgressListener() {
            @Override
            public void onProgress(long bytesRead, long expectedLength) {
                Contract.alueMainActivity.runOnUiThread(() -> {
                    if (mProgressBar != null) {
                        mProgressBar.setProgress((int) (100 * bytesRead / expectedLength));
                    }
                });
            }

            @Override
            public float getGranualityPercentage() {
                return 1.0f;
            }
        });
    }

    public void preload(final String url, RequestOptions options) {
        if (url == null || options == null) return;
        onConnecting();
        dealProgressBar(url);
        //Get Image
        Glide.with(mImageView.getContext())
                .load(url)
                .transition(withCrossFade())
                .apply(options.skipMemoryCache(true))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ProgressAppGlideModule.forget(url);
                        onFinished();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ProgressAppGlideModule.forget(url);
                        onFinished();
                        return false;
                    }
                })
                .preload();
    }

    public Bitmap loadBitmap(final String url, RequestOptions options) throws ExecutionException, InterruptedException {
        if (url == null || options == null) return null;
        onConnecting();
        dealProgressBar(url);
        return Glide.with(Contract.context)
                .asBitmap()
                .load(url)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        ProgressAppGlideModule.forget(url);
                        onFinished();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        onSuccess();
                        ProgressAppGlideModule.forget(url);
                        onFinished();
                        return false;
                    }
                })
                .submit()
                .get();
    }

    public void load(final String url, RequestOptions options) {
        if (url == null || options == null) return;
        onConnecting();
        dealProgressBar(url);
        //Get Image
        Glide.with(mImageView.getContext())
                .load(url)
                .transition(withCrossFade())
                .apply(options.skipMemoryCache(true))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ProgressAppGlideModule.forget(url);
                        onFinished();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        onSuccess();
                        ProgressAppGlideModule.forget(url);
                        onFinished();
                        return false;
                    }
                })
                .into(mImageView);
    }


    public void onSuccess(){};


    private void onConnecting() {
        Contract.alueMainActivity.runOnUiThread(() -> {
            if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
        });

    }


    private void onFinished() {
        if (mProgressBar != null && mImageView != null) {
            mProgressBar.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
        }
    }
}