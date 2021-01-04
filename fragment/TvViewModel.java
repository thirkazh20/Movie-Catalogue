package com.blogspot.thirkazh.moviecatalogue.fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import android.util.Log;

import com.blogspot.thirkazh.moviecatalogue.BuildConfig;
import com.blogspot.thirkazh.moviecatalogue.model.tv.ResponseTv;
import com.blogspot.thirkazh.moviecatalogue.model.tv.TvItem;
import com.blogspot.thirkazh.moviecatalogue.network.ApiClient;
import com.blogspot.thirkazh.moviecatalogue.network.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvViewModel extends ViewModel {
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;;
    private MutableLiveData<List<TvItem>> listTvs = new MutableLiveData<>();

    MutableLiveData<List<TvItem>> getListTv() {
        return listTvs;
    }

    void setListTv() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseTv> tvCall = apiInterface.getTvShow(API_KEY);
        tvCall.enqueue(new Callback<ResponseTv>() {
            @Override
            public void onResponse(@NonNull Call<ResponseTv> call, @NonNull Response<ResponseTv> response) {
                if (response.body() != null) {
                    listTvs.postValue(response.body().getResults());
                    Log.d("onResponseTv ", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseTv> call, @NonNull Throwable t) {
                Log.d("onFailureTv ", t.getMessage());
            }
        });
    }

    void setListSearchTv(String name) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseTv> tvCall = apiInterface.getSearchTvShow(API_KEY, name);
        tvCall.enqueue(new Callback<ResponseTv>() {
            @Override
            public void onResponse(@NonNull Call<ResponseTv> call, @NonNull Response<ResponseTv> response) {
                if (response.body() != null) {
                    listTvs.postValue(response.body().getResults());
                    Log.d("onResponseSearchTv ", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseTv> call, @NonNull Throwable t) {
                Log.d("onFailureSearchTv ", t.getMessage());
            }
        });
    }
}
