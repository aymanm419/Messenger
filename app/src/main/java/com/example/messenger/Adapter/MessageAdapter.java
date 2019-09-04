package com.example.messenger.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.Tools.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import static com.example.messenger.Chat.ChatActivity.MESSAGE_STATE_SEEN;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<ChatActivity.MessageInfo> messageArrayList;
    private FirebaseUser mUser;
    private boolean isDestroyed = false;
    public MessageAdapter(Context mContext, ArrayList<ChatActivity.MessageInfo> usersArrayList) {
        this.mContext = mContext;
        this.messageArrayList = usersArrayList;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean destroy) {
        isDestroyed = destroy;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_receiver_layout, parent, false);
                holder = new View_Holder.View_Holder0(v);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_layout, parent, false);
                holder = new View_Holder.View_Holder1(v);
                break;
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sender_layout, parent, false);
                holder = new View_Holder.View_Holder2(v);
                break;
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sender_image_layout, parent, false);
                holder = new View_Holder.View_Holder3(v);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                final View_Holder.View_Holder0 view_holder0 = (View_Holder.View_Holder0) holder;
                view_holder0.message.setText(messageArrayList.get(position).getMessageContent());
                FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isDestroyed)
                            GlideApp.with(mContext).load(uri).into(view_holder0.imageView);
                    }
                });
                break;
            case 1:
                final View_Holder.View_Holder1 view_holder1 = (View_Holder.View_Holder1) holder;
                FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isDestroyed)
                            GlideApp.with(mContext).load(uri).into(view_holder1.imageView);
                    }
                });
                FirebaseStorage.getInstance().getReference().child("chat_images/" + messageArrayList.get(position).getMessageContent()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isDestroyed)
                            GlideApp.with(mContext).load(uri).fitCenter().into(view_holder1.messageImage);
                    }
                });
                break;
            case 2:
                final View_Holder.View_Holder2 view_holder2 = (View_Holder.View_Holder2) holder;
                view_holder2.message.setText(messageArrayList.get(position).getMessageContent());
                Paint paint = new Paint(view_holder2.message.getPaint());
                ViewGroup.LayoutParams params = view_holder2.message.getLayoutParams();
                int availableWidth = params.width - view_holder2.message.getPaddingLeft() - view_holder2.message.getPaddingRight();
                int textWidth = (int) Math.ceil(paint.measureText(view_holder2.message.getText().toString()));
                if (textWidth <= availableWidth) {
                    view_holder2.message.setGravity(Gravity.RIGHT);
                } else {
                    view_holder2.message.setGravity(Gravity.LEFT);
                }
                if (messageArrayList.get(position).getMessageState() == MESSAGE_STATE_SEEN)
                    GlideApp.with(mContext).load(R.drawable.greencheckmark).into(view_holder2.seenImage);
                else
                    GlideApp.with(mContext).load(R.drawable.deliveredcheckmark).into(view_holder2.seenImage);
                FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isDestroyed)
                            GlideApp.with(mContext).load(uri).into(view_holder2.imageView);
                    }
                });
                break;
            case 3:
                final View_Holder.View_Holder3 view_holder3 = (View_Holder.View_Holder3) holder;
                if (messageArrayList.get(position).getMessageState() == MESSAGE_STATE_SEEN)
                    GlideApp.with(mContext).load(R.drawable.greencheckmark).into(view_holder3.seenImage);
                else
                    GlideApp.with(mContext).load(R.drawable.deliveredcheckmark).into(view_holder3.seenImage);
                FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isDestroyed)
                            GlideApp.with(mContext).load(uri).into(view_holder3.imageView);
                    }
                });
                FirebaseStorage.getInstance().getReference().child("chat_images/" + messageArrayList.get(position).getMessageContent()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!isDestroyed)
                            GlideApp.with(mContext).load(uri).fitCenter().into(view_holder3.messageImage);
                    }
                });
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mUser.getEmail().equals(messageArrayList.get(position).getSenderEmail()))
            return 2 + messageArrayList.get(position).getMessageType();
        return messageArrayList.get(position).getMessageType();
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }
    // Insert a new item to the RecyclerView on a predefined position
    public void insert(ChatActivity.MessageInfo data) {
        messageArrayList.add(data);
        notifyItemInserted(messageArrayList.size() - 1);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(ChatActivity.MessageInfo data) {
        int position = messageArrayList.indexOf(data);
        messageArrayList.remove(position);
        notifyItemRemoved(position);
    }
}
