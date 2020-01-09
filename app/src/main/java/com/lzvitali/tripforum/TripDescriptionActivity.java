package com.lzvitali.tripforum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TripDescriptionActivity extends AppCompatActivity {

    ImageView buttonFavorites;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_description);

        getViews();
    }

    private void getViews()
    {
        buttonFavorites = (ImageView)findViewById(R.id.buttonFavorites);
    }

    public void addToFavorites(View view)
    {
        buttonFavorites.setImageResource(R.drawable.ic_favorites);
    }
}
