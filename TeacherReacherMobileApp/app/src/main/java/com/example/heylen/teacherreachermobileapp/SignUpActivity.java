package com.example.heylen.teacherreachermobileapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "FirebaseEmailPassword";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button registerButton;
    private String email;
    private String password1;
    private String password2;
    private RadioGroup TorSGroep;
    private String TorS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        registerButton = (Button) this.findViewById(R.id.registerButton);
        TorSGroep = (RadioGroup) findViewById(R.id.TorSKeuze);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton){
        createAccount();
        }
    }

    private void createAccount()
    {
        email = ((EditText) this.findViewById(R.id.emailText)).getText().toString();
        password1 = ((EditText) this.findViewById(R.id.passwordText1)).getText().toString();
        password2 = ((EditText) this.findViewById(R.id.passwordText2)).getText().toString();
        int radioButtonID = TorSGroep.getCheckedRadioButtonId();
        View radioButton = TorSGroep.findViewById(radioButtonID);
        int idx = TorSGroep.indexOfChild(radioButton);
        RadioButton r = (RadioButton) TorSGroep.getChildAt(idx);
        TorS = r.getText().toString();

        Log.e(TAG, "createAccount:" + email);
        if (!validation(email, password1, password2, radioButtonID))
        {
            return;
        }

        //Registreren van de authenticatiegegevens van de aangemaakte gebruiker
        //Dit zijn het emailadres en ingegeven paswoord
        //Deze authenticatie staat los van de database
        //Deze vullen we verder in de methode in
        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Als de gebruiker succesvol werd aangemaakt controleren we of het een leerkracht of school is
                            //Aan de hand hiervan bepalen we dan wat we wegschrijven naar de database
                            //We sturen de gebruiker ook meteen door naar zijn bijbehorende homepagina
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            if(TorS.equals("School")){
                                writeNewSchool(uid);
                                Intent myIntent = new Intent(SignUpActivity.this, SchoolHome.class);
                                SignUpActivity.this.startActivity(myIntent);
                                Log.e(TAG, "Account aanmaken: Success!");
                                return;
                            }
                            if (TorS.equals("Leerkracht")){
                                writeNewTeacher(uid);
                                Intent myIntent = new Intent(SignUpActivity.this, TeacherHome.class);
                                SignUpActivity.this.startActivity(myIntent);
                                Log.e(TAG, "Account aanmaken: Success!");
                                return;
                            }
                            else{
                                Log.e(TAG, "Accountinformatie aanmaken: Mislukt!", task.getException());
                                Toast.makeText(getApplicationContext(), "Accountinformatie aanmaken mislukt!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "Account aanmaken: Mislukt!", task.getException());
                            Toast.makeText(getApplicationContext(), "Account aanmaken mislukt!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private boolean validation(String email, String password1, String password2, int radioButtonID) {
        //Per if-statement spreekt de Toast-text voor zich
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Geef een emailadres in!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password1)) {
            Toast.makeText(getApplicationContext(), "Geef een paswoord in!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password1.length() < 6) {
            Toast.makeText(getApplicationContext(), "Paswoord te kort, geef minimum 6 karakters in!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(password1.equals(password2)))
        {
            Toast.makeText(getApplicationContext(), "Uw ingegeven paswoorden komen niet overeen!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (radioButtonID == -1)
        {
            Toast.makeText(getApplicationContext(), "Maak een keuze tussen School of Leerkracht!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void writeNewTeacher(String userId) {
        Teacher user = new Teacher(userId);
        //We schrijven de nieuwe leerkracht naar de database
        mDatabase.child("teacher").child(userId).setValue(user);
    }

    private void writeNewSchool(String userId) {
        School school = new School(userId);
        //We schrijven de nieuwe school naar de database
        mDatabase.child("school").child(userId).setValue(school);
    }


    @IgnoreExtraProperties
    public static class School {
        public String userId;
        public String naam = "De schoolnaam";
        public String soort = "De schoolvorm";
        public String locatie = "Het schooladres";
        public String TorS = "School";

        public School(String userId) {
            this.userId = userId;
        }
    }

    @IgnoreExtraProperties
    public static class Teacher {
        public String userId;
        public String naam = "Uw naam";
        public String nummer = "Uw telefoonnummer";
        public String locatie = "Uw woonplaats";
        public String TorS = "Teacher";
        public String over = "Over uzelf";
        public String leeftijd = "Uw leeftijd";
        public String vakken = "De vakken die u geeft";

        public Teacher(String userId) {
            this.userId = userId;
        }
    }

}
