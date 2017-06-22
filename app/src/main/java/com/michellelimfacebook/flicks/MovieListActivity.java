package com.michellelimfacebook.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.michellelimfacebook.flicks.models.Config;
import com.michellelimfacebook.flicks.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class MovieListActivity extends AppCompatActivity {

    // constants
    // base URL for API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // parameter name for API key
    public final static String API_KEY_PARAM = "api_key";
    //tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    AsyncHttpClient client;
    Config config;
    //the list of movies now playing
    ArrayList<Movie> movies;
    //the recycler view
    RecyclerView rvMovies;
    //the adapter wired to the recycler view
    MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        //initialize the client
        client = new AsyncHttpClient();
        //initialize the list of movies
        movies = new ArrayList<>();
        //initialize the adapter
        adapter = new MovieAdapter(movies);

        //resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        //get the configuration
        getConfiguration();
    }

    private void getConfiguration() {
        // create the url
        String url = API_BASE_URL + "/configuration";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s",
                            config.getImageBaseUrl(), config.getPosterSize()));
                    //pass config to adapter
                    adapter.setConfig(config);
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed getting configuration", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }


    //get the configuration from the API:
    private void getNowPlaying() {
        // create the url
        String url = API_BASE_URL + "/movie/now_playing";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    //iterate through result set
                    int n = results.length();
                    for (int i = 0; i < n; i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify the adapter that a row was added
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", n));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now playing movies", throwable, true);
            }
        });


    }

    //handle errors, log and alert the user
    private void logError(String message, Throwable error, boolean alertUser){
        // always log the error on our console
        log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            //show long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
