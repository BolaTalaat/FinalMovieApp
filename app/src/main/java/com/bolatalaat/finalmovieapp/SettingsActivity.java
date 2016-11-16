package com.bolatalaat.finalmovieapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Boal on 9/22/2016.
 */
public class SettingsActivity {
    Context context;
    private final String prefName = "Setting";
    private static final String sortBy = "sortBy";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SettingsActivity(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(prefName,
                Context.MODE_PRIVATE);
        editor = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .edit();
    }

    public void setSortBy(String sortBy) {
        editor.putString(SettingsActivity.this.sortBy, sortBy);
        editor.apply();

    }

    public String getSortBy() {
        return preferences.getString(SettingsActivity.sortBy, "popular");
    }
}