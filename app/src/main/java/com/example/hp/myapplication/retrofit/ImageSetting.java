package com.example.hp.myapplication.retrofit;

import android.widget.ImageView;

import com.example.hp.myapplication.LoadImageFood;
import com.example.hp.myapplication.model.utils.StringUtils;

public class ImageSetting {
    private LoadImageFood loadImageFood;
    public ImageSetting(ImageView imageView) {
        loadImageFood = new LoadImageFood(imageView);
    }

    public void setPhoto(String maxWidth, String tag, boolean isImage){
        String url;
        if(!isImage){
            System.out.println("image url false");
            url = tag;
        }else{
            System.out.println("image url true");
            url = getUrlForSearchPlacePhoto(maxWidth,tag);
        }
       loadImageFood.execute(url);
    }

    private String getUrlForSearchPlacePhoto(String maxWidth, String photoReference){
        StringBuilder builder = new StringBuilder(StringUtils.PLACES_API_BASE);
        builder.append(StringUtils.TYPE_PLACE_PHOTO);
        builder.append(StringUtils.TYPE_PHOTO);
        builder.append("?maxwidth=").append(maxWidth);
        builder.append("&photoreference=").append(photoReference);
        builder.append("&key=").append(StringUtils.API_KEY);
        return builder.toString();
    }
}
