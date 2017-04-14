package de.exestrial.colorizer.mailcalendarcolor.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Philipp on 18.04.2016.
 */
public abstract class SimpleIndeterminateDialogAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {




    private ProgressDialog dialog;
    //public String mMessage = "…";
    //private Context context

    public SimpleIndeterminateDialogAsyncTask(Context context, String dialogMessage){
        dialog = new ProgressDialog(context);
        dialog.setMessage(dialogMessage);
    }

    @Override
    protected void onPreExecute() {

        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(Result result) {
        try{
            dialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }

        super.onPostExecute(result);
    }
}
