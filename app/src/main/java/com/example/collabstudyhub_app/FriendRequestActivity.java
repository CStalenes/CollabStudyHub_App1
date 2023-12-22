package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.text.UStringsKt;

public class FriendRequestActivity extends AppCompatActivity {

    ImageView emtyFR_List;
    RecyclerView recyclerView_FR;
    ImageView go_back_arrowImgView;
    FriendRequestAdapter friendRequestAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    List<ModelStudent> studentList;

    List<String> studentIdsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        // current user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // set up our recycler
        recyclerView_FR = findViewById(R.id.recycler_FR);
        recyclerView_FR.setLayoutManager(new LinearLayoutManager(this));

        go_back_arrowImgView = findViewById(R.id.go_back_arrow);
        emtyFR_List = findViewById(R.id.noFR_img);

        //go back
        go_back_arrowImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendRequestActivity.this, MainActivity.class));
                finish();
            }
        });

        // get friend request the current user has
        FirebaseDatabase.getInstance().getReference("FriendRequest")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot rootSnapshot) {
                        studentIdsList = new ArrayList<>();

                        for (DataSnapshot parentSnapshot : rootSnapshot.getChildren()) {
                            for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) {
                                String sentTo = childSnapshot.child("sent to").getValue(String.class);

                                if (firebaseUser.getUid().equals(sentTo))
                                {
                                    String friendID = parentSnapshot.getKey().toString();
                                    if (friendID != null) {
                                        studentIdsList.add(friendID);
                                        // show no friend request image
                                        emtyFR_List.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                        studentList = new ArrayList<>();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Students");
                        // get those students
                        for (String studentId : studentIdsList) {
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
                                            friendRequestAdapter = new FriendRequestAdapter(studentList, FriendRequestActivity.this);
                                            recyclerView_FR.setAdapter(friendRequestAdapter);
                                            friendRequestAdapter.notifyDataSetChanged();


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError)
                                        {
                                            Toast.makeText(FriendRequestActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(FriendRequestActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        getSupportActionBar().hide();
    }
}