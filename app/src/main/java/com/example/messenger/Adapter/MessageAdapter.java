package com.example.messenger.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

import static com.example.messenger.Chat.ChatActivity.MESSAGE_TEXT;
import static java.security.AccessController.getContext;

public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ChatActivity.MessageInfo> messageArrayList;
    int width_dp;
    int height_dp;
    public MessageAdapter(Context mContext, ArrayList<ChatActivity.MessageInfo> usersArrayList) {
        this.mContext = mContext;
        this.messageArrayList = usersArrayList;
        int width_px = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height_px = Resources.getSystem().getDisplayMetrics().heightPixels;
        int pixeldpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        width_dp = (width_px / pixeldpi) * 160;
        height_dp = (height_px / pixeldpi) * 160;
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
        View v;
        if (messageArrayList.get(position).getMessageType() == MESSAGE_TEXT) {
            v = View.inflate(mContext, R.layout.chat_receiver_layout, null);
            final TextView textView = (TextView) v.findViewById(R.id.receiverTextView);
            final CircleImageView circleImageView = v.findViewById(R.id.receiverProfilePicture);
            textView.setText(messageArrayList.get(position).getMessageContent());
            FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(mContext).load(uri).into(circleImageView);
                }
            });
        } else {
            v = View.inflate(mContext, R.layout.chat_image_layout, null);
            final CircleImageView circleImageView = v.findViewById(R.id.imageProfilePicture);
            final ImageView imageView = v.findViewById(R.id.imageSent);
            FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(mContext).load(uri).into(circleImageView);
                }
            });
            FirebaseStorage.getInstance().getReference().child("chat_images/" + messageArrayList.get(position).getMessageContent()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(mContext).load(uri).fitCenter().into(imageView);
                }
            });
        }
        return v;
    }
}
