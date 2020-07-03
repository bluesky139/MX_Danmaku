package li.lingfeng.mxdanmaku.contact;

import li.lingfeng.mxdanmaku.base.IBaseView;
import li.lingfeng.mxdanmaku.base.IPresenter;
import li.lingfeng.mxdanmaku.bean.CommentBean;
import li.lingfeng.mxdanmaku.bean.MatchBean;
import li.lingfeng.mxdanmaku.bean.SearchEpisodeBean;

public interface ControlContact {

    interface View extends IBaseView {

        void onDanmakuMatched(MatchBean matchBean);
        void onEpisodeSearched(SearchEpisodeBean searchEpisodeBean);
        void onCommentsGot(CommentBean commentBean);
    }

    interface Presenter extends IPresenter<View> {

        void matchDanmaku(String fileName, String fileHash, long fileSize, int videoDuration);
        void searchEpisode(String anime);
        void getComments(int episodeId);
    }
}
