package com.example.hp.myapplication.model;

import android.location.Location;

import java.io.Serializable;

public class MyLocation extends Location implements Serializable {
    public MyLocation(String provider) {
        super(provider);
    }
}
