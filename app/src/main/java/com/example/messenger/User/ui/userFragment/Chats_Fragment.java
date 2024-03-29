package com.example.messenger.User.ui.userFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.Adapter.UsersAdapter;
import com.example.messenger.Chat.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.User.userInfo;
import com.example.messenger.User.usersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class Chats_Fragment extends Fragment {
    ListView userListView = null;
    private FirebaseUser mUser;
    private DatabaseReference dbR;
    public ArrayList<userInfo> users;
    private UsersAdapter usersAdapter;
    private ChildEventListener childEventListener = null;
    private ChildEventListener notification = null;
    private Activity activity = null;
    final int ACTIVITY_RESULT_CHAT = 100;
    public void Init(final View view) {
        dbR = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userListView = view.findViewById(R.id.usersListView);
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(view.getContext(), users);
        userListView.setAdapter(usersAdapter);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.chats_fragment, container, false);
        if (childEventListener != null)
            dbR.child(String.format("users/%s/friends", mUser.getUid())).removeEventListener(childEventListener);
        Init(view);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    users.add(new userInfo(dataSnapshot.child("nickname").getValue().toString()
                            , dataSnapshot.child("email").getValue().toString(), dataSnapshot.getKey()));
                    usersAdapter.notifyDataSetChanged();
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
        };
        dbR.child(String.format("users/%s/friends", mUser.getUid())).addChildEventListener(childEventListener);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(view.getContext(), ChatActivity.class);
                chatIntent.putExtra("information", new String[]{users.get(position).email, users.get(position).nickname, users.get(position).getUserUID()});
                if (activity instanceof usersActivity)
                    ((usersActivity) activity).removeUserListener(users.get(position).getUserUID());
                startActivityForResult(chatIntent, ACTIVITY_RESULT_CHAT);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_RESULT_CHAT && resultCode == RESULT_OK) {
            if (activity instanceof usersActivity && data.getExtras() != null
                    && data.getExtras().containsKey("nickname") && data.getExtras().containsKey("UID"))
                ((usersActivity) activity).addUserNotification(data.getExtras().getString("nickname"),
                        data.getExtras().getString("UID"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
