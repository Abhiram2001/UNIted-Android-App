package com.example.united;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.united.Utils.UserStatus;
import com.squareup.picasso.Picasso;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImageViewActivity extends AppCompatActivity {

    ImageView imageView, imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN );

        String url = getIntent().getStringExtra("url");
        String postby = getIntent().getStringExtra("postedby");
        String posttime = getIntent().getStringExtra("posttime");

        Glide
                .with(ImageViewActivity.this)
                .load(url)
                .centerCrop()
                .fitCenter()
                .placeholder(R.drawable.profile)
                .into(imageView);

//        Picasso.get().load(url).into(imageView);

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(url, postby, posttime);
            }
        });
    }

    private void DownloadImage(String url, String postby, String posttime) {
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, "From UNIted - "+postby+""+posttime+".jpg");
        downloadManager.enqueue(request);
    }


}