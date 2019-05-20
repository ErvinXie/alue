package com.ervinxie.alue_client.data;

import android.database.SQLException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ervinxie.alue_client.Contract;
import com.ervinxie.alue_client.R;

import com.ervinxie.alue_client.network.Network;
import com.ervinxie.alue_client.util.AboutPictures;
import com.ervinxie.alue_client.util.UrlGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DataTest extends AppCompatActivity {
    static final String TAG = "DataTest: ";

    ImageView imageView1, imageView2, imageView3;
    TextView info;
    EditText url;
    Button get, save, load, clear, insert, query, update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Contract.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_test_view);
        imageView1 = findViewById(R.id.data_test_image_1);
        imageView2 = findViewById(R.id.data_test_image_2);
        info = findViewById(R.id.data_test_info);
        url = findViewById(R.id.data_test_url);
        get = findViewById(R.id.data_test_get_image_from_url);
        save = findViewById(R.id.data_test_save_image);
        load = findViewById(R.id.data_test_load_image);
        clear = findViewById(R.id.data_test_Clear_Database);
        insert = findViewById(R.id.date_test_insert);
        query = findViewById(R.id.data_test_query);

        update = findViewById(R.id.data_test_update);

        get.setOnClickListener(v -> {
            String Url = url.getText().toString();
            GlideApp
                    .with(Contract.context)
                    .load(Url)
                    .into(imageView1);
        });


        class SaveAgent implements DiskReader.DiskSaveInterface {
            Bitmap bitmap;
            TextView info;

            SaveAgent(Bitmap bitmap1, TextView info1) {
                bitmap = bitmap1;
                info = info1;
            }


            @Override
            public Bitmap getBitmap() {
                return bitmap;
            }

            @Override
            public void saved(Boolean saved) {
                if (saved) {
                    info.setText("成功保存");
                } else {
                    info.setText("保存失败");
                }
            }

            @Override
            public String getDir() {
                return Contract.context.getFilesDir() + "/test.png";
            }
        }

        class ReadAgent implements DiskReader.DiskReadInterface {

            ImageView imageView;
            TextView info;

            ReadAgent(ImageView imageView1, TextView textView) {
                imageView = imageView1;
                info = textView;
                info.setText("读取成功");
            }

            @Override
            public String getDir() {
                return Contract.context.getFilesDir() + "/test.png";
            }

            @Override
            public void readed(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        }

        save.setOnClickListener(v -> {
            new DiskReader(new SaveAgent(AboutPictures.imageViewToBitmap(imageView1), info));
        });

        load.setOnClickListener(v -> {
//            new DiskReader(new ReadAgent(imageView2, info));//自己写的读取，备用

            //这种缓存策略可以解决同名图片读取的问题
            GlideApp
                    .with(Contract.context)
                    .load(Contract.context.getFilesDir() + "/test.png")
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView2);
        });


        AppDatabase database = AppDatabase.getInstance(Contract.context);

        insert.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    database.picturesDao().insert(new Pictures("nul", null, null, null, null, null, null, false, null));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        query.setOnClickListener(v -> {
            new Thread(() -> {
                List<Pictures> list = database.picturesDao().getAllPictures();
                String s = new String();
                s += "Pictures Amount: " + database.picturesDao().getPicturesAmount() + "\n";
                for (int i = 0; i < list.size(); i++) {
                    s += (list.get(i).info());
                }

                String finalS = s;
                this.runOnUiThread(() -> {
                    info.setText(finalS);
                });

            }).start();

        });

        clear.setOnClickListener(v -> {
            new Thread(() -> {
                database.picturesDao().delete();
            }).start();
        });



        update.setOnClickListener(v -> {
            new Thread(()->{
                DataManager.updateDatabase();
            }).start();

        });

    }


}
