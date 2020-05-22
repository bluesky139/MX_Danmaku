package li.lingfeng.mxdanmaku.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class PackageUtils {

    public static boolean isPackageInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public static boolean isActivity(Context context, CharSequence packageName, CharSequence className) {
        if (packageName == null || className == null) {
            return false;
        }
        ComponentName componentName = new ComponentName(packageName.toString(), className.toString());
        try {
            return context.getPackageManager().getActivityInfo(componentName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
