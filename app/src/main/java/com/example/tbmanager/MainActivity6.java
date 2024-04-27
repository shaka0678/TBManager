package com.example.tbmanager;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tbmanager.databinding.ActivityMain6Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;

public class MainActivity6 extends AppCompatActivity {

    ActivityMain6Binding binding;
    String fullname, gender, age, residence, next_of_kin, contact;
    FirebaseDatabase db;
    DatabaseReference reference;
    EditText editTextnames;
    EditText editTextgender;
    EditText editTextage;
    EditText editTextreside;
    EditText editTextnext;
    EditText editTextcontact;
    Button buttonnext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Inflater ActivityMainBinding6;
        binding = ActivityMain6Binding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main6);

        EditText editTextnames = findViewById(R.id.email1);
        EditText editTextgender = findViewById(R.id.gen1);
        EditText editTextage = findViewById(R.id.bknz);
        EditText editTextreside = findViewById(R.id.bkn1);
        EditText editTextnext = findViewById(R.id.bkn2);
        EditText editTextcontact = findViewById(R.id.dky2);


        Button buttonnext = findViewById(R.id.btnk);

        buttonnext.setOnClickListener(v -> {
            String email1 = editTextnames.getText().toString();
            String gen1 = editTextgender.getText().toString();
            String bknz = editTextage.getText().toString();
            String bkn1 = editTextreside.getText().toString();
            String bkn2 = editTextnext.getText().toString();
            String dky2 = editTextcontact.getText().toString();

            if (!email1.isEmpty() && !gen1.isEmpty() && !bknz.isEmpty() && !bkn1.isEmpty() && !bkn2.isEmpty() && !dky2.isEmpty()) {
                // Create a Patients object with the input data
                Patients patients = new Patients(email1, gen1, bknz, bkn1, bkn2, dky2);

                // Get a reference to the Firebase database
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference reference = db.getReference("Patients");

                // Set the value in the database
                reference.child(email1).setValue(patients).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Data saved successfully
                        Toast.makeText(MainActivity6.this, "Patient added", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the error (e.g., network issues)
                        Toast.makeText(MainActivity6.this, "Error saving patient data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Handle the case where some fields are empty
                Toast.makeText(MainActivity6.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
