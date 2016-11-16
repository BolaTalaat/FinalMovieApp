package com.bolatalaat.finalmovieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boal on 9/22/2016.
 */
public class DataBaseSource {

    Context context;
    String TAG = "DataBaseSource";

    // Database fields
    private SQLiteDatabase database;
    private DataBaseHelper dataBaseHelper;
    private String[] allColumns = {DataBaseHelper.COLUMN_ID,
            DataBaseHelper.MOVIE_ID, DataBaseHelper.MOVIE_JSONOBJECT};

    public DataBaseSource(Context context) {
        dataBaseHelper = new DataBaseHelper(context);
        this.context = context;
    }

    public void deleteDB() {
        context.deleteDatabase(DataBaseHelper.DATABASE_NAME);
    }

    public void open() throws SQLException {
        database = dataBaseHelper.getWritableDatabase();
        //to enable us to write into database
    }

    public void close() {
        dataBaseHelper.close();
    }

    public long addMovie(ModelData modelData) {
        long id = -1;
        try {
            //contentvaluesوسيط بشيل فيه البيانات
            ContentValues values = new ContentValues();
            values.put(DataBaseHelper.MOVIE_ID, modelData.getId());
            String movieData = modelData.getJsonObject();
            values.put(DataBaseHelper.MOVIE_JSONOBJECT, movieData);
            //يتم ادخال البيانات الي الجدول
            //null ممكن تعوض عنها باي قيمه ممكن تكون نلودي اصلا عوضا عن
            //null is equal string null coulmn hack
            id = database.insert(DataBaseHelper.TABLE_Networks, null, values);
        } catch (Exception e) {
            Toast.makeText(context, "Something Error in Adding..", Toast.LENGTH_SHORT).show();

        }
        return id;
    }

    public int deleteMovie(int movie_id) {
        int id = -1;
        try {
            id = database.delete(DataBaseHelper.TABLE_Networks, DataBaseHelper.MOVIE_ID
                    + " = " + movie_id, null);
        } catch (Exception e) {
            Toast.makeText(context, "Something Error in deleting..", Toast.LENGTH_SHORT).show();
        }
        return id;
    }

    public List<ModelData> getMovies() {
        List<ModelData> modelDataList = new ArrayList<>();
        try {
            Cursor cursor = database.query(DataBaseHelper.TABLE_Networks,
                    allColumns, null, null, null, null, null);
            cursor.moveToFirst();
            //لو موصلش لبعد الاخير
            while (!cursor.isAfterLast()) {
                String movieObject = cursor.getString(2);
                JSONObject jsonObject = new JSONObject(movieObject);
                ModelData modelData = new ModelData(jsonObject);
                modelDataList.add(modelData);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Something wrong in get Favourites");
        }
        return modelDataList;
    }

    public boolean isFavorite(int id) {
        try {
            Cursor cursor = database.query(DataBaseHelper.TABLE_Networks,
                    allColumns, DataBaseHelper.MOVIE_ID + "='" + id + "'",
                    null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isNull(0)) {
                return true;
            }
            cursor.close();
        } catch (Exception e) {

        }
        return false;
    }
}