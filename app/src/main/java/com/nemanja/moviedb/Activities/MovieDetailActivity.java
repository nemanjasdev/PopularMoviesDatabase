package com.nemanja.moviedb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.nemanja.moviedb.Fragments.AboutMovieFragment;
import com.nemanja.moviedb.Fragments.MovieDetailFragment;
import com.nemanja.moviedb.popularmovies.R;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (int i = 0, l = fragments.size(); i < l; i++) {
                Fragment fragment = fragments.get(i);
                if (fragment instanceof MovieDetailFragment
                        || fragment instanceof AboutMovieFragment) {
                    return;
                }
            }
        }

        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_movie_detail, movieDetailFragment)
                .commit();
    }
}
