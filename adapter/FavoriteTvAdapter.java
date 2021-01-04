package com.blogspot.thirkazh.moviecatalogue.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.model.tv.TvItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavoriteTvAdapter extends RecyclerView.Adapter<FavoriteTvAdapter.Holder> {

    private ArrayList<TvItem> listData = new ArrayList<>();
    private Context context;
    private OnItemClickCallback onItemClickCallback;

    public ArrayList<TvItem> getListTv() {
        return listData;
    }

    public FavoriteTvAdapter(Context activity) {
        context = activity;
    }

    public void setListTv(ArrayList<TvItem> listTv) {

        if (listTv.size() > 0) {
            this.listData.clear();
        }
        this.listData.addAll(listTv);

        notifyDataSetChanged();
    }

    public void addItem(TvItem note) {
        this.listData.add(note);
        notifyItemInserted(listData.size() - 1);
    }

    public void updateItem(int position, TvItem note) {
        this.listData.set(position, note);
        notifyItemChanged(position, note);
    }

    public void removeItem(int position) {
        this.listData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,listData.size());
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fav_tv, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        holder.titleFavTv.setText(listData.get(i).getName());
        holder.yearFavTv.setText(listData.get(i).getFirstAirDate());

        String baseUrlImage = "https://image.tmdb.org/t/p/original";
        Glide.with(context).load(baseUrlImage + listData.get(i).getPosterPath())
                .into(holder.posterFavTv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(listData.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView titleFavTv, yearFavTv;
        ImageView posterFavTv;

        Holder(@NonNull View itemView) {
            super(itemView);

            titleFavTv = itemView.findViewById(R.id.title_fav_tv);
            yearFavTv = itemView.findViewById(R.id.year_fav_tv);
            posterFavTv = itemView.findViewById(R.id.poster_fav_tv);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(TvItem data);
    }
}
