package li.lingfeng.mxdanmaku.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class ComponentUtils {

    public static void enableComponent(Context context, String componentCls, boolean enabled) {
        ComponentName componentName = new ComponentName(context, componentCls);
        context.getPackageManager().setComponentEnabledSetting(componentName,
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void enableComponent(Context context, Class<?> componentCls, boolean enabled) {
        enableComponent(context, componentCls.getName(), enabled);
    }

    public static boolean isComponentEnabled(Context context, String componentCls) {
        ComponentName componentName = new ComponentName(context, componentCls);
        return context.getPackageManager().getComponentEnabledSetting(componentName)
                == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    public static boolean isComponentEnabled(Context context, Class<?> componentCls) {
        return isComponentEnabled(context, componentCls.getName());
    }
}
