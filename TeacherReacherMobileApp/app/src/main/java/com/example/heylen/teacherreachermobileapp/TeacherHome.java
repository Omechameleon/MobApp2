package com.example.heylen.teacherreachermobileapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherHome extends AppCompatActivity implements View.OnClickListener {


    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Button signoutButton;

    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;

    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        signoutButton = (Button)findViewById(R.id.logoutButton);
        signoutButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        final String email = user.getEmail();
        ((TextView) findViewById(R.id.menuMail)).setText(email);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef = mDatabase.child("teacher/");

        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacher = dataSnapshot.getValue(Teacher.class);
                ((TextView) findViewById(R.id.teacherName)).setText(teacher.naam);
                ((TextView) findViewById(R.id.teacherAge)).setText(teacher.leeftijd);
                ((TextView) findViewById(R.id.teacherLocation)).setText(teacher.locatie);
                ((TextView) findViewById(R.id.teacherNumber)).setText(teacher.nummer);
                ((TextView) findViewById(R.id.teacherClasses)).setText(teacher.vakken);
                ((TextView) findViewById(R.id.teacherAbout)).setText(teacher.over);
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
        if (position == 0)
        {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
        if (position == 1)
        {
            Intent myIntent = new Intent(TeacherHome.this, EditTeacher.class);
            TeacherHome.this.startActivity(myIntent);
        }
        if (position == 2)
        {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(TeacherHome.this, LoginActivity.class);
            TeacherHome.this.startActivity(myIntent);
        }

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public void onClick(View v) {
        if (v == signoutButton){
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(TeacherHome.this, LoginActivity.class);
            TeacherHome.this.startActivity(myIntent);
        }
    }

    class NavItem {
        String mTitle;
        String mSubtitle;

        public NavItem(String title, String subtitle) {
            mTitle = title;
            mSubtitle = subtitle;
        }
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
    }
}
