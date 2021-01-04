package com.blogspot.thirkazh.moviecatalogue.network;

import com.blogspot.thirkazh.moviecatalogue.model.movie.ResponseMovie;
import com.blogspot.thirkazh.moviecatalogue.model.tv.ResponseTv;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("discover/movie")
    Call<ResponseMovie> getMovies(@Query("api_key") String apiKey);

    @GET("discover/tv")
    Call<ResponseTv> getTvShow(@Query("api_key") String apiKey);

    @GET("search/movie")
    Call<ResponseMovie> getSearchMovies(@Query("api_key") String apiKey,
                                              @Query("query") String movieName);

    @GET("search/tv")
    Call<ResponseTv> getSearchTvShow(@Query("api_key") String apiKey,
                                              @Query("query") String tvName);

    @GET("discover/movie")
    Call<ResponseMovie> getReleaseMovie(@Query("api_key") String apiKey,
                                        @Query("primary_release_date.gte") String todayGte,
                                        @Query("primary_release_date.lte") String todayLte);

}
