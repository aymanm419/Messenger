package com.example.messenger.User.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.messenger.Adapter.UsersAdapter;
import com.example.messenger.Profile.user_profile;
import com.example.messenger.R;
import com.example.messenger.User.userInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Add_Users_Fragment extends Fragment {
    EditText friendTextView;
    ListView listView;
    ArrayList<userInfo> users;
    ArrayList<userInfo> allUsers;
    UsersAdapter usersAdapter;
    DatabaseReference dbR;
    FirebaseUser fbu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_user_fragment, container, false);
        listView = view.findViewById(R.id.addUserListView);
        friendTextView = view.findViewById(R.id.addUserTextView);
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(view.getContext(), users);
        listView.setAdapter(usersAdapter);
        dbR = FirebaseDatabase.getInstance().getReference();
        fbu = FirebaseAuth.getInstance().getCurrentUser();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(view.getContext(), user_profile.class);
                intent.putExtra("information", new String[]{users.get(position).email, users.get(position).nickname, users.get(position).getUserUID()});
                startActivity(intent);
                return;
            }
        });
        friendTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                users.clear();
                usersAdapter.notifyDataSetChanged();
                dbR.child("users").orderByChild("email").equalTo(s.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            users.add(new userInfo(snapshot.child("nickname").getValue().toString(),
                                    snapshot.child("email").getValue().toString(), snapshot.getKey()));
                            Toast.makeText(getContext(), "Match Found!", Toast.LENGTH_SHORT).show();
                        }
                        usersAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        return view;
    }
}
