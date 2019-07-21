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
import com.evaquint.android.utils.dataStructures.DetailedEvent;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.evaquint.android.utils.listeners.CustomItemClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;

/**
 * Created by amrmahmoud on 2019-07-21.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>{
        private SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM d, yyyy hh:mm aa");
        private String[] mDataset;
        CustomItemClickListener listener;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTitleTextView;
            public TextView mDateTextView;
            public ImageView mEventImageView;
            public ViewHolder(View v) {
                super(v);
                mTitleTextView = (TextView) v.findViewById(R.id.eventRecyclerTitle);
                mDateTextView = (TextView) v.findViewById(R.id.eventRecylerDate);
                mEventImageView = (ImageView) v.findViewById(R.id.eventRecyclerImg);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public EventListAdapter(String[] myDataset, CustomItemClickListener listener) {
            mDataset = myDataset;
            this.listener = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean selected = (boolean)v.getTag();
//                    v.setTag(!selected);
//                    if(selected){
//                        v.setBackgroundColor(Color.GREEN);
//                    }else{
//                        v.setBackgroundColor(Color.TRANSPARENT);
//                    }
//                    listener.onItemClick(v, vh.getAdapterPosition());
//                }
//            });
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element


            //get element from database and set items
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(mDataset[position]);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        Log.i("datasnapshot", dataSnapshot.toString());
                        String eventID = dataSnapshot.child("eventID").getValue().toString();
                        Calendar eventDate = Calendar.getInstance();
                        long timeInMillis = dataSnapshot.child("timeInMillis").getValue(long.class);
                        eventDate.setTimeInMillis(timeInMillis);
                        String eventTitle = dataSnapshot.child("eventTitle").getValue().toString();
                        String eventHost = dataSnapshot.child("eventHost").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class), dataSnapshot.child("location").child("longitude").getValue(double.class));
                        boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                        };
                        HashMap<String, String> invited = (HashMap<String, String>) dataSnapshot.child("invited").getValue();
                        HashMap<String,String> attendees = (HashMap<String, String>)dataSnapshot.child("attendees").getValue();
                        DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
                        List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);

                        final EventDB event = new EventDB(eventID, eventTitle, eventHost, eventDate.getTimeInMillis(), address, location, categorizations, eventPrivate, invited, attendees, details);

                        String date = df.format(timeInMillis);
                        holder.mTitleTextView.setText(eventTitle);
                        holder.mDateTextView.setText(date);
                        if(!details.getPictures().get(0).isEmpty()){
                            String downloadUrl = details.getPictures().get(0);
                            Log.i("downloadurl", downloadUrl);
                            Picasso.with(holder.itemView.getContext()).load(Uri.parse(downloadUrl)).fit().into(holder.mEventImageView);
                        }
                        holder.mTitleTextView.setTag(event);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    
}
