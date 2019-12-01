package com.mobica.speedlock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://api.newsriver.io/v2/";

    private static Retrofit mClient = null;

    private ApiClient() {
    }

    public static synchronized Retrofit getApiClient() {

        if (mClient == null) {
            HttpLoggingInterceptor mLogInterceptor = new HttpLoggingInterceptor();
            mLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(mLogInterceptor)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(new TestInterceptor())
                    .build();

            mClient = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return mClient;
    }

    private static class TestInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request originalRequest = chain.request();

            Request newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "sBBqsGXiYgF0Db5OV5tAwy6q6ig_7ago04QDTj6eqksk_mZXCgoN59maCqa_TRDAn2pHZrSf1gT2PUujH1YaQA")
                    .build();

            return chain.proceed(newRequest);
        }
    }
}
