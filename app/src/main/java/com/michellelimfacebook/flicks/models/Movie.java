package com.michellelimfacebook.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by michellelim on 6/21/17.
 */

@Parcel
public class Movie {
    //values from API
    String title;
    String overview;
    String posterPath;
    String backdropPath;
    Double voteAverage;
    Integer id;
    String releaseDate;
    String imageUrl;
    String imageUrl_portrait;

    public Movie(){}

    //initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");
        id = object.getInt("id");
//        voteCount = object.getInt("vote_count");
        releaseDate = object.getString("release_date");
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getId() {
        return id;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageUrl_portrait(String imageUrl_landscape) {
        this.imageUrl_portrait = imageUrl_landscape;
    }

    public String getImageUrl_portrait() {
        return imageUrl_portrait;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
