package com.lzvitali.tripforum;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DialogForDeleteTrip extends DialogFragment
{
    Trip mTripToDelete;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mStorage;
    int mPosition;
    Context mContext;


    // default constructor
    public DialogForDeleteTrip()
    {

    }

    // another constructor
    public DialogForDeleteTrip(String title, Trip tripToDelete, int pos, Context context)
    {
        Bundle args = new Bundle();
        args.putString("TitleKey", title);
        mTripToDelete = tripToDelete;
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.mStorage = FirebaseUtil.mStorage;
        this.mPosition = pos;

        mContext = context;

        setArguments(args);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dlg = new  AlertDialog.Builder(getActivity());

        Bundle bndl = getArguments();
        String title = bndl.getString("TitleKey", " ");

        dlg.setTitle(title);
        dlg.setMessage("You going to delete the trip permanently. Are you sure?");

        dlg.setPositiveButton("I'm sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                // delete from firebase database
                mDatabaseReference.child(mTripToDelete.getId()).removeValue();

                // delete from firebase storage (the image - if exist)
                if(mTripToDelete.getImageUrl() != null && !mTripToDelete.getImageUrl().equals(""))
                {
                    StorageReference storageReference = mStorage.getReferenceFromUrl(mTripToDelete.getImageUrl());
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully

                            Toast.makeText((AppCompatActivity)mContext, "Your post has been deleted from the App!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            Log.e("firebasestorage", "onFailure: did not delete file");
                        }
                    });
                }
                else
                {
                    Toast.makeText((AppCompatActivity)mContext, "Your post has been deleted from the App!", Toast.LENGTH_LONG).show();
                }

                MyPostsActivity.removeItemInPos(mPosition);


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //handle cancel situation...
            }
        });


        return dlg.create();
    }





}
