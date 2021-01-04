package com.blogspot.thirkazh.moviecatalogue.fragment;

import com.blogspot.thirkazh.moviecatalogue.model.tv.TvItem;

import java.util.ArrayList;

public interface LoadTvCallback {
    void preExecute();

    void postExecute(ArrayList<TvItem> tvItems);
}
