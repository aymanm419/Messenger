package com.example.messenger.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.Adapter.MessageAdapter;
import com.example.messenger.User.userInfo;
import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    ListView chatListView;
    TextView chatTextBox;
    DatabaseReference dbR;
    FirebaseAuth mAuth;
    ArrayList<MessageInfo> messages;
    userInfo recivingUser;
    MessageAdapter messageAdapter;

    public static class MessageInfo {
        public String messageContent;
        public String senderEmail;

        public MessageInfo() {

        }

        public MessageInfo(String _messageContent, String _senderEmail) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public String getSenderEmail() {
            return senderEmail;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String info[] = getIntent().getExtras().getStringArray("information");
        dbR = FirebaseDatabase.getInstance().getReference();
        recivingUser = new userInfo(info[0], info[1], info[2]);
        chatListView = findViewById(R.id.chatListView);
        chatTextBox = findViewById(R.id.chatTextBox);
        mAuth = FirebaseAuth.getInstance();
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        chatListView.setAdapter(messageAdapter);
        dbR.child(String.format("users/%s/friends/%s/messages", mAuth.getCurrentUser().getUid(), recivingUser.userUID))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        messages.add(new MessageInfo(dataSnapshot.child("messageContent").getValue().toString(), dataSnapshot.child("senderEmail").getValue().toString()));
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void sendMessage(View view) {
        if (chatTextBox.getText().length() == 0)
            return;
        try {
            //Add yourself to receiver friend list if first message(Does nothing if already added)
            if (messages.isEmpty()) {
                dbR.child(String.format("users/%s/friends/%s", recivingUser.userUID, mAuth.getCurrentUser().getUid()))
                        .setValue(
                                new userInfo(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail(),
                                        mAuth.getCurrentUser().getUid())
                        );
            }
            dbR.child(String.format("users/%s/friends/%s/messages", mAuth.getCurrentUser().getUid(), recivingUser.userUID))
                    .push().setValue(new MessageInfo(chatTextBox.getText().toString(), mAuth.getCurrentUser().getEmail()));
            dbR.child(String.format("users/%s/friends/%s/messages", recivingUser.userUID, mAuth.getCurrentUser().getUid()))
                    .push().setValue(new MessageInfo(chatTextBox.getText().toString(), mAuth.getCurrentUser().getEmail()
            ));
            chatTextBox.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
