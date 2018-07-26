package com.example.hp.myapplication.retrofit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.example.hp.myapplication.ILoadmore;
import com.example.hp.myapplication.MyAdapterRecycler;
import com.example.hp.myapplication.WrapContentLinearLayoutManager;
import com.example.hp.myapplication.controller.MapsActivity;
import com.example.hp.myapplication.model.detail.DetailResponse;
import com.example.hp.myapplication.model.detail.DetailResult;
import com.example.hp.myapplication.model.search.SearchResponse;
import com.example.hp.myapplication.model.search.SearchResults;
import com.example.hp.myapplication.model.utils.StringUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UseService{
    private IGoodService iService;
    public static List<Marker> markerList = new ArrayList<>();;
    private GoogleMap mMap;
    private Button searchButton;
    private Activity activity;
    public static List<SearchResults> searchResults = new ArrayList<>();
    private List<DetailResult> detailResults;
    private Bitmap bitmap;
    private List<Map<String,String>> requestURL;
    //count1 is request quntity, count2 to check search quantity, count3 check search detail quantity
    private int count1,count2, count3;
    private static int defaultQuantity =0;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView recyclerView;
    private MyAdapterRecycler adapterRecycler;
    private boolean checkOneClick = true;

    public UseService(GoogleMap googleMap, Button button, Activity activity, BottomSheetBehavior bottomSheetBehavior, RecyclerView recyclerView, MyAdapterRecycler adapterRecycler) {
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

    private void setAllMarkerToMap(){
        System.out.println("vao marker");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("searchResults list size: "+ searchResults.size());
                mMap.clear();
                markerList.clear();
                mMap.addMarker(new MarkerOptions().position(MapsActivity.marker.getPosition()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
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

    private void getFoodStoreDetail(String placeId){
        iService.getDetailResponse(placeId,StringUtils.API_KEY).enqueue(new Callback<DetailResponse>() {
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

    public void addRequest(String type, double lat, double lng, int radius, String keyWord){
        count1++;
        Map<String, String> map = new HashMap<>();
        map.put(StringUtils.TYPE,type);
        map.put(StringUtils.LOCATION,new StringBuilder(String.valueOf(lat)).append(",").append(String.valueOf(lng)).toString());
        map.put(StringUtils.RADIUS,String.valueOf(radius));
        map.put(StringUtils.KEYWORD,keyWord);
        map.put(StringUtils.KEY,StringUtils.API_KEY);
        requestURL.add(map);
    }

    public void action(){
        enableProgressBar();
        for (Map<String, String> url : requestURL){
            try {
                iService.getSearchResponse(url.get(StringUtils.LOCATION),url.get(StringUtils.RADIUS),
                        url.get(StringUtils.TYPE) , url.get(StringUtils.KEYWORD),url.get(StringUtils.KEY) ).enqueue(new Callback<SearchResponse>() {
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
                            MapsActivity.tvSoluong.setText(String.valueOf(searchResults.size()));
                            setAllMarkerToMap();
                            detailResults.remove(detailResults.size() -1 );
                            adapterRecycler.notifyItemRemoved(detailResults.size());
                            loadFoodStore(5,0);
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
                                MapsActivity.tvSoluong.setText(String.valueOf(searchResults.size()));
                                setAllMarkerToMap();
                                detailResults.remove(detailResults.size() -1 );
                                adapterRecycler.notifyItemRemoved(detailResults.size());
                                loadFoodStore(5,0);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
                            loadFoodStore(StringUtils.DEFAULT_QUANTITY,detailResults.size());
//                            adapterRecycler.notifyDataSetChanged();
//                            adapterRecycler.setLoader();
                        }
                    }, 3000);
                }else{
                    Toast.makeText(activity, "Da tai het du lieu!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        System.out.println("done");
    }

    private void enableProgressBar(){
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(activity));
        detailResults = new ArrayList<>();
        adapterRecycler = new MyAdapterRecycler(activity, detailResults, recyclerView);
        recyclerView.setAdapter(adapterRecycler);
        detailResults.add(null);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapterRecycler.notifyItemInserted(detailResults.size() - 1);
            }
        });

    }
}
