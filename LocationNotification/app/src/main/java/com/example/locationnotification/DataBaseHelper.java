package com.example.locationnotification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static String TABLE_NAME;
    public static final String COLUMN_MARKER_ID = "ID";
    public static final String COLUMN_MARKER_LATITUDE = "MARKER_LATITUDE";
    public static final String COLUMN_MARKER_LONGITUDE = "MARKER_LONGITUDE";
    public static final String COLUMN_MARKER_NAME = "MARKER_NAME";
    public static final String COLUMN_MARKER_DESCRIPTION = "MARKER_DESCRIPTION";
    public static final String COLUMN_MARKER_ICON = "MARKER_ICON";
    public static final String COLUMN_MARKER_DISTANCE= "MARKER_DISTANCE";

    private LatLng latLng;
    private String ID;
    private String name;
    private String description;
    private int icon;
    private float distance;
    private boolean isCompleted;
    private boolean isFlat;
    private Circle circle;

    public DataBaseHelper( Context context, String tableName) {
        super(context, "Dft.db", null, 1);
        TABLE_NAME = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_MARKER_ID + " INT, " + COLUMN_MARKER_LATITUDE + " INT, " + COLUMN_MARKER_LONGITUDE + " INT, " + COLUMN_MARKER_NAME + " TEXT, " + COLUMN_MARKER_DESCRIPTION + " TEXT, " +  COLUMN_MARKER_ICON + " INT, " + COLUMN_MARKER_DISTANCE + " INT)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(DftMarker marker){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MARKER_ID,marker.getID());
        cv.put(COLUMN_MARKER_NAME,marker.getName());
        cv.put(COLUMN_MARKER_LATITUDE,(float) marker.getLatLng().latitude);
        System.out.println(Float.toString((float) marker.getLatLng().latitude));
        cv.put(COLUMN_MARKER_LONGITUDE,(float) marker.getLatLng().longitude);
        cv.put(COLUMN_MARKER_DESCRIPTION,marker.getDescription());
        cv.put(COLUMN_MARKER_ICON,marker.getIcon());
        cv.put(COLUMN_MARKER_DISTANCE,(int)marker.getDistance());

        long insert = db.insert(TABLE_NAME, null, cv);
        db.close();
        return !(insert == -1);
    }
    public boolean deleteOne(DftMarker marker){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM "+TABLE_NAME+" WHERE "+COLUMN_MARKER_ID+" = "+marker.getID();
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }
    public List<DftMarker> getEveryone(){
        List<DftMarker> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do{
                int ID = cursor.getInt(0);
                float latitude = cursor.getFloat(1);
                float longitude = cursor.getFloat(2);
                String name = cursor.getString(3);
                String description = cursor.getString(4);
                int icon = cursor.getInt(5);
                float distance = (float) cursor.getInt(6);
                DftMarker marker = new DftMarker(new LatLng(latitude,longitude),ID,name,description,icon,distance);
                returnList.add(marker);
            }while (cursor.moveToNext());
        }else{

        }
        cursor.close();
        db.close();
        return returnList;
    }
}
