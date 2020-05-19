package li.lingfeng.mxdanmaku.contact;

import li.lingfeng.mxdanmaku.base.IBaseView;
import li.lingfeng.mxdanmaku.base.IPresenter;
import li.lingfeng.mxdanmaku.bean.DanDanCommentBean;
import li.lingfeng.mxdanmaku.bean.DanDanMatchBean;
import li.lingfeng.mxdanmaku.bean.DanDanSearchEpisodeBean;

public interface ControlContact {

    interface View extends IBaseView {

        void onDanmakuMatched(DanDanMatchBean matchBean);
        void onEpisodeSearched(DanDanSearchEpisodeBean searchEpisodeBean);
        void onCommentsGot(DanDanCommentBean commentBean);
    }

    interface Presenter extends IPresenter<View> {

        void matchDanmaku(String fileName, String fileHash, int fileSize, int videoDuration);
        void searchEpisode(String anime);
        void getComments(int episodeId);
    }
}
