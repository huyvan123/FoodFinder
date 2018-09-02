package arcore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.hp.myapplication.R;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.permission.PermissionManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MyArActivity extends AppCompatActivity{
    private  final String foodDefinitionsPath = "POI/poi.json";
    private static final String FOOD_STORE_ROOT = "POI/";
    private String arExperience;
    private  List<FoodStoreCategory> categories;
    private  final PermissionManager permissionManager = ArchitectView.getPermissionManager();
    protected ArchitectView architectView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStoreData();
    }

    public void getStoreData(){
        final String json = FoodJsonParser.loadStringFromAssets(MyArActivity.this, foodDefinitionsPath);
        categories = FoodJsonParser.getCategoriesFromJsonString(json);
        final FoodStoreData foodStoreData = categories.get(0).getSamples().get(3);
        final String[] permissions = UtilsPermmission.getPermissionsForArFeatures(foodStoreData.getArFeatures());

        permissionManager.checkPermissions(MyArActivity.this, permissions, PermissionManager.WIKITUDE_PERMISSION_REQUEST, new PermissionManager.PermissionManagerCallback() {
            @Override
            public void permissionsGranted(int requestCode) {
                show(foodStoreData);
            }

            @Override
            public void permissionsDenied(@NonNull String[] deniedPermissions) {
                Toast.makeText(MyArActivity.this, getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void showPermissionRationale(final int requestCode, @NonNull String[] strings) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyArActivity.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(R.string.permission_rationale_title);
                alertBuilder.setMessage(getString(R.string.permission_rationale_text) + Arrays.toString(permissions));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        permissionManager.positiveRationaleResult(requestCode, permissions);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        });
    }

    private void show(FoodStoreData foodStoreData){
        WebView.setWebContentsDebuggingEnabled(true);
        arExperience = foodStoreData.getPath();
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(getString(R.string.wikitude_license_key));
        config.setCameraPosition(foodStoreData.getCameraPosition());
        config.setCameraResolution(foodStoreData.getCameraResolution());
        config.setCameraFocusMode(foodStoreData.getCameraFocusMode());
        config.setCamera2Enabled(foodStoreData.isCamera2Enabled());
        config.setFeatures(foodStoreData.getArFeatures());

        architectView = new ArchitectView(this);
        architectView.onCreate(config); // create ArchitectView with configuration
        setContentView(architectView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        Intent intent = getIntent();
//        Location location = (Location) intent.getSerializableExtra("location");
//        if(location == null){
//            System.out.println("DIVICE LOCATION IS NULLLLLLLLL");
//        }else {
//            System.out.println("DIVICE LOCATION: " + location.getLatitude() + " ," + location.getLongitude());
//        }
        architectView.setLocation(intent.getDoubleExtra("la",0.0),intent.getDoubleExtra("log",0.0)
                ,intent.getDoubleExtra("acc",0.0));
        try {
            /*
             * Loads the AR-Experience, it may be a relative path from assets,
             * an absolute path (file://) or a server url.
             *
             * To get notified once the AR-Experience is fully loaded,
             * an ArchitectWorldLoadedListener can be registered.
             */
            architectView.load(FOOD_STORE_ROOT + arExperience);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_loading_ar_experience), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
         * Deletes all cached files of this instance of the ArchitectView.
         * This guarantees that internal storage for this instance of the ArchitectView
         * is cleaned and app-memory does not grow each session.
         *
         * This should be called before architectView.onDestroy
         */
        architectView.clearCache();
        architectView.onDestroy(); // Mandatory ArchitectView lifecycle call
    }
}
