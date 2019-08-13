package com.example.messenger.User.ui.main;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.R;
import com.example.messenger.Tools.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Fragment extends Fragment {
    private FirebaseUser mUser;
    private CircleImageView circleImageView;
    private TextView textView;
    private TextView nickname;
    private OnSuccessListener onSuccessListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.profile_fragment, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        circleImageView = (CircleImageView) view.findViewById(R.id.userImageProfile);
        textView = (TextView) view.findViewById(R.id.userEmailAddressText);
        nickname = view.findViewById(R.id.userNickNameText);
        textView.setText(mUser.getEmail());
        nickname.setText(mUser.getDisplayName());
        onSuccessListener = new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(view.getContext()).load(uri).into(circleImageView);
            }
        };
        FirebaseStorage.getInstance().getReference().child("profile_images/" + mUser.getEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(onSuccessListener);
        return view;
    }
}
