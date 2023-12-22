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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HubmateAdapter extends RecyclerView.Adapter<HubmateAdapter.MyViewHolder>{

    private List<ModelStudent> studentList;
    private Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String connected_user = firebaseUser.getUid().toString();

    public HubmateAdapter() {
    }

    public HubmateAdapter(List<ModelStudent> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public HubmateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hubmate, parent, false);
        return new HubmateAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HubmateAdapter.MyViewHolder holder, int position) {

        ModelStudent student = studentList.get(position);

        // Bind data to the ViewHolder's views
        holder.fullnameStudent_Hubmate.setText(student.getStudentname());
        holder.levelStudent_Hubmate.setText(student.getStudentniveau());
        holder.fieldStudent_Hubmate.setText(student.getStudentfiliere());
        holder.idStudent_Hubmate.setText(student.getStudentID());

        if (student.getUriImageStudent().equals(""))
        {
            // students havent change his profile yet
        }
        else
        {
            // display students profile picture using picasso
            Picasso.with(holder.avatarStudent_Hubmate.getContext()).load(student.getUriImageStudent()).fit().centerCrop()
                    .error(R.drawable.default_profile_picture)
                    .into(holder.avatarStudent_Hubmate);
        }

        // send message
        holder.send_button_Hubmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("name", student.getStudentname());
                intent.putExtra("uid", student.getStudentID());
                intent.putExtra("receiverPhoto",student.getUriImageStudent());
                intent.putExtra("backTo","hubmates");
                context.startActivity(intent);
            }
        });

        // unfriend
        holder.unfriend_button_Hubmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               FirebaseDatabase.getInstance().getReference("Friends").child(connected_user).child(student.getStudentID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FirebaseDatabase.getInstance().getReference("Friends").child(student.getStudentID()).child(connected_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(context,"vous n'êtes plus hubmates avec " + student.getStudentname(), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullnameStudent_Hubmate, levelStudent_Hubmate, fieldStudent_Hubmate, idStudent_Hubmate;
        CircleImageView avatarStudent_Hubmate;
        Button send_button_Hubmate, unfriend_button_Hubmate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullnameStudent_Hubmate = itemView.findViewById(R.id.studentItemName_Hubmate);
            levelStudent_Hubmate = itemView.findViewById(R.id.studentItem_level_Hubmate);
            fieldStudent_Hubmate = itemView.findViewById(R.id.studentItem_field_Hubmate);
            avatarStudent_Hubmate = itemView.findViewById(R.id.image_Hubmate);
            idStudent_Hubmate = itemView.findViewById(R.id.studentItemID_Hubmate);

            send_button_Hubmate = itemView.findViewById(R.id.sendMsg_Hubmate);
            unfriend_button_Hubmate = itemView.findViewById(R.id.unfriend_Hubmate);

        }
    }
}
