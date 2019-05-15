package com.ervinxie.alue_client.data;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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


        class DownloadAgent implements Downloader.AfterDownload {
            ImageView imageView;

            public DownloadAgent(ImageView v) {
                imageView = v;

            }

            @Override
            public void onFinished(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);

            }
        }

        get.setOnClickListener(v -> {
            String Url = url.getText().toString();
            new Downloader(new DownloadAgent(imageView1), Url);
        });


        class SaveAgent implements ImageSaver.AfterSaved {
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
            public void onFinished(Boolean saved) {
                if (saved) {
                    info.setText("成功保存");
                } else {
                    info.setText("保存失败");
                }
            }
        }

        save.setOnClickListener(v -> {
            new ImageSaver(new SaveAgent(AboutPictures.imageViewToBitmap(imageView1), info), "test.png");
        });
    }


}
