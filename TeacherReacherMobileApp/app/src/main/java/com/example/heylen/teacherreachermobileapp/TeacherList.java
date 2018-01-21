package com.example.heylen.teacherreachermobileapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherList extends AppCompatActivity implements View.OnClickListener {

    private ListView dataListView;
    private EditText searchText;
    private Button searchButton;

    private ArrayList<Teacher> teachers = new ArrayList<>();
    private ArrayList<Teacher> searchedTeachers = new ArrayList<>();


    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("/teacher");
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        dataListView = (ListView) findViewById(R.id.teacherListViewLayout);
        searchText = (EditText) findViewById(R.id.searchEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        final String email = user.getEmail();
        ((TextView) findViewById(R.id.menuMail)).setText(email);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teachers.clear();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Teacher teacher = messageSnapshot.getValue(Teacher.class);
                    teachers.add(teacher);
                }
                populateAllTeachersListView();
                registerClickCallback(teachers);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Waardes uit DB halen:", "mislukt");
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
        if (position == 0)
        {
            Intent myIntent = new Intent(TeacherList.this, SchoolHome.class);
            TeacherList.this.startActivity(myIntent);
        }
        if (position == 1)
        {
            Intent myIntent = new Intent(TeacherList.this, EditSchool.class);
            TeacherList.this.startActivity(myIntent);
        }
        if (position == 2)
        {
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
        if (position == 3)
        {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(TeacherList.this, LoginActivity.class);
            TeacherList.this.startActivity(myIntent);
        }
        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
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
        ArrayList<TeacherList.NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<TeacherList.NavItem> navItems) {
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


    private void populateAllTeachersListView(){
        ArrayAdapter<Teacher> adapter = new AllTeachersListAdapter();
        ListView list = (ListView) findViewById(R.id.teacherListViewLayout);
        list.setAdapter(adapter);
    }


    private class AllTeachersListAdapter extends ArrayAdapter<Teacher>
    {
        public AllTeachersListAdapter() {
            super(TeacherList.this, R.layout.item_view, teachers);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemview = convertView;

            //Ervoor zorgen dat we een view hebben om mee te werken
            if(itemview == null)
            {
                itemview = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            //leerkracht vinden
            Teacher  currentTeacher = teachers.get(position);

            //view vullen
            ((TextView) itemview.findViewById(R.id.nameListItem)).setText(currentTeacher.naam);
            ((TextView) itemview.findViewById(R.id.ageListItem)).setText(currentTeacher.leeftijd);
            ((TextView) itemview.findViewById(R.id.locationListItem)).setText(currentTeacher.locatie);
            ((TextView) itemview.findViewById(R.id.classesListItem)).setText(currentTeacher.vakken);
            ((TextView) itemview.findViewById(R.id.aboutListItem)).setText(currentTeacher.over);

            return itemview;
        }
    }


    private void registerClickCallback(final ArrayList<Teacher> teacherList) {
        ListView list = (ListView) findViewById(R.id.teacherListViewLayout);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                final Teacher clickedTeacher = teacherList.get(position);

                AlertDialog alertDialog = new AlertDialog.Builder(TeacherList.this).create();
                alertDialog.setTitle("Bevestig uw keuze");
                alertDialog.setMessage("Weet u zeker dat u leerkracht " + clickedTeacher.naam + " wil contacteren?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Annuleer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Bevestig", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", clickedTeacher.nummer);
                        startActivity(smsIntent);
                    }
                });
                alertDialog.show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        searchedTeachers.clear();
        int length = teachers.size();
        String searchTerm = ((EditText) findViewById(R.id.searchEditText)).getText().toString();
        if (searchTerm.equals(""))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(TeacherList.this).create();
            alertDialog.setTitle("Reset");
            alertDialog.setMessage("De leerkrachtenlijst werd succesvol gereset.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    populateAllTeachersListView();
                    registerClickCallback(teachers);
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
        else
        {
            for (Integer count = 0; count < length; count++)
            {
                Teacher element = teachers.get(count);
                if (element.vakken.toUpperCase().contains(searchTerm.toUpperCase()))
                {
                    searchedTeachers.add(element);
                }
            }
            if ((searchedTeachers.size()) == 0)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(TeacherList.this).create();
                alertDialog.setTitle("Geen resultaten gevonden!");
                alertDialog.setMessage("Voor de ingegeven zoekterm zijn geen overeenkomstige leerkrachten gevonden! De leerkrachtenlijst werd gereset.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        populateAllTeachersListView();
                        registerClickCallback(teachers);
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            else
            {
                populateSearchedTeachersListView();
                registerClickCallback(searchedTeachers);
            }
        }
    }

    private void populateSearchedTeachersListView() {
        ArrayAdapter<Teacher> adapter = new SearchedTeachersListAdapter();
        ListView list = (ListView) findViewById(R.id.teacherListViewLayout);
        list.setAdapter(adapter);
    }

    private class SearchedTeachersListAdapter extends ArrayAdapter<Teacher>
    {
        public SearchedTeachersListAdapter() {
            super(TeacherList.this, R.layout.item_view, searchedTeachers);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemview = convertView;

            //Ervoor zorgen dat we een view hebben om mee te werken
            if(itemview == null)
            {
                itemview = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            //leerkracht vinden
            Teacher  currentTeacher = searchedTeachers.get(position);

            //view vullen
            ((TextView) itemview.findViewById(R.id.nameListItem)).setText(currentTeacher.naam);
            ((TextView) itemview.findViewById(R.id.ageListItem)).setText(currentTeacher.leeftijd);
            ((TextView) itemview.findViewById(R.id.locationListItem)).setText(currentTeacher.locatie);
            ((TextView) itemview.findViewById(R.id.classesListItem)).setText(currentTeacher.vakken);
            ((TextView) itemview.findViewById(R.id.aboutListItem)).setText(currentTeacher.over);

            return itemview;
        }
    }


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
