package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }
    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            Preference preference = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSmmary(preference);
            Preference order = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSmmary(order);

        }

        private void bindPreferenceSmmary(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceValue = mPreferences.getString(getString(R.string.settings_min_magnitude_key),"");
            onPreferenceChange(preference,preferenceValue);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String stringValue = o.toString();
            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference)preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex>=0){
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}
