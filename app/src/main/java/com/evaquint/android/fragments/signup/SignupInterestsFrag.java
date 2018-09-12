package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.evaquint.android.R;
import com.evaquint.android.utils.dataStructures.EventCategories;
import com.evaquint.android.utils.dataStructures.firebase_listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.evaquint.android.utils.dataStructures.EventCategories.getEventCategories;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupInterestsFrag extends Fragment {
    private View view;
    private Activity activity;
    private HashMap categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_signup_interests, container, false);
        this.activity = getActivity();

        ((Button) view.findViewById(R.id.signup_interests_next_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextFrag();
                    }
                });
        initInterests();

        return this.view;
    }

    private void initInterests(){
        getEventCategories(new firebase_listener(){
            @Override
            public void onUpdate(Object data){
                HashMap<String, ArrayList<String>> categories = (HashMap<String, ArrayList<String>>) data;
                System.out.println(categories.toString());
//                for (Object name: categories.){
//                    String key =name.toString();
//                    String value = categories.get(name).toString();
//                    System.out.println(key + " " + value);
//
//
//                }
//                final List<String> top_level_categories = new ArrayList<String>(categories.keySet());
//                final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
//                        (getActivity(),android.R.layout.simple_list_item_1, top_level_categories);
//                GridView gv = (GridView) this.view.findViewById(R.id.event_categories);
//                gv.setAdapter(gridViewArrayAdapter);
            }

            @Override
            public void onCancel(){
                //Do something when cancel
            }
        });

    }

    private boolean validateValues(){
        return false;
    }

    private void nextFrag(){
        setActiveFragment(SignupInterestsFrag.this, new SignupInviteFrag());
    }
}

