package de.exestrial.colorizer.mailcalendarcolor;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import de.exestrial.colorizer.mailcalendarcolor.dialogs.PickColorDialog;
import de.exestrial.colorizer.mailcalendarcolor.dialogs.SimpleDialogFragment;
import de.exestrial.colorizer.mailcalendarcolor.dialogs.SimpleIndeterminateDialogAsyncTask;

public class MainActivity extends AppCompatActivity implements SimpleDialogFragment.OnDialogFragmentResultListener {

    private static final String COLOR_PICKER = "color_picker";
    private static final String ACCOUNT_INDEX = "account_index";
    private static final String ACCOUNT_LIST = "account_list";
    ListView mList;
    MyAdapter mListAdapter;
    Button mRetryButton;

    ArrayList<Account> mAccountList = new ArrayList<>(0);

    String tempMailFile;
    String tempCalFile;
    String tempTaskFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mList = (ListView) findViewById(R.id.listView);
        mRetryButton = (Button) findViewById(R.id.retry);

        mListAdapter = new MyAdapter();
        mList.setAdapter(mListAdapter);
        mList.setEmptyView(mRetryButton);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putInt(ACCOUNT_INDEX, position);
                PickColorDialog dialog = PickColorDialog.newInstance(
                        getBaseContext(), bundle, mAccountList.get(position).color);
                dialog.show(getFragmentManager(), COLOR_PICKER);


            }
        });
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryAccountsTask task = new QueryAccountsTask();
                task.execute();
            }
        });



        tempMailFile = new File(getCacheDir(), "mail.db").getAbsolutePath();
        tempCalFile = new File(getCacheDir(), "calendar.db").getAbsolutePath();
        tempTaskFile = new File(getCacheDir(), "MyDB.db").getAbsolutePath();


        if (savedInstanceState != null){
            mAccountList = savedInstanceState.getParcelableArrayList(ACCOUNT_LIST);
            mListAdapter.notifyDataSetChanged();
        } else {
            QueryAccountsTask task = new QueryAccountsTask();
            task.execute();
        }
    }


    @Override
    public void onDialogFragmentResult(String dialogTag, int which, Bundle extras) {
        if (COLOR_PICKER.equals(dialogTag) && which == BUTTON_POSITIVE){
            int index = extras.getInt(ACCOUNT_INDEX);
            int color = extras.getInt(PickColorDialog.RGB, -1);
            if (index >= 0 && index < mAccountList.size() && color >= 0){
                Account a = mAccountList.get(index);
                UpdateAccountsTask task = new UpdateAccountsTask(a, Account.Color.byRGB(color));
                task.execute();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ACCOUNT_LIST, mAccountList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reload){
            QueryAccountsTask task = new QueryAccountsTask();
            task.execute();
            return true;
        }
        if (item.getItemId() == R.id.info){
            startActivity(new Intent(getBaseContext(), InfoActivity.class).putExtra(InfoActivity.ALWAYS,1));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    boolean copyDatabases(){

        return Su.execute(
                "if [ -r \"" + Su.MAIL_DB_PATH + "\" ]; then " +
                        "cat " + Su.MAIL_DB_PATH + " > " + tempMailFile + "; chmod 777 " + tempMailFile + "; fi",
                "if [ -r \"" + Su.CALENDAR_DB_PATH + "\" ]; then " +
                        "cat " + Su.CALENDAR_DB_PATH + " > " + tempCalFile + "; chmod 777 " + tempCalFile + "; fi",
                "if [ -r \"" + Su.TASK_DB_PATH + "\" ]; then " +
                        "cat " + Su.TASK_DB_PATH + " > " + tempTaskFile + "; chmod 777 " + tempTaskFile + "; fi"
        );
    }
    boolean copyBackDatabases(){
        return Su.execute(
                "if [ -r \"" + Su.MAIL_DB_PATH + "\" ]; then " +
                        "cat " + tempMailFile + " > " + Su.MAIL_DB_PATH + "; fi",
                "if [ -r \"" + Su.CALENDAR_DB_PATH + "\" ]; then " +
                        "cat " + tempCalFile + " > " + Su.CALENDAR_DB_PATH + "; fi",
                "if [ -r \"" + Su.TASK_DB_PATH + "\" ]; then " +
                        "cat " + tempTaskFile + " > " + Su.TASK_DB_PATH + "; fi"
        );
    }


    class UpdateAccountsTask extends SimpleIndeterminateDialogAsyncTask<Void, Void, Integer>{

        static final int SUCCESS = 1;
        static final int SUDO_FAIL = 2;
        static final int DB_FAIL = 3;
        static final int CALENDER_OR_TASK_DB_FAIL = 4;

        Account account;
        Account.Color color;

        UpdateAccountsTask(Account a, Account.Color c){
            super(MainActivity.this, getString(R.string.processing));
            account = a;
            color = c;
        }


        @Override
        protected Integer doInBackground(Void... accounts) {

            if (copyDatabases()){
                try {

                    boolean fail = false;

                    SQLiteDatabase db = SQLiteDatabase.openDatabase(tempMailFile, null, 0);
                    ContentValues values = new ContentValues();
                    values.put("_colorIdx", color.mail_value);
                    db.update("accounts", values, "_id = ?", new String[]{account.mailDbId + ""});
                    db.close();

                    try {
                        SQLiteDatabase db2 = SQLiteDatabase.openDatabase(tempCalFile, null, 0);
                        ContentValues values2 = new ContentValues();
                        values2.put("calendar_color", color.calendar_value);
                        db2.update("Calendars", values2, "ownerAccount = ?", new String[]{account.mail + ""});
                        db2.close();
                    } catch (SQLiteException e){
                        // failed to open database
                        e.printStackTrace();
                        fail = true;
                    }
                    try {
                        SQLiteDatabase db3 = SQLiteDatabase.openDatabase(tempTaskFile, null, 0);
                        ContentValues values3 = new ContentValues();
                        values3.put("AccountColor", color.calendar_value);
                        db3.update("Account", values3, "accountName = ?", new String[]{account.mail + ""});
                        db3.close();
                    } catch (SQLiteException e){
                        // failed to open database
                        e.printStackTrace();
                        fail = true;
                    }

                    if (copyBackDatabases()){
                        return fail ? CALENDER_OR_TASK_DB_FAIL : SUCCESS;
                    } else {
                        return SUDO_FAIL;
                    }

                } catch (SQLiteException e){
                    // failed to open database
                    e.printStackTrace();
                    return DB_FAIL;
                }

            } else {
                return SUDO_FAIL;
            }


        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result){
                case SUCCESS:
                    account.color = color;
                    mListAdapter.notifyDataSetChanged();
                    break;

                case DB_FAIL:
                    SimpleDialogFragment dialog2 = SimpleDialogFragment.newInstance(
                            getString(R.string.write_db_failed),
                            getString(R.string.htc_mail_required_msg),
                            getString(R.string.ok),
                            null
                    );
                    dialog2.show(getFragmentManager(), null);
                    break;

                case CALENDER_OR_TASK_DB_FAIL:
                    SimpleDialogFragment dialog3 = SimpleDialogFragment.newInstance(
                            getString(R.string.write_db_failed),
                            getString(R.string.calendar_or_task_db_fail),
                            getString(R.string.ok),
                            null
                    );
                    dialog3.show(getFragmentManager(), null);
                    break;

                case SUDO_FAIL:
                    SimpleDialogFragment dialog = SimpleDialogFragment.newInstance(
                            getString(R.string.root_required),
                            getString(R.string.root_required_msg),
                            getString(R.string.ok),
                            null
                    );
                    dialog.show(getFragmentManager(), null);
                    break;
            }
        }

    }


    class QueryAccountsTask extends SimpleIndeterminateDialogAsyncTask<Void, Void, Integer>{

        static final int SUCCESS = 1;
        static final int SUDO_FAIL = 2;
        static final int DB_FAIL = 3;

        QueryAccountsTask(){
            super(MainActivity.this, getString(R.string.processing));
        }

        ArrayList<Account> accounts = new ArrayList<>();

        @Override
        protected Integer doInBackground(Void... params) {

            if (copyDatabases()){

                try {

                    SQLiteDatabase db = SQLiteDatabase.openDatabase(tempMailFile, null, 0);
                    Cursor cursor = db.rawQuery("SELECT _id, _emailaddress, _desc, _colorIdx FROM accounts", null);

                    if (cursor.moveToFirst()) {
                        do {
                            Account account = new Account();
                            account.mailDbId = cursor.getInt(0);
                            account.mail = cursor.getString(1);
                            account.title = cursor.getString(2);
                            account.color = Account.Color.byMailValue(cursor.getInt(3));
                            accounts.add(account);
                        } while (cursor.moveToNext());
                    }

                    cursor.close();
                    db.close();


                    return SUCCESS;

                } catch (SQLiteException e){
                    // failed to open database
                    e.printStackTrace();
                    return DB_FAIL;
                }

            } else {
                return SUDO_FAIL;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mAccountList.clear();
            switch (result){
                case SUCCESS:

                    mAccountList.addAll(accounts);
                    break;

                case DB_FAIL:
                    SimpleDialogFragment dialog2 = SimpleDialogFragment.newInstance(
                            getString(R.string.read_db_failed),
                            getString(R.string.htc_mail_required_msg),
                            getString(R.string.ok),
                            null
                    );
                    dialog2.show(getFragmentManager(), null);
                    break;
                case SUDO_FAIL:
                    SimpleDialogFragment dialog = SimpleDialogFragment.newInstance(
                            getString(R.string.root_required),
                            getString(R.string.root_required_msg),
                            getString(R.string.ok),
                            null
                    );
                    dialog.show(getFragmentManager(), null);
                    break;
            }
            mListAdapter.notifyDataSetChanged();

        }
    }


    class MyAdapter extends BaseAdapter {

        class ViewHolder{
            TextView title;
            TextView subtitle;
        }

        @Override
        public int getCount() {
            return mAccountList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAccountList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.main_list_item, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Account account = (Account) getItem(position);

            holder.title.setText(account.title);
            holder.title.setTextColor(0xFF000000 + (account.color != null ? account.color.rgb : 0));
            holder.subtitle.setText(account.mail);

            return convertView;
        }
    }

}
