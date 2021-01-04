package com.blogspot.thirkazh.moviecatalogue.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.activity.DetailActivity;
import com.blogspot.thirkazh.moviecatalogue.adapter.TvAdapter;
import com.blogspot.thirkazh.moviecatalogue.model.tv.TvItem;

import java.util.List;

public class TVShowFragment extends Fragment {

    private TvAdapter tvAdapter;
    private RecyclerView rvTv;
    private ProgressBar pgTv;
    private TvViewModel tvViewModel;

    public TVShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tvshow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvViewModel = ViewModelProviders.of(TVShowFragment.this)
                .get(TvViewModel.class);
        tvViewModel.getListTv().observe(this, getTv);

        rvTv = view.findViewById(R.id.rv_tv);
        pgTv = view.findViewById(R.id.pg_tv);

        tvAdapter = new TvAdapter(getActivity());
        tvAdapter.notifyDataSetChanged();

        tvViewModel.setListTv();
        showLoading(true);
        showRecyclerList();
        setHasOptionsMenu(true);
    }

    private Observer<List<TvItem>> getTv = new Observer<List<TvItem>>() {
        @Override
        public void onChanged(@Nullable List<TvItem> tvItems) {
            if (tvItems != null) {
                tvAdapter.setListData(tvItems);
                showLoading(false);
            }
        }
    };

    private void showRecyclerList() {
        rvTv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTv.setAdapter(tvAdapter);

        tvAdapter.setOnItemClickCallback(new TvAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(TvItem data) {
                showSelectedTv(data);
            }
        });
    }

    private void showSelectedTv(TvItem data) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_DETAIL_DATA, data);
        intent.putExtra(DetailActivity.KEY_JENIS_DATA, "tv");
        startActivity(intent);
    }

    private void showLoading(Boolean state) {
        if (state) {
            pgTv.setVisibility(View.VISIBLE);
        } else {
            pgTv.setVisibility(View.GONE);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_tv, menu);
        final MenuItem searchItem = menu.findItem(R.id.nav_search_tv);

        final SearchView searchView = (SearchView) menu.findItem(R.id.nav_search_tv).getActionView();
        searchView.setQueryHint(getString(R.string.search_maches));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                /*Intent intent = new Intent(getActivity(), SearchMovie.class);
                intent.putExtra("query", s);*/
                tvViewModel.setListSearchTv(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchItem.collapseActionView();
                    tvViewModel.setListTv();
                }
            }
        });
    }
}
