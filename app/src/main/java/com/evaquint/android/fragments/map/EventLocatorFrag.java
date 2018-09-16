package com.evaquint.android.fragments.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evaquint.android.HomeActivity;
import com.evaquint.android.R;
import com.evaquint.android.fragments.EventPageFrag;
import com.evaquint.android.popups.EventSuggestionFrag;
import com.evaquint.android.popups.QuickEventFrag;
import com.evaquint.android.utils.dataStructures.DetailedEvent;
import com.evaquint.android.utils.dataStructures.EventCategories;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.evaquint.android.utils.dataStructures.ImageData;
import com.evaquint.android.utils.database.EventDBHelper;
import com.evaquint.android.utils.database.GeofireDBHelper;
import com.evaquint.android.utils.database.UserDBHelper;
import com.evaquint.android.utils.listeners.ShakeDetector;
import com.evaquint.android.utils.storage.PhotoUploadHelper;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;
import static com.evaquint.android.utils.code.IntentValues.EVENT_PAGE_FRAGMENT;
import static com.evaquint.android.utils.code.IntentValues.EVENT_SUGGESTION_FRAGMENT;
import static com.evaquint.android.utils.code.IntentValues.PICK_IMAGE_REQUEST;
import static com.evaquint.android.utils.code.IntentValues.QUICK_EVENT_FRAGMENT;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class EventLocatorFrag extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {
    private Map<String, ArrayList<String>> categories;

    public String mTitle;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private View view;
    private Activity parentActivityInstance;
    private SupportPlaceAutocompleteFragment googlePlacesSearchBarFrag;
    private Fragment popupFragment;
    private GeoQuery surroundingEvents;
    private List<Marker> currentMapMarkers;
    private Circle searchCircle;
    private Marker marker;
    private Marker googlePlaceMarker;
    private LatLng target = null;

    private SensorManager mSensorManager = null ;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    final int REQUEST_LOCATION = 1;

    private boolean isPopupOpen = false;

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }

    //    private GeoDataClient mGeoDataClient;
