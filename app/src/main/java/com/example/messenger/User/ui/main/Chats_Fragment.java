package com.example.messenger.User.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.Adapter.UsersAdapter;
import com.example.messenger.Chat.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.User.AddUser;
import com.example.messenger.User.userInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class Chats_Fragment extends Fragment {
    ListView userListView = null;
    private FirebaseUser mUser;
    private DatabaseReference dbR;
    public ArrayList<userInfo> users;
    private UsersAdapter usersAdapter;

    public void Init(final View view) {
        dbR = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userListView = view.findViewById(R.id.usersListView);
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(view.getContext(), users);
        userListView.setAdapter(usersAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chats_fragment, container, false);
        Init(view);
        dbR.child(String.format("users/%s/friends", mUser.getUid())).addChildEventListener(new ChildEventListener() {
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
                Intent chatIntent = new Intent(view.getContext(), ChatActivity.class);
                chatIntent.putExtra("information", new String[]{users.get(position).email, users.get(position).nickname, users.get(position).userUID});
                startActivity(chatIntent);
            }
        });
        return view;
    }
}
