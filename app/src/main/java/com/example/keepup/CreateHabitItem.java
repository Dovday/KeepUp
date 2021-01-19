package com.example.keepup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.keepup.data.DBHelper;

import java.util.Calendar;

public class CreateHabitItem extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    public View view;

    private int day = 0;
    private int month = 0;
    private int year = 0;
    private int hour= 0;
    private int minute = 0;

    public String cleanDate = "";
    public String cleanTime = "";

    DBHelper DB;

    EditText et_title, et_description, et_icon;
    TextView tv_timeSelected, tv_dateSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = new DBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_create_habit_item, container, false);

        et_title = (EditText) view.findViewById(R.id.et_habitTitle);
        et_description = (EditText) view.findViewById(R.id.et_habitDescription);
        et_icon = (EditText) view.findViewById(R.id.et_icon);
        tv_timeSelected = (TextView) view.findViewById(R.id.tv_timeSelected);
        tv_dateSelected = (TextView) view.findViewById(R.id.tv_dateSelected);

        Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHabitToDB();
            }
        });

        pickDateAndTime();

        // Inflate the layout for this fragment
        return view;
    }

    private void addHabitToDB() {

        String title = et_title.getText().toString();
        Log.d("titleStamp:", title);

        String description = et_description.getText().toString();
        Log.d("descriptionStamp:", description);

        String icon = et_icon.getText().toString();
        Log.d("iconStamp:", icon);

        String timeStamp = cleanDate + " " + cleanTime;
        Log.d("timeStamp:", timeStamp);

        if(!(title.isEmpty() || description.isEmpty() || timeStamp.isEmpty() || icon.isEmpty())) {
            Boolean flagInsert = DB.insertHabitdetails(title, description, timeStamp, icon);

            if(!flagInsert) {
                Toast.makeText(getContext(),"Error: habit NOT created!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_createHabitItem_to_habitList);
            } else {
                Toast.makeText(getContext(),"Habit created successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_createHabitItem_to_habitList);
            }
        } else {
            Toast.makeText(getContext(),"Please fill all the fields!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        cleanDate = cleanDate(dayOfMonth, month, year);
        tv_dateSelected.setText(String.format("Date: %s", cleanDate));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        cleanTime = cleanTime(hourOfDay, minute);
        tv_timeSelected.setText(String.format("Time: %s", cleanTime));
    }

    private void pickDateAndTime() {
        Button btn_pickDate = (Button) view.findViewById(R.id.btn_pickDate);
        btn_pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateCalendar();
                new DatePickerDialog(requireContext(),
                        CreateHabitItem.this,
                        year,
                        month,
                        day).show();
            }
        });

        Button btn_pickTime = (Button) view.findViewById(R.id.btn_pickTime);
        btn_pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeCalendar();
                new TimePickerDialog(getContext(),
                        CreateHabitItem.this,
                        hour,
                        minute,
                        true).show();    //true = 24h format ; false = 12h format
            }
        });

    }

    private void getTimeCalendar() {
        Calendar cal;
        cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
    }

    private void getDateCalendar() {
        Calendar cal;
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH); //jan = 0, dec = 11
        year = cal.get(Calendar.YEAR);
    }

    private String cleanDate(int _day, int _month, int _year) {
        String month, day;

        _month++;        //jan = 0 ; dec = 11

        if(_day<10) day = "0" + _day;
        else day = "" + _day;

        if(_month<10) month = "0" + _month;
        else month = "" + _month;

        return day + "/" + month + "/" + _year;
    }

    private String cleanTime(int _hour, int _minute) {
        String minute, hour;

        if(_hour<10) hour = "0" + _hour;
        else hour = "" + _hour;

        if(_minute<10) minute = "0" + _minute;
        else minute = "" + _minute;

        return hour + ":" + minute;
    }
}