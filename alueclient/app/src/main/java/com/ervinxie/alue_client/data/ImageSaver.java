package com.ervinxie.alue_client.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import com.ervinxie.alue_client.Contract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageSaver {
    static final String TAG = "ImageSaver: ";

    public Bitmap getBitmapFromDir(String dir) {
        try {
            FileInputStream input = new FileInputStream(dir);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    class SaveBitmap extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(Contract.context.getFilesDir(), strings[0]));
                Bitmap bitmap = afterSaved.getBitmap();
                if (bitmap == null) {
                    Log.d(TAG, "This Bitmap is a NULL!");
                    return false;
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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
            afterSaved.onFinished(aBoolean);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    interface AfterSaved {
        Bitmap getBitmap();

        void onFinished(Boolean saved);
    }

    AfterSaved afterSaved;

    public ImageSaver(AfterSaved afterSaved1, String filename) {
        afterSaved = afterSaved1;
        new SaveBitmap().execute(filename);
    }
}
