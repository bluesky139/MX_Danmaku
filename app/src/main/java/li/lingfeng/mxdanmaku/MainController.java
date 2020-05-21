package li.lingfeng.mxdanmaku;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import li.lingfeng.mxdanmaku.util.Logger;

public class MainController extends ContentProvider {

    private OpHandler mHandler;
    private WindowManager mWindowManager;
    private MainView mMainView;
    private ControlView mControlView;
    private boolean mMainViewVisible = true;
    private boolean mControlViewVisible = true;

    @Override
    public boolean onCreate() {
        Logger.d("MainController onCreate.");
        mHandler = new OpHandler();
        return true;
    }

    private void createViews() {
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        createMainView();
        createControlView();
    }

    private void createMainView() {
        Logger.d("createMainView");
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
        Logger.d("createControlView");
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
        int op = Integer.parseInt(uri.getLastPathSegment());
        Logger.v("MainController update op " + OP.sOpStrings.get(op) + ", " + values);
        mHandler.obtainMessage(op, values).sendToTarget();
        return 0;
    }

    private class OpHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ContentValues values = (ContentValues) msg.obj;
            if (msg.what == OP.OP_CREATE) {
                String filePath = values.getAsString("file_path");
                int videoDuration = values.getAsInteger("video_duration");
                if (mMainView == null) {
                    createViews();
                }
                mControlView.resetFileInfo(filePath, videoDuration);
                return;
            } else if (mMainView == null) {
                return;
            }

            switch (msg.what) {
                case OP.OP_SHOW_CONTROL:
                    mControlView.setVisibility(View.VISIBLE);
                    break;
                case OP.OP_HIDE_CONTROL:
                    mControlView.setVisibility(View.GONE);
                    break;
                case OP.OP_SHOW_ALL:
                    mMainView.setVisibility(mMainViewVisible ? View.VISIBLE : View.GONE);
                    mControlView.setVisibility(mControlViewVisible ? View.VISIBLE : View.GONE);
                    break;
                case OP.OP_HIDE_ALL:
                    mMainViewVisible = mMainView.getVisibility() == View.VISIBLE;
                    mControlViewVisible = mControlView.getVisibility() == View.VISIBLE;
                    mMainView.setVisibility(View.GONE);
                    mControlView.setVisibility(View.GONE);
                    break;
                case OP.OP_SEEK_TO:
                    int seconds = values.getAsInteger("seconds");
                    mMainView.seekTo(seconds);
                    break;
                case OP.OP_RESUME:
                    mMainView.resumeDanmaku();
                    break;
                case OP.OP_PAUSE:
                    mMainView.pauseDanmaku();
                    break;
                case OP.OP_DESTROY:
                    mWindowManager.removeView(mMainView);
                    mWindowManager.removeView(mControlView);
                    mMainView = null;
                    mControlView = null;
                    break;
                default:
                    Logger.e("Unknown op " + OP.sOpStrings.get(msg.what));
                    break;
            }
        }
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
