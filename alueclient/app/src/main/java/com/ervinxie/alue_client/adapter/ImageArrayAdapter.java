package com.ervinxie.alue_client.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ervinxie.alue_client.Contract;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.data.Pictures;

import java.util.ArrayList;
import java.util.List;

public class ImageArrayAdapter extends RecyclerView.Adapter<ImageArrayAdapter.PicturesViewHolder> {


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
        GlideApp
                .with(Contract.context)
                .load("https://img.qiyidao.com/201802/09/150303831.jpg")
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return picturesList.size();
    }

    public class PicturesViewHolder extends RecyclerView.ViewHolder {
        public TextView pictureInfo;
        public ImageView picture;

        public PicturesViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureInfo = itemView.findViewById(R.id.picture_info);
            picture = itemView.findViewById(R.id.picture);

        }
    }


}