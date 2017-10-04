package com.centaurs.tmdbapp.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;

public class MovieViewHolder extends RecyclerView.ViewHolder{
    private TextView movieTitleTextView;
    private TextView movieRatingTextView;
    private TextView yearCountryTextView;
    private ImageView posterImageView;
    private ProgressBar progressBar;
    private View itemView;

    MovieViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        movieTitleTextView = itemView.findViewById(R.id.movie_title);
        movieRatingTextView = itemView.findViewById(R.id.movie_rating);
        yearCountryTextView = itemView.findViewById(R.id.movie_year);
        posterImageView = itemView.findViewById(R.id.movie_poster);
        progressBar = itemView.findViewById(R.id.movie_progress);
    }

    public View getItemView() {
        return itemView;
    }

    public void setTitle(String title){
        movieTitleTextView.setText(title);
    }

    public void setRating(String rating){
        movieRatingTextView.setText(rating);
    }

    public void setAdditionalInfo(String yearCountry){
        yearCountryTextView.setText(yearCountry);
    }

    public ImageView getPosterImageView(){
        return posterImageView;
    }

    public void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }
}
