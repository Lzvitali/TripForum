package com.lzvitali.tripforum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity
{
    public static final String SHARED_PREFS = "sharedPrefs";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    private static ArrayList<Trip> mTrips;

    private static RecyclerViewTripAdapter mAdapter;

    private RecyclerView recyclerViewMyPostsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        // get FireBase references from 'FirebaseUtil'
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.mTrips = FirebaseUtil.mTrips;

        recyclerViewMyPostsActivity = findViewById(R.id.recyclerViewMyPostsActivity);

        // for the 'Back button' in the title (action) bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initRecyclerView();

        // setQueryForUserPosts();  // commented this line because we do it in the onResume() func

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // for the 'Back button' in the title (action) bar
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initRecyclerView()
    {
        // addListenerForFirebase();  // commented this line because we do it in the onResume() func
        mAdapter = new RecyclerViewTripAdapter("MyPostsActivity");
        recyclerViewMyPostsActivity.setAdapter(mAdapter);
        recyclerViewMyPostsActivity.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This function will make a query for firebase to get all the posts of the current user
     */
    private void setQueryForUserPosts()
    {
        // get the user Uid
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            String uid = user.getUid();
            Query queryForUserPosts =mDatabaseReference
                    .orderByChild("userUid")
                    .equalTo(uid);

            queryForUserPosts.addChildEventListener(mChildListener);

            Log.i("myPosts", "uid: " + uid);
            mTrips.clear();
            mAdapter.notifyDataSetChanged();
        }

    }


    private void addListenerForFirebase()
    {
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Trip trip = dataSnapshot.getValue(Trip.class);

                trip.setId(dataSnapshot.getKey());

                mTrips.add(trip);
                mAdapter.notifyDataSetChanged();

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
//        mDatabaseReference.addChildEventListener(mChildListener);
    }


    /**
     * This function removes trip in position number pos
     * @param pos  position number
     */
    public static void removeItemInPos(int pos)
    {
        mTrips.remove(pos);
        mAdapter.notifyItemRemoved(pos);
    }


    /**
     * Override this func for updating the recyclerView when getting back to this activity
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        // reset the recycleView
        if(mTrips != null)
        {
            mTrips.clear();
        }
        addListenerForFirebase();

        setQueryForUserPosts();
    }

}
