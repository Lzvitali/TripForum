package com.lzvitali.tripforum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    Trip tripToShow;

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
        Trip tripToShow = (Trip) intent.getSerializableExtra("Trip");
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

        String tripCategories = "<b>" + "Categories: " + "</b>" + tripToShow.getTripPopulationCategory() + ", " + tripToShow.getTripTypeCategory() + " trip.";
        textViewCategoriesTripDescrip.setText( Html.fromHtml(tripCategories, Build.VERSION.SDK_INT));

       // textViewDurationTripDescrip.setText(tripToShow.getPrice());
       showImage(tripToShow.getImageUrl());

    }

    public void addToFavorites(View view)
    {
        buttonFavorites.setImageResource(R.drawable.ic_favorites);

    }

    private void showImage(String url)
    {
        Log.i("photo", "showImage " + url);
        if (url != null && !url.isEmpty())
        {
            Picasso.get()
                    .load(url)
                    .resize(140, 140)
                    .centerCrop()
                    .into(imageViewTripDescrip);
        }
    }






}
