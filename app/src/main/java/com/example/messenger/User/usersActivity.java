package com.example.messenger.User;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.messenger.R;
import com.example.messenger.Tools.GlideApp;
import com.example.messenger.User.ui.main.Chats_Fragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.messenger.User.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class usersActivity extends AppCompatActivity {
    private long lastTimeClicked = 0;
    private ArrayList<String> UIDS;
    private ArrayList<ChildEventListener> listeners;
    private ChildEventListener childEventListener, temporaryListener;
    private DatabaseReference dbR;
    private FirebaseUser mUser;
    private NotificationManagerCompat notificationManager;
    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        UIDS = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        dbR = FirebaseDatabase.getInstance().getReference();
        notificationManager = NotificationManagerCompat.from(this);
        listeners = new ArrayList<>();
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
        initiateNotifications();
    }

    public void addUserNotification(final String nickname, final String userUID) {
        UIDS.add(userUID);
        temporaryListener = dbR.child(String.format("users/%s/friends/%s/messages", mUser.getUid(), userUID)).limitToLast(1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot1, @Nullable String s) {
                        boolean messageNotified = true;
                        if (dataSnapshot1.hasChild("messageNotified"))
                            messageNotified = Boolean.parseBoolean(dataSnapshot1.child("messageNotified").getValue().toString());

                        if (!messageNotified) {
                            FirebaseStorage.getInstance().getReference().child("profile_images/" + dataSnapshot1.child("senderEmail").getValue().toString() + ".jpg")
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    GlideApp.with(getApplicationContext())
                                            .asBitmap()
                                            .load(uri).
                                            into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                                                    mBuilder.setSmallIcon(R.drawable.ic_message_black_24dp);
                                                    mBuilder.setContentTitle(nickname);
                                                    mBuilder.setContentText(dataSnapshot1.child("messageContent").getValue().toString())
                                                            .setAutoCancel(true)
                                                            .setGroup(userUID)
                                                            .setGroupSummary(true)
                                                            .setLargeIcon(resource)
                                                            .build();
                                                    notificationManager.notify(createID(), mBuilder.build());
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                                }
                                            });
                                }
                            });
                            dbR.child(String.format("users/%s/friends/%s/messages/%s/messageNotified", mUser.getUid(), userUID,
                                    dataSnapshot1.getKey())).setValue(true);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        listeners.add(temporaryListener);

    }

    public void removeUserListener(String userUID) {
        for (int i = 0; i < UIDS.size(); i++) {
            if (UIDS.get(i).equals(userUID)) {
                dbR.child(String.format("users/%s/friends/%s/messages", mUser.getUid(), userUID)).
                        removeEventListener(listeners.get(i));
                UIDS.remove(i);
                listeners.remove(i);
                break;
            }
        }
    }
    public void initiateNotifications() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                addUserNotification(dataSnapshot.child("nickname").getValue().toString(), dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull final DataSnapshot dataSnapshot) {
                removeUserListener(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        dbR.child(String.format("users/%s/friends", mUser.getUid())).addChildEventListener(childEventListener);
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