package com.ervinxie.alue_client.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.adapter.ImageArrayAdapter;
import com.ervinxie.alue_client.data.AppDatabase;
import com.ervinxie.alue_client.data.DataManager;
import com.ervinxie.alue_client.data.DataTest;
import com.ervinxie.alue_client.data.Pictures;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.util.FullscreenActivity;
import com.ervinxie.alue_client.util.OnVerticalScrollListener;

import java.util.List;

public class AlueMainActivity extends FullscreenActivity {

    static final String TAG = "AlueMainActivity: ";

    ImageButton nav_picture, nav_info;
    LinearLayout linearLayout;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Contract.context = getApplicationContext();
        Contract.alueMainActivity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alue_main_activity_fullscreen);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        linearLayout = findViewById(R.id.contentPanel);
        recyclerView = findViewById(R.id.recycler);
        mContentView = linearLayout;
        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        nav_picture = findViewById(R.id.imageButton);
        nav_info = findViewById(R.id.nav_info);

        nav_info.setOnClickListener(v -> {
            Intent intent = new Intent(Contract.context, AboutActicity.class);
            startActivity(intent);
        });
        nav_picture.setOnClickListener(v -> {

        });

        new Thread(() -> {
            DataManager.updateDatabase();
        }).start();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            show();
            updateDatabaseAndLoadContent();
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledUp() {
                super.onScrolledUp();
                show();
            }

            @Override
            public void onScrolledDown() {
                super.onScrolledDown();
                hide();
            }
        });

        database = AppDatabase.getInstance(Contract.context);
        loadContent();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    private void updateDatabaseAndLoadContent() {
        new Thread(() -> {
            Log.d(TAG, "Database updating");
            Contract.isLoading = true;
            DataManager.updateDatabase();
            while (Contract.isLoading) ;

            Log.d(TAG, "Database reading");
            List<Pictures> picturesList = database.picturesDao().getAllPicturesDesc();
            for (int i = 0; i < picturesList.size(); i++) {
                Log.d(TAG, picturesList.get(i).info());
            }
            adapter = new ImageArrayAdapter(picturesList);

            this.runOnUiThread(() -> {
                recyclerView.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();

    }

    private void loadContent() {
        new Thread(() -> {


            List<Pictures> picturesList = database.picturesDao().getAllPicturesDesc();
            for (int i = 0; i < picturesList.size(); i++) {
                Log.d(TAG, picturesList.get(i).info());
            }
            adapter = new ImageArrayAdapter(picturesList);

            this.runOnUiThread(() -> {
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            });
        }).start();
    }

}
