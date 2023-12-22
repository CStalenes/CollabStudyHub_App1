package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessagesActivity extends AppCompatActivity {

    ImageView backTohome, nomsg_imgV;
    private RecyclerView inbox_RV;
    private InboxMessageAdapter inboxMessageAdapter;
    List<ModelStudent> studentList;
    List<String> listWithoutDuplicates;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        backTohome = findViewById(R.id.go_back_arrowMsg);
        inbox_RV = findViewById(R.id.recyclerViewInbox);
        nomsg_imgV = findViewById(R.id.nomsg_img);

        inbox_RV.setLayoutManager(new LinearLayoutManager(this));

        backTohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessagesActivity.this, MainActivity.class));
                finish();
            }
        });


        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();


        // get Messages the current user has
        FirebaseDatabase.getInstance().getReference("chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        listWithoutDuplicates = new ArrayList<>();

                        for (DataSnapshot datas : dataSnapshot.getChildren())
                        {
                            String keychat = datas.getKey().toString();

                            // Extract the substring from the found index to the end
                            String[] chatwithkey = keychat.split(currentUser);

                            if (chatwithkey[0].isEmpty())
                            {
                               //Toast.makeText(MessagesActivity.this, "chat with empty", Toast.LENGTH_SHORT).show();
                            }
                            else if (chatwithkey[0].length() == currentUser.length())
                            {
                                //Toast.makeText(MessagesActivity.this, chatwithkey[0], Toast.LENGTH_SHORT).show();
                                listWithoutDuplicates.add(chatwithkey[0]);
                            }
                            else
                            {
                                //Toast.makeText(MessagesActivity.this, "nothing", Toast.LENGTH_SHORT).show();
                            }

                                // show no friend request image
                                nomsg_imgV.setVisibility(View.GONE);
                        }

                        Toast.makeText(MessagesActivity.this, String.valueOf(listWithoutDuplicates.size()), Toast.LENGTH_SHORT).show();

                        studentList = new ArrayList<>();

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Students");
                            // get those students
                            for (String studentId : listWithoutDuplicates) {
                                databaseReference.orderByChild("studentID").equalTo(studentId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    // Convert each DataSnapshot to a Student object
                                                    ModelStudent student = snapshot.getValue(ModelStudent.class);
                                                    if (student != null) {
                                                        studentList.add(student);
                                                    }
                                                }

                                                // Update the RecyclerView adapter
                                                inboxMessageAdapter = new InboxMessageAdapter(studentList, MessagesActivity.this);
                                                inbox_RV.setAdapter(inboxMessageAdapter);
                                                inboxMessageAdapter.notifyDataSetChanged();


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError)
                                            {
                                                Toast.makeText(MessagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(MessagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        getSupportActionBar().hide();

    }

   /* @Override
    protected void onStart() {
        super.onStart();
        inboxMessageAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        inboxMessageAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }*/
}