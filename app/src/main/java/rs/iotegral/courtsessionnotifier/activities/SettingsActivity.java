package rs.iotegral.courtsessionnotifier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Objects;

import rs.iotegral.courtsessionnotifier.R;
import rs.iotegral.courtsessionnotifier.sync.CourtNewsSyncAdapter;

/**
 * Created by Lauda on 9/4/2018 20:54.
 */
public class SettingsActivity extends PreferenceActivity {
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_general);

            Preference syncFrequency = findPreference("court_news_sync_frequency");
            Preference enableNotifications = findPreference(getResources().getString(R.string.court_news_notification_value));

            enableNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return false;
                }
            });

            syncFrequency.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String value = newValue.toString();

                    if (preference instanceof ListPreference) {
                        // For list pref find the correct display value in pref entries list
                        ListPreference listPref = (ListPreference) preference;
                        int index = listPref.findIndexOfValue(value);

                        if (index >= 0) {
                            preference.setSummary(listPref.getEntries()[index]);
                            final int sync = Integer.parseInt(newValue.toString());
                            CourtNewsSyncAdapter.configurePeriodicSync(getActivity().getApplicationContext(), sync, (sync < 0));
                            Log.d(LOG_TAG, "Sync interval change triggered! Syncing now!");
                        }
                    }
                    else {
                        preference.setSummary(value);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return Objects.requireNonNull(super.getParentActivityIntent()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
