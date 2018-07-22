package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.evaquint.android.R;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupVerificationFrag extends Fragment {
    private TextView mResendCodeText;
    private EditText mVerificationCodeField;


    private View view;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_signup_verification, container, false);
        this.activity = getActivity();

        ((Button) view.findViewById(R.id.signup_verification_next_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextFrag();
                    }
                });
        mVerificationCodeField = view.findViewById(R.id.verificationCodeField);
        mResendCodeText = view.findViewById(R.id.resendCodeText);

        mResendCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resend code
            }
        });
        return this.view;
    }

    private boolean validateValues(){
        return false;
    }

    private void nextFrag(){
        setActiveFragment(SignupVerificationFrag.this, new SignupInformationFrag());
    }
}
