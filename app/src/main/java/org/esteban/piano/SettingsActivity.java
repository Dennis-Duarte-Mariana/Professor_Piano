package org.esteban.piano;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		PreferenceScreen screen = getPreferenceScreen();
		Preference pref1 = getPreferenceManager().findPreference("pref_rows");
		Preference pref2 = getPreferenceManager().findPreference("pref_damper");
		Preference pref3 = getPreferenceManager().findPreference("pref_octaves");
		Preference pref4 = getPreferenceManager().findPreference("pref_orient");
		screen.removePreference(pref1);
		screen.removePreference(pref2);
		screen.removePreference(pref3);
		screen.removePreference(pref4);

	}
}
