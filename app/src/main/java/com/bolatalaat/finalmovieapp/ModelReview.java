package com.bolatalaat.finalmovieapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Boal on 9/22/2016.
 */
public class ModelReview {
    private String id;
    private String author;
    private String content;
    private String url;

    public ModelReview(JSONObject reviewObject) throws JSONException {
        this.id = reviewObject.getString("id");
        this.author = reviewObject.getString("author");
        this.content = reviewObject.getString("content");
        this.url = reviewObject.getString("url");
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }
}
