package com.chargeapp.whc.chargeapp.Control;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;

public class DowloadNewDataJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("DowloadNewDataJob","start");
        Common.setChargeDB(this);
        new GetSQLDate(DowloadNewDataJob.this).execute("download");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
