package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evaquint.android.R;

import java.util.Calendar;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupInformationFrag extends Fragment {
    private String firstName;
    private String lastName;
    private int age;
    private String DOB;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private TextView mDOBTextField;
    private Calendar today;

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
                mDOBTextField.setError(null);
                today = Calendar.getInstance();
                int year = today.get(Calendar.YEAR);
                int month = today.get(Calendar.MONTH);
                int day = today.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                today.add(Calendar.YEAR, -12);

                datePickerDialog.getDatePicker().setMaxDate(today.getTimeInMillis());
                today.add(Calendar.YEAR, -88);
                datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
                datePickerDialog.show();
                today.add(Calendar.YEAR, 100);
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = dayOfMonth+"/"+month+"/"+year;
                DOB = date;
                mDOBTextField.setText(date);


                age = today.get(Calendar.YEAR)- year;

                if(today.get(Calendar.MONTH)+1<month){
                    age--;
                }else if(today.get(Calendar.MONTH)+1==month){

                    if(today.get(Calendar.DAY_OF_MONTH)<dayOfMonth){
                        age--;
                    }
                }

             /*   Log.i("day born:" , ""+dob.get(Calendar.DAY_OF_YEAR));
                Log.i("day today:" , ""+today.get(Calendar.DAY_OF_YEAR));
                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                    age--;
                }*/
                Log.i("age:" , ""+age);
                if(age<12 || age>99){
                    mDOBTextField.setError("You must be between 12 and 99 years of age.");
                    mDOBTextField.requestFocus();
                    Toast.makeText(getActivity(),"You must be between 12 and 99 years of age.",Toast.LENGTH_SHORT).show();
                }
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
        if(firstName.isEmpty() || firstName==null || firstName.length()<3){
            mFirstNameField.setError("Please enter your first name.");
            focusView = mFirstNameField;
            cancel = true;
        }
        if(lastName.isEmpty()||lastName==null||lastName.length()<3){
            mLastNameField.setError("Please enter your last name.");
            focusView = mLastNameField;
            cancel = true;
        }
        if(age==0){
            mDOBTextField.setError("Please select your date of birth.");
            Toast.makeText(getActivity(),"Please select your date of birth.",Toast.LENGTH_SHORT).show();
            focusView = mDOBTextField;
            cancel = true;
        }
        if(age<12 || age>99){
            mDOBTextField.setError("You must be between 12 and 99 years of age");
            Toast.makeText(getActivity(),"You must be between 12 and 99 years of age.",Toast.LENGTH_SHORT).show();
            focusView = mDOBTextField;
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
