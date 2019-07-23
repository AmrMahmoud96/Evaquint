package com.evaquint.android.fragments.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.EventPageFrag;
import com.evaquint.android.fragments.dummy.DummyContent.DummyItem;
import com.evaquint.android.utils.Adapter.SearchResultAdapter;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.evaquint.android.utils.database.EventDBHelper;
import com.evaquint.android.utils.listeners.CustomItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

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

        EditText searchText = view.findViewById(R.id.map_searchbar_input);
        searchText.setText(keyword);

        populatePage(view);

        return view;
    }

    public void populatePage(View v){
//        System.out.println(sResults.toString());
//        System.out.println(keyword);
        final RecyclerView qRes = ((RecyclerView)v.findViewById(R.id.query_result_list));
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName());
        final TextView t = v.findViewById(R.id.no_res_text);

        try{

            ref.orderByChild("eventTitle").startAt(keyword).endAt(keyword+"\uf8ff").limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
//            ref.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        HashMap<String,Object> data =(HashMap<String,Object>) dataSnapshot.getValue();
                        ArrayList<String> l = new ArrayList<>();
                        Gson gson = new Gson();
                        if(data!=null){
                            for (Object o:data.values()){
                                l.add(gson.toJson(o));
                            }
                        }
                        JSONArray temp = new JSONArray();
                        for (int i = 0; i < l.size(); i++) {
                            temp.put(new JSONObject(l.get(i)).put("place_id","Evaquint"));
                        }
                        for (int j=0; j<sResults.length();j++){
                            temp.put(sResults.get(j));
                        }
                        if(temp.length()==0){
                            qRes.setVisibility(View.GONE);
                            t.setVisibility(View.VISIBLE);
                        }else{
                            LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
                            qRes.setLayoutManager(layoutManager);
                            SearchResultAdapter s = new SearchResultAdapter(temp, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    if(!v.getTag().toString().equalsIgnoreCase("place")){
                                        EventDB event = new EventDBHelper().structureEventDataFromString(v.getTag().toString());
                                        if(event !=null){
                                            EventPageFrag eventPageFragment = EventPageFrag.newInstance(event);
                                            setActiveFragment(getFragmentManager(), eventPageFragment);
                                        }
                                    }
                                }
                            });
                            qRes.setAdapter(s);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

//                    dataSnapshot.getValue();

//                    RelativeLayout layout = new RelativeLayout(getContext());
//                    LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
//                    qRes.setLayoutManager(layoutManager);
//                    SearchResultAdapter s = new SearchResultAdapter(sResults,null, new CustomItemClickListener() {
//                        @Override
//                        public void onItemClick(View v, int position) {
////                                        EventDB event = (EventDB)v.getTag();
////                                        EventPageFrag eventPageFragment = EventPageFrag.newInstance(event);
////                                        setActiveFragment(getFragmentManager(), eventPageFragment);
//                        }
//                    });
//                    qRes.setAdapter(s);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch(Exception e){
            e.printStackTrace();
            Log.e("Query Err","Unable to query Firebase for events: " + e.getMessage());
        }
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
