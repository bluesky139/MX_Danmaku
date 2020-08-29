package li.lingfeng.mxdanmaku;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
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

import li.lingfeng.mxdanmaku.bean.CommentBean;
import li.lingfeng.mxdanmaku.bean.MatchBean;
import li.lingfeng.mxdanmaku.bean.SearchEpisodeBean;
import li.lingfeng.mxdanmaku.bean.SearchEpisodeBean.Anime;
import li.lingfeng.mxdanmaku.bean.SearchEpisodeBean.Episode;
import li.lingfeng.mxdanmaku.contact.ControlContact;
import li.lingfeng.mxdanmaku.presenter.ControlPresenter;
import li.lingfeng.mxdanmaku.ui.widget.OverlayDialog;
import li.lingfeng.mxdanmaku.util.HashUtils;
import li.lingfeng.mxdanmaku.util.Logger;
import li.lingfeng.mxdanmaku.util.ToastUtils;
import li.lingfeng.mxdanmaku.util.Utils;

public class ControlView extends RelativeLayout implements ControlContact.View {

    private static final int STATE_DANMAKU_HIDDEN         = 0;
    private static final int STATE_PREPARE_FILE_INFO      = 1;
    private static final int STATE_DANMAKU_MATCHING       = 2;
    private static final int STATE_USER_SEARCH            = 3;
    private static final int STATE_RETRIEVING_COMMENTS    = 4;
    private static final int STATE_DANMAKU_VIEW_SHOW      = 5;
    private static final SparseArray<String> sStateStrings = Utils.clsIntFieldsToStrings(ControlView.class, "STATE_");
    private int mState = STATE_DANMAKU_HIDDEN;

    private String mFilePath;
    private int mVideoDuration;

