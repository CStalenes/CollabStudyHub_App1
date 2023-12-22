package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfile_Activity extends AppCompatActivity {

    private TextView retourToProfile_textView;
    private EditText mdpAuthentifier_editText;
    private Button deleteAcount_button, authentifier_button;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    private final static String TAG = "Delete Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        retourToProfile_textView = findViewById(R.id.retourtoProfileTV);
        mdpAuthentifier_editText = findViewById(R.id.mdpAuthentifier);
        authentifier_button = findViewById(R.id.bntAuthentifier);
        deleteAcount_button = findViewById(R.id.bntDeleteAccount);

        //disable delete user account until user is authenticated
        deleteAcount_button.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals(""))
        {
            Toast.makeText(this, "quelque chose s'est mal passé !! les détails de l'utilisateur ne sont pas disponibles", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DeleteProfile_Activity.this,Profil_activity.class));
            finish();
        }
        else
        {
            reAuthenticateStudent(firebaseUser);
        }

        retourToProfile_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeleteProfile_Activity.this,Profil_activity.class));
                finish();
            }
        });

    }

    private void reAuthenticateStudent(FirebaseUser firebaseUser)
    {
        authentifier_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String mdpStudent = mdpAuthentifier_editText.getText().toString();
                if(mdpStudent.isEmpty())
                {
                    Toast.makeText(DeleteProfile_Activity.this, "un mot de passe est nécessaire", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // re-authenticate student now
                    AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),mdpStudent);
                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                //disable password edit text and authenticate button
                                mdpAuthentifier_editText.setEnabled(false);
                                authentifier_button.setEnabled(false);
                                authentifier_button.setBackground(getResources().getDrawable(R.drawable.bouton_style_disable));
                                // enable delete account button
                                deleteAcount_button.setEnabled(true);
                                deleteAcount_button.setBackground(getResources().getDrawable(R.drawable.bouton_style));

                                deleteAcount_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });
                            } else
                            {
                                try
                                {
                                    throw task.getException();
                                } catch (Exception e)
                                {
                                    Toast.makeText(DeleteProfile_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog()
    {
        // set the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfile_Activity.this);
        builder.setTitle("Suppression du compte");
        builder.setMessage("Voulez-vous vraiment supprimer votre profil et les données associées.");

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //delete student account
                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            //delete user data
                            deleteStudentData();
                            // sign out the user
                            authProfile.signOut();
                            Toast.makeText(DeleteProfile_Activity.this, "Le compte a été supprimé !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DeleteProfile_Activity.this,Login_Activity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(DeleteProfile_Activity.this, "Une erreur s'est produite.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // cancel button and retour
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startActivity(new Intent(DeleteProfile_Activity.this,Profil_activity.class));
                finish();
            }
        });

        //create alert dialog
        AlertDialog alertDialog = builder.create();

        //show aler dialog
        alertDialog.show();
    }

    //delete user data and pic
    private void deleteStudentData()
    {
        //delete display pic
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "onSuccess: photo deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, e.getMessage());
                Toast.makeText(DeleteProfile_Activity.this, "quelque chose s'est mal passé !!", Toast.LENGTH_SHORT).show();
            }
        });

        //delete data from realtime database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Students");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "onSuccess: data deleted from realtime");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.i(TAG, e.getMessage());
                Toast.makeText(DeleteProfile_Activity.this, "quelque chose s'est mal passé !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}