package com.example.messenger.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_Holder {
    static public class View_Holder0 extends RecyclerView.ViewHolder {
        TextView message;
        CircleImageView imageView;

        public View_Holder0(@NonNull View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.textMessage);
            imageView = (CircleImageView) itemView.findViewById(R.id.profilePicture);
        }
    }
    static public class View_Holder1 extends RecyclerView.ViewHolder {
        ImageView messageImage;
        CircleImageView imageView;

        public View_Holder1(@NonNull View itemView) {
            super(itemView);
            messageImage = (ImageView) itemView.findViewById(R.id.imageSent);
            imageView = (CircleImageView) itemView.findViewById(R.id.profilePicture);
        }
    }

    static public class View_Holder2 extends RecyclerView.ViewHolder {
        TextView message;
        CircleImageView imageView, seenImage;
        public View_Holder2(@NonNull View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.textMessage);
            imageView = (CircleImageView) itemView.findViewById(R.id.profilePicture);
            seenImage = (CircleImageView) itemView.findViewById(R.id.seenImage);
        }
    }

    static public class View_Holder3 extends RecyclerView.ViewHolder {
        ImageView messageImage;
        CircleImageView imageView, seenImage;

        public View_Holder3(@NonNull View itemView) {
            super(itemView);
            messageImage = (ImageView) itemView.findViewById(R.id.imageSent);
            imageView = (CircleImageView) itemView.findViewById(R.id.profilePicture);
            seenImage = (CircleImageView) itemView.findViewById(R.id.seenImage);
        }
    }

    static public class View_Holder4 extends RecyclerView.ViewHolder {
        TextView email;
        CircleImageView imageView;
        ImageView checkImageView, cancelImageView;

        public View_Holder4(@NonNull View itemView) {
            super(itemView);
            email = (TextView) itemView.findViewById(R.id.pendingTextView);
            imageView = (CircleImageView) itemView.findViewById(R.id.pendingPicture);
            checkImageView = (ImageView) itemView.findViewById(R.id.acceptButton);
            cancelImageView = (ImageView) itemView.findViewById(R.id.rejectButton);
        }
    }
}

