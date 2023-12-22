package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class Profil_activity extends AppCompatActivity {

    private CircleImageView profilePictureImgV;
    private TextView backToHome_TextView,uploadProfilePictureTextView, studentFullNameTextView, studentEmailTextView, studentPhoneTextView,studentLevelTextView,studentFieldTextView,studentEtablissementTextView;
    private Button editProfil_button,deleteAccount_button;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        profilePictureImgV = findViewById(R.id.connectedUserPic);
        uploadProfilePictureTextView = findViewById(R.id.textView_upload_avatar);

        backToHome_TextView = findViewById(R.id.textViewBackToHome);
        editProfil_button = findViewById(R.id.btnEditProfil);
        deleteAccount_button = findViewById(R.id.btnDeleteAccount);

        studentFullNameTextView = findViewById(R.id.textView_full_name);
        studentEmailTextView = findViewById(R.id.textView_email);
        studentPhoneTextView = findViewById(R.id.textView_phone);
        studentLevelTextView = findViewById(R.id.textView_niveau);
        studentFieldTextView = findViewById(R.id.textView_field);
        studentEtablissementTextView = findViewById(R.id.textView_etablissement);

       editProfil_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Profil_activity.this,UpdateProfile_Activity.class));
               finish();
           }
       });

       deleteAccount_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Profil_activity.this,DeleteProfile_Activity.class));
               finish();
           }
       });


        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("StudentsPics");

        // verify if  a student is already logged
        if(firebaseUser == null)
        {
            Toast.makeText(this, "Une erreur s'est produite, les détails de l'utilisateur ne sont pas disponibles", Toast.LENGTH_LONG).show();
        }
        else
        {
            //show student profile details
            showStudentProfile(firebaseUser);
        }

        Uri uri = firebaseUser.getPhotoUrl();

        // is student did not modified his picture
        if(uri == null)
        {
            // keep defalut profile pic
        }
        else
        {
            //set student's picture in imageView if he uploaded already
            Picasso.with(Profil_activity.this).load(uri).into(profilePictureImgV);
        }

        backToHome_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profil_activity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void showStudentProfile(FirebaseUser firebaseUser)
    {
        // get the student ID
        String studentID = firebaseUser.getUid();
        // extracting students details from firebase reference
        DatabaseReference referenceStudent = FirebaseDatabase.getInstance().getReference("Students");
        referenceStudent.child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ReadWriteStudentDetails readStudentDetails = snapshot.getValue(ReadWriteStudentDetails.class);
                if (readStudentDetails != null)
                {
                    studentFullNameTextView.setText(readStudentDetails.studentname);
                    studentEmailTextView.setText(readStudentDetails.studentemail);
                    studentPhoneTextView.setText(readStudentDetails.studentphone);
                    studentLevelTextView.setText(readStudentDetails.studentniveau);
                    studentFieldTextView.setText(readStudentDetails.studentfiliere);
                    studentEtablissementTextView.setText(readStudentDetails.studentetablissement);
                }
                else
                {
                    Toast.makeText(Profil_activity.this, "Une erreur s'est produite.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(Profil_activity.this, "Une erreur s'est produite.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            uriImage = data.getData();
            profilePictureImgV.setImageURI(uriImage);
        }
    }
}