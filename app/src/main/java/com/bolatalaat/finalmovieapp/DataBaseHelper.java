package com.bolatalaat.finalmovieapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Boal on 9/22/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Movies.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_Networks = "movies";
    public static final String COLUMN_ID = "id";
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_JSONOBJECT = "movie_data";

    String TAG = "Database Helper";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_Networks + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + MOVIE_ID
            + " integer unique, " + MOVIE_JSONOBJECT + " text not null" + ");";

    Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //null is equal custom cursor object
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(TAG, "Creating Database");
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading Database");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Networks);
        onCreate(db);
    }
}