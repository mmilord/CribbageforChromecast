package cast.chrome.cribbage.cribbageforchromecast.Utils;

/**
 * Created by milord on 18-Oct-14.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import cast.chrome.cribbage.cribbageforchromecast.R;

public class UserSettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new UserSettingsActivityFragment()).commit();
    }

    public static class UserSettingsActivityFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}