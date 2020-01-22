package com.lzvitali.tripforum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddNewTrip extends AppSuperClass
{
    // for debug
    // private String mTripType;
    // private String mPopulationType;
    // End: for debug

    private static final int PICTURE_RESULT = 42; //the answer to everything

    static final String EXTRA_TRIP_FOR_UPLOAD_SERVICE = "Trip for upload";
    static final String EXTRA_IMAGE_URI_FOR_UPLOAD_SERVICE = "image for upload";
    static final String EXTRA_FLAG_FOR_UPLOAD_SERVICE = "flag for upload";  // 'true' if there is image
                                                                            // 'false if no image

    static final String EXTRA_TRIP = "Trip";
    static final String EXTRA_CLASS_TO_RETURN = "class to return";

    // members
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    Uri mImageUri;
    boolean mIsUploadedPicture = false;
    Trip mNewTrip;
    Context thisContext = this;


    // xml members
    private EditText editTextCountry;
    private EditText editTextCity1;
    private EditText editTextCity2;
    private EditText editTextCity3;
    private EditText editTextDuration;
    private EditText editTextUserName;
    private EditText editTextUserEmail;
    private EditText editTextTripDescription;
    private Button buttonAddPhoto;
    private Button buttonCancel;
    private Button buttonPost;
    private Spinner spinnerPopulationType;
    private Spinner spinnerTripType;

    private TextView textViewCountryNameAddTrip;
    private TextView textViewCitesAddTrip;
    private TextView textViewDurationAddTrip;
    private TextView textViewUserNameAddTrip;
    private TextView textViewUserEmailAddTrip;
    private TextView textViewDescriptionAddTrip;

    private ImageView imageViewPhotoIndicator;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);

        // for the 'Back button' in the title (action) bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // disable opening of the keyboard automatically when Activity starts
        // reference: https://stackoverflow.com/questions/4149415/onscreen-keyboard-opens-automatically-when-activity-starts
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getViews();

        // get Firebase references from 'FirebaseUtil'
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        // turn off the 'ProgressBar'
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // setting the spinners
        // reference: https://developer.android.com/guide/topics/ui/controls/spinner.html
        setSpinnerPopulationTypeItems();
        setSpinnerTripTypeItems();

        // set 'OnCLick' on 'Add Photo' button
        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                mIsUploadedPicture = false;
                startActivityForResult(intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT);
            }
        });

        // set 'OnClick' on 'Post' button
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                postTrip();
            }
        });

        // set 'OnClick' on 'Cancel' button
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // go back to 'MainActivity'
                Intent i = new Intent(thisContext, MainActivity.class);
                startActivity(i);
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // for the 'Back button' in the title (action) bar
            case android.R.id.home:
                Intent intent = getIntent();
                String classToReturn = (String) intent.getSerializableExtra(EXTRA_CLASS_TO_RETURN);

                if(classToReturn.equals("MainActivity"))
                {
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                }
                else if(classToReturn.equals("MyPostsActivity"))
                {
                    Intent i = new Intent(this, MyPostsActivity.class);
                    startActivity(i);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Function that do all the 'findViewById' for the needed views
     */
    private void getViews()
    {
        spinnerPopulationType = (Spinner) findViewById(R.id.spinnerPopulationType);
        spinnerTripType = (Spinner) findViewById(R.id.spinnerTripType);


        editTextCountry = (EditText) findViewById(R.id.editTextCountry);
        editTextCity1 = (EditText) findViewById(R.id.editTextCity1);
        editTextCity2 = (EditText) findViewById(R.id.editTextCity2);
        editTextCity3 = (EditText) findViewById(R.id.editTextCity3);
        editTextDuration = (EditText) findViewById(R.id.editTextDuration);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
        editTextTripDescription = (EditText) findViewById(R.id.editTextTripDescription);
        editTextCountry = (EditText) findViewById(R.id.editTextCountry);
        editTextCountry = (EditText) findViewById(R.id.editTextCountry);

        buttonAddPhoto = (Button) findViewById(R.id.buttonAddPhoto);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonPost = (Button) findViewById(R.id.buttonPost);

        textViewCountryNameAddTrip = (TextView) findViewById(R.id.textViewCountryNameAddTrip);
        textViewCitesAddTrip = (TextView) findViewById(R.id.textViewCitesAddTrip);
        textViewDurationAddTrip = (TextView) findViewById(R.id.textViewDurationAddTrip);
        textViewUserNameAddTrip = (TextView) findViewById(R.id.textViewUserNameAddTrip);
        textViewUserEmailAddTrip = (TextView) findViewById(R.id.textViewUserEmailAddTrip);
        textViewDescriptionAddTrip = (TextView) findViewById(R.id.textViewDescriptionAddTrip);

        imageViewPhotoIndicator = (ImageView) findViewById(R.id.imageViewPhotoIndicator);
    }


    /**
     * Function for the 'OnClick' for the button 'Post'
     */
    private void postTrip()
    {
        // get all the Strings for posting
        String strEditTextCountry = editTextCountry.getText().toString();
        String strEditTextCity1 = editTextCity1.getText().toString();
        String strEditTextCity2 = editTextCity2.getText().toString();
        String strEditTextCity3 = editTextCity3.getText().toString();
        String strEditTextDuration = editTextDuration.getText().toString();

        String strSpinnerPopulationType = spinnerPopulationType.getSelectedItem().toString();;
        String strSpinnerTripType = spinnerTripType.getSelectedItem().toString();

        String strEditTextUserName = editTextUserName.getText().toString();
        String strEditTextUserEmail = editTextUserEmail.getText().toString();
        String strEditTextTripDescription = editTextTripDescription.getText().toString();

        // check the validation of user's input
        boolean isValid = false;
        String errorToReport = "";

        // ------------ check 'editTextCountry' ------------
        if(strEditTextCountry.equals(""))  //check if 'country' name is empty string
        {
            errorToReport = "You must fill the 'Country' field!";
            textViewCountryNameAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
        } // was: [a-zA-Z]+  reference for change: https://stackoverflow.com/questions/24191040/checking-to-see-if-a-string-is-letters-spaces-only
        else if(!strEditTextCountry.matches("^[ A-Za-z]+$"))  //check if 'country' name contains numbers
        {
            errorToReport = "'Country' name field must contain only letters!";
            textViewCountryNameAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
        }
        else
        {
            // set to black the color of 'textViewCountryNameAddTrip'
            textViewCountryNameAddTrip.setTextColor(Color.parseColor("#FF000000"));

            // ------------ check 'editTextCity[1,2,3]' ------------
            if(strEditTextCity1.equals(""))  //check if 'strEditTextCity1' name is empty string
            {
                errorToReport = "You must fill the first 'City' field!";
                textViewCitesAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
            }
            else if(!strEditTextCity1.matches("^[ A-Za-z]+$")
                    || ( !strEditTextCity2.equals("") && !strEditTextCity2.matches("^[ A-Za-z]+$") )
                    || ( !strEditTextCity3.equals("") && !strEditTextCity3.matches("^[ A-Za-z]+$")) )
            {
                errorToReport = "'City' name field must contain only letters!";
                textViewCitesAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
            }
            else
            {
                // set to black the color of 'textViewCountryNameAddTrip'
                textViewCitesAddTrip.setTextColor(Color.parseColor("#FF000000"));

                // ------------ check 'editTextDuration' ------------
                if(strEditTextDuration.equals(""))  //check if 'strEditTextDuration' is empty string
                {
                    errorToReport = "You must fill the 'Duration' field!";
                    textViewDurationAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
                }
                else if(!strEditTextDuration.matches("[0-9]+"))  //check if 'strEditTextDuration' contains only numbers
                {
                    errorToReport = "'Duration' name field must contain only  number!";
                    textViewDurationAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
                }
                else
                {
                    // set to black the color of 'textViewDurationAddTrip'
                    textViewDurationAddTrip.setTextColor(Color.parseColor("#FF000000"));

                    // ------------ check 'strEditTextUserName' ------------
                    if(!strEditTextCountry.matches("[a-zA-Z]+"))  //check if 'user' name contains numbers
                    {
                        errorToReport = "'Your name' name field must contain only letters!";
                        textViewUserNameAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
                    }
                    else
                    {
                        // set to black the color of 'textViewUserNameAddTrip'
                        textViewUserNameAddTrip.setTextColor(Color.parseColor("#FF000000"));

                        // TODO: add email validation check
                        if(strEditTextTripDescription.equals(""))  //check if 'strEditTextDuration' is empty string
                        {


                            errorToReport = "You must fill the 'Yor Trip' field!";
                            textViewDescriptionAddTrip.setTextColor(Color.parseColor("#FFFF0000"));
                        }
                        else
                        {
                            // set to black the color of 'textViewDescriptionAddTrip'
                            textViewDescriptionAddTrip.setTextColor(Color.parseColor("#FF000000"));

                            isValid = true;
                        }
                    }

                }

            }

        }


        if(isValid)
        {
            // disable all the buttons
            buttonPost.setEnabled(false);
            buttonCancel.setEnabled(false);
            buttonAddPhoto.setEnabled(false);

            // Capitalize the first letter
            // reference: https://stackoverflow.com/questions/3904579/how-to-capitalize-the-first-letter-of-a-string-in-java
            strEditTextCountry = strEditTextCountry.substring(0, 1).toUpperCase() + strEditTextCountry.substring(1);
            strEditTextCity1 = strEditTextCity1.substring(0, 1).toUpperCase() + strEditTextCity1.substring(1);
            if(!strEditTextCity2.equals(""))
            {
                strEditTextCity2 = strEditTextCity2.substring(0, 1).toUpperCase() + strEditTextCity2.substring(1);
            }
            if(!strEditTextCity3.equals(""))
            {
                strEditTextCity3 = strEditTextCity3.substring(0, 1).toUpperCase() + strEditTextCity3.substring(1);
            }


            // construct the 'Trip' instance
            mNewTrip = new Trip(strEditTextCountry, strEditTextCity1, strEditTextCity2,
                    strEditTextCity3, strEditTextDuration, strSpinnerPopulationType,
                    strSpinnerTripType, strEditTextUserName, strEditTextUserEmail,
                    strEditTextTripDescription);


            // make the upload of the 'Trip' with service
            Intent serviceIntent = new Intent(this, TripUploadService.class);
            serviceIntent.putExtra(EXTRA_FLAG_FOR_UPLOAD_SERVICE, mIsUploadedPicture);

            // get the user Uid
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null)
            {
                String uid = user.getUid();
                mNewTrip.setUserUid(uid);
            }

            // upload the photo (if user uploaded)
            if(mIsUploadedPicture)
            {
                Log.i("photo", "postTrip");

                serviceIntent.putExtra(EXTRA_TRIP_FOR_UPLOAD_SERVICE, mNewTrip);
                serviceIntent.putExtra(EXTRA_IMAGE_URI_FOR_UPLOAD_SERVICE, mImageUri);

                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            }
            else
            {
                serviceIntent.putExtra(EXTRA_TRIP_FOR_UPLOAD_SERVICE, mNewTrip);
            }

            startService(serviceIntent);

        }
        else
        {
            Toast.makeText(thisContext, errorToReport, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // receive the photo the user uploaded
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            Log.i("photo", "onActivityResult");
            mIsUploadedPicture = true;

            imageViewPhotoIndicator.setImageResource(R.drawable.remove_photo);
        }
    }

    /**
     * will remove the url of the photo that the user uploaded
     * 'onClick' function
     * @param view
     */
    public void removePhoto(View view)
    {
        if(mIsUploadedPicture)
        {
            mImageUri = null;
            mIsUploadedPicture = false;
            imageViewPhotoIndicator.setImageResource(R.drawable.no_photo);
        }
    }

    /**
     * Write the user input to the Firebase-database
     */
    private void writeToFireBaseDB()
    {
        // write to Firebase
        if(mNewTrip.getId() == null)
        {
            mDatabaseReference.push().setValue(mNewTrip);
        }
        else
        {
            mDatabaseReference.child(mNewTrip.getId()).setValue(mNewTrip);
        }

        // through a toast for the user
        Toast.makeText(thisContext, "Your trip uploaded successfully!", Toast.LENGTH_LONG).show();

        // go back to 'MainActivity'
        Intent i = new Intent(thisContext, MainActivity.class);
        startActivity(i);
    }


    //-------------------------- functions for the spinners ----------------------------------------
    public void setSpinnerPopulationTypeItems()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.population_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerPopulationType.setAdapter(adapter);

        // set listeners to the spinner
        spinnerPopulationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)

                // for debug
                // mPopulationType = spinnerPopulationType.getSelectedItem().toString();
                // Log.i("Spinner", "Spinner1: onItemSelected " + mPopulationType);
                // End: for debug
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // for debug
                // mPopulationType = "Family";
                // Log.i("Spinner", "Spinner1: onNothingSelected " + mPopulationType);
                // End: for debug
            }
        });

    }

    public void setSpinnerTripTypeItems()
    {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.trip_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTripType.setAdapter(adapter);

        // set listeners to the spinner
        spinnerTripType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)

                // for debug
                //mTripType = spinnerTripType.getSelectedItem().toString();
                //Log.i("Spinner", "Spinner2: onItemSelected " + mTripType);
                // End: for debug
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // for debug
                // mTripType = "Vacation";
                // Log.i("Spinner", "Spinner2: onNothingSelected " + mTripType);
                // End: for debug

            }
        });

    }
    // End: functions for the spinners -------------------------------------------------------------
}
