package com.paularanas.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, MovieAsyncTask.PassDataInterface {
    private GridView gridView;
    private ArrayList<Movie> movies;
    private MovieAsyncTask movieTask = null;
    private static final String MOVIES_KEY = "saved_movieArrayList";
    private static final String LOG_TAG = "Error: ";
    private String currentSortingOrder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //context to delegate for PassDataInterface
        MovieAsyncTask.delegate = this;
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);
        // set sort by preferences
        SharedPreferences prefs = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sort_by", getString(R.string.sort_by_popularity_value));
        if (savedInstanceState != null) {
            //retrieve data and populate the adapter
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
        } else {
            updateMovies(currentSortingOrder);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Pass parcelable movie object to DetailsActivity to display relevant information
        // for specific poster that is clicked
        Movie movie = (Movie) parent.getAdapter().getItem(position);
        Intent passMovieIntent = new Intent(getApplicationContext(), DetailsActivity.class);
        passMovieIntent.putExtra("movieData", movie);
        startActivity(passMovieIntent);
    }

    @Override
    public void passReturnedData(ArrayList<Movie> movies) {
        //returns data from MovieAsyncTask
        MoviePosterAdapter adapter = new MoviePosterAdapter(this, movies);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }


    public void updateMovies(String sortParam) {
        //calls API with new sort order params
        MovieAsyncTask task = new MovieAsyncTask(this);
        task.execute(sortParam);
    }

    @Override
    public void onResume() {
        super.onResume();
        //if network is available and sorting order has been changed, update data
        if (!isNetworkAvailable()) {
            Log.e(LOG_TAG, "Network is unavailable");
            Toast networkUnavailableMessage = null;


            if (networkUnavailableMessage != null) {
                networkUnavailableMessage.cancel();
            } else {
                CharSequence text = getString(R.string.network_unavailable_message);
                networkUnavailableMessage = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                networkUnavailableMessage.show();
            }
        } else {
            String sortingOrderValue = Utility.getSortingOrderPreference(this);

            if (sortingOrderValue != null && !sortingOrderValue.equals(currentSortingOrder)) {
                Log.d(LOG_TAG, "Updating movies via API call");
                currentSortingOrder = sortingOrderValue;
                updateMovies(currentSortingOrder);

            }

        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save movie data
        outState.putParcelableArrayList("MOVIES_KEY", movies);
        //save sort order
        outState.putString("current_sort_order", currentSortingOrder);
    }

    // check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;

    }
}

class MoviePosterAdapter extends BaseAdapter {
    private Context context;
    ArrayList<Movie> movieList;
    ImageView poster;


    public MoviePosterAdapter(Context c, ArrayList<Movie> movie) {

        context = c;
        movieList = movie;

    }

    @Override
    public int getCount() {
        return movieList.size();

    }

    @Override
    public Object getItem(int position) {

        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridBox = convertView;
        MovieViewHolder holder;
        ImageView poster;


        if (gridBox == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridBox = inflater.inflate(R.layout.single_poster, parent, false);
            holder = new MovieViewHolder(gridBox);
            gridBox.setTag(holder);
        } else {
            // recycle, get Views from MovieViewHolder class
            holder = (MovieViewHolder) gridBox.getTag();
        }
        //get imageview from holder and data at specific position
        poster = holder.imageView;
        Movie temp = movieList.get(position);

        // for movies without provided film posters, set title text below "No Poster Available" image
        TextView title = holder.textView;
        if (temp.getPosterLastPathSegment().equals("null")) {
            title.setVisibility(View.VISIBLE);
            title.setText(temp.getOriginalTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        //load different size error images depending on orientation
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Picasso.with(context).load(temp.getPosterPath())
                    .noPlaceholder().error(R.drawable.movies_placeholder).into(poster);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Picasso.with(context).load(temp.getPosterPath())
                    .noPlaceholder().error(R.drawable.movies_placeholder_land).into(poster);
        }

        return gridBox;
    }


    static class MovieViewHolder {
        ImageView imageView;
        TextView textView;

        MovieViewHolder(View v) {
            imageView = (ImageView) v.findViewById(R.id.imageView);
            textView = (TextView) v.findViewById(R.id.text_poster_title);

        }
    }
}

















