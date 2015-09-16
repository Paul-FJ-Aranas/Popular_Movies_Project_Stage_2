package com.paularanas.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Paul Aranas on 8/19/2015.
 */
class  MovieAsyncTask extends AsyncTask<String, Void, JSONObject> {
    static OnFinishedTask delegate;
    private final Context context;
    private static final String LOG_TAG = "Error: ";
    private String jsonFeed = null;

    public MovieAsyncTask(Context c) {

        context = c;
    }

    private String processJsonFeed(InputStream stream) {
        //process json feed to a string
        StringBuilder sb = new StringBuilder();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
        String line;

        try {
            while ((line = buffer.readLine()) != null) {
                sb.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } return sb.toString();

    }

    private JSONObject stringToJsonObject(String str) {
        //string to json object
        JSONObject jObject = null;

        try {
            jObject = new JSONObject(str);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        //connect to the network
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        String api_key = KeyConstants.API_KEY;
        String baseUrl = KeyConstants.BASE_URL;
        JSONObject jObject = null;
        URL myURL = null;
        String url;

        try {

            url = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("sort_by", params[0])
                    .appendQueryParameter("api_key", api_key)
                    .build().toString();


            myURL = new URL(url);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

          try{  connection = (HttpURLConnection) myURL.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        }
        try {
            inputStream = connection.getInputStream();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            jsonFeed = processJsonFeed(inputStream);
            jObject = stringToJsonObject(jsonFeed);
        } catch (Exception e) {


        } finally

        {

            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception exc) {
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Unable to close input stream");
                    }
                }
            }
        }

        return jObject;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        /*extract json array from object pass to methods inside Movie class
        which hydrates Movie object and returns an array list of Movie objects
        Array list is passed via PassDataInterface to MainActivity */

        super.onPostExecute(obj);
        JSONArray movieJson = null;

        try {
            movieJson = obj.getJSONArray("results");
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        ArrayList<Movie> movieList = Movie.fromJson(movieJson);
        delegate.passData(movieList);
    }


    }

