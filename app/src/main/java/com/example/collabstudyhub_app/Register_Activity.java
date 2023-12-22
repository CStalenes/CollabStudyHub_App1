package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register_Activity extends AppCompatActivity {

    private EditText nomUtilisateurtv, phonetv, emailtv, mdptv, etablissementtv;

    private Spinner spinner_levels, spinner_fields;
    private String selectedLevel, selectedField;
    private ArrayAdapter<CharSequence> levelAdapter, fieldAdapter;
    private Button btnRegister;
    private TextView retourloginTV;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        nomUtilisateurtv = findViewById(R.id.nomUtilisateurRegister);
        phonetv = findViewById(R.id.phoneRegister);
        emailtv = findViewById(R.id.emailRegister);
        mdptv = findViewById(R.id.mdpRegister);
        etablissementtv = findViewById(R.id.etablissementRegister);
        progressBar = findViewById(R.id.progressbarregister);
        spinner_levels = findViewById(R.id.spinnerlevel);
        spinner_fields = findViewById(R.id.spinnerField);

        btnRegister = findViewById(R.id.registerbtn);
        retourloginTV = findViewById(R.id.retourLogin);

        // populate levelAdapter using string array from ressource
        levelAdapter = ArrayAdapter.createFromResource(Register_Activity.this,R.array.levels, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_levels.setAdapter(levelAdapter);

        fieldAdapter = ArrayAdapter.createFromResource(Register_Activity.this,R.array.college_field, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_fields.setAdapter(fieldAdapter);

        // when any item if spinner_level is selected
        spinner_levels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                // obtain the selected level
                selectedLevel = spinner_levels.getSelectedItem().toString();

                int parentID = parent.getId();
                if(parentID == R.id.spinnerlevel)
                {
                    switch (selectedLevel)
                    {
                        case "Collège-6éme" :
                        case "Collège-5éme" :
                        case "Collège-4éme" :
                        case "Collège-3éme" :fieldAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.college_field, android.R.layout.simple_spinner_item);
                            break;

                        case "Lycée-2nde" :
                        case "Lycée-1ère" :
                        case "Lycée-Terminale" : fieldAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.lycee_field, android.R.layout.simple_spinner_item);
                            break;

                        case "DEUG" :
                        case "BTS" :
                        case "DUT" :
                        case "DEUST" :
                        case "Licence" :
                        case "Licence professionnelle" :
                        case "Maîtrise" :
                        case "Master" : fieldAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.university_field, android.R.layout.simple_spinner_item);
                            break;
                        default:break;
                    }
                }
                fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // populate fields spinner according to selected level
                spinner_fields.setAdapter(fieldAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_fields.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedField = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get edit texts values and put them in String variables
                String usernameRegister = nomUtilisateurtv.getText().toString().toLowerCase().trim();
                String useremailRegister = emailtv.getText().toString().toLowerCase().trim();
                String usermdpRegister = mdptv.getText().toString().trim();
                String userphoneRegister = phonetv.getText().toString().trim();
                String userniveauRegister = selectedLevel;
                String userfiliereRegister = selectedField;
                String useretablissementRegister = etablissementtv.getText().toString().toLowerCase().trim();

                //add emty image url
                String imgURL = "";

                //check if all fields are not empty
                if(usernameRegister.isEmpty() || userphoneRegister.isEmpty() ||useremailRegister.isEmpty() ||usermdpRegister.isEmpty() ||userniveauRegister.isEmpty() || userfiliereRegister.isEmpty() || useretablissementRegister.isEmpty())
                {
                    Toast.makeText(Register_Activity.this, "Veuillez vous assurer que tous les champs sont remplis", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(usernameRegister,useremailRegister,usermdpRegister,userphoneRegister,userniveauRegister,userfiliereRegister,useretablissementRegister,imgURL);
                }
                Toast.makeText(Register_Activity.this, selectedLevel, Toast.LENGTH_SHORT).show();
                Toast.makeText(Register_Activity.this, selectedField, Toast.LENGTH_SHORT).show();
            }
        });

        retourloginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register_Activity.this,Login_Activity.class));
                finish();
            }
        });
    }

    //register user using all entered fields
    private void registerUser(String unameRegister, String uemailRegister, String umdpRegister, String uphoneRegister, String univeauRegister, String ufiliereRegister, String uetablissementRegister,String imgUrlGregister)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(uemailRegister,umdpRegister).addOnCompleteListener(Register_Activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    //save user data into firebase realtime database
                    // create a class that we can use in saving, updating or retreiving student details
                    ReadWriteStudentDetails writeStudentDetails = new ReadWriteStudentDetails(firebaseUser.getUid(),unameRegister,uemailRegister,uphoneRegister,univeauRegister,ufiliereRegister,uetablissementRegister,imgUrlGregister);

                    //extracting user reference from database for "Students"
                    DatabaseReference referenceProfileStudent = FirebaseDatabase.getInstance().getReference("Students");

                    referenceProfileStudent.child(firebaseUser.getUid()).setValue(writeStudentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                // send vérification email to the user
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(Register_Activity.this, "Compte crée avec succés, un email de confirmation a été envoyé", Toast.LENGTH_SHORT).show();

                                // open user dashboard
                                Intent intent = new Intent(Register_Activity.this, MainActivity.class);

                                // to prevent user from returning back to register activity or pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                //close register activity
                                finish();
                            }
                            else
                            {
                                Toast.makeText(Register_Activity.this, "Echec de l'enregistrement", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });
                }
                else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        Toast.makeText(Register_Activity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                    catch (FirebaseAuthUserCollisionException e)
                    {
                        Toast.makeText(Register_Activity.this, "Un utilisateur déjà enregistré avec cet email", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Register_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}