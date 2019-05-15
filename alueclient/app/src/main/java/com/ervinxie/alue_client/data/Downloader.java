package com.ervinxie.alue_client.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;

public class Downloader {
    private  class DownloadImage extends AsyncTask<String,Void, Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            afterDownload.onFinished(bitmap);
        }
    }
    public Downloader(AfterDownload afterDownload1, String url){
        afterDownload = afterDownload1;
        new DownloadImage().execute(url);
    }
    interface AfterDownload {
        void onFinished(Bitmap bitmap);
    }
    AfterDownload afterDownload;
}
