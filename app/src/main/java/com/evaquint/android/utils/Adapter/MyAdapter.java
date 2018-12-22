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
import com.evaquint.android.fragments.UserProfileFrag;
import com.evaquint.android.popups.EventAttendeesFrag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;


/**
 * Created by amrmahmoud on 2018-12-21.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private String[] mDataset;
    private EventAttendeesFrag frag;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;

        public MyViewHolder(View v) {
            super(v);
//            mTextView = v;
            mTextView = (TextView) v.findViewById(R.id.aName);
            mImageView = (ImageView) v.findViewById(R.id.aImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset,EventAttendeesFrag f) {
        frag=f;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendee_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(mDataset[position]);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                    Log.i("datasnapshot", dataSnapshot.toString());
                    String fullName = dataSnapshot.child("firstName").getValue(String.class)+" "+dataSnapshot.child("lastName").getValue(String.class);
                    holder.mTextView.setText(fullName);

                    Log.i("name", fullName);
                    holder.itemView.setTag(mDataset[position]);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserProfileFrag userProfileFrag = new UserProfileFrag().newInstance(holder.itemView.getTag().toString());
                            frag.dismiss();
                            setActiveFragment(frag, userProfileFrag);

//                            Log.i("clicked",holder.itemView.getTag().toString());
                        }
                    });
                    if(!dataSnapshot.child("picture").getValue(String.class).equals("default")){
                        String downloadUrl = dataSnapshot.child("picture").getValue(String.class);
                        Log.i("downloadurl", downloadUrl);
                        Picasso.with(holder.itemView.getContext()).load(Uri.parse(downloadUrl)).fit().into(holder.mImageView);
                        //   URL url = dataSnapshot.child("picture").getValue(URL.class);
                    }
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
