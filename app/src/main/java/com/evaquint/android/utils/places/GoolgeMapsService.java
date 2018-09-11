package com.evaquint.android.utils.places;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by henry on 7/28/2018.
 */

public interface  GoolgeMapsService {

    @GET("/maps/api/place/nearbysearch/json?")
    void getData(Callback<GoogleJson> response);
}
