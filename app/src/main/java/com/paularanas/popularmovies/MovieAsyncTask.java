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
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Paul Aranas on 8/19/2015.
 */
    public class MovieAsyncTask extends AsyncTask<String, Void, JSONObject> {
        static OnFinishedTask delegate;
        Context context;
        private static final String LOG_TAG = "Error: ";

        public MovieAsyncTask(Context c) {

            context = c;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            //connect to the network
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            String api_key = KeyConstants.API_KEY;
            String baseUrl = KeyConstants.BASE_URL;
            JSONObject jObject = null;
            URL myURL;
            String url;

            try {

                url = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("sort_by", params[0])
                        .appendQueryParameter("api_key", api_key)
                        .build().toString();


                myURL = new URL(url);


                connection = (HttpURLConnection) myURL.openConnection();
                connection.setRequestMethod("GET");
                inputStream = connection.getInputStream();
                String jsonFeed = processJsonFeed(inputStream);
                jObject = stringToJsonObject(jsonFeed);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Malformed URL");
            } catch (IOException e) {
                Log.e(LOG_TAG, "IO connection error");

            } finally

            {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Unable to close input stream");
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
                Log.d("PFFFJJAA", movieJson.toString());
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Json parsing error");
            }

            ArrayList<Movie> movieList = Movie.fromJson(movieJson);
            delegate.passData(movieList);

        }


        public String processJsonFeed(InputStream stream) {
            //process json feed to a string
            StringBuilder sb = new StringBuilder();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
            String line;

            try {
                while ((line = buffer.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "IO error while creating StringBuilder object");
            }
            return sb.toString();
        }

        public JSONObject stringToJsonObject(String str) {
            //string to json object
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(str);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Json parsing error");
            }
            return jObject;

        }

    }


