package com.evaquint.android.fragments.login;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evaquint.android.R;
import com.evaquint.android.utils.view.ViewAnimator;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

/**
 * Created by henry on 11/5/2017.
 */

public class SignUpFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View view;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_register, container, false);
        this.activity = getActivity();

        final Button mSwitchButton = (Button) view.findViewById(R.id.switch_button);
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveFragment(SignUpFrag.this, new SignInFrag());
            }
        });
        // Set up everything else


        return this.view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}