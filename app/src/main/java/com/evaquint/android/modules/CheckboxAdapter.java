package com.evaquint.android.modules;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.evaquint.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by henry on 9/14/2018.
 */

public class CheckboxAdapter extends ArrayAdapter {
    Context context;
    List<Boolean> checkboxState;
    List<String> checkboxItems;

    public CheckboxAdapter(Context context, List<String> resource) {
        super(context, R.layout.checkbox, resource);

        this.context = context;
        this.checkboxItems = resource;
        this.checkboxState = new ArrayList<Boolean>(Collections.nCopies(resource.size(), false));
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.checkbox, parent, false);
        TextView textView = (TextView) convertView.findViewById(R.id.subcategory);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.subcategory_checkbox);

        textView.setText(checkboxItems.get(position));
        cb.setChecked(checkboxState.get(position));
        return convertView;
    }

    void setChecked(boolean state, int position)
    {
        checkboxState.set(position, state);
    }
}
