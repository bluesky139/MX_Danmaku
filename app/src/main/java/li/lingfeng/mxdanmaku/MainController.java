package li.lingfeng.mxdanmaku;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import li.lingfeng.mxdanmaku.util.Logger;

public class MainController extends ContentProvider {

    private WindowManager mWindowManager;
    private MainView mMainView;
    private ControlView mControlView;
    private boolean mMainViewVisible;
    private boolean mControlViewVisible;

    @Override
    public boolean onCreate() {
        Logger.d("MainController onCreate.");
        return true;
    }

    private void createViews() {
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        createMainView();
        createControlView();
    }

    private void createMainView() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mMainView = new MainView(getContext());
        mWindowManager.addView(mMainView, layoutParams);
    }

    private void createControlView() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        mControlView = new ControlView(getContext(), mMainView);
        mWindowManager.addView(mControlView, layoutParams);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String op = uri.getLastPathSegment();
        Logger.v("MainController update op " + op + ", " + values);
        if (op.equals("create")) {
            String filePath = values.getAsString("file_path");
            int videoDuration = values.getAsInteger("video_duration");
            if (mMainView == null) {
                createViews();
            }
            mControlView.resetFileInfo(filePath, videoDuration);
            return 0;
        } else if (mMainView == null) {
            return 0;
        }

        if (op.equals("show_control")) {
            mControlView.setVisibility(View.VISIBLE);
        } else if (op.equals("hide_control")) {
            mControlView.setVisibility(View.GONE);
        } else if (op.equals("show_all")) {
            Logger.d("mMainView show_all mMainViewVisible " + mMainViewVisible);
            mMainView.setVisibility(mMainViewVisible ? View.VISIBLE : View.GONE);
            mControlView.setVisibility(mControlViewVisible ? View.VISIBLE : View.GONE);
        } else if (op.equals("hide_all")) {
            Logger.d("mMainView hide_all");
            mMainViewVisible = mMainView.getVisibility() == View.VISIBLE;
            mControlViewVisible = mControlView.getVisibility() == View.VISIBLE;
            mMainView.setVisibility(View.GONE);
            mControlView.setVisibility(View.GONE);
        } else if (op.equals("seek_to")) {
            int seconds = values.getAsInteger("seconds");
            mMainView.seekTo(seconds);
        } else if (op.equals("resume")) {
            mMainView.resumeDanmaku();
        } else if (op.equals("pause")) {
            mMainView.pauseDanmaku();
        } else if (op.equals("destroy")) {
            mWindowManager.removeView(mMainView);
            mWindowManager.removeView(mControlView);
            mMainView = null;
            mControlView = null;
        } else {
            Logger.e("Unknown op " + op);
        }
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
