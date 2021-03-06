/*
 * HabitEventHistory
 *
 * Version 1.0
 *
 * November 12, 2017
 *
 * Copyright (c) 2017 Team NOTcmput301, CMPUT301, University of Alberta - All Rights Reserved
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behavior at University of Alberta.
 * You can find a copy of the license in the project wiki on github. Otherwise please contact miller4@ualberta.ca.
 */
package com.notcmput301.habitbook;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Activity for displaying Habit Event History
 *
 * @author NOTcmput301
 * @version 1.0
 * @since 1.0
 */
public class HabitEventHistory2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private User loggedInUser;
    private HabitTypeSingleton HTS;
    private ArrayList<HabitType> habitTypes;
    private NetworkHandler nH;

    private ArrayList<HabitEvent> habitEvents = new ArrayList<>();
    private Gson gson = new Gson();

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState previous instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_event_history2);

        Intent receiver = getIntent();

        String u = receiver.getExtras().getString("passedUser");
        loggedInUser = gson.fromJson(u, User.class);
        HTS=HabitTypeSingleton.getInstance();
        habitTypes = HTS.getHabitTypes();
        nH = new NetworkHandler(this);

        FillList();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FillList();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.eventmap);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent map = new Intent(HabitEventHistory2.this, MapsActivity.class);
                map.putExtra("events", gson.toJson(habitEvents));
                startActivity(map);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //enables us to put our own icon and not show up as greys
        navigationView.setItemIconTintList(null);
        //change the headerviews name, and image
        View headerview = navigationView.getHeaderView(0);
        TextView navName = (TextView) headerview.findViewById(R.id.MNavH_Name);
        navName.setText(loggedInUser.getUsername());
        navigationView.setNavigationItemSelectedListener(this);

        //Checks if Network Connection is detected.
        BroadcastReceiver br = new NetworkStateChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);
    }

    /**
     * trigger function for when back button
     *
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called when creating options menu
     *
     * @param menu menu object to operate on
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.habit_event_history2, menu);
        return true;
    }

    /**
     * function for handling options menu
     *
     * @param item selected menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function for handling navigation menu selections
     *
     * @param item selected navigation item
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.habit_type) {
            Intent habitType = new Intent(HabitEventHistory2.this, HabitTypeList2.class);
            habitType.putExtra("passedUser", gson.toJson(loggedInUser));
            finish();
            startActivity(habitType);

        } else if (id == R.id.today_habit) {

            Intent todayHabit = new Intent(HabitEventHistory2.this, MainActivity.class);
            todayHabit.putExtra("passedUser", gson.toJson(loggedInUser));
            finish();
            startActivity(todayHabit);
        } else if (id == R.id.habit_event_history) {

        } else if (id == R.id.online) {

            if(!nH.isNetworkAvailable()){
                Toast.makeText(this, "Content Not accessible without internet", Toast.LENGTH_LONG).show();
            }else{
                //HLS.setList(habitTypes);
                HTS.setHabitTypes(habitTypes);
                Intent online = new Intent(HabitEventHistory2.this, Online.class);
                online.putExtra("passedUser", gson.toJson(loggedInUser));
                finish();
                startActivity(online);
            }
        } else if (id == R.id.logout) {
            Intent logout = new Intent(HabitEventHistory2.this, LoginActivity.class);
            startActivity(logout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * function for handling filter button click.
     *
     * @param view view of current activity status
     */
    public void onFilterClick(View view) {
        EditText htBar = (EditText) findViewById(R.id.heHabitTypeFilter);
        EditText commentBar = (EditText) findViewById(R.id.heCommentFilter);

        String htString = htBar.getText().toString().trim().replaceAll("\\s+", " ");
        String commentString = commentBar.getText().toString().trim().replaceAll("\\s+", " ");
        
        filterFillList(htString, commentString);

        //filterFillList(htString, commentString);
    }

    /**
     * Fill listview with Habit Events
     *
     */
    public void FillList(){
        ListView EventHistoryList = (ListView) findViewById(R.id.eventList);

        if (habitTypes.size() < 1){
            habitTypes = nH.getHabitList(loggedInUser.getUsername());
            HTS.setHabitTypes(habitTypes);
        }
        habitEvents.clear();

        for (int i = 0; i<habitTypes.size(); i++){
            HabitType h = habitTypes.get(i);

            ArrayList<HabitEvent> events = h.getEvents();
            habitEvents.addAll(events);
        }


        HabitEventHistory2.EventHistoryAdapter eventHistoryAdapter = new HabitEventHistory2.EventHistoryAdapter();
        EventHistoryList.setAdapter(eventHistoryAdapter);
        EventHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent habitdetail = new Intent(HabitEventHistory2.this, HabitEventDetailsActivity.class);

                habitdetail.putExtra("passedUser", gson.toJson(loggedInUser));
                habitdetail.putExtra("passedHabitEvent", gson.toJson(habitEvents.get(position)));

                startActivity(habitdetail);
            }
        });
    }

    // filterFillList coded by Matteo
    // Updates list after filter is clicked

    /**
     * Filters habit list with user defined criteria
     *
     * @param hType HabitType to filter by
     * @param comment comment to filter by
     */
    public void filterFillList(String hType, String comment) {
        ListView heList = (ListView) findViewById(R.id.eventList); // Get listView


        // Declare booleans to determine whether variables are empty
        boolean hTypeEmpty = (hType.isEmpty());
        boolean commentEmpty = (comment.isEmpty());


        // Set up temperary listviews to be used in future loops
        ArrayList<HabitType> tempHt = new ArrayList<HabitType>();
        ArrayList<HabitEvent> tempEvent = new ArrayList<>();


        // Return list to normal if both fields are empty
        if (hTypeEmpty && commentEmpty) {
            FillList();
            return;
        }

        // if Habit type isn't empty (means there isn't a habit type filter)
        // iterate through habit types, and put ones with matching title into tempHt ArrayList
        if ( ! hTypeEmpty)  {
            for (HabitType ht: habitTypes) {
                if ( ht.getTitle().equals(hType) ) tempHt.add(ht);
            }
        }
        // Or add everything if habit type not filtered for
        else {
            tempHt.addAll(habitTypes);
        }

        // If comment type is filtered, iterate through tempHt, get it's habit events, put them
        // into temp, and add events with comments that include a substring of comment parameter
        // is added into tempEvent
        if (! commentEmpty) {
            for (HabitType ht : tempHt) {
                ArrayList<HabitEvent> temp;
                temp = ht.getEvents();
                for (HabitEvent he : temp) {
                    String tempCom = he.getComment();
                    int boolIndex = tempCom.indexOf(comment);

                    if (boolIndex != -1) tempEvent.add(he);
                }
            }
        }

        // If comment type is not filtered, add all events for each habitType in tempHt
        else {
            for (HabitType ht : tempHt) {
                ArrayList<HabitEvent> temp;
                temp = ht.getEvents();
                tempEvent.addAll(temp);
            }
        }

        // Set habitEvents field variable to tempEvent (so i can reuse this adapter)
        habitEvents = tempEvent;

        // Resetet adapter to habitEvents, and reset list click listener
        HabitEventHistory2.EventHistoryAdapter eventHistoryAdapter = new HabitEventHistory2.EventHistoryAdapter();
        heList.setAdapter(eventHistoryAdapter);
        heList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent habitdetail = new Intent(HabitEventHistory2.this, HabitEventDetailsActivity.class);

                habitdetail.putExtra("passedUser", gson.toJson(loggedInUser));
                habitdetail.putExtra("passedHabitEvent", gson.toJson(habitEvents.get(position)));

                startActivity(habitdetail);
            }
        });

    }


    class EventHistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return habitEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.habit_event_list_layout, null);
            TextView titleL = (TextView) convertView.findViewById(R.id.HTLIST_Title);
            TextView descriptionL = (TextView) convertView.findViewById(R.id.HTLIST_Description);
            TextView likes = (TextView) convertView.findViewById(R.id.likes);
            CircleImageView imageV = (CircleImageView) convertView.findViewById(R.id.eventImg);
            try {
                imageV.setImageBitmap(habitEvents.get(position).imageToBitmap());
            }catch (Exception e){

            }
            Integer i = habitEvents.get(position).getLikes();
            likes.setText("Likes: "+i.toString());
            titleL.setText(habitEvents.get(position).getComment());
            descriptionL.setText("habit type: "+habitEvents.get(position).getHabit());
            return convertView;
        }
    }
}

