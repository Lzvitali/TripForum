package com.lzvitali.tripforum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class TripDescriptionActivity extends AppSuperClass
{

    static final String EXTRA_TRIP = "Trip";
    static final String EXTRA_CLASS_TO_RETURN = "class to return";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    ImageView buttonFavorites;
    TextView textViewCountryTripDescrip;
    TextView textViewCitiesTripDescrip;
    TextView textViewDurationTripDescrip;
    TextView textViewCategoriesTripDescrip;
    TextView textViewDescriptionTripDescrip;
    TextView textViewByTripDescrip;
    TextView textViewEmailTripDescrip;
    ImageView imageViewTripDescrip;
    private Trip mTripToShow;
    private String mAllFavorites;

    public static final String SHARED_PREFS = "sharedPrefs";
    public  String idToSaveForFavorites;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_description);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getViews();
        setTripDescription();

    }


    private void getViews()
    {
        buttonFavorites = (ImageView)findViewById(R.id.buttonFavorites);
        textViewCountryTripDescrip = (TextView)findViewById(R.id.textViewCountryTripDescrip);
        textViewCitiesTripDescrip = (TextView)findViewById(R.id.textViewCitiesTripDescrip);
        textViewDurationTripDescrip = (TextView)findViewById(R.id.textViewDurationTripDescrip);
        textViewCategoriesTripDescrip = (TextView)findViewById(R.id.textViewCategoriesTripDescrip);
        textViewDescriptionTripDescrip = (TextView)findViewById(R.id.textViewDescriptionTripDescrip);
        textViewByTripDescrip = (TextView)findViewById(R.id.textViewByTripDescrip);
        textViewEmailTripDescrip = (TextView)findViewById(R.id.textViewEmailTripDescrip);
        imageViewTripDescrip=(ImageView)findViewById(R.id.imageViewTripDescrip);

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


    /**
     * set information about trip in to xml
     */
    private void setTripDescription()
    {
        Intent intent = getIntent();
        mTripToShow = (Trip) intent.getSerializableExtra(EXTRA_TRIP);
        if (mTripToShow ==null) {
            mTripToShow = new Trip();
        }
        this.mTripToShow = mTripToShow;

        String tripName = "<b>" + "Trip to " + mTripToShow.getCountryName() + "</b>";
        textViewCountryTripDescrip.setText(Html.fromHtml(tripName, Build.VERSION.SDK_INT));

        String tripCities = "<b>" + "Cities: " + "</b>" + mTripToShow.getCity1();
        if(!mTripToShow.getCity2().equals(""))
        {
            tripCities += ", " + mTripToShow.getCity2();
        }
        if(!mTripToShow.getCity3().equals(""))
        {
            tripCities += ", " + mTripToShow.getCity3();
        }
        textViewCitiesTripDescrip.setText(Html.fromHtml(tripCities, Build.VERSION.SDK_INT));

        String tripCategories = "<b>" + "Categories: " + "</b>" + mTripToShow.getTripPopulationCategory() + " and " + mTripToShow.getTripTypeCategory() + " trip.";
        textViewCategoriesTripDescrip.setText( Html.fromHtml(tripCategories, Build.VERSION.SDK_INT));

        String tripDuration = "<b>" + "Duration: " + "</b>" + mTripToShow.getDuration() + " days.";
        textViewDurationTripDescrip.setText( Html.fromHtml(tripDuration, Build.VERSION.SDK_INT));

        String tripDescription = "<b>" + "Description: " + "</b>" + mTripToShow.getTripDescription();
        textViewDescriptionTripDescrip.setText(Html.fromHtml(tripDescription, Build.VERSION.SDK_INT));

        String userName = "<b>" + "By: " + "</b>" + mTripToShow.getUserName();
        if( mTripToShow.getUserName().equals(""))
        {
            userName = "<b>" + "By: " + "</b>" + "Anonymous";
        }
        textViewByTripDescrip.setText( Html.fromHtml(userName, Build.VERSION.SDK_INT));

        String userEmail = "<b>" + "Email: " + "</b>" + mTripToShow.getUserEmail();
        if(  mTripToShow.getUserEmail().equals(""))
        {
            userEmail = "";
        }
        textViewEmailTripDescrip.setText( Html.fromHtml(userEmail, Build.VERSION.SDK_INT));

        showImage(mTripToShow.getImageUrl());

        //check if it must be "full heart" picture
        mSharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mAllFavorites = mSharedPreferences.getString("userFavorites", "");
        if(mAllFavorites.contains(mTripToShow.getId().toString()))
        {
            buttonFavorites.setImageResource(R.drawable.ic_favorites);
            buttonFavorites.setTag("favorite");
        }
    }

    /**
     * 'on click' on "heart" -- add this trip to favorites or remove
     * @param view
     */
    public void favoritesButtonClicked(View view)
    {
        // change picture for each click reference: https://stackoverflow.com/questions/19482272/how-to-check-which-image-is-linked-to-imageview-in-android
        if(buttonFavorites.getTag() != null && buttonFavorites.getTag().toString().equals("favorite")){
            buttonFavorites.setImageResource(R.drawable.ic_not_favorites);
            buttonFavorites.setTag("not_favorite");
            removeFromFavorites();
        }
        else {
            buttonFavorites.setImageResource(R.drawable.ic_favorites);
            buttonFavorites.setTag("favorite");
            saveDataForFavorites();
        }
    }


    /**
     * save this trip in sp memory for favorites trips
     */
    private void removeFromFavorites()
    {
       // SharedPreferences mSharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mAllFavorites = mSharedPreferences.getString("userFavorites", "");
        mEditor = mSharedPreferences.edit();
        String idToDelete= mTripToShow.getId().toString() + " ";

       // Log.i("favorite", "Before Removed--  " + mAllFavorites);
        //remove this id from old string in file
        mAllFavorites = mAllFavorites.replace(idToDelete, "");

        // change old string to new string(without deleted id)
        mEditor.putString("userFavorites", mAllFavorites);
        mEditor.apply();

      //  Log.i("favorite", "Removed--  " + mAllFavorites);


    }

    /**
     * save this trip in sp memory for favorites trips
     */
    public void saveDataForFavorites()
    {
        mEditor = mSharedPreferences.edit();

        //save id of favorite trip in file. upload data from file and add new
        mAllFavorites = mSharedPreferences.getString("userFavorites", "");
        // Log.i("favorite", "Before Added -- " + mAllFavorites);
        mAllFavorites += mTripToShow.getId().toString();
        mAllFavorites += " ";

        mEditor.putString("userFavorites", mAllFavorites);
        mEditor.apply();
        // Log.i("favorite", "Added -- " + mAllFavorites);
        //Toast.makeText(this, "Data saved to Favorites!", Toast.LENGTH_SHORT).show();
    }


    /**
     * func to show image
     * @param url of the image
     */
    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*4/5)
                    .centerCrop()
                    .into(imageViewTripDescrip);
        }
    }




}
