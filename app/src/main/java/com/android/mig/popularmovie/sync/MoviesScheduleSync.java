package com.android.mig.popularmovie.sync;

import android.content.Context;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * This class schedules sync Content Provider with up to date data from the cloud
 */
public class MoviesScheduleSync {

    private static int SYNC_INTERVAL_HOURS = 11;
    private static int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static int SYNC_FLEXIBLE_SECONDS = SYNC_INTERVAL_SECONDS / 11;  // 1 hr in seconds
    private static String SYNC_TAG = "sync_tag";

    synchronized public static void scheduleSync(Context context){

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        Job mJob = firebaseJobDispatcher.newJobBuilder()
                .setService(MoviesFirebaseJobService.class)
                .setTag(SYNC_TAG)
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXIBLE_SECONDS))     // every 11-12 hours
                .build();
        firebaseJobDispatcher.mustSchedule(mJob);
    }
}
