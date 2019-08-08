package com.example.messenger.User;

import android.os.Bundle;

import com.example.messenger.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.messenger.User.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class usersActivity extends AppCompatActivity {
    private long lastTimeCliked = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }
    @Override
    public void onBackPressed() {
        long time = (new Date()).getTime();
        if (time - lastTimeCliked >= 2000) {
            Toast.makeText(this, "Click Back Again to logout!", Toast.LENGTH_SHORT).show();
            lastTimeCliked = time;
            return;
        } else {
            FirebaseAuth.getInstance().signOut();
            finish();
            super.onBackPressed();
        }
    }
}