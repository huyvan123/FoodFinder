package com.example.hp.myapplication.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.myapplication.recyvlerview.PermissionAccess;
import com.example.hp.myapplication.R;
import com.example.hp.myapplication.recyvlerview.WrapContentLinearLayoutManager;
import com.example.hp.myapplication.model.detail.DetailResult;
import com.example.hp.myapplication.model.directions.Leg;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.example.hp.myapplication.recyvlerview.MyAdapterRecycler;
import com.example.hp.myapplication.recyvlerview.RouteAdapterRecycler;
import com.example.hp.myapplication.retrofit.UseService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.common.permission.PermissionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import arcore.FoodJsonParser;
import arcore.FoodStoreCategory;
import arcore.FoodStoreData;
import arcore.MyArActivity;
import arcore.UtilsPermmission;

import static android.location.LocationManager.GPS_PROVIDER;
import static com.example.hp.myapplication.recyvlerview.PermissionAccess.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

/**
 * 01/07/2018
 * author Huy Van
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnPoiClickListener, GoogleMap.OnMyLocationButtonClickListener,
        LocationListener {

    public static DetailResult mChoseLocationDetail = null;
    public Marker marker;
    public static Location mDeviceLocation = null;
    public boolean isDeviceLocationChanged = false;
    public ImageButton imageButton;
    public TextView tvSoluong;
    public static Leg legDirections;
    private GoogleMap mMap;
    private Button searchStoreBtn, dialogButton, cancelBtn, startArBtn;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager manager;
    private FusedLocationProviderClient mFusedClientProvider ;
    private boolean isDialogDismiss;
    private PlaceAutocompleteFragment placeAutoComplete;

    private boolean mLocationPermissionGranted;
    //2 locations to check multi click on 1 location
    private Location mLastKnownLocation, mBeforeLocation;
    private BottomSheetBehavior bottomSheetBehavior;
    private Dialog dialog;
    private TextView dialogContent, totalDistance;
    private RecyclerView recyclerView, routeRecycler;
    //adapter for food list detail
    private MyAdapterRecycler adapterRecycler;
    //adapter of recycler for step of direction
    private RouteAdapterRecycler routeAdapterRecycler;
    private UseService useService;

    /**
     * oncreate
     * @param savedInstanceState
     */
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //create dialog contents
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_internet_request);
        dialogContent = dialog.findViewById(R.id._permission_content);
        dialogButton = dialog.findViewById(R.id.confirm);
        dialogButton.setOnClickListener(this);
        //check internet connection
        try {
            if(new PermissionAccess().execute(MapsActivity.this).get() == false){
                dialogContent.setText(FoodFinderUtils.PERMISSION_INTERNET);
                dialog.show();
            }else{
                //init attributes
                init(savedInstanceState);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * init attributes
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState){
        mBeforeLocation = new Location("Huy Van");
        mBeforeLocation.setLongitude(0.0);
        mBeforeLocation.setLatitude(0.0);
        mDeviceLocation = new Location("HuyVan");
        mDeviceLocation.setLongitude(0.0);
        mDeviceLocation.setLatitude(0.0);
        legDirections = new Leg();
        recyclerView = findViewById(R.id.RecycleviewList);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this));
