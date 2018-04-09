package com.master.rxlib.Rx.Net;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by ck on 2017/7/31.
 */

public class RetrofitHttpManger {
    private static List<String> progrssUrls = new ArrayList<>();
    private static Map<Object, OkHttpClient> httpClients = new HashMap<>();
    private Retrofit mRetrofit;

    private RetrofitHttpManger() {
    }

    public RetrofitHttpManger addDownloadUrlListener(String url) {
        if (!progrssUrls.contains(url))
            progrssUrls.add(url);
        return this;
    }

    public OkHttpClient getClient(RetrofitHttpManger manger) {
        return httpClients.get(manger);
    }

    public RetrofitHttpManger removeDownloadListener(String url) {
        if (progrssUrls.contains(url)) {
            progrssUrls.remove(url);
        }
        return this;
    }

    public Retrofit get() {
        return mRetrofit;
    }

    public static class Builder {
        private int connectOut = 12;
        private int readOut = 12;
        private String baseUrl;
        private Retrofit mRetrofit;
        private boolean showlog = true;
        OkHttpClient httpclient;
        private Map<String, String> headers = new LinkedHashMap<>();
        private HttpsCerUtil.SSLParams sslParams;

        public Builder setTimeout(int connectOut, int readOut) {
            this.connectOut = connectOut;
            this.readOut = readOut;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setShowlog(boolean showlog) {
            this.showlog = showlog;
            return this;
        }

        public Builder addHeaders(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder addCert(InputStream[] certificates, InputStream bksFile, String password) {
            sslParams = HttpsCerUtil.getSslSocketFactory(certificates, bksFile, password);
            return this;
        }

        public Builder setOkhttpClient(OkHttpClient httpclient) {
            this.httpclient = httpclient;
            return this;
        }

        public RetrofitHttpManger Builder() {
            if (httpclient == null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                if (showlog) {
                    builder.addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            Log.i("HttpLoggingInterceptor", message);
                        }
                    }).setLevel(HttpLoggingInterceptor.Level.BODY));
                }
                builder.connectTimeout(connectOut, TimeUnit.SECONDS)
                        .readTimeout(readOut, TimeUnit.SECONDS)
                        // 添加公共参数拦截器
                        // 添加通用的Header
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request.Builder builder = chain.request().newBuilder();
                                for (Map.Entry<String, String> entry : headers.entrySet()) {
                                    builder.addHeader(entry.getKey(), entry.getValue());
                                }

                                return chain.proceed(builder.build());
                            }
                        })
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                String url = originalResponse.request().url().url().toString();
                                if (progrssUrls.contains(url)) {
                                    return originalResponse.newBuilder()
                                            .body(new ProgressDownloadBody(originalResponse.body(), url))
                                            .build();
                                } else {
                                    return originalResponse;
                                }
                            }
                        });
                if (sslParams != null) {
                    builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
                }
                httpclient = builder.build();
            }
            RetrofitHttpManger retrofitHttpManger = new RetrofitHttpManger();
            mRetrofit = new Retrofit.Builder()
                    .client(httpclient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(baseUrl)
                    .build();
            retrofitHttpManger.httpClients.put(retrofitHttpManger,httpclient);
            retrofitHttpManger.mRetrofit = mRetrofit;
            return retrofitHttpManger;
        }
    }
}

