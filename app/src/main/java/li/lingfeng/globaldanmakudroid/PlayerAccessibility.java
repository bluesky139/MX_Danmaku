package li.lingfeng.globaldanmakudroid;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.apache.commons.lang3.StringUtils;

import li.lingfeng.globaldanmakudroid.util.Logger;

public class PlayerAccessibility extends AccessibilityService {

    private boolean mPlaying;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Logger.d("PlayerAccessibility onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Logger.d("onAccessibilityEvent " + event);
        AccessibilityNodeInfo source;
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                if ("com.mxtech.videoplayer.ad.ActivityScreen".equals(className)) {
                    Logger.v("New play.");
                    mPlaying = true;
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if ((source = getSourceFromEvent(event, "posText", "android.widget.TextView")) != null) {
                    Logger.v("Update pos " + source.getText());
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if ((source = getSourceFromEvent(event, "playpause", "android.widget.ImageButton")) != null) {
                    mPlaying = !mPlaying;
                    Logger.v("Play/Pause button clicked, mPlaying " + mPlaying);
                }
                break;
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

    @Override
    public void onInterrupt() {
        Logger.w("PlayerAccessibility onInterrupt");
    }
}
