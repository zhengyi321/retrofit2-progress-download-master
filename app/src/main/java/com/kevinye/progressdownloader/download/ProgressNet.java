package com.kevinye.progressdownloader.download;

import com.kevinye.progressdownloader.download.ProgressResponseBody.ProgressListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 *
 * Created by Kevin on 2016/6/19.
 */
public class ProgressNet {
    private static final String BASE_URL = "http://gdown.baidu.com/";
    private static final String WE_CHART_URL = "data/wisegame/8d5889f722f640c8/weixin_800.apk";

    public interface ApiService {
        @Streaming
        @GET(WE_CHART_URL)
        Observable<ResponseBody> getWeChartAPK();
    }

    public static ApiService getApiService(final ProgressListener progressListener){

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build()
                .create(ApiService.class);
    }

}
