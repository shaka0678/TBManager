package com.example.tbmanager;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbmanager.R;
import com.example.tbmanager.fenceD;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private List<fenceD> fenceDList = new ArrayList<>();
    private DatabaseReference reference;
    private CountDownTimer countDownTimer;
    private Context context;

    public PatientAdapter(DatabaseReference reference) {
        this.reference = reference;
    }

    public void startCountDownTimer(Date targetDate) {
        long currentTime = System.currentTimeMillis();
        long targetTime = targetDate.getTime();
        long remainingTime = targetTime - currentTime;

        if (remainingTime > 0) {
            countDownTimer = new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Update UI with remaining time
                }

                @Override
                public void onFinish() {
                    // Handle countdown finish
                    Toast.makeText(context, "Patient geofence expired", Toast.LENGTH_SHORT).show();
                }
            };

            countDownTimer.start();
        } else {
            // Handle case where target date is in the past
        }
    }

    @NonNull
    @Override
    public PatientAdapter.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_patient, parent, false);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAdapter.PatientViewHolder holder, int position) {
        fenceD fenceD = fenceDList.get(position);
        // Set your data to your views here
        // You can access the views from the holder object
        // For example: holder.nameTextView.setText(fenceD.getName());
    }

    @Override
    public int getItemCount() {
        return fenceDList.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {
        // Declare your views here
        // For example: TextView nameTextView;

        public PatientViewHolder(View itemView) {
            super(itemView);
            // Initialize your views here
            // For example: nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}