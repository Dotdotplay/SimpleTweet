package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    EditText etcompose;
    Button btnTweet;
    TextView etValue;
    TwitterClient client;
    public static final int MAX_TWEET_LENGTH = 140;
    public static final String  TAG = "ComposeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        etcompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        // add a listenter on the button to add logic on publishing text to twitter api
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String tweetContent = etcompose.getText().toString();
                if (tweetContent.isEmpty()) {
                   Toast.makeText(ComposeActivity.this,"Sorry your tweet cannot be empty", Toast.LENGTH_LONG).show();
                   return;
                }

                if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this,"Sorry your tweet is too long",Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(ComposeActivity.this,tweetContent,Toast.LENGTH_LONG).show();


                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG,"On Succcess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG,"Ppublish tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,intent);
                            finish(); //closes activity
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"On faliure to publish tweet",throwable);
                    }
                });
            }
        });

    }
}
