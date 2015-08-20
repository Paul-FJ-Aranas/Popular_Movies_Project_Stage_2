package com.paularanas.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * Created by Paul Aranas on 8/18/2015.
 */
public class MovieFragment extends Fragment implements AdapterView.OnItemClickListener, OnFinishedTask {
    private static final String LOG_TAG = "Error: ";
    private String currentSortingOrder = null;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private GridView grid;
    private Context context;
    private MoviePosterAdapter adapter;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //delegate from AsyncTask
        MovieAsyncTask.delegate = this;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_fragment, container, false);
        context = getActivity();
        // set sort by preferences
        grid = (GridView) view.findViewById(R.id.gridView);
        Utility.initializeSortPreference(context);
        currentSortingOrder = Utility.getSortingOrderPreference(context);

        if (savedInstanceState == null) {
            updateMovies(currentSortingOrder);

        } else {
            // retrieve data and populate the adapter
            movieList = savedInstanceState.getParcelableArrayList("MOVIES_KEY");
            grid.deferNotifyDataSetChanged();
            grid.invalidateViews();
            grid.setAdapter(adapter);
        }

        grid.setOnItemClickListener(this);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("scrollPosition");
            grid.smoothScrollToPosition(index);
            currentSortingOrder = savedInstanceState.getString("current_sort_order");

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //if network is available and sorting order has been changed, update data
        if (!isNetworkAvailable()) {
            Log.e(LOG_TAG, "Network is unavailable");
            Toast networkUnavailableMessage = null;

            CharSequence text = getString(R.string.network_unavailable_message);
            networkUnavailableMessage = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
            networkUnavailableMessage.show();
        } else {
            String sortingOrderValue = Utility.getSortingOrderPreference(getActivity());
            if (sortingOrderValue != null && !sortingOrderValue.equals(currentSortingOrder)) {
                updateMovies(sortingOrderValue);
                currentSortingOrder = sortingOrderValue;
            }
        }
    }

    //fetch data through AsyncTask
    public void updateMovies(String sortParam) {
        //calls API with new sort order params via TaskFragment
        MovieAsyncTask task = new MovieAsyncTask(context);
        task.execute(sortParam);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save movie data
        outState.putParcelableArrayList("MOVIES_KEY", movieList);
        index = grid.getFirstVisiblePosition();
        outState.putInt("scrollPosition", index);
        outState.putString("current_sort_order", currentSortingOrder);
        super.onSaveInstanceState(outState);
    }

    // check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Pass parcelable movie object to DetailsActivity to display relevant information
        // for specific poster that is clicked
        Movie movie = (Movie) parent.getAdapter().getItem(position);
        Intent passMovieIntent = new Intent(getActivity(), DetailsActivity.class);
        passMovieIntent.putExtra("movieData", movie);
        startActivity(passMovieIntent);
    }

    //receive movie data in ArrayList from MovieAsyncTask
    //set adapter and OnClickListener
    @Override
    public void passData(ArrayList<Movie> movies) {
        movieList = movies;
        adapter = new MoviePosterAdapter(context);
        grid.setAdapter(adapter);
        // grid.setOnItemClickListener(this);
    }


class MoviePosterAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Movie> movies = new ArrayList<>();

    public MoviePosterAdapter(Context c) {
        context = c;
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



