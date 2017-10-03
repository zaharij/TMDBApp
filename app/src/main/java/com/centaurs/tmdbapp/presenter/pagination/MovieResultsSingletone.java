package com.centaurs.tmdbapp.presenter.pagination;


import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.Result;

import java.util.ArrayList;
import java.util.List;

public class MovieResultsSingletone {
    private static MovieResultsSingletone movieResultsSingletone;
    private List<Result> movieResults;
    private MovieGenres movieGenres;

    private MovieResultsSingletone(){
        movieResults = new ArrayList<>();
    }

    public static MovieResultsSingletone getMovieResultsSingletone(){
        if (movieResultsSingletone == null){
            movieResultsSingletone = new MovieResultsSingletone();
        }
        return movieResultsSingletone;
    }

    public List<Result> getMovieResults() {
        return movieResults;
    }

    public void addMovieResult(Result movieResult) {
        this.movieResults.add(movieResult);
    }

    public void addAllMovieResults(List<Result> movieResults) {
        this.movieResults.addAll(movieResults);
    }

    public MovieGenres getMovieGenres() {
        return movieGenres;
    }

    public void setMovieGenres(MovieGenres movieGenres) {
        this.movieGenres = movieGenres;
    }
}
