package com.evaquint.android.popups;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import  android.widget.TimePicker;
import android.app.TimePickerDialog;
import java.text.DateFormat;

import com.evaquint.android.MapActivity;
import com.evaquint.android.R;
import com.evaquint.android.fragments.map.EventLocatorFrag;

import java.sql.Time;

/**
 * Created by henry on 11/27/2017.
 */


public class QuickEventFrag extends DialogFragment {

    private TextView mLocationText;
    private TextView mTimeText;
    private EditText mEventTitle;
    private Switch mPrivateSwitch;
    private ImageView mCalendarBtn;
  //  private CheckBox mMultiDaySwitch;
    private Button mCreateEventButton;
    private Calendar dateSelected;
    SimpleDateFormat df;
    int mHour,mMinute;
    private DatePickerDialog datePickerDialog;


    public QuickEventFrag() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static QuickEventFrag newInstance(String address) {
        QuickEventFrag frag = new QuickEventFrag();
        Bundle args = new Bundle();
        args.putString("address", address);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);

        getDialog().getWindow().setLayout(width, height);

        return inflater.inflate(R.layout.fragment_popup_quick_event, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mLocationText = (TextView) view.findViewById(R.id.event_location_field);
        mTimeText = (TextView) view.findViewById(R.id.event_time);
        mEventTitle = (EditText) view.findViewById(R.id.event_title_field);
        mPrivateSwitch = (Switch) view.findViewById(R.id.private_switch);
        // Fetch arguments from bundle and set title
        String address = getArguments().getString("address");
        mLocationText.setText(address);
        mCalendarBtn = (ImageView) view.findViewById(R.id.calendarBtn);
     //   mMultiDaySwitch = (CheckBox) view.findViewById(R.id.multiDaySwitch);

        dateSelected = Calendar.getInstance();
        df = new SimpleDateFormat("E, MMM d, yyyy hh:mm aa");


        mCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inflate calendar.
                Log.w("ContentValues","Calendar Clicked.");
                Calendar newCalendar = dateSelected;
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth);
                        pickTime();

                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
               //mTimeText.setText(df.format(dateSelected.getTime()));
            }
        });

        mCreateEventButton = (Button) view.findViewById(R.id.create_event_btn);

        mCreateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullFields();
                dismiss();
            }
        });

    }
    public void pickTime(){
        // Get Current Time selected
        mHour = dateSelected.get(Calendar.HOUR_OF_DAY);
        mMinute = dateSelected.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        dateSelected.set(dateSelected.get(Calendar.YEAR),dateSelected.get(Calendar.MONTH),dateSelected.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        mTimeText.setText(df.format(dateSelected.getTime()));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
    public void pullFields(){
        String event_title = mEventTitle.getText().toString().trim();
        String location = mLocationText.getText().toString().trim();
        Boolean event_private = mPrivateSwitch.isChecked();
       // Boolean event_mult_day = mMultiDaySwitch.isChecked();
        //MapActivity.setFocusToView(getView().findViewById();
        Log.d("Title", "Title: "+event_title);
        Log.d("Title", "Title: "+location);
        Log.d("Title", "Title: "+event_private);
        //Log.d("Title", "Title: "+event_mult_day);
    }
}