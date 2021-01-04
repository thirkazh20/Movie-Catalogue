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
import java.util.List;

public class TvAdapter extends RecyclerView.Adapter<TvAdapter.Holder> {

    private List<TvItem> listData = new ArrayList<>();
    private Context context;

    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void setListData(List<TvItem> listData) {
        this.listData.clear();
        this.listData.addAll(listData);
        notifyDataSetChanged();
    }

    public TvAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tvshow, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        holder.titleTv.setText(listData.get(i).getName());
        holder.yearTv.setText(listData.get(i).getFirstAirDate());

        String baseUrlImage = "https://image.tmdb.org/t/p/original";
        Glide.with(context).load(baseUrlImage + listData.get(i).getPosterPath())
                .into(holder.posterTv);

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
        TextView titleTv, yearTv;
        ImageView posterTv;

        Holder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.title_tv);
            yearTv = itemView.findViewById(R.id.year_tv);
            posterTv = itemView.findViewById(R.id.poster_tv);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(TvItem data);
    }
}
