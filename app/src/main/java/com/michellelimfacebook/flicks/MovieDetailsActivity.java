package com.michellelimfacebook.flicks;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    int placeholderId;
    // the view objects
    @BindView (R.id.tvTitle) TextView tvTitle;
    @BindView (R.id.tvOverview) TextView tvOverview;
    @BindView (R.id.rbVoteAverage) RatingBar rbVoteAverage;
    @BindView (R.id.tvReleaseDate) TextView tvReleaseDate;
    @Nullable @BindView (R.id.ivTrailerImage) ImageView ivTrailerImage;
    String videokey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);
        client = new AsyncHttpClient();
        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));
        Log.d("MovieDetailsActivity", String.format("Showing the image url '%s'", movie.getImageUrl()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        //determine the current orientation
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        //build url for poster image
        String imageUrl = null;

        if (isPortrait) {
            imageUrl = movie.getImageUrl();
        } else {
            //load the portrait image
            imageUrl = movie.getImageUrl_portrait();
        }
        int placeholderId = isPortrait ? R.drawable.flicks_backdrop_placeholder : R.drawable.flicks_movie_placeholder;

        //load image using glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(ivTrailerImage);


        // vote average is a scale of 10, convert to a scale of 5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
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
                    JSONArray results = response.getJSONArray("results");
                    JSONObject results_object = results.getJSONObject(0);
                    //if results exists, get first object
                    if (results_object!=null)
                    {
                        videokey = results_object.getString("key");
                        log.i("MovieTrailer", YOUTUBE_BASE_URL+videokey);
                    } else {
                        log.e("MovieTrailer", "Trailer link doesn't exist");
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

        //once the trailer is loaded, set up the listener
        setupImageViewListener();
    }

    private void setupImageViewListener(){
        Log.i("MovieDetailsActivity", "Setting up listener on movie details");

        ivTrailerImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               Log.i("MovieDetailsActivity","I'm clicked");
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                intent.putExtra("Video_Key", videokey);
                startActivityForResult(intent, 20);
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
