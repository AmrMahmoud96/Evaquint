package com.evaquint.android.popups;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import  android.widget.TimePicker;
import android.widget.Toast;

import com.evaquint.android.R;
import com.google.android.gms.maps.model.LatLng;

import static com.evaquint.android.utils.code.IntentValues.PICK_IMAGE_REQUEST;

/**
 * Created by henry on 11/27/2017.
 */


public class QuickEventFrag extends DialogFragment {

    private TextView mLocationText;
    private TextView mTimeText;
    private EditText mEventTitle;
    private ImageView mEventPicture;
    private Switch mPrivateSwitch;
    private ImageView mCalendarBtn;
  //  private CheckBox mMultiDaySwitch;
    private Button mCreateEventButton;
    private Calendar dateSelected;
    SimpleDateFormat df;
    int mHour,mMinute;
    private DatePickerDialog datePickerDialog;

    final Calendar c = Calendar.getInstance();


    public QuickEventFrag() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static QuickEventFrag newInstance(String address, LatLng location) {
        QuickEventFrag frag = new QuickEventFrag();
        Bundle args = new Bundle();
        args.putString("address", address);
        args.putDouble("latitude", location.latitude);
        args.putDouble("longitude",location.longitude);
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
        // Fetch arguments from bundle and set title
        String address = getArguments().getString("address");
        mLocationText.setText(address);
        mCalendarBtn = (ImageView) view.findViewById(R.id.calendarBtn);
        mEventPicture = (ImageView) view.findViewById(R.id.eventImageBtn);
     //   mMultiDaySwitch = (CheckBox) view.findViewById(R.id.multiDaySwitch);

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
//                pickDate();
                pickImages();
            }
        });
        mEventPicture.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                  //  chooseImage();
                                             }
                                         }
        );
        mCreateEventButton = (Button) view.findViewById(R.id.create_event_btn);

        mCreateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullFields();
                dismiss();
            }
        });

    }

    public void pickImages(){
        Intent intent = new Intent(); intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
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
                .putExtra("privacy", event_private)
                .putExtra("event_date",dateSelected)
                .putExtra("address", getArguments().getString("address"))
                .putExtra("latitude", getArguments().getDouble("latitude"))
                .putExtra("longitude", getArguments().getDouble("longitude"));
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        //Log.d("Title", "Title: "+event_mult_day);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}