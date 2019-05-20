package com.ervinxie.alue_client.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ervinxie.alue_client.Contract;
import com.ervinxie.alue_client.network.Network;
import com.ervinxie.alue_client.util.UrlGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataManager {
    static final String TAG = "Data Manager: ";

    private DataManager() {
    }

    static AppDatabase database = AppDatabase.getInstance(Contract.context);

    static void updateDatabase() {
        int database_photos = database.picturesDao().getPicturesAmount();
        Log.d(TAG, "database_photos = " + database_photos);
        JsonObjectRequest requestCollectionInfo = new JsonObjectRequest
                (Request.Method.GET, UrlGenerator.CollectionInfo(), null,
                        (JSONObject collectionInfo) -> {
                            try {

                                Log.d(TAG, " request Collection Info " + collectionInfo.toString());
                                Log.d(TAG, "total_photos " + collectionInfo.getString("total_photos"));
                                Integer total_photos = new Integer(collectionInfo.getString("total_photos"));
                                Log.d(TAG, "total_photos = " + total_photos);
                                if (total_photos.equals(database_photos) == false) {
                                    connectAndUpdate(total_photos - database_photos);
                                }
                                else {
                                    Log.d(TAG, "No need to Update!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    Log.d(TAG, "error in request Collection Info: " + error.getMessage());

                });
        Network.getInstance(Contract.context).addToRequestQueue(requestCollectionInfo);


    }

    static void connectAndUpdate(int absent_photos) {
        Log.d(TAG, "connectAndUpdate at " + absent_photos + " absent");
        for (int page = 1; page <= (absent_photos - 1) / 30 + 1; page++) {
            Log.d(TAG, "connectAndUpdate (" + page + "/" + (absent_photos - 1) / 30 + 1 + ") started");

            int finalPage = page;
            JsonArrayRequest collectionPhotosRequest = new JsonArrayRequest
                    (Request.Method.GET, UrlGenerator.CollectionPhotos(finalPage, 30), null,
                            (JSONArray photosResponse) -> {
                                for (int i = 0; 30 * (finalPage - 1) + i < absent_photos; i++) {
                                    try {

                                        JSONObject jsonObject = photosResponse.getJSONObject(i);
                                        Pictures pictures = new Pictures();
                                        pictures.setId(jsonObject.getString("id"));
                                        pictures.setLiked(false);
                                        pictures.setUrlRaw(jsonObject.getJSONObject("urls").getString("raw"));
                                        pictures.setUrlFull(jsonObject.getJSONObject("urls").getString("full"));
                                        pictures.setUrlRegular(jsonObject.getJSONObject("urls").getString("regular"));
                                        pictures.setUrlSmall(jsonObject.getJSONObject("urls").getString("small"));
                                        pictures.setUrlThumb(jsonObject.getJSONObject("urls").getString("thumb"));
                                        pictures.setUpdate_at(jsonObject.getString("updated_at"));
                                        pictures.setFilePath(null);

                                        new Thread(() -> database.picturesDao().insert(pictures)).start();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d(TAG, " collection Photos Request(" + finalPage + ") " + photosResponse.toString());
                            },
                            error -> {
                                Log.d(TAG, "error in collection Photos Request: " + error.getMessage());
                            });

            Network.getInstance(Contract.context).addToRequestQueue(collectionPhotosRequest);
        }
        Log.d(TAG,"All absent pictures("+absent_photos+") have been Updated!");
    }
}
