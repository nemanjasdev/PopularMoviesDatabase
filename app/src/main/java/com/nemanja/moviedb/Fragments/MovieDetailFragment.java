package com.nemanja.moviedb.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.nemanja.moviedb.DB.MovieContract;
import com.nemanja.moviedb.Helpers.Constants;
import com.nemanja.moviedb.Helpers.StateHandler;
import com.nemanja.moviedb.Data.Movie;
import com.nemanja.moviedb.API.TmdbRestClient;
import com.nemanja.moviedb.popularmovies.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {
  public static final String TAG = MovieDetailFragment.class.getName();

  private Movie movie;

  private boolean isFavorite;
  private FloatingActionButton favoriteButton;


  private TextView duration;
  private TextView rating;


  private TextView tagLine;
  private TextView overview;
  private TextView readMore;

  private final String BACKDROP_IMAGE_URL = "http://image.tmdb.org/t/p/w500";
  private final String POSTER_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

  private final String TMDB_MOVIE_URL = "https://www.themoviedb.org/movie/";

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getActivity().getIntent();
    if (intent != null) {
      movie = intent.getParcelableExtra(Constants.INTENT_EXTRA_MOVIE);
    }

    if (getArguments() != null) {
      movie = getArguments().getParcelable(Constants.BUNDLE_MOVIE);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      movie = savedInstanceState.getParcelable(Constants.BUNDLE_MOVIE);
    }

    View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
    initToolbarAndFAB(view);
    initMovieDetail(view);
    initMovieImages(view);
    initAboutMovie(view);
    return view;
  }

  private void initToolbarAndFAB(View view) {
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setTitle(movie.getTitle());
    toolbar.inflateMenu(R.menu.menu_movie_detail);


    favoriteButton = (FloatingActionButton) view.findViewById(R.id.button_favorite);
    favoriteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new FavoriteTogglerTask().execute();
      }
    });
    new FavoriteCheckerTask().execute();
  }

  private void initMovieDetail(View view) {
    TextView title = (TextView) view.findViewById(R.id.text_view_title);
    title.setText(movie.getTitle());

    TextView releaseDate = (TextView) view.findViewById(R.id.text_view_release_date);
    releaseDate.setText(movie.getReleaseDate());

    rating = (TextView) view.findViewById(R.id.text_view_rating);
    String voteAverage = Double.toString(movie.getVoteAverage());
    rating.setText(voteAverage);

    duration = (TextView) view.findViewById(R.id.text_view_duration);
  }

  private void initMovieImages(View view) {
    ImageView backdropImage = (ImageView) view.findViewById(R.id.image_view_backdrop);
    ImageView posterImage = (ImageView) view.findViewById(R.id.image_view_poster);

    Glide.with(view.getContext())
      .load(BACKDROP_IMAGE_URL + movie.getBackdropPath())
      .asBitmap()
      .format(DecodeFormat.PREFER_ARGB_8888)
      .placeholder(R.drawable.image_placeholder)
      .into(backdropImage);

    Glide.with(view.getContext())
      .load(POSTER_IMAGE_URL + movie.getPosterPath())
      .placeholder(R.drawable.image_placeholder)
      .into(posterImage);
  }

  private void initAboutMovie(View view) {
    overview = (TextView) view.findViewById(R.id.text_view_overview);
    overview.setText(movie.getOverview());
    tagLine = (TextView) view.findViewById(R.id.text_view_tag_line);
    readMore = (TextView) view.findViewById(R.id.text_view_read_more);
    readMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_ID, movie.getId());
        bundle.putString(Constants.BUNDLE_TITLE, movie.getTitle());
        bundle.putString(Constants.BUNDLE_TAG_LINE, movie.getTagLine());
        bundle.putString(Constants.BUNDLE_OVERVIEW, movie.getOverview());

        AboutMovieFragment aboutMovieFragment = new AboutMovieFragment();
        aboutMovieFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
          .replace(R.id.container_movie_detail, aboutMovieFragment)
          .addToBackStack(null)
          .commit();
      }
    });
    readMore.setVisibility(View.INVISIBLE);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    loadMovieDetails();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(Constants.BUNDLE_MOVIE, movie);
  }

  private void loadMovieDetails() {
    if (movie.getTagLine() != null) {
      updateUI();
      return;
    }
    Call<Movie> call = TmdbRestClient.getInstance()
      .getMovieDetailsImpl()
      .getMovieDetails(movie.getId());
    Callback<Movie> callback = new Callback<Movie>() {
      @Override
      public void onResponse(Call<Movie> call, Response<Movie> response) {
        if (!response.isSuccessful()) {
          movie.setTagLine(StateHandler.handleMovieDetailState(getContext(), Constants.SERVER_ERROR));

        } else {
          movie.setDuration(response.body().getDuration());
          movie.setTagLine(response.body().getTagLine());
          movie.setOverview(response.body().getOverview());
          movie.setVoteCount(response.body().getVoteCount());
          movie.setVoteAverage(response.body().getVoteAverage());
        }
        update();
      }

      @Override
      public void onFailure(Call<Movie> call, Throwable t) {
        movie.setTagLine(StateHandler.handleMovieDetailState(getContext(), Constants.NETWORK_ERROR));
        update();
      }

      private void update() {
        updateUI();
      }
    };
    call.enqueue(callback);
  }

  private void updateUI() {
    setupAboutMovieView();
  }

  private void setupAboutMovieView() {
    String runtime = Integer.toString(movie.getDuration()) + " minutes";
    duration.setText(runtime);

    rating.setText(String.valueOf(movie.getVoteAverage()));
    if (movie.getTagLine() != null && movie.getTagLine().length() > 0) {
      tagLine.setText(movie.getTagLine());
    }
    overview.setText(movie.getOverview());
    if (overview != null || overview.length() > 0) {
      readMore.setVisibility(View.VISIBLE);
    }
  }

  private class FavoriteCheckerTask extends AsyncTask<Void, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Void... params) {
      Cursor cursor = getContext().getContentResolver()
        .query(
          MovieContract.MovieEntry.CONTENT_URI,
          new String[]{MovieContract.MovieColumns.MOVIE_ID},
          MovieContract.MovieColumns.MOVIE_ID + " = ?",
          new String[]{String.valueOf(movie.getId())},
          null
        );
      boolean isExists = cursor != null && cursor.getCount() == 1;
      if (cursor != null) {
        cursor.close();
      }
      return isExists;
    }

    @Override
    protected void onPostExecute(Boolean isExists) {
      super.onPostExecute(isExists);
      if (isExists) {
        favoriteButton.setImageResource(R.drawable.ic_star_white_24dp);
      } else {
        favoriteButton.setImageResource(R.drawable.ic_star_border_white_24dp);
      }
      isFavorite = isExists;
    }
  }

  private class FavoriteTogglerTask extends AsyncTask<Void, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Void... params) {
      boolean isSuccessful;
            /*
            If movie is already among favorites, remove it.
            Else add.
             */
      if (isFavorite) {
        isSuccessful = getContext().getContentResolver()
          .delete(
            MovieContract.MovieEntry.CONTENT_URI,
            MovieContract.MovieColumns.MOVIE_ID + " = ?",
            new String[]{String.valueOf(movie.getId())}
          ) == 1;
      } else {
        isSuccessful = getContext().getContentResolver()
          .insert(MovieContract.MovieEntry.CONTENT_URI, getContentValues()) != null;
      }
      return isSuccessful;
    }

    @Override
    protected void onPostExecute(Boolean isSuccessful) {
      super.onPostExecute(isSuccessful);
      if (!isSuccessful) {
        showToast(getString(R.string.op_failed));
        return;
      }
      isFavorite = !isFavorite;
      if (isFavorite) {
        favoriteButton.setImageResource(R.drawable.ic_star_white_24dp);
        showToast(getString(R.string.movie_added));
      } else {
        favoriteButton.setImageResource(R.drawable.ic_star_border_white_24dp);
        showToast(getString(R.string.movie_removed));
      }
    }

    private ContentValues getContentValues() {
      ContentValues values = new ContentValues();
      values.put(MovieContract.MovieColumns.MOVIE_ID, movie.getId());
      values.put(MovieContract.MovieColumns.MOVIE_TITLE, movie.getTitle());
      values.put(MovieContract.MovieColumns.MOVIE_RELEASE_DATE, movie.getReleaseDate());
      values.put(MovieContract.MovieColumns.MOVIE_DURATION, movie.getDuration());
      values.put(MovieContract.MovieColumns.MOVIE_RATING, movie.getVoteAverage());
      values.put(MovieContract.MovieColumns.MOVIE_POSTER_PATH, movie.getPosterPath());
      values.put(MovieContract.MovieColumns.MOVIE_BACKDROP_PATH, movie.getBackdropPath());
      return values;
    }
  }

  private void showToast(String text) {
    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
  }
}
