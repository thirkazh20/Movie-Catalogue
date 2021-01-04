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
import com.blogspot.thirkazh.moviecatalogue.adapter.FavoriteMovieAdapter;
import com.blogspot.thirkazh.moviecatalogue.db.MovieHelper;
import com.blogspot.thirkazh.moviecatalogue.model.movie.MovieItem;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMovieFragment extends Fragment implements LoadMoviesCallback {
    private ProgressBar pgFavMovie;
    private MovieHelper movieHelper;
    private FavoriteMovieAdapter favoriteMovieAdapter;
    private static final String EXTRA_STATE = "EXTRA_STATE";

    public FavoriteMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_favorite_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvFavMovie = view.findViewById(R.id.rv_fav_movie);
        rvFavMovie.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavMovie.setHasFixedSize(true);
        pgFavMovie = view.findViewById(R.id.pg_fav_movie);

        movieHelper = MovieHelper.getInstance(getActivity());
        try {
            movieHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        favoriteMovieAdapter = new FavoriteMovieAdapter(getActivity());
        rvFavMovie.setAdapter(favoriteMovieAdapter);

        favoriteMovieAdapter.setOnItemClickCallback(new FavoriteMovieAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(MovieItem data) {
                showSelectedMovie(data);
            }
        });

        if (savedInstanceState == null) {
            Log.d("favoritemovie", "onViewCreated: saved instance kosong");
            new LoadMoviesAsync(movieHelper, this).execute();
        } else {
            Log.d("favoritemovie", "onViewCreated: saved instance ada");
            ArrayList<MovieItem> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                favoriteMovieAdapter.setListMovies(list);
            }
        }
    }

    private void showSelectedMovie(MovieItem movie) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_DETAIL_DATA, movie);
        intent.putExtra(DetailActivity.KEY_JENIS_DATA, "movie");
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, favoriteMovieAdapter.getListMovies());
    }

    @Override
    public void preExecute() {
        new Runnable() {
            @Override
            public void run() {
                pgFavMovie.setVisibility(View.VISIBLE);
            }
        };
        Log.d("favoritemovie", "preExecute: masuk");
    }

    @Override
    public void postExecute(ArrayList<MovieItem> movieItems) {
        pgFavMovie.setVisibility(View.INVISIBLE);
        favoriteMovieAdapter.setListMovies(movieItems);
        Log.d("favoritemovie", "postExecute: " + movieItems.toString());
    }

    private class LoadMoviesAsync extends AsyncTask<Void, Void, ArrayList<MovieItem>> {
        private final WeakReference<MovieHelper> movieHelperWeakReference;
        private final WeakReference<LoadMoviesCallback> moviesCallbackWeakReference;

        LoadMoviesAsync(MovieHelper movieHelper, LoadMoviesCallback loadMoviesCallback) {
            movieHelperWeakReference = new WeakReference<>(movieHelper);
            moviesCallbackWeakReference = new WeakReference<>(loadMoviesCallback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            moviesCallbackWeakReference.get().preExecute();
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(Void... voids) {
            return movieHelperWeakReference.get().getAllMovies();
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieItems) {
            super.onPostExecute(movieItems);

            moviesCallbackWeakReference.get().postExecute(movieItems);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        movieHelper.close();
    }
}

