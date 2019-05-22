package com.ervinxie.alue_client.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.Activity.ImageInspectorActivity;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.data.Pictures;

import java.util.List;

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

        String urlSmall = picture.getUrlSmall();
        String urlThumb = picture.getUrlThumb();
        String urlRegular = picture.getUrlRegular();

        RequestOptions requestOptions = new RequestOptions().placeholder(R.color.colorPrimary).centerCrop();
        GlideApp
                .with(Contract.context)
                .load(urlSmall)
                .error(R.drawable.ic_clear_white_144dp)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(GlideApp.with(Contract.context).load(urlThumb).apply(requestOptions))
                .apply(requestOptions)
                .into(holder.picture);




        holder.linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Contract.context, ImageInspectorActivity.class);
            intent.putExtra("liked",picture.getLiked());
            intent.putExtra("id",picture.getId());
            intent.putExtra("urlRaw", picture.getUrlRaw());
            intent.putExtra("urlFull", picture.getUrlFull());
            intent.putExtra("urlRegular", urlRegular);
            intent.putExtra("urlSmall", urlSmall);
            intent.putExtra("title", picture.getTitle());
            intent.putExtra("description", picture.getDescription());
            Contract.alueMainActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return picturesList.size();
    }

    public class PicturesViewHolder extends RecyclerView.ViewHolder {
        public TextView pictureInfo;
        public ImageView picture;

        public LinearLayout linearLayout;

        public Button saveImage;
        public Button setWallpaper;
        public Button detail;

        public PicturesViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            picture = itemView.findViewById(R.id.picture);

        }
    }


}