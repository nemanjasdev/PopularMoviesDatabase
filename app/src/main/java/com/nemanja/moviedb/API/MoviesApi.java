package com.nemanja.moviedb.API;

import com.nemanja.moviedb.Data.Credits;
import com.nemanja.moviedb.Data.Movie;
import com.nemanja.moviedb.Data.MovieImages;
import com.nemanja.moviedb.Data.MoviesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MoviesApi {
    public interface PopularMovies {
        @GET("movie/popular")
        Call<MoviesList> getPopularMovies(@Query("page") Integer page);
    }

    public interface TopRatedMovies {
        @GET("movie/top_rated")
        Call<MoviesList> getTopRatedMovies(@Query("page") Integer page);
    }

    public interface MovieDetails {
        @GET("movie/{id}")
        Call<Movie> getMovieDetails(@Path("id") Integer id);
    }

    public interface GenreMovies {
        @GET("genre/{id}/movies")
        Call<MoviesList> getGenreMovies(@Path("id") Integer id, @Query("page") Integer page);
    }

    public interface SimilarMovies {
        @GET("movie/{id}/similar")
        Call<MoviesList> getSimilarMovies(@Path("id") Integer id, @Query("page") Integer page);
    }

    public interface MovieBackdropImages {
        @GET("movie/{id}/images")
        Call<MovieImages> getMovieBackdropImages(@Path("id") Integer id);
    }

    public interface MovieCredits {
        @GET("movie/{id}/credits")
        Call<Credits> getMovieCredits(@Path("id") Integer id);
    }

    //https://api.themoviedb.org/3/search/movie?api_key=c22d755514350d9836b3f9b173b3d763&query=the+avengers
    public interface MovieSearch {
        @GET("search/movie/{query}")
        Call<Credits> getMovieByTitle( @Query("query") String query);
    }
}
