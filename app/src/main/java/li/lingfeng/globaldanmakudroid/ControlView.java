package li.lingfeng.globaldanmakudroid;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;

import li.lingfeng.globaldanmakudroid.bean.DanDanMatchBean;
import li.lingfeng.globaldanmakudroid.bean.DanDanSearchEpisodeBean;
import li.lingfeng.globaldanmakudroid.bean.DanDanSearchEpisodeBean.Anime;
import li.lingfeng.globaldanmakudroid.bean.DanDanSearchEpisodeBean.Episode;
import li.lingfeng.globaldanmakudroid.contact.ControlContact;
import li.lingfeng.globaldanmakudroid.presenter.ControlPresenter;
import li.lingfeng.globaldanmakudroid.util.HashUtils;
import li.lingfeng.globaldanmakudroid.util.Logger;
import li.lingfeng.globaldanmakudroid.util.ToastUtils;
import li.lingfeng.globaldanmakudroid.util.Utils;

public class ControlView extends RelativeLayout implements ControlContact.View {

    private static final int STATE_DANMAKU_HIDDEN    = 0;
    private static final int STATE_PREPARE_FILE_INFO = 1;
    private static final int STATE_DANMAKU_MATCHING  = 2;
    private static final SparseArray<String> sStateStrings = Utils.clsIntFieldsToStrings(ControlView.class, "STATE_");

