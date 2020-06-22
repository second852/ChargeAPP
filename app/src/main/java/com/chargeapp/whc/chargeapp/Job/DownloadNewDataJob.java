package com.chargeapp.whc.chargeapp.Job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.chargeapp.whc.chargeapp.ChargeDB.GetSQLDate;
import com.chargeapp.whc.chargeapp.ChargeDB.SetupDateBase64;
import com.chargeapp.whc.chargeapp.Control.Common;

public class DownloadNewDataJob extends JobService {



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("DownloadNewDataJob","start");
        Common.setChargeDB(this);

        new GetSQLDate(DownloadNewDataJob.this,this).execute("download");
        new GetSQLDate(DownloadNewDataJob.this,this).execute("getWinInvoice");
        new SetupDateBase64(DownloadNewDataJob.this,DownloadNewDataJob.this.getApplicationContext()).execute("consumeVO");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
