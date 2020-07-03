package li.lingfeng.mxdanmaku.model;

import io.reactivex.Observable;
import li.lingfeng.mxdanmaku.bean.CommentBean;
import li.lingfeng.mxdanmaku.bean.MatchBean;
import li.lingfeng.mxdanmaku.bean.SearchEpisodeBean;
import li.lingfeng.mxdanmaku.net.NetManager;

public class ControlModel {

    public Observable<MatchBean> matchDanmaku(String fileName, String fileHash, long fileSize, int videoDuration) {
        return NetManager.instance().getRetrofitService().match(fileName, fileHash, fileSize,
                videoDuration, "hashAndFileName");
    }

    public Observable<SearchEpisodeBean> searchEpisode(String anime) {
        return NetManager.instance().getRetrofitService().searchEpisode(anime);
    }

    public Observable<CommentBean> getComments(int episodeId) {
        return NetManager.instance().getRetrofitService().getComments(episodeId, true);
    }
}
