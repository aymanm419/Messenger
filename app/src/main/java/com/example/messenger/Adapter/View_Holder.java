package com.example.messenger.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_Holder {
    static public class View_Holder0 extends RecyclerView.ViewHolder {
        TextView message;
        CircleImageView imageView;

        public View_Holder0(@NonNull View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.receiverTextView);
            imageView = (CircleImageView) itemView.findViewById(R.id.receiverProfilePicture);
        }
    }

    static public class View_Holder1 extends RecyclerView.ViewHolder {
        ImageView messageImage;
        CircleImageView imageView;

        public View_Holder1(@NonNull View itemView) {
            super(itemView);
            messageImage = (ImageView) itemView.findViewById(R.id.imageSent);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageProfilePicture);
        }
    }
}

