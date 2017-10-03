package com.centaurs.tmdbapp.model.constants;


public class QueryConstants {
    public final static String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w";
    public static final int DEFAULT_IMAGE_SIZE_W = 150;
    public static String getBaseImageUrlStrByWidth(int width){
        return BASE_URL_IMG + width;
    }
}
