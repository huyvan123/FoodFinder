package com.example.hp.myapplication.recyvlerview;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.myapplication.R;
import com.example.hp.myapplication.model.detail.DetailResult;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.example.hp.myapplication.model.utils.Validation;
import com.example.hp.myapplication.mywidget.LoadingViewHolder;
import com.example.hp.myapplication.retrofit.ImageSetting;
import com.example.hp.myapplication.retrofit.UseService;

import java.util.List;

public class MyAdapterRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private ILoadmore iLoadmore;
        private boolean isLoading;
        private Activity activity;
        private List<DetailResult> detailResults;
        private int visiableThreshold = 5;
        private boolean checkItemcount = true;
        private int lastVisibleItem, totalItemCount;
        private UseService useService;


    /**
     * 1. go constructor
     * @param activity
     * @param storeList
     * @param recyclerView
     * @param useService
     */
        public MyAdapterRecycler(Activity activity, List<DetailResult> storeList, RecyclerView recyclerView, UseService useService) {
            System.out.println("FOOD-[MyAdapterRecycler]: VAO MyAdapterRecycler constructor");
            this.activity = activity;
            this.detailResults = storeList;
            this.useService = useService;
            recyclerView.setAdapter(this);
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    checkItemcount = true;
                    System.out.println("FOOD-[MyAdapterRecycler]: VAO on scroll listener");
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    System.out.println("FUCK VAO ON SCROLL");
                    System.out.println("FUCK IS LOADING: "+ isLoading);
                    System.out.println("FUCK LAST VISIBLE ITEM: "+ lastVisibleItem);
                    System.out.println("FUCK visiableThreshold: "+ visiableThreshold);
                    System.out.println("FUCK totalItemCount: "+ totalItemCount);
                    if(!isLoading && totalItemCount <= (lastVisibleItem + visiableThreshold) && lastVisibleItem%4 ==0){
                        System.out.println("VAO LOAD MORE");
                        if(iLoadmore !=null){
                            System.out.println("totalItemCount: "+ totalItemCount);
                            iLoadmore.onLoadMore();
                            isLoading = true;
                        }
                    }
                }
            });
        }

        public void setLoadMore(ILoadmore loadMore){
            System.out.println("FOOD-[MyAdapterRecycler]: SET LOAD MORE");
            this.iLoadmore = loadMore;
        }

    /**
     * 3.
     * @param position
     * @return
     */
    @Override
        public int getItemViewType(int position) {
            System.out.println("FOOD-[MyAdapterRecycler]: GET ITEM VIEW TYPE with position: "+ position);
            return detailResults.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

    /**
     * 4.
     * @param viewGroup
     * @param i
     * @return
     */
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            System.out.println("FOOD-[MyAdapterRecycler]: onCreateViewHolder");
            if (i == VIEW_TYPE_ITEM) {
                System.out.println("FOOD-[MyAdapterRecycler]: onCreateViewHolder - VIEW_TYPE_ITEM");
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.food_listview, viewGroup, false);
                final RecyclerView.ViewHolder holder = new FoodViewHolder(view);
                view.setOnClickListener(useService);
                return holder;
            } else if (i == VIEW_TYPE_LOADING) {
                System.out.println("FOOD-[MyAdapterRecycler]: onCreateViewHolder - VIEW_TYPE_LOADING");
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.content_loading, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            System.out.println("FOOD-[MyAdapterRecycler]: onCreateViewHolder - nothing");
            return new FoodViewHolder(viewGroup);
        }

    /**
     * 5.
     * @param viewHolder
     * @param i
     */
    @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            System.out.println("FOOD-[MyAdapterRecycler]: onBindViewHolder");
            if(viewHolder instanceof FoodViewHolder){
                DetailResult foodStore = detailResults.get(i);
                FoodViewHolder foodViewHolder = (FoodViewHolder) viewHolder;
                try {
                    //image
                    ImageSetting imageSetting = new ImageSetting(foodViewHolder.icon);
                    if((foodStore.getPhotoURLs()) != null){
                        System.out.println("image vao khac null");
                        imageSetting.setPhoto(FoodFinderUtils.MAXWIDTH_IMAGE,foodStore.getPhotoURLs().get(0).getPhotoReference(), true);
                    }else {
                        System.out.println("image vao null");
                        imageSetting.setPhoto(FoodFinderUtils.MAXWIDTH_IMAGE,foodStore.getIconURL(),false);
                    }
                    //name
                    if(!Validation.isStringEmpty(foodStore.getName())) {
                        foodViewHolder.foodName.setText(foodStore.getName());
                    }else{
                        foodViewHolder.foodName.setText(FoodFinderUtils.NA);
                    }
                    //address
                    if(!Validation.isStringEmpty(foodStore.getFormatedAddress())) {
                        foodViewHolder.address.setText(foodStore.getFormatedAddress());
                    }else{
                        foodViewHolder.address.setText(FoodFinderUtils.NA);
                    }
                    //phone number
                    if(!Validation.isStringEmpty(foodStore.getFormatedPhoneNumber()) && !Validation.isStringEmpty(foodStore.getInternationalPhoneNumber())){
                        foodViewHolder.phone.setText(foodStore.getFormatedPhoneNumber() + " \n" + foodStore.getInternationalPhoneNumber());
                    }else if(!Validation.isStringEmpty(foodStore.getFormatedPhoneNumber())){
                        foodViewHolder.phone.setText(foodStore.getFormatedPhoneNumber());
                    }else if(!Validation.isStringEmpty(foodStore.getInternationalPhoneNumber())){
                        foodViewHolder.phone.setText(foodStore.getInternationalPhoneNumber());
                    }else{
                        foodViewHolder.phone.setText(FoodFinderUtils.NA);
                    }
                    //rating
                    if(!Validation.isStringEmpty(foodStore.getRating())) {
                        foodViewHolder.rating.setText(foodStore.getRating());
                    }else{
                        foodViewHolder.rating.setText(FoodFinderUtils.NA);
                    }
                    if(foodStore.getOpeningHour() != null){
                        if(!Validation.isStringEmpty(foodStore.getOpeningHour().getOpenNow())){
                            foodViewHolder.openNow.setText(foodStore.getOpeningHour().getOpenNow());
                        }else{
                            foodViewHolder.openNow.setText(FoodFinderUtils.NA);
                        }
                        if(!Validation.isListEmpty(foodStore.getOpeningHour().getOpenHourInWeek())){
                            String openTime = "";
                            for (String s : foodStore.getOpeningHour().getOpenHourInWeek()) {
                                openTime += s + "\n";
                            }
                            if(openTime.trim().equals("")){
                                foodViewHolder.openTime.setText(FoodFinderUtils.NA);
                            }else{
                                foodViewHolder.openTime.setText(openTime);
                            }
                        }else{
                            foodViewHolder.openTime.setText(FoodFinderUtils.NA);
                        }
                    }else {
                        foodViewHolder.openNow.setText(FoodFinderUtils.NA);
                        foodViewHolder.openTime.setText(FoodFinderUtils.NA);
                    }
                    if(!Validation.isStringEmpty(foodStore.getWebsite())){
                        foodViewHolder.website.setText(foodStore.getWebsite());
                    }else{
                        foodViewHolder.website.setText(FoodFinderUtils.NA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkItemcount = false;
            }else if(viewHolder instanceof LoadingViewHolder){
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
                //set run progress bar
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

    /**
     * 2. go 2 turn
     * @return
     */
    @Override
        public int getItemCount() {
//        if(!checkItemcount){
//            return -1;
//        }
            System.out.println("FOOD-[MyAdapterRecycler]: getItemCount : "+ detailResults.size());
            return detailResults.size();
        }

        public void setLoader(){
            System.out.println("FOOD-[MyAdapterRecycler]: setLoader : false");
            isLoading = false;
        }
}

class FoodViewHolder extends RecyclerView.ViewHolder{
    ImageView icon;
    TextView foodName, address, phone, rating, openNow, openTime, website;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.food_icon);
        foodName = itemView.findViewById(R.id.food_name);
        address = itemView.findViewById(R.id.food_address);
        phone = itemView.findViewById(R.id.food_phonenumber);
        rating = itemView.findViewById(R.id.food_rating);
        openNow = itemView.findViewById(R.id.food_open_now);
        openTime = itemView.findViewById(R.id.food_open_time);
        website = itemView.findViewById(R.id.food_website);
    }
}

