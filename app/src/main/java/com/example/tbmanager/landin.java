package com.example.tbmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class landin extends AppCompatActivity {
    CardView cardViewPat;
    CardView cardViewdoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landin);
            cardViewdoc = findViewById(R.id.appointCard);
            cardViewPat = findViewById(R.id.patientCard);
           cardViewdoc.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(landin.this, SignUp.class);
                   startActivity(intent);
               }
           });

           cardViewPat.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(landin.this, SignParent.class);
                   startActivity(intent);
               }
           });


    }
}
