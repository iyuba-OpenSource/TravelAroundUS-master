package com.iyuba.concept2.api;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitFactory {


	private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
	private static OkHttpClient okHttpClient = new OkHttpClient();
	private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
	//域名更新
	private static SpeechApi speechApi;
	private static FamilyUsaApi familyUsaApi;

	private static void initOkHttpClient(){
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.addInterceptor(interceptor)
				.build();
	}

	//获取动态域名
	public static SpeechApi getSpeechApi(){
		if (speechApi == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(SpeechApi.BASE_URL)
					.addConverterFactory(gsonConverterFactory)
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.build();
			speechApi = retrofit.create(SpeechApi.class);
		}
		return speechApi;
	}

	//获取动态域名
	public static FamilyUsaApi getAppsApi(){
		if (familyUsaApi == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(FamilyUsaApi.BASE_URL)
					.addConverterFactory(gsonConverterFactory)
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.build();
			familyUsaApi = retrofit.create(FamilyUsaApi.class);
		}
		return familyUsaApi;
	}


}