    private ImageButton mShowHideButton;
    private boolean mDanmakuShown = false;
    //private TextView mStatusView;
    private MainView mMainView;
    private ControlPresenter mPresenter = new ControlPresenter();
    private ViewGroup mTitleChooseView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logger.d("Change state to " + sStateStrings.get(msg.what));
            switch (msg.what) {
                case STATE_DANMAKU_HIDDEN:
                    break;
                case STATE_PREPARE_FILE_INFO:
                    prepareFileInfo();
                    break;
            }
        }
    };

    private void setState(int state) {
        mHandler.sendEmptyMessage(state);
    }

    public ControlView(Context context, MainView mainView) {
        super(context);
        mMainView = mainView;
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.control_view, this);
        mShowHideButton = viewGroup.findViewById(R.id.show_hide_button);
        mShowHideButton.setOnClickListener(v -> {
            if (!mDanmakuShown) {
                mDanmakuShown = true;
                mShowHideButton.setImageResource(R.drawable.danmaku_shown_button);
                mMainView.initDanmakuView();
                setState(STATE_PREPARE_FILE_INFO);
            } else {
                mDanmakuShown = false;
                mShowHideButton.setImageResource(R.drawable.danmaku_hidden_button);
            }
        });



        //mStatusView = viewGroup.findViewById(R.id.status_view);
        mPresenter.attachView(this);
        /*mStatusView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("real click");
                String hash = HashUtils.hashFileHead(context, Uri.parse("file:///sdcard/みなみけ ただいま (Creditless OP) BDrip x264-ank [XV"), 16 * 1024 * 1024);
                Logger.d("hash " + hash);
                //
                //mPresenter.matchDanmaku("みなみけ ただいま (Creditless OP) BDrip x264-ank [XVID 720p]",
                //        "658d05841b9476ccc7420b3f0bb21c3b", 24238942, 92);
            }
        });*/
    }

    private void prepareFileInfo() {
        //Uri uri = Uri.parse("http://172.18.0.4/sv_download/%e3%81%bf%e3%81%aa%e3%81%bf%e3%81%91%20%e3%81%9f%e3%81%a0%e3%81%84%e3%81%be%20(Creditless%20OP)%20BDrip%20x264-ank%20%5bXVID%20720p%5d.avi");
        Uri uri = Uri.parse("file:///sdcard/みなみけ ただいま (Creditless OP) BDrip x264-ank [XV");
        int videoDuration = 92;
        String _fileName = uri.getLastPathSegment();
        _fileName = FilenameUtils.removeExtension(_fileName);
        if (NumberUtils.isParsable(_fileName)) {
            List<String> segments = uri.getPathSegments();
            _fileName = segments.get(segments.size() - 2) + ' ' + _fileName;
        }
        String fileName = _fileName;
        mMainView.appendStatusLog("FileName: " + fileName);
        mMainView.appendStatusLog("VideoDuration: " + videoDuration);

        HashUtils.hashFileHeadAsync(getContext(), uri, 16 * 1024 * 1024, (hash, fileSize) -> {
            if (hash == null) {
                mMainView.appendStatusError("Error to get file hash.");
                setState(STATE_DANMAKU_HIDDEN);
            } else {
                hash = "658d05841b9476ccc7420b3f0bb21c3c"; // for test
                mMainView.appendStatusLog("FileHash: " + hash);
                mMainView.appendStatusLog("FileSize: " + fileSize);
                mPresenter.matchDanmaku(fileName, hash, fileSize, videoDuration);
                setState(STATE_DANMAKU_MATCHING);
            }
        });
    }

    @Override
    public void onDanmakuMatched(DanDanMatchBean matchBean) {
        if (matchBean.errorCode != 0) {
            danmakuOff(false, "danmaku match error, code " + matchBean.errorCode + ", " + matchBean.errorMessage);
            return;
        }
        if (!matchBean.isMatched && matchBean.matches.size() > 0) {
            ViewGroup viewGroup = getTitleChooseView().findViewById(R.id.title_choose_layout);
            ListView listView = viewGroup.findViewById(R.id.match_list);
            String[] titles = matchBean.matches.stream().map(m -> m.animeTitle + " - " + m.episodeTitle).toArray(String[]::new);
            listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles));
            listView.setOnItemClickListener((parent, view, position, id) -> {
                DanDanMatchBean.Match match = matchBean.matches.get(position);
                mMainView.appendStatusLog("User choose " + match);
            });
            viewGroup.findViewById(R.id.go_search_button).setOnClickListener(v -> {
                Logger.i("No title match.");
                showUserSearchDialog();
                viewGroup.setVisibility(View.GONE);
            });
            viewGroup.setVisibility(View.VISIBLE);
        }
    }

    private void danmakuOff(boolean byUser, String reason) {
        if (byUser) {
            mMainView.appendStatusLog("Danmaku off by user.");
        } else {
            mMainView.appendStatusError("Danmaku off by system, " + reason);
        }
        setState(STATE_DANMAKU_HIDDEN);
    }

    private ViewGroup getTitleChooseView() {
        if (mTitleChooseView == null) {
            mTitleChooseView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.title_choose_view, this);
        }
        return mTitleChooseView;
    }

    private void showUserSearchDialog() {
        ViewGroup viewGroup = getTitleChooseView().findViewById(R.id.title_search_layout);
        viewGroup.findViewById(R.id.search_box).setOnClickListener(v -> {
            String words = ((EditText) viewGroup.findViewById(R.id.search_box)).getText().toString();
            if (!words.isEmpty()) {
                mPresenter.searchEpisode(words);
            }
        });
        viewGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEpisodeSearched(DanDanSearchEpisodeBean searchEpisodeBean) {
        if (searchEpisodeBean.errorCode != 0) {
            ToastUtils.show(getContext(), "Episode search error, code " + searchEpisodeBean.errorCode + ", " + searchEpisodeBean.errorMessage);
            return;
        }

        Object[] episodes = searchEpisodeBean.animes.stream().flatMap(a -> a.episodes.stream().map(e -> Pair.create(a, e))).toArray();
        String[] titles = Arrays.stream(episodes).map(e -> {
            Pair<Anime, Episode> p = (Pair<Anime, Episode>) e;
            return p.first.animeTitle + " - " + p.second.episodeTitle;
        }).toArray(String[]::new);
        ListView listView = getTitleChooseView().findViewById(R.id.search_result_list);
        listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Pair<Anime, Episode> pair = (Pair<Anime, Episode>) episodes[position];
            //mMainView.appendStatusLog("User choose " + match);
        });

        if (searchEpisodeBean.hasMore) {
            //ToastUtils.show(getContext());
            return;
        }
    }
}
