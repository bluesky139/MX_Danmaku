package li.lingfeng.mxdanmaku.ui.fragment;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.accessibility.AccessibilityManager;

import li.lingfeng.mxdanmaku.IntentRedirector;
import li.lingfeng.mxdanmaku.PlayerAccessibility;
import li.lingfeng.mxdanmaku.R;
import li.lingfeng.mxdanmaku.util.ComponentUtils;

public class AccessibilityFragment extends ModePreference {

    private SwitchPreference mStoragePreference;
    private SwitchPreference mServicePreference;
    private SwitchPreference mIntentRedirectorPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_accessibility);
        initStoragePreference();
        initOverlayPreference();
        initServicePreference();
        initIntentRedirectorPreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStoragePreference();
        refreshOverlayPreference();
        refreshServicePreference();
        refreshIntentRedirectorPreference();
    }

    private void initStoragePreference() {
        mStoragePreference = (SwitchPreference) findPreference("key_accessibility_storage_permission");
        refreshStoragePreference();
        mStoragePreference.setOnPreferenceChangeListener((_preference, checked) -> {
            if ((boolean) checked) {
                requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 100);
                return true;
            }
            return false;
        });
    }

    private void refreshStoragePreference() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mStoragePreference.setChecked(true);
        } else {
            mStoragePreference.setChecked(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            refreshStoragePreference();
        }
    }

    private void initServicePreference() {
        mServicePreference = (SwitchPreference) findPreference("key_accessibility_service_permission");
        refreshServicePreference();
        mServicePreference.setOnPreferenceChangeListener((preference, checked) -> {
            if ((boolean) checked) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void refreshServicePreference() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        boolean enabled = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_VISUAL)
                .stream()
                .filter(info -> info.getResolveInfo().serviceInfo.name.equals(PlayerAccessibility.class.getName()))
                .findFirst()
                .isPresent();
        mServicePreference.setChecked(enabled);
    }

    private void initIntentRedirectorPreference() {
        mIntentRedirectorPreference = (SwitchPreference) findPreference("key_accessibility_intent_redirector");
        refreshIntentRedirectorPreference();
        mIntentRedirectorPreference.setOnPreferenceChangeListener((preference, checked) -> {
            ComponentUtils.enableComponent(getContext(), IntentRedirector.class, (boolean) checked);
            return true;
        });
    }

    private void refreshIntentRedirectorPreference() {
        if (ComponentUtils.isComponentEnabled(getContext(), IntentRedirector.class)) {
            mIntentRedirectorPreference.setChecked(true);
        } else {
            mIntentRedirectorPreference.setChecked(false);
        }
    }
}