    private ImageButton mShowHideButton;
    private MainView mMainView;
    private AlertDialog mTitleSearchDialog;
    private ControlPresenter mPresenter = new ControlPresenter();
    private boolean mDanmakuShown = false;
    private boolean mCommentsGot = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mState = msg.what;
            Logger.d("Change state to " + sStateStrings.get(mState));
            switch (mState) {
                case STATE_DANMAKU_HIDDEN:
                    danmakuHidden();
                    break;
                case STATE_PREPARE_FILE_INFO:
                    prepareFileInfo();
                    break;
                case STATE_USER_SEARCH:
                    showUserSearchDialog();
                    break;
                case STATE_DANMAKU_VIEW_SHOW:
                    showDanmaku();
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
                if (!mCommentsGot) {
                    setState(STATE_PREPARE_FILE_INFO);
                } else {
                    setState(STATE_DANMAKU_VIEW_SHOW);
                }
            } else {
                setState(STATE_DANMAKU_HIDDEN);
            }
        });
        mPresenter.attachView(this);
    }

    public void setFile(String filePath, int videoDuration) {
        mFilePath = filePath;
        mVideoDuration = videoDuration;
    }

    private void danmakuHidden() {
        mDanmakuShown = false;
        mShowHideButton.setImageResource(R.drawable.danmaku_hidden_button);
        mMainView.hideDanmaku();
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
        mMainView.appendStatusLog(getString(R.string.control_file_name, fileName));
        mMainView.appendStatusLog(getString(R.string.control_video_duration, mVideoDuration));

        HashUtils.hashFileHeadAsync(getContext(), uri, 16 * 1024 * 1024, (hash, fileSize) -> {
            if (hash == null) {
                danmakuOff(false, getString(R.string.control_error_to_hash_file));
            } else {
                mMainView.appendStatusLog(getString(R.string.control_file_hash, hash));
                mMainView.appendStatusLog(getString(R.string.control_file_size, fileSize));
                mPresenter.matchDanmaku(fileName, hash, fileSize, mVideoDuration);
                setState(STATE_DANMAKU_MATCHING);
            }
        });
    }

    @Override
    public void onDanmakuMatched(MatchBean matchBean) {
        if (mState != STATE_DANMAKU_MATCHING) {
            return;
        }
        if (matchBean.errorCode != 0) {
            danmakuOff(false, getString(R.string.control_danmaku_match_error, matchBean.errorCode, matchBean.errorMessage));
            return;
        }
        if (!matchBean.isMatched) {
            if (matchBean.matches.size() > 0) {
                String[] titles = matchBean.matches.stream().map(m -> m.animeTitle + " - " + m.episodeTitle).toArray(String[]::new);
                new OverlayDialog.Builder(getContext())
                        .setTitle(getString(R.string.control_select_title))
                        .setItems(titles, (_dialog, which) -> {
                            MatchBean.Match match = matchBean.matches.get(which);
                            mMainView.appendStatusLog(getString(R.string.control_user_select, match.animeTitle, match.episodeTitle));
                            Logger.d(match.toString());
                            retrieveComments(match.episodeId);
                        })
                        .setNegativeButton(getString(R.string.control_manual_search), (_dialog, which) -> {
                            Logger.i("No title match.");
                            setState(STATE_USER_SEARCH);
                            _dialog.dismiss();
                        })
                        .setOnCancelListener(dialog -> {
                            if (mState == STATE_DANMAKU_MATCHING) {
                                setState(STATE_DANMAKU_HIDDEN);
                            }
                        })
                        .show();
            } else {
                setState(STATE_USER_SEARCH);
            }
        } else {
            retrieveComments(matchBean.matches.get(0).episodeId);
        }
    }

    private void danmakuOff(boolean byUser, String reason) {
        if (byUser) {
            mMainView.appendStatusLog(getString(R.string.control_danmaku_off_by_user));
        } else {
            mMainView.appendStatusError(getString(R.string.control_danmaku_off_by_system, reason));
        }
        setState(STATE_DANMAKU_HIDDEN);
    }

    private void showUserSearchDialog() {
        mTitleSearchDialog = new OverlayDialog.Builder(getContext())
                .setTitle(getString(R.string.control_manual_search))
                .setView(R.layout.title_search_dialog)
                .create();
        mTitleSearchDialog.setOnDismissListener(dialog -> {
            mTitleSearchDialog = null;
        });
        mTitleSearchDialog.setOnCancelListener(dialog -> {
            if (mState == STATE_USER_SEARCH) {
                setState(STATE_DANMAKU_HIDDEN);
            }
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
    public void onEpisodeSearched(SearchEpisodeBean searchEpisodeBean) {
        if (mState != STATE_USER_SEARCH) {
            return;
        }
        mTitleSearchDialog.findViewById(R.id.search_button).setEnabled(true);
        if (searchEpisodeBean.errorCode != 0) {
            ToastUtils.show(getContext(), getString(R.string.control_episode_search_error,
                    searchEpisodeBean.errorCode, searchEpisodeBean.errorMessage));
            return;
        }
        if (searchEpisodeBean.hasMore) {
            ToastUtils.show(getContext(), getString(R.string.control_type_precise_search));
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
            mMainView.appendStatusLog(getString(R.string.control_user_select, pair.first.animeTitle, pair.second.episodeTitle));
            Logger.d(pair.first.toString() + ',' + pair.second);
            mTitleSearchDialog.dismiss();
            retrieveComments(pair.second.episodeId);
        });
    }

    private void retrieveComments(int episodeId) {
        mPresenter.getComments(episodeId);
        setState(STATE_RETRIEVING_COMMENTS);
    }

    @Override
    public void onCommentsGot(CommentBean commentBean) {
        if (mState != STATE_RETRIEVING_COMMENTS) {
            return;
        }
        if (commentBean.errorCode != 0) {
            danmakuOff(false, getString(R.string.control_error_to_get_comments, commentBean.errorCode, commentBean.errorMessage));
            return;
        }
        if (commentBean.count == 0) {
            danmakuOff(false, getString(R.string.control_no_comment));
            return;
        }
        mCommentsGot = true;
        mMainView.initDanmakuView(commentBean.comments);
    }

    private void showDanmaku() {
        mMainView.showDanmaku();
    }

    public void destroy() {
        mPresenter.detachView();
    }

    public final String getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    public final String getString(@StringRes int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }
}
