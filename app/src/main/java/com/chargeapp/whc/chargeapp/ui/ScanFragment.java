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
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.ArraySet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDB;
import com.chargeapp.whc.chargeapp.ChargeDB.TypeDetailDB;
import com.chargeapp.whc.chargeapp.Control.Common;
import com.chargeapp.whc.chargeapp.Control.Download;
import com.chargeapp.whc.chargeapp.Control.Insert.InsertConsumeType;
import com.chargeapp.whc.chargeapp.Control.MainActivity;
import com.chargeapp.whc.chargeapp.Control.MyContextWrapper;
import com.chargeapp.whc.chargeapp.Model.TypeDetailVO;
import com.chargeapp.whc.chargeapp.Model.TypeVO;
import com.chargeapp.whc.chargeapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.jsoup.internal.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Activity for the multi-tracker app.  This app detects faces and barcodes with the rear facing
 * camera, and draws overlay graphics to indicate the position, size, and ID of each face and
 * barcode.
 */
public final class ScanFragment extends Fragment {

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    public static AwesomeTextView answer;
    public static String result;
    //避免重複對獎

    public static String action;
    public RelativeLayout buttonR,scanR;
    public BootstrapButton search,back,backP,typeSetting,recordTwo,searchTwo;
    public PopupMenu popupMenu;
    private LinearLayout firstL, secondL;
    private GridView firstG, secondG;

    private TypeVO typeVO;
    private List<TypeVO> typeVOS;
    private Activity activity;
    public static boolean isAutoSetType=true;
    public static String mainType,secondType;
    public static Set<String> nulName;
    public static Set<String> qrCode;


