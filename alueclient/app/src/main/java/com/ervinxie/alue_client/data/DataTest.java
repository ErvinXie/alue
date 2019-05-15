package com.ervinxie.alue_client.data;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ervinxie.alue_client.Contract;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.util.AboutPictures;

public class DataTest extends AppCompatActivity {
    static final String TAG = "DataTest: ";

    ImageView imageView1, imageView2;
    TextView info;
    EditText url;
    Button get, save, load;

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
            new DiskReader(new ReadAgent(imageView2, info));
        });
    }


}
