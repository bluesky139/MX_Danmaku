package li.lingfeng.globaldanmakudroid.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<V extends IBaseView> implements IPresenter<V> {

    protected V mView;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        if (!mDisposables.isDisposed()) {
            mDisposables.clear();
        }
    }

    public void addSubscription(Disposable disposable) {
        mDisposables.add(disposable);
    }
}
