package com.iyuba.core.common.retrofitapi.result;


import com.iyuba.core.common.retrofitapi.ApiApi;
import com.iyuba.core.common.retrofitapi.HostUpdateApi;
import com.iyuba.core.common.retrofitapi.OtherApi;
import com.iyuba.core.common.retrofitapi.QQSupportApi;
import com.iyuba.core.common.retrofitapi.YzPhoneNumber;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 作者：renzhy on 16/6/22 16:45
 * 邮箱：renzhongyigoo@gmail.com
 */
public class ApiRequestFactory {


	private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
	private static OkHttpClient okHttpClient = new OkHttpClient();
	private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
	private static YzPhoneNumber yzPhoneNumber;
	//域名更新
	private static HostUpdateApi hostUpdateApi;
	//qq客服支持
	private static QQSupportApi qqSupportApi;
	//其他补充的api
	private static OtherApi otherApi;

	private static ApiApi apiApi;

	private static void initOkHttpClient(){
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.addInterceptor(interceptor)
				.build();
	}
	public static YzPhoneNumber getYzPhoneNumber(){
		if(yzPhoneNumber == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(YzPhoneNumber.YZNUMBER_URL)
					.addConverterFactory(gsonConverterFactory)
					.build();

			yzPhoneNumber = retrofit.create(YzPhoneNumber.class);
		}
		return yzPhoneNumber;
	}

	//获取动态域名
	public static HostUpdateApi getHostUpdate(){
		if (hostUpdateApi == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(HostUpdateApi.BASE_URL)
					.addConverterFactory(gsonConverterFactory)
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.build();
			hostUpdateApi = retrofit.create(HostUpdateApi.class);
		}
		return hostUpdateApi;
	}

	//获取qq客服支持
	public static QQSupportApi getQQSupport(){
		if (qqSupportApi == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(QQSupportApi.BASE_URL)
					.addConverterFactory(gsonConverterFactory)
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.build();
			qqSupportApi = retrofit.create(QQSupportApi.class);
		}
		return qqSupportApi;
	}

	public static ApiApi getApiApi(){
		if (apiApi == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(ApiApi.BASE_URL)
					.addConverterFactory(gsonConverterFactory)
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.build();
			apiApi = retrofit.create(ApiApi.class);
		}
		return apiApi;
	}

	//获取其他的补充api
	public static OtherApi getOtherApi(){
		if (otherApi == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(OtherApi.BASE_URL)
					.addConverterFactory(gsonConverterFactory)
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.build();
			otherApi = retrofit.create(OtherApi.class);
		}
		return otherApi;
	}
}
