package com.lzvitali.tripforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MainActivity extends AppSuperClass implements NavigationView.OnNavigationItemSelectedListener
{

    private static final int RC_SIGN_IN = 123;
    static final String EXTRA_CLASS_TO_RETURN = "class to return";


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    private RecyclerView recyclerView;
    private RecyclerViewTripAdapter mAdapter;
    private ChildEventListener mChildListener;
    ArrayList<Trip> mTrips;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public final Context mContext = this;

    public static boolean isAddNewTripPressed = false;
    MenuItem mSelectedItemFromNavigation;

    private Button buttonSearch;
    private RadioGroup radioGroup;
    private RadioButton radioButtonCountry;
    private RadioButton radioButtonCity;
    private Button buttonClear;
    private CheckBox checkBoxFamily;
    private CheckBox checkBoxAdults;
    private CheckBox checkBoxAnyAge;
    private CheckBox checkBoxYoung;
    private CheckBox checkBoxVacation;
    private CheckBox checkBoxRomantic;
    private CheckBox checkBoxExtreme;
    private CheckBox checkBoxCultural;
    private EditText editTextSearch;

    private TextView textViewResultTitle;




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

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.mTrips = FirebaseUtil.mTrips;

        createMenu();

        initRecyclerView();


        // set 'OnCLick' on 'search' button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!editTextSearch.getText().toString().equals("") && -1 == radioGroup.getCheckedRadioButtonId())
                {
                    Toast.makeText(mContext, "Please select one of the radio buttons (country / city)", Toast.LENGTH_LONG).show();
                }
                else if(editTextSearch.getText().toString().equals("")
                        && !checkBoxFamily.isChecked() && !checkBoxYoung.isChecked()
                        && !checkBoxAdults.isChecked() && !checkBoxAnyAge.isChecked()
                        && !checkBoxVacation.isChecked() && !checkBoxRomantic.isChecked()
                        && !checkBoxExtreme.isChecked() && !checkBoxCultural.isChecked())
                {
                    Toast.makeText(mContext, "You didn't selected anything", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // take string from 'strings.xml': https://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
                    textViewResultTitle.setText(getResources().getString(R.string.activity_main_your_search_results));
                    ArrayList<QueryDataObject> selectedItemsArr;
                    selectedItemsArr = getSelectedRadioButtonsItems();
                    searchUserTrip(selectedItemsArr);
                }
            }
        });

        // set 'OnCLick' on 'Clear' button
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(editTextSearch.getText().toString().equals("")
                    && !checkBoxFamily.isChecked() && !checkBoxYoung.isChecked()
                    && !checkBoxAdults.isChecked() && !checkBoxAnyAge.isChecked()
                    && !checkBoxVacation.isChecked() && !checkBoxRomantic.isChecked()
                    && !checkBoxExtreme.isChecked() && !checkBoxCultural.isChecked())
                {
                Toast.makeText(mContext, "You didn't searched anything", Toast.LENGTH_LONG).show();
                }
                else
                {
                    clearSearch();
                }
            }
        });
    }


    private void initRecyclerView()
    {
        // addListenerForFirebase();  // commented this line because we do it in the onResume() func
        mAdapter = new RecyclerViewTripAdapter("MainActivity");
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * construct an ArrayList<QueryDataObject> of the content of the selected radio button
     * if no one selected a there will be one object to indicate that situation
     * @return ArrayList<QueryDataObject> of selected radio buttons
     */
    private ArrayList<QueryDataObject> getSelectedRadioButtonsItems()
    {
        ArrayList<QueryDataObject> selectedItemsArr = new ArrayList<QueryDataObject>();

        // one of the radio buttons is checked
        // reference: https://stackoverflow.com/questions/24992936/how-to-check-if-a-radiobutton-is-checked-in-a-radiogroup-in-android
        if (radioGroup.getCheckedRadioButtonId() != -1)
        {
            // if radioButtonCountry checked
            if(radioButtonCountry.isChecked())
            {
                QueryDataObject selectedRadioButton = new QueryDataObject("countryName", editTextSearch.getText().toString());
                selectedItemsArr.add(selectedRadioButton);
            }
            else
            {
                QueryDataObject selectedRadioButton1 = new QueryDataObject("city1", editTextSearch.getText().toString());
                QueryDataObject selectedRadioButton2 = new QueryDataObject("city2", editTextSearch.getText().toString());
                QueryDataObject selectedRadioButton3 = new QueryDataObject("city3", editTextSearch.getText().toString());
                selectedItemsArr.add(selectedRadioButton1);
                selectedItemsArr.add(selectedRadioButton2);
                selectedItemsArr.add(selectedRadioButton3);
            }

        }

        QueryDataObject selectedCheckBox = new QueryDataObject("no null key","no null value" );
        selectedItemsArr.add(selectedCheckBox);

        return  selectedItemsArr;
    }


    private void getViews()
    {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewMainActivity);
        buttonSearch = (Button)findViewById(R.id.buttonSearch);
        radioButtonCountry = (RadioButton)findViewById(R.id.radioButtonCountry);
        radioButtonCity = (RadioButton)findViewById(R.id.radioButtonCity);
        buttonClear = (Button)findViewById(R.id.buttonClear);
        checkBoxFamily = (CheckBox)findViewById(R.id.checkBoxFamily);
        checkBoxAdults = (CheckBox)findViewById(R.id.checkBoxAdults);
        checkBoxAnyAge = (CheckBox)findViewById(R.id.checkBoxAnyAge);
        checkBoxYoung = (CheckBox)findViewById(R.id.checkBoxYoung);
        checkBoxVacation = (CheckBox)findViewById(R.id.checkBoxVacation);
        checkBoxRomantic = (CheckBox)findViewById(R.id.checkBoxRomantic);
        checkBoxExtreme = (CheckBox)findViewById(R.id.checkBoxExtreme);
        checkBoxCultural = (CheckBox)findViewById(R.id.checkBoxCultural);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        editTextSearch = (EditText)findViewById(R.id.editTextSearch);
        textViewResultTitle = (TextView)findViewById(R.id.textViewResultTitle);

    }

    /**
     * Function the clears all the searching fields (including radio buttons and checkboxes)
     */
    private void clearSearch()
    {
        radioGroup.clearCheck();
        checkBoxFamily.setChecked(false);
        checkBoxAdults.setChecked(false);
        checkBoxAnyAge.setChecked(false);
        checkBoxYoung.setChecked(false);
        checkBoxVacation.setChecked(false);
        checkBoxRomantic.setChecked(false);
        checkBoxExtreme.setChecked(false);
        checkBoxCultural.setChecked(false);
        editTextSearch.setText("");

        // take string from 'strings.xml': https://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
        textViewResultTitle.setText(getResources().getString(R.string.activity_main_most_recent));

        // reset the recycleView
        mTrips.clear();
        addListenerForFirebase();

    }


    /**
     * This function sets a query for Firebase when radio buttons were selected:
     * when there is a search by Country/City we query that on the server side
     * and the the checkboxes requirements will be filtered on the client side
     * in the onChildAdded() func, with 'checkIfTripIsRelevant(trip)' func
     * @param selectedItemsArr
     */
    private void searchUserTrip(ArrayList<QueryDataObject> selectedItemsArr)
    {
        // make queries:
        // reference: https://github.com/probelalkhan/firebase-query-examples-android/blob/master/app/src/main/java/net/simplifiedcoding/firebasequeryexample/MainActivity.java
        //            https://stackoverflow.com/questions/26700924/query-based-on-multiple-where-clauses-in-firebase

        // if one of the radio button selected
        String firstParam = selectedItemsArr.get(0).getKey();  // firstParam -- the radio button key
        if(firstParam.equals("countryName") || firstParam.equals("city1") )
        {
            // if it is 'countryName'
            if(firstParam.equals("countryName"))
            {
                Query queryForCountry = FirebaseDatabase.getInstance().getReference("trips")
                        .orderByChild("countryName")
                        .equalTo(selectedItemsArr.get(0).getValue());

                queryForCountry.addChildEventListener(mChildListener);

            }
            // if it is city
            else
            {
                Query queryForCity1 = FirebaseDatabase.getInstance().getReference("trips")
                        .orderByChild("city1")
                        .equalTo(selectedItemsArr.get(0).getValue());

                Query queryForCity2 = FirebaseDatabase.getInstance().getReference("trips")
                        .orderByChild("city2")
                        .equalTo(selectedItemsArr.get(1).getValue());

                Query queryForCity3 = FirebaseDatabase.getInstance().getReference("trips")
                        .orderByChild("city3")
                        .equalTo(selectedItemsArr.get(2).getValue());

                queryForCity1.addChildEventListener(mChildListener);
                queryForCity2.addChildEventListener(mChildListener);
                queryForCity3.addChildEventListener(mChildListener);

            }
            // we will clear the result list in order to show the results
            mTrips.clear();
            mAdapter.notifyDataSetChanged();
        }
        // if we here - user not selected radio buttons and selected at least one of the check boxes
        else
        {
            //TODO: DELETE the commented code
            //check for each trip if it is still relevant
//            for(int i = mTrips.size() - 1; i >= 0; i--)
//            {
//                if(!checkIfTripIsRelevant(mTrips.get(i)))
//                {
//                    mTrips.remove(i);
//                    mAdapter.notifyItemRemoved(i);
//                }
//            }

            // clear the arrayList and get it fresh again
            // then the results will bee filtered in the onChildAdded() func
            // with 'checkIfTripIsRelevant(trip)' func
            mTrips.clear();
            addListenerForFirebase();

        }


    }


    private void addListenerForFirebase()
    {
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Trip trip = dataSnapshot.getValue(Trip.class);

                // 'checkIfTripIsRelevant()' is filtering the trips that are not relevant according
                // the check-boxes
                if(checkIfTripIsRelevant(trip))
                {
                    trip.setId(dataSnapshot.getKey());

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


    /**
     * This function filtering checks if the 'trip' is relevant according the check-boxes
     * @param trip - the trip to check
     * @return true if the trip is relevant for the user search
     */
    private boolean checkIfTripIsRelevant(Trip trip)
    {
        boolean isRelevantTrip = true;

        // check witch checkBoxes selected
        // reference: https://stackoverflow.com/questions/18336151/how-to-check-if-android-checkbox-is-checked-within-its-onclick-method-declared
        if(checkBoxFamily.isChecked())
        {
            if(null == trip.getTripPopulationCategory()
                    || !trip.getTripPopulationCategory().equals("Family"))
            {
                isRelevantTrip = false;
            }
        }
        if(checkBoxYoung.isChecked())
        {
            if(null == trip.getTripPopulationCategory()
                    || !trip.getTripPopulationCategory().equals("Young people"))
            {
                isRelevantTrip = false;
            }
        }
        if(checkBoxAdults.isChecked())
        {
            if(null == trip.getTripPopulationCategory()
                    || !trip.getTripPopulationCategory().equals("Adults"))
            {
                isRelevantTrip = false;
            }
        }
        if(checkBoxAnyAge.isChecked())
        {
            if(null == trip.getTripPopulationCategory()
                    || !trip.getTripPopulationCategory().equals("Any age"))
            {
                isRelevantTrip = false;
            }
        }
        if(checkBoxVacation.isChecked())
        {
            if(null == trip.getTripTypeCategory()
                    || !trip.getTripTypeCategory().equals("Vacation"))
            {
                isRelevantTrip = false;
            }
        }
        if(checkBoxRomantic.isChecked())
        {
            if(null == trip.getTripTypeCategory()
                    || !trip.getTripTypeCategory().equals("Romantic"))
            {
                isRelevantTrip = false;
            }
        }
        if(checkBoxExtreme.isChecked())
        {
            if(null == trip.getTripTypeCategory()
                    || !trip.getTripTypeCategory().equals("Extreme"))
            {
                isRelevantTrip = false;
            }
        }
        if(checkBoxCultural.isChecked())
        {
            if(null == trip.getTripTypeCategory()
                    || !trip.getTripTypeCategory().equals("Cultural"))
            {
                isRelevantTrip = false;
            }
        }

        return isRelevantTrip;
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
                FirebaseUtil.checkIfuserConnected();
                if(!FirebaseUtil.isUserConnected)
                {
                    new DialogForLogin("Alert").show(getSupportFragmentManager(),null);
                }
                else
                {
                    Intent i1 = new Intent(this, MyPostsActivity.class);
                    mDrawerLayout.closeDrawer(GravityCompat.START);  // hide the Navigation menu
                    mSelectedItemFromNavigation.setCheckable(true);  // mark the selection
                    startActivity(i1);
                    item.setCheckable(true);
                }

                return true;
            case R.id.nav_favorites:
                Intent i2 = new Intent(this, FavoritesPostsActivity.class);
                mDrawerLayout.closeDrawer(GravityCompat.START);  // hide the Navigation menu
                mSelectedItemFromNavigation.setCheckable(true);  // mark the selection
                startActivity(i2);
                return true;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openActivityAddNewTrip()
    {
        Intent i = new Intent(this, AddNewTrip.class);
        i.putExtra(EXTRA_CLASS_TO_RETURN, "MainActivity");

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


    /**
     * Override this func for updating the recyclerView when getting back to this activity
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        // this function does the clear of the search and reset of the recyclerView
        clearSearch();
    }
}
