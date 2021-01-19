package com.example.keepup;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.keepup.data.DBHelper;
import com.example.keepup.data.MyAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;
import static androidx.appcompat.app.AppCompatDelegate.*;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class HabitList extends Fragment {

    FloatingActionButton fab_add;
    DBHelper DBHelper;

    //Empty LIST
    public static RecyclerView rv_habits;
    public static ImageView iv_emptyView;

    //Adapter
    public static MyAdapter myAdapter;

    ArrayList<String> Title = new ArrayList<>();
    ArrayList<String> Description = new ArrayList<>();
    ArrayList<String> TimeStamp = new ArrayList<>();
    ArrayList<String> Icon = new ArrayList<>();

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        DBHelper = new DBHelper(getContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_habit_list, container, false);
        iv_emptyView = (ImageView) view.findViewById(R.id.iv_emptyView);
        rv_habits = (RecyclerView) view.findViewById(R.id.rv_habits);

        //Adapter
        SetMyAdapter();

        CheckForHabits();

        setHasOptionsMenu(true);

        SwipeRefreshLayout swipeToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeToRefresh.setSize(1);  //large size
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                @Override
                                                public void onRefresh() {
                                                    Title.clear();
                                                    Description.clear();
                                                    TimeStamp.clear();
                                                    Icon.clear();

                                                    getMyData();
                                                    SetMyAdapter();
                                                    swipeToRefresh.setRefreshing(false);
                                                }
                                            });

        fab_add = view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_habitList_to_createHabitItem);
            }
        });

        return view;
    }

    public void CheckForHabits() {
        if(DBHelper.CountRows()==0) {
            rv_habits.setVisibility(GONE);
            iv_emptyView.setVisibility(View.VISIBLE);
        } else {
            rv_habits.setVisibility(View.VISIBLE);
            iv_emptyView.setVisibility(GONE);

            getMyData();
            SetMyAdapter();
        }
    }

    public void getMyData() {

        DBHelper.getData();
        this.Title = DBHelper.Title;
        this.Description = DBHelper.Description;
        this.TimeStamp = DBHelper.TimeStamp;
        this.Icon = DBHelper.Icon;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.nav_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String itemID = item.toString();
        Log.d("ItemID: ", itemID);

        switch (item.toString()) {
            case "Delete Everything":
                if(DBHelper.CountRows()==0) AddHabitFirstAlert();
                else DeleteEverythingAlert();
                break;
            case "Night Mode":
                SwitchNightMode();
                break;
            case "Local Calendar":
                OpenLocalCalendar();
                break;
            default:
                Toast.makeText(getContext(),"Please try again.", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void DeleteEverythingAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());   //select the activity
        builder.setTitle("DELETE");
        builder.setMessage("Do you really want to delete all habits? The action can't be undone.");
        builder.setCancelable(true);   //allows you to go back to the activity by clicking out of the alert

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        dialog.dismiss();  // quit AlertDialog
                        DBHelper.deleteEverything();
                        Toast.makeText(getContext(),"All habits have been deleted!", Toast.LENGTH_LONG).show();
                        getMyData();
                        SetMyAdapter();
                        CheckForHabits();
                    }
                });

        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        dialog.dismiss();  // quit AlertDialog
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void SwitchNightMode() {
        if (getDefaultNightMode()==MODE_NIGHT_NO) {
            setDefaultNightMode(MODE_NIGHT_YES);
        } else {
            setDefaultNightMode(MODE_NIGHT_NO);
        }
    }

    public void OpenLocalCalendar() {
        try {
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, Calendar.getInstance().getTimeInMillis());
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(builder.build());
            startActivity(intent);
        } catch (Exception e) {
            InstallGoogleCalendarAlert();
        }
    }

    public void InstallGoogleCalendarAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());   //select the activity
        builder.setTitle("ATTENTION");
        builder.setMessage("Please install Google Calendar to use this functionality.");
        builder.setCancelable(true);   //allows you to go back to the activity by clicking out of the alert

        builder.setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        dialog.dismiss();  // quit AlertDialog
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void AddHabitFirstAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());   //select the activity
        builder.setTitle("ATTENTION");
        builder.setMessage("Please add a habit first.");
        builder.setCancelable(true);   //allows you to go back to the activity by clicking out of the alert

        builder.setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        dialog.dismiss();  // quit AlertDialog
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void SetMyAdapter() {
        myAdapter = new MyAdapter(getContext(), Title, Description, TimeStamp, Icon);
        rv_habits.setAdapter(myAdapter);
        rv_habits.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}