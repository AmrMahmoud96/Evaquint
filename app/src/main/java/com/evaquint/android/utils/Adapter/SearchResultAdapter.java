package com.evaquint.android.utils.Adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.utils.listeners.CustomItemClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by amrmahmoud on 2019-07-21.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder>{
        private SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM d, yyyy hh:mm aa");
        private JSONArray mPlaceDataset;
        private String[] mEventDataset;

        private static String pictureRequestURL = "https://maps.googleapis.com/maps/api/place/photo";
        private static String urlFormat = "%s?maxwidth=%03d&maxheight=%03d&photoreference=%s&key=%s";
        CustomItemClickListener listener;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTitleTextView;
            public TextView mDateTextView;
            public ImageView mEventImageView;
            public TextView mHostTextView;
            public ViewHolder(View v) {
                super(v);
                mTitleTextView =  v.findViewById(R.id.eventRecyclerTitle);
                mDateTextView =  v.findViewById(R.id.eventRecyclerDate);
                mEventImageView =  v.findViewById(R.id.eventRecyclerImg);
                mHostTextView = v.findViewById(R.id.event_host_desc);
                mHostTextView.setVisibility(View.VISIBLE);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public SearchResultAdapter(JSONArray myPlaceDataset, String[]myEventDataset, CustomItemClickListener listener) {
            mPlaceDataset = myPlaceDataset;
            mEventDataset = myEventDataset;
            this.listener = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public SearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_recyclerview, parent, false);
//            v.setTag(true);
            final ViewHolder vh = new ViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v.findViewById(R.id.eventRecyclerTitle), vh.getAdapterPosition());
                }
            });
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            //JSON array loop and populate
            JSONObject array = null;
            LatLng location;
            String title="Place";
            String address="Location";
            String pictureURL="";
            String photoRef = "";
            try {
                array = (JSONObject) mPlaceDataset.get(position);
                JSONObject coords = (JSONObject) ((JSONObject)array.get("geometry")).get("location");
                location = new LatLng((double) coords.get("lat"),
                        (double) coords.get("lng"));
                address = (String) ((JSONObject)  array.get("plus_code")).get("compound_code");
                title = (String) array.get("name");
                photoRef =((JSONObject)((JSONArray)array.get("photos")).get(0)).getString("photo_reference");
            } catch (JSONException e) {
                Log.e("JSON Err", "Problem parsing JSON response: " + e.getMessage());
            }

            holder.mTitleTextView.setText(title);
            holder.mDateTextView.setText(address);
            holder.mHostTextView.setText("Places");
            try{
                if(!photoRef.isEmpty()){
                    pictureURL = String.format(urlFormat, pictureRequestURL,600,600,photoRef,"AIzaSyDbW7Mh_CKD0FHcGswtg0ZddTICwu2mbiw");
//                    System.out.println(pictureURL);
                    if(!pictureURL.isEmpty()){
                        String downloadUrl = pictureURL;
//                        Log.i("downloadurl", downloadUrl);
                        Picasso.with(holder.itemView.getContext()).load(Uri.parse(downloadUrl)).fit().into(holder.mEventImageView);
                    }
                }
            }catch (Exception e){
                Log.e("Image Load Err","problem retrieving places picture: "+e.getMessage());
            }
//
            holder.mTitleTextView.setTag("Place");



//            //get element from database and set items
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(mDataset[position]);
//            ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
//                        Log.i("datasnapshot", dataSnapshot.toString());
//                        String eventID = dataSnapshot.child("eventID").getValue().toString();
//                        Calendar eventDate = Calendar.getInstance();
//                        long timeInMillis = dataSnapshot.child("timeInMillis").getValue(long.class);
//                        eventDate.setTimeInMillis(timeInMillis);
//                        String eventTitle = dataSnapshot.child("eventTitle").getValue().toString();
//                        String eventHost = dataSnapshot.child("eventHost").getValue().toString();
//                        String address = dataSnapshot.child("address").getValue().toString();
//                        LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class), dataSnapshot.child("location").child("longitude").getValue(double.class));
//                        boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
//                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
//                        };
//                        HashMap<String, String> invited = (HashMap<String, String>) dataSnapshot.child("invited").getValue();
//                        HashMap<String,String> attendees = (HashMap<String, String>)dataSnapshot.child("attendees").getValue();
//                        DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
//                        List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);
//
//                        final EventDB event = new EventDB(eventID, eventTitle, eventHost, eventDate.getTimeInMillis(), address, location, categorizations, eventPrivate, invited, attendees, details);
//
//                        String date = df.format(timeInMillis);
//                        holder.mTitleTextView.setText(eventTitle);
//                        holder.mDateTextView.setText(date);
//                        if(!details.getPictures().get(0).isEmpty()){
//                            String downloadUrl = details.getPictures().get(0);
//                            Log.i("downloadurl", downloadUrl);
//                            Picasso.with(holder.itemView.getContext()).load(Uri.parse(downloadUrl)).fit().into(holder.mEventImageView);
//                        }
//                        holder.mTitleTextView.setTag(event);
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.w("error: ", "onCancelled", databaseError.toException());
//                }
//            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mPlaceDataset.length();//// needs to be changed
        }
    
}
