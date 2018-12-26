package com.evaquint.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.evaquint.android.utils.Network.GooglePlacesQueue;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestGooglePlacesRequest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.evaquint.evaquint", appContext.getPackageName());
        LatLng latLng = new LatLng(-33.8670522,151.1957362);
        GooglePlacesQueue mGooglePlacesQueue = GooglePlacesQueue.getInstance(appContext);
        mGooglePlacesQueue.sendPlacesRequest(latLng, null,1500, "cruise");
        assertEquals(1, 2);

    }
}