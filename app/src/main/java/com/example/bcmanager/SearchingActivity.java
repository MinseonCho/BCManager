package com.example.bcmanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import java.util.Objects;

public class SearchingActivity extends AppCompatActivity {

    private BCMApplication myApp;
    private SearchView mSearchView;
//    public ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.actionbar_title_nobtn); // 커스텀 사용할 파일 위치
        getSupportActionBar().setTitle("BCManager");

        myApp = (BCMApplication) getApplication(); //user정보 가져오기

        Intent intent = getIntent();

        final SearchView searchView;
        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) { //검색 버튼 눌러졌을 때 이벤트
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) { //검색어가 변경되었을때 이벤트
                return false;
            }

        });

        Button submitBtn = findViewById(R.id.search_btn);
        submitBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                searchView.setQuery(searchView.getQuery(), true);


            }

        });

//        iv = findViewById(R.id.imageview);

//        if( intent != null){
//            byte[] bytes = intent.getByteArrayExtra("data");
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            iv.setImageBitmap(bitmap);
//        }


    }
}
