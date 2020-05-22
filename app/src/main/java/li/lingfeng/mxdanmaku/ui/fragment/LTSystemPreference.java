package li.lingfeng.mxdanmaku.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import li.lingfeng.mxdanmaku.R;

public class LTSystemPreference extends ModePreference {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ltsystem);
        initOverlayPreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshOverlayPreference();
    }
}
