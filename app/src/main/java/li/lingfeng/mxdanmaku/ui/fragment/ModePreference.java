package li.lingfeng.mxdanmaku.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;

public class ModePreference extends PreferenceFragment {

    protected SwitchPreference mOverlayPreference;

    protected void initOverlayPreference() {
        mOverlayPreference = (SwitchPreference) findPreference("key_mode_overlay_permission");
        refreshOverlayPreference();
        mOverlayPreference.setOnPreferenceChangeListener((preference1, checked) -> {
            if ((boolean) checked) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    protected void refreshOverlayPreference() {
        mOverlayPreference.setChecked(Settings.canDrawOverlays(getActivity()));
    }
}
