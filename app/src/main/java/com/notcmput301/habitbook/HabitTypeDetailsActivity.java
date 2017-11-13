/*
 * HabitTypeDetailsActivity
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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Activity for viewing details of habit type
 *
 * @author NOTcmput301
 * @version 1.0
 * @see HabitType
 * @since 1.0
 */

public class HabitTypeDetailsActivity extends AppCompatActivity {
    private User LoggedInUser;
    private HabitType habit;
    private EditText titleText;
    private EditText reasonText;
    private TextView startDate;
    private TextView habitSchedule;
    private ArrayList<Integer> weekdays;
    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thursday;
    private CheckBox friday;
    private CheckBox saturday;
    private CheckBox sunday;
    private TextView completionStatus;
    private Button back;
    private Button update;
    private Button delete;
    private Button addNewHabitEvent;

    private String target;
    private Gson gson;
    private User user;
    private ArrayList<HabitType> habitTypes;
    private LocalFileController localFileControler;
    private HabitType currentHabitType;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState previous instance of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_type_details);

        Bundle bundle = getIntent().getExtras();
        int index = bundle.getInt("index");
        this.target = bundle.getString("user");
        this.gson = new Gson();
        this.user = gson.fromJson(target, User.class);
        this.habitTypes = user.getHabitTypes();
        this.currentHabitType = this.habitTypes.get(index);
        //this.target = bundle.getString("localfilecontroller");
        //this.localFileControler = gson.fromJson(target, LocalFileControler.class);
        this.weekdays = currentHabitType.getWeekdays();

        titleText = (EditText) findViewById(R.id.Title);
        titleText.setText(currentHabitType.getTitle());
        reasonText = (EditText) findViewById(R.id.Reason);
        reasonText.setText(currentHabitType.getReason());
        startDate = (TextView) findViewById(R.id.StartDate);
        startDate.setText("Starting Date: "+currentHabitType.getStartDate().toString());
        monday = (CheckBox) findViewById(R.id.Mon);
        monday.setChecked(this.weekdays.indexOf(1) != -1);
        tuesday = (CheckBox) findViewById(R.id.Tu);
        tuesday.setChecked(this.weekdays.indexOf(2) != -1);
        wednesday = (CheckBox) findViewById(R.id.Wes);
        wednesday.setChecked(this.weekdays.indexOf(3) != -1);
        thursday = (CheckBox) findViewById(R.id.Thu);
        thursday.setChecked(this.weekdays.indexOf(4) != -1);
        friday = (CheckBox) findViewById(R.id.Fri);
        friday.setChecked(this.weekdays.indexOf(5) != -1);
        saturday = (CheckBox) findViewById(R.id.Sat);
        saturday.setChecked(this.weekdays.indexOf(6) != -1);
        sunday = (CheckBox) findViewById(R.id.Sun);
        sunday.setChecked(this.weekdays.indexOf(7) != -1);
        completionStatus = (TextView) findViewById(R.id.Status);
        //completionStatus.setText(currentHabitType.get);

        back = (Button) findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }   );
        update = (Button) findViewById(R.id.Update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (monday.isChecked()){
                    if (weekdays.indexOf(1) == -1){
                        weekdays.add(1);
                    }
                }
                else{
                    weekdays.remove(new Integer(1));
                }
                if (tuesday.isChecked()){
                    if (weekdays.indexOf(2) == -1){
                        weekdays.add(2);
                    }
                }
                else{
                    weekdays.remove(new Integer(2));}
                if (wednesday.isChecked()){
                    if (weekdays.indexOf(3) == -1){
                        weekdays.add(3);
                    }
                }
                else{
                    weekdays.remove(new Integer(3));}
                if (thursday.isChecked()){
                    if (weekdays.indexOf(4) == -1){
                        weekdays.add(4);
                    }
                }
                else{
                    weekdays.remove(new Integer(4));}
                if (friday.isChecked()){
                    if (weekdays.indexOf(5) == -1){
                        weekdays.add(5);
                    }
                }
                else{
                    weekdays.remove(new Integer(5));}
                if (saturday.isChecked()){
                    if (weekdays.indexOf(6) == -1){
                        weekdays.add(6);
                    }
                }
                else{
                    weekdays.remove(new Integer(6));}
                if (sunday.isChecked()){
                    if (weekdays.indexOf(7) == -1){
                        weekdays.add(7);
                    }
                }
                else{
                    weekdays.remove(new Integer(7));}

                if (weekdays.isEmpty()){
                    Toast.makeText(HabitTypeDetailsActivity.this, "weekdays cannot be empty ", Toast.LENGTH_LONG).show();
                    return;
                }
                HabitType Newhabit = new HabitType(user.getUsername(), titleText.getText().toString(),
                        reasonText.getText().toString(),
                        currentHabitType.getStartDate(),
                        weekdays);
                Newhabit.setEvents(currentHabitType.getEvents());
                Newhabit.setEventsCompleted(currentHabitType.getEventsCompleted());
                Newhabit.setStartDate(currentHabitType.getStartDate());
                Newhabit.setCreationDate(currentHabitType.getCreationDate());
                Newhabit.setTotalEvents(currentHabitType.getTotalEvents());
                user.updateHabitType(currentHabitType, Newhabit);
                //localFileControler.Save(user);
                ElasticSearch.UpdateUserTask updateUserTask = new ElasticSearch.UpdateUserTask();
                updateUserTask.execute(user);
                Intent intent = new Intent(HabitTypeDetailsActivity.this, HabitTypeListActivity.class);
                target = gson.toJson(Newhabit);
                intent.putExtra("Newhabit", target);
                finish();

            }
        }   );
        delete = (Button) findViewById(R.id.Delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.removeHabitType(currentHabitType);
                ElasticSearch.UpdateUserTask updateUserTask = new ElasticSearch.UpdateUserTask();
                updateUserTask.execute(user);
                finish();
            }
        }   );


    }

    /**
     * Displays completion percentage of habit events
     *
     */
    public void displayCompletionStatus(HabitType Habit){

    }
}
