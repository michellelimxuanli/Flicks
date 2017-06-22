package com.michellelimfacebook.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michellelim on 6/22/17.
 */

public class Config {
    //the base url for loading images
    String imageBaseUrl;
    //the poster size to use when fetching images, part of the url
    String posterSize;

    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        //get the image base Url
        imageBaseUrl = images.getString("secure_base_url");
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        // use option at index 3 or w342 as a fallback
        posterSize = posterSizeOptions.optString(3, "w342");
    }

    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }
}
