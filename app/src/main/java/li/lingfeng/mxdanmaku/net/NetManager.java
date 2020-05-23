package li.lingfeng.mxdanmaku.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetManager {

    private static NetManager _instance;
    public static NetManager instance() {
        if (_instance == null) {
            _instance = new NetManager();
        }
        return _instance;
    }

    private OkHttpClient mHttpClient;

    public RetrofitService getRetrofitService() {
        Retrofit retrofit = getRetrofit();
        return retrofit.create(RetrofitService.class);
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
