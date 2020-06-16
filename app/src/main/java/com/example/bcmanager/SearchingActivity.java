package com.example.bcmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class SearchingActivity extends AppCompatActivity {

    private BCMApplication myApp;
    private ArrayList<CardInfoItem.cardInfo> SearchCardsList;
    private Button btn_Search;
    private TextView noSearchCard_text;
    private TextView SearchedCard_text;
    private CardRecyclerViewAdapter Searchadapter = null;
    private RecyclerView recyclerView;
    private EditText info_searchtext;
    private  String searchtext;
    ArrayList<String> SearchtmpCards = new ArrayList<String>(); // done == 0 인 카드 이미지들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.actionbar_title_nobtn); // 커스텀 사용할 파일 위치
        getSupportActionBar().setTitle("BCManager");

        noSearchCard_text = findViewById(R.id.textview_noSearchCard);
        SearchedCard_text = findViewById(R.id.textview_SearchedCard);
        recyclerView = findViewById(R.id.recyclerview);
        btn_Search = findViewById(R.id.search_btn);
        info_searchtext = findViewById(R.id.search_part);

        myApp = (BCMApplication) getApplication(); //user정보 가져오기

        Intent intent = getIntent();
        SearchCardsList = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setNestedScrollingEnabled(false);

        if (SearchCardsList.isEmpty()) {
            SearchedCard_text.setVisibility(View.GONE);
            noSearchCard_text.setVisibility(View.VISIBLE);
        }

        btn_Search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchtext = info_searchtext.getText().toString();
                Log.d("searchtext",searchtext);
                if(searchtext.getBytes().length <= 0) {
                    Log.d("searchtext, if",searchtext);
                    getCardInfo();
                }
                else{
                    Log.d("searchtext,else",searchtext);
                    getSearchCardInfo();
                }


            }
        });


    }

    void getCardInfo() {
        try { //MainActivity.GET_CARDS_INFO
            HttpConnection httpConn = new HttpConnection(new URL(MainActivity.GET_CARDS_INFO));
            httpConn.requestGetCards(myApp.userID, new OnRequestCompleteListener() {
                @Override
                public void onSuccess(@org.jetbrains.annotations.Nullable String data) {

                    if (data != null && !data.isEmpty()) {
                        SearchCardsList.clear();
                        Log.d("성공 ", data);
                        Gson gson = new GsonBuilder().create();
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
                        JsonArray jsonArray = (JsonArray) jsonObject.get("cardInfo");

                        CardInfoItem.cardInfo result;
                        for (int i = 0; i < jsonArray.size(); i++) {
                            final JsonObject j = jsonArray.get(i).getAsJsonObject();
                            result = gson.fromJson(j, CardInfoItem.cardInfo.class);
                            SearchCardsList.add(result);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Log.d(TAG_, String.valueOf(SearchCardsList.size()));
                                SearchedCard_text.setVisibility(View.VISIBLE);
                                noSearchCard_text.setVisibility(View.GONE);
                                Searchadapter = new CardRecyclerViewAdapter(SearchingActivity.this, SearchCardsList);
                                recyclerView.setAdapter(Searchadapter);
                                Searchadapter.notifyDataSetChanged();

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SearchedCard_text.setVisibility(View.GONE);
                                noSearchCard_text.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                @Override
                public void onError() {

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    void getSearchCardInfo() {
        try { //MainActivity.GET_CARDS_INFO
            HttpConnection httpConn = new HttpConnection(new URL(MainActivity.SEARCH_CARD_GET_INFO));
            httpConn.requestSearchGetCards(myApp.userID, searchtext, new OnRequestCompleteListener() {
                @Override
                public void onSuccess(@org.jetbrains.annotations.Nullable String data) {

                    if (data != null && !data.isEmpty()) {
                        SearchCardsList.clear();
                        Log.d("성공 ", data);
                        Gson gson = new GsonBuilder().create();
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
                        JsonArray jsonArray = (JsonArray) jsonObject.get("cardInfo");

                        CardInfoItem.cardInfo result;
                        for (int i = 0; i < jsonArray.size(); i++) {
                            final JsonObject j = jsonArray.get(i).getAsJsonObject();
                            result = gson.fromJson(j, CardInfoItem.cardInfo.class);
                            SearchCardsList.add(result);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Log.d(TAG_, String.valueOf(SearchCardsList.size()));
                                SearchedCard_text.setVisibility(View.VISIBLE);
                               noSearchCard_text.setVisibility(View.GONE);
                                Searchadapter = new CardRecyclerViewAdapter(SearchingActivity.this, SearchCardsList);
                                recyclerView.setAdapter(Searchadapter);
                                Searchadapter.notifyDataSetChanged();

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SearchedCard_text.setVisibility(View.GONE);
                                noSearchCard_text.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                @Override
                public void onError() {

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
