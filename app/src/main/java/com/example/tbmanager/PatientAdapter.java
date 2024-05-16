package com.example.tbmanager;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbmanager.R;
import com.example.tbmanager.fenceD;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

    private Context mContext;
    private List<fenceD> fenceDList;

    public PatientAdapter(Context context, ArrayList<fenceD> list) {
        this.mContext = context;
        this.fenceDList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        fenceD currentUser = fenceDList.get(position);


        holder.txtName.setText(currentUser.getPatientName());
        // Update these lines according to the actual methods in fenceD class
        holder.txtAge.setText(String.valueOf(currentUser.getResidence()));
        holder.txtReside.setText(currentUser.getDuration());
    }

    @Override
    public int getItemCount() {
        return fenceDList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtAge, txtReside;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.namez);
            txtAge = itemView.findViewById(R.id.nameb);
            txtReside = itemView.findViewById(R.id.resid1);
        }
    }
}