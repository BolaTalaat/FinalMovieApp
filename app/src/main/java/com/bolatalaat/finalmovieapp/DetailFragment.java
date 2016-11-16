package com.bolatalaat.finalmovieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boal on 9/22/2016.
 */
public class DetailFragment extends Fragment implements TrailersTask.TrailersCallback, ReviewsTask.ReviewCallback {

    public static final String MOVIE_DATA = "DETAIL_MOVIE";
    public static final String TAG = "Detail Fragment";
    List<ModelTrailer> modelTrailerList;
    private ModelData modelData;
    private ImageView mImageView;
    private TextView mTitleView, mOverviewView, releaseDateTV;
    private TextView voteAverageView;

    ListView trailersListV, reviewsListV;
    ImageButton favBtn;

    private boolean isFavorite(int movie_id) {
        DataBaseSource dataBaseSource = new DataBaseSource(getActivity());
        dataBaseSource.open();
        return dataBaseSource.isFavorite(movie_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        Bundle arguments = getArguments();
        if (arguments == null)
            return;

        modelData = arguments.getParcelable(DetailFragment.MOVIE_DATA);

        trailersListV = (ListView) rootView.findViewById(R.id.trailersListV);
        reviewsListV = (ListView) rootView.findViewById(R.id.reviewsListV);

        mImageView = (ImageView) rootView.findViewById(R.id.moviePoster);
        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.overview);
        releaseDateTV = (TextView) rootView.findViewById(R.id.date);
        voteAverageView = (TextView) rootView.findViewById(R.id.vote_average);

        favBtn = (ImageButton) rootView.findViewById(R.id.favBtn);
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite(modelData.getId()))
                    fav();
                else {
                    deleteMovie();
                }
            }
        });
        trailersListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPlayingIntent(modelTrailerList.get(position));
            }
        });
        if (isFavorite(modelData.getId())) {
            favBtn.setImageResource(R.mipmap.ic_remove_favourites);
        }

        if (IsConneted.isConnected(getActivity()))
            new TrailersTask(DetailFragment.this).execute(modelData.getId() + "");
        new ReviewsTask(DetailFragment.this).execute(modelData.getId() + "");
        try {
            String image_url = "http://image.tmdb.org/t/p/w342" + modelData.getImage2();
            Picasso.with(getActivity()).load(image_url).into(mImageView);
            mTitleView.setText(modelData.getTitle());
            mOverviewView.setText(modelData.getOverview());
            String movie_date = modelData.getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = DateUtils.formatDateTime(getActivity(),
                    formatter.parse(movie_date).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            releaseDateTV.setText(date);
            voteAverageView.setText(Integer.toString(modelData.getRating()));
        } catch (Exception e) {
            Log.e(TAG, "Error " + e);
        }
    }

    @Override
    public void preExecuteTrailers() {

    }

    @Override
    public void preExecuteReviews() {

    }

    @Override
    public void postExecuteReviews(List<ModelReview> modelReviewList) {
        try {
            String[] reviews = new String[modelReviewList.size()];
            Log.d(TAG, "Reviews Count " + modelReviewList.size());
            for (int i = 0; i < modelReviewList.size(); i++) {
                reviews[i] = modelReviewList.get(i).getContent();
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, reviews);
            reviewsListV.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Exception , " + e);
        }
    }

    @Override
    public void postExecuteTrailers(List<ModelTrailer> modelTrailerList) {
        try {
            DetailFragment.this.modelTrailerList = modelTrailerList;
            String[] trailers = new String[modelTrailerList.size()];
            Log.d(TAG, "Trailers Count " + modelTrailerList.size());
            for (int i = 0; i < modelTrailerList.size(); i++) {
                trailers[i] = modelTrailerList.get(i).getName();
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, trailers);
            trailersListV.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Exception , " + e);
        }
    }

    private void startPlayingIntent(ModelTrailer modelTrailer) {
        String url = "https://www.youtube.com/watch?v=" + modelTrailer.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void fav() {
        DataBaseSource dataBaseSource = new DataBaseSource(getActivity());
        dataBaseSource.open();
        long id = dataBaseSource.addMovie(modelData);
        if (id != -1) {
            Toast.makeText(getActivity(), "Added to Favourites", Toast.LENGTH_SHORT).show();
            favBtn.setImageResource(R.mipmap.ic_remove_favourites);
        }
    }

    private void deleteMovie() {
        DataBaseSource dataBaseSource = new DataBaseSource(getActivity());
        dataBaseSource.open();
        int id = dataBaseSource.deleteMovie(modelData.getId());
        if (id != 0) {
            Toast.makeText(getActivity(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
            favBtn.setImageResource(R.mipmap.ic_favourites);
        }

    }
}


class TrailersTask extends AsyncTask<String, Void, List<ModelTrailer>> {

    private final String TAG = "TrailersTask";
    TrailersCallback trailersCallback;

    public TrailersTask(TrailersCallback trailersCallback) {
        this.trailersCallback = trailersCallback;
    }

    public interface TrailersCallback {
        public void preExecuteTrailers();

        public void postExecuteTrailers(List<ModelTrailer> modelTrailerList);

    }

    private List<ModelTrailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
        JSONObject trailerJson = new JSONObject(jsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray("results");
        List<ModelTrailer> results = new ArrayList<>();
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailerObject = trailerArray.getJSONObject(i);
            results.add(new ModelTrailer(trailerObject));
        }
        return results;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (trailersCallback != null)
            trailersCallback.preExecuteTrailers();
    }

    @Override
    protected List<ModelTrailer> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(TAG, url.toString());
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
            Log.e(TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getTrailersDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(TAG, "Error in Json Parsing " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<ModelTrailer> modelTrailers) {
        // call back to return modelTrailers data to MovieFragment
        if (this.trailersCallback != null) {
            trailersCallback.postExecuteTrailers(modelTrailers);
        }
    }
}

class ReviewsTask extends AsyncTask<String, Void, List<ModelReview>> {

    private final String TAG = "ReviewsTask";
    ReviewCallback reviewCallback;


    public ReviewsTask(ReviewCallback reviewCallback) {
        this.reviewCallback = reviewCallback;
    }

    public interface ReviewCallback {
        public void preExecuteReviews();

        public void postExecuteReviews(List<ModelReview> trailerList);
    }

    private List<ModelReview> getReviewsDataFromJson(String jsonStr) throws JSONException {
        JSONObject reviewJson = new JSONObject(jsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray("results");

        List<ModelReview> results = new ArrayList<>();

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject review = reviewArray.getJSONObject(i);
            results.add(new ModelReview(review));
        }

        return results;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (reviewCallback != null)
            reviewCallback.preExecuteReviews();
        Log.d(TAG, "PreExecute Reviews");
    }

    @Override
    protected List<ModelReview> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(TAG, url.toString());
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
            Log.e(TAG, "Input-Output Exception ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream ", e);
                }
            }
        }

        try {
            return getReviewsDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(TAG, "Error in Json Parsing " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<ModelReview> modelReviews) {
        // call back to return modelReviews data to MovieFragment
        if (this.reviewCallback != null) {
            reviewCallback.postExecuteReviews(modelReviews);
        }
    }
}