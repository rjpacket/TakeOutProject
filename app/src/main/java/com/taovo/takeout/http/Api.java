package com.taovo.takeout.http;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taovo.takeout.BuildConfig;
import com.taovo.takeout.util.AES;
import com.taovo.takeout.util.AppUtils;
import com.taovo.takeout.util.Base64;
import com.taovo.takeout.util.LL;
import com.taovo.takeout.util.MD5;
import com.taovo.takeout.util.ToolsAesCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Gimpo create on 2018/1/26 12:43
 * @email : jimbo922@163.com
 */

public class Api {
    public static final String BASE_URL = "https://test.dajiang365.com/";
    private static final String KEY = "B4F9CED935M9419D";
    private static Api mInstance;

    public static Api getInstance() {
        if (mInstance == null) {
            synchronized (Api.class) {
                if (mInstance == null) {
                    mInstance = new Api();
                }
            }
        }
        return mInstance;
    }

    public <T> void getObject(Context mContext, String command, JSONObject params, long timeOut, boolean showLoadingDialog, String loadingMessage, final GetObjectCallBack<T> callBack) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)       //设置连接超时
                .readTimeout(10000L, TimeUnit.MILLISECONDS)          //设置读取超时
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)         //设置写入超时
                .cache(new Cache(mContext.getCacheDir(), 10 * 1024 * 1024))   //设置缓存目录和10M缓存
                .addInterceptor(interceptor)    //添加日志拦截器（该方法也可以设置公共参数，头信息）
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        service.request(getEncryptParams(mContext, command, params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String result) {
                        try {
                            String decodeResponse = decodeResponse(result);
                            LL.json(decodeResponse);
                            Gson gson = new GsonBuilder().create();
                            BaseModel baseModel = gson.fromJson(decodeResponse, new TypeToken<BaseModel>() {
                            }.getType());
                            String errorcode = baseModel.getErrorcode();
                            if ("0".equals(errorcode)) {
                                Object data = baseModel.getData();
                                Class cl = getTypeClassOfInterfaceObject(callBack);
                                T t = gson.fromJson(gson.toJson(data), new Element<T>(cl));
                                if (t != null) {
                                    callBack.onSuccess(t);
                                } else {
                                    callBack.onFailure("请求失败");
                                }
                            } else {
                                callBack.onFailure(baseModel.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public <T> void getList(Context mContext, String command, JSONObject params, long timeOut, boolean showLoadingDialog, String loadingMessage, final GetListCallBack<T> callBack) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)       //设置连接超时
                .readTimeout(10000L, TimeUnit.MILLISECONDS)          //设置读取超时
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)         //设置写入超时
                .cache(new Cache(mContext.getCacheDir(), 10 * 1024 * 1024))   //设置缓存目录和10M缓存
                .addInterceptor(interceptor)    //添加日志拦截器（该方法也可以设置公共参数，头信息）
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        service.request(getEncryptParams(mContext, command, params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String result) {
                        try {
                            String decodeResponse = decodeResponse(result);
                            LL.json(decodeResponse);
                            Gson gson = new GsonBuilder().create();
                            BaseModel baseModel = gson.fromJson(decodeResponse, new TypeToken<BaseModel>() {
                            }.getType());
                            String errorcode = baseModel.getErrorcode();
                            if ("0".equals(errorcode)) {
                                Object data = baseModel.getData();
                                Class cl = getTypeClassOfInterfaceObject(callBack);
                                List<T> list = gson.fromJson(gson.toJson(data), new ListWithElements<T>(cl));
                                if (list != null) {
                                    callBack.onSuccess(list);
                                } else {
                                    callBack.onFailure("请求失败");
                                }
                            } else {
                                callBack.onFailure(baseModel.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private Class getTypeClassOfInterfaceObject(Object obj) {
        return (Class) ((ParameterizedType) obj.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    /**
     * 解密获取的数据
     */
    @Nullable
    private String decodeResponse(String response) throws Exception {
        String decrypt;
        if (response.contains("\"")) {
            decrypt = ToolsAesCrypt.Decrypt(response.substring(1, response.length() - 1), KEY);
        } else {
            decrypt = AES.decrypt(response);
        }
        byte[] base64d = Base64.decode(decrypt);
        byte[] decompress = AppUtils.decompress2(base64d);
        return new String(decompress, "utf-8");
    }

    /**
     * 获取加密的请求参数
     *
     * @param command
     * @param data
     * @return
     */
    private String getEncryptParams(Context context, String command, JSONObject data) {
        String encryptedParams = null;
        try {
            encryptedParams = ToolsAesCrypt.Encrypt(getUnEncryptParams(context, command, data).toString(), KEY);
        } catch (Exception e) {
            e.printStackTrace();
            LL.e(command, "getEncryptParams: --> encrypt params error!");
        }
        LL.e(command, "getEncryptParams: --> " + encryptedParams);
        return encryptedParams;
    }

    /**
     * 未加密的请求参数
     *
     * @param command
     * @param data
     * @return
     */
    private JSONObject getUnEncryptParams(Context context, String command, JSONObject data) {
        JSONObject params = new JSONObject();
        try {
            params.put(RequestParams.CLIENT_INFO, getClientInfoParam(context, command));
            params.put(RequestParams.COMMAND, command);
            params.put(RequestParams.DATA, data);
        } catch (JSONException e) {
            e.printStackTrace();
            LL.e("getUnEncryptParams: --> wrap request params error!");
        }

        LL.json(command, params.toString());
        return params;
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    private JSONObject getClientInfoParam(Context context, String command) {
        long timestamp = System.currentTimeMillis();
        JSONObject clientInfo = new JSONObject();
        try {
            clientInfo.put(RequestParams.IMEI, AppUtils.getIMEI(context));
            clientInfo.put(RequestParams.OSVERSION, Build.VERSION.RELEASE); // Build.VERSION.RELEASE
            clientInfo.put(RequestParams.MAC, AppUtils.getMobileMAC(context));
            clientInfo.put(RequestParams.JPUSH_ID, ""); //JPushInterface.getRegistrationID(App.getContext())
//            String clientid = PushManager.getInstance().getClientid(App.getContext());
            clientInfo.put(RequestParams.GEPUSH_ID, "");
            clientInfo.put(RequestParams.BRAND, Build.BRAND);
            clientInfo.put(RequestParams.MODEL, Build.MODEL);
            clientInfo.put(RequestParams.PLATFORM, "android");
            clientInfo.put(RequestParams.APP, "DaJiang365");
            clientInfo.put(RequestParams.APN, AppUtils.getNetworkType(context));
            clientInfo.put(RequestParams.VERSION, BuildConfig.VERSION_NAME);
            clientInfo.put(RequestParams.AGENT_ID, RequestParams.getChannel(context));
            clientInfo.put(RequestParams.AUTH_TOKEN, "");
            clientInfo.put(RequestParams.TIMESTAMP, timestamp);
            clientInfo.put(RequestParams.TOEKN, MD5.toMd5(MD5.toMd5((KEY + timestamp).getBytes()).getBytes()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LL.e(command, "getClientInfoParam: --> " + clientInfo.toString());
        return clientInfo;
    }

    /**
     * 回调接口
     */
    public interface GetObjectCallBack<T> {

        void onSuccess(T result);

        void onFailure(String errorMsg);
    }

    /**
     * 回调接口
     */
    public interface GetListCallBack<T> {

        void onSuccess(List<T> result);

        void onFailure(String errorMsg);
    }
}
