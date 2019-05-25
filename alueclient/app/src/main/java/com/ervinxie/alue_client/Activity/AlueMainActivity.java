package com.ervinxie.alue_client.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.adapter.ImageArrayAdapter;
import com.ervinxie.alue_client.data.AppDatabase;
import com.ervinxie.alue_client.data.DataManager;
import com.ervinxie.alue_client.util.DrawableGen;
import com.ervinxie.alue_client.util.myglide.GlideApp;
import com.ervinxie.alue_client.data.Pictures;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.util.OnVerticalScrollListener;

import java.util.List;

public class AlueMainActivity extends FullscreenActivity {

    static final String TAG = "AlueMainActivity: ";

    ImageButton nav_bubble, nav_info, nav_like, center_like;
    LinearLayout linearLayout,nolike;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ImageArrayAdapter adapter;

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
        nolike = findViewById(R.id.no_like);
        nolike.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.recycler);
        mContentView = linearLayout;
        swipeRefreshLayout = findViewById(R.id.swipeContainer);

        nav_bubble = findViewById(R.id.nav_bubble);
        nav_info = findViewById(R.id.nav_info);
        nav_like = findViewById(R.id.like);
        center_like = findViewById(R.id.center_like);

        nav_info.setOnClickListener(v -> {
            Intent intent = new Intent(Contract.context, AboutActicity.class);
            startActivity(intent);
        });
        nav_bubble.setOnClickListener(v -> {
            updateDatabaseAndLoadContent();
        });


        center_like.setOnClickListener(likeListener);
        nav_like.setOnClickListener(likeListener);

        updateDatabaseAndLoadContent();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            show();
            updateDatabaseAndLoadContent();
        });

        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorWhite);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
        UIHandler.sendEmptyMessage(Refreshing);
        new Thread(() -> {

            new DataManager(){
                @Override
                public void OnSuccess() {
                    super.OnSuccess();
                    UIHandler.sendEmptyMessage(loadContent);
                }

                @Override
                public void OnFailed() {
                    super.OnFailed();
                    UIHandler.sendEmptyMessage(RefreshedFail);
                }

                @Override
                public void OnTimeOut() {
                    super.OnTimeOut();
                    UIHandler.sendEmptyMessage(RefreshTimeout);
                }
            }.updateDatabase();

        }).start();


    }

    private void loadContentFromDataBase() {
        runOnUiThread(()->{
            nav_bubble.setClickable(false);
            swipeRefreshLayout.setRefreshing(true);
        });
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
            });
            UIHandler.sendEmptyMessage(RefreshedOk);

        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIHandler.sendEmptyMessage(loadContent);
    }


    private static final int Refreshing = 1;
    private static final int RefreshedOk = 2;
    private static final int RefreshedFail = 3;
    private static final int RefreshFinish = 9;
    private static final int RefreshTimeout = 8;

    private static final int SetLikeClickable = 4;
    private static final int SetLike = 11;
    private static final int SetDislike = 12;

    private static final int loadContent = 5;
    private static final int ShowNoLike = 6;
    private static final int HideNoLike = 7;



    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Refreshing:{
                    swipeRefreshLayout.setRefreshing(true);
                    nav_bubble.setClickable(false);
                    GlideApp
                            .with(Contract.context)
                            .load(DrawableGen.getCircularProgressDrawable(30, Color.WHITE))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(nav_bubble);
                    break;
                }
                case RefreshedOk:{
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_bubble_chart_colorful_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(nav_bubble);
                    swipeRefreshLayout.setRefreshing(false);
                    this.sendEmptyMessageDelayed(RefreshFinish,2000);
                    break;
                }
                case RefreshedFail:{
                    ToastShort("加载失败");
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(nav_bubble);
                    swipeRefreshLayout.setRefreshing(false);
                    this.sendEmptyMessageDelayed(RefreshFinish,2000);
                    break;
                }
                case RefreshTimeout:{
                    ToastShort("请求超时");
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(nav_bubble);
                    swipeRefreshLayout.setRefreshing(false);
                    this.sendEmptyMessageDelayed(RefreshFinish,2000);
                    break;
                }
                case RefreshFinish:{
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_bubble_chart_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(nav_bubble);
                    nav_bubble.setClickable(true);

                    break;
                }
                case SetLikeClickable:{
                    nav_like.setClickable(true);
                    center_like.setClickable(true);
                    break;
                }
                case SetDislike:{
                    nav_like.setClickable(false);
                    center_like.setClickable(false);
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_favorite_border_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(nav_like);
                    break;
                }
                case SetLike:{
                    nav_like.setClickable(false);
                    center_like.setClickable(false);
                    GlideApp
                            .with(Contract.context)
                            .load(R.drawable.ic_baseline_favorite_144px)
                            .error(R.drawable.ic_clear_white_144dp)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(nav_like);
                    break;
                }
                case loadContent:{
                    loadContentFromDataBase();
                    break;
                }
                case ShowNoLike:{
                    nolike.setVisibility(View.VISIBLE);
                    break;
                }
                case HideNoLike:{
                    nolike.setVisibility(View.GONE);
                    break;
                }

                default:{
                    break;
                }
            }




        }
    };
    View.OnClickListener likeListener = v -> {
        liked = !liked;
        UIHandler.sendEmptyMessage(liked?SetLike:SetDislike);
        refreshLike();
    };
    private void refreshLike() {
        new Thread(() -> {
            try {
                List<Pictures> adapterPicturesList = adapter.getPicturesList();
                List<Pictures> picturesList = database.picturesDao().getAllPicturesDesc();
                if (liked == false) {
                    UIHandler.sendEmptyMessage(HideNoLike);
                    for (int i = 0; i < picturesList.size(); i++) {
                        if (picturesList.get(i).getLiked() == false) {
                            adapter.addData(i,picturesList.get(i));
                        }
                    }
                } else {
                    for(int i=picturesList.size()-1;i>=0;i--) {
                        if (picturesList.get(i).getLiked() == false) {
                            adapter.removeData(i);
                        }
                    }
                    if(adapterPicturesList.size()==0){
                        UIHandler.sendEmptyMessage(ShowNoLike);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            UIHandler.sendEmptyMessageDelayed(SetLikeClickable,500);
        }).start();
    }
}
