package com.example.heylen.teacherreachermobileapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditTeacher extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "FirebaseEmailPassword";

    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;

    private Button saveButton;

    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private String uid;
    private Teacher teacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher);


        saveButton = (Button)findViewById(R.id.saveTeacherButton);
        saveButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();
        final String email = user.getEmail();
        ((TextView) findViewById(R.id.menuMail)).setText(email);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef = mDatabase.child("teacher/");

        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacher = dataSnapshot.getValue(Teacher.class);
                ((EditText) findViewById(R.id.nameTeacherEditText)).setText(teacher.naam);
                ((EditText) findViewById(R.id.ageTeacherEditText)).setText(teacher.leeftijd);
                ((EditText) findViewById(R.id.locationTeacherEditText)).setText(teacher.locatie);
                ((EditText) findViewById(R.id.numberTeacherEditText)).setText(teacher.nummer);
                ((EditText) findViewById(R.id.classesTeacherEditText)).setText(teacher.vakken);
                ((EditText) findViewById(R.id.aboutTeacherEditText)).setText(teacher.over);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

        mNavItems.add(new NavItem("Home", "Uw profiel"));
        mNavItems.add(new NavItem("Profiel bewerken", "Maak hier aanpassingen aan uw profiel"));
        mNavItems.add(new NavItem("Uitloggen", "Log hier uit"));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
    }


    private void selectItemFromDrawer(int position) {
        if (position == 0)
        {
            Intent myIntent = new Intent(EditTeacher.this, TeacherHome.class);
            EditTeacher.this.startActivity(myIntent);
        }
        if (position == 1)
        {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
        if (position == 2)
        {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(EditTeacher.this, LoginActivity.class);
            EditTeacher.this.startActivity(myIntent);
        }
        mDrawerLayout.closeDrawer(mDrawerPane);
    }


    @Override
    public void onClick(View v) {
        String name = ((EditText) findViewById(R.id.nameTeacherEditText)).getText().toString();
        String age = ((EditText) findViewById(R.id.ageTeacherEditText)).getText().toString();
        String location = ((EditText) findViewById(R.id.locationTeacherEditText)).getText().toString();
        String number = ((EditText) findViewById(R.id.numberTeacherEditText)).getText().toString();
        String classes = ((EditText) findViewById(R.id.classesTeacherEditText)).getText().toString();
        String about = ((EditText) findViewById(R.id.aboutTeacherEditText)).getText().toString();
        if (v == saveButton){
            if(validation(name, age, location, number, classes, about)) {
                writeTeacher(uid, name, age, location, number, classes, about);
                Intent myIntent = new Intent(EditTeacher.this, TeacherHome.class);
                EditTeacher.this.startActivity(myIntent);
                Log.e(TAG, "Account aanpassen: Success!");
                return;
            }
        }
    }

    private void writeTeacher(String userId, String name, String age, String location, String number, String classes, String about) {
        Teacher user = new Teacher();
        user.Set(userId, name, age, location, number, classes, about);
        mDatabase.child("teacher").child(userId).setValue(user);
    }

    class NavItem {
        String mTitle;
        String mSubtitle;

        public NavItem(String title, String subtitle) {
            mTitle = title;
            mSubtitle = subtitle;
        }
    }

    private boolean validation(String name, String age, String location, String number, String classes, String about) {

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Geef uw naam in!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(age)) {
            Toast.makeText(getApplicationContext(), "Geef uw leeftijd in!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(location)) {
            Toast.makeText(getApplicationContext(), "Geef uw woonplaats in!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(getApplicationContext(), "Geef een nummer in waarop u bereikbaar bent!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(classes)) {
            Toast.makeText(getApplicationContext(), "Geef de vakken die u geeft in!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(about)) {
            Toast.makeText(getApplicationContext(), "Geef wat informatie over uzelf in!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );

            return view;
        }
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

        public void Set(String userId, String name, String age, String location, String number, String classes, String about) {
            this.userId = userId;
            this.naam = name;
            this.nummer = number;
            this.locatie = location;
            this.TorS = "Teacher";
            this.over = about;
            this.leeftijd = age;
            this.vakken = classes;
        }
    }
}
