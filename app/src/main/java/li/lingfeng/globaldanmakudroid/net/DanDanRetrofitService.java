package li.lingfeng.globaldanmakudroid.net;

import io.reactivex.Observable;
import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DanDanRetrofitService {

    @POST("match")
    @FormUrlEncoded
    Observable<DanDanMatchBean> match(@Field("fileName") String fileName, @Field("fileHash") String fileHash,
                                      @Field("fileSize") int fileSize, @Field("videoDuration") int videoDuration,
                                      @Field("matchMode") String matchMode);
}
