package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.evaquint.android.R;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.evaquint.android.utils.database.UserDBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import com.evaquint.android.popups.EventSuggestionFrag;
import com.evaquint.android.popups.InterestsSubCategoriesFrag;
import com.evaquint.android.utils.dataStructures.EventCategories;
import com.evaquint.android.utils.dataStructures.firebase_listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.evaquint.android.utils.code.IntentValues.EVENT_SUGGESTION_FRAGMENT;
import static com.evaquint.android.utils.dataStructures.EventCategories.getEventCategories;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupInterestsFrag extends Fragment {
    private View view;
    private Activity activity;

    private FirebaseAuth mAuth;

    public HashMap getCategories() {
        return categories;
    }

    public void setCategories(HashMap categories) {
        this.categories = categories;
    }

    private HashMap<String, ArrayList<String>> categories;
    private Fragment popupFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
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

    private void openPopup(ArrayList<String> subCategories){
        final FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("SUBCATEGORIES", subCategories);
        InterestsSubCategoriesFrag subcategories_frag = new InterestsSubCategoriesFrag();
        popupFragment = subcategories_frag;
        System.out.println(bundle.toString());
        subcategories_frag.setArguments(bundle);
//        subcategories_frag.setTargetFragment(this, EVENT_SUGGESTION_FRAGMENT);
        subcategories_frag.show(fm, "fragment_popup_interests_sub_categories");
    }



    private void initInterests(){
        getEventCategories(new firebase_listener(){
            @Override
            public void onUpdate(Object data, Fragment fragment){
                categories = (HashMap<String, ArrayList<String>>) data;
//                ((SignupInterestsFrag) fragment).setCategories((HashMap<String, ArrayList<String>>) data);
//                HashMap<String, ArrayList<String>> categories = ;
                for (Object name: categories.keySet()){
                    String key = name.toString();
                    String value = categories.get(name).toString();
                    System.out.println(key + " " + value);
                }
                final List<String> top_level_categories = new ArrayList<String>(categories.keySet());
                final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                        (getActivity(),android.R.layout.simple_list_item_1, top_level_categories);
                GridView gv = (GridView) getActivity().findViewById(R.id.event_categories);
                gv.setAdapter(gridViewArrayAdapter);
                gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        openPopup(categories.get(top_level_categories.get(i)));
                    }
                });
            }

            @Override
            public void onCancel(){
                //Do something when cancel
            }
        }, this);

    }

    private boolean validateValues(){
        return false;
    }

    private void nextFrag(){
        createPhoneUser();
        setActiveFragment(SignupInterestsFrag.this, new SignupInviteFrag());
    }
    private void createPhoneUser(){
        FirebaseUser fUser = mAuth.getCurrentUser();
        if(fUser!=null){
            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getArguments().getString("firstName") +" "+ getArguments().getString("lastName"))
                    .build();
            fUser.updateProfile(updateProfile).addOnCompleteListener(activity, null);
            //   userDatabaseHandler.addUser(fUser.getUid(),new UserDB());
            UserDBHelper userDBHelper = new UserDBHelper();
            Bundle userInfo = getArguments();
            userDBHelper.addUser(fUser.getUid(),
                    new UserDB(userInfo.getString("firstName"),userInfo.getString("lastName"),userInfo.getLong("dob"),"default","",userInfo.getString("phoneNumber"), Arrays.asList(""),Calendar.getInstance().getTimeInMillis(),new HashMap<String, String>(), new HashMap<String, String>(), Arrays.asList(""),0,0,0,0));

        }

    }
}

