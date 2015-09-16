package com.paularanas.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class DetailsActivity extends AppCompatActivity {
    Parcelable object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        Intent movieIntent = getIntent();

        if (movieIntent != null) {

            if (movieIntent.getParcelableExtra("movieData") instanceof Movie) {
                try {
                    object = getIntent().getParcelableExtra("movieData");
                } catch (NullPointerException exc) {
                }
            } else if (movieIntent.getParcelableExtra("movieFavData") instanceof FavoriteMovie) {
                try {

                    object = getIntent().getParcelableExtra("movieFavData");
                } catch (NullPointerException exc) {
                }
            }
            if (savedInstanceState == null) {
                try{

                DetailsActivityFragment detailsFragment = DetailsActivityFragment.newInstance(object);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.movie_detail_container, detailsFragment);
                transaction.commit(); }
             catch (Exception e) {}
                Log.e("TAG", "Error with fragment instantiation");
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {
                startActivity(new Intent(this, Settings.class));
                return true;
            } catch (Exception e) {}
        } if  (id == android.R.id.home) {
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
