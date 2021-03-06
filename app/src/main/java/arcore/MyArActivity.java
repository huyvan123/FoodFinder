package arcore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.hp.myapplication.R;
import com.example.hp.myapplication.controller.MapsActivity;
import com.example.hp.myapplication.model.detail.DetailResult;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
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
    public static ArchitectView architectView;
    private boolean isPermissionGranted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FoodStoreData foodStoreData = (FoodStoreData) getIntent().getSerializableExtra(FoodFinderUtils.FOOD_STORE_DATA);
        show(foodStoreData);
        isPermissionGranted = true;

    }

    public void getStoreData(){
        final String json = FoodJsonParser.loadStringFromAssets(MyArActivity.this, foodDefinitionsPath);
        categories = FoodJsonParser.getCategoriesFromJsonString(json);
        final FoodStoreData foodStoreData = categories.get(0).getSamples().get(1);
        final String[] permissions = UtilsPermmission.getPermissionsForArFeatures(foodStoreData.getArFeatures());

        permissionManager.checkPermissions(MyArActivity.this, permissions, PermissionManager.WIKITUDE_PERMISSION_REQUEST, new PermissionManager.PermissionManagerCallback() {
            @Override
            public void permissionsGranted(int requestCode) {
                isPermissionGranted = true;
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

    private void getPermission(){

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
//        if(!isPermissionGranted){
//            setArchitecView();
//        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (isPermissionGranted) {
            architectView.onPostCreate();
//        Intent intent = getIntent();
//        Location location = (Location) intent.getSerializableExtra("location");
//        if(location == null){
//            System.out.println("DIVICE LOCATION IS NULLLLLLLLL");
//        }else {
//            System.out.println("DIVICE LOCATION: " + location.getLatitude() + " ," + location.getLongitude());
//        }
            setArchitecView();
        }
    }

    private void setArchitecView(){
        DetailResult detailResult = MapsActivity.mChoseLocationDetail;
        String id = detailResult.getId();
        String title = detailResult.getName();
        String description = detailResult.getFormatedAddress();
        Double lat = detailResult.getGeometry().getLocation().getLatitude();
        Double lng = detailResult.getGeometry().getLocation().getLongtitude();
        Double height = 2.5;
        StringBuilder builder = new StringBuilder("World.setMarkerLocation(");
        builder.append("'").append(id).append("'");
        builder.append(",").append("'").append(title).append("'");
        builder.append(",").append("'").append(description).append("'");
        builder.append(",").append(height);
        builder.append(",").append(lat);
        builder.append(",").append(lng);
        builder.append(")");
        Location mDeviceLocation = MapsActivity.mDeviceLocation;
        try {
            architectView.callJavascript(builder.toString());
            architectView.setLocation(mDeviceLocation.getLatitude(), mDeviceLocation.getLongitude(), mDeviceLocation.getAccuracy());
            /*
             * Loads the AR-Experience, it may be a relative path from assets,
             * an absolute path (file://) or a server url.
             *
             * To get notified once the AR-Experience is fully loaded,
             * an ArchitectWorldLoadedListener can be registered.
             */
            architectView.load(FOOD_STORE_ROOT + arExperience);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_loading_ar_experience), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isPermissionGranted) {
            architectView.onResume(); // Mandatory ArchitectView lifecycle call
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isPermissionGranted) {
            architectView.onPause(); // Mandatory ArchitectView lifecycle call
        }
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
        if (isPermissionGranted) {
            architectView.clearCache();
            architectView.onDestroy(); // Mandatory ArchitectView lifecycle call
        }
    }
}
