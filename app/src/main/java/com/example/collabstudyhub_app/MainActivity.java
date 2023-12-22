package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CardView listSalon,mesDocuments,calerRevision,hubMates,messages,recherche;
    private TextView fullnameTV, levelTV, countFiendRequesttextV, nbrMsgTV;
    private CircleImageView loggedUserphoto;
    private ImageView notificationImgV, logoutImgV, fiendRequestImgV;
    private LinearLayout linearToProfile;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recherche = findViewById(R.id.rechercheCV);
        fullnameTV = findViewById(R.id.fullnamelogged);
        levelTV = findViewById(R.id.niveaulogged);
        loggedUserphoto = findViewById(R.id.connectedUserPic);
        notificationImgV = findViewById(R.id.notificationimageView);
        logoutImgV = findViewById(R.id.logoutimageView);
        linearToProfile = findViewById(R.id.linearToProfile);
        messages = findViewById(R.id.messageCV);
        fiendRequestImgV = findViewById(R.id.fiendRequestImageView);
        hubMates = findViewById(R.id.hubMatesCV);
        nbrMsgTV = findViewById(R.id.nbrMsg);
        calerRevision = findViewById(R.id.calerRevisionCV);
        listSalon = findViewById(R.id.listSalonCV);

        fiendRequestImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FriendRequestActivity.class));
                finish();
            }
        });

        countFiendRequesttextV = findViewById(R.id.countFiendRequestTV);
        countFiendRequesttextV.setVisibility(View.GONE);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        // count how many friend request the current user has
        FirebaseDatabase.getInstance().getReference("FriendRequest")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot rootSnapshot) {
                        int currentStudentRequestAcount = 0;

                        for (DataSnapshot parentSnapshot : rootSnapshot.getChildren()) {
                            for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) {
                                String sentTo = childSnapshot.child("sent to").getValue(String.class);

                                if (firebaseUser.getUid().equals(sentTo)) {
                                    currentStudentRequestAcount++;
                                    //Toast.makeText(MainActivity.this, parentSnapshot.getKey().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        // test if current user has friendrequest
                        if(currentStudentRequestAcount > 0)
                        {
                            countFiendRequesttextV.setVisibility(View.VISIBLE);
                            countFiendRequesttextV.setText("(" + String.valueOf(currentStudentRequestAcount) + ")");
                        }
                        else
                            countFiendRequesttextV.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // count how many messages the current user have
        FirebaseDatabase.getInstance().getReference("chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        int nbrMsg = 0;

                                                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                                            String keychat = datas.getKey().toString();

                                                            // Extract the substring from the found index to the end
                                                            String[] chatwithkey = keychat.split(firebaseUser.getUid());

                                                            if (chatwithkey[0].isEmpty()) {
                                                                //Toast.makeText(MessagesActivity.this, "chat with empty", Toast.LENGTH_SHORT).show();
                                                            } else if (chatwithkey[0].length() == firebaseUser.getUid().length()) {
                                                                nbrMsg++;
                                                            }

                                                        }

                                                        nbrMsgTV.setText("(" + String.valueOf(nbrMsg) + ")");

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

        //set student's picture in imageView if he uploaded already
        Uri uri = firebaseUser.getPhotoUrl();

        // is student did not modified his picture
        if(uri == null)
        {
            // keep defalut profile pic
        }
        else {
            Picasso.with(MainActivity.this).load(uri).into(loggedUserphoto);
        }

        if(firebaseUser == null)
        {
            Toast.makeText(this, "Une erreur s'est produite. Veuillez réessayer", Toast.LENGTH_LONG).show();
        }
        else
        {
            String studentID = firebaseUser.getUid();

            // extracting student details from database reference "Students"
            DatabaseReference referenceStudent = FirebaseDatabase.getInstance().getReference("Students");

            // get current user name and level
            referenceStudent.child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteStudentDetails readStudentDetails = snapshot.getValue(ReadWriteStudentDetails.class);
                    if(readStudentDetails != null)
                    {
                        fullnameTV.setText(readStudentDetails.studentname);
                        levelTV.setText(readStudentDetails.studentniveau);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {
                    Toast.makeText(MainActivity.this, "Une erreur s'est produite.", Toast.LENGTH_SHORT).show();
                }
            });

        }

        logoutImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        linearToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Profil_activity.class));
                finish();
            }
        });

        recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchHubmates_Activity.class));
                finish();
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MessagesActivity.class));
                finish();
            }
        });

        hubMates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Hubmates_List_Activity.class));
                finish();
            }
        });

        calerRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateGroupChat.class);
                intent.putExtra("creator",fullnameTV.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        listSalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListGroupeActivity.class));
                finish();
            }
        });

        getSupportActionBar().hide();
    }

    private void showAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Se déconnecter");
        builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?");

        //log out after clicking on yes
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                authProfile.signOut();
                startActivity(new Intent(MainActivity.this,Login_Activity.class));
                finish();
            }
        });

        // close alert dialog after clicking on no
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // create the alert dialog
        AlertDialog alertDialog = builder.create();

        // show the alerte dialog
        alertDialog.show();
    }
}