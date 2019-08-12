package com.example.messenger.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.R;
import com.example.messenger.Tools.GlideApp;
import com.example.messenger.User.userInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_profile extends AppCompatActivity {
    userInfo receivingUser;
    CircleImageView profileImage;
    TextView nickname, email;
    FirebaseUser currentUser;
    ArrayList<userInfo> userFriends;

    void Init() {
        profileImage = findViewById(R.id.userImageProfile);
        nickname = findViewById(R.id.userNickNameText);
        email = findViewById(R.id.userEmailAddressText);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userFriends = new ArrayList<>();
    }

    boolean isAlreadyFriend(String email) {
        for (userInfo user : userFriends) {
            if (user.email.equals(email))
                return true;
        }
        return false;
    }

    public void addFriend(View view) {
        String email = receivingUser.email;
        if (isAlreadyFriend(email)) {
            Toast.makeText(this, "You're Already friends.", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/friends/%s", currentUser.getUid(),
                receivingUser.getUserUID())).setValue(new userInfo(receivingUser.nickname, receivingUser.email, receivingUser.getUserUID()));
        FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/pending/%s", receivingUser.getUserUID(),
                currentUser.getUid())).setValue(new userInfo(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getUid()));
        Toast.makeText(this, "Friend Added Successfully!", Toast.LENGTH_SHORT).show();
    }

    public void deleteFriend(View view) {
        String email = receivingUser.email;
        if (!isAlreadyFriend(email)) {
            Toast.makeText(this, "You're not friends with this user", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/friends/%s", currentUser.getUid(),
                receivingUser.getUserUID())).removeValue();
        Toast.makeText(this, "Friend Deleted Successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Init();
        String info[] = getIntent().getExtras().getStringArray("information");
        receivingUser = new userInfo(info[1], info[0], info[2]);
        nickname.setText(receivingUser.nickname);
        email.setText(receivingUser.email);
        FirebaseStorage.getInstance().getReference().child("profile_images/" + receivingUser.email + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(getApplicationContext()).load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(user_profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/friends", currentUser.getUid())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                userFriends.add(new userInfo(dataSnapshot.child("nickname").getValue().toString(),
                        dataSnapshot.child("email").getValue().toString(), dataSnapshot.getKey()));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (userInfo user : userFriends) {
                    if (user.email.equals(dataSnapshot.child("email").getValue().toString())) {
                        userFriends.remove(user);
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
