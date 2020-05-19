package li.lingfeng.globaldanmakudroid.contact;

import li.lingfeng.globaldanmakudroid.base.BasePresenter;
import li.lingfeng.globaldanmakudroid.base.IBaseView;
import li.lingfeng.globaldanmakudroid.base.IPresenter;
import li.lingfeng.globaldanmakudroid.bean.DanDanCommentBean;
import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;
import li.lingfeng.globaldanmakudroid.bean.DanDanSearchEpisodeBean;

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
