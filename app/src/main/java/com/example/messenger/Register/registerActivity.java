package com.example.messenger.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity {
    private static class user {
        String email, password, nickname;

        public user() {

        }

        public user(String _email, String _password, String _nickname) {
            email = _email;
            password = _password;
            nickname = _nickname;
        }
    }

    private static final int PICK_IMAGE = 1;
    private FirebaseAuth mAuth;
    private static int passwordStrengthPart = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        ((EditText) findViewById(R.id.passwordRegisterText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((ProgressBar) findViewById(R.id.passwordStrengthBar)).setProgress(getPasswordStrength(s.toString()) * passwordStrengthPart);
                ((TextView) findViewById(R.id.passwordStrengthPercent)).setText(getPasswordStrength(s.toString()) * passwordStrengthPart + "%");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Error! choose again", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ((ImageView) findViewById(R.id.uploadImageView)).setImageBitmap(bitmap);
                    findViewById(R.id.uploadImageView).setTag("1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean validateEmail(String email) {
        return Pattern.matches("[A-Z0-9a-z._%+-]+@[a-zA-Z0-9]+\\.[A-Za-z]{2,4}", email);
    }

    public void Register(View view) {
        String nickName = ((EditText) findViewById(R.id.nickNameEditText)).getText().toString();
        String email = ((EditText) findViewById(R.id.emailRegisterText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordRegisterText)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.passwordConfirmText)).getText().toString();
        if (!validateEmail(email)) {
            Toast.makeText(this, "Invalid Email Address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nickName.length() <= 2) {
            Toast.makeText(this, "Nickname is very short!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 3) {
            Toast.makeText(this, "Password too Short!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() > 30) {
            Toast.makeText(this, "Password too Long!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.compareTo(confirmPassword) != 0) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getPasswordStrength(password) < 2) {
            Toast.makeText(this, "Password must contain lower-case, upper-case, numbers or special numbers and Be within length of (3-30) and have strength of 40% atleast", Toast.LENGTH_LONG).show();
            return;
        }
        if (findViewById(R.id.uploadImageView).getTag().equals("0")) {
            Toast.makeText(this, "Please Choose a picture", Toast.LENGTH_SHORT).show();
            return;
        }
        makeAccount(email, password, nickName);
    }

    public void makeAccount(final String email, final String password, final String nickname) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadPhoto(email, nickname, user);
                            FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).setValue(
                                    new user(email, password, nickname));
                        } else {
                            Toast.makeText(registerActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void uploadPhoto(final String email, final String nickName, final FirebaseUser user) {
        ImageView imageView = findViewById(R.id.uploadImageView);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("Images").child(email + ".jpg").putBytes(data);
        Toast.makeText(this, "Uploading!", Toast.LENGTH_SHORT).show();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(registerActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nickName)
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                }
                            }
                        });
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(registerActivity.this, "Verification Email Sent!", Toast.LENGTH_LONG).show();
                            }
                        });
                finish();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                ((ProgressBar) findViewById(R.id.progressBar)).setProgress((int) progress);
            }
        });
        return;
    }

    private int getPasswordStrength(String password) {
        int ret = 0;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i)))
                ret |= (1 << 0);
            else if (Character.isUpperCase(password.charAt(i)))
                ret |= (1 << 1);
            else if (Character.isDigit(password.charAt(i)))
                ret |= (1 << 2);
            else
                ret |= (1 << 3);
        }
        if (password.length() >= 8)
            ret |= (1 << 4);
        int strength = 0;
        for (int i = 0; i < 5; i++) {
            if (((ret >> i) & 1) > 0)
                strength++;
        }
        return strength;
    }
}
