package com.evaquint.android.fragments.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Geocoder;
import android.location.Address;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.bumptech.glide.Glide;
import com.evaquint.android.R;
import com.evaquint.android.popups.QuickEventFrag;
import com.evaquint.android.utils.dataStructures.DetailedEvent;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.evaquint.android.utils.database.EventDBHelper;
import com.evaquint.android.utils.database.GeofireDBHelper;
import com.evaquint.android.utils.storage.PhotoUploadHelper;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import android.support.v4.app.Fragment;

import static android.content.ContentValues.TAG;
import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;
import static com.evaquint.android.utils.code.IntentValues.PICK_IMAGE_REQUEST;
import static com.evaquint.android.utils.code.IntentValues.QUICK_EVENT_FRAGMENT;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class EventLocatorFrag extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    public String mTitle;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private View view;
    private Activity a;
    private PlaceAutocompleteFragment googlePlacesSearchBarFrag;
    private Fragment popupFragment;


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
            //initialize the box

            if (marker.getTag() != null &&marker.getTag().getClass()!=String.class) {
                final ImageView eventPic = (ImageView) myContentsView.findViewById(R.id.eventPic);
                PhotoUploadHelper photoUploadHelper = new PhotoUploadHelper();

                EventDB event = (EventDB) marker.getTag();
    //EventDB event= null;
                TextView test = ((TextView) myContentsView.findViewById(R.id.title));
               // EventDBHelper eventDBHelper = new EventDBHelper();
                //Log.i("data2: ", marker.getTag().toString());
                //EventDB event = eventDBHelper.retreiveEvent(marker.getTag().toString());
             //   EventDB event2 = eventDBHelper.getEvent();
                if (event == null) {
                    Log.i("event: ","null");
                    test.setText("");
                }
               /* if (event2 == null) {
                    Log.i("wrong ", " time");
                }*/
               else{
                   Log.i("event2: ", photoUploadHelper.getStorageRef().getPath()+"/events/"+event.eventID+"/0");

                    test.setText(event.eventTitle);
                                  /*   photoUploadHelper.getStorageRef().child("events").child(event.eventID).child("0").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            eventPic.setImageURI(uri);
                        }
                    });
   Glide.with(this.myContentsView.getContext())
                            .using(new FirebaseImageLoader())
                            .load(photoUploadHelper.getStorageRef().child("events").child(event.eventID).child("0"))
                            .into(eventPic);*/
                    Log.i("event2: ", "doggo");

                    //test.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                }
//            test.setText(event.eventTitle);
                //    test.setText("KKKKKKKKKKKKK FUCK YA BITCH HOMES");
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
        this.view = inflater.inflate(R.layout.fragment_event_locator_maps, container, false);
        this.a = getActivity();

        android.support.v4.app.FragmentManager fm = getFragmentManager();
        googlePlacesSearchBarFrag = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.event_maps_searchbar);

        googlePlacesSearchBarFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "error: " + status);
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
                initOverlay();
                goToCurrentLocation();
            }
        });

        return this.view;
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

        goToCurrentLocation();
        initOverlay();

    }

    public void goToCurrentLocation() {
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
            final GeoQuery surroundingEvents = helper.queryAtLocation(selfLoc, 10);
            surroundingEvents.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.latitude, location.longitude))
                            .title(key)
                            .snippet(key)
                          //  .icon(BitmapDescriptorFactory.fromResource(R.mipmap.soccerball)));
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    marker.setTag(key);
                    stickEventToMarker(marker,key);
                    Log.i("data: ", marker.getTag().toString());

                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {
                    surroundingEvents.removeAllListeners();
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
//    surroundingEvents.removeAllListeners();
        }

    }
    private void stickEventToMarker(final Marker marker, String eventID){
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

                            Log.i("datasnapshot", dataSnapshot.toString());
                            String eventID = dataSnapshot.child("eventID").getValue().toString();
                            String eventTitle  = dataSnapshot.child("eventTitle").getValue().toString();
                            String eventHost = dataSnapshot.child("eventHost").getValue().toString();
                            Calendar eventDate = Calendar.getInstance();
                            eventDate.setTimeInMillis(dataSnapshot.child("timeInMillis").getValue(long.class));
                            String address = dataSnapshot.child("address").getValue().toString();
                            LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class),dataSnapshot.child("location").child("longitude").getValue(double.class));
                            boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
                            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                            List<String> invited =  dataSnapshot.child("invited").getValue(t);
                            List<String> attendees = dataSnapshot.child("invited").getValue(t);
                            DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
                            List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);

                            EventDB event = new EventDB(eventID,eventTitle,eventHost,eventDate.getTimeInMillis(),address,location,categorizations,eventPrivate,invited,attendees,details);
                            marker.setTag(event);
                            Log.i("true: ", marker.getTag().toString());

                            //  setEvent(event);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error: ", "onCancelled", databaseError.toException());

                    }
                });/*
                ValueEventListener listener =  new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                            Log.i("data: ", "out");

                            Log.i("datasnapshot", dataSnapshot.toString());
                            String Eve
                            String eventTitle  = dataSnapshot.child("eventTitle").getValue().toString();
                            String eventHost = dataSnapshot.child("eventHost").getValue().toString();
                            Calendar eventDate = Calendar.getInstance();
                            eventDate.setTimeInMillis(dataSnapshot.child("eventDate").child("timeInMillis").getValue(long.class));
                            String address = dataSnapshot.child("address").getValue().toString();
                            LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class),dataSnapshot.child("location").child("longitude").getValue(double.class));
                            boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
                            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                            List<String> invited =  dataSnapshot.child("invited").getValue(t);
                            List<String> attendees = dataSnapshot.child("invited").getValue(t);
                            DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
                            List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);

                            EventDB event = new EventDB(eventTitle,eventHost,eventDate,address,location,categorizations,eventPrivate,invited,attendees,details);
                            marker.setTag(event);
                            Log.i("true: ", marker.getTag().toString());

                            //  setEvent(event);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error: ", "onCancelled", databaseError.toException());
                    }
                };*/
              //  ref.addListenerForSingleValueEvent(listener);
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
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                view.findViewById(R.id.event_maps_searchbar).setVisibility(View.VISIBLE);
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
                 /*   Log.d("Title", "Event: "+event_title);
                    Log.d("Title", "Location: "+location);
                    Log.d("Title", "Privacy: "+event_private);*/
                    com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    EventDB event = new EventDB(eventID,event_title, user.getUid(), event_date.getTimeInMillis(), address, location, new ArrayList<String>(), event_private, null, Arrays.asList(""), new DetailedEvent());
                    EventDBHelper eventDBHelper = new EventDBHelper();
                    GeofireDBHelper geofireDBHelper = new GeofireDBHelper();
                    Log.i("event to add: ", event.toString());
                    eventDBHelper.addEvent(event);
                    geofireDBHelper.addEventToGeofire(event);
                    eventDBHelper.addTestEvent("test", event);

                    //relocate this and add the set tag to it the event id
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(event_title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTag(event);
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
