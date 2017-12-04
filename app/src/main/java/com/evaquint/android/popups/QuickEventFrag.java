package com.evaquint.android.popups;
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
import java.text.DateFormat;

import com.evaquint.android.R;

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
    private CheckBox mMultiDaySwitch;
    private Button mCreateEventButton;
    private Calendar dateSelected;
    SimpleDateFormat df;
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
        mMultiDaySwitch = (CheckBox) view.findViewById(R.id.multiDaySwitch);

        dateSelected = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        mCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inflate calendar.
                Log.w("ContentValues","Calendar Clicked.");
                Calendar newCalendar = dateSelected;
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                        mTimeText.setText(df.format(dateSelected.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
               mTimeText.setText(df.format(dateSelected.getTime()));
            }
        });

        mCreateEventButton = (Button) view.findViewById(R.id.create_event_btn);

        mCreateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullFields();
            }
        });

    }
    public void pullFields(){
        String event_title = mEventTitle.getText().toString().trim();
        String location = mLocationText.getText().toString().trim();
        Boolean event_private = mPrivateSwitch.isChecked();
        Boolean event_mult_day = mMultiDaySwitch.isChecked();
        Log.d("Title", "Title: "+event_title);
        Log.d("Title", "Title: "+location);
        Log.d("Title", "Title: "+event_private);
        Log.d("Title", "Title: "+event_mult_day);
    }
}