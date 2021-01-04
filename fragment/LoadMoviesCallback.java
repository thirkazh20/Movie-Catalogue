package com.blogspot.thirkazh.moviecatalogue.fragment;

import com.blogspot.thirkazh.moviecatalogue.model.movie.MovieItem;

import java.util.ArrayList;

public interface LoadMoviesCallback {
    void preExecute();

    void postExecute(ArrayList<MovieItem> movieItems);
}
