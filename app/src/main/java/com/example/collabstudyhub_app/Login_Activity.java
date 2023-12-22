package com.example.collabstudyhub_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login_Activity extends AppCompatActivity {

    private Button loginbutton;
    private Button registerbutton;
    private EditText loginEmailTextEdit, mdpTextEdit;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginbutton = findViewById(R.id.bntLogin);
        registerbutton = findViewById(R.id.btnregisterLogin);
        loginEmailTextEdit = findViewById(R.id.emailLogin);
        mdpTextEdit = findViewById(R.id.mdpLogin);
        authProfile = FirebaseAuth.getInstance();

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get edittexts value and put them in String variables
                String userEmailLogin = loginEmailTextEdit.getText().toString();
                String userPassword = mdpTextEdit.getText().toString();

                //check if all fields are  empty
                if(userEmailLogin.isEmpty() || userPassword.isEmpty())
                {
                    Toast.makeText(Login_Activity.this, "Veuillez saisir votre email et mot de passe !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loginStudent(userEmailLogin,userPassword);
                }
            }

        });

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this, Register_Activity.class));
            }
        });

        getSupportActionBar().hide();
    }

    private void loginStudent(String emailLogin, String passwordLogin)
    {
        authProfile.signInWithEmailAndPassword(emailLogin, passwordLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(Login_Activity.this, "Bienvenue !", Toast.LENGTH_SHORT).show();

                    // open user dashboard
                    Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                    // to prevent user from returning back to register activity or pressing back button after registration
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    //close register activity
                    finish();
                }
                else
                {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e)
                    {
                        Toast.makeText(Login_Activity.this, "l'utilisateur n'existe pas ou n'est plus valide. !!", Toast.LENGTH_SHORT).show();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        Toast.makeText(Login_Activity.this, "les informations d'identification invalides. veuillez vérifier à nouveau", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // check if user is already logged in. In such case, take the user to the user home page
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() != null)
        {
            startActivity(new Intent(Login_Activity.this, MainActivity.class));
            finish();
        }
    }


}