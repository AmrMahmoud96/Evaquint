package com.evaquint.android.popups;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.map.EventLocatorFrag;
import com.evaquint.android.modules.CheckboxAdapter;

import java.util.ArrayList;

/**
 * Created by henry on 9/12/2018.
 */

public class InterestsSubCategoriesFrag extends DialogFragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ((EventLocatorFrag)getTargetFragment()).setPopupOpen();
        ArrayList<String> subcategories = getArguments().getStringArrayList("SUBCATEGORIES");
        System.out.println(subcategories.toString());
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);

        getDialog().getWindow().setLayout(width, height);
        this.view =  inflater.inflate(R.layout.fragment_popup_interests, container);
        populate_checklist(subcategories);
        return view;
    }

    private void populate_checklist(ArrayList<String> subcategories){
        ListView listView = (ListView) this.view.findViewById(R.id.sub_categories);
//        stringList = dbUtils.getServiceConfigForFixedOptions(attribute.getListValues());
        CheckboxAdapter checkboxAdapter = new CheckboxAdapter(getActivity(), subcategories);
        listView.setAdapter(checkboxAdapter);

//        CheckBox cb = (CheckBox) checkboxAdapter.getView(0, null, listView).findViewById(R.id.subcategory_checkbox);
//        cb.setChecked(true);
    }
}
