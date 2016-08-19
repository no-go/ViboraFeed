package de.vibora.viborafeed;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Wird zum Laden der in XML abgelegten Preferences genutzt.
 * Es wird im Layout der {@link PreferencesActivity} genutzt.
 */
public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}