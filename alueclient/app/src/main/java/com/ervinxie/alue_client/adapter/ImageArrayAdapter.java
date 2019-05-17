package com.ervinxie.alue_client.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ervinxie.alue_client.R;

import java.util.ArrayList;

public class ImageArrayAdapter extends RecyclerView.Adapter<ImageArrayAdapter.ImageViewHolder> {
    private ArrayList<Bitmap> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public ImageViewHolder(ImageView v) {
            super(v);
            imageView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ImageArrayAdapter(ArrayList<Bitmap> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        // create a new view

        ImageView v = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_image_view,parent, false);
        ImageViewHolder vh = new ImageViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        holder.imageView.setImageBitmap(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}