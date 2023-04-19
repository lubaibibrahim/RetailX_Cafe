package com.hadaba.retailxcafe.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadaba.retailxcafe.utils.AppPreference;
import com.hadaba.retailxcafe.utils.ObjectFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static AppPreference appPreference;
    public static String BASE_URL = "http://hadabaoffice.dyndns.tv:98/";//cant use this because base url differ .

    private static Retrofit retrofit = null;


    public static Retrofit getClient(Context context) {

        appPreference = ObjectFactory.getInstance(context).getAppPreference();
        String url=appPreference.getUrl();
        BASE_URL="http://"+url+"/";
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            try {
                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(600, TimeUnit.SECONDS)
                        .connectTimeout(600, TimeUnit.SECONDS)
                        .addInterceptor(loggingInterceptor)   //   view result  when run the project
                        .build();
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            } catch (Exception ignored) {

            }



        return retrofit;
    }
}
