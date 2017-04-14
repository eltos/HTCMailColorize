package de.exestrial.colorizer.mailcalendarcolor.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by Philipp on 17.04.2016.
 */
public abstract class CustomViewDialogFragment extends SimpleDialogFragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);
        View content = onCreateContentView(savedInstanceState, dialog);
        dialog.setView(content);
        return dialog;
    }

    @Override
    public void callResultListener(int which, Bundle extras) {
        Bundle results = onResult(which);
        if (extras == null) extras = new Bundle();
        if (results != null) extras.putAll(results);
        super.callResultListener(which, extras);
    }

    /**
     * Inflate your custom view here.
     *
     * @param savedInstanceState
     * @return
     */
    public abstract View onCreateContentView(Bundle savedInstanceState, AlertDialog dialog);

    /**
     * Called if a button was pressed or the dialog was canceled.
     * Collect all the data from the dialog and store it in the bundle.
     *
     * The Attributes contained in the Bundle returned will be passed to
     * {@link OnDialogFragmentResultListener#onDialogFragmentResult}
     *
     * @param which see {@link SimpleDialogFragment.OnDialogFragmentResultListener}
     * @return
     */
    public abstract Bundle onResult(int which);


}
