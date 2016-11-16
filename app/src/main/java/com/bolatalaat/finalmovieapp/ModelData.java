package com.bolatalaat.finalmovieapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Boal on 9/22/2016.
 */
public class ModelData implements Parcelable {

    private int id;
    private int rating;
    private String title;
    private String image;
    private String image2;
    private String overview;
    private String date;
    private String jsonObject;


    public ModelData(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.title = movie.getString("original_title");
        this.image = movie.getString("poster_path");
        this.image2 = movie.getString("backdrop_path");
        this.overview = movie.getString("overview");
        this.rating = movie.getInt("vote_average");
        this.date = movie.getString("release_date");
        this.jsonObject = movie.toString();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getImage2() {
        return image2;
    }

    public String getOverview() {
        return overview;
    }

    public int getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(image2);
        dest.writeString(overview);
        dest.writeInt(rating);
        dest.writeString(date);
        dest.writeString(jsonObject);
    }


    public static final Creator<ModelData> CREATOR
            = new Creator<ModelData>() {
        public ModelData createFromParcel(Parcel in) {
            return new ModelData(in);
        }

        public ModelData[] newArray(int size) {
            return new ModelData[size];
        }
    };

    private ModelData(Parcel in) {
        id = in.readInt();
        title = in.readString();
        image = in.readString();
        image2 = in.readString();
        overview = in.readString();
        rating = in.readInt();
        date = in.readString();
        jsonObject = in.readString();
    }

    public String getJsonObject() {
        return jsonObject;
    }
}