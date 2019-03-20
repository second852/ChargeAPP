package com.chargeapp.whc.chargeapp.Job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.Control.Common;

public class DownloadNewDataJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("DownloadNewDataJob","start");
        Common.setChargeDB(this);
        new GetSQLDate(DownloadNewDataJob.this).execute("download");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
