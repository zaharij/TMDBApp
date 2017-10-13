package com.centaurs.tmdbapp.ui.movieslist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;

class MovieViewHolder extends RecyclerView.ViewHolder{
    private TextView movieTitleTextView;
    private TextView movieRatingTextView;
    private TextView yearCountryTextView;
    private ImageView posterImageView;
    private View itemView;

    MovieViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        movieTitleTextView = itemView.findViewById(R.id.movie_title);
        movieRatingTextView = itemView.findViewById(R.id.movie_rating);
        yearCountryTextView = itemView.findViewById(R.id.movie_year);
        posterImageView = itemView.findViewById(R.id.movie_poster);
    }

    View getItemView() {
        return itemView;
    }

    void setTitle(String title){
        movieTitleTextView.setText(title);
    }

    void setRating(String rating){
        movieRatingTextView.setText(rating);
    }

    void setAdditionalInfo(String yearCountry){
        yearCountryTextView.setText(yearCountry);
    }

    ImageView getPosterImageView(){
        return posterImageView;
    }
}
