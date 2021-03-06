package com.lzvitali.tripforum;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewTripAdapter extends RecyclerView.Adapter<RecyclerViewTripAdapter.TripViewHolder>
{
    static final String EXTRA_TRIP = "Trip";
    static final String EXTRA_CLASS_TO_RETURN = "class to return";
    ArrayList<Trip> mTrips;
    String mClassToReturn;



    // -----------------------------------Constructor---------------------------------------------
    public RecyclerViewTripAdapter(String classToReturn)
    {
        this.mTrips = FirebaseUtil.mTrips;
        this.mClassToReturn = classToReturn;
    }
    // End: Constructor----------------------------------------------------------------------------


    // -------------------------- Inner class: TripViewHolder--------------------------------------
    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnLongClickListener
    {
        TextView textViewTripName;
        TextView textViewTripCities;
        TextView textViewTripDescription;
        TextView textViewTripAuthor;
        public ImageView imageViewTripPhoto;
        LinearLayout linearLayoutRow;


        public TripViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewTripName = (TextView)itemView.findViewById(R.id.textViewTripName);
            textViewTripCities = (TextView)itemView.findViewById(R.id.textViewTripCities);
            textViewTripDescription = (TextView)itemView.findViewById(R.id.textViewTripDescription);
            textViewTripAuthor = (TextView)itemView.findViewById(R.id.textViewTripAuthor);
            imageViewTripPhoto = (ImageView)itemView.findViewById(R.id.imageViewTripPhoto);
            linearLayoutRow = (LinearLayout) itemView.findViewById(R.id.linearLayoutRow);

            // set onClicks for itemView (itemView is the row in the recyclerView)
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        public void bind(Trip trip)
        {
            String tripName = "<b>" + "Trip to " + trip.getCountryName() + "</b>";
            String tripCities = "<b>" + "Cities: " + "</b>" + trip.getCity1();
            if(!trip.getCity2().equals(""))
            {
                tripCities += ", " + trip.getCity2();
            }
            if(!trip.getCity3().equals(""))
            {
                tripCities += ", " + trip.getCity3();
            }
            String tripDesc = "<b>" + "Description: " + "</b>" + trip.getDuration() + " days, " +
                    trip.getTripPopulationCategory() + ", " + trip.getTripTypeCategory() + " trip.";
            String tripAuthor;
            if(trip.getUserName().equals(""))
            {
                tripAuthor = "<b>" + "By: " + "</b>" + "Anonymous";
            }
            else
            {
                tripAuthor = "<b>" + "By: " + "</b>" + trip.getUserName();
            }


            textViewTripName.setText(Html.fromHtml(tripName, Build.VERSION.SDK_INT));
            textViewTripCities.setText(Html.fromHtml(tripCities, Build.VERSION.SDK_INT));
            textViewTripDescription.setText(Html.fromHtml(tripDesc, Build.VERSION.SDK_INT));
            textViewTripAuthor.setText(Html.fromHtml(tripAuthor, Build.VERSION.SDK_INT));
            showImage(trip.getImageUrl());

        }


        private void showImage(String url)
        {
            Log.i("photo", "showImage " + url);
            imageViewTripPhoto.setImageDrawable (null);
            imageViewTripPhoto.setImageResource(R.drawable.g5);
            Picasso.get().cancelRequest(imageViewTripPhoto);
            if (url != null && !url.isEmpty())
            {
                Picasso.get()
                        .load(url)
                        .resize(140, 140)
                        .centerCrop()
                        .into(imageViewTripPhoto);

            }
        }


        // open activity with description about the clicked trip row
        @Override
        public void onClick(View v)
        {
            if(mClassToReturn.equals("MainActivity") || mClassToReturn.equals("FavoritesPostsActivity")
            || mClassToReturn.equals("MyPostsActivity"))
            {
                Trip tripToShow = mTrips.get(getAdapterPosition());
                Intent intent = new Intent(v.getContext(), TripDescriptionActivity.class);
                intent.putExtra(EXTRA_TRIP, tripToShow);
                intent.putExtra(EXTRA_CLASS_TO_RETURN, mClassToReturn);
                v.getContext().startActivity(intent);//open activity and send object to this activity
            }
            // TODO: For future development - make the option to update trips
//            else if(mClassToReturn.equals("MyPostsActivity"))
//            {
//                Trip tripToShow = mTrips.get(getAdapterPosition());
//                Intent intent = new Intent(v.getContext(), AddNewTrip.class);
//                intent.putExtra(EXTRA_TRIP, tripToShow);
//                intent.putExtra(EXTRA_CLASS_TO_RETURN, mClassToReturn);
//                v.getContext().startActivity(intent);//open activity and send object to this activity
//            }

        }

        @Override
        public boolean onLongClick(View v)
        {
            if(mClassToReturn.equals("MyPostsActivity"))
            {
                Context thisContext = v.getContext();
                Trip tripToDelete = mTrips.get(getAdapterPosition());
                new DialogForDeleteTrip("Alert", tripToDelete, getAdapterPosition(), thisContext).show(((AppCompatActivity)thisContext).getSupportFragmentManager(),null);


            }
            return false;
        }
    }
    // End: Inner class: TripViewHolder------------------------------------------------------------



    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_trip, parent, false);
        TripViewHolder holder = new TripViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position)
    {
        Trip deal = mTrips.get(position);
        //Picasso.get().cancelRequest(holder.imageViewTripPhoto);
        holder.bind(deal);
    }

    @Override
    public int getItemCount()
    {
        return mTrips.size();
    }


}
