package com.iyuba.http.http;

import android.util.Log;

import com.iyuba.http.BaseHttpRequest;
import com.iyuba.http.BaseHttpResponse;
import com.iyuba.http.ErrorResponse;
import com.iyuba.http.IErrorReceiver;
import com.iyuba.http.INetStateReceiver;
import com.iyuba.http.IResponseReceiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHandler {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private int rspCookie = -1;
    private BaseHttpRequest request;
    private IResponseReceiver rspReceiver;
    private IErrorReceiver errorReceiver;
    private INetStateReceiver stateReceiver;

    public HttpHandler(int rspCookie, BaseHttpRequest request, IResponseReceiver rspReceiver,
                       IErrorReceiver errReceiver, INetStateReceiver stateReceiver) {
        this.rspCookie = rspCookie;
        this.request = request;
        this.rspReceiver = rspReceiver;
        this.errorReceiver = errReceiver;
        this.stateReceiver = stateReceiver;
    }

    public void request() {
        String url = request.getAbsoluteURI();
        Log.e("http handler url :: ", url);

        Request okhttpRequest;
        switch (request.getMethod()) {
            case BaseHttpRequest.Method.GET:
                okhttpRequest = new Request.Builder()
                        .url(url)
                        .build();
                break;
            case BaseHttpRequest.Method.POST:
                okhttpRequest = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(Http.MEDIA_TYPE_TEXT, ""))
                        .build();

                break;
            default:
                okhttpRequest = new Request.Builder()
                        .url(url)
                        .build();
                break;
        }

        Http.getOkHttpClient().newCall(okhttpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ErrorResponse err = new ErrorResponse(e.getMessage());
                if (errorReceiver != null) {
                    errorReceiver.onError(err, request, rspCookie);
                } else if (stateReceiver != null) {
                    stateReceiver.onNetError(request, rspCookie, err);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ErrorResponse err = null;
                if (response.isSuccessful()) {
                    try {
                        BaseHttpResponse httpResponse = request.createResponse();
                        httpResponse.setResponseHeaders(flatHeaders(response.headers()));
                        httpResponse.parseInputStream(response.body().byteStream());
                        response.body().close();
                        if (rspReceiver != null) {
                            rspReceiver.onResponse(httpResponse, request, rspCookie);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        err = new ErrorResponse(ErrorResponse.ERROR_NET_IO);
                    }
                } else {
                    err = new ErrorResponse(response.message());
                }

                if (err != null && errorReceiver != null) {
                    errorReceiver.onError(err, request, rspCookie);
                } else if (err != null && stateReceiver != null) {
                    stateReceiver.onNetError(request, rspCookie, err);
                }
            }
        });
    }

    private Map<String, String> flatHeaders(Headers headers) {
        Map<String, String> map = new HashMap<>();
        Iterator<String> iterator = headers.names().iterator();
        if (iterator.hasNext()) {
            String key = iterator.next();
            String value = headers.get(key);
            map.put(key, value);
        }
        return map;
    }
}