//    private PlaceDetectionClient mPlaceDetectionClient;
//    private FusedLocationProviderClient mFusedLocationProviderClient;
    class EventPreviewWindow implements GoogleMap.InfoWindowAdapter {

        private View myContentsView;

        public EventPreviewWindow() {
            //inflater.inflate(R.layout.fragment_event_locator_maps, container, false);
            myContentsView = getLayoutInflater().inflate(R.layout.event_preview_window, null);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (marker.getTag().equals("PlaceMarker")) {
                return null;
            }
            if (marker.getTag() != null && marker.getTag().getClass() != String.class) {
                final ImageView eventPic = (ImageView) myContentsView.findViewById(R.id.eventPic);
                EventDB event = (EventDB) marker.getTag();
                TextView title = ((TextView) myContentsView.findViewById(R.id.title));
                try {
                    String picURL = event.details.getPictures().get(0);
                    if (picURL != null && !picURL.isEmpty()) {
                        //  eventPic.setImageBitmap(getImageBitmap(picURL));
                        Picasso.with(myContentsView.getContext()).load(event.details.getPictures().get(0)).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).resize(220, 90).into(eventPic);
                    }
                    Log.i("downloaded", "true");
                } catch (Exception e) {
                    Log.e("event image error", e.getMessage());
                }

                if (event == null) {
                    Log.i("event: ", "null");
                    title.setText("");
                } else {
                    title.setText(event.eventTitle);
                }
            }
            return myContentsView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = this.view == null ?
                inflater.inflate(R.layout.fragment_event_locator_maps, container, false)
                : this.view;
        initView();
        return this.view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }

    public void setPopupOpen() {
        isPopupOpen = true;
    }

    public void setPopupClosed() {
        isPopupOpen = false;
    }

    public synchronized void handleShakeEvent(int count) {
        if (count >= 2 && !isPopupOpen) {
            Log.i(TAG, "Phone Shake Detected");
            mShakeDetector.setmShakeCount(0);
            FragmentManager fm = getFragmentManager();
            EventSuggestionFrag editNameDialogFragment = new EventSuggestionFrag();
            popupFragment = editNameDialogFragment;

            editNameDialogFragment.setTargetFragment(this, EVENT_SUGGESTION_FRAGMENT);
            editNameDialogFragment.show(fm, "fragment_popup_quick_event");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        System.out.println(mShakeDetector);
        System.out.println(mAccelerometer);
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onMapLongClick(LatLng point) {

        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this.getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            if(addresses.size()>0) {
                String address = addresses.get(0).getAddressLine(0);
                Log.d("Address held", "Address: " + addresses.toString());

                FragmentManager fm = getFragmentManager();
                QuickEventFrag editNameDialogFragment = QuickEventFrag.newInstance(address, point, UUID.randomUUID().toString());
                popupFragment = editNameDialogFragment;

                editNameDialogFragment.setTargetFragment(this, QUICK_EVENT_FRAGMENT);
                editNameDialogFragment.show(fm, "fragment_popup_quick_event");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add activity marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        // Add activity marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(parentActivityInstance, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(parentActivityInstance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            goToCurrentLocation();
        }
        initOverlay();
    }

    private void initView(){
        this.parentActivityInstance = getActivity();
        ((HomeActivity)getActivity()).enableMenuButton();
        try{
            currentMapMarkers = new ArrayList<Marker>();

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here

            }
            //searchCircle = new Circle();


//            categories = new EventCategories().getCategories();
//            Log.i("cat:",categories.toString());
//            android.support.v4.app.FragmentManager fm = getFragmentManager();
//            googlePlacesSearchBarFrag = (SupportPlaceAutocompleteFragment) fm.findFragmentById(R.id.event_maps_searchbar);
//            googlePlacesSearchBarFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                @Override
//                public void onPlaceSelected(Place place) {
//                    if (googlePlaceMarker != null) {
//                        googlePlaceMarker.remove();
//                    }
//                    LatLng placeLocation = place.getLatLng();
//                    googlePlaceMarker = mMap.addMarker(new MarkerOptions()
//                            .position(placeLocation)
//                            .title(place.getName().toString())
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//                    googlePlaceMarker.setTag("PlaceMarker");
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
//                    Log.i("place", "Place: " + place.getName());
//
//                    Log.i("place", "Place: " + place.getAddress());
//                    Log.i("place", "Place: " + place.getRating());
//                    Log.i("place", "Place: " + place.getAttributions());
//                    //   Log.i("place","Place: " + place.get);
//                }
//
//                @Override
//                public void onError(Status status) {
//                    Log.i(TAG, "error: " + status);
//                }
//            });

            initShakeSensor();

            //        mGeoDataClient = Places.getGeoDataClient(this,null);
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this,null);
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        googlePlacesSearchBarFrag = new SupportPlaceAutocompleteFragment();
//        android.support.v4.app.FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.map_searchbar_container, googlePlacesSearchBarFrag).commit();

            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_container, mapFragment)
                    .commit();

        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
    }

    public Marker getMarkerFromMap(String eventID) {
        if (currentMapMarkers != null) {
            for (Marker m : currentMapMarkers) {
                if (m.getTag() != null && m.getTag().getClass() != String.class) {
                    EventDB event = (EventDB) m.getTag();
                    Log.i("marker with eventid", event.eventID);
                    if (eventID.equals(event.eventID)) {
                        //    Log.i("in","true");
                        return m;
                    }
                    //      Log.i("in","false");
                } else {
                    return null;
                }

            }
        }
        return null;
    }

    private void drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw parentActivityInstance circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(1000);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        searchCircle = mMap.addCircle(circleOptions);

    }


    public void goToCurrentLocation() {
        // String[] categories = getResources().getStringArray(R.array.event_categories);
        // String search = categories[0]+"_"+"subcategories";
        // Class<R.array> categories = R.array.event_categories;
        // Class<R.array> cat = R.array.event_categories;

        //String[] musicSubCategories = getResources();
        // Log.d("this is cat array",Arrays.toString(categories));
        //Log.d("this is cat array",Arrays.toString(musicSubCategories));

        LocationManager locationManager =
                (LocationManager) parentActivityInstance.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(parentActivityInstance,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(parentActivityInstance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(parentActivityInstance, "Permission denied to use location",
                    Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(parentActivityInstance,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
                return;
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
                return;
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        final Location selfLocation = locationManager
                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (selfLocation != null) {
            //Move the map to the user's location
            final LatLng selfLoc = new LatLng(selfLocation.getLatitude(), selfLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(selfLoc, 15);

            target = mMap.getCameraPosition().target;

            double lat = target.latitude;
            double lon = target.longitude;
            double earthR = 6371;  // earth radius in km

            double radius = 500; // km

            double x1 = lon - Math.toDegrees(radius / earthR / Math.cos(Math.toRadians(lat)));
            double x2 = lon + Math.toDegrees(radius / earthR / Math.cos(Math.toRadians(lat)));
            double y1 = lat + Math.toDegrees(radius / earthR);
            double y2 = lat - Math.toDegrees(radius / earthR);

            LatLngBounds bounds = new LatLngBounds(new LatLng(y2, x2), new LatLng(y1, x1));
            googlePlacesSearchBarFrag.setBoundsBias(bounds);


            mMap.animateCamera(update);
            if (surroundingEvents != null) {
                surroundingEvents.setCenter(new GeoLocation(selfLoc.latitude, selfLoc.longitude));
            } else {
                GeofireDBHelper helper = new GeofireDBHelper("events");
                surroundingEvents = helper.queryAtLocation(selfLoc, 1);
                surroundingEvents.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        Marker mapMarker;
                        mapMarker = getMarkerFromMap(key);
                        searchCircle.setCenter(selfLoc);
                        if (mapMarker != null) {
                            mapMarker.setVisible(true);
                        } else {
                            stickEventToMarker(marker, key);
                        }

                    }

                    @Override
                    public void onKeyExited(String key) {
                        Log.i("event has left radius", key);
                        Marker marker = getMarkerFromMap(key);
                        if (marker != null) {
                            marker.setVisible(false);
                        }
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });


            }

        }

    }

    private void stickEventToMarker(final Marker marker2, String eventID) {
        //need to check if event ID exists in event db first
        if (!eventID.isEmpty() && eventID != null) {
            Log.i("data: ", "in");
            Log.i("path ", EVENTS_TABLE.getName() + "/" + eventID);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    new Thread() {
                        public void run() {
                            try {
                                Log.i("data: ", "out2");
                                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                    Log.i("data: ", "out");

                                    Log.i("datasnapshot", dataSnapshot.getValue().toString());
                                    String eventID = dataSnapshot.child("eventID").getValue().toString();
                                    Calendar eventDate = Calendar.getInstance();
                                    long timeInMillis = dataSnapshot.child("timeInMillis").getValue(long.class);
                                    if (eventDate.getTimeInMillis() > timeInMillis + 3600000) {
                                        //remove event from geofire + don't show on map
                                        GeofireDBHelper helper = new GeofireDBHelper("events");
                                        helper.removeEvent(eventID);
                                        // marker.remove();
                                        return;
                                    }
                                    eventDate.setTimeInMillis(timeInMillis);
                                    String eventTitle = dataSnapshot.child("eventTitle").getValue().toString();
                                    String eventHost = dataSnapshot.child("eventHost").getValue().toString();

                                    String address = dataSnapshot.child("address").getValue().toString();
                                    LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class), dataSnapshot.child("location").child("longitude").getValue(double.class));
                                    boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
                                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                                    };
                                    HashMap<String, String> invited = (HashMap<String, String>) dataSnapshot.child("invited").getValue();
                                    List<String> attendees = dataSnapshot.child("attendees").getValue(t);
                                    DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
                                    List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);

                                    final EventDB event = new EventDB(eventID, eventTitle, eventHost, eventDate.getTimeInMillis(), address, location, categorizations, eventPrivate, invited, attendees, details);

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(event.location.latitude, event.location.longitude))
                                                    .title(event.eventTitle)
                                                    .snippet(event.eventID)
                                                    //  .icon(BitmapDescriptorFactory.fromResource(R.mipmap.soccerball)));
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                            marker.setTag(event);
                                            // marker.setTag(key);
                                            currentMapMarkers.add(marker);
                                            Log.i("Marker Added to map", marker.getTag().toString());
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());

                }
            });
        }
    }

    private void initShakeSensor() {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) parentActivityInstance.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                handleShakeEvent(count);
            }
        });
    }

    private void initOverlay() {
       /*String [] u = {"hWA6z2zZlabzlB1UnjZYheUFcsz2","toGErwcycwN9jCUvV936GQD8yiF2","vtEKNdiODFXmNkXa0HT5gMF66oq1","yR0EmwzUAxZ7VZZTEUaI6TJjD443"};
        double[] la = {43.6573415,37.41571395227373,37.41571395227373,43.6573415};
        double[] lo = {-79.6010352,-122.08525277674198,-122.08525277674198,-79.6010352};
        GeofireDBHelper h = new GeofireDBHelper("users");
        for (int i = 0; i < 4; i ++){
            h.addUserToGeofire(u[i],new LatLng(la[i],lo[i]));
        }*/

        target = mMap.getCameraPosition().target;

        drawCircle(target);

        double lat = target.latitude;
        double lon = target.longitude;
        double earthR = 6371;  // earth radius in km

        double radius = 500; // km

        double x1 = lon - Math.toDegrees(radius / earthR / Math.cos(Math.toRadians(lat)));
        double x2 = lon + Math.toDegrees(radius / earthR / Math.cos(Math.toRadians(lat)));
        double y1 = lat + Math.toDegrees(radius / earthR);
        double y2 = lat - Math.toDegrees(radius / earthR);

        LatLngBounds bounds = new LatLngBounds(new LatLng(y2, x2), new LatLng(y1, x1));
        //googlePlacesSearchBarFrag.setBoundsBias(bounds);

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setInfoWindowAdapter(new EventPreviewWindow());

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                view.findViewById(R.id.event_maps_searchbar).setVisibility(View.INVISIBLE);
//                view.findViewById(R.id.event_searchbar_frame).setVisibility(View.INVISIBLE);
            }
        });
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
//                searchCircle.setCenter(mMap.getCameraPosition().target);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng loc = mMap.getCameraPosition().target;

