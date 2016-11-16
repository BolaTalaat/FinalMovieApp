package com.bolatalaat.finalmovieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boal on 9/22/2016.
 */
public class MovieFragment extends Fragment implements DataTask.Callback {

    private String SORT_SETTING_KEY = "sort_setting";
    private final String FAVOURITES = "fav";
    private final String POPULARITY_DESC = "popular";
    private final String RATING_DESC = "top_rated";
    private final String MOVIES_KEY = "movies";
    private GridView mGridView;
    private TextView progressTV;
    private ProgressBar progressBar;
    private LinearLayout progressLayout;
    private GridAdapter mGridAdapter;
    private String sortBy = POPULARITY_DESC;
    private ArrayList<ModelData> moviesList = null;
    private SettingsActivity settings;
    DataTask dataTask;

    String TAG = "MovieFragment";

    Callback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        settings = new SettingsActivity(getActivity());
        Log.d(TAG, "Create");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On Create View");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        callback = (Callback) getActivity();
        mGridView = (GridView) view.findViewById(R.id.gridview_movies);

        progressLayout = (LinearLayout) view.findViewById(R.id.progress);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressTV = (TextView) view.findViewById(R.id.progressTV);

        checkInstanceState(savedInstanceState);
        return view;
    }

    private void initAdapter() {
        mGridAdapter = new GridAdapter(getActivity());
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelData modelData = mGridAdapter.getItem(position);
                if (callback != null)
                    callback.actionCallback(modelData);
            }
        });
    }

    private void checkInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.d(TAG, "Instance was saved..");
            progressLayout.setVisibility(View.GONE);
            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                moviesList = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                for (ModelData modelData : moviesList) {
                    mGridAdapter.add(modelData);
                }
            } else {
                initAdapter();
                Log.d(TAG, "Update First Time..");
                updateMovies(sortBy);
            }
        } else {
            initAdapter();
            Log.d(TAG, "Update First Time..");
            sortBy = settings.getSortBy();
            if (!sortBy.equalsIgnoreCase(FAVOURITES))
                updateMovies(sortBy);
            else {
                loadFavourites();
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        checkInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (moviesList != null) {
            outState.putParcelableArrayList(MOVIES_KEY, moviesList);
        }
        Log.d(TAG, "On Save Instance..");
        super.onSaveInstanceState(outState);
    }

    private void updateMovies(String sort_by) {
        Log.d(TAG, "Loading Movies in order to " + sort_by);
        if (IsConneted.isConnected(getActivity())) {
            dataTask = new DataTask(MovieFragment.this);
            dataTask.execute(sort_by);
        } else {
            progressBar.setVisibility(View.GONE);
            progressTV.setText("Check Internet Connection");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_show_favourites = menu.findItem(R.id.action_show_favourites);
        sortBy = settings.getSortBy();
        if (sortBy.equalsIgnoreCase(RATING_DESC))
            action_sort_by_rating.setChecked(true);
        else if (sortBy.equalsIgnoreCase(POPULARITY_DESC))
            action_sort_by_popularity.setChecked(true);
        else
            action_show_favourites.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (dataTask != null)
            dataTask.cancel(true);
        if (item.isChecked())
            return true;
        item.setChecked(true);
        switch (id) {
            case R.id.action_sort_by_popularity:
                sortBy = POPULARITY_DESC;
                updateMovies(sortBy);
                settings.setSortBy(sortBy);
                return true;
            case R.id.action_sort_by_rating:
                sortBy = RATING_DESC;
                settings.setSortBy(sortBy);
                updateMovies(sortBy);
                return true;
            case R.id.action_show_favourites:
                sortBy = FAVOURITES;
                settings.setSortBy(sortBy);
                loadFavourites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFavourites() {
        DataBaseSource dataBaseSource = new DataBaseSource(getActivity());
        dataBaseSource.open();
        moviesList = (ArrayList<ModelData>) dataBaseSource.getMovies();
        Log.d(TAG, "Favourites Size " + moviesList.size());
        if (moviesList == null || moviesList.isEmpty()) {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            progressTV.setText("No Favourites..");
            mGridView.setVisibility(View.GONE);
        } else {
            progressLayout.setVisibility(View.GONE);
            mGridAdapter.clear();
            for (ModelData modelData : moviesList)
                mGridAdapter.add(modelData);
            mGridAdapter.notifyDataSetChanged();
        }
        dataBaseSource.close();
    }

    @Override
    public void preExecute() {
        mGridView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressTV.setVisibility(View.VISIBLE);
        progressTV.setText("Loading Movies \n wait a moment...");
    }

    @Override
    public void postExecute(List<ModelData> modelDataList) {
        if (mGridAdapter != null) {
            mGridAdapter.clear();
            if (modelDataList != null)
                for (ModelData modelData : modelDataList) {
                    mGridAdapter.add(modelData);
                }
        }
        mGridView.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
        moviesList = new ArrayList<>();
        if (modelDataList != null)
            moviesList.addAll(modelDataList);
        if (MainActivity.frameLayout != null)
            if (callback != null && !moviesList.isEmpty())
                callback.actionCallback(moviesList.get(0));
    }
}



class DataTask extends AsyncTask<String, Void, List<ModelData>> {
    Callback callback;
    private final String TAG = "DataTask";

    public interface Callback {
        public void preExecute();

        public void postExecute(List<ModelData> modelDataList);
    }

    public DataTask(Callback callback) {
        this.callback = callback;
    }

    private final String LOG_TAG = DataTask.class.getSimpleName();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.callback != null) {
            callback.preExecute();
        }

    }

    @Override
    protected List<ModelData> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)//
                    .build();
            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getMoviesDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private List<ModelData> getMoviesDataFromJson(String jsonStr) throws JSONException {
        if (jsonStr.isEmpty() || jsonStr == null)
            return null;
        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");
        List<ModelData> results = new ArrayList<>();
        if (movieArray != null && !jsonStr.isEmpty())
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                ModelData modelDataModel = new ModelData(movie);
                results.add(modelDataModel);
            }
        return results;
    }

    @Override
    protected void onPostExecute(List<ModelData> movies) {
        // call back to return movies data to MovieFragment
        if (callback != null) {
            callback.postExecute(movies);
        }
    }
}