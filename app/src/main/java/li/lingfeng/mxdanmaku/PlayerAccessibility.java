package li.lingfeng.mxdanmaku;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import li.lingfeng.mxdanmaku.util.Logger;

public class PlayerAccessibility extends AccessibilityService {

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
                    if (event.getText().contains("Do you wish to resume from where you stopped?")) {
                        ContentValues values = new ContentValues(2);
                        values.put("file_path", "file:///sdcard/みなみけ ただいま (Creditless OP) BDrip x264-ank [XV");
                        values.put("video_duration", 92);
                        sendCommand("create", values);
                        mInMXPLayer = true;
                        mPlaying = true;
                        return;
                    }
                    String className = event.getClassName().toString();
                    if ("com.mxtech.videoplayer.ActivityScreen".equals(className) || "com.mxtech.videoplayer.ad.ActivityScreen".equals(className)) {
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
                    String pos = source.getText().toString();
                    Logger.v("Update pos " + pos);
                    String[] strings = StringUtils.split(pos, ':');
                    int seconds = Integer.parseInt(strings[strings.length - 1]) + Integer.parseInt(strings[strings.length - 2]) * 60
                            + (strings.length == 3 ? Integer.parseInt(strings[0]) * 3600 : 0);
                    ContentValues values = new ContentValues(1);
                    values.put("seconds", seconds);
                    sendCommand("seek_to", values);
                    resumeIfNot();
                } else {
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    if (rootNode == null) {
                        return;
                    }
                    List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(event.getPackageName() + ":id/posText");
                    if (nodes.size() > 0 && nodes.get(0).isVisibleToUser()) {
                        if (!mControlShown) {
                            Logger.v("Show danmaku control button.");
                            mControlShown = true;
                            sendCommand("show_control");
                        }
                    } else if (mControlShown) {
                        Logger.v("Hide danmaku control button.");
                        mControlShown = false;
                        sendCommand("hide_control");
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
                    sendCommand(mPlaying ? "resume" : "pause");
                }
                break;
        }
    }

    private boolean isMXPlayer(AccessibilityEvent event) {
        CharSequence packageName = event.getPackageName();
        return "com.mxtech.videoplayer.pro".equals(packageName) || "com.mxtech.videoplayer.ad".equals(packageName);
    }

    private boolean isActivity(AccessibilityEvent event) {
        ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
        try {
            return getPackageManager().getActivityInfo(componentName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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

    private void resumeIfNot() {
        if (!mInMXPLayer) {
            Logger.v("In MX Player.");
            mInMXPLayer = true;
            sendCommand("show_all");
            if (mPlaying) {
                sendCommand("resume");
            }
        }
    }

    private void pause() {
        Logger.v("Not in MX Player.");
        mInMXPLayer = false;
        sendCommand("hide_all");
        if (mPlaying) {
            sendCommand("pause");
        }
    }

    private void sendCommand(String op) {
        sendCommand(op, null);
    }

    private void sendCommand(String op, ContentValues values) {
        getContentResolver().update(Uri.parse("content://li.lingfeng.mxdanmaku.MainController/" + op), values, null, null);
    }

    @Override
    public void onInterrupt() {
        Logger.w("PlayerAccessibility onInterrupt");
    }
}
