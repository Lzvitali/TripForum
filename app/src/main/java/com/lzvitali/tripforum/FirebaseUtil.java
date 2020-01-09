package com.lzvitali.tripforum;

import android.app.Activity;
import android.app.ListActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public static boolean isUserConnected = false;

    public static ArrayList<Trip> mTrips;
    private static TextView textViewUserName;

    private FirebaseUtil(){};

    public static void openFbReference(String ref, final MainActivity callerActivity)
    {
        caller = callerActivity;

        // get reference for the 'TextView' form the 'nav_header' layout and set it
        // reference: https://stackoverflow.com/questions/34973456/how-to-change-text-of-a-textview-in-navigation-drawer-header
        NavigationView navigationView = (NavigationView) caller.findViewById(R.id.navigation);
        View headerView = navigationView.getHeaderView(0);
        textViewUserName = (TextView) headerView.findViewById(R.id.textViewUserName);

        if (firebaseUtil == null)
        {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();


            // add listener for the authentication
            mFirebaseAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                FirebaseUser user;
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if ((user = firebaseAuth.getCurrentUser()) == null)
                    {
                        FirebaseUtil.signIn();

                    }
                    else {
//                        isUserConnected = true;
                        //String userId = firebaseAuth.getUid();

                        // Name, email of the user
//                        String name = user.getDisplayName();
//                        String email = user.getEmail();
//
//                        textViewUserName.setText("Hi, " + name);

                        //Toast.makeText(caller.getBaseContext(), "Welcome!", Toast.LENGTH_LONG).show();

//                        if(MainActivity.isAddNewTripPressed)
//                        {
//                            caller.openActivityAddNewTrip();
//                        }

                    }

                }
            };

            connectStorage();
        }

        checkIfuserConnected();

        mTrips = new ArrayList<Trip>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);

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

    public static void checkIfuserConnected()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // User is signed in
            isUserConnected = true;

            // set name of the user
            String name = user.getDisplayName();
            textViewUserName.setText("Hi, " + name);
        }
        else
        {
            // No user is signed in
            isUserConnected = false;
            textViewUserName.setText("Hi, you");

        }
    }

}
