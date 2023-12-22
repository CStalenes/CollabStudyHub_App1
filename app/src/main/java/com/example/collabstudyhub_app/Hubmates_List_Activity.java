package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Hubmates_List_Activity extends AppCompatActivity {

    ImageView emtyFR_List;
    RecyclerView recyclerView_Hubmates;
    ImageView go_back_arrowImgView;
    HubmateAdapter hubmateAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    List<ModelStudent> studentList;

    List<String> hubmatesIdsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hubmates_list);

        // current user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // set up our recycler
        recyclerView_Hubmates = findViewById(R.id.recyclerViewHubmates);
        recyclerView_Hubmates.setLayoutManager(new LinearLayoutManager(this));

        go_back_arrowImgView = findViewById(R.id.go_back_arrowHubmates);
        //emtyFR_List = findViewById(R.id.noFR_img);

        //go back
        go_back_arrowImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hubmates_List_Activity.this, MainActivity.class));
                finish();
            }
        });

        // get hubmates list of the current user
        FirebaseDatabase.getInstance().getReference("Friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot rootSnapshot) {

                        hubmatesIdsList = new ArrayList<>();

                        for (DataSnapshot parentSnapshot : rootSnapshot.getChildren())
                        {
                            if (firebaseUser.getUid().equals(parentSnapshot.getKey().toString()))
                            {
                                for (DataSnapshot childSnapshot : parentSnapshot.getChildren())
                                {
                                    //Toast.makeText(Hubmates_List_Activity.this, childSnapshot.getKey().toString(), Toast.LENGTH_SHORT).show();
                                    String hubmate = childSnapshot.getKey().toString();
                                    if (hubmate != null) {
                                        hubmatesIdsList.add(hubmate);
                                        // show no friend request image
                                        //emtyFR_List.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        studentList = new ArrayList<>();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Students");
                        // get those students
                        for (String studentId : hubmatesIdsList) {
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
                                            hubmateAdapter = new HubmateAdapter(studentList, Hubmates_List_Activity.this);
                                            recyclerView_Hubmates.setAdapter(hubmateAdapter);
                                            hubmateAdapter.notifyDataSetChanged();


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError)
                                        {
                                            Toast.makeText(Hubmates_List_Activity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        Toast.makeText(Hubmates_List_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        getSupportActionBar().hide();
    }
}