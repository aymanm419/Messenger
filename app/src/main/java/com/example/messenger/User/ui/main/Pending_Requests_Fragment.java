package com.example.messenger.User.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Adapter.PendingRequestsAdapter;
import com.example.messenger.R;
import com.example.messenger.User.userInfo;
import com.example.messenger.User.usersActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Pending_Requests_Fragment extends Fragment {
    ArrayList<userInfo> pending;
    RecyclerView recyclerView;
    PendingRequestsAdapter pendingRequestsAdapter;

    public void addPending(View view) {

    }

    public void deletePending(View view) {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_requests_fragment, container, false);
        pending = new ArrayList<>();
        recyclerView = view.findViewById(R.id.pendingRecyclerView);
        pendingRequestsAdapter = new PendingRequestsAdapter(view.getContext(), pending);
        recyclerView.setAdapter(pendingRequestsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/pending", FirebaseAuth.getInstance().getUid())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                userInfo user = new userInfo(dataSnapshot.child("nickname").getValue().toString(), dataSnapshot.child("email").getValue().toString(),
                        dataSnapshot.getKey());
                pendingRequestsAdapter.insert(user);
                Activity activity = (usersActivity) getActivity();
                if (activity != null) {
                    TabLayout tabLayout = activity.findViewById(R.id.tabs);
                    tabLayout.getTabAt(2).setText("Pending Requests(" + pending.size() + ")");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                userInfo user = new userInfo(dataSnapshot.child("nickname").getValue().toString(), dataSnapshot.child("email").getValue().toString(),
                        dataSnapshot.getKey());
                pendingRequestsAdapter.remove(user);
                Activity activity = (usersActivity) getActivity();
                if (activity != null) {
                    TabLayout tabLayout = activity.findViewById(R.id.tabs);
                    tabLayout.getTabAt(2).setText("Pending Requests(" + pending.size() + ")");
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}
