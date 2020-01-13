package com.lzvitali.tripforum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FavoritesPostsActivity extends AppCompatActivity
{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    public static final String SHARED_PREFS = "sharedPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ArrayList<Trip> mTrips;
    private RecyclerView recyclerView;
    private RecyclerViewTripAdapter mAdapter;
    private ChildEventListener mChildListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_posts);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.mTrips = FirebaseUtil.mTrips;
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

  //      initRecyclerView();
    }



//
//
//    private void initRecyclerView()
//    {
//        addListenerForFirebase();
//        mAdapter = new RecyclerViewTripAdapter();
//        recyclerView.setAdapter(mAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//    private void addListenerForFirebase()
//    {
//        mChildListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                Trip trip = dataSnapshot.getValue(Trip.class);
//
////                if(checkIfTripIsRelevant(trip))
////                {
//                    trip.setId(dataSnapshot.getKey());
//                    mTrips.add(trip);
//                    //notifyItemInserted(mTrips.size()-1);
//                    //mAdapter.notifyItemInserted(mTrips.size()-1);
//                    mAdapter.notifyDataSetChanged();
//               // }
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s)
//            {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        mDatabaseReference.addChildEventListener(mChildListener);
//    }

}
