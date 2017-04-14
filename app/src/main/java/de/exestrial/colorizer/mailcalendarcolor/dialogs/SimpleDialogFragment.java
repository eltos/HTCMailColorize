package de.exestrial.colorizer.mailcalendarcolor.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import de.exestrial.colorizer.mailcalendarcolor.R;

/**
 * Created by Philipp on 17.04.2016.
 */
public class SimpleDialogFragment extends DialogFragment {
    public static final String TITLE = "dialog_title";
    public static final String MESSAGE = "dialog_message";
    public static final String POSITIVE_BUTTON_TEXT = "dialog_positiveButtonText";
    public static final String NEGATIVE_BUTTON_TEXT = "dialog_negativeButtonText";
    public static final String NEUTRAL_BUTTON_TEXT = "dialog_neutralButtonText";
    public static final String ICON_RESOURCE = "dialog_iconResource";
    public static final String CANCELABLE = "dialog_cancelable";
    public static final String BUNDLE = "dialog_bundle";

    public interface OnDialogFragmentResultListener {
        int CANCELED = 0;
        int BUTTON_POSITIVE = DialogInterface.BUTTON_POSITIVE;
        int BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE;
        int BUTTON_NEUTRAL = DialogInterface.BUTTON_NEUTRAL;

        void onDialogFragmentResult(String dialogTag, int which, Bundle extras);
    }

    private DialogInterface.OnClickListener forwardOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Bundle extras = getArguments() == null ? new Bundle() : getArguments().getBundle(BUNDLE);
            callResultListener(which, extras);
        }
    };

    public void callResultListener(int which, Bundle extras) {
        mOnClickListener.onDialogFragmentResult(getTag(), which, extras);
    }


    private OnDialogFragmentResultListener mOnClickListener;
    private AlertDialog dialog;

    public static SimpleDialogFragment newInstance(
            String title, String message, String positiveButton, String negativeButton) {
        return SimpleDialogFragment.newInstance(title, message, positiveButton, negativeButton, new Bundle());
    }
    public static SimpleDialogFragment newInstance(
            String title, String message, String positiveButton, String negativeButton, Bundle extras) {
        return SimpleDialogFragment.newInstance(title, message, positiveButton, negativeButton, null,
                null, true, extras);
    }
    public static SimpleDialogFragment newInstance(
            String title, String message, String positiveButton, String negativeButton, String neutralButton,
            Integer iconResource, boolean cancelable) {
        return SimpleDialogFragment.newInstance(title, message, positiveButton, negativeButton, neutralButton,
                iconResource, cancelable, new Bundle());
    }
    public static SimpleDialogFragment newInstance(
            String title, String message, String positiveButton, String negativeButton, String neutralButton,
            Integer iconResource, boolean cancelable, Bundle extras) {
        SimpleDialogFragment f = new SimpleDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putString(POSITIVE_BUTTON_TEXT, positiveButton);
        args.putString(NEGATIVE_BUTTON_TEXT, negativeButton);
        args.putString(NEUTRAL_BUTTON_TEXT, neutralButton);
        if (iconResource != null) args.putInt(ICON_RESOURCE, iconResource);
        args.putBoolean(CANCELABLE, cancelable);
        args.putBundle(BUNDLE, extras);
        f.setArguments(args);
        return f;


    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnClickListener = (OnDialogFragmentResultListener) activity;
        } catch (ClassCastException e) {
            mOnClickListener = new OnDialogFragmentResultListener() { @Override public void onDialogFragmentResult(String dialogTag, int which, Bundle extras) {} };
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnClickListener = new OnDialogFragmentResultListener() { @Override public void onDialogFragmentResult(String dialogTag, int which, Bundle extras) {} };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        setCancelable(getArguments().getBoolean(CANCELABLE));

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle).create();
        dialog.setTitle(getArguments().getString(TITLE));
        dialog.setMessage(getArguments().getString(MESSAGE));
        String positiveButtonText = getArguments().getString(POSITIVE_BUTTON_TEXT);
        if (positiveButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    positiveButtonText, forwardOnClickListener);
        }
        String negativeButtonText = getArguments().getString(NEGATIVE_BUTTON_TEXT);
        if (negativeButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    negativeButtonText, forwardOnClickListener);
        }
        String neutralButtonText = getArguments().getString(NEUTRAL_BUTTON_TEXT);
        if (neutralButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                    neutralButtonText, forwardOnClickListener);
        }
        int iconResourceId = getArguments().getInt(ICON_RESOURCE);
        if (iconResourceId != 0) {
            dialog.setIcon(iconResourceId);
        }
        dialog.setCancelable(getArguments().getBoolean(CANCELABLE));

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Bundle extras = getArguments() == null ? new Bundle() : getArguments().getBundle(BUNDLE);
        callResultListener(OnDialogFragmentResultListener.CANCELED, extras);

    }

    // This is to work around what is apparently a bug. If you don't have it
    // here the dialog will be dismissed on rotation, so tell it not to dismiss.
    @Override
    public void onDestroyView()
    {
        if (dialog != null && getRetainInstance())
            dialog.setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        try {
            return super.show(transaction, tag);
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

