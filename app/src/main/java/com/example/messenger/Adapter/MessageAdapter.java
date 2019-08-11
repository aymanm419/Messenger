package com.example.messenger.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.Tools.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<ChatActivity.MessageInfo> messageArrayList;
    public MessageAdapter(Context mContext, ArrayList<ChatActivity.MessageInfo> usersArrayList) {
        this.mContext = mContext;
        this.messageArrayList = usersArrayList;
    }

    public class View_Holder0 extends RecyclerView.ViewHolder {
        TextView message;
        CircleImageView imageView;

        public View_Holder0(@NonNull View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.receiverTextView);
            imageView = (CircleImageView) itemView.findViewById(R.id.receiverProfilePicture);
        }
    }

    public class View_Holder1 extends RecyclerView.ViewHolder {
        ImageView messageImage;
        CircleImageView imageView;

        public View_Holder1(@NonNull View itemView) {
            super(itemView);
            messageImage = (ImageView) itemView.findViewById(R.id.imageSent);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageProfilePicture);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_receiver_layout, parent, false);
                holder = new View_Holder0(v);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_layout, parent, false);
                holder = new View_Holder1(v);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                final View_Holder0 view_holder0 = (View_Holder0) holder;
                view_holder0.message.setText(messageArrayList.get(position).getMessageContent());
                FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GlideApp.with(mContext).load(uri).into(view_holder0.imageView);
                    }
                });
                break;
            case 1:
                final View_Holder1 view_holder1 = (View_Holder1) holder;
                FirebaseStorage.getInstance().getReference().child("profile_images/" + messageArrayList.get(position).getSenderEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GlideApp.with(mContext).load(uri).into(view_holder1.imageView);
                    }
                });
                FirebaseStorage.getInstance().getReference().child("chat_images/" + messageArrayList.get(position).getMessageContent()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GlideApp.with(mContext).load(uri).fitCenter().into(view_holder1.messageImage);
                    }
                });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messageArrayList.get(position).getMessageType();
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
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
