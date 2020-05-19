package li.lingfeng.mxdanmaku.net;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DanDanRetrofitManager {

    private static DanDanRetrofitManager _instance;
    public static DanDanRetrofitManager instance() {
        if (_instance == null) {
            _instance = new DanDanRetrofitManager();
        }
        return _instance;
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://api.acplay.net/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build();
    }

    public DanDanRetrofitService getService() {
        Retrofit retrofit = getRetrofit();
        return retrofit.create(DanDanRetrofitService.class);
    }
}
