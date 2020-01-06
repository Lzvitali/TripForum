package com.lzvitali.tripforum;

import android.app.Activity;
import android.app.ListActivity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil
{
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;

    // attributes for authentication
    private static final int RC_SIGN_IN = 123;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private static MainActivity caller;

    public static ArrayList<Trip> mTrips;

    private FirebaseUtil(){};

    public static void openFbReference(String ref, MainActivity callerActivity)
    {
        if (firebaseUtil == null)
        {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();


            // add listener for the authentication
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null)
                    {
                        FirebaseUtil.signIn();
                    }
                    else {
                        //String userId = firebaseAuth.getUid();
                    }
                    Toast.makeText(caller.getBaseContext(), "Welcome!", Toast.LENGTH_LONG).show();
                }
            };

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


    private static void signIn()
    {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.logo)      // Set logo drawable
                        .setTheme(R.style.MySuperAppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }


    public static void attachListener()
    {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
    public static void detachListener()
    {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

}
