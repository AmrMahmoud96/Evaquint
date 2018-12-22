package com.evaquint.android.popups;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.signup.SignupInterestsFrag;
import com.evaquint.android.modules.CheckboxAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * Created by henry on 9/12/2018.
 */

public class InterestsSubCategoriesFrag extends DialogFragment {
    View view;
    ArrayList<String> selectedStrings = new ArrayList<String>();
    ArrayAdapter checkboxAdapter;
    ListView listView;
    SignupInterestsFrag callerFragment;
    HashMap<String, ArrayAdapter> arrayAdapterHashMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ((EventLocatorFrag)getTargetFragment()).setPopupOpen();
        if (checkboxAdapter == null) {

        }
        String subcategory = getArguments().getString("SUBCATEGORIES");
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.75);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 1);

        getDialog().getWindow().setLayout(width, height);
        view =  inflater.inflate(R.layout.fragment_popup_interests, container);
        listView = (ListView) view.findViewById(R.id.sub_categories);

        callerFragment = (SignupInterestsFrag) getTargetFragment();
        arrayAdapterHashMap = callerFragment.getArrayAdapterHashMap();


        populate_checklist(subcategory);

        view.findViewById(R.id.popup_interests_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InterestsSubCategoriesFrag.this.dismiss();
            }
        });
        return view;
    }

    private void populate_checklist(String subcategory){
        checkboxAdapter = arrayAdapterHashMap.get(subcategory);
        listView.setAdapter(checkboxAdapter);
    }

    @Override
    public void onDismiss(final DialogInterface dialog){
        Intent i = new Intent().putExtra("selected", ((CheckboxAdapter) checkboxAdapter).getSelectedString());
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, i);
        callerFragment.setArrayAdapterHashMap(arrayAdapterHashMap);
        super.onDismiss(dialog);

    }
}
