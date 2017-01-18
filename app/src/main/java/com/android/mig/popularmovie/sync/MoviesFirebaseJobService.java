package com.android.mig.popularmovie.sync;

import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MoviesFirebaseJobService extends JobService{

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                MoviesSyncTask.syncMovies(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };
        mBackgroundTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null){
            mBackgroundTask.cancel(true);
        }
        return true;
    }
}
