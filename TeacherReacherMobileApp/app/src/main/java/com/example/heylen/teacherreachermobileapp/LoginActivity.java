package com.example.heylen.teacherreachermobileapp;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText EmailEditText;
    private EditText PasswordEditText;
    private Button SignInButton;
    private Button SignUpButton;
    private Button ResetPasswordButton;

    private String EmailString = "";
    private String PasswordString = "";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EmailEditText = (EditText) this.findViewById(R.id.email);
        PasswordEditText = (EditText) this.findViewById(R.id.password);
        SignInButton = (Button) this.findViewById(R.id.email_sign_in_button);
        SignUpButton = (Button) this.findViewById(R.id.email_register_button);
        ResetPasswordButton = (Button) this.findViewById(R.id.password_reset_button);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //mAuth.signOut();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(myIntent);

                } else {
                    // User is signed out
                }
            }
        };

        SignInButton.setOnClickListener(this);
        SignUpButton.setOnClickListener(this);
        ResetPasswordButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onClick(View v) {
        if( v == SignInButton) {
            EmailString = EmailEditText.getText().toString();
            PasswordString = PasswordEditText.getText().toString();

            if (EmailString.equals("") || PasswordString.equals("") || PasswordString.length() < 6)
            {
                Toast.makeText(getApplicationContext(), "Gelieve uw gegevens volledig in te geven!", Toast.LENGTH_LONG).show();
            }
            else
            {
                mAuth.signInWithEmailAndPassword(EmailString, PasswordString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (!task.isSuccessful())
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                alertDialog.setTitle("Geen profiel gevonden!");
                                alertDialog.setMessage("Voor de ingegeven gegevens zijn geen overeenkomstige profielen gevonden! Gelieve het ingegeven emailadres en paswoord te controleren.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                            }
                        }
                    });
            }

        }
        if( v == SignUpButton){
            Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
        if (v == ResetPasswordButton)
        {
            Intent myIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

