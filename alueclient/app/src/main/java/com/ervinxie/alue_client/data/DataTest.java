package com.ervinxie.alue_client.data;

import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ervinxie.alue_client.Contract;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.ViewModels.ImageViewModel;
import com.ervinxie.alue_client.util.AboutPictures;

import java.util.List;

public class DataTest extends AppCompatActivity {
    static final String TAG = "DataTest: ";

    ImageView imageView1, imageView2,imageView3;
    TextView info;
    EditText url;
    Button get, save, load, create, insert, query,saveurl;

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
        create = findViewById(R.id.data_test_Create_Database);
        insert = findViewById(R.id.date_test_insert);
        query = findViewById(R.id.data_test_query);

        saveurl = findViewById(R.id.data_test_save_url);
        imageView3 = findViewById(R.id.data_test_imageview3);

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
                    .load(Contract.context.getFilesDir()+"/test.png")
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView2);
        });


        AppDatabase database = AppDatabase.getInstance(Contract.context);

        insert.setOnClickListener(v -> {
            pid++;
            new Thread(()->{
                Log.d(TAG,"Insert a "+pid);
                try {
                    database.picturesDao().insert(new Pictures(pid, "nul", null, false));
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }).start();
        });

        query.setOnClickListener(v -> {
            new Thread(() -> {
                List<Pictures> list = database.picturesDao().getAllPictures();
                String s = new String();
                for (int i = 0; i < list.size(); i++) {
                    s+=(list.get(i).info());
                }

                String finalS = s;
                this.runOnUiThread(()->{
                    info.setText(finalS);
                });

            }).start();

        });

        ImageViewModel imageViewModel = ViewModelProviders.of(this).get(ImageViewModel.class);
        imageViewModel.getUrl().observe(this,url->{
            GlideApp
                    .with(Contract.context)
                    .load(url)
                    .into(imageView3);
        });
        saveurl.setOnClickListener(v->{
            imageViewModel.surl = url.getText().toString();
            imageViewModel.loadUrl();
        });

    }

    public static int pid = 0;

}
