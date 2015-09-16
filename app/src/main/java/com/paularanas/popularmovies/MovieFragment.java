package com.paularanas.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import static com.paularanas.popularmovies.MovieContract.FavoriteMovieTable;
import static com.paularanas.popularmovies.MovieContract.URI_FAVORITES;

/**
 * Created by Paul Aranas on 8/18/2015.
 */

public class MovieFragment extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, OnFinishedTask, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "Error: ";
    private static final int CURSOR_LOADER = 1;
    private String currentSortingOrder = null;
    private ArrayList<? extends Parcelable> movieList = new ArrayList<>();
    private GridView grid;
    private Context context;
    private MoviePosterAdapter adapter;
    private int index;
    private OnMovieSelectedListener listener;
    private int mActivatedPos = 0;
    MoviePosterAdapter offlineAdapter;
    protected View gridBox;

    public MovieFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnMovieSelectedListener) {
            listener = (OnMovieSelectedListener) activity;
        } else {
            throw new ClassCastException(
                    activity.toString()
                            + " it is necessary to implement MovieFragment.OnMovieSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //delegate from AsyncTask
        MovieAsyncTask.delegate = this;
        setRetainInstance(true);
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("scrollPosition");
            grid.smoothScrollToPosition(index);
            currentSortingOrder = savedInstanceState.getString("current_sort_order");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_fragment, container, false);
        context = getActivity();
        // set sort by preferences
        grid = (GridView) view.findViewById(R.id.gridView);
        Utility.initializeSortPreference(context);
        currentSortingOrder = Utility.getSortingOrderPreference(context);


        if (savedInstanceState == null && isNetworkAvailable() && !currentSortingOrder.equals("favorites")) {

            updateMovies(currentSortingOrder);
        }
        else if (!isNetworkAvailable()) {

            Log.e(LOG_TAG, "Network is unavailable");
            Utility.initializeOffLineFavSortPref(context);
            getLoaderManager().initLoader(CURSOR_LOADER, null, this);
            offlineAdapter = new MoviePosterAdapter(context);

            Boolean isEmpty = checkEmptyTable();
            if (isEmpty) {

                FavoritesEmptyFragment favoritesEmptyFragment = new FavoritesEmptyFragment();
                favoritesEmptyFragment.show(getFragmentManager(), "FavEmptyDialog");

            }
            Toast networkUnavailableMessage = null;
            CharSequence text = getString(R.string.network_unavailable_message);
            networkUnavailableMessage = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
            networkUnavailableMessage.show();
            networkUnavailableMessage.cancel();
        }
        if (isNetworkAvailable() && savedInstanceState != null && !currentSortingOrder.equals("favorites")) {
            // retrieve data and populate the adapter
            movieList = savedInstanceState.getParcelableArrayList("MOVIES_KEY");
            grid.setAdapter(adapter);

        }
        grid.setOnItemClickListener(this);
        grid.setOnItemSelectedListener(this);

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore previously activated item position.
        if (savedInstanceState != null && MainActivity.sTwoPane && savedInstanceState.containsKey("ACTIVATED_POS")) {
            setActivatedPosition(savedInstanceState.getInt("ACTIVATED_POS", mActivatedPos));
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
            networkUnavailableMessage.cancel();
        } else {
            String sortingOrderValue = Utility.getSortingOrderPreference(getActivity());
            if (sortingOrderValue != null && !sortingOrderValue.equals(currentSortingOrder) && !sortingOrderValue.equals("favorites")) {

                updateMovies(sortingOrderValue);

            }
            if (sortingOrderValue != null && isNetworkAvailable() && sortingOrderValue.equals("favorites")) {
                getLoaderManager().initLoader(CURSOR_LOADER, null, this);
               // Boolean isEmpty = checkEmptyTable();
                if (checkEmptyTable()) {
                    FavoritesEmptyFragment favoritesEmptyFragment = new FavoritesEmptyFragment();
                    favoritesEmptyFragment.show(getFragmentManager(), "FavEmptyDialog");
                    Utility.initializeSortPreference(getActivity());

                }


            }

            currentSortingOrder = sortingOrderValue;
        }
    }


    //fetch data through AsyncTask

    private void updateMovies(String sortParam) {
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
        if (MainActivity.sTwoPane) {
            outState.putInt("ACTIVATED_POS", mActivatedPos);
        }
        super.onSaveInstanceState(outState);
    }

    // check if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

    }

    private void setActivatedPosition(int position) {
        mActivatedPos = position;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Pass parcelable movie object to DetailsActivityFragment via MainActivity
        // to display relevant information for specific poster that is clicked

        Parcelable movie = (Parcelable) parent.getAdapter().getItem(position);
        listener.onMovieSelected(movie);
    }

    //receive movie data in ArrayList from MovieAsyncTask
    //set adapter


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CURSOR_LOADER:
                return new CursorLoader(getActivity(), URI_FAVORITES, null,
                        null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        ArrayList<FavoriteMovie> favoriteListTemp = new ArrayList<>();

        if (data.moveToFirst()) {
            do {

                int identifications = data.getInt(data
                        .getColumnIndex(FavoriteMovieTable.MOVIE_ID));
                String titles = data.getString(data.getColumnIndex(FavoriteMovieTable.TITLE));
                String overviews = data.getString(data.getColumnIndex(FavoriteMovieTable.OVERVIEW));
                String releaseDates = data.getString(data.getColumnIndex(FavoriteMovieTable.RELEASE_DATE));
                String filmPosters = data.getString(data.getColumnIndex(FavoriteMovieTable.POSTER_PATH));
                String backdrops = data.getString(data.getColumnIndex(FavoriteMovieTable.BACKDROP));
                double voteAverages = data.getDouble(data.getColumnIndex(FavoriteMovieTable.VOTE_AVERAGE));
                String lastPaths = data.getString(data.getColumnIndex(FavoriteMovieTable.POSTER_LAST_PATH));
                String trailerNames = data.getString(data.getColumnIndex(FavoriteMovieTable.TRAILERS_NAMES));
                String trailerSizes = data.getString(data.getColumnIndex(FavoriteMovieTable.TRAILERS_SIZES));
                String trailerSources = data.getString(data.getColumnIndex(FavoriteMovieTable.TRAILERS_SOURCES));
                String reviewIds = data.getString(data.getColumnIndex(FavoriteMovieTable.REVIEWS_ID));
                String reviewAuthors = data.getString(data.getColumnIndex(FavoriteMovieTable.REVIEWS_AUTHOR));
                String reviewContent = data.getString(data.getColumnIndex(FavoriteMovieTable.REVIEWS_CONTENT));

                favoriteListTemp.add(new FavoriteMovie(identifications, titles, overviews, releaseDates, filmPosters,
                        backdrops, voteAverages, lastPaths, trailerNames, trailerSizes,
                        trailerSources, reviewIds, reviewAuthors, reviewContent));

            }
            while (data.moveToNext());
            movieList = favoriteListTemp;

            if (isNetworkAvailable()) {
                grid.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            if (!isNetworkAvailable()) {
                grid.setAdapter(offlineAdapter);
            }

        }
    }


    private Boolean checkEmptyTable() {
        boolean empty = true;
        MovieDatabaseHelper helper = new MovieDatabaseHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, MovieContract.FavoriteMovieTable.TABLE_NAME);
        if (count == 0) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    //receive movie data in ArrayList from MovieAsyncTask
    //set adapter
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Parcelable movie = (Parcelable) parent.getAdapter().getItem(position);
        listener.onMovieSelected(movie);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void passData(ArrayList<Movie> movies) {
        movieList = movies;
        adapter = new MoviePosterAdapter(context);
        grid.setAdapter(adapter);

        if (MainActivity.sTwoPane) {
            grid.post(new Runnable() {

                @Override
                public void run() {
                    grid.setSelection(0);
                    onItemSelected(grid, gridBox, 0, 0);
                }
            });
        }

    }


    public interface OnMovieSelectedListener {

        void onMovieSelected(Parcelable obj);

    }

    public void setActivateOnMovieClick(boolean activateOnItemClick) {
        // CHOICE_MODE_SINGLE, movies in activated state on touch
        grid.setChoiceMode(
                activateOnItemClick ? GridView.CHOICE_MODE_SINGLE
                        : GridView.CHOICE_MODE_NONE);

    }


    class MoviePosterAdapter extends BaseAdapter {
        private final Context context;

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
            Movie temp = null;
            FavoriteMovie temp1 = null;
            if (movieList.get(position) instanceof Movie) {
                temp = (Movie) movieList.get(position);
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


            } else if (movieList.get(position) instanceof FavoriteMovie) {
                temp1 = (FavoriteMovie) movieList.get(position);

                // for movies without provided film posters, set title text below "No Poster Available" image
                TextView title = holder.textView;
                if (temp1.getFavoritePosterLastPathSegment().equals("null")) {
                    title.setVisibility(View.VISIBLE);
                    title.setText(temp1.getFavoriteTitle());
                } else {
                    title.setVisibility(View.GONE);
                }

                //load different size error images depending on orientation

                int orientation = context.getResources().getConfiguration().orientation;

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Picasso.with(context).load(temp1.getFavoritePosterPath())
                            .noPlaceholder().error(R.drawable.movies_placeholder).into(poster);
                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Picasso.with(context).load(temp1.getFavoritePosterPath())
                            .noPlaceholder().error(R.drawable.movies_placeholder_land).into(poster);
                }

            }


            return gridBox;

        }
    }

    static class MovieViewHolder {
        final ImageView imageView;
        final TextView textView;

        MovieViewHolder(View v) {
            imageView = (ImageView) v.findViewById(R.id.imageView);
            textView = (TextView) v.findViewById(R.id.text_poster_title);
        }

    }
}
