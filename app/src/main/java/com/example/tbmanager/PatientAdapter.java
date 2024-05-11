package com.example.tbmanager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private List<fenceD> fenceDList = new ArrayList<>();
    private DatabaseReference reference;

    public PatientAdapter(DatabaseReference reference) {
        this.reference = reference;
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fenceDList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    fenceD fenceD = snapshot.getValue(fenceD.class);
                    fenceDList.add(fenceD);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        fenceD fenceD = fenceDList.get(position);
        holder.patientName.setText(fenceD.getFullname());
        holder.duration.setText(fenceD.getDuration());
        holder.residence.setText(fenceD.getResidence());
    }

    @Override
    public int getItemCount() {
        return fenceDList.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {

        TextView patientName;
        TextView duration;
        TextView residence;

        PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            duration = itemView.findViewById(R.id.duration);
            residence = itemView.findViewById(R.id.residence);
        }
    }
}