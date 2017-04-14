package de.exestrial.colorizer.mailcalendarcolor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import de.exestrial.colorizer.mailcalendarcolor.dialogs.SimpleDialogFragment;
import de.exestrial.colorizer.mailcalendarcolor.dialogs.SimpleIndeterminateDialogAsyncTask;

public class InfoActivity extends AppCompatActivity {

    public static final String ALWAYS = "1";
    private static final String P_KEY_ROOT = "rootTest";
    private static final String P_KEY_MAIL = "mailTest";
    private static final String P_KEY_CALENDAR = "calendarTest";
    private static final String P_KEY_TASK = "taskTest";
    private static final String P_KEY_FIRST = "firstStart";

    Button mNextButton;
    TextView mTopic;
    ProgressBar mSpinner;
    TableLayout mTable;
    ImageView mStateRoot;
    ImageView mStateMail;
    ImageView mStateCalendar;
    ImageView mStateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        mNextButton = (Button) findViewById(R.id.next);
        mTopic = (TextView) findViewById(R.id.topic);
        mSpinner = (ProgressBar) findViewById(R.id.progressBar);
        mTable = (TableLayout) findViewById(R.id.table);
        mStateRoot = (ImageView) findViewById(R.id.root_state);
        mStateMail = (ImageView) findViewById(R.id.mail_state);
        mStateCalendar = (ImageView) findViewById(R.id.calendar_state);
        mStateTask = (ImageView) findViewById(R.id.task_state);

        SharedPreferences pref = getSharedPreferences("preferences", MODE_PRIVATE);

        if (pref.getBoolean(P_KEY_FIRST, true)){
            // first
            mTable.setVisibility(View.GONE);
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTable.setVisibility(View.VISIBLE);
                    TestTask task = new TestTask();
                    task.execute();
                }
            });

        } else if (getIntent() != null && getIntent().hasExtra(ALWAYS)){
            // forced
            setTitle(R.string.info);
            mTopic.setVisibility(View.GONE);
            mTable.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.INVISIBLE);
            mStateRoot.setImageResource(pref.contains(P_KEY_ROOT) ? pref.getBoolean(P_KEY_ROOT, false) ?
                    R.drawable.ic_ok : R.drawable.ic_not_ok : R.drawable.ic_pending);
            mStateCalendar.setImageResource(pref.contains(P_KEY_CALENDAR) ? pref.getBoolean(P_KEY_CALENDAR, false) ?
                    R.drawable.ic_ok : R.drawable.ic_not_ok : R.drawable.ic_pending);
            mStateMail.setImageResource(pref.contains(P_KEY_MAIL) ? pref.getBoolean(P_KEY_MAIL, false) ?
                    R.drawable.ic_ok : R.drawable.ic_not_ok : R.drawable.ic_pending);
            mStateTask.setImageResource(pref.contains(P_KEY_TASK) ? pref.getBoolean(P_KEY_TASK, false) ?
                    R.drawable.ic_ok : R.drawable.ic_not_ok : R.drawable.ic_pending);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);

        } else {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class TestTask extends AsyncTask<Void, Integer, Void> {

        boolean root = false;
        boolean mail = false;
        boolean calendar = false;
        boolean task = false;

        //TestTask(){ super(InfoActivity.this, getString(R.string.processing)); }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNextButton.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
            mStateRoot.setImageResource(R.drawable.ic_pending);
            mStateMail.setImageResource(R.drawable.ic_pending);
            mStateCalendar.setImageResource(R.drawable.ic_pending);
            mStateTask.setImageResource(R.drawable.ic_pending);
        }

        @Override
        protected Void doInBackground(Void... params) {
            root = Su.execute();
            publishProgress(0);
            if (root) {
                mail = Su.execute("if [ -r \"" + Su.MAIL_DB_PATH + "\" ]; then exit 0; else exit 1; fi");
                publishProgress(1);
                calendar = Su.execute("if [ -r \"" + Su.CALENDAR_DB_PATH + "\" ]; then exit 0; else exit 1; fi");
                publishProgress(2);
                task = Su.execute("if [ -r \"" + Su.TASK_DB_PATH + "\" ]; then exit 0; else exit 1; fi");
                publishProgress(3);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]){
                case 0:
                    mStateRoot.setImageResource(root ? R.drawable.ic_ok : R.drawable.ic_not_ok);
                    break;
                case 1:
                    mStateMail.setImageResource(mail ? R.drawable.ic_ok : R.drawable.ic_not_ok);
                    break;
                case 2:
                    mStateCalendar.setImageResource(calendar ? R.drawable.ic_ok : R.drawable.ic_not_ok);
                    break;
                case 3:
                    mStateTask.setImageResource(task ? R.drawable.ic_ok : R.drawable.ic_not_ok);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mNextButton.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
            if (root && mail) {
                getSharedPreferences("preferences", MODE_PRIVATE).edit()
                        .putBoolean(P_KEY_FIRST, false)
                        .putBoolean(P_KEY_ROOT, root)
                        .putBoolean(P_KEY_MAIL, mail)
                        .putBoolean(P_KEY_CALENDAR, calendar)
                        .putBoolean(P_KEY_TASK, task)
                        .apply();
                mNextButton.setText(R.string._continue);
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }
                });
            } else
            if (!root) {
                SimpleDialogFragment dialog = SimpleDialogFragment.newInstance(
                        getString(R.string.root_required),
                        getString(R.string.root_required_msg),
                        getString(R.string.ok),
                        null
                );
                dialog.show(getFragmentManager(), null);
            } else {
                SimpleDialogFragment dialog2 = SimpleDialogFragment.newInstance(
                        getString(R.string.read_db_failed),
                        getString(R.string.htc_mail_required_msg),
                        getString(R.string.ok),
                        null
                );
                dialog2.show(getFragmentManager(), null);
            }

        }
    }


}
