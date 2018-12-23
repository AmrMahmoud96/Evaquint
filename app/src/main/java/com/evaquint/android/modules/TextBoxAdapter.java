package com.evaquint.android.modules;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.evaquint.android.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TextBoxAdapter extends ArrayAdapter {
    private Context context;
    private List<String> selectedStrings;
    private Set<String> allStrings = new HashSet<String>();


    public TextBoxAdapter(Context context, List<String> resource) {
        super(context, R.layout.box_text, resource);
        this.context = context;
        selectedStrings = resource;
    }

    public View getView(final int position, @Nullable View convertView, ViewGroup parent) {
        String current_string = selectedStrings.get(position);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.box_text, parent, false);
        final TextView textView = convertView.findViewById(R.id.textbox_string);
        textView.setText(current_string);

        return convertView;
    }
}
