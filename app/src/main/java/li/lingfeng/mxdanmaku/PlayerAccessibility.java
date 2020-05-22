package li.lingfeng.mxdanmaku;

import android.accessibilityservice.AccessibilityService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import li.lingfeng.mxdanmaku.util.Logger;
import li.lingfeng.mxdanmaku.util.PackageUtils;
import li.lingfeng.mxdanmaku.util.Utils;

public class PlayerAccessibility extends AccessibilityService {

    private String mFilePath;
    private boolean mPlaying;
    private boolean mInMXPLayer = false;
    private boolean mControlShown = false;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Logger.d("PlayerAccessibility onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Logger.d("onAccessibilityEvent " + event);
        AccessibilityNodeInfo source;
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (isMXPlayer(event)) {
                    String className = event.getClassName().toString();
                    if ("com.mxtech.videoplayer.ActivityScreen".equals(className) || "com.mxtech.videoplayer.ad.ActivityScreen".equals(className)) {
                        String newPath = IntentRedirector.popFilePath();
                        if (newPath != null) {
                            sendCommand(OP.OP_DESTROY); // Destroy last.
                            mFilePath = newPath;
                            mInMXPLayer = true;
                            mPlaying = true;
                        }
                        resumeIfNot();
                        return;
                    }
                }
                if (mInMXPLayer && isActivity(event)) {
                    pause();
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (!isMXPlayer(event)) {
                    return;
                }
                if ((source = getSourceFromEvent(event, "posText", "android.widget.TextView")) != null) {
                    createIfNot(event);
                    resumeIfNot();
                    String pos = source.getText().toString();
                    Logger.v("Update pos " + pos);
                    ContentValues values = new ContentValues(1);
                    values.put("seconds", Utils.stringTimeToSeconds(pos));
                    sendCommand(OP.OP_SEEK_TO, values);
                } else {
                    AccessibilityNodeInfo node = getNode(event, "posText");
                    if (node != null && node.isVisibleToUser()) {
                        if (!mControlShown) {
                            Logger.v("Show danmaku control button.");
                            mControlShown = true;
                            sendCommand(OP.OP_SHOW_CONTROL);
                        }
                    } else if (mControlShown) {
                        Logger.v("Hide danmaku control button.");
                        mControlShown = false;
                        sendCommand(OP.OP_HIDE_CONTROL);
                    }
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if (!isMXPlayer(event)) {
                    return;
                }
                if ((source = getSourceFromEvent(event, "playpause", "android.widget.ImageButton")) != null) {
                    mPlaying = !mPlaying;
                    Logger.v("Play/Pause button clicked, mPlaying " + mPlaying);
                    sendCommand(mPlaying ? OP.OP_RESUME : OP.OP_PAUSE);
                }
                break;
        }
    }

    private boolean isMXPlayer(AccessibilityEvent event) {
        return PackageNames.isMXPlayer(event.getPackageName());
    }

    private boolean isActivity(AccessibilityEvent event) {
        return PackageUtils.isActivity(this, event.getPackageName(), event.getClassName());
    }

    private AccessibilityNodeInfo getSourceFromEvent(AccessibilityEvent event, String resName, String resType) {
        String className = event.getClassName().toString();
        if (!resType.equals(className)) {
            return null;
        }
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return null;
        }
        String name = source.getViewIdResourceName();
        name = StringUtils.substringAfterLast(name, "/");
        return resName.equals(name) ? source : null;
    }

    private AccessibilityNodeInfo getNode(AccessibilityEvent event, String resName) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(event.getPackageName() + ":id/" + resName);
        return nodes.size() > 0 ? nodes.get(0) : null;
    }

    private void createIfNot(AccessibilityEvent event) {
        if (mFilePath != null) {
            AccessibilityNodeInfo node = getNode(event, "durationText");
            if (node != null) {
                String _duration = node.getText().toString();
                int duration = Utils.stringTimeToSeconds(_duration);
                if (duration > 0) {
                    ContentValues values = new ContentValues(2);
                    values.put("file_path", mFilePath);
                    values.put("video_duration", duration);
                    sendCommand(OP.OP_CREATE, values);
                    mFilePath = null;
                }
            }
        }
    }

    private void resumeIfNot() {
        if (!mInMXPLayer) {
            Logger.v("In MX Player.");
            mInMXPLayer = true;
            sendCommand(OP.OP_SHOW_ALL);
            if (mPlaying) {
                sendCommand(OP.OP_RESUME);
            }
        }
    }

    private void pause() {
        Logger.v("Not in MX Player.");
        mInMXPLayer = false;
        sendCommand(OP.OP_HIDE_ALL);
        if (mPlaying) {
            sendCommand(OP.OP_PAUSE);
        }
    }

    private void sendCommand(int op) {
        sendCommand(op, null);
    }

    private void sendCommand(int op, ContentValues values) {
        getContentResolver().update(Uri.parse("content://li.lingfeng.mxdanmaku.MainController/" + op), values, null, null);
    }

    @Override
    public void onInterrupt() {
        Logger.w("PlayerAccessibility onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sendCommand(OP.OP_DESTROY);
        return super.onUnbind(intent);
    }
}
