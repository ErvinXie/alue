package com.ervinxie.alue_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ervinxie.alue_client.adapter.ImageArrayAdapter;
import com.ervinxie.alue_client.data.AppDatabase;
import com.ervinxie.alue_client.data.DataTest;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.data.Pictures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlueMainActivity extends AppCompatActivity {

    static final String TAG = "AlueMainActivity: ";

    Button load,to_data_test;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Contract.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alue_activity_main);

        recyclerView = findViewById(R.id.recycler);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        AppDatabase database = AppDatabase.getInstance(Contract.context);

        load = findViewById(R.id.load);
        to_data_test = findViewById(R.id.to_data_test);
        to_data_test.setOnClickListener(v->{
            Intent intent = new Intent(this,DataTest.class);
            startActivity(intent);
        });
        load.setOnClickListener(v -> {
            new Thread(()->{
                List<Pictures> picturesList =  database.picturesDao().getAllPictures();
                for(int i=0;i<picturesList.size();i++){
                    Log.d(TAG,picturesList.get(i).info());
                }
                adapter = new ImageArrayAdapter(picturesList);

                this.runOnUiThread(()->{
                    recyclerView.setAdapter(adapter);
                });
            }).start();
        });



    }

}
