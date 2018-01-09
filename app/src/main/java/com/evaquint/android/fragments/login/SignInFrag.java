package com.evaquint.android.fragments.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evaquint.android.MainActivity;
import com.evaquint.android.R;
import com.evaquint.android.utils.authenticator.EmailAuthenticator;
import com.evaquint.android.utils.view.ViewAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

/**
 * Created by henry on 8/8/2017.
 */

public class SignInFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to activity real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private EditText mNameView;
    private View mProgressView;
    private View mLoginFormView;
    private View view;
    private Activity activity;
    private ViewAnimator animationManager;
    private boolean register = false;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        this.activity = getActivity();
        this.animationManager= new ViewAnimator(activity);

        // Set up the login form.
        mEmailView = view.findViewById(R.id.email);

        mPasswordView = (EditText) view.findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        mPasswordConfirmView = (EditText) view.findViewById(R.id.confirm_password);
//        mNameView = (EditText) view.findViewById(R.id.display_name);

        final Button mEmailSignInButton = (Button) view.findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);
        mProgressView.setVisibility(View.INVISIBLE);

        final Button mSwitchButton = (Button) view.findViewById(R.id.switch_button);
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveFragment(SignInFrag.this, new SignUpFrag());
            }
        });

//        final Button mSwitchButton = (Button) view.findViewById(R.id.switch_button);
//        mSwitchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setActiveFragment(SignInFrag.this, new SignUpFrag());
//            }
//        });

//            @Override
//            public void onClick(View view) {
//                String switchButton;
//                String signInButton;
//                if (register) {
//                    switchButton=getString(R.string.action_switch_sign_in);
//                    signInButton=getString(R.string.action_sign_in);
//                } else {
//                    switchButton=getString(R.string.action_switch_register);
//                    signInButton=getString(R.string.action_register);
//                }
//                animationManager.slideDownThenUp(displayNameInput);
//                animationManager.slideDownThenUp(confirmPasswordInput);
//                mSwitchButton.setText(switchButton);
//                mEmailSignInButton.setText(signInButton);
//                register=!register;
//
//            }
     //   });

        return this.view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
//        String passwordConfirm = mPasswordView.getText().toString();
//        String name = mNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for activity valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
//
//        // If user is in registration mode, check if passwords match
//        if (!doesPasswordsMatch(password, passwordConfirm) && register == true) {
//            mPasswordView.setError(getString(R.string.error_non_matching_passwords));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//        // If user is in registration mode, check if name is valid
//        if (!isNameValid(name) && register == true) {
//            mPasswordView.setError(getString(R.string.error_invalid_name));
//            focusView = mNameView;
//            cancel = true;
//        }

        // Check for activity valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show activity progress spinner, and kick off activity background task to
            handler.post(new Runnable() {
                public void run() {
                    mProgressView.setVisibility(View.VISIBLE);
                    SignInFrag.this.getView().setAlpha( (float) 0.5 );
                }
            });
            // perform the user login attempt.
//            showProgress(true);
//            if (register)
//                (new EmailAuthenticator(activity,new Intent(activity, MainActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                        )).createAccount(name, email, password);
//            else
                (new EmailAuthenticator(activity,new Intent(activity, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY),
                        new Callable() {
                            @Override
                            public Object call() throws Exception {
                                SignInFrag.this.getView().setAlpha(1);
                                mProgressView.setVisibility(View.INVISIBLE);
                                return null;
                            }
                        }
                )).signIn(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isNameValid(String name) {
        return name.length()>3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4 ;
    }

    private boolean doesPasswordsMatch(String password, String passwordConfirm) {
        return password.equals(passwordConfirm);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(activity,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // activity primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

//        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
//
//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(activity,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
//        mEmailView.setAdapter(adapter);
//    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}
