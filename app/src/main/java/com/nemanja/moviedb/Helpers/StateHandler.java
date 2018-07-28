package com.nemanja.moviedb.Helpers;

import android.content.Context;

import com.nemanja.moviedb.popularmovies.R;

/**
 * Helper class to deal with different error states
 * of the values for certain views to display. Returns
 * the required value for such variables depending on
 * the state. For instance, if there is a network error
 * intimate user to check if device is connected to the
 * Internet by setting the tagline text to the same.
 */
public class StateHandler {
  public static String handleMovieDetailState(Context context, int flag) {
    String tagLine;
    switch (flag) {
      case Constants.NETWORK_ERROR: {
        tagLine = context.getString(R.string.network_error);
        break;
      }

      case Constants.SERVER_ERROR: {
        tagLine = context.getString(R.string.server_error);
        break;
      }

      default: {
        tagLine = "";
      }
    }
    return tagLine;
  }
}
