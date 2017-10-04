package com.centaurs.tmdbapp.model;

import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.Result;

import java.util.ArrayList;
import java.util.List;

public class MovieResultsSingleton {
    private static MovieResultsSingleton movieResultsSingleton;
    private List<Result> movieResults;
    private MovieGenres movieGenres;

    private MovieResultsSingleton(){
        movieResults = new ArrayList<>();
    }

    public static MovieResultsSingleton getMovieResultsSingleton(){
        if (movieResultsSingleton == null){
            movieResultsSingleton = new MovieResultsSingleton();
        }
        return movieResultsSingleton;
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
