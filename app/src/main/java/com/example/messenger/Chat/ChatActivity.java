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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.messenger.Adapter.MessageAdapter;
import com.example.messenger.Tools.BitMapHandler;
import com.example.messenger.Tools.GlideApp;
import com.example.messenger.User.userInfo;
import com.example.messenger.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.messenger.Register.registerActivity.PICK_IMAGE;

public class ChatActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 2;
    private RecyclerView chatListView;
    private TextView chatTextBox;
    private DatabaseReference dbR;
    private FirebaseUser mUser;
    private ArrayList<MessageInfo> messages;
    private userInfo recivingUser;
    private MessageAdapter messageAdapter;
    private ChildEventListener childEventListener, seenEventListener, makeSeenEventListener;
    private HashMap<String, Integer> UIDToIndex;
    private TextView nameTextView;
    private CircleImageView profileImageView;
    private int messageCounter = 0;
    public static final int MESSAGE_TEXT = 0;
    public static final int MESSAGE_PHOTO = 1;
    public static final int MESSAGE_NO_LAST_SEEN = 2;
    public static final int MESSAGE_STATE_PENDING = 0;
    public static final int MESSAGE_STATE_DELIVERED = 1;
    public static final int MESSAGE_STATE_SEEN = 2;

    public static class MessageInfo {
        private String messageContent;
        private String senderEmail;
        private int messageType = 0;
        private boolean messageNotified = false;
        private int messageState = 0;
        @Exclude
        private String messageUID = null;

        public MessageInfo() {

        }

        public MessageInfo(String _messageContent, String _senderEmail) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
        }

        public MessageInfo(String _messageContent, String _senderEmail, boolean _messageNotified) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
            this.messageNotified = _messageNotified;
        }

        private MessageInfo(String _messageContent, String _senderEmail, boolean _messageNotified, int _messageState) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
            this.messageNotified = _messageNotified;
            this.messageState = _messageState;
        }

        private MessageInfo(String _messageContent, String _senderEmail, int _messageType, boolean _messageNotified, int _messageState) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
            this.messageType = _messageType;
            this.messageNotified = _messageNotified;
            this.messageState = _messageState;
        }

        private MessageInfo(String _messageContent, String _senderEmail, int _messageType, String _messageUID, int _messageState) {
            this.messageContent = _messageContent;
            this.senderEmail = _senderEmail;
            this.messageType = _messageType;
            this.messageUID = _messageUID;
            this.messageState = _messageState;
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

        public int getMessageState() {
            return messageState;
        }

        private void setMessageState(int messageState) {
            this.messageState = messageState;
        }

        public boolean isMessageNotified() {
            return messageNotified;
        }

        @Exclude
        public String getMessageUID() {
            return messageUID;
        }
    }

    public int createID() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
    }

    public void Init() {
        String[] info = getIntent().getExtras().getStringArray("information");
        dbR = FirebaseDatabase.getInstance().getReference();
        recivingUser = new userInfo(info[1], info[0], info[2]);
        chatListView = findViewById(R.id.chatListView);
        chatTextBox = findViewById(R.id.chatTextBox);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        UIDToIndex = new HashMap<>();
        nameTextView = findViewById(R.id.nameTextView);
        profileImageView = findViewById(R.id.profileImageView);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String message = dataSnapshot.child("messageContent").getValue().toString();
                MessageInfo messageInfo = new MessageInfo(dataSnapshot.child("messageContent").getValue().toString(), dataSnapshot.child("senderEmail").getValue().toString()
                        , Integer.parseInt(dataSnapshot.child("messageType").getValue().toString()), dataSnapshot.getKey(),
                        Integer.parseInt(dataSnapshot.child("messageState").getValue().toString()));
                UIDToIndex.put(dataSnapshot.getKey(), messageCounter);
                messageCounter++;
                messageAdapter.insert(messageInfo);
                chatListView.getLayoutManager().scrollToPosition(messages.size() - 1);
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
        };
        seenEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (Integer.parseInt(dataSnapshot.child("messageState").getValue().toString()) == MESSAGE_STATE_SEEN) {
                    String UID = dataSnapshot.getKey();
                    int pos = UIDToIndex.get(UID);
                    messages.get(pos).setMessageState(MESSAGE_STATE_SEEN);
                    messageAdapter.notifyItemChanged(pos);
                }
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
        };
        makeSeenEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (Integer.parseInt(dataSnapshot.child("messageState").getValue().toString()) == MESSAGE_STATE_DELIVERED &&
                        dataSnapshot.child("senderEmail").getValue().toString().equals(recivingUser.getEmail())) {
                    dbR.child(String.format("users/%s/friends/%s/messages/%s/messageState", mUser.getUid(), recivingUser.getUserUID(),
                            dataSnapshot.getKey())).setValue(MESSAGE_STATE_SEEN);
                }
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
        };
        nameTextView.setText(recivingUser.getNickname());
        FirebaseStorage.getInstance().getReference().child(String.format("profile_images/%s.jpg", recivingUser.getEmail())).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(getApplicationContext()).load(uri).into(profileImageView);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_chat);
        Init();
        chatListView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatListView.setLayoutManager(linearLayoutManager);
        chatListView.setHasFixedSize(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(300);
        chatListView.setItemAnimator(itemAnimator);
        dbR.child(String.format("users/%s/friends/%s/messages", recivingUser.getUserUID(), mUser.getUid()))
                .addChildEventListener(childEventListener);
        dbR.child(String.format("users/%s/friends/%s/messages", mUser.getUid(), recivingUser.getUserUID()))
                .addChildEventListener(makeSeenEventListener);
        dbR.child(String.format("users/%s/friends/%s/messages", recivingUser.getUserUID(), mUser.getUid()))
                .addChildEventListener(seenEventListener);
        Bundle bundle = new Bundle();
        bundle.putString("UID", recivingUser.getUserUID());
        bundle.putString("nickname", recivingUser.getNickname());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
    }

    public void sendMessage(View view) {
        if (chatTextBox.getText().length() == 0)
            return;
        try {
            String mGroupId = dbR.child(String.format("users/%s/friends/%s/messages", mUser.getUid(), recivingUser.getUserUID()))
                    .push().getKey();
            dbR.child(String.format("users/%s/friends/%s/messages/%s", mUser.getUid(), recivingUser.getUserUID(),
                    mGroupId)).setValue(new MessageInfo(chatTextBox.getText().toString(), mUser.getEmail(), true,
                    MESSAGE_STATE_SEEN
            ));
            dbR.child(String.format("users/%s/friends/%s/messages/%s", recivingUser.getUserUID(), mUser.getUid(),
                    mGroupId)).setValue(new MessageInfo(chatTextBox.getText().toString(), mUser.getEmail(), false,
                    MESSAGE_STATE_DELIVERED
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
        bitmap = bitMapHandler.getResizedBitmapLessThanMaxSize(bitmap, 50);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("chat_images").child(name + ".jpg").putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String mGroupId = dbR.child(String.format("users/%s/friends/%s/messages", mUser.getUid(), recivingUser.getUserUID()))
                        .push().getKey();
                dbR.child(String.format("users/%s/friends/%s/messages/%s", mUser.getUid(), recivingUser.getUserUID(),
                        mGroupId))
                        .setValue(new MessageInfo(name + ".jpg", mUser.getEmail(), MESSAGE_PHOTO,
                                true, MESSAGE_STATE_SEEN
                ));
                dbR.child(String.format("users/%s/friends/%s/messages/%s", recivingUser.getUserUID(), mUser.getUid(),
                        mGroupId))
                        .setValue(new MessageInfo(name + ".jpg", mUser.getEmail(), MESSAGE_PHOTO,
                                false, MESSAGE_STATE_DELIVERED
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

    @Override
    public void onBackPressed() {
        messageAdapter.setDestroyed(true);
        dbR.child(String.format("users/%s/friends/%s/messages", recivingUser.getUserUID(), mUser.getUid()))
                .removeEventListener(childEventListener);
        dbR.child(String.format("users/%s/friends/%s/messages", mUser.getUid(), recivingUser.getUserUID()))
                .removeEventListener(makeSeenEventListener);
        dbR.child(String.format("users/%s/friends/%s/messages", recivingUser.getUserUID(), mUser.getUid()))
                .removeEventListener(seenEventListener);
        super.onBackPressed();
    }
}
