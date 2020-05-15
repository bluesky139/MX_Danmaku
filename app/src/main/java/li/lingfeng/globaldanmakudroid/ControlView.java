package li.lingfeng.globaldanmakudroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.reactivex.Observable;
import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;
import li.lingfeng.globaldanmakudroid.contact.ControlContact;
import li.lingfeng.globaldanmakudroid.net.DanDanRetrofitService;
import li.lingfeng.globaldanmakudroid.presenter.ControlPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ControlView extends RelativeLayout implements ControlContact.View {

    private TextView mStatusView;
    private MainView mMainView;
    private ControlPresenter mPresenter = new ControlPresenter();

    public ControlView(Context context, MainView mainView) {
        super(context);
        mMainView = mainView;
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.control_view, this);
        mStatusView = viewGroup.findViewById(R.id.status_view);
        mPresenter.attachView(this);
        mStatusView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("real click");
                mMainView.initDanmakuView();
                mPresenter.matchDanmaku("みなみけ ただいま (Creditless OP) BDrip x264-ank [XVID 720p]",
                        "658d05841b9476ccc7420b3f0bb21c3b", 24238942, 92);
            }
        });
    }

    @Override
    public void onDanmakuMatched(DanDanMatchBean matchBean) {
        Logger.d("onDanmakuMatched " + matchBean);
    }
}
