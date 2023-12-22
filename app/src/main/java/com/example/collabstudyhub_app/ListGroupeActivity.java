package com.example.collabstudyhub_app;

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

public class ListGroupeActivity extends AppCompatActivity {

    private ImageView go_back_home;
    private RecyclerView list_Group_RV;
    GroupAdapter groupAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    List<ModelGroup> groupList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groupe);

        go_back_home = findViewById(R.id.go_back_litsGroup);

        // set up our recycler
        list_Group_RV = findViewById(R.id.recyclerViewListGroup);
        list_Group_RV.setLayoutManager(new LinearLayoutManager(this));

        go_back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListGroupeActivity.this, MainActivity.class));
                finish();
            }
        });

        groupList = new ArrayList<>();

        // get list of groups
        FirebaseDatabase.getInstance().getReference("Groups")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    // Convert each DataSnapshot to a Student object
                                    ModelGroup group = snapshot.getValue(ModelGroup.class);
                                    if (group != null) {
                                        groupList.add(group);
                                    }
                                }

                                // Update the RecyclerView adapter
                                groupAdapter = new GroupAdapter(groupList, ListGroupeActivity.this);
                                list_Group_RV.setAdapter(groupAdapter);
                                groupAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(ListGroupeActivity.this, "no", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Toast.makeText(ListGroupeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        getSupportActionBar().hide();
    }
}