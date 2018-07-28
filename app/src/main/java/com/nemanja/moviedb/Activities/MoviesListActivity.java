package com.nemanja.moviedb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nemanja.moviedb.Adapters.FragmentTabsAdapter;
import com.nemanja.moviedb.Adapters.MoviesListAdapter;
import com.nemanja.moviedb.Fragments.FavoriteMoviesFragment;
import com.nemanja.moviedb.Fragments.MovieDetailFragment;
import com.nemanja.moviedb.Fragments.PopularMoviesFragment;
import com.nemanja.moviedb.Fragments.TopRatedMoviesFragment;
import com.nemanja.moviedb.Helpers.Constants;
import com.nemanja.moviedb.Data.Movie;
import com.nemanja.moviedb.popularmovies.R;

public class MoviesListActivity extends AppCompatActivity implements MoviesListAdapter.OnMovieClickListener {
    private final String TAG = MoviesListActivity.class.getName();

    private int type = Constants.MOVIES_GENERAL;

    private PopularMoviesFragment popularMoviesFragment;
    private TopRatedMoviesFragment topRatedMoviesFragment;
    private FavoriteMoviesFragment favoriteMoviesFragment;

    private int movieId;
    private String movieTitle;

    private ViewPager viewPager;

    private boolean isTablet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        isTablet = getResources().getBoolean(R.bool.is_tablet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.app_name));
        }
        /*
        In the case of tablet, the other fragment has a different
        toolbar. Hence set as ActionBar only if it is a phone.
         */
        if (!isTablet) {
            setSupportActionBar(toolbar);
        }

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra(Constants.INTENT_EXTRA_TYPE, Constants.MOVIES_GENERAL);
            if (type == Constants.MOVIES_SIMILAR) {
                movieId = intent.getIntExtra(Constants.BUNDLE_ID, 0);
                movieTitle = intent.getStringExtra(Constants.BUNDLE_TITLE);

            }
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        initFragments(savedInstanceState);
        setupViewPager();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager() {
        FragmentTabsAdapter adapter = new FragmentTabsAdapter(getSupportFragmentManager());
        switch (type) {
            case Constants.MOVIES_GENERAL: {
                adapter.addFragment(popularMoviesFragment, getString(R.string.popular));
                adapter.addFragment(topRatedMoviesFragment, getString(R.string.top_rated));
                adapter.addFragment(favoriteMoviesFragment, getString(R.string.favorites));
                viewPager.setOffscreenPageLimit(3);
                break;
            }
        }
        viewPager.setAdapter(adapter);
    }

    private void initFragments(Bundle savedInstanceState) {
        /*
        If savedInstanceState is not null, do not recreate the fragments.
        Use the references to the previously created ones.
         */
        if (savedInstanceState != null) {
            loadFromSavedInstanceState(savedInstanceState);
            return;
        }

        switch (type) {
            case Constants.MOVIES_GENERAL: {
                popularMoviesFragment = new PopularMoviesFragment();
                topRatedMoviesFragment = new TopRatedMoviesFragment();
                favoriteMoviesFragment = new FavoriteMoviesFragment();
                break;
            }
        }
    }

    private void loadFromSavedInstanceState(Bundle savedInstanceState) {
        switch (type) {
            case Constants.MOVIES_GENERAL: {
                popularMoviesFragment = (PopularMoviesFragment) getSupportFragmentManager().getFragment(savedInstanceState, PopularMoviesFragment.TAG);
                topRatedMoviesFragment = (TopRatedMoviesFragment) getSupportFragmentManager().getFragment(savedInstanceState, TopRatedMoviesFragment.TAG);
                favoriteMoviesFragment = (FavoriteMoviesFragment) getSupportFragmentManager().getFragment(savedInstanceState, FavoriteMoviesFragment.TAG);
                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        switch (type) {
            case Constants.MOVIES_GENERAL: {
                getSupportFragmentManager().putFragment(outState, PopularMoviesFragment.TAG, popularMoviesFragment);
                getSupportFragmentManager().putFragment(outState, TopRatedMoviesFragment.TAG, topRatedMoviesFragment);
                getSupportFragmentManager().putFragment(outState, FavoriteMoviesFragment.TAG, favoriteMoviesFragment);
                break;
            }
        }
    }

    @Override
    public void onMovieClick(Movie movie) {
        if (!isTablet) {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_MOVIE, movie);
            startActivity(intent);

        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BUNDLE_MOVIE, movie);

            /*
            If tablet, the MovieDetailFragment will be loaded in the same
            activity in the view R.id.container_movie_detail. Using replace
            instead of add because internally it will work as
            replace(currentFragment).add(viewId, newFragment, null). This
            will prevent overlapping of fragments. However, the for the
            first time this fragment is loaded, it will be as
            replace(null).add(viewId, newFragment, null) as there are no
            fragments in that view.
             */
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_movie_detail, movieDetailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
