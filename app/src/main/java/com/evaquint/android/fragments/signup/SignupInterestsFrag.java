package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.evaquint.android.R;
import com.evaquint.android.modules.CheckboxAdapter;
import com.evaquint.android.popups.InterestsSubCategoriesFrag;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.evaquint.android.utils.dataStructures.firebase_listener;
import com.evaquint.android.utils.database.UserDBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.Set;
import static com.evaquint.android.utils.code.IntentValues.PICK_SUBCATEGORIES;
import static com.evaquint.android.utils.dataStructures.EventCategories.getEventCategories;
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
    private Set<String> selected_categories;
    private HashMap<String, ArrayAdapter> arrayAdapterHashMap;
    private Fragment popupFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_signup_interests, container, false);
        this.activity = getActivity();
        arrayAdapterHashMap = new HashMap<String, ArrayAdapter>();

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

    public HashMap<String, ArrayAdapter> getArrayAdapterHashMap() {
        return arrayAdapterHashMap;
    }

    public void setArrayAdapterHashMap(HashMap<String, ArrayAdapter> arrayAdapterHashMap) {
        this.arrayAdapterHashMap = arrayAdapterHashMap;
    }

    private void openPopup(String subcategory){
        final FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("SUBCATEGORIES", subcategory);
        InterestsSubCategoriesFrag subcategories_frag = new InterestsSubCategoriesFrag();
        popupFragment = subcategories_frag;
        subcategories_frag.setArguments(bundle);
        subcategories_frag.setTargetFragment(this, PICK_SUBCATEGORIES);
        subcategories_frag.show(fm, "fragment_popup_interests_sub_categories");
    }

    private CheckboxAdapter getCheckboxAdapter(ArrayList<String> subcategories){
        return new CheckboxAdapter(getActivity(), subcategories);
    }

    private void initInterests(){
        getEventCategories(new firebase_listener(){
            @Override
            public void onUpdate(Object data, Fragment fragment){
                categories = (HashMap<String, ArrayList<String>>) data;
                for (Map.Entry<String, ArrayList<String>> entry :
                        (categories).entrySet()) {
                    String key = entry.getKey();
                    ArrayList<String> value = entry.getValue();
                    arrayAdapterHashMap.put(key,getCheckboxAdapter(value));
                }

                final List<String> top_level_categories = new ArrayList<String>(categories.keySet());
                final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                        (getActivity(),android.R.layout.simple_list_item_1, top_level_categories);
                GridView gv = (GridView) getActivity().findViewById(R.id.event_categories);
                gv.setAdapter(gridViewArrayAdapter);
                gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        openPopup(top_level_categories.get(i));
                    }
                });
            }

            @Override
            public void onCancel(){
                //Do something when cancel
            }
        }, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_SUBCATEGORIES:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    List selected = bundle.getStringArrayList("selected");
                    System.out.println(selected.toString());
                }
                break;
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

