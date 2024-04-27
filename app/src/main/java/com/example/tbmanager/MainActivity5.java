package com.example.tbmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity5 extends AppCompatActivity {
    CardView cardViewabout;
    CardView cardViewsupport;
    CardView cardViewshare;
    CardView cardViewlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main5);
        cardViewabout = findViewById(R.id.crd1);
        cardViewsupport = findViewById(R.id.crdk);
        cardViewshare = findViewById(R.id.crd2);
        cardViewlog = findViewById(R.id.crd3);

        cardViewabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity5.this);
                builder.setTitle("About US");
                builder.setMessage("This is a final year project created by Shyaka and Fetaa for TB geofencing");

                builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });
                builder.show();

            }
        });

        cardViewsupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity5.this);
                builder.setTitle("Support");
                builder.setMessage("Email us: shyakaandre7@gmail.com/fetaachris6@gmail.com");

                builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });
                      builder.show();
            }
        });

        cardViewlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity5.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();

                        Toast.makeText(MainActivity5.this,"Logout successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity5.this, landin.class);
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