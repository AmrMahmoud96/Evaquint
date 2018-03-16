package com.evaquint.android.popups;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.evaquint.android.utils.listeners.CustomItemClickListener;
import com.evaquint.android.utils.storage.PhotoUploadHelper;
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
  //  private CheckBox mMultiDaySwitch;
    private Button mCreateEventButton;
    private Calendar dateSelected;
    private ArrayList<ImageData> images;
    SimpleDateFormat df;
    int mHour,mMinute;
    private DatePickerDialog datePickerDialog;

    final Calendar c = Calendar.getInstance();

    private class ImageData {
        public Bitmap icon;
        public Uri uri;
    }

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
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);

        getDialog().getWindow().setLayout(width, height);

        return inflater.inflate(R.layout.fragment_popup_quick_event, container);
    }

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
     //   mMultiDaySwitch = (CheckBox) view.findViewById(R.id.multiDaySwitch);
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
        mCreateEventButton = (Button) view.findViewById(R.id.create_event_btn);

        final RecyclerView friends = ((RecyclerView)view.findViewById(R.id.friendsListView));
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("friends");

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

                mCreateEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pullFields();
                        uploadData();
                        dismiss();
                    }
                });

    }

    public void pickImages(){
        Intent intent = new Intent(); intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getTargetFragment().startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
//        getDialog().dismiss();
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
    public void pullFields(){
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
                .putExtra("invited", (Serializable) invited);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, i);
        //Log.d("Title", "Title: "+event_mult_day);
    }

    public void uploadData(){
        PhotoUploadHelper puh = new PhotoUploadHelper();
        int count = 0;
        if(images!=null){
            for (ImageData image : images) {
                puh.uploadEventImageAt(
                        getArguments().getString("eventID")
                        , image.uri,count);
                count++;
            }
        }


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

}