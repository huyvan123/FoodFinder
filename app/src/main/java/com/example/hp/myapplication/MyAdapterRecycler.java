package com.example.hp.myapplication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.myapplication.model.detail.DetailResult;
import com.example.hp.myapplication.model.utils.StringUtils;
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
        private int lastVisibleItem, totalItemCount;
        private UseService useService;


        public MyAdapterRecycler(Activity activity, List<DetailResult> storeList, RecyclerView recyclerView) {
            this.activity = activity;
            this.detailResults = storeList;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!isLoading && totalItemCount <= (lastVisibleItem + visiableThreshold)){
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
            this.iLoadmore = loadMore;
        }

        @Override
        public int getItemViewType(int position) {
            System.out.println("position cc: "+ position);
            return detailResults.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.food_listview, viewGroup, false);
                final RecyclerView.ViewHolder holder = new FoodViewHolder(view);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(activity, "vl item", Toast.LENGTH_SHORT).show();
                    }
                });
                return holder;
            } else if (i == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.content_loading, viewGroup, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(viewHolder instanceof FoodViewHolder){
                DetailResult foodStore = detailResults.get(i);
                FoodViewHolder foodViewHolder = (FoodViewHolder) viewHolder;
                try {
                    //image
                    ImageSetting imageSetting = new ImageSetting(foodViewHolder.icon);
                    if((foodStore.getPhotoURLs()) != null){
                        System.out.println("image vao khac null");
                        imageSetting.setPhoto(StringUtils.MAXWIDTH_IMAGE,foodStore.getPhotoURLs().get(0).getPhotoReference(), true);
                    }else {
                        System.out.println("image vao null");
                        imageSetting.setPhoto(StringUtils.MAXWIDTH_IMAGE,foodStore.getIconURL(),false);
                    }
                    //name
                    if(!Validation.isStringEmpty(foodStore.getName())) {
                        foodViewHolder.foodName.setText(foodStore.getName());
                    }else{
                        foodViewHolder.foodName.setText(StringUtils.NA);
                    }
                    //address
                    if(!Validation.isStringEmpty(foodStore.getFormatedAddress())) {
                        foodViewHolder.address.setText(foodStore.getFormatedAddress());
                    }else{
                        foodViewHolder.address.setText(StringUtils.NA);
                    }
                    //phone number
                    if(!Validation.isStringEmpty(foodStore.getFormatedPhoneNumber()) && !Validation.isStringEmpty(foodStore.getInternationalPhoneNumber())){
                        foodViewHolder.phone.setText(foodStore.getFormatedPhoneNumber() + " \n" + foodStore.getInternationalPhoneNumber());
                    }else if(!Validation.isStringEmpty(foodStore.getFormatedPhoneNumber())){
                        foodViewHolder.phone.setText(foodStore.getFormatedPhoneNumber());
                    }else if(!Validation.isStringEmpty(foodStore.getInternationalPhoneNumber())){
                        foodViewHolder.phone.setText(foodStore.getInternationalPhoneNumber());
                    }else{
                        foodViewHolder.phone.setText(StringUtils.NA);
                    }
                    //rating
                    if(!Validation.isStringEmpty(foodStore.getRating())) {
                        foodViewHolder.rating.setText(foodStore.getRating());
                    }else{
                        foodViewHolder.rating.setText(StringUtils.NA);
                    }
                    if(foodStore.getOpeningHour() != null){
                        if(!Validation.isStringEmpty(foodStore.getOpeningHour().getOpenNow())){
                            foodViewHolder.openNow.setText(foodStore.getOpeningHour().getOpenNow());
                        }else{
                            foodViewHolder.openNow.setText(StringUtils.NA);
                        }
                        if(!Validation.isListEmpty(foodStore.getOpeningHour().getOpenHourInWeek())){
                            String openTime = "";
                            for (String s : foodStore.getOpeningHour().getOpenHourInWeek()) {
                                openTime += s + "\n";
                            }
                            if(openTime.trim().equals("")){
                                foodViewHolder.openTime.setText(StringUtils.NA);
                            }else{
                                foodViewHolder.openTime.setText(openTime);
                            }
                        }else{
                            foodViewHolder.openTime.setText(StringUtils.NA);
                        }
                    }else {
                        foodViewHolder.openNow.setText(StringUtils.NA);
                        foodViewHolder.openTime.setText(StringUtils.NA);
                    }
                    if(!Validation.isStringEmpty(foodStore.getWebsite())){
                        foodViewHolder.website.setText(foodStore.getWebsite());
                    }else{
                        foodViewHolder.website.setText(StringUtils.NA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(viewHolder instanceof LoadingViewHolder){
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return detailResults.size();
        }

        public void setLoader(){
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

