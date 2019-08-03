package com.example.messenger.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddUser extends AppCompatActivity {
    ArrayList<userInfo> AllUsers;
    ArrayList<userInfo> UserFriends;
    private FirebaseUser mUser;
    private DatabaseReference dbR;

    boolean isAlreadyFriend(String email) {
        for (userInfo user : UserFriends) {
            if (user.email.equals(email))
                return true;
        }
        return false;
    }

    public void addUser(View view) {
        String email = ((TextView) findViewById(R.id.addUserText)).getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Insert Your Friend's Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            Toast.makeText(this, "You Can't Add Yourself!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isAlreadyFriend(email)) {
            Toast.makeText(this, "You're Already friends.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Searching!", Toast.LENGTH_SHORT).show();
        for (userInfo currentUser : AllUsers) {
            if (currentUser.email.equals(email)) {
                FirebaseDatabase.getInstance().getReference().child("users").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child(currentUser.userUID).setValue(
                        new userInfo(currentUser.nickname, currentUser.email, currentUser.userUID));
                Toast.makeText(this, "Friend Added Successfully!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this, "No Match Found!", Toast.LENGTH_SHORT).show();
    }

    public void deleteUser(View view) {
        String email = ((TextView) findViewById(R.id.deleteUserText)).getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please insert your friend's email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAlreadyFriend(email)) {
            Toast.makeText(this, "You're not friends with this user", Toast.LENGTH_SHORT).show();
            return;
        }
        for (userInfo currentUser : UserFriends) {
            if (currentUser.email.equals(email)) {
                FirebaseDatabase.getInstance().getReference().child("users").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child(currentUser.userUID).removeValue();
                Toast.makeText(this, "Friend Deleted Successfully!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        AllUsers = new ArrayList<>();
        UserFriends = new ArrayList<>();
        dbR = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        dbR.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    AllUsers.add(new userInfo(dataSnapshot.child("nickname").getValue().toString()
                            , dataSnapshot.child("email").getValue().toString(), dataSnapshot.getKey()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (userInfo user : AllUsers) {
                    if (user.email.equals(dataSnapshot.child("email").getValue().toString())) {
                        AllUsers.remove(user);
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        dbR.child(String.format("users/%s/friends", mUser.getUid())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    UserFriends.add(new userInfo(dataSnapshot.child("nickname").getValue().toString()
                            , dataSnapshot.child("email").getValue().toString(), dataSnapshot.getKey()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (userInfo user : UserFriends) {
                    if (user.email.equals(dataSnapshot.child("email").getValue().toString())) {
                        UserFriends.remove(user);
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
