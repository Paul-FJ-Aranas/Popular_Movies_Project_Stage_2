package com.paularanas.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuItemWrapperICS;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ShareActionProvider;

import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements MovieFragment.OnMovieSelectedListener {
    private Boolean mTwoPane = false;
    private Parcelable movieObject;
    private Bundle state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        establishPaneLayout();
        state = savedInstanceState;

    }

    private void establishPaneLayout() {
        FrameLayout fragmentItemDetail = (FrameLayout) findViewById(R.id.movie_detail_container);

        if (fragmentItemDetail != null) {
            mTwoPane = true;
            MovieFragment movieFragment =
                    (MovieFragment) getFragmentManager().findFragmentById(R.id.main_movie_fragment);
            movieFragment.setActivateOnMovieClick(true);
            getSupportActionBar().setElevation(0f);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }


    @Override
    public void onMovieSelected(Parcelable movieObject) {

        if (mTwoPane) { // one activity, replace framelayout with new details fragment
                DetailsActivityFragment fragmentDetails = DetailsActivityFragment.newInstance(movieObject);
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.movie_detail_container, fragmentDetails);
                transaction.commit();

        } else {
            // go to separate activity
            // launch detail activity using intent
            Intent intent = new Intent(this, DetailsActivity.class);
            if (movieObject instanceof Movie) {
                intent.putExtra("movieData", movieObject);
            } else if (movieObject instanceof FavoriteMovie) {
                intent.putExtra("movieFavData", movieObject);
            }
            startActivity(intent);
        }

    }
}

















