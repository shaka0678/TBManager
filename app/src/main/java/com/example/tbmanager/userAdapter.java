package com.example.tbmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<userlist> mUserList;

    public userAdapter(Context context, ArrayList<userlist> userList) {
        mContext = context;
        mUserList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main3, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        userlist currentUser = mUserList.get(position);

        // Bind user details to the ViewHolder
        holder.txtName.setText(currentUser.getFullname());
        holder.txtAge.setText(String.valueOf(currentUser.getGender()));
        holder.txtReside.setText(String.valueOf(currentUser.getResidence()));
        // Add more fields as needed
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtAge,txtReside;
        // Add more TextViews for other user details

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.namez);
            txtAge = itemView.findViewById(R.id.nameb);
            txtReside= itemView.findViewById(R.id.resid1);
            // Initialize other TextViews here
        }
    }
}
