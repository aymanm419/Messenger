package com.example.messenger.User.ui.userFragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Adapter.PendingRequestsAdapter;
import com.example.messenger.R;
import com.example.messenger.User.userInfo;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Pending_Requests_Fragment extends Fragment {
    private ArrayList<userInfo> pending = null;
    private RecyclerView recyclerView = null;
    private PendingRequestsAdapter pendingRequestsAdapter = null;
    private Activity activity = null;
    private ChildEventListener childEventListener = null;

    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pending_requests_fragment, container, false);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (activity != null) {
                activity.finish();
                return null;
            }
            return view;
        }
        pending = new ArrayList<>();
        recyclerView = view.findViewById(R.id.pendingRecyclerView);
        pendingRequestsAdapter = new PendingRequestsAdapter(view.getContext(), pending, FirebaseAuth.getInstance().getUid());
        recyclerView.setAdapter(pendingRequestsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        if (childEventListener != null)
            FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/pending",
                    FirebaseAuth.getInstance().getUid())).removeEventListener(childEventListener);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                userInfo user = new userInfo(dataSnapshot.child("nickname").getValue().toString(), dataSnapshot.child("email").getValue().toString(),
                        dataSnapshot.getKey());
                pendingRequestsAdapter.insert(user);
                if (activity != null) {
                    TabLayout tabLayout = activity.findViewById(R.id.tabs);
                    tabLayout.getTabAt(2).setText("Pending Requests(" + pending.size() + ")");
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(view.getContext());
                    mBuilder.setSmallIcon(R.drawable.ic_person_add_black_24dp);
                    mBuilder.setContentTitle("New Friend Request!");
                    mBuilder.setContentText(dataSnapshot.child("nickname").getValue().toString() + " Sent you a friend request.");
                    mBuilder.build();
                    NotificationManager notification = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notification.notify(createID(), mBuilder.build());
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
        };
        FirebaseDatabase.getInstance().getReference().child(String.format("users/%s/pending",
                FirebaseAuth.getInstance().getUid())).addChildEventListener(childEventListener);
        return view;
    }
}
