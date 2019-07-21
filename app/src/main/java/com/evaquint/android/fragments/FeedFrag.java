package com.evaquint.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.dummy.DummyContent.DummyItem;
import com.evaquint.android.utils.Adapter.EventListAdapter;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.evaquint.android.utils.listeners.CustomItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

/**
 * A fragment representing activity list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FeedFrag extends Fragment {

    private boolean isHostView = false;
    private boolean currVisible = true;
    private boolean pastVisible = true;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedFrag() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FeedFrag newInstance(boolean hosting) {
        FeedFrag fragment = new FeedFrag();
        Bundle args = new Bundle();
        args.putBoolean("hosting", hosting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isHostView = getArguments().getBoolean("hosting");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        final RecyclerView currEvents = ((RecyclerView)view.findViewById(R.id.currEventList));
        final RecyclerView pastEvents = ((RecyclerView)view.findViewById(R.id.pastEventList));
        TextView cPageTitle = (TextView)view.findViewById(R.id.currPageTitle);
        TextView pPageTitle = (TextView)view.findViewById(R.id.pastPageTitle);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String node = "";
        cPageTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currVisible){
                    currVisible = false;
                    currEvents.setVisibility(View.GONE);
                }else{
                    currVisible = true;
                    currEvents.setVisibility(View.VISIBLE);
                }
            }
        });
        pPageTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pastVisible){
                    pastVisible = false;
                    pastEvents.setVisibility(View.GONE);
                }else{
                    pastVisible = true;
                    pastEvents.setVisibility(View.VISIBLE);
                }
            }
        });
        if(isHostView){
            node = "eventsHosted";
            cPageTitle.setText("Hosting");
            pPageTitle.setText("Hosted");

        }else{
            node = "eventsAttended";
            cPageTitle.setText("Attending");
            pPageTitle.setText("Attended");
        }
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child(node);

        try {
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null&&dataSnapshot.getValue()!=null) {
                                Log.i("eventsHosted", dataSnapshot.toString());
//                                RelativeLayout layout = new RelativeLayout(getContext());
                                LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
                                currEvents.setLayoutManager(layoutManager);
                                HashMap<String, String> events = (HashMap<String, String>) dataSnapshot.getValue();
                                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                                String[] currEventsArr = events.keySet().toArray(new String[events.size()]);

                                EventListAdapter e = new EventListAdapter(currEventsArr, new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        EventDB event = (EventDB)v.getTag();
                                        EventPageFrag eventPageFragment = EventPageFrag.newInstance(event);
                                        setActiveFragment(getFragmentManager(), eventPageFragment);
                                    }
                                });
                                currEvents.setAdapter(e);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

        return view;
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <activity href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</activity> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
