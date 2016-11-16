package com.bolatalaat.finalmovieapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements Callback {

    static final String TAG = "MainActivityLog";
    public static FrameLayout frameLayout; // static to be used in MovieFragment to check the device type.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }
        frameLayout = (FrameLayout) findViewById(R.id.frame_container);
    }

    @Override
    public void actionCallback(ModelData modelData) {
        Log.d(TAG, "Callback with " + modelData.getJsonObject());
        /*
            framelayout is null when the android OS read the small layout,
            and it will allocate reference in case tablet
            is used as the OS will find the layout in the layout -sw600dp directory
        */
        if (frameLayout == null) { // start Activity when app runs on a phone
            Intent intent = new Intent(MainActivity.this, DetailActivity.class)
                    .putExtra(DetailFragment.MOVIE_DATA, modelData);
            startActivity(intent);
        } else { // start Activity when app runs on a tablet
            Bundle arguments = new Bundle();

            arguments.putParcelable(DetailFragment.MOVIE_DATA,
                    modelData);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(arguments);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, detailFragment);
            fragmentTransaction.commit();
        }
    }
}