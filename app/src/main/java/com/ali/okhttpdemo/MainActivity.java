package com.ali.okhttpdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
    }


//    //同步的get请求
//    public void login(View view) {
//
//        String url = "https://www.zhaoapi.cn/user/login?mobile=12345678901&password=123456";
//        Request request = new Request.Builder()
//                .get()
//                .url(url)
//                .build();
//
//        final Call call = okHttpClient.newCall(request);
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Response response = call.execute();
//                    if (response != null && response.isSuccessful()) {
//                        String json = response.body().string();
//                        LoginBean loginBean = new Gson().fromJson(json, LoginBean.class);
//                        if ("0".equalsIgnoreCase(loginBean.getCode())) {
//                            Log.e("tag", "成功了 " + loginBean.getMsg());
//                        } else {
//                            Log.e("tag", "失败了 " + loginBean.getMsg());
//
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    //异步的get请求
//    public void login(View view) {
//
//        String url = "https://www.zhaoapi.cn/user/login?mobile=12345678901&password=123456";
//        Request request = new Request.Builder()
//                .get()
//                .url(url)
//                .build();
//
//        final Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    if (response != null && response.isSuccessful()) {
//                        String json = response.body().string();
//                        LoginBean loginBean = new Gson().fromJson(json, LoginBean.class);
//                        if ("0".equalsIgnoreCase(loginBean.getCode())) {
//                            Log.e("tag", "成功了 " + loginBean.getMsg());
//                        } else {
//                            Log.e("tag", "失败了 " + loginBean.getMsg());
//
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//    }

    //异步的Post请求
//    public void login(View view) {
//
//        String url = "https://www.zhaoapi.cn/user/login";
////        String url = "https://www.zhaoapi.cn/user/login?mobile=12345678901&password=123456";
//
//        FormBody formBody = new FormBody.Builder()
//                .add("mobile", "15501186623")
//                .add("password", "123456")
//                .build();
//
//        Request request = new Request.Builder()
//                .post(formBody)
//                .url(url)
//                .build();
//
//        final Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("tag", "失败了 ");
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    if (response != null && response.isSuccessful()) {
//                        String json = response.body().string();
//                        LoginBean loginBean = new Gson().fromJson(json, LoginBean.class);
//                        if ("0".equalsIgnoreCase(loginBean.getCode())) {
//                            Log.e("tag", "成功了 " + loginBean.getMsg());
//                            return;
//                        } else {
//                            Log.e("tag", "失败了 " + loginBean.getMsg());
//                            return;
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Log.e("tag", "失败了 ");
//            }
//        });
//
//
//    }

    public void login(View view) {
        String url = "https://www.zhaoapi.cn/user/login";
//        Map<String, String> map = new HashMap<>();
//        map.put("mobile", "15501186623");
//        map.put("password", "123456");
        url += "?";
        url += "mobile=15501186623&password=123456";

        OkhtttpUtils.getInstance().doGet(url,new OkhtttpUtils.OkCallback() {
            @Override
            public void onFailure(Exception e) {
                Log.e("tag", "失败了 ");
            }

            @Override
            public void onResponse(String json) {
                Toast.makeText(MainActivity.this, "chenggongle", Toast.LENGTH_SHORT).show();
                Log.e("tag", "成功了 " + json);
            }
        });
    }
}


//1.同步请求需要开启子线程去处理  2 异步不需要开启子线程，但是onResponse也是子线程