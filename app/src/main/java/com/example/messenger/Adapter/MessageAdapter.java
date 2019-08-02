package com.example.messenger.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.messenger.Chat.ChatActivity;
import com.example.messenger.Tools.*;
import com.example.messenger.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ChatActivity.MessageInfo> messageArrayList;

    public MessageAdapter(Context mContext, ArrayList<ChatActivity.MessageInfo> usersArrayList) {
        this.mContext = mContext;
        this.messageArrayList = usersArrayList;
    }

    @Override
    public int getCount() {
        return messageArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.chat_receiver_layout, null);
        final TextView textView = (TextView) v.findViewById(R.id.receiverTextView);
        final CircleImageView circleImageView = v.findViewById(R.id.receiverProfilePicture);
        textView.setText(messageArrayList.get(position).getMessageContent());
        FirebaseStorage.getInstance().getReference().child("Images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(mContext).load(uri).into(circleImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Info", "Failed");
            }
        });
        return v;
    }
}
