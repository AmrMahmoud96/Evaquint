package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.evaquint.android.R;

import java.util.Calendar;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupInformationFrag extends Fragment {
    private String firstName;
    private String lastName;
    private int age;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private TextView mDOBTextField;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

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
        mDOBTextField = view.findViewById(R.id.DOBText);
        mDOBTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = dayOfMonth+"/"+month+"/"+year;
                mDOBTextField.setText(date);
            }
        };
        return this.view;
    }

    private void grabValues(){
        firstName = mFirstNameField.getText().toString().trim();
        lastName = mLastNameField.getText().toString().trim();
      /*  if(!mAgeField.getText().toString().trim().isEmpty()){
            age = Integer.parseInt(mAgeField.getText().toString().trim());
        }*/
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
       /* if(age<12 || age>99){
            mAgeField.setError("You must be between 12 and 99 years of age");
            focusView = mAgeField;
            cancel = true;
        }*/
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
