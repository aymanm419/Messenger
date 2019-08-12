package com.example.messenger.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.Adapter.MessageAdapter;
import com.example.messenger.Tools.BitMapHandler;
import com.example.messenger.User.userInfo;
import com.example.messenger.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.messenger.Register.registerActivity.PICK_IMAGE;

public class ChatActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 2;
    RecyclerView chatListView;
    TextView chatTextBox;
    DatabaseReference dbR;
    FirebaseAuth mAuth;
    ArrayList<MessageInfo> messages;
    userInfo recivingUser;
    MessageAdapter messageAdapter;
    public static final int MESSAGE_TEXT = 0;
    public static final int MESSAGE_PHOTO = 1;

    public static class MessageInfo {
        private String messageContent;
        private String senderEmail;
        private int messageType = 0;
        private String messageUID = null;
        public MessageInfo() {

        }

        public MessageInfo(String _messageContent, String _senderEmail) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
        }

        public MessageInfo(String _messageContent, String _senderEmail, int _messageType) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
            this.messageType = _messageType;
        }

        public MessageInfo(String _messageContent, String _senderEmail, int _messageType, String _messageUID) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
            this.messageType = _messageType;
            this.messageUID = _messageUID;
        }
        public String getMessageContent() {
            return messageContent;
        }

        public String getSenderEmail() {
            return senderEmail;
        }

        public int getMessageType() {
            return messageType;
        }
    }

    public void Init() {
        String info[] = getIntent().getExtras().getStringArray("information");
        dbR = FirebaseDatabase.getInstance().getReference();
        recivingUser = new userInfo(info[0], info[1], info[2]);
        chatListView = findViewById(R.id.chatListView);
        chatTextBox = findViewById(R.id.chatTextBox);
        mAuth = FirebaseAuth.getInstance();
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Init();
        chatListView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListView.setLayoutManager(linearLayoutManager);
        chatListView.setHasFixedSize(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        chatListView.setItemAnimator(itemAnimator);
        dbR.child(String.format("users/%s/friends/%s/messages", mAuth.getCurrentUser().getUid(), recivingUser.userUID))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        MessageInfo messageInfo = new MessageInfo(dataSnapshot.child("messageContent").getValue().toString(), dataSnapshot.child("senderEmail").getValue().toString()
                                , Integer.parseInt(dataSnapshot.child("messageType").getValue().toString()), dataSnapshot.getKey());
                        messageAdapter.insert(messageInfo);
                        chatListView.getLayoutManager().scrollToPosition(0);
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
                    .push().setValue(new MessageInfo(chatTextBox.getText().toString(), mAuth.getCurrentUser().getEmail()
            ));
            dbR.child(String.format("users/%s/friends/%s/messages", recivingUser.userUID, mAuth.getCurrentUser().getUid()))
                    .push().setValue(new MessageInfo(chatTextBox.getText().toString(), mAuth.getCurrentUser().getEmail()
            ));
            chatTextBox.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public void pickImage(View view) {
        if (!hasReadPermissions()) {
            requestAppPermissions();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    public void uploadPhoto(final String name, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitMapHandler bitMapHandler = new BitMapHandler();
        bitmap = bitMapHandler.getResizedBitmapLessThanMaxSize(bitmap, 100);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("chat_images").child(name + ".jpg").putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dbR.child(String.format("users/%s/friends/%s/messages", mAuth.getCurrentUser().getUid(), recivingUser.userUID))
                        .push().setValue(new MessageInfo(name + ".jpg", mAuth.getCurrentUser().getEmail(), MESSAGE_PHOTO
                ));
                dbR.child(String.format("users/%s/friends/%s/messages", recivingUser.userUID, mAuth.getCurrentUser().getUid()))
                        .push().setValue(new MessageInfo(name + ".jpg", mAuth.getCurrentUser().getEmail(), MESSAGE_PHOTO
                ));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    String name = UUID.randomUUID().toString();
                    uploadPhoto(name, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
