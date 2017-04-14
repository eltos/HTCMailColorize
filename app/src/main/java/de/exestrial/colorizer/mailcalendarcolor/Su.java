package de.exestrial.colorizer.mailcalendarcolor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Philipp on 20.04.2016.
 */
public class Su {

    static final String MAIL_DB_PATH = "/data/data/com.htc.android.mail/databases/mail.db";
    static final String CALENDAR_DB_PATH = "/data/data/com.android.providers.calendar/databases/calendar.db";
    static final String TASK_DB_PATH = "/data/data/com.htc.task/databases/MyDB.db";

    static boolean execute(String... commands){
        Process p;
        try {
            // Preform su to get root privledges
            p = Runtime.getRuntime().exec("su"); // -c

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String cmd : commands){
                os.writeBytes(cmd+"\n");
            }
            os.writeBytes("exit\n");
            os.flush();

            try {

                p.waitFor();

                if (p.exitValue() == 0) {
                    // success
                    return true;

                } else {
                    // fail
//                    String line;
//                    String error = "";
//                    BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                    while ((line = stdErr.readLine ()) != null) {
//                        error += line;
//                    }
//                    Log.e("su Error", error);
//
//                    String output = "";
//                    BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
//                    while ((line = stdOut.readLine ()) != null) {
//                        output += line;
//                    }
//                    Log.e("su Output", output);

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                // fail
            }
        } catch (IOException e) {
            e.printStackTrace();
            // fail
        }
        return false;
    }
}