//                Log.i("idle location",loc.toString());
                if(surroundingEvents!=null){

                    surroundingEvents.setCenter(new GeoLocation(loc.latitude,loc.longitude));
                }
                view.findViewById(R.id.event_maps_searchbar).setVisibility(View.VISIBLE);
//                view.findViewById(R.id.event_searchbar_frame).setVisibility(View.VISIBLE);
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTag() != null && marker.getTag().getClass() != String.class) {
                    EventDB event = (EventDB) marker.getTag();
                    FragmentManager fm = getFragmentManager();
                    EventPageFrag eventPageFragment = EventPageFrag.newInstance(event);
                    popupFragment = eventPageFragment;

                    setActiveFragment(fm, eventPageFragment);
//                    eventPageFragment.setTargetFragment(getParentFragment(),EVENT_PAGE_FRAGMENT);
//
//                    setActiveFragment(getSupportFragmentManager(), new UserProfileFrag());
//                    eventPageFragment.show();
                   // eventPageFragment.


                  /*  EventPageFrag eventPageFragment = new EventPageFrag(event);


                    EventSuggestionFrag editNameDialogFragment = new EventSuggestionFrag();
                    popupFragment = editNameDialogFragment;

                    editNameDialogFragment.setTargetFragment(this, EVENT_SUGGESTION_FRAGMENT);
                    editNameDialogFragment.show(fm, "fragment_popup_quick_event");*/

                    //Bundle bundle;
                    //bundle.put
                 /*   FragmentTransaction ft = getFragmentManager().beginTransaction();
                    EventPageFrag fragment = EventPageFrag.newInstance(event);
                   // ft.replace(R.i, fragment);
                    ft.commit();
                    */
                }
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.current_location_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCurrentLocation();
            }
        });

