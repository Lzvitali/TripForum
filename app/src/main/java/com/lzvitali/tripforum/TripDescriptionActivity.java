package com.lzvitali.tripforum;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class TripDescriptionActivity extends AppCompatActivity
{

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
    private Trip tripToShow;
    private String allFavorites;

    public static final String SHARED_PREFS = "sharedPrefs";
    public  String idToSaveForFavorites;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_description);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
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

    // set information about trip in to xml
    private void setTripDescription()
    {
        Intent intent = getIntent();
         tripToShow = (Trip) intent.getSerializableExtra("Trip");
        if (tripToShow==null) {
            tripToShow = new Trip();
        }
        this.tripToShow = tripToShow;

        String tripName = "<b>" + "Trip to " + tripToShow.getCountryName() + "</b>";
        textViewCountryTripDescrip.setText(Html.fromHtml(tripName, Build.VERSION.SDK_INT));

        String tripCities = "<b>" + "Cities: " + "</b>" + tripToShow.getCity1();
        if(!tripToShow.getCity2().equals(""))
        {
            tripCities += ", " + tripToShow.getCity2();
        }
        if(!tripToShow.getCity3().equals(""))
        {
            tripCities += ", " + tripToShow.getCity3();
        }
        textViewCitiesTripDescrip.setText(Html.fromHtml(tripCities, Build.VERSION.SDK_INT));

        String tripCategories = "<b>" + "Categories: " + "</b>" + tripToShow.getTripPopulationCategory() + " and " + tripToShow.getTripTypeCategory() + " trip.";
        textViewCategoriesTripDescrip.setText( Html.fromHtml(tripCategories, Build.VERSION.SDK_INT));

        String tripDuration = "<b>" + "Duration: " + "</b>" + tripToShow.getDuration() + " days.";
        textViewDurationTripDescrip.setText( Html.fromHtml(tripDuration, Build.VERSION.SDK_INT));

        String tripDescription = "<b>" + "Description: " + "</b>" + tripToShow.getTripDescription();
        textViewDescriptionTripDescrip.setText(Html.fromHtml(tripDescription, Build.VERSION.SDK_INT));

        String userName = "<b>" + "By: " + "</b>" + tripToShow.getUserName();
        if( tripToShow.getUserName().equals(""))
        {
            userName = "<b>" + "By: " + "</b>" + "Anonymous";
        }
        textViewByTripDescrip.setText( Html.fromHtml(userName, Build.VERSION.SDK_INT));

        String userEmail = "<b>" + "Email: " + "</b>" + tripToShow.getUserEmail();
        if(  tripToShow.getUserEmail().equals(""))
        {
            userEmail = "<b>" + "Email: " + "</b>" + "anonymous@gmail.com";
        }
        textViewEmailTripDescrip.setText( Html.fromHtml(userEmail, Build.VERSION.SDK_INT));

        showImage(tripToShow.getImageUrl());

        //check if it must be "full heart" picture
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        allFavorites = sharedPreferences.getString("userFavorites", "");
        if(allFavorites.contains(tripToShow.getId().toString()))
        {
            buttonFavorites.setImageResource(R.drawable.ic_favorites);
            buttonFavorites.setTag("favorite");
        }
    }

    // "on clic' on "heart" -- add this trip to favorites or remove
    public void addToFavorites(View view)
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


    // save this trip in sp memory for favorites trips
    private void removeFromFavorites()
    {
       // SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        allFavorites = sharedPreferences.getString("userFavorites", "");
        editor = sharedPreferences.edit();
        String idToDelete=tripToShow.getId().toString();

       // Log.i("favorite", "Before Removed--  " + allFavorites);
        //remove this id from old string in file
        allFavorites = allFavorites.replace(idToDelete, "");

        // change old string to new string(without deleted id)
        editor.putString("userFavorites",allFavorites );
        editor.apply();

      //  Log.i("favorite", "Removed--  " + allFavorites);


    }

    // save this trip in sp memory for favorites trips
    public void saveDataForFavorites()
    {
     editor = sharedPreferences.edit();

    //save id of favorite trip in file. upload data from file and add new
      allFavorites = sharedPreferences.getString("userFavorites", "");
    //  Log.i("favorite", "Before Added -- " + allFavorites);
      allFavorites+=tripToShow.getId().toString();

      editor.putString("userFavorites",allFavorites );
      editor.apply();
     // Log.i("favorite", "Added -- " + allFavorites);
      //Toast.makeText(this, "Data saved to Favorites!", Toast.LENGTH_SHORT).show();
}


    //func to show image
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
