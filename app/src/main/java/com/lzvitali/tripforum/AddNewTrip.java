package com.lzvitali.tripforum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddNewTrip extends AppCompatActivity
{
    // for debug
    // private String mTripType;
    // private String mPopulationType;
    // End: for debug

    private static final int PICTURE_RESULT = 42; //the answer to everything

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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);

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


        // TODO add validation checks
        boolean isValid = false;
//        if()

        isValid = true;  // for delete
        // End: TODO add validation checks

        if(isValid)
        {
            // construct the 'Trip' instance
            mNewTrip = new Trip(strEditTextCountry, strEditTextCity1, strEditTextCity2,
                    strEditTextCity3, strEditTextDuration, strSpinnerPopulationType,
                    strSpinnerTripType, strEditTextUserName, strEditTextUserEmail,
                    strEditTextTripDescription);

            // upload the photo (if user uploaded)
            if(mIsUploadedPicture)
            {
                Log.i("photo", "postTrip");

                // reference: https://firebase.google.com/docs/storage/android/upload-files?authuser=0
                //            https://stackoverflow.com/questions/50554548/error-cannot-find-symbol-method-getdownloadurl-of-type-com-google-firebase-st
                final StorageReference ref = FirebaseUtil.mStorageRef.child(mImageUri.getLastPathSegment());
                UploadTask  uploadTask = ref.putFile(mImageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        //ref.getName();
                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            mNewTrip.setImageUrl(downloadUri.toString());

                        // write to Firebase
                        if(mNewTrip.getId() == null)
                        {
                            mDatabaseReference.push().setValue(mNewTrip);
                        }
                        else
                        {
                            mDatabaseReference.child(mNewTrip.getId()).setValue(mNewTrip);
                        }

                        // turn off the 'ProgressBar'
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        // through a toast for the user
                        Toast.makeText(thisContext, "Your trip uploaded successfully!", Toast.LENGTH_LONG).show();

                        // go back to 'MainActivity'
                        Intent i = new Intent(thisContext, MainActivity.class);
                        startActivity(i);
                        Log.i("photo", "onSuccess" + downloadUri.toString());

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            }





        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            Log.i("photo", "onActivityResult");
            mIsUploadedPicture = true;
        }
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
