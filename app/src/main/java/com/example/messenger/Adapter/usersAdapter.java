package com.example.messenger.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.messenger.Tools.*;
import com.example.messenger.R;
import com.example.messenger.User.userInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class usersAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<userInfo> usersArrayList;

    public usersAdapter(Context mContext, ArrayList<userInfo> usersArrayList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.user_custom_layout, null);
        ((TextView) v.findViewById(R.id.friendTextView)).setText(usersArrayList.get(position).nickname);
        final CircleImageView circleImageView = v.findViewById(R.id.profileCircleView);
        FirebaseStorage.getInstance().getReference().child("Images/" + usersArrayList.get(position).email + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
