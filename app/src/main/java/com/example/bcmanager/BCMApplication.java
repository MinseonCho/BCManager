package com.example.bcmanager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class BCMApplication  extends Application{

    public boolean isLogined;

    @Override
    public void onCreate() {
        super.onCreate();
        isLogined = false;

    }
    public void showPopup(View v, PopupMenu.OnMenuItemClickListener listener) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(listener);
        MenuInflater inflater = popup.getMenuInflater();
        if (isLogined) inflater.inflate(R.menu.menu_login, popup.getMenu());
        else inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

}
