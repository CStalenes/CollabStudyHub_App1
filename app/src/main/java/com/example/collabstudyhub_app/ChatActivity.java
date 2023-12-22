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

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;

    CircleImageView receiverpic_ImgView;
    MessageAdapter adapter;
    ArrayList<Message> messages;
    TextView receiverName;

    ImageView go_back_arrow_img;

    String senderRoom, receiverRoom;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter = new MessageAdapter(this,messages);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewChat.setAdapter(adapter);

        receiverpic_ImgView = findViewById(R.id.receiverPhoto);
        receiverName = findViewById(R.id.receiverName);
        go_back_arrow_img = findViewById(R.id.go_back_arrow);

        // get name, id and photo of student to start chat
        String name = getIntent().getStringExtra("name");
        String receiverUID  = getIntent().getStringExtra("uid");
        String receiverImg = getIntent().getStringExtra("receiverPhoto");
        String gobackTo = getIntent().getStringExtra("backTo");

        // picture test
        if (receiverImg.equals(""))
        {
            // students havent change his profile yet
        }
        else
        {
            // display students profile picture using picasso
            Picasso.with(this).load(receiverImg).fit().centerCrop()
                    .error(R.drawable.default_profile_picture)
                    .into(receiverpic_ImgView);
        }

        receiverName.setText(name);

        String senderUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        database = FirebaseDatabase.getInstance();

        database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
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

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();
                Date date = new Date();

                // create an object message
                Message message = new Message(messageTxt,senderUID,date.getTime());

                // clear messagebox
                binding.messageBox.setText("");

                // to show last msg with its time
                HashMap<String, Object> lastMsgObject = new HashMap<>();
                lastMsgObject.put("lastMsg", message.getMessage());
                lastMsgObject.put("lastMsgTime", date.getTime());

                // update chat table
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObject);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObject);

                // add in chat table if it exsist if not create it
                database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                        // to show last msg with its time
                        HashMap<String, Object> lastMsgObject = new HashMap<>();
                        lastMsgObject.put("lastMsg", message.getMessage());
                        lastMsgObject.put("lastMsgTime", date.getTime());

                        // update chat table
                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObject);
                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObject);
                    }
                });
            }
        });

        // go back
        go_back_arrow_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gobackTo.equals("inbox"))
                {
                    startActivity(new Intent(ChatActivity.this,MessagesActivity.class));
                    finish();
                }
                if(gobackTo.equals("search"))
                {
                    startActivity(new Intent(ChatActivity.this,SearchHubmates_Activity.class));
                    finish();
                }
                if(gobackTo.equals("hubmates"))
                {
                    startActivity(new Intent(ChatActivity.this,Hubmates_List_Activity.class));
                    finish();
                }
            }
        });

        getSupportActionBar().hide();

    }

}