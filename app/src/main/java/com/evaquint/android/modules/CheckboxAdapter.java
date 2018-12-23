package com.evaquint.android.modules;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.evaquint.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by henry on 9/14/2018.
 */

public class CheckboxAdapter extends ArrayAdapter {
    private Context context;
    private List<Boolean> checkboxState;
    private List<String> checkboxItems;
    private ArrayList<String> selectedStrings = new ArrayList<String>();


    public CheckboxAdapter(Context context, List<String> resource) {
        super(context, R.layout.checkbox, resource);

        this.context = context;
        this.checkboxItems = resource;
        this.checkboxState = new ArrayList<Boolean>(Collections.nCopies(resource.size(), false));
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.checkbox, parent, false);
        final TextView textView = (TextView) convertView.findViewById(R.id.subcategory);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.subcategory_checkbox);
        textView.setText(checkboxItems.get(position));
        cb.setChecked(checkboxState.get(position));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedStrings.add(textView.getText().toString());
                    checkboxState.set(position, true);
                }else{
                    selectedStrings.remove(textView.getText().toString());
                    checkboxState.set(position, false);
                }

            }
        });

        return convertView;
    }

    public ArrayList<String> getSelectedString(){
        return selectedStrings;
    }

    public ArrayList<String> getUnselectedString(){
        ArrayList<String> copy_all = (ArrayList<String>) ((ArrayList<String>) checkboxItems).clone();
        copy_all.removeAll(selectedStrings);
        return copy_all;
    }
}
