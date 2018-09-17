package com.evaquint.android.popups;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.map.EventLocatorFrag;
import com.evaquint.android.fragments.signup.SignupInterestsFrag;
import com.evaquint.android.modules.CheckboxAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by henry on 9/12/2018.
 */

public class InterestsSubCategoriesFrag extends DialogFragment {
    View view;
    ArrayList<String> selectedStrings = new ArrayList<String>();
    CheckboxAdapter checkboxAdapter;
    ListView listView;
    SignupInterestsFrag callerFragment;
    HashMap<String, ArrayAdapter> arrayAdapterHashMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ((EventLocatorFrag)getTargetFragment()).setPopupOpen();
        String subcategory = getArguments().getString("SUBCATEGORIES");
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.75);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 1);

        getDialog().getWindow().setLayout(width, height);
        view =  inflater.inflate(R.layout.fragment_popup_interests, container);
        listView = (ListView) view.findViewById(R.id.sub_categories);

        callerFragment = (SignupInterestsFrag) getTargetFragment();
        arrayAdapterHashMap = callerFragment.getArrayAdapterHashMap();

        populate_checklist(subcategory);
        return view;
    }

    private void populate_checklist(String subcategory){
        listView.setAdapter(arrayAdapterHashMap.get(subcategory));
    }

    @Override
    public void onDismiss(final DialogInterface dialog){
//        Intent i = new Intent().putExtra("selected", checkboxAdapter.getSelectedString());
//        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, i);
        callerFragment.setArrayAdapterHashMap(arrayAdapterHashMap);
        super.onDismiss(dialog);

    }
}
