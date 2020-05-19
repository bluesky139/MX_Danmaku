package li.lingfeng.mxdanmaku.model;

import io.reactivex.Observable;
import li.lingfeng.mxdanmaku.bean.DanDanCommentBean;
import li.lingfeng.mxdanmaku.bean.DanDanMatchBean;
import li.lingfeng.mxdanmaku.bean.DanDanSearchEpisodeBean;
import li.lingfeng.mxdanmaku.net.DanDanRetrofitManager;

public class ControlModel {

    public Observable<DanDanMatchBean> matchDanmaku(String fileName, String fileHash, int fileSize, int videoDuration) {
        return DanDanRetrofitManager.instance().getService().match(fileName, fileHash, fileSize,
                videoDuration, "hashAndFileName");
    }

    public Observable<DanDanSearchEpisodeBean> searchEpisode(String anime) {
        return DanDanRetrofitManager.instance().getService().searchEpisode(anime);
    }

    public Observable<DanDanCommentBean> getComments(int episodeId) {
        return DanDanRetrofitManager.instance().getService().getComments(episodeId, true);
    }
}