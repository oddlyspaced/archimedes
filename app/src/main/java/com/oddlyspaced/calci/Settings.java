package com.oddlyspaced.calci;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.oddlyspaced.calci.archimedes.model.ARSettings;
/* loaded from: classes.dex */
public class Settings extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(16908290, new SettingsFragment()).commit();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        ARSettings.sharedSettings(this).updateForStoredPreferences(this);
        Intent settingsChangedNotification = new Intent();
        settingsChangedNotification.setAction(ARSettings.SETTINGS_DID_CHANGE_NOTIFICATION);
        sendBroadcast(settingsChangedNotification);
    }

    /* loaded from: classes.dex */
    public static class SettingsFragment extends PreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
