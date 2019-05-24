package com.ervinxie.alue_client.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.network.Network;
import com.ervinxie.alue_client.util.UrlGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataManager {
    static final String TAG = "Data Manager: ";

    public DataManager() { }

    public static AppDatabase database = AppDatabase.getInstance(Contract.context);

    public void updateDatabase() {
        int database_photos = database.picturesDao().getPicturesAmount();
        Log.d(TAG, "database_photos = " + database_photos);

        JsonObjectRequest requestCollectionInfo = new JsonObjectRequest
                (Request.Method.GET, UrlGenerator.CollectionInfo(), null,
                        (JSONObject collectionInfo) -> {
                            try {
                                Log.d(TAG, " request Collection Info " + collectionInfo.toString());
                                Log.d(TAG, "total_photos " + collectionInfo.getString("total_photos"));
                                Integer total_photos =
                                        new Integer(collectionInfo.getString("total_photos"));
                                Log.d(TAG, "total_photos = " + total_photos);
                                if (total_photos.equals(database_photos) == false) {
                                    connectAndUpdate(total_photos - database_photos);
                                } else {
                                    OnSuccess();
                                    Log.d(TAG, "No need to Update!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    if(error.getNetworkTimeMs()>10000){
                        OnTimeOut();
                    }
                    else{
                        OnFailed();
                    }
                    Log.d(TAG, "error in request Collection Info: " + error.getMessage());
                });
        Network.getInstance(Contract.context).addToRequestQueue(requestCollectionInfo);
    }
    public void OnSuccess(){
        OnFinished();}
    public void OnFailed(){
        OnFinished();}
    public void OnFinished(){}
    public void OnTimeOut(){
        OnFinished();
    }

    private static int pictureId;

    void connectAndUpdate(int absent_photos) throws InterruptedException {
        Log.d(TAG, "connectAndUpdate at " + absent_photos + " absent");
        new Thread(() -> {
            pictureId = database.picturesDao().getMaxId();
            for (int page = (absent_photos - 1) / 30 + 1; page >= 0; page--) {
                Log.d(TAG, "connectAndUpdate (" + page + "/" + ((absent_photos - 1) / 30 + 1) + ") started");
                int finalPage = page;
                JsonArrayRequest collectionPhotosRequest = new JsonArrayRequest
                        (Request.Method.GET, UrlGenerator.CollectionPhotos(finalPage, 30), null,
                                (JSONArray photosResponse) -> {
                                    Log.d(TAG, "requesting: " + UrlGenerator.CollectionPhotos(finalPage, 30));
                                    for (int i = photosResponse.length() - 1; i >= 0; i--) {
                                        try {

                                            JSONObject jsonObject = photosResponse.getJSONObject(i);
                                            Log.d(TAG, (i + 1) + ": " + jsonObject.toString());
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

                                            pictures.setDescription(jsonObject.getString("description"));
                                            pictures.setTitle("description");


                                            Thread save = new Thread(() -> {
                                                Pictures temp =
                                                        database.picturesDao().getPicturesById(pictures.getId());
                                                if (temp != null) {
                                                    Log.d(TAG, pictures.getId() + " " + pictures.getTitle() + " already exists");
                                                } else {
                                                    pictureId++;
                                                    pictures.setPictureId(pictureId);
                                                    Log.d(TAG, pictures.info());
                                                    database.picturesDao().insert(pictures);
                                                }
                                            });
                                            save.start();
                                            save.join();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.d(TAG, " collection Photos Request(" + finalPage + ") " + photosResponse.toString());
                                    if (finalPage == 0)
                                        OnSuccess();

                                },
                                error -> {
                                    Log.d(TAG, "error in collection Photos Request: " + error.getMessage());
                                    if (finalPage == 0){
                                        if(error.getNetworkTimeMs()>10000){
                                            OnTimeOut();
                                        }
                                        else{
                                            OnFailed();
                                        }
                                    }

                                });

                collectionPhotosRequest.setShouldCache(false);
                Network.getInstance(Contract.context).addToRequestQueue(collectionPhotosRequest);
            }

        }).start();
    }

    public void clear_all() {
        database.picturesDao().delete();
    }
}
