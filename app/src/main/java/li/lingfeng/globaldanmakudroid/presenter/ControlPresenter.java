package li.lingfeng.globaldanmakudroid.presenter;

import io.reactivex.disposables.Disposable;
import li.lingfeng.globaldanmakudroid.Logger;
import li.lingfeng.globaldanmakudroid.base.BasePresenter;
import li.lingfeng.globaldanmakudroid.base.IBaseView;
import li.lingfeng.globaldanmakudroid.contact.ControlContact;
import li.lingfeng.globaldanmakudroid.contact.MainContact;
import li.lingfeng.globaldanmakudroid.model.ControlModel;

public class ControlPresenter extends BasePresenter<ControlContact.View> implements ControlContact.Presenter {

    private ControlModel model = new ControlModel();

    @Override
    public void matchDanmaku(String fileName, String fileHash, int fileSize, int videoDuration) {
        Disposable disposable = model.matchDanmaku(fileName, fileHash, fileSize, videoDuration)
                .subscribe(matchBean -> {
                    Logger.d("got matchBean " + matchBean);
                    Logger.d("thread " + Thread.currentThread());
                    mView.onDanmakuMatched(matchBean);
                }, err -> {
                    Logger.e("error " + err);
                });
        addSubscription(disposable);
    }
}
