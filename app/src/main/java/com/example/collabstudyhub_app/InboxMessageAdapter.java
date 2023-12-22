package com.example.collabstudyhub_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxMessageAdapter extends RecyclerView.Adapter<InboxMessageAdapter.MyViewHolder> {

        private List<ModelStudent> studentList;
        private Context context;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        String connected_user = firebaseUser.getUid().toString();

    public InboxMessageAdapter() {
        }

    public InboxMessageAdapter(List<ModelStudent> studentList, Context context) {
            this.studentList = studentList;
            this.context = context;
        }

        @NonNull
        @Override
        public InboxMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
            return new InboxMessageAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            ModelStudent student = studentList.get(position);

            // Bind data to the ViewHolder's views
            holder.fullnameStudent_inbox.setText(student.getStudentname());
            //holder.levelStudent_inbox.setText(student.getStudentniveau());
            //holder.fieldStudent_inbox.setText(student.getStudentfiliere());
            holder.idStudent_inbox.setText(student.getStudentID());

            if (student.getUriImageStudent().equals(""))
            {
                // students havent change his profile yet
            }
            else
            {
                // display students profile picture using picasso
                Picasso.with(holder.avatarStudent_inbox.getContext()).load(student.getUriImageStudent()).fit().centerCrop()
                        .error(R.drawable.default_profile_picture)
                        .into(holder.avatarStudent_inbox);
            }

            // display last message and time
            String userID = student.getStudentID();
            String sendID = connected_user;
            String senderRoom = sendID + userID;

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.exists()) {
                                String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                Long time = snapshot.child("lastMsgTime").getValue(Long.class);

                                holder.lastmsg_inbox.setText(lastMsg);
                                holder.time_inbox.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(time));
                            }
                            else
                            {
                                holder.lastmsg_inbox.setText("Commencez le chat...");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                                }
                    });

            // view message
            holder.read_btn_inbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ChatActivity.class);
                    intent.putExtra("name", student.getStudentname());
                    intent.putExtra("uid", student.getStudentID());
                    intent.putExtra("receiverPhoto",student.getUriImageStudent());
                    intent.putExtra("backTo","inbox");
                    context.startActivity(intent);
                }
            });

            // delete msg
            holder.delete_btn_inbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseDatabase.getInstance().getReference("chats").child(student.getStudentID() + connected_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(context, "Chat supprimé", Toast.LENGTH_SHORT).show();
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

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView fullnameStudent_inbox, lastmsg_inbox, time_inbox, idStudent_inbox;
            CircleImageView avatarStudent_inbox;
            Button read_btn_inbox,delete_btn_inbox;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                fullnameStudent_inbox = itemView.findViewById(R.id.studentItemName_inbox);
                lastmsg_inbox = itemView.findViewById(R.id.lastMsg_inbox);
                time_inbox = itemView.findViewById(R.id.time_inbox);
                avatarStudent_inbox = itemView.findViewById(R.id.imageStagiaire_inbox);
                idStudent_inbox = itemView.findViewById(R.id.studentItemID_inbox);

                read_btn_inbox = itemView.findViewById(R.id.lire_inbox);
                delete_btn_inbox = itemView.findViewById(R.id.delete_inbox);
            }
        }
}
