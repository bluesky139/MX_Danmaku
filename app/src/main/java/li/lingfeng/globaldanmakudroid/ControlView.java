package li.lingfeng.globaldanmakudroid;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;
import li.lingfeng.globaldanmakudroid.contact.ControlContact;
import li.lingfeng.globaldanmakudroid.presenter.ControlPresenter;
import li.lingfeng.globaldanmakudroid.util.HashUtils;
import li.lingfeng.globaldanmakudroid.util.Logger;

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
                String hash = HashUtils.hashFileHead(context, Uri.parse("file:///sdcard/みなみけ ただいま (Creditless OP) BDrip x264-ank [XV"), 16 * 1024 * 1024);
                Logger.d("hash " + hash);
                //mMainView.initDanmakuView();
                //mPresenter.matchDanmaku("みなみけ ただいま (Creditless OP) BDrip x264-ank [XVID 720p]",
                //        "658d05841b9476ccc7420b3f0bb21c3b", 24238942, 92);
            }
        });
    }

    @Override
    public void onDanmakuMatched(DanDanMatchBean matchBean) {
        Logger.d("onDanmakuMatched " + matchBean);
    }
}
