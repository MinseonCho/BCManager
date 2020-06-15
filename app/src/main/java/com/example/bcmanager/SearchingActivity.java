package com.example.bcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class SearchingActivity extends AppCompatActivity {

    public ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        iv = findViewById(R.id.imageview);

//        Intent intent = getIntent();
//        if( intent != null){
//            byte[] bytes = intent.getByteArrayExtra("data");
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            iv.setImageBitmap(bitmap);
//        }


    }
}
