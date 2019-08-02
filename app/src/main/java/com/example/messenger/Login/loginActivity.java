package com.example.messenger.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.R;
import com.example.messenger.Register.registerActivity;
import com.example.messenger.User.usersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

public class loginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mAuth.getCurrentUser() != null) {
            Intent UsersActivity = new Intent(getApplicationContext(), usersActivity.class);
            startActivity(UsersActivity);
        }
    }

    public void Login(View view) {
        final TextView emailView = findViewById(R.id.emailEditText);
        final TextView passView = findViewById(R.id.passwordEditText);
        if (emailView.getText().toString().isEmpty() || passView.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please insert your information!", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(emailView.getText().toString(), passView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            /*if (!user.isEmailVerified()) {
                                Toast.makeText(loginActivity.this, "This Email address is not verified yet!", Toast.LENGTH_SHORT).show();
                                return;
                            }*/
                            Intent UsersActivity = new Intent(getApplicationContext(), usersActivity.class);
                            startActivity(UsersActivity);
                            //finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException)
                                Toast.makeText(getApplicationContext(), "This Email Address does not exist!", Toast.LENGTH_SHORT).show();
                            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                Toast.makeText(getApplicationContext(), "Invalid Password!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void Register(View view) {
        Intent registerAcitivty = new Intent(this, registerActivity.class);
        startActivity(registerAcitivty);
    }
}
