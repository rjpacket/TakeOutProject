package com.taovo.takeout.http;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Gimpo create on 2018/1/26 12:32
 * @email : jimbo922@163.com
 */

public interface RetrofitService {

    @POST("lotserver/api/request")
    Observable<String> request(@Query("body") String body);
}
