package com.example.hp.myapplication.retrofit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hp.myapplication.ILoadmore;
import com.example.hp.myapplication.recyvlerview.MyAdapterRecycler;
import com.example.hp.myapplication.WrapContentLinearLayoutManager;
import com.example.hp.myapplication.controller.MapsActivity;
import com.example.hp.myapplication.model.detail.DetailResponse;
import com.example.hp.myapplication.model.detail.DetailResult;
import com.example.hp.myapplication.model.directions.DirectionResponse;
import com.example.hp.myapplication.model.directions.Leg;
import com.example.hp.myapplication.model.directions.Route;
import com.example.hp.myapplication.model.directions.Step;
import com.example.hp.myapplication.model.search.SearchResponse;
import com.example.hp.myapplication.model.search.SearchResults;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 01/07/2018
 *  author HuyVan
 */

public class UseService implements View.OnClickListener{
    public static List<Marker> markerList = new ArrayList<>();;
    public static List<SearchResults> searchResults = new ArrayList<>();
    private static int defaultQuantity = 0;
    private IGoodService iService;
    private GoogleMap mMap;
    private Button searchButton;
    private MapsActivity activity;
    private List<DetailResult> detailResults;
    private Bitmap bitmap;
    private List<Map<String,String>> requestURL;
    //count1 is request quantity, count2 to check search quantity, count3 check search detail quantity
    private int count1,count2, count3;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView recyclerView;
    private MyAdapterRecycler adapterRecycler;
    private boolean checkOneClick = true;
    private String destination;

    /**
     *
     * @param googleMap
     * @param button
     * @param activity
     * @param bottomSheetBehavior
     * @param recyclerView
     * @param adapterRecycler
     */
    public UseService(GoogleMap googleMap, Button button, MapsActivity activity, BottomSheetBehavior bottomSheetBehavior, RecyclerView recyclerView, MyAdapterRecycler adapterRecycler) {
        System.out.println("retrofit vao khoi tao");
        this.iService = PlaceApi.getIGoodService();
        this.mMap = googleMap;
        this.searchButton = button;
        this.activity = activity;
        requestURL = new ArrayList<>();
        this.bottomSheetBehavior = bottomSheetBehavior;
        this.recyclerView = recyclerView;
        this.adapterRecycler = adapterRecycler;
    }

