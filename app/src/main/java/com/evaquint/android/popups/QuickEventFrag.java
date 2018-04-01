    package com.evaquint.android.popups;

    import android.Manifest;
    import android.app.DatePickerDialog;
    import android.app.TimePickerDialog;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.content.res.Resources;
    import android.graphics.Bitmap;
    import android.os.Build;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.support.annotation.Nullable;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.app.DialogFragment;
    import android.support.v7.widget.LinearLayoutManager;
    import android.support.v7.widget.RecyclerView;
    import android.util.Log;
    import android.util.TypedValue;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.ViewGroup.LayoutParams;
    import android.widget.Button;
    import android.widget.DatePicker;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.Switch;
    import android.widget.TextView;
    import android.widget.TimePicker;
    import android.widget.Toast;

    import com.evaquint.android.R;
    import com.evaquint.android.utils.Adapter.FriendsListAdapter;
    import com.evaquint.android.utils.dataStructures.ImageData;
    import com.evaquint.android.utils.listeners.CustomItemClickListener;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.GenericTypeIndicator;
    import com.google.firebase.database.ValueEventListener;

    import java.io.Serializable;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.HashMap;
    import java.util.List;

    import static android.app.Activity.RESULT_CANCELED;
    import static android.app.Activity.RESULT_OK;
    import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;
    import static com.evaquint.android.utils.code.IntentValues.PICK_IMAGE_REQUEST;

    /**
     * Created by henry on 11/27/2017.
     */


    public class QuickEventFrag extends DialogFragment {

        private TextView mLocationText;
        private HashMap<String, String> invited;
        private TextView mTimeText;
        private EditText mEventTitle;
        private ImageView mEventPicture;
        private Switch mPrivateSwitch;
        private ImageView mCalendarBtn;
        private LinearLayout mGalleryView;
        private Switch mAgeRestriction;
        private Switch mQRCodes;
        private Switch mTournamentMode;
        private EditText eventAgeRestriction;
        private EditText eventDescription;
        private EditText capacity;
        private Button mCreateEventButton;
        private Calendar dateSelected;
        private ArrayList<ImageData> images;
        private boolean moreDetails=false;
        private TextView mMoreDetails;

        SimpleDateFormat df;
        int mHour,mMinute;
        private DatePickerDialog datePickerDialog;

        final Calendar c = Calendar.getInstance();

        final int REQUEST_GALLERY = 1;

        public QuickEventFrag() {
            // Empty constructor is required for DialogFragment
            // Make sure not to add arguments to the constructor
            // Use `newInstance` instead as shown below
        }

        public static QuickEventFrag newInstance(String address, LatLng location,String eventID) {
            QuickEventFrag frag = new QuickEventFrag();
            Bundle args = new Bundle();
            args.putString("address", address);
            args.putDouble("latitude", location.latitude);
            args.putDouble("longitude",location.longitude);
            args.putString("eventID",eventID);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           // dismiss();
            int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
            int height = (int)(getResources().getDisplayMetrics().heightPixels*0.75);

            getDialog().getWindow().setLayout(width, height);

            return inflater.inflate(R.layout.fragment_popup_quick_event, container);
        }
    /*
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState = pullFields().getExtras();
        }

        @Override
        public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
            super.onViewStateRestored(savedInstanceState);
            if(savedInstanceState!=null){
                mEventTitle.setText(savedInstanceState.getString("event_title"));
                getArguments().putString("eventID",savedInstanceState.getString("eventID"));
                mPrivateSwitch.setChecked(savedInstanceState.getBoolean("privacy"));
                dateSelected = (Calendar) savedInstanceState.get("event_date");
                mTimeText.setText(df.format(dateSelected.getTime()));
                getArguments().putString("address",savedInstanceState.getString("address"));
                getArguments().putDouble("latitude",savedInstanceState.getDouble("latitude"));
                getArguments().putDouble("longitude",savedInstanceState.getDouble("longitude"));
                invited = (HashMap<String, String>) savedInstanceState.getSerializable("invited");
                moreDetails = savedInstanceState.getBoolean("moreDetails");
                eventDescription.setText(savedInstanceState.getString("description"));
                capacity.setText(savedInstanceState.getInt("capacity"));
                mTournamentMode.setChecked(savedInstanceState.getBoolean("tournMode"));
                mQRCodes.setChecked(savedInstanceState.getBoolean("QRCodes"));
                eventAgeRestriction.setText(savedInstanceState.getInt("ageRestriction"));
                images = (ArrayList<ImageData>) savedInstanceState.get("images");
            }
        }*/

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
          //  dismiss();
            // Get field from view
            mLocationText = (TextView) view.findViewById(R.id.event_location_field);
            mTimeText = (TextView) view.findViewById(R.id.event_time);
            mEventTitle = (EditText) view.findViewById(R.id.event_title_field);
            mPrivateSwitch = (Switch) view.findViewById(R.id.private_switch);
            mGalleryView = (LinearLayout) view.findViewById(R.id.id_gallery);
            // Fetch arguments from bundle and set title
            String address = getArguments().getString("address");
            mLocationText.setText(address);
            mCalendarBtn = (ImageView) view.findViewById(R.id.calendarBtn);
            mEventPicture = (ImageView) view.findViewById(R.id.eventImageBtn);
            mMoreDetails = (TextView) view.findViewById(R.id.moreDetails);

            mCreateEventButton = (Button) view.findViewById(R.id.create_event_btn);

            mAgeRestriction = (Switch) view.findViewById(R.id.ageRestriction);
            mQRCodes = (Switch) view.findViewById(R.id.QRCodeSwitch);
            mTournamentMode = (Switch) view.findViewById(R.id.tournamentMode);
            eventAgeRestriction = (EditText) view.findViewById(R.id.eventAgeRestriction);
            eventDescription= (EditText) view.findViewById(R.id.eventDescription);
            capacity =  (EditText) view.findViewById(R.id.capacity);

            mMoreDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventAgeRestriction.setVisibility(View.INVISIBLE);
                    int visibility;
                    if(!moreDetails){
                        moreDetails = true;
                        mMoreDetails.setText("Remove extra details");
                        visibility = View.VISIBLE;
                        eventAgeRestriction.setVisibility(mAgeRestriction.isChecked()? View.VISIBLE:View.INVISIBLE);
                    }else{
                        moreDetails = false;
                        mMoreDetails.setText("Add more details");
                        visibility = View.INVISIBLE;
                    }
                    mAgeRestriction.setVisibility(visibility);
                    mQRCodes.setVisibility(visibility);
                    mTournamentMode.setVisibility(visibility);
                    eventDescription.setVisibility(visibility);
                    capacity.setVisibility(visibility);
                    mEventPicture.setVisibility(visibility);
                    mGalleryView.setVisibility(visibility);
                }
            });
            int visibility = View.INVISIBLE;
            mAgeRestriction.setVisibility(visibility);
            mQRCodes.setVisibility(visibility);
            mTournamentMode.setVisibility(visibility);
            eventDescription.setVisibility(visibility);
            capacity.setVisibility(visibility);
            eventAgeRestriction.setVisibility(visibility);
            mEventPicture.setVisibility(visibility);
            mGalleryView.setVisibility(visibility);

            mAgeRestriction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(eventAgeRestriction.getVisibility()==View.INVISIBLE){
                        eventAgeRestriction.setVisibility(View.VISIBLE);
                    }else{
                        eventAgeRestriction.setVisibility(View.INVISIBLE);
                    }
                }
            });
            mTournamentMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if user is < 1000 host rating, dont allow
                    mTournamentMode.setChecked(false);
                    Toast.makeText(getActivity(),"Need 1000 host rating to host a tournament.",Toast.LENGTH_SHORT).show();
                }
            });

            invited = new HashMap<String,String>();
            dateSelected = Calendar.getInstance();
            df = new SimpleDateFormat("E, MMM d, yyyy hh:mm aa");

            mEventPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickImages();
                }
            });

            mCalendarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //inflate calendar.
                    Log.w("ContentValues","Calendar Clicked.");
                    pickDate();
    //                pickImages();
                }
            });

            final RecyclerView friends = ((RecyclerView)view.findViewById(R.id.friendsListView));
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("friends");
            new Thread() {
                public void run() {
                    try {
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null) {
                                            Log.i("friends", dataSnapshot.toString());
                                            LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
                                            friends.setLayoutManager(layoutManager);
                                            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                                            final List<String> friendsList = dataSnapshot.getValue(t);
                                            FriendsListAdapter f = new FriendsListAdapter(friendsList.toArray(new String[0]), new CustomItemClickListener() {
                                                @Override
                                                public void onItemClick(View v, int position) {
                                                    String userID = friendsList.get(position);
                                                    Log.i("invite friend",userID);
                                                    if(invited.get(userID)!=null){
                                                        invited.remove(userID);
                                                    }else{
                                                        invited.put(userID,userID);
                                                    }
                                                    //   Log.i("invited",invited.toString());
                                                }
                                            });
                                            friends.setAdapter(f);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }.start();

                    mCreateEventButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(validateFields()){
                                Intent i = pullFields();
                                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, i);
                                dismiss();
                            }

                        }
                    });

        }

        public void pickImages(){
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_GALLERY);
                return;
            }

            Intent intent = new Intent(); intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getTargetFragment().startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        }

        public void pickDate(){
            Calendar newCalendar = dateSelected;
            datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dateSelected.set(year, monthOfYear, dayOfMonth);
                    if(checkValidDate()){
                        pickTime();
                    }else{
                        Toast.makeText(getActivity(),"Please select a valid date.",Toast.LENGTH_SHORT).show();
                        pickDate();
                    }
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            datePickerDialog.show();
            //mTimeText.setText(df.format(dateSelected.getTime()));
        }
        public boolean checkValidDate(){
            if(dateSelected.getTimeInMillis()>c.getTimeInMillis()-1000){
                return true;
            }else{
                return false;
            }
        }
        public boolean validateFields(){
            // need to add string values for these messages
            View focusView=null;
            if(mEventTitle.getText().toString().trim().isEmpty()){
                mEventTitle.setError("Please enter an event title.");
                focusView = mEventTitle;
            }
            if(mTimeText.getText().toString().isEmpty()){
               // mTimeText.setError("Please select an event time.");
                Toast.makeText(getActivity(),"Please select an event time.",Toast.LENGTH_SHORT).show();
                focusView = mTimeText;
            }
           /* if(moreDetails){
                if(eventDescription.getText().toString().trim().isEmpty()){
                    eventDescription.setError("Please enter an event description.");
                }
            }*/


            if(focusView != null){
                focusView.requestFocus();
                return false;
            }
            return true;
        }
        public void pickTime(){
            // Get Current Time selected
            mHour = dateSelected.get(Calendar.HOUR_OF_DAY);
            mMinute = dateSelected.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            dateSelected.set(dateSelected.get(Calendar.YEAR),dateSelected.get(Calendar.MONTH),dateSelected.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                            if(checkValidDate()){
                                mTimeText.setText(df.format(dateSelected.getTime()));
                            }else{
                                Toast.makeText(getActivity(),"Please select a valid time.",Toast.LENGTH_SHORT).show();
                                pickTime();
                            }

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        public Intent pullFields(){
            String event_title = mEventTitle.getText().toString().trim();
            //String location = mLocationText.getText().toString().trim();
            Boolean event_private = mPrivateSwitch.isChecked();

           // Boolean event_mult_day = mMultiDaySwitch.isChecked();
            //MapActivity.setFocusToView(getView().findViewById();
            Intent i = new Intent()
                    .putExtra("title", event_title)
                    .putExtra("eventID",getArguments().getString("eventID"))
                    .putExtra("privacy", event_private)
                    .putExtra("event_date",dateSelected)
                    .putExtra("address", getArguments().getString("address"))
                    .putExtra("latitude", getArguments().getDouble("latitude"))
                    .putExtra("longitude", getArguments().getDouble("longitude"))
                    .putExtra("invited", (Serializable) invited)
                    .putExtra("moreDetails",moreDetails);

            if(moreDetails){
                String description = eventDescription.getText().toString();
                int cap=0;
                if(!capacity.getText().toString().isEmpty()){
                     cap = Integer.parseInt(capacity.getText().toString());
                }
                boolean tournMode = mTournamentMode.isChecked();
                boolean QRCodes = mQRCodes.isChecked();
                int ageRestriction = 0;
                if(!eventAgeRestriction.getText().toString().isEmpty()) {
                    ageRestriction = Integer.parseInt(eventAgeRestriction.getText().toString());
                }
                i.putExtra("description",description)
                        .putExtra("capacity",cap)
                        .putExtra("tournMode",tournMode)
                        .putExtra("QRCodes",QRCodes)
                        .putExtra("ageRestriction",ageRestriction)
                        .putExtra("images",images);
            }
            return i;
            //Log.d("Title", "Title: "+event_mult_day);
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && data.getClipData() != null) {
                        int numberOfImages = data.getClipData().getItemCount();
                        images = new ArrayList();
                        mGalleryView.removeAllViews();
                        for (int i = 0; i < numberOfImages; i++) {
                            try {
                                ImageData imageData = new ImageData();
                                imageData.uri = data.getClipData().getItemAt(i).getUri();

                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageData.uri);
                                imageData.icon = Bitmap.createScaledBitmap(bitmap, getPixelsFromDP(75), getPixelsFromDP(75), false);

                                addView(imageData.icon);
                                images.add(imageData);
                            } catch (Exception e) {
                                Log.e("Pick Image Failed With:", e.getMessage());
                            }
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && data != null) {
                        images = new ArrayList();
                        mGalleryView.removeAllViews();
                        try {
                            ImageData imageData = new ImageData();
                            imageData.uri = data.getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageData.uri);
                            imageData.icon = Bitmap.createScaledBitmap(bitmap, getPixelsFromDP(75), getPixelsFromDP(75), false);

                            addView(imageData.icon);
                            images.add(imageData);
                        } catch (Exception e) {
                            Log.e("Pick Image Failed With:", e.getMessage());
                        }
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }

        private void addView(Bitmap bm) {
            //ImageView Setup
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageBitmap(bm);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            mGalleryView.addView(imageView);
        }

        private int getPixelsFromDP(float dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,  Resources.getSystem().getDisplayMetrics());
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case REQUEST_GALLERY: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        pickImages();
                    } else {
                        Toast.makeText(getContext(), "Permission denied to read storage",
                                Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            }
        }
    }