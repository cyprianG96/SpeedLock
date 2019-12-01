package com.mobica.speedlock;


import com.mobica.speedlock.model.Hit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {


//    query=text%3Abama&sortBy=_score&sortOrder=DESC&limit=15"

    @GET("search")
    Call<List<Hit>> getHits(@Query("query") String query, @Query("sortBy") String sortBy, @Query("sortOrder") String sortOrder, @Query("limit") String limit);

}
