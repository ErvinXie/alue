package com.ervinxie.alue_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ervinxie.alue_client.adapter.ImageArrayAdapter;
import com.ervinxie.alue_client.data.AppDatabase;
import com.ervinxie.alue_client.data.DataTest;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.data.Pictures;
import com.ervinxie.alue_client.network.NetworkTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlueMainActivity extends AppCompatActivity {

    static final String TAG = "AlueMainActivity: ";

    Button load,to_data_test,to_net_test;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Contract.context = getApplicationContext();
        Contract.alueMainActivity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alue_activity_main);

        info = findViewById(R.id.info);

        recyclerView = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        AppDatabase database = AppDatabase.getInstance(Contract.context);

        load = findViewById(R.id.load);
        to_data_test = findViewById(R.id.to_data_test);
        to_data_test.setOnClickListener(v->{
            Intent intent = new Intent(this,DataTest.class);
            startActivity(intent);
        });

        to_net_test = findViewById(R.id.to_network_test);
        to_net_test.setOnClickListener(v->{
            Intent intent = new Intent(this, NetworkTest.class);
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
                    info.setText("Loaded");
                });
            }).start();
            info.setText("Loading");
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG,"onPostResume: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState: ");
    }
}
