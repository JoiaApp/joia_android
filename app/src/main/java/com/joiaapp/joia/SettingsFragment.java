package com.joiaapp.joia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arnell on 12/29/2016.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener, MainAppFragment {

    private ListView lvSettings;
    private SettingsOptionsArrayAdapter settingsOptionsArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        lvSettings = (ListView) rootView.findViewById(R.id.lvSettings);
        List<String> settingsOptions = Arrays.asList("Profile", "Group Settings", "Terms & Conditions", "About our Sponsor", "Logout");
        settingsOptionsArrayAdapter = new SettingsOptionsArrayAdapter(getActivity(), settingsOptions);
        lvSettings.setAdapter(settingsOptionsArrayAdapter);
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public String getNavBarTitle() {
        return "Settings";
    }

    @Override
    public boolean isNavBarBackButtonVisible() {
        return false;
    }

    @Override
    public boolean isNavBarNextButtonVisible() {
        return false;
    }
}
