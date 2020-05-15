package li.lingfeng.globaldanmakudroid.contact;

import li.lingfeng.globaldanmakudroid.base.BasePresenter;
import li.lingfeng.globaldanmakudroid.base.IBaseView;
import li.lingfeng.globaldanmakudroid.base.IPresenter;
import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;

public interface ControlContact {

    interface View extends IBaseView {

        void onDanmakuMatched(DanDanMatchBean matchBean);
    }

    interface Presenter extends IPresenter<View> {

        void matchDanmaku(String fileName, String fileHash, int fileSize, int videoDuration);
    }
}
