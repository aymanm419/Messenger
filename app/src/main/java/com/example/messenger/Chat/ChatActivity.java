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
import com.example.messenger.User.usersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    ListView chatMessages;
    TextView chatTextBox;
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
        recivingUser = new userInfo(info[0], info[1], info[2]);
        chatMessages = findViewById(R.id.chatListView);
        chatTextBox = findViewById(R.id.chatTextBox);
        mAuth = FirebaseAuth.getInstance();
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        chatMessages.setAdapter(messageAdapter);
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("friends").child(recivingUser.userUID).child("messages").addChildEventListener(new ChildEventListener() {
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
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid())
                    .child("friends").child(recivingUser.userUID).child("messages").push().setValue(new MessageInfo(chatTextBox.getText().toString(),
                    mAuth.getCurrentUser().getEmail()));
            FirebaseDatabase.getInstance().getReference().child("users").child(recivingUser.userUID)
                    .child("friends").child(mAuth.getCurrentUser().getUid()).child("messages").push().setValue(new MessageInfo(chatTextBox.getText().toString(),
                    mAuth.getCurrentUser().getEmail()
            ));
            chatTextBox.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, usersActivity.class);
        startActivity(backIntent);
        super.onBackPressed();
    }
}
