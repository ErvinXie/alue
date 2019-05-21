package com.ervinxie.alue_client.network;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ervinxie.alue_client.Contract;
import com.ervinxie.alue_client.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkTest extends AppCompatActivity {

    static final String TAG = "Network test: ";
    TextView textView1;
    EditText Url;
    Button request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Contract.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_test_view);
        textView1 = findViewById(R.id.network_test_textView1);
        Url = findViewById(R.id.network_test_url);
        request = findViewById(R.id.network_test_request);

        Button finish = findViewById(R.id.finish);
        finish.setOnClickListener(v->{
            finish();
        });

        request.setOnClickListener(v->{
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, Url.getText().toString(), null,
                            (JSONArray response) -> {
//                                textView1.setText("Response: " + response.toString());
                                String s="";
                                for(int i=0;i<response.length();i++){
                                    try {
                                        s+=response.getJSONObject(i).getString("id")+"\n";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                textView1.setText(s);
                            },
                            error -> {
                                textView1.setText("Error: "+error.getMessage());

                            });

            Network.getInstance(Contract.context).addToRequestQueue(jsonArrayRequest);
        });
    }
}
