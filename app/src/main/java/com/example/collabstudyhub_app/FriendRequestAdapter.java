package com.example.collabstudyhub_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {

    private List<ModelStudent> studentList;
    private Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String connected_user = firebaseUser.getUid().toString();

    public FriendRequestAdapter() {
    }

    public FriendRequestAdapter(List<ModelStudent> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelStudent student = studentList.get(position);

        // Bind data to the ViewHolder's views
        holder.fullnameStudent_FR.setText(student.getStudentname());
        holder.levelStudent_FR.setText(student.getStudentniveau());
        holder.fieldStudent_FR.setText(student.getStudentfiliere());
        holder.idStudent_FR.setText(student.getStudentID());

        if (student.getUriImageStudent().equals(""))
        {
            // students havent change his profile yet
        }
        else
        {
            // display students profile picture using picasso
            Picasso.with(holder.avatarStudent_FR.getContext()).load(student.getUriImageStudent()).fit().centerCrop()
                    .error(R.drawable.default_profile_picture)
                    .into(holder.avatarStudent_FR);
        }

        // delete friend Request
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("FriendRequest").child(student.getStudentID()).child(connected_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context, "Demande d'ajout annulée", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // accept friend request
        holder.accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the request from other will be deleted from table friend request
                FirebaseDatabase.getInstance().getReference("FriendRequest").child(student.getStudentID()).child(connected_user).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        HashMap hashMap = new HashMap();
                        hashMap.put("status","friends");

                        // add friends in the friends table
                        FirebaseDatabase.getInstance().getReference("Friends").child(connected_user).child(student.getStudentID()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if(task.isSuccessful())
                                {
                                    FirebaseDatabase.getInstance().getReference("Friends").child(student.getStudentID()).child(connected_user).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            Toast.makeText(context, "vous êtes maintenant amis", Toast.LENGTH_SHORT).show();
                                            context.startActivity(new Intent(context,Hubmates_List_Activity.class));
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });

    }
    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fullnameStudent_FR, levelStudent_FR, fieldStudent_FR, idStudent_FR;
        CircleImageView avatarStudent_FR;
        Button accept_button, delete_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullnameStudent_FR = itemView.findViewById(R.id.studentItemName_FR);
            levelStudent_FR = itemView.findViewById(R.id.studentItem_level_FR);
            fieldStudent_FR = itemView.findViewById(R.id.studentItem_field_FR);
            avatarStudent_FR = itemView.findViewById(R.id.imageStagiaire_FR);
            idStudent_FR = itemView.findViewById(R.id.studentItemID_FR);

            accept_button = itemView.findViewById(R.id.Accepter_FR);
            delete_button = itemView.findViewById(R.id.delete_FR);
        }
    }
}
