package com.evaquint.android.fragments.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.evaquint.android.R;
import com.evaquint.android.fragments.dummy.DummyContent.DummyItem;
import com.evaquint.android.utils.Adapter.SearchResultAdapter;
import com.evaquint.android.utils.listeners.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A fragment representing activity list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchResultsFrag extends Fragment {

    private String keyword;
    private JSONArray sResults;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchResultsFrag() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchResultsFrag newInstance(JSONArray search_results,String k) {
        SearchResultsFrag fragment = new SearchResultsFrag();
        Bundle args = new Bundle();
        args.putString("results",search_results.toString());
        args.putString("keyword",k);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try{
                sResults = new JSONArray(getArguments().getString("results"));
                keyword = getArguments().getString("keyword");
            }catch(JSONException e){
                Log.e("JSON Error:", "Problem parsing JSON response");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results,container,false);
        populatePage(view);

//        view.findViewById(R.id.event_host_desc).setVisibility(View.VISIBLE);

//        View view = inflater.inflate(R.layout.fragment_feed, container, false);
//
//        final RecyclerView currEvents = ((RecyclerView)view.findViewById(R.id.currEventList));
//        final RecyclerView pastEvents = ((RecyclerView)view.findViewById(R.id.pastEventList));
//        TextView cPageTitle = (TextView)view.findViewById(R.id.currPageTitle);
//        TextView pPageTitle = (TextView)view.findViewById(R.id.pastPageTitle);
//        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String node = "";
//        cPageTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(currVisible){
//                    currVisible = false;
//                    currEvents.setVisibility(View.GONE);
//                }else{
//                    currVisible = true;
//                    currEvents.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        pPageTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(pastVisible){
//                    pastVisible = false;
//                    pastEvents.setVisibility(View.GONE);
//                }else{
//                    pastVisible = true;
//                    pastEvents.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        if(isHostView){
//            node = "eventsHosted";
//            cPageTitle.setText("Hosting");
//            pPageTitle.setText("Hosted");
//
//        }else{
//            node = "eventsAttended";
//            cPageTitle.setText("Attending");
//            pPageTitle.setText("Attended");
//        }
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child(node);
//
//        try {
//                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if(dataSnapshot!=null&&dataSnapshot.getValue()!=null) {
//                                Log.i("eventsHosted", dataSnapshot.toString());
////                                RelativeLayout layout = new RelativeLayout(getContext());
//                                LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
//                                currEvents.setLayoutManager(layoutManager);
//                                HashMap<String, String> events = (HashMap<String, String>) dataSnapshot.getValue();
//                                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
//                                String[] currEventsArr = events.keySet().toArray(new String[events.size()]);
//
//                                EventListAdapter e = new EventListAdapter(currEventsArr, new CustomItemClickListener() {
//                                    @Override
//                                    public void onItemClick(View v, int position) {
//                                        EventDB event = (EventDB)v.getTag();
//                                        EventPageFrag eventPageFragment = EventPageFrag.newInstance(event);
//                                        setActiveFragment(getFragmentManager(), eventPageFragment);
//                                    }
//                                });
//                                currEvents.setAdapter(e);
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
        return view;
//        return null;
    }

    public void populatePage(View v){
//        System.out.println(sResults.toString());
//        System.out.println(keyword);
        RecyclerView qRes = ((RecyclerView)v.findViewById(R.id.query_result_list));
        RelativeLayout layout = new RelativeLayout(getContext());
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        qRes.setLayoutManager(layoutManager);
        SearchResultAdapter s = new SearchResultAdapter(sResults,null, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                                        EventDB event = (EventDB)v.getTag();
//                                        EventPageFrag eventPageFragment = EventPageFrag.newInstance(event);
//                                        setActiveFragment(getFragmentManager(), eventPageFragment);
            }
        });
        qRes.setAdapter(s);

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
