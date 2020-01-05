package com.lzvitali.tripforum;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewTripAdapter extends RecyclerView.Adapter<RecyclerViewTripAdapter.TripViewHolder>
{

    ArrayList<Trip> mTrips;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;


    // -----------------------------------Constructor---------------------------------------------
    public RecyclerViewTripAdapter()
    {
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.mTrips = FirebaseUtil.mTrips;

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                trip.setId(dataSnapshot.getKey());
                mTrips.add(trip);
                notifyItemInserted(mTrips.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);
    }
    // End: Constructor----------------------------------------------------------------------------


    // -------------------------- Inner class: TripViewHolder--------------------------------------
    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textViewTripName;
        TextView textViewTripCities;
        TextView textViewTripDescription;
        TextView textViewTripAuthor;
        ImageView imageViewTripPhoto;
        LinearLayout linearLayoutRow;


        public TripViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewTripName = (TextView)itemView.findViewById(R.id.textViewTripName);
            textViewTripCities = (TextView)itemView.findViewById(R.id.textViewTripCities);
            textViewTripDescription = (TextView)itemView.findViewById(R.id.textViewTripDescription);
            textViewTripAuthor = (TextView)itemView.findViewById(R.id.textViewTripAuthor);
            imageViewTripPhoto = (ImageView)itemView.findViewById(R.id.imageViewTripPhoto);
            linearLayoutRow = (LinearLayout) itemView.findViewById(R.id.linearLayoutRow);

            itemView.setOnClickListener(this);
        }

        public void bind(Trip trip)
        {
            String tripName = "Trip to " + trip.getCountryName();
            String tripCities = "Cities: " + trip.getCity1();
            if(!trip.getCity2().equals(""))
            {
                tripCities += ", " + trip.getCity2();
            }
            if(!trip.getCity3().equals(""))
            {
                tripCities += ", " + trip.getCity3();
            }
            String tripDesc = "Description: " + trip.getDuration() + "days, " +
                    trip.getTripPopulationCategory() + ", " + trip.getTripTypeCategory() + " trip.";
            String tripAuthor;
            if(trip.getCountryName().equals(""))
            {
                tripAuthor = "By: Anonymous";
            }
            else
            {
                tripAuthor = "By: " + trip.getCountryName();
            }


            textViewTripName.setText(tripName);
            textViewTripCities.setText(tripCities);
            textViewTripDescription.setText(tripDesc);
            textViewTripAuthor.setText(tripAuthor);
            showImage(trip.getImageUrl());


//            if(setSecondBackground)
//            {
//                linearLayoutRow.setBackgroundResource(R.color.rv_row2);
//            }
        }


        private void showImage(String url)
        {
            Log.i("photo", "showImage " + url);
            if (url != null && !url.isEmpty())
            {
                Picasso.get()
                        .load(url)
                        .resize(140, 140)
                        .centerCrop()
                        .into(imageViewTripPhoto);
            }
        }


        @Override
        public void onClick(View v)
        {

        }
    }
    // End: Inner class: TripViewHolder------------------------------------------------------------




    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_trip, parent, false);
        TripViewHolder holder = new TripViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position)
    {
        Trip deal = mTrips.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount()
    {
        return mTrips.size();
    }


}