package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.evaquint.android.HomeActivity;
import com.evaquint.android.R;
import com.google.android.gms.appinvite.AppInviteInvitation;

import static android.app.Activity.RESULT_OK;

public class SignupInviteFrag extends Fragment {
    private View view;
    private Activity activity;

    private ImageButton mInviteButton;

    private static final int REQUEST_INVITE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_signup_invitefriends, container, false);
        this.activity = getActivity();
        mInviteButton = view.findViewById(R.id.inviteBtn);
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInviteClicked();
            }
        });
        ((Button) view.findViewById(R.id.signup_invite_friends_next_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextFrag();
                    }
                });

        return this.view;
    }

    private boolean validateValues(){
        return false;
    }

    private void onInviteClicked() {
            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                    .setMessage(getString(R.string.invitation_message))
                    .setDeepLink(Uri.parse("https://evaquint.page.link"))
                    .setCallToActionText(getString(R.string.invitation_cta))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("","onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.i("", "onActivityResult: sent invitation " + id);
                }
            } else {

                Log.i("", "onActivityResult: failed to send ");
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }

    private void nextFrag(){
        Intent myIntent = new Intent(getActivity(), HomeActivity.class);
        startActivity(myIntent);
    }
}


