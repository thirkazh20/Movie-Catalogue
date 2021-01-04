package com.blogspot.thirkazh.moviecatalogue.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.activity.DetailActivity;
import com.blogspot.thirkazh.moviecatalogue.adapter.FavoriteTvAdapter;
import com.blogspot.thirkazh.moviecatalogue.db.TvHelper;
import com.blogspot.thirkazh.moviecatalogue.model.tv.TvItem;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteTvFragment extends Fragment implements LoadTvCallback {
    private ProgressBar pgFavTv;
    private TvHelper tvHelper;
    private FavoriteTvAdapter favoriteTvAdapter;
    private static final String EXTRA_STATE = "EXTRA_STATE";

    public FavoriteTvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_favorite_tv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvFavTv = view.findViewById(R.id.rv_fav_tv);
        rvFavTv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavTv.setHasFixedSize(true);
        pgFavTv = view.findViewById(R.id.pg_fav_tv);

        tvHelper = TvHelper.getInstance(getActivity());
        try {
            tvHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        favoriteTvAdapter = new FavoriteTvAdapter(getActivity());
        rvFavTv.setAdapter(favoriteTvAdapter);

        favoriteTvAdapter.setOnItemClickCallback(new FavoriteTvAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(TvItem data) {
                showSelectedTv(data);
            }
        });

        if (savedInstanceState == null) {
            Log.d("favoritetv", "onViewCreated: saved instance kosong");
            new FavoriteTvFragment.LoadTvAsync(tvHelper, this).execute();
        } else {
            ArrayList<TvItem> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                favoriteTvAdapter.setListTv(list);
            }
            Log.d("favoritetv", "onViewCreated: saved instance ada : " + list);
        }
    }

    private void showSelectedTv(TvItem data) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_DETAIL_DATA, data);
        intent.putExtra(DetailActivity.KEY_JENIS_DATA, "tv");
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, favoriteTvAdapter.getListTv());
        Log.d("favoritetv", "onSaveInstanceState: " + favoriteTvAdapter.getListTv());
    }

    @Override
    public void preExecute() {
        new Runnable() {
            @Override
            public void run() {
                pgFavTv.setVisibility(View.VISIBLE);
            }
        };
        Log.d("favoritetv", "preExecute: masuk");
    }

    @Override
    public void postExecute(ArrayList<TvItem> tvItems) {
        pgFavTv.setVisibility(View.INVISIBLE);
        favoriteTvAdapter.setListTv(tvItems);
        Log.d("favoritetv", "postExecute: " + tvItems.toString());
    }

    private class LoadTvAsync extends AsyncTask<Void, Void, ArrayList<TvItem>> {
        private final WeakReference<TvHelper> tvHelperWeakReference;
        private final WeakReference<LoadTvCallback> tvCallbackWeakReference;

        LoadTvAsync(TvHelper tvHelper, LoadTvCallback loadTvCallback) {
            tvHelperWeakReference = new WeakReference<>(tvHelper);
            tvCallbackWeakReference = new WeakReference<>(loadTvCallback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvCallbackWeakReference.get().preExecute();
        }

        @Override
        protected ArrayList<TvItem> doInBackground(Void... voids) {
            return tvHelperWeakReference.get().getAllTv();
        }

        @Override
        protected void onPostExecute(ArrayList<TvItem> tvItems) {
            super.onPostExecute(tvItems);

            tvCallbackWeakReference.get().postExecute(tvItems);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tvHelper.close();
    }
}
