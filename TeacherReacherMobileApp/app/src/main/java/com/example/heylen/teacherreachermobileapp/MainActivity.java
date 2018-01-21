package com.example.heylen.teacherreachermobileapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference myRefTeacher;
    private DatabaseReference myRefSchool;

    private Teacher teacher;
    private School school;

    int check1 = 0;
    int check2 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRefTeacher = mDatabase.child("teacher/");
        myRefSchool = mDatabase.child("school/");


        //We controleren in de database of er voor de huidige gebruiker data bestaat in
        //de school- of teachertak van de database
        //Als de gebruiker zich in een van deze bevindt zal hij/zij zich niet in de andere bevinden
        //Op basis hiervan verwijzen we de gebruiker door naar de juiste pagina's

        myRefTeacher.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacher = dataSnapshot.getValue(Teacher.class);
                check1 = 1;
                if ((check1 == 1) && (check2 == 1)){
                    Redirect();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        myRefSchool.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                school = dataSnapshot.getValue(School.class);
                check2 = 1;
                if ((check1 == 1) && (check2 == 1)){
                    Redirect();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void Redirect()
    {
        //Indien de gebruiker een leerkracht is
        if(teacher!=null)
        {
            Intent myIntent = new Intent(MainActivity.this, TeacherHome.class);
            MainActivity.this.startActivity(myIntent);
            return;
        }
        //Indien de gebruiker een school is
        if(school!=null)
        {
            Intent myIntent = new Intent(MainActivity.this, SchoolHome.class);
            MainActivity.this.startActivity(myIntent);
            return;
        }
        //Indien de gebruiker in geen van beide takken van de database zit
        //In dit geval zou de gebruiker zich niet eens hier mogen bevinden tenzij er iets is misgelopen
        //bij het registreren.
        //Voor de veiligheid heb ik dit ingebouwd
        //De gebruiker zal teruggestuurd worden naar de LoginActivity
        else{
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
            return;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


    @IgnoreExtraProperties
    public static class School {
        public String userId;
        public String naam;
        public String soort;
        public String locatie;
        public String TorS;
    }

    @IgnoreExtraProperties
    public static class Teacher {
        public String userId;
        public String naam;
        public String nummer;
        public String locatie;
        public String TorS;
        public String over;
        public String leeftijd;
        public String vakken;
    }


    @Override
    public void onBackPressed(){
    }
}