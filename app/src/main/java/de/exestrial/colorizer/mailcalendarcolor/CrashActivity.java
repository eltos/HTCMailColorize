package de.exestrial.colorizer.mailcalendarcolor;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CrashActivity extends AppCompatActivity {

    public static final String REPORT_EMAIL = "exestrial+colorizer@gmail.com";
    public static final String REPORT_SUBJECT = "Mail Colorizer Crash Report";

    public static final String EXCEPTION = "exception";
    public static final String LOG_FILE = "crash.log";
    Button mRestartButton;
    Button mReportButton;
    TextView mStackTraceView;

    String mErrorInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.crash_report);

        mRestartButton = (Button) findViewById(R.id.restart);
        mReportButton = (Button) findViewById(R.id.report);
        mStackTraceView = (TextView) findViewById(R.id.stack_trace);

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        getBaseContext().getPackageManager().getLaunchIntentForPackage(
                                getBaseContext().getPackageName())
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + REPORT_EMAIL + "?"
                        + "subject=" + REPORT_SUBJECT + "&"
                        + "body=" + getString(R.string.crash_report_email_draft) + "\n\n\n" + mErrorInfo.replace("&", "%26")));
                startActivity(Intent.createChooser(intent, getString(R.string.send_report)));
            }
        });

        try {
            File file = new File(getCacheDir(), CrashActivity.LOG_FILE);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            mErrorInfo =
                    "Android API Level: " + Build.VERSION.SDK_INT + "\n" +
                    "Version: " + System.getProperty("os.version") + " (" + android.os.Build.VERSION.INCREMENTAL + ")\n" +
                    "Modell: " + Build.MODEL + " (" + Build.MANUFACTURER + ")\n" +
                    "App-Version: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")\n\n" +
                    "StackTrace: \n" + sb.toString();

            mStackTraceView.setText(mErrorInfo);
            mStackTraceView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
