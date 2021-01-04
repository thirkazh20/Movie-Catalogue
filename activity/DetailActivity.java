package com.blogspot.thirkazh.moviecatalogue.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.db.MovieHelper;
import com.blogspot.thirkazh.moviecatalogue.db.TvHelper;
import com.blogspot.thirkazh.moviecatalogue.model.movie.MovieItem;
import com.blogspot.thirkazh.moviecatalogue.model.tv.TvItem;
import com.blogspot.thirkazh.moviecatalogue.widget.FavoriteWidget;
import com.bumptech.glide.Glide;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.provider.BaseColumns._ID;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.CONTENT_URI;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.DATE;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.OVERVIEW;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.POSTER;
import static com.blogspot.thirkazh.moviecatalogue.db.DatabaseContract.MovieColumns.TITLE;

public class DetailActivity extends AppCompatActivity {
    public static String KEY_DETAIL_DATA = "detail_data";
    public static String KEY_JENIS_DATA = "jenis_data";
    public String jenisData;
    private String title, year, description, poster;
    private int id;
    private Menu menuItem = null;
    private Boolean isFavorite = false;
    private MovieItem movie;
    private TvItem tvShow;
    private MovieHelper movieHelper;
    private TvHelper tvHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView txtDetailTitle = findViewById(R.id.txt_detail_title);
        TextView txtDetailYear = findViewById(R.id.txt_detail_year);
        TextView txtDetailDescription = findViewById(R.id.txt_detail_description);
        ImageView imgDetailPoster = findViewById(R.id.img_detail_poster);



        jenisData = getIntent().getStringExtra(KEY_JENIS_DATA);

        if (jenisData.equals("movie")) {
            movieHelper = MovieHelper.getInstance(getApplicationContext());
            try {
                movieHelper.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            movie = getIntent().getParcelableExtra(KEY_DETAIL_DATA);
            id = movie.getId();
            title = movie.getTitle();
            year = movie.getReleaseDate();
            description = movie.getOverview();
            poster = movie.getPosterPath();
            setTitle(title);
        } else if (jenisData.equals("tv")) {
            tvHelper = TvHelper.getInstance(getApplicationContext());
            try {
                tvHelper.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            tvShow = getIntent().getParcelableExtra(KEY_DETAIL_DATA);
            id = tvShow.getId();
            title = tvShow.getName();
            year = tvShow.getFirstAirDate();
            description = tvShow.getOverview();
            poster = tvShow.getPosterPath();
            setTitle(title);
        }

        txtDetailTitle.setText(title);
        txtDetailYear.setText(year);
        txtDetailDescription.setText(description);

        String baseUrlImage = "https://image.tmdb.org/t/p/original";
        Glide.with(this).load(baseUrlImage + poster).into(imgDetailPoster);

        favoriteState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_to_favorite:
                if (isFavorite) {
                    removeFromFavorite();
                } else {
                    addToFavorite();
                }

                isFavorite = !isFavorite;
                setFavorite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFavorite() {
        if (isFavorite) {
            menuItem.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        } else {
            menuItem.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }
    }

    private void addToFavorite() {
        if (jenisData.equals("movie")) {
//            long result = movieHelper.insertMovie(movie);
            ContentValues values = new ContentValues();

            values.put(_ID, id);
            values.put(TITLE, title);
            values.put(DATE, year);
            values.put(OVERVIEW, description);
            values.put(POSTER, poster);

            getContentResolver().insert(CONTENT_URI, values);

//            if (!(result > 0)) {
//                Toast.makeText(DetailActivity.this, R.string.msg_failed_Favorite, Toast.LENGTH_SHORT).show();
//            } else {
                updateFavoriteWidget(this);

                Toast.makeText(DetailActivity.this, R.string.msg_success_Favorite, Toast.LENGTH_SHORT).show();
//            }
        } else if (jenisData.equals("tv")) {
            long result = tvHelper.insertTv(tvShow);

            if (!(result > 0)) {
                Toast.makeText(DetailActivity.this, R.string.msg_failed_Favorite, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailActivity.this, R.string.msg_success_Favorite, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeFromFavorite() {
        if (jenisData.equals("movie")) {
//            long result = movieHelper.deleteMovie(id);
            int result = getContentResolver().delete(Uri.parse(CONTENT_URI + "/" + id),
                    null,
                    null);

            if (!(result > 0)) {
                Toast.makeText(DetailActivity.this, R.string.failed_delete_favorite, Toast.LENGTH_SHORT).show();
            } else {
                updateFavoriteWidget(this);

                Toast.makeText(DetailActivity.this, R.string.success_delete_favorite, Toast.LENGTH_SHORT).show();
            }
        } else if (jenisData.equals("tv")) {
            long result = tvHelper.deleteTv(id);

            if (!(result > 0)) {
                Toast.makeText(DetailActivity.this, R.string.failed_delete_favorite, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailActivity.this, R.string.success_delete_favorite, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void updateFavoriteWidget(Context context)
    {
        Intent intent = new Intent(context, FavoriteWidget.class);
        intent.setAction(FavoriteWidget.UPDATE_WIDGET);
        context.sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        menuItem = menu;
        setFavorite();
        return true;
    }

    private void favoriteState() {
        // cari data di dalam SQLite
        // jika data ada maka set isFavorite = true
        if (jenisData.equals("movie")) {

            MovieItem movieItem = movieHelper.getMovieById(id);

            if (movieItem != null) {
                Log.d("detail", "favoriteState: data favorite ditemukan");
                Log.d("detail", "favoriteState: " + movieItem);

                List<MovieItem> movieItemList = new ArrayList<>();
                movieItemList.add(0, movieItem);

                if (movieItemList.isEmpty()) {
                    isFavorite = false;
                    Log.d("detail", "favoriteState: data favorite tidak ditemukan");
                } else {
                    isFavorite = true;
                }
            } else {
                isFavorite = false;
                Log.d("detail", "favoriteState: data favorite tidak ditemukan");
            }

        } else if (jenisData.equals("tv")) {

            TvItem tvItem = tvHelper.getTvById(id);

            if (tvItem != null) {
                Log.d("detail", "favoriteState: data favorite ditemukan");
                Log.d("detail", "favoriteState: " + tvItem);

                List<TvItem> tvItemList = new ArrayList<>();
                tvItemList.add(0, tvItem);

                if (tvItemList.isEmpty()) {
                    isFavorite = false;
                    Log.d("detail", "favoriteState: data favorite tidak ditemukan");
                } else {
                    isFavorite = true;
                }
            } else {
                isFavorite = false;
                Log.d("detail", "favoriteState: data favorite tidak ditemukan");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (jenisData.equals("movie")) {
            movieHelper.close();
        } else if (jenisData.equals("tv")) {
            tvHelper.close();
        }
    }
}
