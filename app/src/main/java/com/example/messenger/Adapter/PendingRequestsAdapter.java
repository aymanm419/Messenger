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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class PendingRequestsAdapter extends RecyclerView.Adapter<View_Holder.View_Holder2> {
    private Context mContext;
    private ArrayList<userInfo> pendingArrayList;

    public PendingRequestsAdapter(Context mContext, ArrayList<userInfo> usersArrayList) {
        this.mContext = mContext;
        this.pendingArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public View_Holder.View_Holder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_row, parent, false);
        return new View_Holder.View_Holder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final View_Holder.View_Holder2 holder, int position) {
        holder.email.setText(pendingArrayList.get(position).email);
        FirebaseStorage.getInstance().getReference().child(String.format("profile_images/%s.jpg", pendingArrayList.get(position).email)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(mContext).load(uri).into(holder.imageView);
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
        int position = pendingArrayList.indexOf(data);
        pendingArrayList.remove(position);
        notifyItemRemoved(position);
    }
}
