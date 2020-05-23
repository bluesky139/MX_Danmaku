package li.lingfeng.mxdanmaku.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static RetrofitManager _instance;
    public static RetrofitManager instance() {
        if (_instance == null) {
            _instance = new RetrofitManager();
        }
        return _instance;
    }

    private OkHttpClient mHttpClient;

    public DanDanRetrofitService getService() {
        Retrofit retrofit = getRetrofit();
        return retrofit.create(DanDanRetrofitService.class);
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(getHttpClient())
                .baseUrl("http://api.acplay.net/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build();
    }

    public OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient();
        }
        return mHttpClient;
    }
}
