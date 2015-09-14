package com.paularanas.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "Error Log: ";
    private Context context;
    private String movieId = null;
    private Movie movie;
    private FavoriteMovie favMovie;
    private View view;
    private ArrayList<Trailer> movieTrailerList = new ArrayList<>();
    private ArrayList<Review> reviewList = new ArrayList<>();
    private String backdropPath;
    private String originalTitle;
    private String releaseDate;
    private double voteAverage;
    private String posterPath;
    private String posterLastPath;
    private String movieOverview;
    private TrailerReviewTask trailerReviewTask;
    String sortValue;
    RelativeLayout reviewLayout = null;
    RelativeLayout trailerLayout = null;
    TextView seeAllClickText;
    ImageView favoriteButton;
    android.support.v7.widget.ShareActionProvider mShareActionProvider;


    public DetailsActivityFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortValue = prefs.getString("sort_by", getActivity().getString(R.string.sort_by_default_value));

        //retrieve intent from MainActivity with Movie or FavoriteMovie object as extra
        Bundle arguments = getArguments();

        if (arguments.containsKey("MovieObject")) {
            movie = arguments.getParcelable("MovieObject");

            movieId = Integer.toString(movie.getId());
            posterLastPath = movie.getPosterLastPathSegment();
            dataToMovieViews();
        } else if (arguments.containsKey("FavMovieObject")) {

            favMovie = arguments.getParcelable("FavMovieObject");
            movieId = Integer.toString(favMovie.getFavoriteId());
            posterLastPath = favMovie.getFavoritePosterLastPathSegment();
            dataToFavoriteViews();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);
        context = getActivity();

        if (!isNetworkAvailable()) {
            Log.e(LOG_TAG, "Network is unavailable");
            Toast networkUnavailableMessage = null;

            CharSequence text = getString(R.string.network_unavailable_message);
            networkUnavailableMessage = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
            networkUnavailableMessage.show();
        } else {

            if (savedInstanceState == null && !sortValue.equals("favorites") && trailerReviewTask == null) {
                //fetch trailer and review data
                trailerReviewTask = (TrailerReviewTask) new TrailerReviewTask(this, favoriteButton).execute(movieId);

            }
        }


        return view;
    }

    public void dataToMovieViews() {
        backdropPath = movie.getBackdropPath();
        originalTitle = movie.getOriginalTitle();
        releaseDate = movie.getReleaseDate();
        voteAverage = movie.getVoteAverage();
        posterPath = movie.getPosterPath();
        movieOverview = movie.getOverview();
    }

    public void dataToFavoriteViews() {
        backdropPath = favMovie.getFavoriteBackdropPath();
        originalTitle = favMovie.getFavoriteTitle();
        releaseDate = favMovie.getFavoriteReleaseDate();
        voteAverage = favMovie.getFavoriteVoteAverage();
        posterPath = favMovie.getFavoritePosterPath();
        movieOverview = favMovie.getFavoriteOverview();
    }

    public void instantiateViews(View view) {
        //instantiate views when savedInstanceState is not null
        favoriteButton = (ImageView) view.findViewById(R.id.favoriteButton);
        ImageView backdrop = (ImageView) view.findViewById(R.id.backdrop);
        Picasso.with(context).load(backdropPath).into(backdrop);
        TextView titleText = (TextView) view.findViewById(R.id.textView_originalTitle);
        titleText.setText(originalTitle);
        TextView releaseDateText = (TextView) view.findViewById(R.id.textView_release_date);
        releaseDateText.setText(getString(R.string.release_date) + " " + releaseDate);
        TextView userRatingText = (TextView) view.findViewById(R.id.textView_user_rating);
        userRatingText.setText(getString(R.string.user_rating) + " " + voteAverage + "/10");
        ImageView posterThumbnail = (ImageView) view.findViewById(R.id.poster_thumbnail);
        Picasso.with(getActivity()).load(posterPath).error(R.drawable.movies_thumbnail_placeholder).into(posterThumbnail);
        TextView overview = (TextView) view.findViewById(R.id.textView_overview);

        if ("null".equals(movieOverview) || "No overview found.".equals(movieOverview) || "null".equals(movieOverview)) {
            overview.setText("");
        } else {
            overview.setText(movieOverview);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider =
        (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

               }


    public void createShareTrailerIntent(ArrayList <Trailer> list) {
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + list.get(0).getSource()));
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(trailerIntent);
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // retrieve saved data
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortValue = prefs.getString("sort_by", getActivity().getString(R.string.sort_by_default_value));
        if (savedInstanceState != null && !sortValue.equals("favorites")) {
            movieTrailerList = savedInstanceState.getParcelableArrayList("TRAILER_LIST");
            reviewList = savedInstanceState.getParcelableArrayList("REVIEW_LIST");
            instantiateViews(view);
            trailerCreator(movieTrailerList);
        }
        if (savedInstanceState != null && sortValue.equals("favorites")) {
            movieTrailerList = savedInstanceState.getParcelableArrayList("TRAILER_LIST");
            reviewList = savedInstanceState.getParcelableArrayList("REVIEW_LIST");
        }

        if (savedInstanceState != null) {
            trailerCreator(movieTrailerList);
            reviewCreator(reviewList);

        }
        if (savedInstanceState != null && sortValue.equals("favorites")) {
            trailerCreator(movieTrailerList);
            reviewCreator(reviewList);
        }
        instantiateViews(view);


    }

    //save data for configuration changes
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("TRAILER_LIST", movieTrailerList);
        outState.putParcelableArrayList("REVIEW_LIST", reviewList);
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

    //master-detail fragment instantiation
    public static DetailsActivityFragment newInstance(Parcelable obj) {
        DetailsActivityFragment fragment = new DetailsActivityFragment();
        Bundle args = new Bundle();
        if (obj instanceof Movie) {
            args.putParcelable("MovieObject", obj);
        } else if (obj instanceof FavoriteMovie) {
            args.putParcelable("FavMovieObject", obj);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void reviewCreator(ArrayList<Review> filmReviewList) {
        reviewList = filmReviewList;
        if (sortValue.equals("favorites")) {
            ArrayList<Review> reviewFavList = new ArrayList<>();
            String[] idsReviews = favMovie.getFavoriteReviewsId().split("\0");
            String[] authorReviews = favMovie.getFavoriteReviewsAuthor().split("\0");
            String[] contentReviews = favMovie.getFavoriteReviewsContent().split("\0");


            for (int i = 0; i < idsReviews.length; i++) {
                reviewFavList.add(new Review(idsReviews[i], authorReviews[i], contentReviews[i]));
            }

            reviewList = reviewFavList;
        }


        reviewLayout = (RelativeLayout) view.findViewById(R.id.review_container);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        seeAllClickText = new TextView(context);
        seeAllClickText.setText("See all");
        seeAllClickText.setTextColor(context.getResources().getColor(R.color.teal));
        seeAllClickText.setClickable(true);
        seeAllClickText.setId(2);


        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params2.setMargins(0, 5, 25, 2);
        reviewLayout.addView(seeAllClickText, params2);

        TextView firstReview = new TextView(context);
        if (reviewList.isEmpty()) {
            firstReview.setText("No reviews");
        } else {
            firstReview.setText(reviewList.get(0).getAuthor() + "\n \n" + reviewList.get(0).getContent());
        }
        firstReview.setTextColor(context.getResources().getColor(R.color.teal));
        firstReview.setId(1);
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, seeAllClickText.getId());
        params1.addRule(RelativeLayout.ALIGN_RIGHT, seeAllClickText.getId());
        params1.addRule(RelativeLayout.BELOW, seeAllClickText.getId());
        params1.setMargins(20, 20, 5, 50);


        ((ViewGroup) seeAllClickText.getParent()).removeView(seeAllClickText);

        reviewLayout.addView(seeAllClickText, params2);
        if (firstReview.getParent() != null) {
            ((ViewGroup) firstReview.getParent()).removeView(firstReview);
        }
        reviewLayout.addView(firstReview, params1);
        seeAllClickText.setOnClickListener(this);


        ImageView favoriteButton = (ImageView) view.findViewById(R.id.favoriteButton);
        ImageView backdrop = (ImageView) view.findViewById(R.id.backdrop);
        Picasso.with(context).load(backdropPath).into(backdrop);
        TextView titleText = (TextView) view.findViewById(R.id.textView_originalTitle);
        titleText.setText(originalTitle);
        TextView releaseDateText = (TextView) view.findViewById(R.id.textView_release_date);
        releaseDateText.setText(getString(R.string.release_date) + " " + releaseDate);
        TextView userRatingText = (TextView) view.findViewById(R.id.textView_user_rating);
        userRatingText.setText(getString(R.string.user_rating) + " " + voteAverage + "/10");
        ImageView posterThumbnail = (ImageView) view.findViewById(R.id.poster_thumbnail);
        Picasso.with(getActivity()).load(posterPath).error(R.drawable.movies_thumbnail_placeholder).into(posterThumbnail);
        TextView overview = (TextView) view.findViewById(R.id.textView_overview);

        if ("null".equals(movieOverview) || "No overview found.".equals(movieOverview) || "null".equals(movieOverview)) {
            overview.setText("");
        } else {
            overview.setText(movieOverview);
        }

        favoriteButton.setOnClickListener(this);

        ImageButton[] bt = new ImageButton[movieTrailerList.size()];

        reviewLayout = (RelativeLayout) view.findViewById(R.id.trailer_button_container);

        for (int i = 0; i < movieTrailerList.size(); i++) {

            bt[i] = new ImageButton(context);
            bt[i].setImageResource(R.drawable.trailer_play_button);
            bt[i].setScaleType(ImageView.ScaleType.FIT_XY);
            bt[i].setId(500 + i);
            bt[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bt[i].getLayoutParams();
            params.setMargins(10, 10, 10, 10);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            if (i != 0) {
                params.addRule(RelativeLayout.BELOW, bt[i - 1].getId());
            }
            trailerLayout.addView(bt[i]);


        }
    }


    //instantiate trailerObjects and pass in data
    public void trailerCreator(ArrayList<Trailer> trailerList) {
        movieTrailerList = trailerList;

        if (sortValue.equals("favorites")) {
            ArrayList<Trailer> trailerFavList = new ArrayList<>();

            String[] namesTrailers = favMovie.getFavoriteTrailersName().split("\0");
            String[] sizeTrailers = favMovie.getFavoriteTrailersSize().split("\0");
            String[] sourceTrailers = favMovie.getFavoriteTrailersSource().split("\0");


            for (int i = 0; i < namesTrailers.length; i++) {

                trailerFavList.add(new Trailer(namesTrailers[i], sizeTrailers[i], sourceTrailers[i]));
            }
            movieTrailerList = trailerFavList;
            instantiateTrailerViews(movieTrailerList);
        } else


            instantiateTrailerViews(movieTrailerList);

    }

    public void instantiateTrailerViews(ArrayList<Trailer> list) {

        ImageButton[] bt = new ImageButton[list.size()];

        trailerLayout = (RelativeLayout) view.findViewById(R.id.trailer_button_container);

        for (int i = 0; i < list.size(); i++) {

            bt[i] = new ImageButton(context);
            bt[i].setImageResource(R.drawable.trailer_play_button);
            bt[i].setScaleType(ImageView.ScaleType.FIT_XY);
            bt[i].setId(500 + i);
            bt[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bt[i].getLayoutParams();
            params.setMargins(10, 10, 10, 10);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            if (i != 0) {
                params.addRule(RelativeLayout.BELOW, bt[i - 1].getId());
            }
            trailerLayout.addView(bt[i]);

            TextView trailerDescriptionText = new TextView(context);

            trailerDescriptionText.setText(list.get(i).getTrailerName() + "\n");
            trailerDescriptionText.setTextColor(context.getResources().getColor(R.color.teal));
            trailerDescriptionText.setId(600 + i);
            trailerDescriptionText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) trailerDescriptionText.getLayoutParams();
            textParams.setMargins(10, 10, 10, 10);
            textParams.addRule(RelativeLayout.RIGHT_OF, bt[i].getId());
            textParams.addRule(RelativeLayout.ALIGN_BOTTOM, bt[i].getId());
            trailerLayout.addView(trailerDescriptionText);

            final String trailerSource = movieTrailerList.get(i).getSource();
            bt[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String trailerUrl = "https://www.youtube.com/watch?v=" + trailerSource;
                    Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                    context.startActivity(trailerIntent);
                }
            });
        }

    }


    public void onClick(View v) {

        if (v.getId() == seeAllClickText.getId()) {

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("reviewData", reviewList);

            ReviewDialogFragment reviewFragment = new ReviewDialogFragment();
            reviewFragment.setArguments(bundle);
            reviewFragment.show(getFragmentManager(), "ReviewDialog");

        } else if (v == view.findViewById(R.id.favoriteButton)) {

            saveFavorites();
        }
    }

    public void saveFavorites() {
        {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.FavoriteMovieTable.BACKDROP, backdropPath);
                    cv.put(MovieContract.FavoriteMovieTable.TITLE, originalTitle);
                    cv.put(MovieContract.FavoriteMovieTable.OVERVIEW, movieOverview);
                    cv.put(MovieContract.FavoriteMovieTable.VOTE_AVERAGE, voteAverage);
                    cv.put(MovieContract.FavoriteMovieTable.MOVIE_ID, movieId);
                    cv.put(MovieContract.FavoriteMovieTable.RELEASE_DATE, releaseDate);
                    cv.put(MovieContract.FavoriteMovieTable.POSTER_PATH, posterPath);
                    cv.put(MovieContract.FavoriteMovieTable.POSTER_LAST_PATH, posterLastPath);


                    StringBuilder reviewIdBuilder = new StringBuilder();
                    StringBuilder reviewAuthorBuilder = new StringBuilder();
                    StringBuilder reviewContentBuilder = new StringBuilder();
                    if (!reviewList.isEmpty()) {
                        for (int i = 0; i < reviewList.size(); i++) {

                            reviewIdBuilder.append(reviewList.get(i).getIdNum());
                            reviewAuthorBuilder.append(reviewList.get(i).getAuthor());
                            reviewContentBuilder.append(reviewList.get(i).getContent());

                            if (i != reviewList.size() - 1) {
                                reviewIdBuilder.append("\0");
                                reviewAuthorBuilder.append("\0");
                                reviewContentBuilder.append("\0");
                            }
                        }
                    }
                    String filmReviewId = reviewIdBuilder.toString();
                    Log.d("SSSAW", filmReviewId);
                    String filmReviewAuthor = reviewAuthorBuilder.toString();
                    String filmReviewContent = reviewContentBuilder.toString();

                    cv.put(MovieContract.FavoriteMovieTable.REVIEWS_ID, filmReviewId);
                    cv.put(MovieContract.FavoriteMovieTable.REVIEWS_AUTHOR, filmReviewAuthor);
                    cv.put(MovieContract.FavoriteMovieTable.REVIEWS_CONTENT, filmReviewContent);

                    StringBuilder trailerNameBuilder = new StringBuilder();
                    StringBuilder trailerSizeBuilder = new StringBuilder();
                    StringBuilder trailerSourceBuilder = new StringBuilder();
                    if (!movieTrailerList.isEmpty()) {
                        for (int i = 0; i < movieTrailerList.size(); i++) {
                            trailerNameBuilder.append(movieTrailerList.get(i).getTrailerName());
                            trailerSizeBuilder.append(movieTrailerList.get(i).getSize());
                            trailerSourceBuilder.append(movieTrailerList.get(i).getSource());

                            if (i != movieTrailerList.size() - 1) {
                                trailerNameBuilder.append("\0");
                                trailerSizeBuilder.append("\0");
                                trailerSourceBuilder.append("\0");
                            }
                        }
                    }
                    String filmTrailerNames = trailerNameBuilder.toString();
                    String filmTrailerSizes = trailerSizeBuilder.toString();
                    String filmTrailerSources = trailerSourceBuilder.toString();

                    cv.put(MovieContract.FavoriteMovieTable.TRAILERS_NAMES, filmTrailerNames);
                    cv.put(MovieContract.FavoriteMovieTable.TRAILERS_SIZES, filmTrailerSizes);
                    cv.put(MovieContract.FavoriteMovieTable.TRAILERS_SOURCES, filmTrailerSources);

                    getActivity().getContentResolver().insert(MovieContract.URI_FAVORITES, cv);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);


            Toast toast = null;
            if (toast == null) {
                toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show();
            } else {
                toast.cancel();
            }
        }
    }

    public void reviewDataFunnel(ArrayList<Review> filmReviewList) {
        reviewList = filmReviewList;
    }

    public void trailerDataFunnel(ArrayList<Trailer> trailerList) {
        movieTrailerList = trailerList;
    }


    private static class TrailerReviewTask extends AsyncTask<String, Void, JSONObject> {
        JSONObject jsonObj;
        private static final String LOG_TAG = "Error: ";
        DetailsActivityFragment uIContainer;
        ImageView favoriteButton;

        public TrailerReviewTask(DetailsActivityFragment act, ImageView saveButton) {
            uIContainer = act;
            favoriteButton = saveButton;


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            //connect to the network
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            String api_key = KeyConstants.API_KEY;
            String baseUrl = KeyConstants.TRAILERS_REVIEWS_BASE_URL;
            JSONObject jObject = null;
            URL myURL;
            String url;

            try {

                url = Uri.parse(baseUrl).buildUpon().appendPath(params[0])
                        .appendQueryParameter("api_key", api_key).appendQueryParameter("append_to_response", "trailers,reviews")
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

        @Override
        protected void onPostExecute(JSONObject obj) {
        /*extract json array from object pass to methods inside Movie class
        which hydrates Movie object and returns an array list of Movie objects
        Array list is passed via PassDataInterface to MainActivity */
            super.onPostExecute(obj);
            jsonObj = obj;

            //instantiate detail views with data
            ArrayList<Trailer> trailerList = Trailer.fromJson(obj);
            ArrayList<Review> reviewList = Review.fromJson(obj);

            uIContainer.createShareTrailerIntent(trailerList);
            uIContainer.trailerCreator(trailerList);
            uIContainer.reviewCreator(reviewList);


            //instantiate views and set data from api
            ImageView favoriteButton = (ImageView) uIContainer.view.findViewById(R.id.favoriteButton);
            ImageView backdrop = (ImageView) uIContainer.view.findViewById(R.id.backdrop);
            Picasso.with(uIContainer.context).load(uIContainer.backdropPath).into(backdrop);
            TextView titleText = (TextView) uIContainer.view.findViewById(R.id.textView_originalTitle);
            titleText.setText(uIContainer.originalTitle);
            TextView releaseDateText = (TextView) uIContainer.view.findViewById(R.id.textView_release_date);
            releaseDateText.setText(uIContainer.getString(R.string.release_date) + " " + uIContainer.releaseDate);
            TextView userRatingText = (TextView) uIContainer.view.findViewById(R.id.textView_user_rating);
            userRatingText.setText(uIContainer.getString(R.string.user_rating) + " " + uIContainer.voteAverage + "/10");
            ImageView posterThumbnail = (ImageView) uIContainer.view.findViewById(R.id.poster_thumbnail);
            Picasso.with(uIContainer.getActivity()).load(uIContainer.posterPath).error(R.drawable.movies_thumbnail_placeholder).into(posterThumbnail);
            TextView overview = (TextView) uIContainer.view.findViewById(R.id.textView_overview);

            if ("null".equals(uIContainer.movieOverview) || "No overview found.".equals(uIContainer.movieOverview) || "null".equals(uIContainer.movieOverview)) {
                overview.setText("");
            } else {
                overview.setText(uIContainer.movieOverview);
            }

            favoriteButton.setOnClickListener(uIContainer);

            ImageButton[] bt = new ImageButton[uIContainer.movieTrailerList.size()];

            uIContainer.trailerLayout = (RelativeLayout) uIContainer.view.findViewById(R.id.trailer_button_container);

            for (int i = 0; i < uIContainer.movieTrailerList.size(); i++) {

                bt[i] = new ImageButton(uIContainer.context);
                bt[i].setImageResource(R.drawable.trailer_play_button);
                bt[i].setScaleType(ImageView.ScaleType.FIT_XY);
                bt[i].setId(500 + i);
                bt[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bt[i].getLayoutParams();
                params.setMargins(10, 10, 10, 10);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                if (i != 0) {
                    params.addRule(RelativeLayout.BELOW, bt[i - 1].getId());
                }
                uIContainer.reviewLayout.addView(bt[i]);

                TextView trailerDescriptionText = new TextView(uIContainer.context);

                trailerDescriptionText.setText(uIContainer.movieTrailerList.get(i).getTrailerName() + "\n");
                trailerDescriptionText.setTextColor(uIContainer.context.getResources().getColor(R.color.teal));
                trailerDescriptionText.setId(600 + i);
                trailerDescriptionText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) trailerDescriptionText.getLayoutParams();
                textParams.setMargins(10, 10, 10, 10);
                textParams.addRule(RelativeLayout.RIGHT_OF, bt[i].getId());
                textParams.addRule(RelativeLayout.ALIGN_BOTTOM, bt[i].getId());
                uIContainer.trailerLayout.addView(trailerDescriptionText);

                final String trailerSource = uIContainer.movieTrailerList.get(i).getSource();
                bt[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trailerUrl = "https://www.youtube.com/watch?v=" + trailerSource;
                        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                        uIContainer.context.startActivity(trailerIntent);
                    }
                });
            }

            // instantiate review text dynamically
            uIContainer.reviewLayout = (RelativeLayout) uIContainer.view.findViewById(R.id.review_container);

            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            uIContainer.seeAllClickText = new TextView(uIContainer.context);
            uIContainer.seeAllClickText.setText("See all");
            uIContainer.seeAllClickText.setTextColor(uIContainer.context.getResources().getColor(R.color.teal));
            uIContainer.seeAllClickText.setClickable(true);
            uIContainer.seeAllClickText.setId(2);

            //add rules and set text
            params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params2.setMargins(0, 5, 25, 2);
            uIContainer.reviewLayout.addView(uIContainer.seeAllClickText, params2);

            TextView firstReview = new TextView(uIContainer.context);
            if (reviewList.isEmpty()) {
                firstReview.setText("No reviews");
            } else {
                firstReview.setText(uIContainer.reviewList.get(0).getAuthor() + "\n \n" + reviewList.get(0).getContent());
            }
            firstReview.setTextColor(uIContainer.context.getResources().getColor(R.color.teal));
            firstReview.setId(1);
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, uIContainer.seeAllClickText.getId());
            params1.addRule(RelativeLayout.ALIGN_RIGHT, uIContainer.seeAllClickText.getId());
            params1.addRule(RelativeLayout.BELOW, uIContainer.seeAllClickText.getId());
            params1.setMargins(20, 20, 5, 50);


            ((ViewGroup) uIContainer.seeAllClickText.getParent()).removeView(uIContainer.seeAllClickText);

            uIContainer.reviewLayout.addView(uIContainer.seeAllClickText, params2);
            if (firstReview.getParent() != null) {
                ((ViewGroup) firstReview.getParent()).removeView(firstReview);
            }
            //add view to container
            uIContainer.reviewLayout.addView(firstReview, params1);
            //set click on see all reviews text listener
            uIContainer.seeAllClickText.setOnClickListener(uIContainer);


        }
    }


}























