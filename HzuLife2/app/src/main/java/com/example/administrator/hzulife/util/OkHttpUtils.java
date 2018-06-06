package com.example.administrator.hzulife.util;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * <pre>
 *     author : 蔡文豪
 *     e-mail : 1261654234@qq.com
 *     time   : 2018/5/1
 *     desc   : 网络连接
 *     version: 1.0
 * </pre>
 */
public class OkHttpUtils {
    private static  final String URL_PATH="http://111.230.49.54:80/MyWeb/LoginServlet";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static Call postDataAsync(Map<String,String> parameterMap) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        Set<Map.Entry<String,String>> set=parameterMap.entrySet();//获取映射的Set视图
        Iterator<Map.Entry<String,String>>iterator=set.iterator();//获取迭代器
        while (iterator.hasNext()){
            Map.Entry mapEntry=(Map.Entry)iterator.next();
            formBody.add((String)mapEntry.getKey(),(String)mapEntry.getValue());
        }
//        formBody.add("username","zhangsan");//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url(URL_PATH)
                .post(formBody.build())//传递请求体
                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
        return client.newCall(request);//返回请求对象
    }
    public static Call getDataAsync(String path) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if(response.isSuccessful()){//回调的方法执行在子线程。
//
//                }
//            }
//        });
        return client.newCall(request);
    }

    /**
     *Post方式混合传送文件和参数
     *
     */
    public static Call sendMultipart(Map<String,String> parameterMap,List<File> files,String pic_key){
        OkHttpClient client=new OkHttpClient();
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        //遍历map中所有参数
        if (parameterMap != null){
            for (String key : parameterMap.keySet()) {
                multipartBodyBuilder.addFormDataPart(key, parameterMap.get(key));
            }
        }
        //遍历所有文件
        if (files != null){
            for (File file : files) {//第一个参数为图片名字
                multipartBodyBuilder.addFormDataPart(pic_key, file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
            }
        }
        //构建请求体
        RequestBody multipartBody = multipartBodyBuilder.build();
        Request request=new Request.Builder().url(URL_PATH)
                .addHeader("User-Agent","android")
                .header("Content-Type","text/html; charset=utf-8;")
                .post(multipartBody)//传参数、文件或者混合
                .build();
        return client.newCall(request);
    }
}
