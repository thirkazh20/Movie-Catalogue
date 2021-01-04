package com.blogspot.thirkazh.moviecatalogue.fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import android.util.Log;

import com.blogspot.thirkazh.moviecatalogue.BuildConfig;
import com.blogspot.thirkazh.moviecatalogue.model.movie.ResponseMovie;
import com.blogspot.thirkazh.moviecatalogue.model.movie.MovieItem;
import com.blogspot.thirkazh.moviecatalogue.network.ApiClient;
import com.blogspot.thirkazh.moviecatalogue.network.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private MutableLiveData<List<MovieItem>> listMovies = new MutableLiveData<>();

    public MutableLiveData<List<MovieItem>> getListMovies() {
        return listMovies;
    }

    void setListMovies() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseMovie> movieCall = apiInterface.getMovies(API_KEY);
        movieCall.enqueue(new Callback<ResponseMovie>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMovie> call, @NonNull Response<ResponseMovie> response) {
                if (response.body() != null) {
                    listMovies.postValue(response.body().getResults());
                    Log.d("onResponseMovie ", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMovie> call, @NonNull Throwable t) {
                Log.d("onFailureMovie ", t.getMessage());
            }
        });
    }

    void setListSearchMovies(String name) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseMovie> movieCall = apiInterface.getSearchMovies(API_KEY, name);
        movieCall.enqueue(new Callback<ResponseMovie>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMovie> call, @NonNull Response<ResponseMovie> response) {
                if (response.body() != null) {
                    listMovies.postValue(response.body().getResults());
                    Log.d("onResponseSearchMovie ", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMovie> call, @NonNull Throwable t) {
                Log.d("onFailureSearchMovie ", t.getMessage());
            }
        });
    }
}
