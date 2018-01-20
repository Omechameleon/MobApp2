package com.example.heylen.teacherreachermobileapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetButton = (Button) findViewById(R.id.resetPasswordButton);
        resetButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == resetButton)
        {
            resetPassword();
        }
    }

    private void resetPassword()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = ((EditText) findViewById(R.id.emailadresEditText)).getText().toString();
        if (emailAddress.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Gelieve een emailadres in te geven.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Succes:", "Email sent.");
                                Toast.makeText(getApplicationContext(), "Er is een mail naar uw emailadres verstuurd.", Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(ResetPasswordActivity.this, SignUpActivity.class);
                                ResetPasswordActivity.this.startActivity(myIntent);
                            }
                        }
                    });
        }
    }
}
