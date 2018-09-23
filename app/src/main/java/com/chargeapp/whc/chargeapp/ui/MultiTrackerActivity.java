/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chargeapp.whc.chargeapp.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.InsertSpend;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.UpdateSpend;
import com.chargeapp.whc.chargeapp.Model.ConsumeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Activity for the multi-tracker app.  This app detects faces and barcodes with the rear facing
 * camera, and draws overlay graphics to indicate the position, size, and ID of each face and
 * barcode.
 */
public final class MultiTrackerActivity extends AppCompatActivity {

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public static boolean refresh;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    public static TextView answer;
    public static String result;
    //避免重複對獎
    public static String oldElu,p,oldPeriod;
    public static boolean isold;
    public static int colorChange;
    public static String action;
    public RelativeLayout buttonR;
    public BootstrapButton search,back,backP;
    public static AwesomeTextView awardTitle;
    /**
     * Initializes the UI and creates the detector pipeline.
     */



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.main);
        answer=findViewById(R.id.answer);
        back=findViewById(R.id.back);
        awardTitle=findViewById(R.id.awardTitle);
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay =findViewById(R.id.faceOverlay);
        search=findViewById(R.id.search);
        buttonR=findViewById(R.id.buttonR);
        backP=findViewById(R.id.backP);
        oldPeriod=null;
        oldElu=null;
        if(refresh)
        {
            buttonR.setVisibility(View.VISIBLE);
            answer.setVisibility(View.GONE);
            backP.setVisibility(View.GONE);
            action=getIntent().getStringExtra("action");
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(action.equals("setConsume"))
                    {
                        MainActivity.oldFramgent.add("InsertSpend");
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("action", "InsertSpend");
                        bundle.putSerializable("needSet", true);
                        MainActivity.bundles.add(bundle);
                        Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                        intent.putExtra("action",MultiTrackerActivity.action);
                        MultiTrackerActivity.this.setResult(9,intent);
                        MultiTrackerActivity.this.finish();
                    }else{
                        Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                        intent.putExtra("action",MultiTrackerActivity.action);
                        intent.putExtra("bundle",getIntent().getBundleExtra("bundle"));
                        MainActivity.bundles.add(getIntent().getBundleExtra("bundle"));
                        MainActivity.oldFramgent.add("UpdateSpend");
                        MultiTrackerActivity.this.setResult(9,intent);
                        MultiTrackerActivity.this.finish();
                    }
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(action.equals("setConsume"))
                    {
//                        if(BarcodeGraphic.hashMap!=null&&BarcodeGraphic.hashMap.size()==1)
//                        {
//                            InsertSpend.returnCM=true;
//                        }
                        Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                        intent.putExtra("action",MultiTrackerActivity.action);
                        MultiTrackerActivity.this.setResult(0,intent);
                        MultiTrackerActivity.this.finish();
                    }else{
                        Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                        intent.putExtra("action",MultiTrackerActivity.action);
                        Bundle bundle=getIntent().getBundleExtra("bundle");
//                        if(BarcodeGraphic.hashMap!=null&&BarcodeGraphic.hashMap.size()==1)
//                        {
//                            bundle.putSerializable("returnCM",true);
//                        }
                        intent.putExtra("bundle",bundle);
                        MultiTrackerActivity.this.setResult(0,intent);
                        MultiTrackerActivity.this.finish();
                    }
                }
            });
        }else {

            try {
                action=getIntent().getStringExtra("action");
            }catch (Exception e)
            {
                action=" ";
            }
            backP.setVisibility(View.VISIBLE);
            backP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                    intent.putExtra("action",MultiTrackerActivity.action);
                    MultiTrackerActivity.this.setResult(0,intent);
                    MultiTrackerActivity.this.finish();
                }
            });
            buttonR.setVisibility(View.GONE);
            answer.setVisibility(View.VISIBLE);
        }

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        ActivityCompat.requestPermissions(this, permissions,
                RC_HANDLE_CAMERA_PERM);
    }





    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        BarcodeGraphic.hashMap=new HashMap<>();
        // A face detector is created to track faces.  An associated multi-processor instance
        // is set to receive the face detection results, track the faces, and maintain graphics for
        // each face on screen.  The factory is used by the multi-processor to create a separate
        // tracker instance for each face.
        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE).
                build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay,this);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());
        // A multi-detector groups the two detectors together as one detector.  All images received
        // by this detector from the camera will be sent to each of the underlying detectors, which
        // will each do face and barcode detection, respectively.  The detection results from each
        // are then sent to associated tracker instances which maintain per-item graphics on the
        // screen.
        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(barcodeDetector)
                .build();

        if (!multiDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this,"手機儲存空間不夠!", Toast.LENGTH_LONG).show();
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        mCameraSource = new CameraSource.Builder(getApplicationContext(), multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(action.equals("setConsume"))
            {
                Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                intent.putExtra("action",MultiTrackerActivity.action);
                MultiTrackerActivity.this.setResult(0,intent);
                MultiTrackerActivity.this.finish();
            }else if(action.equals("UpdateSpend")){
                Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                intent.putExtra("action",MultiTrackerActivity.action);
                intent.putExtra("bundle",getIntent().getBundleExtra("bundle"));
                MultiTrackerActivity.this.setResult(0,intent);
                MultiTrackerActivity.this.finish();
            }else if(action.equals("PriceHand"))
            {
                Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                intent.putExtra("action",MultiTrackerActivity.action);
                MultiTrackerActivity.this.setResult(0,intent);
                MultiTrackerActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }


    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission, so create the camerasource
            createCameraSource();
            Common.showToast(this,"開始QRCode掃描!");
            return;
        }

        String remain;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)) {
            remain="沒有相機權限，無法使用。\n要使用此功能請按\"YES\"，並允許相機權限!\n不使用請按\"NO\"!";
        } else {
            remain="沒有相機權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開相機權限!\n不使用此功能請按\"NO\"。";
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(ActivityCompat.shouldShowRequestPermissionRationale(MultiTrackerActivity.this,Manifest.permission.CAMERA))
                {
                    Common.askPermissions(Manifest.permission.CAMERA, MultiTrackerActivity.this,0);
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", MultiTrackerActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent,6);
                }
            }
        };

        DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(action.equals("setConsume"))
                {
                    Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                    intent.putExtra("action",MultiTrackerActivity.action);
                    MultiTrackerActivity.this.setResult(0,intent);
                    MultiTrackerActivity.this.finish();
                }else if(action.equals("UpdateSpend")){
                    Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                    intent.putExtra("action",MultiTrackerActivity.action);
                    intent.putExtra("bundle",getIntent().getBundleExtra("bundle"));
                    MultiTrackerActivity.this.setResult(0,intent);
                    MultiTrackerActivity.this.finish();
                }else if(action.equals("PriceHand"))
                {
                    Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                    intent.putExtra("action",MultiTrackerActivity.action);
                    MultiTrackerActivity.this.setResult(0,intent);
                    MultiTrackerActivity.this.finish();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("無法使用相機!")
                .setMessage(remain)
                .setPositiveButton("YES", listener)
                .setNegativeButton("NO",nolistener)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result != PackageManager.PERMISSION_GRANTED) {

            String remain;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)) {
                remain="沒有相機權限，無法使用。\n要使用此功能請按\"YES\"，並允許相機權限!\n不使用請按\"NO\"!";
            } else {
                remain="沒有相機權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開相機權限!\n不使用此功能請按\"NO\"。";
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if(ActivityCompat.shouldShowRequestPermissionRationale(MultiTrackerActivity.this,Manifest.permission.CAMERA))
                    {
                        Common.askPermissions(Manifest.permission.CAMERA, MultiTrackerActivity.this,0);
                    }else {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", MultiTrackerActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent,6);
                    }
                }
            };

            DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(action.equals("setConsume"))
                    {
                        Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                        intent.putExtra("action",MultiTrackerActivity.action);
                        MultiTrackerActivity.this.setResult(0,intent);
                        MultiTrackerActivity.this.finish();
                    }else if(action.equals("UpdateSpend")){
                        Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                        intent.putExtra("action",MultiTrackerActivity.action);
                        intent.putExtra("bundle",getIntent().getBundleExtra("bundle"));
                        MultiTrackerActivity.this.setResult(0,intent);
                        MultiTrackerActivity.this.finish();
                    }else if(action.equals("PriceHand"))
                    {
                        Intent intent = new Intent(MultiTrackerActivity.this,MainActivity.class);
                        intent.putExtra("action",MultiTrackerActivity.action);
                        MultiTrackerActivity.this.setResult(0,intent);
                        MultiTrackerActivity.this.finish();
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("無法使用相機!")
                    .setMessage(remain)
                    .setPositiveButton("YES", listener)
                    .setNegativeButton("NO",nolistener)
                    .show();


        }else {
            createCameraSource();
            Common.showToast(this,"開始QRCode掃描!");
        }

    }
    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


}
