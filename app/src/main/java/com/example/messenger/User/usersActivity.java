package com.example.messenger.User;

import android.os.Bundle;

import com.example.messenger.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.messenger.User.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class usersActivity extends AppCompatActivity {
    private long lastTimeClicked = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_users);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    @Override
    public void onBackPressed() {
        long time = (new Date()).getTime();
        if (time - lastTimeClicked >= 2000) {
            Toast.makeText(this, "Click Back Again to logout!", Toast.LENGTH_SHORT).show();
            lastTimeClicked = time;
            return;
        } else {
            FirebaseAuth.getInstance().signOut();
            finish();
            super.onBackPressed();
        }
    }
}