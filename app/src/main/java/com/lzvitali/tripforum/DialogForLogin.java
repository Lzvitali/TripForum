package com.lzvitali.tripforum;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogForLogin extends DialogFragment
{
    // default constructor
    public DialogForLogin()
    {

    }

    // another constructor
    public DialogForLogin(String title)
    {
        Bundle args = new Bundle();
        args.putString("TitleKey", title);

        setArguments(args);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dlg = new  AlertDialog.Builder(getActivity());

        Bundle bndl = getArguments();
        String title = bndl.getString("TitleKey", " ");

        dlg.setTitle(title);
        dlg.setMessage("You should Log-in for adding new Trips!");

        dlg.setPositiveButton("Log-in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUtil.attachListener();
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
