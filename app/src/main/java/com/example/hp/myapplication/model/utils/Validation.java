package com.example.hp.myapplication.model.utils;

import java.util.List;

public class Validation {

    public static boolean isStringEmpty(String s){
        if(s == null || s.trim().equals("")){
            return true;
        }
        return false;
    }

    public static boolean isListEmpty(List<?> list){
        if(list == null || list.isEmpty()){
            return true;
        }
        return false;
    }
}