    /**
     * Initializes the UI and creates the detector pipeline.
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        } else {
            this.activity = getActivity();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
        action=getArguments().getString("action");

        back=view.findViewById(R.id.back);
        mPreview =view.findViewById(R.id.preview);
        mGraphicOverlay =view.findViewById(R.id.faceOverlay);
        search=view.findViewById(R.id.search);
        buttonR=view.findViewById(R.id.buttonR);
        backP=view.findViewById(R.id.backP);
        if(ScanFragment.nulName==null)
        {
            ScanFragment.nulName=new HashSet<>();
        }

        switch (action)
        {
            case "setConsume":
            case "UpdateSpend":
                activity.setTitle("QR Code掃描");
                answer=view.findViewById(R.id.answer2);
                buttonR.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
                answer.setVisibility(View.GONE);
                backP.setVisibility(View.GONE);
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
                            Intent intent = new Intent(activity,MainActivity.class);
                            intent.putExtra("action", ScanFragment.action);
                            activity.setResult(9,intent);
                            activity.finish();
                        }else{
                            Intent intent = new Intent(activity,MainActivity.class);
                            intent.putExtra("action", ScanFragment.action);
                            intent.putExtra("bundle",getArguments());
                            MainActivity.bundles.add(getArguments());
                            MainActivity.oldFramgent.add("UpdateSpend");
                            activity.setResult(9,intent);
                            activity.finish();
                        }
                    }
                });
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(action.equals("setConsume"))
                        {
                            Intent intent = new Intent(activity,MainActivity.class);
                            intent.putExtra("action", ScanFragment.action);
                            activity.setResult(0,intent);
                            activity.finish();
                        }else{
                            Intent intent = new Intent( activity,MainActivity.class);
                            intent.putExtra("action", ScanFragment.action);
                            intent.putExtra("bundle",getArguments());
                            activity.setResult(0,intent);
                            activity.finish();
                        }
                    }
                });
                break;
            case "PriceHand":


                activity.setTitle("發票兌獎");
                answer=view.findViewById(R.id.answer1);
                backP.setVisibility(View.VISIBLE);
                backP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent( activity,MainActivity.class);
                        intent.putExtra("action", ScanFragment.action);
                        activity.setResult(0,intent);
                        activity.finish();
                    }
                });
                buttonR.setVisibility(View.GONE);
                answer.setVisibility(View.GONE);
                break;
            case Common.scanFragment:
            case Common.scanByOnline:
            case Common.scanUpdateSpend:
                activity.setTitle("多筆QR Code掃描");
                buttonR.setVisibility(View.GONE);
                answer=view.findViewById(R.id.answer2);
                scanR=view.findViewById(R.id.scanR);
                scanR.setVisibility(View.VISIBLE);
                typeSetting=view.findViewById(R.id.typeSetting);
                popupMenu = new PopupMenu(activity, typeSetting);
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, Menu.FIRST, 0, "自動");
                menu.add(Menu.NONE, Menu.FIRST+1, 1, "設定");
                typeSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupMenu.show();
                    }
                });
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId())
                        {
                            case 2:
                                isAutoSetType=false;
                                firstL.setVisibility(View.VISIBLE);
                                secondL.setVisibility(View.GONE);
                                break;
                            default:
                                isAutoSetType=true;
                                firstL.setVisibility(View.GONE);
                                typeSetting.setText(R.string.text_autoSetting);
                                break;
                        }

                        popupMenu.dismiss();
                        return false;
                    }
                });

                firstL=view.findViewById(R.id.firstL);
                firstG=view.findViewById(R.id.firstG);
                secondG=view.findViewById(R.id.secondG);
                secondL=view.findViewById(R.id.secondL);
                setFirstGrid();

                if(Common.showfirstgrid)
                {
                    firstL.setVisibility(View.VISIBLE);
                    firstG.setVisibility(View.VISIBLE);
                    Common.showfirstgrid=false;
                }

                if(Common.showsecondgrid)
                {
                    typeVO= (TypeVO) getArguments().getSerializable("typeVO");
                    mainType=typeVO.getName();
                    secondG.setVisibility(View.VISIBLE);
                    secondL.setVisibility(View.VISIBLE);
                    setSecondGrid();
                    Common.showsecondgrid=false;
                }


                if(!StringUtil.isBlank(secondType))
                {
                    typeSetting.setText("主項目 : "+mainType+"\n次項目 :"+secondType);
                }

                backP=view.findViewById(R.id.backP);
                backP.setVisibility(View.VISIBLE);
                backP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent( activity,MainActivity.class);
                        activity.setResult(10,intent);
                        activity.finish();
                    }
                });

                recordTwo=view.findViewById(R.id.recordTwo);
                recordTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.bundles.add(getArguments());
                        MainActivity.oldFramgent.add(Common.scanFragment);
                        Fragment fragment=new ScanListFragment();
                        fragment.setArguments(getArguments());
                        Common.switchFragment(fragment,Common.scanFragment,getFragmentManager());
                    }
                });

                searchTwo=view.findViewById(R.id.searchTwo);
                searchTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.bundles.add(getArguments());
                        MainActivity.oldFramgent.add(Common.scanFragment);
                        Fragment fragment=new ScanByOnline();
                        fragment.setArguments(getArguments());
                        Common.switchFragment(fragment,Common.scanFragment,getFragmentManager());
                    }
                });

                break;
        }



        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }





        return view;
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        ActivityCompat.requestPermissions(activity, permissions,
                RC_HANDLE_CAMERA_PERM);
    }





    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = activity.getApplicationContext();
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
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay,activity,action);
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
            boolean hasLowStorage = activity.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(activity,"手機儲存空間不夠!", Toast.LENGTH_LONG).show();
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        mCameraSource = new CameraSource.Builder(activity.getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        startCameraSource();
    }




    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mCameraSource != null) {
//            mCameraSource.release();
//        }
//    }


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
            Common.showToast(activity,"開始QRCode掃描!");
            return;
        }

        String remain;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.RECORD_AUDIO)) {
            remain="沒有相機權限，無法使用。\n要使用此功能請按\"YES\"，並允許相機權限!\n不使用請按\"NO\"!";
        } else {
            remain="沒有相機權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開相機權限!\n不使用此功能請按\"NO\"。";
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.CAMERA))
                {
                    Common.askPermissions(Manifest.permission.CAMERA, activity,0);
                }else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent,6);
                }
            }
        };

        DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(action.equals("setConsume"))
                {
                    Intent intent = new Intent(activity,MainActivity.class);
                    intent.putExtra("action", ScanFragment.action);
                    activity.setResult(0,intent);
                    activity.finish();
                }else if(action.equals("UpdateSpend")){
                    Intent intent = new Intent(activity,MainActivity.class);
                    intent.putExtra("action", ScanFragment.action);
                    intent.putExtra("bundle",getArguments());
                    activity.setResult(0,intent);
                    activity.finish();
                }else if(action.equals("PriceHand"))
                {
                    Intent intent = new Intent(activity,MainActivity.class);
                    intent.putExtra("action", ScanFragment.action);
                    activity.setResult(0,intent);
                    activity.finish();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("無法使用相機!")
                .setMessage(remain)
                .setPositiveButton("YES", listener)
                .setNegativeButton("NO",nolistener)
                .show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result != PackageManager.PERMISSION_GRANTED) {

            String remain;
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
                remain = "沒有相機權限，無法使用。\n要使用此功能請按\"YES\"，並允許相機權限!\n不使用請按\"NO\"!";
            } else {
                remain = "沒有相機權限!\n如果要使用此功能按\"YES\"。\n並到權限，打開相機權限!\n不使用此功能請按\"NO\"。";
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                        Common.askPermissions(Manifest.permission.CAMERA, activity, 0);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 6);
                    }
                }
            };

            DialogInterface.OnClickListener nolistener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (action.equals("setConsume")) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("action", ScanFragment.action);
                        activity.setResult(0, intent);
                        activity.finish();
                    } else if (action.equals("UpdateSpend")) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("action", ScanFragment.action);
                        intent.putExtra("bundle", getArguments());
                        activity.setResult(0, intent);
                        activity.finish();
                    } else if (action.equals("PriceHand")) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra("action", ScanFragment.action);
                        activity.setResult(0, intent);
                        activity.finish();
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("無法使用相機!")
                    .setMessage(remain)
                    .setPositiveButton("YES", listener)
                    .setNegativeButton("NO", nolistener)
                    .show();


        } else {
            createCameraSource();
            Common.showToast(activity, "開始QRCode掃描!");
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
                activity.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS);
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




    private void setSecondGrid() {
        HashMap item;
        ArrayList items = new ArrayList<Map<String, Object>>();
        TypeDetailDB typeDetailDB = new TypeDetailDB(MainActivity.chargeAPPDB);
        List<TypeDetailVO> typeDetailVOS = typeDetailDB.findByGroupname(mainType);
        for (TypeDetailVO t : typeDetailVOS) {
            item = new HashMap<String, Object>();
            item.put("image", Download.imageAll[t.getImage()]);
            item.put("text", t.getName());
            items.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.returnt);
        item.put("text", "返回");
        items.add(item);
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.add);
        item.put("text", "新增");
        items.add(item);
        item = new HashMap<String, Object>();
        item.put("image", R.drawable.cancel);
        item.put("text", "取消");
        items.add(item);
        SimpleAdapter adapter = new SimpleAdapter(activity,
                items, R.layout.main_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        secondG.setAdapter(adapter);
        secondG.setNumColumns(4);
        secondG.setOnItemClickListener(new secondGridOnClick());
    }




    private void setFirstGrid() {
        try {
            TypeDB typeDB=new TypeDB(MainActivity.chargeAPPDB);
            HashMap item;
            ArrayList items = new ArrayList<Map<String, Object>>();
            typeVOS = typeDB.getAll();
            for (TypeVO t : typeVOS) {
                item = new HashMap<String, Object>();
                item.put("image", Download.imageAll[t.getImage()]);
                item.put("text", t.getName());
                items.add(item);
            }
            item = new HashMap<String, Object>();
            item.put("image", R.drawable.add);
            item.put("text", "新增");
            items.add(item);
            item = new HashMap<String, Object>();
            item.put("image", R.drawable.cancel);
            item.put("text", "取消");
            items.add(item);
            SimpleAdapter adapter = new SimpleAdapter(activity, items, R.layout.main_item, new String[]{"image", "text"},
                    new int[]{R.id.image, R.id.text});
            firstG.setAdapter(adapter);
            firstG.setNumColumns(4);
            firstG.setOnItemClickListener(new firstGridOnClick());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class firstGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (i < typeVOS.size()) {
                typeVO = typeVOS.get(i);
            }
            if (type.equals("新增")) {
                Common.showfirstgrid = true;
                returnThisFragment(new InsertConsumeType());
                return;
            }
            if (type.equals("取消")) {
                firstL.setVisibility(View.GONE);
                Common.showfirstgrid = false;
                return;
            }

            mainType=type;
            setSecondGrid();
            firstL.setVisibility(View.GONE);
            secondL.setVisibility(View.VISIBLE);
            Common.showfirstgrid = false;
        }
    }



    private void returnThisFragment(Fragment fragment) {
        Bundle bundle = getArguments();
        if(bundle==null)
        {
            bundle=new Bundle();
        }
        bundle.putSerializable("typeVO", typeVO);
        bundle.putSerializable("action",Common.scanFragment);
        bundle.putSerializable("mainType",mainType);
        fragment.setArguments(bundle);
        Common.switchFragment(fragment,Common.scanFragment,getFragmentManager(),R.id.body);
    }


    private class secondGridOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView textView = view.findViewById(R.id.text);
            String type = textView.getText().toString().trim();
            if (type.equals("返回")) {
                firstL.setVisibility(View.VISIBLE);
                secondL.setVisibility(View.GONE);
                return;
            }
            if (type.equals("新增")) {
                Common.showsecondgrid = true;
                returnThisFragment(new InsertConsumeType());
                return;
            }
            if (type.equals("取消")) {
                Common.showsecondgrid = false;
                secondL.setVisibility(View.GONE);
                return;
            }

            secondType=type;
            secondL.setVisibility(View.GONE);
            Common.showsecondgrid = false;
            typeSetting.setText("主項目 : "+mainType+"\n次項目 :"+secondType);

        }
    }

}
