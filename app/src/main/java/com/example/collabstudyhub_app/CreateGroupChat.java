package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateGroupChat extends AppCompatActivity {

    private ImageView go_back_arrow_groupeChat;
    private Button createGroupe_btn;
    private EditText groupeName;
    private Spinner spinner_levels_Groupe, spinner_fields_Groupe;
    private String selectedLevel, selectedField;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String connected_user = firebaseUser.getUid().toString();
    private ArrayAdapter<CharSequence> levelAdapterGroupe, fieldAdapterGroupe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);

        go_back_arrow_groupeChat = findViewById(R.id.go_back_arrow_groupe);
        spinner_levels_Groupe = findViewById(R.id.spinnerlevelGroupe);
        spinner_fields_Groupe = findViewById(R.id.spinnerFieldGroupe);
        groupeName = findViewById(R.id.groupeName);
        createGroupe_btn = findViewById(R.id.createGroupeBTN);

        // populate levelAdapter using string array from ressource
        levelAdapterGroupe = ArrayAdapter.createFromResource(CreateGroupChat.this,R.array.levels, android.R.layout.simple_spinner_item);
        levelAdapterGroupe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_levels_Groupe.setAdapter(levelAdapterGroupe);

        fieldAdapterGroupe = ArrayAdapter.createFromResource(CreateGroupChat.this,R.array.college_field, android.R.layout.simple_spinner_item);
        levelAdapterGroupe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_fields_Groupe.setAdapter(fieldAdapterGroupe);

        // when any item if spinner_level is selected
        spinner_levels_Groupe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                // obtain the selected level
                selectedLevel = spinner_levels_Groupe.getSelectedItem().toString();

                int parentID = parent.getId();
                if(parentID == R.id.spinnerlevelGroupe)
                {
                    switch (selectedLevel)
                    {
                        case "Collège-6éme" :
                        case "Collège-5éme" :
                        case "Collège-4éme" :
                        case "Collège-3éme" :fieldAdapterGroupe = ArrayAdapter.createFromResource(parent.getContext(),R.array.college_field, android.R.layout.simple_spinner_item);
                            break;

                        case "Lycée-2nde" :
                        case "Lycée-1ère" :
                        case "Lycée-Terminale" : fieldAdapterGroupe = ArrayAdapter.createFromResource(parent.getContext(),R.array.lycee_field, android.R.layout.simple_spinner_item);
                            break;

                        case "DEUG" :
                        case "BTS" :
                        case "DUT" :
                        case "DEUST" :
                        case "Licence" :
                        case "Licence professionnelle" :
                        case "Maîtrise" :
                        case "Master" : fieldAdapterGroupe = ArrayAdapter.createFromResource(parent.getContext(),R.array.university_field, android.R.layout.simple_spinner_item);
                            break;
                        default:break;
                    }
                }
                fieldAdapterGroupe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // populate fields spinner according to selected level
                spinner_fields_Groupe.setAdapter(fieldAdapterGroupe);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinner_fields_Groupe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedField = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // create groupe
        createGroupe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String groupeChatName = groupeName.getText().toString().trim();
                // get connected user name
                Intent intent = getIntent();
                String connected_user_name = intent.getStringExtra("creator").toString();

                if (groupeChatName.isEmpty())
                {
                    Toast.makeText(CreateGroupChat.this, "Veuillez saisir un nom de groupe.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // add groupe chat in firebase database
                    CreateNewGroup(groupeChatName,selectedLevel,selectedField,connected_user_name);
                    startActivity(new Intent(CreateGroupChat.this,ListGroupeActivity.class));
                    finish();
                }


            }
        });

        // go back home page
        go_back_arrow_groupeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateGroupChat.this, MainActivity.class));
                finish();
            }
        });

        getSupportActionBar().hide();
    }

    private void CreateNewGroup(String Gname, String Glevel, String Gfield, String connectName)
    {
        HashMap hashMap = new HashMap();
        hashMap.put("name",Gname);
        hashMap.put("level",Glevel);
        hashMap.put("field",Gfield);
        hashMap.put("admin",connectName);

        // insert the new group chat
        FirebaseDatabase.getInstance().getReference("Groups").push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(CreateGroupChat.this, "Le groupe "+ Gname + " a été créé avec succès", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CreateGroupChat.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}