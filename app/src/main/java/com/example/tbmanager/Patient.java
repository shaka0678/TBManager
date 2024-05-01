package com.example.tbmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Patient extends AppCompatActivity {
    CardView cardViewcall;
    CardView cardViewmap;
    ImageButton imageButtonlogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient);
        cardViewcall =findViewById(R.id.patientCard);
        cardViewmap =findViewById(R.id.aCard);
        imageButtonlogout =findViewById(R.id.logh);

        cardViewcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient.this,Call.class);
                startActivity(intent);

            }
        });
        cardViewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        imageButtonlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Patient.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();

                        Toast.makeText(Patient.this,"Logout successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Patient.this, landin.class);
                        startActivity(intent);
                        finish();

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No," do nothing or dismiss the dialog
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }
}