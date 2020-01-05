package com.lzvitali.tripforum;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseUtil
{
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static ArrayList<Trip> mTrips;

    private FirebaseUtil(){};

    public static void openFbReference(String ref)
    {
        if (firebaseUtil == null)
        {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();

        }
        mTrips = new ArrayList<Trip>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);

        connectStorage();
    }


    public static void connectStorage()
    {
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("trips_pictures");
    }

}
