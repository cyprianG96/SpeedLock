package com.mobica.speedlock.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class HitsList {

    @SerializedName("hits")
    private List<Hit> hits = new ArrayList<>();

    public List<Hit> getHits() {
        return hits;
    }

}
