package com.example.heylen.teacherreachermobileapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class EditSchool extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "FirebaseEmailPassword";

    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Button saveButton;

    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private String uid;
    private School school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_school);

        saveButton = (Button) findViewById(R.id.saveSchoolButton);
        saveButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();
        final String email = user.getEmail();
        ((TextView) findViewById(R.id.menuMail)).setText(email);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef = mDatabase.child("school/");

        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                school = dataSnapshot.getValue(School.class);
                ((EditText) findViewById(R.id.schoolNameEditText)).setText(school.naam);
                ((EditText) findViewById(R.id.schoolLocationEditText)).setText(school.locatie);
                ((EditText) findViewById(R.id.schoolTypeEditText)).setText(school.soort);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        mNavItems.add(new NavItem("Home", "Uw profiel"));
        mNavItems.add(new NavItem("Profiel bewerken", "Maak hier aanpassingen aan uw profiel"));
        mNavItems.add(new NavItem("Leerkrachten", "Bekijk hier een lijst met leerkrachten"));
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0) {
            Intent myIntent = new Intent(EditSchool.this, SchoolHome.class);
            EditSchool.this.startActivity(myIntent);
        }
        if (position == 1) {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
        if (position == 2)
        {
            Intent myIntent = new Intent(EditSchool.this, TeacherList.class);
            EditSchool.this.startActivity(myIntent);
        }
        if (position == 3) {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(EditSchool.this, LoginActivity.class);
            EditSchool.this.startActivity(myIntent);
        }
        mDrawerLayout.closeDrawer(mDrawerPane);
    }


    @Override
    public void onClick(View v) {
        String name = ((EditText) findViewById(R.id.schoolNameEditText)).getText().toString();
        String location = ((EditText) findViewById(R.id.schoolLocationEditText)).getText().toString();
        String type = ((EditText) findViewById(R.id.schoolTypeEditText)).getText().toString();
        if (v == saveButton) {
            //Controler of er gegevens zijn ingegeven
            if (validation(name, location, type)) {
                //De gegevens naar de database schrijven
                writeSchool(uid, name, location, type);
                Intent myIntent = new Intent(EditSchool.this, SchoolHome.class);
                EditSchool.this.startActivity(myIntent);
                Log.e(TAG, "Account aanpassen: Success!");
                return;
            }
        }
    }

    private void writeSchool(String userId, String name, String location, String type) {
        School user = new School();
        user.Set(userId, name, location, type);
        mDatabase.child("school").child(userId).setValue(user);
    }

    class NavItem {
        String mTitle;
        String mSubtitle;

        public NavItem(String title, String subtitle) {
            mTitle = title;
            mSubtitle = subtitle;
        }
    }

    private boolean validation(String name, String location, String type) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Geef de schoolnaam in!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(getApplicationContext(), "Geef de locatie van uw school in!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(type)) {
            Toast.makeText(getApplicationContext(), "Geef de schoolvorm in!", Toast.LENGTH_SHORT).show();
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
            } else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);

            titleView.setText(mNavItems.get(position).mTitle);
            subtitleView.setText(mNavItems.get(position).mSubtitle);

            return view;
        }
    }

    @IgnoreExtraProperties
    public static class School {
        public String userId;
        public String naam;
        public String locatie;
        public String soort;
        public String TorS;

        public void Set(String userId, String name, String location, String type) {
            this.userId = userId;
            this.naam = name;
            this.locatie = location;
            this.soort = type;
            this.TorS = "School";
        }
    }
}