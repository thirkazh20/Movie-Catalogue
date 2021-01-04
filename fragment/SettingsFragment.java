package com.blogspot.thirkazh.moviecatalogue.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.blogspot.thirkazh.moviecatalogue.R;
import com.blogspot.thirkazh.moviecatalogue.alarm.DailyAlarm;
import com.blogspot.thirkazh.moviecatalogue.alarm.ReleaseAlarm;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private ReleaseAlarm releaseAlarm = new ReleaseAlarm();
    private DailyAlarm dailyAlarm= new DailyAlarm();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference languafePreference = findPreference("language");
        SwitchPreferenceCompat dailyReminder = findPreference("daily_reminder");
        SwitchPreferenceCompat releaseReminder = findPreference("relase_reminder");

        if (dailyReminder != null) {
            dailyReminder.setOnPreferenceChangeListener(this);
        }
        if (releaseReminder != null) {
            releaseReminder.setOnPreferenceChangeListener(this);
        }

        if (languafePreference != null) {
            languafePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                    startActivity(mIntent);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        boolean isSet = (boolean) newValue;

        if (key.equals("daily_reminder")) {
            if (isSet) {
                dailyAlarm.setDailyAlarm(getActivity());
            } else {
                dailyAlarm.cancelDailyAlarm(getActivity());
            }
        } else {
            if (isSet) {
                releaseAlarm.setReapeatAlarm(getActivity());
            } else {
                releaseAlarm.cancelReleaseAlarm(getActivity());
            }
        }

        return true;
    }
}