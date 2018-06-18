package com.ali.okhttpdemo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by mumu on 2018/6/14.
 */

public class OkhtttpUtils {
    private static OkhtttpUtils mOkhtttpUtils;
    private OkHttpClient mOkHttpClien;
    private final Handler mHandler;


    private OkhtttpUtils() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //创建一个主线程的handler
        mHandler = new Handler(Looper.getMainLooper());
        mOkHttpClien = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }


    public static OkhtttpUtils getInstance() {
        if (mOkhtttpUtils == null) {
            synchronized (OkhtttpUtils.class) {
                if (mOkhtttpUtils == null) {
                    return mOkhtttpUtils = new OkhtttpUtils();
                }
            }
        }
        return mOkhtttpUtils;
    }

    public void doGet(String url, final OkCallback okCallback) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        final Call call = mOkHttpClien.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (okCallback != null) {

                    //切换到主线程
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            okCallback.onFailure(e);
                        }
                    });

                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try {
                    if (response != null && response.isSuccessful()) {
                        final String json = response.body().string();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (okCallback != null) {
                                    okCallback.onResponse(json);
                                    return;
                                }

                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void doPost(String url, Map<String, String> map, final OkCallback okCallback) {

        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            for (String key : map.keySet()) {
                builder.add(key, map.get(key));
            }
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        final Call call = mOkHttpClien.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (okCallback != null) {

                    //切换到主线程
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            okCallback.onFailure(e);
                        }
                    });

                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try {
                    if (response != null && response.isSuccessful()) {
                        final String json = response.body().string();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (okCallback != null) {
                                    okCallback.onResponse(json);
                                    return;
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (okCallback != null) {
                    okCallback.onFailure(new Exception("网络异常"));
                }

            }
        });
    }

    public interface OkCallback {
        void onFailure(Exception e);

        void onResponse(String json);
    }

    //封装了GET POST请求公共参数拦截器
    // 考虑到了get请求之前是否有参数的情况，也考虑到了GET请求参数拼接重复性问题
    public class PublicParamInterceptor implements Interceptor {

        //公共请求参数
        private HashMap<String, String> publicParam = new HashMap<>();

        public PublicParamInterceptor(HashMap<String, String> publicParam) {
            this.publicParam = publicParam;
        }

        //设置公共请求参数
        public void setPublicParam(HashMap hashMap) {
            publicParam = hashMap;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String url = request.url().toString();

            if (request.method().equalsIgnoreCase("GET")) {
                //只有需要拼接公共请求参数的时候才去操作
                if (publicParam != null && publicParam.size() > 0) {
                    for (HashMap.Entry<String, String> entry : publicParam.entrySet()) {
                        //不会重复拼接相同的key value
                        if (!url.contains(entry.getKey())) {
                            url += "&" + entry.getKey() + "=" + entry.getValue();
                        }
                    }
                    //考虑到了之前的url是否有无参数
                    if (!url.contains("?")) {
                        url = url.replaceFirst("&", "?");
                    }
                    Log.e("tag", "url = = " + url);
                    Request newRequest = request.newBuilder().url(url).build();
                    return chain.proceed(newRequest);
                }
            } else {
                if (publicParam != null && publicParam.size() > 0) {
                    //只有是表单形式才去处理
                    if (request.body() != null && request.body() instanceof FormBody) {
                        RequestBody body = request.body();
                        FormBody.Builder builder = new FormBody.Builder();
                        FormBody formBody = (FormBody) body;
                        //拼接原有的key和value
                        for (int i = 0; i < formBody.size(); i++) {
                            builder.add(formBody.encodedName(i), formBody.encodedValue(i));
                        }
                        //拼接公共的Key和value
                        for (HashMap.Entry<String, String> entry : publicParam.entrySet()) {
                            builder.add(entry.getKey(), entry.getValue());
                        }
                        FormBody newFormBody = builder.build();
                        Log.e("tag", "url = = " + url);
                        Request newRequest = request.newBuilder().post(newFormBody).build();
                        Response proceed = chain.proceed(newRequest);
                        return proceed;
                    }
                }
            }
            Log.e("tag", "url = = " + url);

            return chain.proceed(request);
        }
    }
}


