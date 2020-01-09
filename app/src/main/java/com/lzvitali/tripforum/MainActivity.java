package com.lzvitali.tripforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private static final int RC_SIGN_IN = 123;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    private RecyclerView recyclerView;
    RecyclerViewTripAdapter mAdapter;

    public final Context mContext = this;

    public static boolean isAddNewTripPressed = false;
    MenuItem mSelectedItemFromNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // disable opening of the keyboard automatically when Activity starts
        // reference: https://stackoverflow.com/questions/4149415/onscreen-keyboard-opens-automatically-when-activity-starts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getViews();

        FirebaseUtil.openFbReference("trips", this);

        createMenu();

        initRecyclerView();
    }


    private void getViews()
    {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewMainActivity);
    }


    private void initRecyclerView()
    {
        mAdapter = new RecyclerViewTripAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //-------------------------- functions for the menu ------------------------------------------
    // function that creates the menu.
    void createMenu()
    {
        //set the menu
        mNavigationView = findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        // when open the menu - unmark all the items
        // reference: https://stackoverflow.com/questions/36051045/how-to-uncheck-checked-items-in-navigation-view
        int size = mNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            mNavigationView.getMenu().getItem(i).setCheckable(false);
        }

        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        if(R.id.btnMenuLogin == item.getItemId())
        {
            FirebaseUtil.checkIfuserConnected();
            if(FirebaseUtil.isUserConnected)
            {
                //logout if connected
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(mContext, "You Logged out!", Toast.LENGTH_LONG).show();

                                FirebaseUtil.checkIfuserConnected();
                            }
                        });

                FirebaseUtil.detachListener();
            }
            else
            {
                //login if not connected
                // attach the listener that will produce activity of the login
                FirebaseUtil.attachListener();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        mSelectedItemFromNavigation = item;
        switch (item.getItemId())
        {
            case R.id.nav_add_trip:
                // go to activity "add trip"
                isAddNewTripPressed = true;

                FirebaseUtil.checkIfuserConnected();
                if(!FirebaseUtil.isUserConnected)
                {
                    new DialogForLogin("Alert").show(getSupportFragmentManager(),null);
                }
                else
                {
                    openActivityAddNewTrip();
                }

                return true;
            case R.id.nav_my_posts:
                //TODO add action
                item.setCheckable(true);
                //return true;
            case R.id.nav_favorites:
//                Intent i = new Intent(this, TripDescriptionActivity.class);
//                mDrawerLayout.closeDrawer(GravityCompat.START);  // hide the Navigation menu
//                mSelectedItemFromNavigation.setCheckable(true);  // mark the selection
//                startActivity(i);
                return true;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openActivityAddNewTrip()
    {
        Intent i = new Intent(this, AddNewTrip.class);
        mDrawerLayout.closeDrawer(GravityCompat.START);  // hide the Navigation menu
        mSelectedItemFromNavigation.setCheckable(true);  // mark the selection
        startActivity(i);
    }

    // For the top menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top, menu);
        return true;
    }

    // End :functions for the menu ----------------------------------------------------------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            FirebaseUtil.checkIfuserConnected();

            // Successfully signed in
            if (resultCode == RESULT_OK)
            {
                if(MainActivity.isAddNewTripPressed)
                {
                    openActivityAddNewTrip();
                }
            }
            else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    return;
                }
            }
        }
    }



}
