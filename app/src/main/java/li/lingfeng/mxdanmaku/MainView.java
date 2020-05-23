package li.lingfeng.mxdanmaku;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import li.lingfeng.mxdanmaku.bean.CommentBean.Comment;
import li.lingfeng.mxdanmaku.presenter.CommentBeanSource;
import li.lingfeng.mxdanmaku.presenter.DanmakuParser;
import li.lingfeng.mxdanmaku.util.Logger;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.SimpleTextCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class MainView extends RelativeLayout {

    private DanmakuView mDanmakuView;
    private TextView mStatusView;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;

    private int mSecondsToSeek = 0;
    private Handler mHandler;
    private Runnable mHideStatusRunnable = () -> {
        mStatusView.setVisibility(View.GONE);
    };
    private boolean mPauseAtStart = false;

    public MainView(Context context) {
        super(context);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.main_view, this);
        mDanmakuView = viewGroup.findViewById(R.id.danmaku_view);
        mStatusView = viewGroup.findViewById(R.id.status_view);
        mHandler = new Handler();
    }

    public void initDanmakuView(List<Comment> comments) {
        appendStatusLog(getResources().getString(R.string.main_init_danmaku, comments.size()));
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext
                .setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.3f)
                .setScaleTextSize(0.7f)
                .setCacheStuffer(new SimpleTextCacheStuffer(), null)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair)
                .setDanmakuMargin(10);
        if (mDanmakuView != null) {
            mParser = createParser(comments);
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    Logger.v("Start from " + mSecondsToSeek + "s.");
                    mDanmakuView.start(mSecondsToSeek * 1000L);
                    if (mPauseAtStart) {
                        mHandler.post(() -> {
                            mDanmakuView.pause();
                        });
                    }
                }
            });
            mDanmakuView.prepare(mParser, mDanmakuContext);
            //mDanmakuView.showFPS(true);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    private BaseDanmakuParser createParser(List<Comment> comments) {
        CommentBeanSource dataSource = new CommentBeanSource(comments);
        BaseDanmakuParser parser = new DanmakuParser();
        parser.load(dataSource);
        return parser;
    }

    public void seekTo(int seconds) {
        mSecondsToSeek = seconds;
        if (mDanmakuView.isPrepared() && Math.abs(mDanmakuView.getCurrentTime() - seconds * 1000L) > 2000L) {
            Logger.v("Seek once to " + seconds + "s");
            mDanmakuView.seekTo(seconds * 1000L);
        }
    }

    public boolean isPrepared() {
        return mDanmakuView.isPrepared();
    }

    public void resumeDanmaku() {
        mDanmakuView.resume();
        mDanmakuView.setVisibility(View.VISIBLE);
        mPauseAtStart = false;
    }

    public void pauseDanmaku() {
        pauseDanmaku(false);
    }

    public void pauseDanmaku(boolean hide) {
        mDanmakuView.pause();
        if (hide) {
            mDanmakuView.setVisibility(View.GONE);
        }
        mPauseAtStart = true;
    }

    public void stopDanmaku() {
        mDanmakuView.stop();
    }

    public void appendStatusLog(String msg) {
        Logger.i(msg);
        appendOnStatus(msg);
    }

    public void appendStatusError(String msg) {
        Logger.e(msg);
        appendOnStatus(msg);
    }

    private void appendOnStatus(String msg) {
        mStatusView.setText(mStatusView.getText().toString() + '\n' + msg);
        mHandler.removeCallbacks(mHideStatusRunnable);
        mStatusView.setVisibility(View.VISIBLE);
        mHandler.postDelayed(mHideStatusRunnable, 4000);
    }
}
