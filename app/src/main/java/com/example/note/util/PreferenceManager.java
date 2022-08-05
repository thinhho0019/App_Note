package com.example.note.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.List;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("APPNOTEPRE",context.MODE_PRIVATE);
    }
    public void putString(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }

}
