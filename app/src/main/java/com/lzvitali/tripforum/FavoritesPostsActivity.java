package com.lzvitali.tripforum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

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
    private RecyclerView recyclerViewFavoritesActivity;
    private RecyclerViewTripAdapter mAdapter;
    private ChildEventListener mChildListener;

    private String mAllFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_posts);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.mTrips = FirebaseUtil.mTrips;

        recyclerViewFavoritesActivity = findViewById(R.id.recyclerViewFavoritesActivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get to 'mIdOfFavoritesTrips[]' all the id's of the favorites trips
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mAllFavorites = sharedPreferences.getString("userFavorites", "");


        mTrips.clear();
        initRecyclerView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void initRecyclerView()
    {
        addListenerForFirebase();
        mAdapter = new RecyclerViewTripAdapter("FavoritesPostsActivity");
        recyclerViewFavoritesActivity.setAdapter(mAdapter);
        recyclerViewFavoritesActivity.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addListenerForFirebase()
    {
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Trip trip = dataSnapshot.getValue(Trip.class);

                trip.setId(dataSnapshot.getKey());

                // check if the 'id' of the trip is in the favorites
                if(mAllFavorites.contains(trip.getId().toString()))
                {
                    mTrips.add(trip);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {

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

}
