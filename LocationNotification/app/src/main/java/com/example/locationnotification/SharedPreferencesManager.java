package com.example.locationnotification;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private final String USER_SETTINGS="USER_SETTINGS";
    private final Context context;
    public SharedPreferencesManager(Context context) {
        this.context = context;
    }
    public void addString(String stringName,String value,int MODE_PRIVATE){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_SETTINGS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(stringName,value);
        editor.apply();
    }
    public String getString(String stringName, int MODE_PRIVATE){
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_SETTINGS, MODE_PRIVATE);
        return sharedPreferences.getString(stringName, "");
    }

}
