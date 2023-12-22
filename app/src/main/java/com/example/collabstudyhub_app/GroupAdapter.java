package com.example.collabstudyhub_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private List<ModelGroup> groupList;
    private Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    String connected_user = firebaseUser.getUid().toString();

    public GroupAdapter(List<ModelGroup> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
    }

    public GroupAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelGroup group = groupList.get(position);

        // Bind data to the ViewHolder's views
        holder.name_group.setText("Salon: " + group.getName());
        holder.level_group.setText(group.getLevel());
        holder.field_group.setText(group.getField());
        holder.admin_group.setText("Créé par: " + group.getAdmin());

        holder.perform_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SalonChatActivity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name_group, admin_group, level_group, field_group;
        Button perform_btn, leave_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name_group = itemView.findViewById(R.id.groupeNameItem);
            admin_group = itemView.findViewById(R.id.groupeadminItem);
            level_group = itemView.findViewById(R.id.group_levelItem);
            field_group = itemView.findViewById(R.id.group_fieldItem);
            perform_btn = itemView.findViewById(R.id.performGroupItem);
            leave_btn = itemView.findViewById(R.id.leavegroupItem);

        }
    }
}
