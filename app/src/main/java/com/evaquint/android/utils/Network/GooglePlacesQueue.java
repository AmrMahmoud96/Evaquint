package com.evaquint.android.utils.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.evaquint.android.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;


public class GooglePlacesQueue {
    private static GooglePlacesQueue mInstance;
    private static Context mCtx;
    private static String tag = "GooglePlacesQueue";
    private static String placesNearbyUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static String urlFormat = "%s?location=%2f,%2f&radius=%d&type=%s&keyword=%s&key=%s";
    private RequestQueue mRequestQueue;

    private GooglePlacesQueue(Context context) {
//        // Instantiate the cache
//        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
//        // Set up the network to use HttpURLConnection as the HTTP client.
//        Network network = new BasicNetwork(new HurlStack());
//        // Instantiate the RequestQueue with the cache and network.
//        mRequestQueue = new RequestQueue(cache, network);
//        // Start the queue
//        mRequestQueue.start();
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public static synchronized GooglePlacesQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GooglePlacesQueue(context);
        }
        return mInstance;
    }

    public void sendPlacesRequest(final JSONPopulate callback_instance, LatLng location, String type, int radius, final String keyword) {
        String url = String.format(urlFormat, placesNearbyUrl, location.latitude, location.longitude,
                radius, type==null?"":type, keyword, mCtx.getString(R.string.google_places_key));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type type = new TypeToken<Map<String, String>>(){}.getType();
                        JSONArray res_array = null;
                        try {
                            res_array = (JSONArray) (new JSONObject(response)).get("results");
                        } catch (JSONException e) {
                            Log.e(tag, "Invalid JSON response");
                        }
                        callback_instance.populateJSON(res_array,keyword);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(tag, error.toString());
                    }
                });
        // Add the request to the RequestQueue.
        getRequestQueue().add(stringRequest);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}