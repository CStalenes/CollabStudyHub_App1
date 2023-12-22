package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collabstudyhub_app.databinding.ActivityChatBinding;
import com.example.collabstudyhub_app.databinding.ActivitySalonChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SalonChatActivity extends AppCompatActivity {
    ActivitySalonChatBinding binding;

    SalonChatAdapter adapter;
    ArrayList<Message> messages;

    ImageView go_back_arrow_img;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalonChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter = new SalonChatAdapter(this,messages);
        database = FirebaseDatabase.getInstance();

        binding.recyclerViewChatGroup.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewChatGroup.setAdapter(adapter);

        go_back_arrow_img = findViewById(R.id.go_back_arrow);

        database.getReference().child("public")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            Message message = snapshot1.getValue(Message.class);
                            messages.add(message);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        String senderUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        binding.sendToAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBoxGroup.getText().toString();
                Date date = new Date();

                // create an object message
                Message message = new Message(messageTxt,senderUID,date.getTime());

                // clear messagebox
                binding.messageBoxGroup.setText("");

                // add in chat table if it exsist if not create it
                database.getReference().child("public")
                        .push()
                        .setValue(message);
                }

        });

        // go back
        go_back_arrow_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalonChatActivity.this,ListGroupeActivity.class));
                finish();
            }
        });

        getSupportActionBar().hide();

    }

}