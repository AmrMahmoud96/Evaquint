package com.evaquint.android.utils.places;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by henry on 7/24/2018.
 */



public class PlacesSearch {
//  need function to make an async call to google for json data
//    on return, take data from results and show it here
    Gson gson = new Gson();
    private static final String BASEURL = "https://maps.googleapis.com/";
    public PlacesSearch(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}