//        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tvSoluong = findViewById(R.id.so_luong);
        searchStoreBtn = findViewById(R.id.search_store);
        searchStoreBtn.setOnClickListener(this);
        imageButton = findViewById(R.id.imageButton);
        imageButton.setVisibility(View.GONE);
        imageButton.setOnClickListener(this);
        mFusedClientProvider = LocationServices.getFusedLocationProviderClient(this);
        if(savedInstanceState != null){
            mLastKnownLocation = savedInstanceState.getParcelable(FoodFinderUtils.KEY_LOCATION);
        }
        //set autocomplete search bar
        setPlaceAutoComplete();
        //enable google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }


    private void setPlaceAutoComplete(){
//        placeAutoComplete = (getSupportFragmentManager().findFragmentById(R.id.place_autocomplete));
        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                updateMarker(latLng);
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

    }


    /**
     * on map ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("vao on map ready");
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker != null) {
                    View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_content, (FrameLayout) findViewById(R.id.map), false);

                    TextView title = infoWindow.findViewById(R.id.title);
                    title.setText(marker.getTitle());

                    TextView snippet = infoWindow.findViewById(R.id.snippet);
                    snippet.setText(marker.getSnippet());

                    return infoWindow;
                }else{
                    Log.i("wtf:", "marker is null");
                    return null;
                }
            }
        });
        //get permission
        getLocationPerission();

    }

    /**
     * request permission result
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                    getLocationPerission();
                }
                break;
            default:
                dialogContent.setText(FoodFinderUtils.PERMISSION_LOCATION);
                dialog.show();
        }
    }

    /**
     * update device location ui
     */
    @SuppressLint("MissingPermission")
    private void updateLocationUI(){
        if(mMap == null){
            return;
        }
        try{
            if(mLocationPermissionGranted){
                System.out.println("key_01 vao set my location");
                mMap.setMyLocationEnabled(true);
//                mMap.setPadding();
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                setUpMap();
            }else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPerission();
            }
        }catch (Exception ex){
            Log.e("Excption: %s", ex.getMessage());
        }
    }

    private void setUpMap() {

        // Find myLocationButton view
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        @SuppressLint("ResourceType") View myLocationButton = mapFragment.getView().findViewById(0x2);

        if (myLocationButton != null && myLocationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();

            // Align it to - parent BOTTOM|LEFT
            params.addRule(15);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

            // Update margins, set to 10dp
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics());
            params.setMargins(margin, margin, margin, margin);

            myLocationButton.setLayoutParams(params);
        }
    }

    /**
     * set device location to map
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){
        try{
            if(mLocationPermissionGranted){
                mFusedClientProvider.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    System.out.println("key_01 location != null");
                                    setLocation1FromLocation2(mDeviceLocation,location);
                                    mLastKnownLocation = location;
                                    LatLng latLng = new LatLng(mLastKnownLocation.getLatitude()
                                            , mLastKnownLocation.getLongitude());
                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng
                                            , FoodFinderUtils.DEFAULT_ZOOM),1000,null);
                                    }else{
                                    System.out.println("key_01 location == null");
                                            turnOnGPS();
                                }
                            }
                        });
            }
        }catch (Exception e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * on selected item
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.none:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return true;
    }

    /**
     * create map style
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_menu, menu);
        return true;
    }

    /**
     * on map clicked
     * @param latLng
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapClick(LatLng latLng) {
        updateMarker(latLng);
    }

    /**
     * update marker
     * @param latLng
     */
    private void updateMarker(LatLng latLng){
        //remove current marker location
        if(marker != null){
            marker.remove();
        }
        //set current marker location and move camera
        mLastKnownLocation.setLatitude(latLng.latitude);
        mLastKnownLocation.setLongitude(latLng.longitude);
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, FoodFinderUtils.DEFAULT_ZOOM),1000,null);
    }

    /**
     * on button or something clicked
     * @param view
     */
    @Override
    public void onClick(View view) {
        //on tìm clicked
        if(view.getId() == R.id.search_store){
            isDialogDismiss = false;
            isDeviceLocationChanged = false;
            if(!isLocationTheSame(mBeforeLocation,mLastKnownLocation)) {
                imageButton.setVisibility(View.GONE);
                setLocation1FromLocation2(mBeforeLocation,mLastKnownLocation);
                UseService.searchResults = new ArrayList<>();
                bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
                useService = new UseService(mMap,searchStoreBtn,this,bottomSheetBehavior,recyclerView,adapterRecycler);
                useService.addRequest(FoodFinderUtils.SEARCH_TYPE_MEAL_DELEVERY, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), FoodFinderUtils.DEFAULT_RADIUS, null);
                useService.addRequest(FoodFinderUtils.SEARCH_TYPE_MEAL_TAKEAWAY, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), FoodFinderUtils.DEFAULT_RADIUS, null);
                useService.addRequest(FoodFinderUtils.SEARCH_TYPE_RESTAURANT, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), FoodFinderUtils.DEFAULT_RADIUS, null);
                useService.addRequest(FoodFinderUtils.SEARCH_TYPE_SUPERMARKET, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), FoodFinderUtils.DEFAULT_RADIUS, null);
                useService.addRequest(null, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), FoodFinderUtils.DEFAULT_RADIUS, FoodFinderUtils.SEARCH_KEY_QUAN_COM);
                useService.addRequest(null, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), FoodFinderUtils.DEFAULT_RADIUS, FoodFinderUtils.SEARCH_KEY_QUAN_NHAU);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                useService.action();
            }else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                imageButton.setVisibility(View.VISIBLE);
            }
            //cancel button at direction clicked
        }else if(view.getId() == dialogButton.getId()){
            dialog.cancel();
            this.finishAffinity();
            //on view direction button clicked
        }else if(view.getId() == imageButton.getId()){
            if(isDialogDismiss) {
                dialog.show();
            }else {
                dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_route);
                totalDistance = dialog.findViewById(R.id.total_distance);
                cancelBtn = dialog.findViewById(R.id.cancel_dialog_steps);
                cancelBtn.setOnClickListener(this);
                startArBtn = dialog.findViewById(R.id.get_start_ar);
                startArBtn.setOnClickListener(this);
                totalDistance.setText(legDirections.getDistance().getText());

                //do show recycler
                routeRecycler = dialog.findViewById(R.id.step_recyrcler);
                routeRecycler.setLayoutManager(new WrapContentLinearLayoutManager(this));
                routeAdapterRecycler = new RouteAdapterRecycler(legDirections.getSteps(), this);
                routeRecycler.setAdapter(routeAdapterRecycler);
                dialog.show();
            }
        }else if(view.getId() == cancelBtn.getId()){
            isDialogDismiss = true;
            dialog.dismiss();
        }else if(view.getId() == startArBtn.getId()){
            //ar would be here
            if(mDeviceLocation == null){
                System.out.println("DIVICE LOCATION IS NULLLLLLLLL");
            }else {
                System.out.println("DIVICE LOCATION: " + mDeviceLocation.getLatitude() + " ," + mDeviceLocation.getLongitude());
                System.out.println("DIVICE LOCATION 1: " + mChoseLocationDetail.getGeometry().getLocation().getLatitude()
                        + " ," + mChoseLocationDetail.getGeometry().getLocation().getLongtitude());
                checkArPermission();
//                Intent intent = new Intent(this, MyArActivity.class);
//                startActivity(intent);
            }
        }
    }

    /**
     * on marker clicked
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Geocoder geocoder =  new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);
            String content = "";
            for (Address address: addresses){
                if(address.getAddressLine(0) !=null){
                    content = address.getAddressLine(0);
                }
            }
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * on POI clicked
     * @param pointOfInterest
     */
    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        Toast.makeText(this, pointOfInterest.name, Toast.LENGTH_SHORT).show();
    }

    /**
     * on my location clicked
     * @return
     */
    @Override
    public boolean onMyLocationButtonClick() {
        if(marker !=null){
            marker.remove();
        }
        getDeviceLocation();
        return true;
    }

    /**
     * turn on gps
     */
    private void turnOnGPS(){
        if (mGoogleApiClient == null) {
            System.out.println("key_01 vao google api client = null");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);

            LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            System.out.println("key_01 vao LocationSettingsStatusCodes.SUCCESS");
                            try{
                                manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1.0f, MapsActivity.this);
                                if (!manager.isProviderEnabled( GPS_PROVIDER ) ) {
                                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                                }else{
                                    System.out.println("key_01 update location ui");
                                    updateLocationUI();
                                    getDeviceLocation();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            System.out.println("key_01 vao LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                            try {
                                status.startResolutionForResult(
                                        MapsActivity.this,
                                        FoodFinderUtils.REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            System.out.println("key_01 vao LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                            break;

                            default:
                                System.out.println("vao nothing");
                    }
                }
            });
        }else{
            updateLocationUI();
            getDeviceLocation();
        }

    }

    /**
     * on activity result
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case FoodFinderUtils.REQUEST_CHECK_SETTINGS:
//                this.finish();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        System.out.println("key_01 vao 0K");
                        turnOnGPS();
                        break;
                    case Activity.RESULT_CANCELED:
                        System.out.println("key_01 not connected");
                        this.finishAffinity();
                        break;
                    default:
                        break;
                }
                break;
            case FoodFinderUtils.PLACE_PICKER_REQUEST:
                if(requestCode == FoodFinderUtils.PLACE_PICKER_REQUEST){
                    if(resultCode == RESULT_OK){
                        Place place = PlacePicker.getPlace(this, intent);
                        mLastKnownLocation.setLatitude(place.getLatLng().latitude);
                        mLastKnownLocation.setLongitude(place.getLatLng().longitude);
                        getDeviceLocation();
                    }
                }
        }
    }

    /**
     * 4 methods below are override from locationListener
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
//        try {
//            if (isDeviceLocationChanged) {
//                String origin = mDeviceLocation.getLatitude() + "," + mDeviceLocation.getLongitude();
////            useService.getDirections(origin);
//            } else {
//                setLocation1FromLocation2(mDeviceLocation, location);
//                updateMarker(new LatLng(location.getLatitude(), location.getLongitude()));
//            }
//            MyArActivity.architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy());
//            System.out.println("key_01 loaction changed");
//        }catch (Exception e){
//            Toast.makeText(this, "Onlocation change throw ex: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
    }

    private void getLocationPerission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
            System.out.println("key_01 after check location ui");
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMapClickListener(this);
            mMap.setOnPoiClickListener(this);
            turnOnGPS();
        }else{
            System.out.println("key_01 enter else");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * check 2 locations are the same
     * @param l1
     * @param l2
     * @return
     */
    private boolean isLocationTheSame(Location l1, Location l2){
        if(l1.getLatitude() == l2.getLatitude() && l1.getLongitude() == l2.getLongitude()){
            return true;
        }
        return false;
    }

    /**
     * setLocation1FromLocation2
     * @param location1
     * @param location2
     */

    private void setLocation1FromLocation2(Location location1, Location location2){
        if(location1 == null){
            location1 = location2;
        }else {
            location1.setLatitude(location2.getLatitude());
            location1.setLongitude(location2.getLongitude());
        }
    }

    /**
     * check ar permission
     */
    private void checkArPermission(){
        final String json = FoodJsonParser.loadStringFromAssets(this, "POI/poi.json");
        List<FoodStoreCategory> categories;
        categories = FoodJsonParser.getCategoriesFromJsonString(json);
        final FoodStoreData foodStoreData = categories.get(0).getSamples().get(1);
        PermissionManager permissionManager = ArchitectView.getPermissionManager();
        final String[] permissions = UtilsPermmission.getPermissionsForArFeatures(foodStoreData.getArFeatures());
        permissionManager.checkPermissions(this, permissions, PermissionManager.WIKITUDE_PERMISSION_REQUEST, new PermissionManager.PermissionManagerCallback() {
            @Override
            public void permissionsGranted(int requestCode) {
                System.out.println("request code: "+ requestCode);
                System.out.println("vao ganed");
                Intent intent = new Intent(MapsActivity.this, MyArActivity.class);
                intent.putExtra(FoodFinderUtils.FOOD_STORE_DATA, foodStoreData);
                startActivity(intent);
            }

            @Override
            public void permissionsDenied(@NonNull String[] deniedPermissions) {
                Toast.makeText(MapsActivity.this, getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void showPermissionRationale(final int requestCode, @NonNull String[] strings) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MapsActivity.this);
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

//    /**
//     * set on search location button clicked
//     * @param view
//     */
//    public void onMapSearch(View view) {
//        EditText locationSearch = (EditText) findViewById(R.id.editText);
//        String location = locationSearch.getText().toString();
//        List<Address>addressList = null;
//
//        if (location != null || !location.equals("")) {
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                addressList = geocoder.getFromLocationName(location, 1);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Address address = addressList.get(0);
//            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//            updateMarker(latLng);
//        }
//    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}