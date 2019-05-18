package cn.edu.cczu.iot161g2.ccdict.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import cn.edu.cczu.iot161g2.ccdict.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle(); // TODO
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
