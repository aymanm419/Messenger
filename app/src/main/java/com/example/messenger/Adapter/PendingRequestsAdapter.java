package com.example.messenger.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.Tools.GlideApp;
import com.example.messenger.User.userInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class PendingRequestsAdapter extends RecyclerView.Adapter<View_Holder.View_Holder2> {
    private Context mContext;
    private ArrayList<userInfo> pendingArrayList;
    private String UID;
    private DatabaseReference dbr;
    private FirebaseUser mUser;

    public PendingRequestsAdapter(Context mContext, ArrayList<userInfo> usersArrayList, String UID) {
        this.mContext = mContext;
        this.pendingArrayList = usersArrayList;
        this.UID = UID;
        dbr = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public View_Holder.View_Holder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_row, parent, false);
        return new View_Holder.View_Holder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final View_Holder.View_Holder2 holder, final int position) {
        holder.email.setText(pendingArrayList.get(position).email);
        FirebaseStorage.getInstance().getReference().child(String.format("profile_images/%s.jpg", pendingArrayList.get(position).email)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(mContext).load(uri).into(holder.imageView);
            }
        });
        holder.checkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbr.child(String.format("users/%s/friends/%s", UID, pendingArrayList.get(position).getUserUID())).setValue(pendingArrayList.get(position));
                dbr.child(String.format("users/%s/friends/%s", pendingArrayList.get(position).getUserUID(), UID)).setValue(
                        new userInfo(mUser.getDisplayName(), mUser.getEmail(), mUser.getUid()));
                dbr.child(String.format("users/%s/pending/%s", UID, pendingArrayList.get(position).getUserUID())).removeValue();
            }
        });
        holder.cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbr.child(String.format("users/%s/pending/%s", FirebaseAuth.getInstance().getUid(), pendingArrayList.get(position).getUserUID())).removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingArrayList.size();
    }

    public void insert(userInfo data) {
        pendingArrayList.add(data);
        notifyItemInserted(pendingArrayList.size() - 1);
    }

    public void remove(userInfo data) {
        for (int i = 0; i < pendingArrayList.size(); i++) {
            if (data.getUserUID().equals(pendingArrayList.get(i).getUserUID())) {
                pendingArrayList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}
