package com.evaquint.android.popups;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evaquint.android.R;
import com.evaquint.android.fragments.map.EventLocatorFrag;

public class EventSuggestionFrag extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((EventLocatorFrag)getTargetFragment()).setPopupOpen();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);

        getDialog().getWindow().setLayout(width, height);

        return inflater.inflate(R.layout.fragment_popup_quick_event, container);
    }

    @Override
    public void onDestroyView() {
        ((EventLocatorFrag)getTargetFragment()).setPopupClosed();
        super.onDestroyView();
    }
}