//        googlePlacesSearchBarFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
    }

    public void uploadData(String eventID, ArrayList<ImageData> images) {
        PhotoUploadHelper puh = new PhotoUploadHelper();
        int count = 0;
        if (images != null) {
            for (ImageData image : images) {
                puh.uploadEventImageAt(eventID
                        , image.uri, count);
                count++;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case QUICK_EVENT_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String eventID = bundle.getString("eventID");
                    String event_title = bundle.getString("title");
                    Boolean event_private = bundle.getBoolean("privacy");
                    Calendar event_date = (Calendar) bundle.get("event_date");
                    String address = bundle.getString("address");
                    LatLng location = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
                    Map<String, String> invited = (Map<String, String>) bundle.getSerializable("invited");
                    DetailedEvent details;
                    ArrayList<ImageData> images = new ArrayList<>();
                    String description = bundle.getString("description");
                    if (description != null) {
                        int cap = bundle.getInt("capacity");
                        boolean tournMode = bundle.getBoolean("tournMode");
                        boolean QRCodes = bundle.getBoolean("QRCodes");
                        int ageRestriction = bundle.getInt("ageRestriction");
                        images = (ArrayList<ImageData>) bundle.get("images");

                        details = new DetailedEvent(description, ageRestriction, Arrays.asList(""), Arrays.asList(""), cap, tournMode);
                    } else {
                        details = new DetailedEvent();
                    }
                    com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    EventDB event = new EventDB(eventID, event_title, user.getUid(), event_date.getTimeInMillis(), address, location, new ArrayList<String>(Arrays.asList(user.getUid())), event_private, invited, Arrays.asList(user.getUid()), details);
                    EventDBHelper eventDBHelper = new EventDBHelper();
                    GeofireDBHelper geofireDBHelper = new GeofireDBHelper("events");
                    UserDBHelper userDBHelper = new UserDBHelper();
                    Log.i("event to add: ", event.toString());
                    eventDBHelper.addEvent(event);
                    geofireDBHelper.addEventToGeofire(event);
                    eventDBHelper.addTestEvent("test", event);
                    userDBHelper.addEventHosted(user.getUid(), eventID);
                    if (description != null) {
                        uploadData(eventID, images);
                    }
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(event_title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    marker.setTag(event);
                    currentMapMarkers.add(marker);
                    // geofireDBHelper.queryAtLocation(event.location,10);


                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //do nothing
                }
                break;
            case PICK_IMAGE_REQUEST:
                popupFragment.onActivityResult(requestCode,  resultCode, data);
                break;
            case EVENT_SUGGESTION_FRAGMENT:
                isPopupOpen = false;
                break;
            case EVENT_PAGE_FRAGMENT:
                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initOverlay();
                    goToCurrentLocation();

                } else {
                    Toast.makeText(parentActivityInstance, "Permission denied to use location",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
