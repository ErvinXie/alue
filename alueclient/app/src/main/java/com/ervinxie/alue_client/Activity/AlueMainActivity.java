package com.ervinxie.alue_client.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.adapter.ImageArrayAdapter;
import com.ervinxie.alue_client.data.AppDatabase;
import com.ervinxie.alue_client.data.DataManager;
import com.ervinxie.alue_client.data.GlideApp;
import com.ervinxie.alue_client.data.Pictures;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.util.FullscreenActivity;
import com.ervinxie.alue_client.util.OnVerticalScrollListener;

import java.util.List;

public class AlueMainActivity extends FullscreenActivity {

    static final String TAG = "AlueMainActivity: ";

    ImageButton nav_bubble, nav_info, nav_like;
    LinearLayout linearLayout;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    AppDatabase database;

    Boolean liked = false;

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

        nav_bubble = findViewById(R.id.nav_bubble);
        nav_info = findViewById(R.id.nav_info);
        nav_like = findViewById(R.id.like);

        nav_info.setOnClickListener(v -> {
            delayedHide(3000);
            Intent intent = new Intent(Contract.context, AboutActicity.class);
            startActivity(intent);
        });
        nav_bubble.setOnClickListener(v -> {
            delayedHide(3000);
            swipeRefreshLayout.setRefreshing(true);
            updateDatabaseAndLoadContent();
        });
        nav_like.setOnClickListener(v -> {
            delayedHide(3000);
            if (liked) {
                liked = false;
                loadContent();
                GlideApp
                        .with(Contract.context)
                        .load(R.drawable.ic_baseline_favorite_border_144px)
                        .error(R.drawable.ic_clear_white_144dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(nav_like);
            } else {
                liked = true;
                loadContent();
                GlideApp
                        .with(Contract.context)
                        .load(R.drawable.ic_baseline_favorite_144px)
                        .error(R.drawable.ic_clear_white_144dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(nav_like);
            }
        });

        updateDatabaseAndLoadContent();

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
                delayedHide(3000);
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
            loadContent();
        }).start();

    }

    private void loadContent() {
        new Thread(() -> {
            List<Pictures> picturesList = database.picturesDao().getAllPicturesDesc();
            if (liked) {
                for (int i = 0; i < picturesList.size(); i++) {
                    if (picturesList.get(i).getLiked() == false) {
                        picturesList.remove(i);
                        i--;
                    }
                }
            }
            adapter = new ImageArrayAdapter(picturesList);

            this.runOnUiThread(() -> {
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            });
        }).start();
    }

}
