package com.example.messenger.Adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.messenger.Chat.ChatActivity.MessageInfo;
import com.example.messenger.Tools.*;
import com.example.messenger.R;
import com.example.messenger.User.userInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.messenger.Chat.ChatActivity.MESSAGE_NO_LAST_SEEN;
import static com.example.messenger.Chat.ChatActivity.MESSAGE_PHOTO;

public class UsersAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<userInfo> usersArrayList;
    private final int MAX_LAST_MESSAGE_SIZE = 35;
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference dbR = FirebaseDatabase.getInstance().getReference();
    int st = 0;
    public UsersAdapter(Context mContext, ArrayList<userInfo> usersArrayList) {
        this.mContext = mContext;
        this.usersArrayList = usersArrayList;

    }

    @Override
    public int getCount() {
        return usersArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return usersArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View v = View.inflate(mContext, R.layout.user_custom_layout, null);
        final TextView textView = (TextView) v.findViewById(R.id.friendTextView);
        final CircleImageView circleImageView = v.findViewById(R.id.receiverProfilePicture);
        textView.setText(usersArrayList.get(position).nickname);
        FirebaseStorage.getInstance().getReference().child("profile_images/" + usersArrayList.get(position).email + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(mContext).load(uri).into(circleImageView);
            }
        });
        Query lastQuery = dbR.child(String.format("users/%s/friends/%s/messages", mUser.getUid(), usersArrayList.get(position).getUserUID()))
                .limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (Integer.parseInt(dataSnapshot.child("messageType").getValue().toString()) == MESSAGE_PHOTO) {
                    ((TextView) v.findViewById(R.id.lastMessageTextView)).setText("Photo");
                    return;
                }
                String message = dataSnapshot.child("messageContent").getValue().toString();
                if (message.length() > MAX_LAST_MESSAGE_SIZE)
                    message = message.substring(0, MAX_LAST_MESSAGE_SIZE) + "...";
                ((TextView) v.findViewById(R.id.lastMessageTextView)).setText(message);
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
        return v;
    }
}
