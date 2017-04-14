package de.exestrial.colorizer.mailcalendarcolor;

import android.app.Application;
import android.content.Intent;

import java.io.File;
import java.io.PrintStream;

/**
 * Created by Philipp on 20.04.2016.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                try {
                    File file = new File(getCacheDir(), CrashActivity.LOG_FILE);
                    PrintStream ps = new PrintStream(file);
                    e.printStackTrace(ps);
                    ps.close();

                    Intent intent = new Intent("de.exestrial.colorizer.mailcalendarcolor.CrashActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(CrashActivity.EXCEPTION, e);
                    startActivity(intent);

                } catch (Exception error){
                    error.printStackTrace();
                }
                System.exit(1); // kill off the crashed app
            }
        });
    }

}
