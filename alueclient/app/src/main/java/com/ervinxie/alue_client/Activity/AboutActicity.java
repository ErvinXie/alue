package com.ervinxie.alue_client.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.DataTest;
import com.ervinxie.alue_client.util.FullscreenActivity;

public class AboutActicity extends FullscreenActivity {

    ImageButton nav_bubble;
    Button offer_me_coffe,to_data_test;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_view);

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        linearLayout = findViewById(R.id.contentPanel);
        to_data_test = findViewById(R.id.to_data_test);
        nav_bubble = findViewById(R.id.nav_bubble);

        mContentView = linearLayout;
        AUTO_HIDE = true;
        mContentView.setOnClickListener(v->{
            toggle();
            delayedHide(3000);
        });


        to_data_test.setOnClickListener(v -> {
            Intent intent = new Intent(this, DataTest.class);
            startActivity(intent);
        });


        offer_me_coffe = findViewById(R.id.offer_me_coffee);

        nav_bubble.setOnClickListener(v->finish());

    }
}
