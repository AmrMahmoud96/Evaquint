package com.evaquint.android.popups;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.map.EventLocatorFrag;
import com.evaquint.android.utils.dataStructures.EventCategories;

import java.util.ArrayList;
import java.util.Map;

public class EventSuggestionFrag extends DialogFragment {
    private TextView mRecText;
    private TextView mRecommendation;
    private Button mRunAgain;
    private Button mContinue;
    private String lastSuggestion;
    private Map<String, ArrayList<String>> categories;
    private View view;
    public EventSuggestionFrag() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

   /* public static EventSuggestionFrag newInstance(String address, LatLng location, String eventID) {
        EventSuggestionFrag frag = new EventSuggestionFrag();
        Bundle args = new Bundle();
        args.putString("address", address);
        args.putDouble("latitude", location.latitude);
        args.putDouble("longitude",location.longitude);
        args.putString("eventID",eventID);
        frag.setArguments(args);
        return frag;
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((EventLocatorFrag)getTargetFragment()).setPopupOpen();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);

        getDialog().getWindow().setLayout(width, height);
        this.view =  inflater.inflate(R.layout.fragment_popup_recommendation, container);
        mRecText =  (TextView) view.findViewById(R.id.recText);
        mRecommendation = (TextView) view.findViewById(R.id.recommendation);
        mContinue = (Button) view.findViewById(R.id.contButton);
        mRunAgain = (Button) view.findViewById(R.id.reroll);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to venue recommendation then to popup quick event
            }
        });

        mRunAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int suggestionType = (int) Math.floor(Math.random()*5);
                suggestEvent(suggestionType);
            }
        });
        init();
        return view;
    }

    @Override
    public void onDestroyView() {
        ((EventLocatorFrag)getTargetFragment()).setPopupClosed();
        super.onDestroyView();
    }


    public void init(){
        String[] catArray = getResources().getStringArray(R.array.event_categories);
        categories = new EventCategories(catArray).getCategories();
        //Log.i("cat:",categories.toString());
        int firstSuggestion = (int) Math.floor(Math.random()*5);
        suggestEvent(firstSuggestion);
    }

    public void suggestEvent(int suggestionType){
               /*
        * Ways to recommend people evenets
        *   1. Based off user's interests
        *   2. Based off user's friends interests
        *   3. Based off surrounding peoples interests
        *   4. Randomly based off all event types
        *
        *Set the recommendation text specific to each type.
        *
        * Ways to better algorithm:
        *  1. find the recommendations contained between recommendations 1-4
        *
        * */
        String recommendation = "";

        //1. Based off user's interests
        if(suggestionType == 1){
            mRecText.setText("We saw that you are interested in:");
            recommendation = "a";
        }

        //2. Based off user's friends interests
        if(suggestionType == 2){
            mRecText.setText("X, Y, and Z others are interested in:");
            recommendation = "b";
        }
        //3. Based off surrounding peoples interests
        if(suggestionType == 3){
            mRecText.setText("X people near you are interested in:");
            recommendation = "c";
        }

        //4. Randomly based off all event types
        if(suggestionType == 4){
            mRecText.setText("How about trying:");
            ArrayList<String> cat = new ArrayList<>(categories.keySet());
            int catSelect = (int) Math.floor(Math.random()*cat.size());
            String category = cat.get(catSelect);
            if(category.equalsIgnoreCase("other")){
                suggestEvent(suggestionType);
            }else{
                ArrayList<String> subcat = categories.get(category);
                if(subcat!=null){
                    String selection = subcat.get((int)Math.floor(Math.random()*subcat.size()));
                    recommendation = selection;
                }else{
                    recommendation = category;
                }
            }
        }

        if(recommendation.isEmpty()){
            // if nothing was returned, try again with type 4 suggestion.
            suggestEvent(4);
        }else{
            if(recommendation.equalsIgnoreCase(lastSuggestion)){
                // no re runs of the same type.
                suggestEvent(4);
            }else{
                mRecommendation.setText(recommendation);
                lastSuggestion = recommendation;
            }
        }

    }
}