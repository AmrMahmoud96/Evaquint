package com.evaquint.android.fragments.map;

import android.Manifest;
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
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evaquint.android.R;
import com.evaquint.android.popups.QuickEventFrag;
import com.evaquint.android.utils.dataStructures.DetailedEvent;
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
import static com.evaquint.android.utils.code.IntentValues.PICK_IMAGE_REQUEST;
import static com.evaquint.android.utils.code.IntentValues.QUICK_EVENT_FRAGMENT;

public class EventLocatorFrag extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    public String mTitle;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private View view;
    private Activity a;
    private PlaceAutocompleteFragment googlePlacesSearchBarFrag;
    private Fragment popupFragment;
    private GeoQuery surroundingEvents;
    private List<Marker> currentMapMarkers;
    private Circle searchCircle;
    private Marker marker;
    private Marker googlePlaceMarker;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


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
            if(marker.getTag().equals("PlaceMarker")){
                return null;
            }
            if (marker.getTag() != null &&marker.getTag().getClass()!=String.class) {
                final ImageView eventPic = (ImageView) myContentsView.findViewById(R.id.eventPic);
                EventDB event = (EventDB) marker.getTag();
                TextView title = ((TextView) myContentsView.findViewById(R.id.title));

                try{
                    Picasso.with(myContentsView.getContext()).load(event.details.getPictures().get(0)).into(eventPic);
                    Log.i("downloaded", "true");
                }catch(Exception e){
                    Log.e("event image error", e.getMessage());
                }


                if (event == null) {
                    Log.i("event: ","null");
                    title.setText("");
                }
               else{
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

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            this.view = inflater.inflate(R.layout.fragment_event_locator_maps, container, false);
            this.a = getActivity();
            currentMapMarkers =  new ArrayList<Marker>();

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here

            }
            //searchCircle = new Circle();

            android.support.v4.app.FragmentManager fm = getFragmentManager();
            googlePlacesSearchBarFrag = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.event_maps_searchbar);

            googlePlacesSearchBarFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    if(googlePlaceMarker!=null){
                        googlePlaceMarker.remove();
                    }
                    LatLng placeLocation = place.getLatLng();
                    googlePlaceMarker = mMap.addMarker(new MarkerOptions()
                            .position(placeLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    googlePlaceMarker.setTag("PlaceMarker");
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
                    searchCircle.setCenter(placeLocation);
                    Log.i("place","Place: " + place.getName());

                    Log.i("place","Place: " + place.getAddress());
                    Log.i("place","Place: " + place.getRating());
                    Log.i("place","Place: " + place.getAttributions());
                 //   Log.i("place","Place: " + place.get);
                }

                @Override
                public void onError(Status status) {
                    Log.i(TAG, "error: " + status);
                }
            });

            // ShakeDetector initialization
            mSensorManager = (SensorManager) a.getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

                @Override
                public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                    handleShakeEvent(count);
                }
            });


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

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
//                .findFragmentById(R.id.map);
//
//        mapFragment.getMapAsync(this);
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.current_location_button);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Acquire a reference to the system Location Manager

                    goToCurrentLocation();
                }
            });
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }





        return this.view;
    }

    public void handleShakeEvent(int count) {
        if(count>=2) {
            mShakeDetector.setmShakeCount(0);
            Log.i(TAG,"YAYAYAYA");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
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
            String address = addresses.get(0).getAddressLine(0);
            Log.d("Address held", "Address: " + addresses.toString());

            FragmentManager fm = getFragmentManager();
            QuickEventFrag editNameDialogFragment = QuickEventFrag.newInstance(address, point, UUID.randomUUID().toString());
            popupFragment = editNameDialogFragment;

            editNameDialogFragment.setTargetFragment(this, QUICK_EVENT_FRAGMENT);
            editNameDialogFragment.show(fm, "fragment_popup_quick_event");

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

        if (ActivityCompat.checkSelfPermission(a, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(a, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        view.findViewById(R.id.map_searchbar_container).bringToFront();
        initOverlay();
        goToCurrentLocation();


    }
    public Marker getMarkerFromMap(String eventID){
        if(currentMapMarkers!=null){
            for(Marker m: currentMapMarkers){
                if (m.getTag() != null &&m.getTag().getClass()!=String.class) {
                    EventDB event = (EventDB) m.getTag();
                    Log.i("marker with eventid",event.eventID);
                    if(eventID.equals(event.eventID)){
                    //    Log.i("in","true");
                        return m;
                    }
                  //      Log.i("in","false");
                }else{
                    return null;
                }

            }
        }
        return null;
    }
    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
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
                (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(a, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(a, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        Location selfLocation = locationManager
                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (selfLocation != null) {
            //Move the map to the user's location
            LatLng selfLoc = new LatLng(selfLocation.getLatitude(), selfLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(selfLoc, 15);
            mMap.animateCamera(update);
            GeofireDBHelper helper = new GeofireDBHelper();
            surroundingEvents = helper.queryAtLocation(selfLoc, 1);
            surroundingEvents.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Marker mapMarker;
                    mapMarker = getMarkerFromMap(key);
                    if(mapMarker!=null){
                        mapMarker.setVisible(true);
                    }else{
                        stickEventToMarker(marker,key);
                    }

                }

                @Override
                public void onKeyExited(String key) {
                    Log.i("event has left radius",key);
                    Marker marker=getMarkerFromMap(key);
                    if(marker != null){
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
    private void stickEventToMarker(final Marker marker2, String eventID){
            //need to check if event ID exists in event db first
            if(!eventID.isEmpty()&&eventID!=null){
                Log.i("data: ", "in");
                Log.i("path ", EVENTS_TABLE.getName()+"/"+eventID);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("data: ", "out2");
                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                            Log.i("data: ", "out");

                            Log.i("datasnapshot", dataSnapshot.getValue().toString());
                            String eventID = dataSnapshot.child("eventID").getValue().toString();
                            Calendar eventDate = Calendar.getInstance();
                            long timeInMillis = dataSnapshot.child("timeInMillis").getValue(long.class);
                            if(eventDate.getTimeInMillis() > timeInMillis + 3600000) {
                                //remove event from geofire + don't show on map
                                GeofireDBHelper helper = new GeofireDBHelper();
                                helper.removeEvent(eventID);
                               // marker.remove();
                                return;
                            }
                            eventDate.setTimeInMillis(timeInMillis);
                            String eventTitle  = dataSnapshot.child("eventTitle").getValue().toString();
                            String eventHost = dataSnapshot.child("eventHost").getValue().toString();

                            String address = dataSnapshot.child("address").getValue().toString();
                            LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class),dataSnapshot.child("location").child("longitude").getValue(double.class));
                            boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
                            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                            HashMap<String, String> invited = (HashMap<String, String>) dataSnapshot.child("invited").getValue();
                            List<String> attendees = dataSnapshot.child("attendees").getValue(t);
                            DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
                            List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);

                            EventDB event = new EventDB(eventID,eventTitle,eventHost,eventDate.getTimeInMillis(),address,location,categorizations,eventPrivate,invited,attendees,details);

                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .title(eventTitle)
                                    .snippet(eventID)
                                    //  .icon(BitmapDescriptorFactory.fromResource(R.mipmap.soccerball)));
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            marker.setTag(event);
                           // marker.setTag(key);
                            currentMapMarkers.add(marker);
                            Log.i("Marker Added to map", marker.getTag().toString());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error: ", "onCancelled", databaseError.toException());

                    }
                });
            }
    }

    private void initOverlay() {
        if (ActivityCompat.checkSelfPermission(a,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(a, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(a, "Permission denied to use location",
                    Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(a,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(a,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                return;
            } else {
                ActivityCompat.requestPermissions(a,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                return;
            }
        }
        LatLng target = mMap.getCameraPosition().target;

        drawCircle(target);

        double lat = target.latitude;
        double lon = target.longitude;
        double earthR= 6371;  // earth radius in km

        double radius = 500; // km

        double x1 = lon - Math.toDegrees(radius/earthR/Math.cos(Math.toRadians(lat)));

        double x2 = lon + Math.toDegrees(radius/earthR/Math.cos(Math.toRadians(lat)));

        double y1 = lat + Math.toDegrees(radius/earthR);

        double y2 = lat - Math.toDegrees(radius/earthR);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTag() != null &&marker.getTag().getClass()!=String.class) {
                EventDB event = (EventDB) marker.getTag();
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
        LatLngBounds bounds = new LatLngBounds(new LatLng(y2,x2),new LatLng(y1,x1));
        googlePlacesSearchBarFrag.setBoundsBias(bounds);
        //googlePlacesSearchBarFrag.setBoundsBias(new LatLngBounds.Builder().include(target).build());
        //Log.i("bounds",new LatLngBounds.Builder().include(mMap.getCameraPosition().target).build().toString());
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setInfoWindowAdapter(new EventPreviewWindow());
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                view.findViewById(R.id.event_maps_searchbar).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.event_searchbar_frame).setVisibility(View.INVISIBLE);
            }
        });
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                searchCircle.setCenter(mMap.getCameraPosition().target);
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
                view.findViewById(R.id.event_searchbar_frame).setVisibility(View.VISIBLE);
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.current_location_button);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Acquire a reference to the system Location Manager
//                LocationManager locationManager =
//                        (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
//
//                if (ActivityCompat.checkSelfPermission(a, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
//                        PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(a, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                    return;
//
//                Location selfLocation = locationManager
//                        .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//
//                //Move the map to the user's location
//                LatLng selfLoc = new LatLng(selfLocation.getLatitude(), selfLocation.getLongitude());
//                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(selfLoc, 15);
//                mMap.animateCamera(update);
//            }
//        });

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
    public void uploadData(String eventID,ArrayList<ImageData> images){
        PhotoUploadHelper puh = new PhotoUploadHelper();
        int count = 0;
        if(images!=null){
            for (ImageData image : images) {
                puh.uploadEventImageAt(eventID
                        , image.uri,count);
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
                    if(description!=null){
                        int cap = bundle.getInt("capacity");
                        boolean tournMode = bundle.getBoolean("tournMode");
                        boolean QRCodes = bundle.getBoolean("QRCodes");
                        int ageRestriction = bundle.getInt("ageRestriction");
                        images = (ArrayList<ImageData>) bundle.get("images");

                        details = new DetailedEvent(description,ageRestriction,Arrays.asList(""),Arrays.asList(""),cap,tournMode);
                    }
                    else{
                        details = new DetailedEvent();
                    }
                    com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    EventDB event = new EventDB(eventID,event_title, user.getUid(), event_date.getTimeInMillis(), address, location, new ArrayList<String>(), event_private, invited, Arrays.asList(""), details);
                    EventDBHelper eventDBHelper = new EventDBHelper();
                    GeofireDBHelper geofireDBHelper = new GeofireDBHelper();
                    UserDBHelper userDBHelper = new UserDBHelper();
                    Log.i("event to add: ", event.toString());
                    eventDBHelper.addEvent(event);
                    geofireDBHelper.addEventToGeofire(event);
                    eventDBHelper.addTestEvent("test", event);
                    userDBHelper.addEventHosted(user.getUid(),eventID);
                    if(description!=null){
                        uploadData(eventID,images);
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
                popupFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initOverlay();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(a, "Permission denied to use location",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
