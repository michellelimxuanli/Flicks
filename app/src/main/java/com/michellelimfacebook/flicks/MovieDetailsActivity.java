package com.michellelimfacebook.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.michellelimfacebook.flicks.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;
import static com.michellelimfacebook.flicks.MovieListActivity.API_BASE_URL;
import static com.michellelimfacebook.flicks.MovieListActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends AppCompatActivity {

    // the movie to display
    Movie movie;
    AsyncHttpClient client;
    public final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    // the view objects
    @BindView (R.id.tvTitle) TextView tvTitle;
    @BindView (R.id.tvOverview) TextView tvOverview;
    @BindView (R.id.rbVoteAverage) RatingBar rbVoteAverage;
    @BindView (R.id.tvReleaseDate) TextView tvReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);
        client = new AsyncHttpClient();
        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is a scale of 10, convert to a scale of 5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
        tvReleaseDate.setText(movie.getReleaseDate());


        getTrailer();
    }

    private void getTrailer() {
        // get the Id
        String id = String.valueOf(movie.getId());
        // create the url
        String url = API_BASE_URL + "/movie/" + id + "/videos";

        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String videokey;
                    JSONArray results = response.getJSONArray("results");
                    JSONObject results_object = results.getJSONObject(0);
                    //if results exists, get first object
                    if (results_object!=null)
                    {
                        videokey = results_object.getString("key");
                        log.i("MovieTrailer", YOUTUBE_BASE_URL+videokey);
                    } else {
                        log.e("MovieTrailer", "Trailer link doesn't exist")
                    }
                } catch (JSONException e) {
                    logError("Failed to get youtube url", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from movie videos", throwable, true);
            }
        });


    }

    //handle errors, log and alert the user
    private void logError(String message, Throwable error, boolean alertUser){
        // always log the error on our console
        log.e("MovieTrailer", message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            //show long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

}
