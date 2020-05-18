package li.lingfeng.globaldanmakudroid.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import li.lingfeng.globaldanmakudroid.base.BasePresenter;
import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;
import li.lingfeng.globaldanmakudroid.bean.DanDanSearchEpisodeBean;
import li.lingfeng.globaldanmakudroid.contact.ControlContact;
import li.lingfeng.globaldanmakudroid.model.ControlModel;
import li.lingfeng.globaldanmakudroid.util.Logger;

public class ControlPresenter extends BasePresenter<ControlContact.View> implements ControlContact.Presenter {

    private ControlModel model = new ControlModel();

    @Override
    public void matchDanmaku(String fileName, String fileHash, int fileSize, int videoDuration) {
        Disposable disposable = model.matchDanmaku(fileName, fileHash, fileSize, videoDuration)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(matchBean -> {
                    Logger.d("Got matchBean " + matchBean);
                    Logger.d("thread " + Thread.currentThread());
                    mView.onDanmakuMatched(matchBean);
                }, err -> {
                    Logger.e("Error " + err);
                    DanDanMatchBean matchBean = new DanDanMatchBean();
                    matchBean.errorCode = -1;
                    matchBean.errorMessage = err.toString();
                    mView.onDanmakuMatched(matchBean);
                });
        addSubscription(disposable);
    }

    @Override
    public void searchEpisode(String anime) {
        Disposable disposable = model.searchEpisode(anime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchEpisodeBean -> {
                    Logger.d("Got searchEpisodeBean " + searchEpisodeBean);
                    mView.onEpisodeSearched(searchEpisodeBean);
                }, err -> {
                    Logger.e("Error " + err);
                    DanDanSearchEpisodeBean searchEpisodeBean = new DanDanSearchEpisodeBean();
                    searchEpisodeBean.errorCode = -1;
                    searchEpisodeBean.errorMessage = err.toString();
                    mView.onEpisodeSearched(searchEpisodeBean);
                });
        addSubscription(disposable);
    }
}
