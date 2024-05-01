package com.example.tbmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignParent extends AppCompatActivity {
    EditText editTextEmail;
    EditText editTextpassword;
    FirebaseAuth mAuth;
    Button buttonlog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_parent);
        buttonlog = findViewById(R.id.btn1);
        editTextEmail = findViewById(R.id.email1);
        editTextpassword = findViewById(R.id.pass1);
        mAuth = FirebaseAuth.getInstance();

        buttonlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1, pass1;
                email1 = String.valueOf(editTextEmail.getText());
                pass1 = String.valueOf(editTextpassword.getText());

                if (TextUtils.isEmpty(email1)) {
                    Toast.makeText(SignParent.this, "enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pass1)) {
                    Toast.makeText(SignParent.this, "enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    mAuth.signInWithEmailAndPassword(email1, pass1)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignParent.this, Patient.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignParent.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}