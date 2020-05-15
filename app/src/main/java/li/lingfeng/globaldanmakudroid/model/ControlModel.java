package li.lingfeng.globaldanmakudroid.model;

import io.reactivex.Observable;
import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;
import li.lingfeng.globaldanmakudroid.net.DanDanRetrofitManager;

public class ControlModel {

    public Observable<DanDanMatchBean> matchDanmaku(String fileName, String fileHash, int fileSize, int videoDuration) {
        return DanDanRetrofitManager.instance().getService().match(fileName, fileHash, fileSize,
                videoDuration, "hashAndFileName");
    }
}
