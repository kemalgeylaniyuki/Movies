package com.kemalgeylani.newmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kemalgeylani.newmovies.databinding.RecyclerviewMovieItemBinding;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    ArrayList<Movie> movieArrayList;

    public MovieAdapter(ArrayList<Movie> movieArrayList) {
        this.movieArrayList = movieArrayList;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewMovieItemBinding recyclerviewMovieItemBinding = RecyclerviewMovieItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MovieHolder(recyclerviewMovieItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {

        holder.binding.recyclerViewNameText.setText(movieArrayList.get(position).nameText);
        holder.binding.recyclerViewExplanationText.setText(movieArrayList.get(position).explanationText);

        byte[] imageArray = movieArrayList.get(position).image;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageArray,0, imageArray.length);
        holder.binding.recyclerViewimageView.setImageBitmap(bitmap);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String videoURL = movieArrayList.get(position).urlText;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoURL));
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    public class MovieHolder extends RecyclerView.ViewHolder{

        private RecyclerviewMovieItemBinding binding;
        private ImageView imageView;

        public MovieHolder(RecyclerviewMovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.imageView = binding.recyclerViewimageView;
        }
    }

}
