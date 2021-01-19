 package com.example.keepup.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

 public class DBHelper extends SQLiteOpenHelper {

     public SQLiteDatabase db;
     public final String TABLE_NAME = "Habitdetails";
     public final String _ID = "id";
     public final String _TITLE = "title";
     public final String _DESCRIPTION = "description";
     public final String _TIME_STAMP = "timeStamp";
     public final String _ICON = "icon";

     public ArrayList<String> Title = new ArrayList<>();
     public ArrayList<String> Description = new ArrayList<>();
     public ArrayList<String> TimeStamp = new ArrayList<>();
     public ArrayList<String> Icon = new ArrayList<>();

     public DBHelper(Context context) {
         super(context, "Habit.db", null, 1);
     }

     @Override
     public void onCreate(SQLiteDatabase db) {
         db.execSQL("create Table "+TABLE_NAME+" ("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+_TITLE+" TEXT, "+_DESCRIPTION+" TEXT, "+_TIME_STAMP+" TEXT, "+_ICON+" TEXT)");
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("drop Table if exists " + TABLE_NAME);
     }

     public Boolean insertHabitdetails(String title, String description, String timeStamp, String icon) {
         db = this.getWritableDatabase();

         Log.d("DBTitle: ", title);
         Log.d("DBDescription: ", description);
         Log.d("DBTimeStamp: ", timeStamp);
         Log.d("DBIcon: ", icon);

         ContentValues contentValues = new ContentValues();
         contentValues.put(_TITLE, title);
         contentValues.put(_DESCRIPTION, description);
         contentValues.put(_TIME_STAMP, timeStamp);
         contentValues.put(_ICON, icon);

         long result = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

         if (result == -1) {
             return false;
         } else {
             return true;
         }
     }

     public long deleteHabit(int id) {
         SQLiteDatabase db = this.getWritableDatabase();
         ArrayList<String> itemIDs = new ArrayList<>();

         String[] projection = {_ID};
         String sortOrder = _ID + " DESC";
         Cursor res = db.query(TABLE_NAME,
                 projection,
                 null,
                 null,
                 null,
                 null,
                 sortOrder);

         while (res.moveToNext()) {
             String itemID = res.getString(res.getColumnIndexOrThrow(_ID));
             itemIDs.add(itemID);
         }
         res.close();

         String[] _id = {itemIDs.get(id)};

         long s = db.delete(TABLE_NAME, _ID + " = ?", _id);
         Log.d("Message:: ", String.valueOf(_id));
         return s;
     }

     public void deleteEverything() {
         SQLiteDatabase db = this.getWritableDatabase();
         db.delete(TABLE_NAME, null, null);

         return;
     }

     public void getData() {

         SQLiteDatabase db = this.getReadableDatabase();

         String[] projection = {_TITLE, _DESCRIPTION, _TIME_STAMP, _ICON};

         String sortOrder = _ID + " DESC";

         Cursor res = db.query(TABLE_NAME,
                 projection,
                 null,
                 null,
                 null,
                 null,
                 sortOrder);

         while (res.moveToNext()) {
             String title = res.getString(res.getColumnIndexOrThrow(_TITLE));
             Title.add(title);

             String description = res.getString(res.getColumnIndexOrThrow(_DESCRIPTION));
             Description.add(description);

             String timeStamp = res.getString(res.getColumnIndexOrThrow(_TIME_STAMP));
             TimeStamp.add(timeStamp);

             String icon = res.getString(res.getColumnIndexOrThrow(_ICON));
             Icon.add(icon);
         }
         res.close();

         return;
     }

     public int CountRows() {
         SQLiteDatabase db = this.getReadableDatabase();
         int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
         return numRows;
     }
 }
