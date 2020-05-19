package li.lingfeng.mxdanmaku.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;

public class OverlayDialog {

    public static class Builder extends AlertDialog.Builder {

        public Builder(Context context) {
            super(context);
        }

        @Override
        public AlertDialog create() {
            AlertDialog dialog = super.create();
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layoutParams.format = PixelFormat.RGBA_8888;
            return dialog;
        }
    }
}
