package com.ervinxie.alue_client;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ervinxie.alue_client.adapter.ImageArrayAdapter;
import com.ervinxie.alue_client.data.GlideApp;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AlueMainActivity extends AppCompatActivity {

    static final String TAG = "AlueMainActivity: ";

    Button load;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Contract.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alue_activity_main);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Bitmap> myDataset = new ArrayList<Bitmap>();

        ArrayList<String> urls = new ArrayList<String>();
        urls.add("https://img.freepik.com/free-photo/blue-mountains-famous-tourism-scenery-lijiang_1417-1143.jpg?size=626&ext=jpg");
        urls.add("https://images.pexels.com/photos/459225/pexels-photo-459225.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500");
        urls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS1nfqtF9wv0Y-hj_mQfWFiyhq_0xl0ZCVhioBK0tOzmNwokGlF");


        load = findViewById(R.id.load);
        load.setOnClickListener(v -> {
            for (int i = 0; i < urls.size(); i++) {

                int finalI = i;
                new Thread(() -> {
                    Bitmap bitmap = null;
                    try {
                        bitmap = GlideApp
                                .with(Contract.context)
                                .asBitmap()
                                .load(urls.get(finalI))
                                .submit()
                                .get();
                        Log.d(TAG,urls.get(finalI)+" success!");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myDataset.add(bitmap);
                    mAdapter = new ImageArrayAdapter(myDataset);
                    this.runOnUiThread(()->{
                        recyclerView.setAdapter(mAdapter);
                    });

                }).start();
            }
        });

    }

}
