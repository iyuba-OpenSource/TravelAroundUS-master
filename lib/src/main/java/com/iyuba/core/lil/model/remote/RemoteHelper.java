package com.iyuba.core.lil.model.remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RemoteHelper {

    private static RemoteHelper instance;

    public static RemoteHelper getInstance(){
        if (instance==null){
            synchronized (RemoteHelper.class){
                if (instance==null){
                    instance = new RemoteHelper();
                }
            }
        }
        return instance;
    }

    public <T>T createJson(Class<T> clz){
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl("http://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(clz);
    }

    public <T>T createXml(Class<T> clz){
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl("http://www.baidu.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(clz);
    }

    private OkHttpClient getClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();
        return client;
    }
}
