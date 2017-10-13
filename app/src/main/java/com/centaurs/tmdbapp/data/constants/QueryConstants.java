package com.centaurs.tmdbapp.data.constants;


public class QueryConstants {
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w";

    public final static String BASE_URL = "https://api.themoviedb.org/3/";
    public static final int DEFAULT_IMAGE_SIZE_W = 150;
    public static String getBaseImageUrlStrByWidth(int width){
        return BASE_URL_IMG + width;
    }
}
