package com.ervinxie.alue_client.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImageViewModel extends ViewModel {

    static final String TAG = "ImageViewModel: ";

    private MutableLiveData<String> url;

    public LiveData<String> getUrl() {
        if(url==null){
            url = new MutableLiveData<>();
        }
        loadUrl();

        return url;
    }

    public String surl="";
    public void loadUrl() {
        url.setValue(surl);
    }
}
