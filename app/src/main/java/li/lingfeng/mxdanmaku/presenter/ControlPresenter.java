package li.lingfeng.mxdanmaku.presenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import li.lingfeng.mxdanmaku.base.BasePresenter;
import li.lingfeng.mxdanmaku.bean.CommentBean;
import li.lingfeng.mxdanmaku.bean.MatchBean;
import li.lingfeng.mxdanmaku.bean.SearchEpisodeBean;
import li.lingfeng.mxdanmaku.contact.ControlContact;
import li.lingfeng.mxdanmaku.model.ControlModel;
import li.lingfeng.mxdanmaku.util.Logger;

public class ControlPresenter extends BasePresenter<ControlContact.View> implements ControlContact.Presenter {

    private ControlModel model = new ControlModel();

    @Override
    public void matchDanmaku(String fileName, String fileHash, long fileSize, int videoDuration) {
        Disposable disposable = model.matchDanmaku(fileName, fileHash, fileSize, videoDuration)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(matchBean -> {
                    Logger.d("Got matchBean " + matchBean);
                    mView.onDanmakuMatched(matchBean);
                }, err -> {
                    Logger.e("Error " + err);
                    MatchBean matchBean = new MatchBean();
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
                    SearchEpisodeBean searchEpisodeBean = new SearchEpisodeBean();
                    searchEpisodeBean.errorCode = -1;
                    searchEpisodeBean.errorMessage = err.toString();
                    mView.onEpisodeSearched(searchEpisodeBean);
                });
        addSubscription(disposable);
    }

    @Override
    public void getComments(int episodeId) {
        Disposable disposable = model.getComments(episodeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commentBean -> {
                    Logger.d("Got commentBean " + commentBean);
                    mView.onCommentsGot(commentBean);
                }, err -> {
                    Logger.e("Error " + err);
                    CommentBean commentBean = new CommentBean();
                    commentBean.errorCode = -1;
                    commentBean.errorMessage = err.toString();
                    mView.onCommentsGot(commentBean);
                });
        addSubscription(disposable);
    }
}
