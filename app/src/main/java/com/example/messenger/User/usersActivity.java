package com.example.messenger.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.messenger.Adapter.usersAdapter;
import com.example.messenger.Chat.ChatActivity;
import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class usersActivity extends AppCompatActivity {
    long lastTimeCliked = 0;
    Toolbar userToolBar = null;
    ListView userListView = null;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public ArrayList<userInfo> users;
    private com.example.messenger.Adapter.usersAdapter usersAdapter;

    public void Init() {
        userToolBar = findViewById(R.id.toolbar);
        userListView = findViewById(R.id.userList);
        userToolBar.setTitle(mAuth.getCurrentUser().getDisplayName());
        userToolBar.setSubtitle("Online");
        users = new ArrayList<userInfo>();
        usersAdapter = new usersAdapter(getApplicationContext(), users);
        userListView.setAdapter(usersAdapter);
        userToolBar.inflateMenu(R.menu.adduser);
        userToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.addUser) {
                    Intent intent = new Intent(getApplicationContext(), AddUser.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Init();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    users.add(new userInfo(dataSnapshot.child("nickname").getValue().toString()
                            , dataSnapshot.child("email").getValue().toString(), dataSnapshot.getKey()));
                    usersAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (userInfo user : users) {
                    if (user.email.equals(dataSnapshot.child("email").getValue().toString())) {
                        users.remove(user);
                        break;
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                chatIntent.putExtra("information",new String[]{users.get(position).email,users.get(position).nickname,users.get(position).userUID});
                startActivity(chatIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        long time = (new Date()).getTime();
        if (time - lastTimeCliked >= 2000) {
            Toast.makeText(this, "Click Back Again to logout!", Toast.LENGTH_SHORT).show();
            lastTimeCliked = time;
            return;
        } else {
            mAuth.signOut();
            finish();
            super.onBackPressed();
        }
    }
}
