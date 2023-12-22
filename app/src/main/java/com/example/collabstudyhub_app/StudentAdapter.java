package com.example.collabstudyhub_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAdapter extends FirebaseRecyclerAdapter<ModelStudent,StudentAdapter.MyViewHolder> implements Filterable {

    Context context;

    // get connected user id
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String connected_user = firebaseUser.getUid().toString();
    // get the database if it exsists if not create it
    DatabaseReference referenceFriendRequest = FirebaseDatabase.getInstance().getReference("FriendRequest");
    DatabaseReference referenceFriends = FirebaseDatabase.getInstance().getReference("Friends");
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StudentAdapter(@NonNull FirebaseRecyclerOptions<ModelStudent> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ModelStudent model) {


        holder.fullnameStudent.setText(model.getStudentname());
        holder.levelStudent.setText(model.getStudentniveau());
        holder.fieldStudent.setText(model.getStudentfiliere());
        holder.idStudent.setText(model.getStudentID());

        if (model.getUriImageStudent().equals(""))
        {
            // students havent change his profile yet
        }
        else
        {
            // display students profile picture using picasso
            Picasso.with(holder.avatarStudent.getContext()).load(model.getUriImageStudent()).fit().centerCrop()
                    .error(R.drawable.default_profile_picture)
                    .into(holder.avatarStudent);
        }

        // hide the current user card from search result

        if(connected_user.equals(model.getStudentID()))
        {
            holder.student_cardView.setVisibility(View.GONE);
        }


        // make a test on FriendRequest table ------------------------------------------------------

        // if connected user sent already a request to this student
        referenceFriendRequest.child(connected_user).child(holder.idStudent.getText().toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            if(snapshot.child("status").getValue().toString().equals("pending"))
                            {
                                holder.requestState.setText("I sent and wait");
                                holder.addFriend_button.setText("Annuler");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // if other student sent already a request to connected user
        referenceFriendRequest.child(holder.idStudent.getText().toString()).child(connected_user)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            if(snapshot.child("status").getValue().toString().equals("pending"))
                            {
                                holder.requestState.setText("he sent and wait");
                                holder.addFriend_button.setText("Accepter");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // if we are friends
        referenceFriends.child(connected_user).child(holder.idStudent.getText().toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            holder.requestState.setText("friends");
                            holder.addFriend_button.setText("Envoyer un message");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // sent friend request
        holder.addFriend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if user has already send a friend request to this student
                // nothing happend means that there is no friend request between this two users
                if (holder.requestState.getText().equals("nothing happend"))
                {
                    HashMap hashMap = new HashMap();
                    hashMap.put("status","pending");
                    hashMap.put("sent to",holder.idStudent.getText().toString());

                    // insert the new friend request into FriendRequest table with a pending status
                    referenceFriendRequest.child(connected_user).child(holder.idStudent.getText().toString()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {
                                holder.requestState.setText("I sent and wait");
                                holder.addFriend_button.setText("Annuler");
                                Toast.makeText(context, "Demande d'ajout envoyée", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                // if current student sent friend request and it's pending
                if (holder.requestState.getText().equals("I sent and wait"))
                {
                    // in this case add button will be (annuler)
                    referenceFriendRequest.child(connected_user).child(holder.idStudent.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                holder.requestState.setText("nothing happend");
                                holder.addFriend_button.setText("Ajouter");
                                Toast.makeText(context, "Demande d'ajout annulée", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                // if other student sent a friend request to current user and it still pending
                //in this case add button will be (accepter)
                else if (holder.requestState.getText().equals("he sent and wait"))
                {
                    // the request from other will be deleted from table friend request
                    referenceFriendRequest.child(holder.idStudent.getText().toString()).child(connected_user).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            holder.requestState.setText("friends");
                            HashMap hashMap = new HashMap();
                            hashMap.put("status","friends");

                            // add friends in the friends table
                            referenceFriends.child(connected_user).child(holder.idStudent.getText().toString()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        referenceFriends.child(holder.idStudent.getText().toString()).child(connected_user).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task)
                                            {
                                                Toast.makeText(context, "vous êtes maintenant amis", Toast.LENGTH_SHORT).show();
                                                holder.addFriend_button.setText("Envoyer un message");
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
                //in this case add button will be (Envoyer un message)
                else if (holder.requestState.getText().equals("friends"))
                {
                    Intent intent = new Intent(context,ChatActivity.class);
                    intent.putExtra("name", model.getStudentname());
                    intent.putExtra("uid", model.getStudentID());
                    intent.putExtra("receiverPhoto",model.getUriImageStudent());
                    intent.putExtra("backTo","search");
                    context.startActivity(intent);
                }
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullnameStudent, levelStudent, fieldStudent, idStudent, requestState;
        CircleImageView avatarStudent;
        Button addFriend_button;

        CardView student_cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullnameStudent = itemView.findViewById(R.id.studentItemName);
            levelStudent = itemView.findViewById(R.id.studentItem_level);
            fieldStudent = itemView.findViewById(R.id.studentItem_field);
            avatarStudent = itemView.findViewById(R.id.imageStagiaire);
            idStudent = itemView.findViewById(R.id.studentItemID);

            student_cardView = itemView.findViewById(R.id.student_card);

            requestState = itemView.findViewById(R.id.requestStateTV);

            requestState.setText("nothing happend");

            addFriend_button = itemView.findViewById(R.id.btn_add_friend);

        }
    }
}
