package ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.teachervirus.R;


public class SettingActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_default);
    }
}
