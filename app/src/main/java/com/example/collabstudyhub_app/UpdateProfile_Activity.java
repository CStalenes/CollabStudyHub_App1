package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile_Activity extends AppCompatActivity {

    private Spinner spinner_levels_update, spinner_fields_update;
    private String selectedLevel_update, selectedField_update;
    private ArrayAdapter<CharSequence> levelAdapter_update, fieldAdapter_update;
    private CircleImageView profilePicture_CircleImage;
    private ImageView  edit_avatar_update_imageView;
    private TextView backToHomeTextView, level_updateTextView, field_updateTextView;
    private EditText nomUtilisateur_EditText, phone_EditText, email_EditText, etablissement_EditText;
    private Button update_Button;
    private ProgressBar progressBar;
    private FirebaseAuth authProfil;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    String imgURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        backToHomeTextView = findViewById(R.id.textViewBackToHome1);

        nomUtilisateur_EditText = findViewById(R.id.nomUtilisateurUpdate);
        phone_EditText = findViewById(R.id.phoneUpdate);
        email_EditText = findViewById(R.id.emailUpdate);
        etablissement_EditText = findViewById(R.id.etablissementUpdate);

        edit_avatar_update_imageView = findViewById(R.id.imageView_edit_avatar_update);

        profilePicture_CircleImage = findViewById(R.id.connectedUserPicUpdate);

        progressBar = findViewById(R.id.progressbarUpdate);

        // put old values in text views
        level_updateTextView = findViewById(R.id.tv_level_update);
        field_updateTextView = findViewById(R.id.tv_field_update);

        //hide text views
        level_updateTextView.setVisibility(View.GONE);
        field_updateTextView.setVisibility(View.GONE);

        // spinner
        spinner_levels_update = findViewById(R.id.spinnerlevelUpdate);
        // define spinner_field and populate it according the selected level
        spinner_fields_update = findViewById(R.id.spinnerFieldUpdate);

        update_Button = findViewById(R.id.updateBTN);

        authProfil = FirebaseAuth.getInstance();
        firebaseUser = authProfil.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("StudentsPics");

        // populate levelAdapter using string array from ressource
        levelAdapter_update = ArrayAdapter.createFromResource(UpdateProfile_Activity.this,R.array.levels, android.R.layout.simple_spinner_item);
        levelAdapter_update.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_levels_update.setAdapter(levelAdapter_update);

        fieldAdapter_update = ArrayAdapter.createFromResource(UpdateProfile_Activity.this,R.array.college_field, android.R.layout.simple_spinner_item);
        fieldAdapter_update.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_fields_update.setAdapter(fieldAdapter_update);

        //show profile data
        showProfile(firebaseUser);

        // when any item if spinner_level is selected
        spinner_levels_update.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                level_updateTextView.setText("");
                field_updateTextView.setText("");

                // obtain the selected level
                selectedLevel_update = spinner_levels_update.getSelectedItem().toString();

                int parentID = parent.getId();
                if(parentID == R.id.spinnerlevelUpdate)
                {
                    switch (selectedLevel_update)
                    {
                        case "Collège-6éme" :
                        case "Collège-5éme" :
                        case "Collège-4éme" :
                        case "Collège-3éme" :fieldAdapter_update = ArrayAdapter.createFromResource(parent.getContext(),R.array.college_field, android.R.layout.simple_spinner_item);
                            break;

                        case "Lycée-2nde" :
                        case "Lycée-1ère" :
                        case "Lycée-Terminale" : fieldAdapter_update = ArrayAdapter.createFromResource(parent.getContext(),R.array.lycee_field, android.R.layout.simple_spinner_item);
                            break;

                        case "DEUG" :
                        case "BTS" :
                        case "DUT" :
                        case "DEUST" :
                        case "Licence" :
                        case "Licence professionnelle" :
                        case "Maîtrise" :
                        case "Master" : fieldAdapter_update = ArrayAdapter.createFromResource(parent.getContext(),R.array.university_field, android.R.layout.simple_spinner_item);
                            break;
                        default:break;
                    }
                }
                fieldAdapter_update.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // populate fields spinner according to selected level
                spinner_fields_update.setAdapter(fieldAdapter_update);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_fields_update.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedField_update = parent.getItemAtPosition(position).toString();
                field_updateTextView.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //show profile data
        showProfile(firebaseUser);

        //set student's picture in imageView if he uploaded already
        Uri uri = firebaseUser.getPhotoUrl();

        // is student did not modified his picture
        if(uri == null)
        {
            // keep defalut profile pic
        }
        else
        {
            Picasso.with(UpdateProfile_Activity.this).load(uri).into(profilePicture_CircleImage);
        }
        backToHomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile_Activity.this,MainActivity.class));
                finish();
            }
        });

        edit_avatar_update_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilechooser();
            }
        });

        update_Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //uploadPicAndProfilUpdate();
                //user select a pic
                if(uriImage != null)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    //save the image with uid of the current logged student
                    StorageReference fileReference = storageReference.child(authProfil.getCurrentUser().getUid() + "." + getFileExtension(uriImage));
                    //save image to storage
                    fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    firebaseUser = authProfil.getCurrentUser();

                                    // set the sutudent's image after apload
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                                    firebaseUser.updateProfile(profileUpdates);

                                    updateStudentProfile(firebaseUser,uri.toString());

                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
                // if user has already changed his photo
                else
                {
                    if (firebaseUser.getPhotoUrl() != null)
                    {
                        updateStudentProfile(firebaseUser,firebaseUser.getPhotoUrl().toString());

                        progressBar.setVisibility(View.GONE);
                    }
                    // if user still have default pic
                    else
                    {
                        updateStudentProfile(firebaseUser,"");
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    //fetch data from database and show it
    private void showProfile(FirebaseUser firebaseUser)
    {
        String studentID = firebaseUser.getUid();
        // extracting student reference from database
        DatabaseReference referenceStudent = FirebaseDatabase.getInstance().getReference("Students");

        referenceStudent.child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ReadWriteStudentDetails readStudentDetails = snapshot.getValue(ReadWriteStudentDetails.class);
                if (readStudentDetails != null)
                {
                    nomUtilisateur_EditText.setText(readStudentDetails.studentname);
                    email_EditText.setText(readStudentDetails.studentemail);

                    // set level spinner selected item
                    String selectedLevel = readStudentDetails.studentniveau;
                    int levelPosition = findPositionInAdapter(selectedLevel,levelAdapter_update);
                    spinner_levels_update.setSelection(levelPosition);

                    // set field spinner selected item
                    String selectedField = readStudentDetails.studentfiliere;
                    int fieldPosition = findPositionInAdapter(selectedField,fieldAdapter_update);
                    spinner_fields_update.setSelection(fieldPosition);

                    level_updateTextView.setText(readStudentDetails.studentniveau);
                    field_updateTextView.setText(readStudentDetails.studentfiliere);
                    phone_EditText.setText(readStudentDetails.studentphone);
                    etablissement_EditText.setText(readStudentDetails.studentetablissement);
                }
                else
                {
                    Toast.makeText(UpdateProfile_Activity.this, "Une erreur s'est produite.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void openFilechooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            uriImage = data.getData();
            profilePicture_CircleImage.setImageURI(uriImage);
        }
    }

    private int findPositionInAdapter(String item, ArrayAdapter adapter) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(item)) {
                return i;
            }
        }
        return 0; // Default to the first item if not found
    }

    // test
    private void uploadPicAndProfilUpdate()
    {

    }

    // update student profile
    private void updateStudentProfile(FirebaseUser firebaseUser,String downloadUriString)
    {
        String fullnameStudent = nomUtilisateur_EditText.getText().toString().toLowerCase().trim();
        String emailStudent = email_EditText.getText().toString().toLowerCase().trim();
        String phoneStudent = phone_EditText.getText().toString().trim();
        String niveauStudent = level_updateTextView.getText().toString();
        String fieldStudent = field_updateTextView.getText().toString();

        // for level and field
        if(level_updateTextView.getText().equals("") || field_updateTextView.getText().equals(""))
        {
            niveauStudent = selectedLevel_update;
            fieldStudent = selectedField_update;
        }


        String etablissementStudent = etablissement_EditText.getText().toString().toLowerCase().trim();

        //check if all fields are not empty
        if(fullnameStudent.isEmpty() || emailStudent.isEmpty() ||phoneStudent.isEmpty() ||niveauStudent.isEmpty() ||fieldStudent.isEmpty() || etablissementStudent.isEmpty())
        {
            Toast.makeText(UpdateProfile_Activity.this, "Veuillez vous assurer que tous les champs sont remplis", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // get student id
            String studentID = firebaseUser.getUid();
            // start progress bar
            //progressBar.setVisibility(View.VISIBLE);

            // enter user data into firebase database
            ReadWriteStudentDetails writeStudentDetails = new ReadWriteStudentDetails(studentID,fullnameStudent, emailStudent, phoneStudent, niveauStudent, fieldStudent, etablissementStudent,downloadUriString);

            // extract student reference from database
            DatabaseReference updatedStudentReference = FirebaseDatabase.getInstance().getReference("Students");


            updatedStudentReference.child(studentID).setValue(writeStudentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        // setting new display name
                        UserProfileChangeRequest studentprofileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(fullnameStudent).build();
                        firebaseUser.updateProfile(studentprofileUpdate);


                        Toast.makeText(UpdateProfile_Activity.this, "Mise à jour réussie", Toast.LENGTH_SHORT).show();

                        // Stop user from returning to updateprofile activity after pressing back button and close activity
                        Intent intent = new Intent(UpdateProfile_Activity.this, Profil_activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        //close register activity
                        finish();
                    } else {
                        Toast.makeText(UpdateProfile_Activity.this, "Echec de l'enregistrement", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}