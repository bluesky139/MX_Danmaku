package li.lingfeng.mxdanmaku.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_LONG);
    }

    public static void show(Context context, String text, int duration) {
        Toast.makeText(context, text, duration).show();
        Logger.i(text);
    }
}
