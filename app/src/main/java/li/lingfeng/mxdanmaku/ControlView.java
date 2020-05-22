package li.lingfeng.mxdanmaku;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;

import li.lingfeng.mxdanmaku.bean.DanDanCommentBean;
import li.lingfeng.mxdanmaku.bean.DanDanMatchBean;
import li.lingfeng.mxdanmaku.bean.DanDanSearchEpisodeBean;
import li.lingfeng.mxdanmaku.bean.DanDanSearchEpisodeBean.Anime;
import li.lingfeng.mxdanmaku.bean.DanDanSearchEpisodeBean.Episode;
import li.lingfeng.mxdanmaku.contact.ControlContact;
import li.lingfeng.mxdanmaku.presenter.ControlPresenter;
import li.lingfeng.mxdanmaku.ui.widget.OverlayDialog;
import li.lingfeng.mxdanmaku.util.HashUtils;
import li.lingfeng.mxdanmaku.util.Logger;
import li.lingfeng.mxdanmaku.util.ToastUtils;
import li.lingfeng.mxdanmaku.util.Utils;

public class ControlView extends RelativeLayout implements ControlContact.View {

    private static final int STATE_DANMAKU_HIDDEN      = 0;
    private static final int STATE_PREPARE_FILE_INFO   = 1;
    private static final int STATE_DANMAKU_MATCHING    = 2;
    private static final int STATE_RETRIEVING_COMMENTS = 3;
    private static final SparseArray<String> sStateStrings = Utils.clsIntFieldsToStrings(ControlView.class, "STATE_");

    private String mFilePath;
    private int mVideoDuration;

    private ImageButton mShowHideButton;
    private boolean mDanmakuShown = false;
    private MainView mMainView;
    private AlertDialog mTitleSearchDialog;
    private ControlPresenter mPresenter = new ControlPresenter();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logger.d("Change state to " + sStateStrings.get(msg.what));
            switch (msg.what) {
                case STATE_DANMAKU_HIDDEN:
                    danmakuHidden();
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
                setState(STATE_PREPARE_FILE_INFO);
            } else {
                setState(STATE_DANMAKU_HIDDEN);
            }
        });
        mPresenter.attachView(this);
    }

    public void reset(String filePath, int videoDuration) {
        mFilePath = filePath;
        mVideoDuration = videoDuration;
        setState(STATE_DANMAKU_HIDDEN);
    }

    private void danmakuHidden() {
        mDanmakuShown = false;
        mShowHideButton.setImageResource(R.drawable.danmaku_hidden_button);
        mMainView.stopDanmaku();
    }

    private void prepareFileInfo() {
        Uri uri = Uri.parse(mFilePath);
        String _fileName = uri.getLastPathSegment();
        _fileName = FilenameUtils.removeExtension(_fileName);
        if (NumberUtils.isParsable(_fileName)) {
            List<String> segments = uri.getPathSegments();
            _fileName = segments.get(segments.size() - 2) + ' ' + _fileName;
        }
        String fileName = _fileName;
        mMainView.appendStatusLog("FileName: " + fileName);
        mMainView.appendStatusLog("VideoDuration: " + mVideoDuration);

        HashUtils.hashFileHeadAsync(getContext(), uri, 16 * 1024 * 1024, (hash, fileSize) -> {
            if (hash == null) {
                mMainView.appendStatusError("Error to get file hash.");
                setState(STATE_DANMAKU_HIDDEN);
            } else {
                mMainView.appendStatusLog("FileHash: " + hash);
                mMainView.appendStatusLog("FileSize: " + fileSize);
                mPresenter.matchDanmaku(fileName, hash, fileSize, mVideoDuration);
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
        if (!matchBean.isMatched) {
            if (matchBean.matches.size() > 0) {
                String[] titles = matchBean.matches.stream().map(m -> m.animeTitle + " - " + m.episodeTitle).toArray(String[]::new);
                new OverlayDialog.Builder(getContext())
                        .setTitle("Select title")
                        .setItems(titles, (_dialog, which) -> {
                            DanDanMatchBean.Match match = matchBean.matches.get(which);
                            mMainView.appendStatusLog("User choose " + match);
                            retrieveComments(match.episodeId);
                        })
                        .setNegativeButton("Search", (_dialog, which) -> {
                            Logger.i("No title match.");
                            showUserSearchDialog();
                            _dialog.dismiss();
                        })
                        .show();
            } else {
                showUserSearchDialog();
            }
        } else {
            retrieveComments(matchBean.matches.get(0).episodeId);
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

    private void retrieveComments(int episodeId) {
        mPresenter.getComments(episodeId);
        setState(STATE_RETRIEVING_COMMENTS);
    }

    private void showUserSearchDialog() {
        mTitleSearchDialog = new OverlayDialog.Builder(getContext())
                .setTitle("Search title")
                .setView(R.layout.title_search_dialog)
                .create();
        mTitleSearchDialog.setOnDismissListener(dialog -> {
            mTitleSearchDialog = null;
        });
        mTitleSearchDialog.show();

        EditText searchBox = mTitleSearchDialog.findViewById(R.id.search_box);
        Button searchButton = mTitleSearchDialog.findViewById(R.id.search_button);
        searchBox.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        });
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchButton.performClick();
                return true;
            }
            return false;
        });
        searchButton.setOnClickListener(v -> {
            String words = ((EditText) mTitleSearchDialog.findViewById(R.id.search_box)).getText().toString();
            if (!words.isEmpty()) {
                searchBox.clearFocus();
                searchButton.setEnabled(false);
                mPresenter.searchEpisode(words);
            }
        });
    }

    @Override
    public void onEpisodeSearched(DanDanSearchEpisodeBean searchEpisodeBean) {
        mTitleSearchDialog.findViewById(R.id.search_button).setEnabled(true);
        if (searchEpisodeBean.errorCode != 0) {
            ToastUtils.show(getContext(), "Episode search error, code " + searchEpisodeBean.errorCode + ", " + searchEpisodeBean.errorMessage);
            return;
        }
        if (searchEpisodeBean.hasMore) {
            ToastUtils.show(getContext(), "Type more words for precise search.");
        }

        Object[] episodes = searchEpisodeBean.animes.stream().flatMap(a -> a.episodes.stream().map(e -> Pair.create(a, e))).toArray();
        String[] titles = Arrays.stream(episodes).map(e -> {
            Pair<Anime, Episode> p = (Pair<Anime, Episode>) e;
            return p.first.animeTitle + " - " + p.second.episodeTitle;
        }).toArray(String[]::new);
        ListView listView = mTitleSearchDialog.findViewById(R.id.search_result_list);
        listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, titles));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Pair<Anime, Episode> pair = (Pair<Anime, Episode>) episodes[position];
            mMainView.appendStatusLog("User choose " + pair.first + ", " + pair.second);
            mTitleSearchDialog.dismiss();
            retrieveComments(pair.second.episodeId);
        });
    }

    @Override
    public void onCommentsGot(DanDanCommentBean commentBean) {
        if (commentBean.errorCode != 0) {
            mMainView.appendStatusError("Error to get comments, code " + commentBean.errorCode + ", " + commentBean.errorMessage);
            return;
        }
        if (commentBean.count == 0) {
            mMainView.appendStatusLog("No comment.");
            return;
        }
        mMainView.initDanmakuView(commentBean.comments);
    }
}
