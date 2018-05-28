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

public class SignupMethodFrag extends Fragment {
    private String phoneNumber;

    private EditText mPhoneNumberField;

    private View view;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_signup_method, container, false);
        this.activity = getActivity();

        ((Button) view.findViewById(R.id.signup_method_next_button)).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextFrag();
            }
        });
        mPhoneNumberField = view.findViewById(R.id.phoneNumberField);

        return this.view;
    }

    private boolean validateValues(){
        return false;
    }

    private void nextFrag(){
        setActiveFragment(SignupMethodFrag.this, new SignupVerificationFrag());
    }
}
