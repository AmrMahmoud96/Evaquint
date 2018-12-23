package com.evaquint.android.popups;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evaquint.android.R;
import com.evaquint.android.utils.Adapter.MyAdapter;

import java.util.HashMap;

public class EventAttendeesFrag extends DialogFragment {
    private View view;
    private String[] attendees;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public EventAttendeesFrag() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EventAttendeesFrag newInstance(HashMap<String,String> attendees) {
         EventAttendeesFrag frag = new EventAttendeesFrag();
         Bundle args = new Bundle();
         String[] ASA = attendees.values().toArray(new String[0]);
         args.putStringArray("attendees", ASA);
         frag.setArguments(args);
         return frag;
     }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setLayout(width, height);
        getDialog().setCanceledOnTouchOutside(true);

        this.view =  inflater.inflate(R.layout.fragment_attendees_popup, container);
        this.attendees =getArguments().getStringArray("attendees");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
//        String[] arr = {"test","test2"};
        mAdapter = new MyAdapter(attendees,EventAttendeesFrag.this);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }




}