    /**
     * set all marker of food store to map
     */
    private void setAllMarkerToMap(){
        System.out.println("vao marker");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("searchResults list size: "+ searchResults.size());
                mMap.clear();
                markerList.clear();
                mMap.addMarker(new MarkerOptions().position(activity.marker.getPosition()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                markerList = new ArrayList<>();
                Marker marker;
                for (SearchResults searchResult: searchResults){
                    double lat = searchResult.getGeometry().getLocation().getLatitude();
                    double lng = searchResult.getGeometry().getLocation().getLongtitude();
                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).icon(BitmapDescriptorFactory.defaultMarker()));
                    markerList.add(marker);
                }
                System.out.println("marker list size: "+ markerList.size());
            }
        });
    }

    /**
     * load food_store info from quantity and begin point
     * @param quantity
     * @param begin
     */
    public void loadFoodStore(int quantity, int begin){
        defaultQuantity = quantity;
        count3 = 0;

        for(int i = begin; i < (begin + quantity); i++){
            try {
                getFoodStoreDetail(searchResults.get(i).getPlaceId());
            }catch (IndexOutOfBoundsException e){
                break;
            }
        }
    }

    /**
     * get food store detail
     * @param placeId
     */
    private void getFoodStoreDetail(String placeId){
        iService.getDetailResponse(placeId, FoodFinderUtils.API_SERVER_KEY).enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                count3++;
                if (response.isSuccessful()){
                    DetailResult detailResult = response.body().getDetailResults();
                    detailResults.add(response.body().getDetailResults());
                }
                if(count3==defaultQuantity && checkOneClick){
                    checkOneClick = false;
                    adapterRecycler.notifyDataSetChanged();
                    adapterRecycler.setLoader();
                    setBottomSheet();
                }else if(count3==defaultQuantity){
                    adapterRecycler.notifyDataSetChanged();
                    adapterRecycler.setLoader();
                }
            }

            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {

            }
        });
    }

    /**
     * add search style, type search
     * @param type
     * @param lat
     * @param lng
     * @param radius
     * @param keyWord
     */
    public void addRequest(String type, double lat, double lng, int radius, String keyWord){
        count1++;
        Map<String, String> map = new HashMap<>();
        map.put(FoodFinderUtils.TYPE,type);
        map.put(FoodFinderUtils.LOCATION,new StringBuilder(String.valueOf(lat)).append(",").append(String.valueOf(lng)).toString());
        map.put(FoodFinderUtils.RADIUS,String.valueOf(radius));
        map.put(FoodFinderUtils.KEYWORD,keyWord);
        map.put(FoodFinderUtils.KEY, FoodFinderUtils.API_SERVER_KEY);
        requestURL.add(map);
    }

    /**
     * go first to find food store
     */
    public void action(){
        enableProgressBar();
        for (Map<String, String> url : requestURL){
            try {
                iService.getSearchResponse(url.get(FoodFinderUtils.LOCATION),url.get(FoodFinderUtils.RADIUS),
                        url.get(FoodFinderUtils.TYPE) , url.get(FoodFinderUtils.KEYWORD),url.get(FoodFinderUtils.KEY) ).enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        count2++;
                        System.out.println("count1: "+ count1);
                        System.out.println("count2: "+ count2);
                        if(response.isSuccessful()){
                            for (SearchResults result: response.body().getResults()) {
                                if(!searchResults.contains(result)){
                                    searchResults.add(result);
                                }
                            }
                        }
                        if(count1 == count2){
                            System.out.println("search result size: "+ searchResults.size());
                            activity.tvSoluong.setText(String.valueOf(searchResults.size()));
                            setAllMarkerToMap();
                            detailResults.remove(detailResults.size() -1 );
                            adapterRecycler.notifyItemRemoved(detailResults.size());
                            loadFoodStore(FoodFinderUtils.DEFAULT_QUANTITY, FoodFinderUtils.DEFAULT_BEGIN);
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        count2++;
                        System.out.println("count1 fail: "+ count1);
                        System.out.println("count2 fail: "+ count2);
                        System.out.println("fail error: "+ t.getMessage());
                        if(count1 == count2){
                            if(searchResults.size() == 0){
                                activity.finish();
                            }else {
                                System.out.println("search result size: " + searchResults.size());
                                activity.tvSoluong.setText(String.valueOf(searchResults.size()));
                                setAllMarkerToMap();
                                detailResults.remove(detailResults.size() -1 );
                                adapterRecycler.notifyItemRemoved(detailResults.size());
                                loadFoodStore(FoodFinderUtils.DEFAULT_QUANTITY, FoodFinderUtils.DEFAULT_BEGIN);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * set recycler to bottom sheet
     */
    private void setBottomSheet(){
        adapterRecycler.setLoadMore(new ILoadmore() {
            @Override
            public void onLoadMore() {
                System.out.println("marher lit sai: "+ UseService.markerList.size());
                if(detailResults.size() <= UseService.markerList.size()) {
                    System.out.println("recycler load more");
                    detailResults.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            adapterRecycler.notifyItemInserted(detailResults.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            detailResults.remove(detailResults.size() - 1);
                            adapterRecycler.notifyItemRemoved(detailResults.size());
                            //add load more
                            loadFoodStore(FoodFinderUtils.DEFAULT_QUANTITY,detailResults.size());
                        }
                    }, FoodFinderUtils.DEFAULT_TIME_WAITING);
                }else{
                    Toast.makeText(activity, "Da tai het du lieu!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        System.out.println("done");
    }

    /**
     * turn on progress bar
     */
    private void enableProgressBar(){
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(activity));
        detailResults = new ArrayList<>();
        adapterRecycler = new MyAdapterRecycler(activity, detailResults, recyclerView, this);
        recyclerView.setAdapter(adapterRecycler);
        detailResults.add(null);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapterRecycler.notifyItemInserted(detailResults.size() - 1);
            }
        });

    }

    @Override
    public void onClick(View view) {
        activity.isDeviceLocationChanged = true;
        activity.imageButton.setVisibility(View.VISIBLE);
        System.out.println("vao onclick");
        int position = recyclerView.getChildAdapterPosition(view);
        DetailResult detailResult = detailResults.get(position);
        MapsActivity.mChoseLocationDetail = detailResult;
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        mMap.clear();
        double lat = detailResult.getGeometry().getLocation().getLatitude();
        double lng = detailResult.getGeometry().getLocation().getLongtitude();
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).icon(BitmapDescriptorFactory.defaultMarker()));
        activity.marker = mMap.addMarker(new MarkerOptions().position(new LatLng(activity.mDeviceLocation.getLatitude(),activity.mDeviceLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        destination = detailResult.getGeometry().getLocation().getLatitude()+","+detailResult.getGeometry().getLocation().getLongtitude();
        String origin = activity.mDeviceLocation.getLatitude()+","+activity.mDeviceLocation.getLongitude();
        getDirections(origin);
    }

    public void getDirections(String originLocation){
        System.out.println("vao get direction");
        iService.getDirectionsResponse(originLocation,destination, FoodFinderUtils.DRIVING,FoodFinderUtils.VIETNAMESE, FoodFinderUtils.API_CLIENT_KEY)
                .enqueue(new Callback<DirectionResponse>() {
                    @Override
                    public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                        System.out.println("direction url: "+ call.request().url());
                        if (response.isSuccessful()) {
                            System.out.println("directions vao successful");
                            DirectionResponse directionResponse = response.body();
                            PolylineOptions polylineOptions = null;
                            for (Route route: directionResponse.getRoutes()) {
                                for (Leg leg: route.getLegs()) {
                                    MapsActivity.legDirections = leg;
                                    for (Step step: leg.getSteps()) {
                                        polylineOptions = new PolylineOptions();
                                        String point = step.getPolyline().getPoint();
                                        List<LatLng> latLngs = decodePolyline(point);
                                        polylineOptions.addAll(latLngs);
                                        polylineOptions.width(15);
                                        polylineOptions.color(Color.BLUE);
                                        polylineOptions.geodesic(true);
                                        mMap.addPolyline(polylineOptions);
                                    }
                                }
                            }
                            if(polylineOptions != null){
//                                mMap.addPolyline(polylineOptions);
                            }else{
                                Toast.makeText(activity, "Direction not found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<DirectionResponse> call, Throwable t) {
                        System.out.println("url: "+ call.request().url());
                    }
                });
    }
    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            try {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index) - 63;
                    index++;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index) - 63;
                    index++;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                LatLng latLng = new LatLng((double) (lat / 1E5),  (double) (lng / 1E5));
                poly.add(latLng);
            } catch (Exception e) {
                break;
            }
        }
        return poly;
    }
}
