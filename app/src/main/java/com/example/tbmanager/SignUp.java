package com.example.tbmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    EditText editTextEmail;

    EditText editTextCreatePassword;
    FirebaseAuth mAuth;
    Button buttonreg;
    Button buttonsigk;
    TextView textViewAlready;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.passb);
        editTextCreatePassword = findViewById(R.id.bkn);
        textViewAlready = findViewById(R.id.cctxt);
        buttonreg = findViewById(R.id.btn1);


        buttonreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();
                String password = editTextCreatePassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Account created successfully.", Toast.LENGTH_SHORT).show();

                                    // User registration successful, navigate to MainActivity3
                                    Intent intent = new Intent(SignUp.this, MainActivity2 .class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Exception exception = task.getException();
                                    if (exception instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(getApplicationContext(), "An account with this email already exists.", Toast.LENGTH_SHORT).show();
                                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(getApplicationContext(), "Invalid password.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Authentication failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });


                }
            }
        });
          textViewAlready.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(SignUp.this, Login.class);
                  startActivity(intent);
              }
          });
    }
}