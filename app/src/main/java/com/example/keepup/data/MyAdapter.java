package com.example.keepup.data;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepup.HabitList;
import com.example.keepup.MainActivity;
import com.example.keepup.R;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.keepup.HabitList.iv_emptyView;
import static com.example.keepup.HabitList.myAdapter;
import static com.example.keepup.HabitList.rv_habits;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<String> Title;
    ArrayList<String> Description;
    ArrayList<String> TimeStamp;
    ArrayList<String> Icon;
    Context context;

    DBHelper DBHelper;

    public MyAdapter(Context ct, ArrayList<String> Title, ArrayList<String> Description, ArrayList<String> TimeStamp, ArrayList<String> Icon) {
        context = ct;
        this.Title=Title;
        this.Description=Description;
        this.TimeStamp=TimeStamp;
        this.Icon=Icon;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_habit_item, parent, false);
        DBHelper = new DBHelper(context);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.tv_item_icon.setText(Icon.get(position));
        holder.tv_item_title.setText(Title.get(position));
        holder.tv_item_description.setText(Description.get(position));
        holder.tv_item_timeStamp.setText(TimeStamp.get(position));

        holder.cv_recycler.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DeleteOneHabitAlert(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (Title.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_item_icon, tv_item_title, tv_item_description, tv_item_timeStamp;
        CardView cv_recycler;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_item_icon = itemView.findViewById(R.id.tv_item_icon);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_description = itemView.findViewById(R.id.tv_item_description);
            tv_item_timeStamp = itemView.findViewById(R.id.tv_item_timeStamp);

            cv_recycler = itemView.findViewById(R.id.cv_cardView);
        }
    }

    public void DeleteOneHabitAlert(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);   //select the activity
        builder.setTitle("DELETE");
        builder.setMessage("Do you really want to delete this habits? The action can't be undone.");
        builder.setCancelable(true);   //allows you to go back to the activity by clicking out of the alert

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {

                        if(DBHelper.deleteHabit(pos)<=0){
                            Toast.makeText(context, "Error: the habit has not been deleted. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "The habit has been deleted.", Toast.LENGTH_SHORT).show();

                            Title.clear();
                            Description.clear();
                            TimeStamp.clear();
                            Icon.clear();

                            getMyData();

                            CheckForHabits();
                        }
                        dialog.dismiss();  // quit AlertDialog
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        dialog.dismiss();  // quit AlertDialog
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void CheckForHabits() {
        if(DBHelper.CountRows()==0) {
            rv_habits.setVisibility(GONE);
            iv_emptyView.setVisibility(VISIBLE);
        } else {
            rv_habits.setVisibility(VISIBLE);
            iv_emptyView.setVisibility(GONE);
            getMyData();
            SetMyAdapter();
        }
    }

    public void getMyData() {

        DBHelper.getData();
        Title = DBHelper.Title;
        Description = DBHelper.Description;
        TimeStamp = DBHelper.TimeStamp;
        Icon = DBHelper.Icon;
    }

    public void SetMyAdapter() {
        myAdapter = new MyAdapter(context, Title, Description, TimeStamp, Icon);
        rv_habits.setAdapter(myAdapter);
        rv_habits.setLayoutManager(new LinearLayoutManager(context));
    }

}
