package com.evaquint.android.popups;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evaquint.android.R;

/**
 * Created by henry on 11/27/2017.
 */


public class QuickEventFrag extends DialogFragment {

    private TextView mText;

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
        mText = (TextView) view.findViewById(R.id.popup_test);
        // Fetch arguments from bundle and set title
        String address = getArguments().getString("address");
        mText.setText(address);
    }
}