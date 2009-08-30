package com.ipoki.android;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the XML preferences file
        try {
        addPreferencesFromResource(R.xml.settings);
        }
        catch(Exception ex) {
        	Log.e("Ipoki", ex.getMessage());
        }
    }

}
