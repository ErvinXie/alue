package com.ervinxie.alue_client.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DiskReader {
    static final String TAG = "DiskReader: ";

    class ReadBitmap extends AsyncTask<Void,Void,Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            String filedir=diskReadInterface.getDir();
            try {
                FileInputStream input = new FileInputStream(filedir);
                Log.d(TAG,"Starting to read "+ filedir);
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            diskReadInterface.readed(bitmap);
            Log.d(TAG,"Read Finished");
        }
    }

    class SaveBitmap extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            FileOutputStream out;
            String filedir = diskSaveInterface.getDir();
            try {

                out = new FileOutputStream(filedir);
                Bitmap bitmap = diskSaveInterface.getBitmap();
                if (bitmap == null) {
                    Log.d(TAG, "This Bitmap is a NULL!");
                    return false;
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Log.d(TAG,"Saved at "+filedir);
                    return true;

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            diskSaveInterface.saved(aBoolean);
            Log.d(TAG,"Save Finished");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    interface DiskSaveInterface {
        Bitmap getBitmap();
        void saved(Boolean saved);
        String getDir();

    }

    DiskSaveInterface diskSaveInterface;

    public DiskReader(DiskSaveInterface diskSaveInterface1) {
        diskSaveInterface = diskSaveInterface1;
        new SaveBitmap().execute();
    }

    interface DiskReadInterface{
        String getDir();
        void readed(Bitmap bitmap);
    }

    DiskReadInterface diskReadInterface;

    public DiskReader(DiskReadInterface diskReadInterface1){
        diskReadInterface=diskReadInterface1;
        new ReadBitmap().execute();
    }
}
