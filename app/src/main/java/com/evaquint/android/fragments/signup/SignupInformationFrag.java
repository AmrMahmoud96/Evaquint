package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evaquint.android.R;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupInformationFrag extends Fragment {
    private String firstName;
    private String lastName;
    private int age;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mAgeField;

    private View view;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_signup_form, container, false);
        this.activity = getActivity();

        ((Button) view.findViewById(R.id.signup_form_next_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        grabValues();
                        if(validateValues()) nextFrag();
                    }
                });
        mFirstNameField = view.findViewById(R.id.firstNameField);
        mLastNameField = view.findViewById(R.id.lastNameField);
        mAgeField = view.findViewById(R.id.ageField);

        return this.view;
    }

    private void grabValues(){
        firstName = mFirstNameField.getText().toString().trim();
        lastName = mLastNameField.getText().toString().trim();
        age = Integer.parseInt(mAgeField.getText().toString().trim());
    }

    private boolean validateValues(){
        boolean cancel = false;
        View focusView = null;
        if(firstName.isEmpty()){
            mFirstNameField.setError("Please enter your first name.");
            focusView = mFirstNameField;
            cancel = true;
        }
        if(lastName.isEmpty()){
            mLastNameField.setError("Please enter your last name.");
            focusView = mLastNameField;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void nextFrag(){
        setActiveFragment(SignupInformationFrag.this, new SignupInterestsFrag());
    }
}
