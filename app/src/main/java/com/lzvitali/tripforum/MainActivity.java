package com.lzvitali.tripforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    private RecyclerView recyclerView;
    RecyclerViewTripAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            FirebaseUtil.attachListener();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.nav_add_trip:
                // go to activity "add trip"
                Intent i = new Intent(this, AddNewTrip.class);
                mDrawerLayout.closeDrawer(GravityCompat.START);  // hide the Navigation menu
                item.setCheckable(true);  // mark the selection
                startActivity(i);


                return true;
            case R.id.nav_my_posts:
                //TODO add action
                item.setCheckable(true);
                //return true;
            case R.id.nav_favorites:
                //TODO add action
                item.setCheckable(true);
                //return true;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

}
