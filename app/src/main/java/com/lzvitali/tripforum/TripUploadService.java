package com.lzvitali.tripforum;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.lzvitali.tripforum.App.CHANNEL_ID;

// reference for learning about services:
// https://codinginflow.com/tutorials/android/foreground-service

public class TripUploadService extends Service
{
    private DatabaseReference mDatabaseReference;

    static final String EXTRA_TRIP_FOR_UPLOAD_SERVICE = "Trip for upload";
    static final String EXTRA_IMAGE_URI_FOR_UPLOAD_SERVICE = "image for upload";
    static final String EXTRA_FLAG_FOR_UPLOAD_SERVICE = "flag for upload";  // 'true' if there is image
                                                                            // 'false if no image

    public static final String SHARED_PREFS = "sharedPrefs";
    private String mMyPosts;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;

    Trip mTripToUpload;
    Uri mImageUri;
    boolean mIsUploadedPicture;

    @Override
    public void onCreate()
    {
        // get Firebase reference from 'FirebaseUtil'
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        // get all the id's of the trips from Shared Preferences
        mSharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mMyPosts = mSharedPreferences.getString("userPosts", "");

        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        // get information from the intent
        // reference for getApplicationContext() :
        // https://stackoverflow.com/questions/25685993/pass-context-to-service-with-putextra
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, 0);
        mTripToUpload = (Trip) intent.getSerializableExtra(EXTRA_TRIP_FOR_UPLOAD_SERVICE);
        mIsUploadedPicture = (Boolean)intent.getBooleanExtra(EXTRA_FLAG_FOR_UPLOAD_SERVICE, false);



        // upload the photo (if user uploaded)
        if(mIsUploadedPicture)
        {
            // reference for pass a URI to an intent:https://stackoverflow.com/questions/8017374/how-to-pass-a-uri-to-an-intent/20284270
            mImageUri = (Uri) intent.getParcelableExtra(EXTRA_IMAGE_URI_FOR_UPLOAD_SERVICE);

            // set the image and trip upload
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
                        mTripToUpload.setImageUrl(downloadUri.toString());

                        // write the new trip to Firebase DB
                        writeToFireBaseDB();

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
        // upload only to Firebase data-base if there is no photo
        else
        {
            // write the new trip to Firebase (when no image was uploaded)
            writeToFireBaseDB();

        }



        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Trip uploading")
                .setContentText("your trip is uploading to the data-base")
                .setSmallIcon(R.drawable.ic_upload_trip)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    /**
     * Write the user input to the Firebase-database
     */
    private void writeToFireBaseDB()
    {
        // write to Firebase
        if(mTripToUpload.getId() == null)
        {
            // reference: https://firebase.google.com/docs/database/android/read-and-write
            mDatabaseReference.push().setValue(mTripToUpload)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            // Write was successful!

                            // through a toast for the user
                            Toast.makeText(getApplicationContext(), "Your trip uploaded successfully!", Toast.LENGTH_LONG).show();

                            // finish service and go back to the activity
                            Intent intent1 = new Intent("service finished");
                            sendBroadcast(intent1);

                            // stop the service
                            stopSelf();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Write failed

                            // stop the service
                            stopSelf();
                        }
                    });
        }
        else
        {
            mDatabaseReference.child(mTripToUpload.getId()).setValue(mTripToUpload)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!

                            // through a toast for the user
                            Toast.makeText(getApplicationContext(), "Your trip uploaded successfully!", Toast.LENGTH_LONG).show();

                            // finish service and go back to the activity
                            Intent intent1 = new Intent("service finished");
                            sendBroadcast(intent1);

                            // stop the service
                            stopSelf();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed

                            // stop the service
                            stopSelf();
                        }
                    });
        }

//        // through a toast for the user
//        Toast.makeText(getApplicationContext(), "Your trip uploaded successfully!", Toast.LENGTH_LONG).show();

//        // go back to 'MainActivity'
//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
    }



}