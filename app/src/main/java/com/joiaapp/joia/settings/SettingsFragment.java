package com.joiaapp.joia.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joiaapp.joia.MainActivity;
import com.joiaapp.joia.MainAppFragment;
import com.joiaapp.joia.R;
import com.joiaapp.joia.service.ServiceFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arnell on 12/29/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener, MainAppFragment {

    private ListView lvSettings;
    private SettingsOptionsArrayAdapter settingsOptionsArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        lvSettings = (ListView) rootView.findViewById(R.id.lvSettings);
        List<String> settingsOptions = Arrays.asList("Profile", "Group Settings", "Terms & Conditions", "About our Sponsor", "Logout");
        settingsOptionsArrayAdapter = new SettingsOptionsArrayAdapter(getActivity(), settingsOptions);
        lvSettings.setAdapter(settingsOptionsArrayAdapter);
        lvSettings.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        String clickedItem = (String) adapter.getItemAtPosition(position);
        switch (clickedItem) {
            case "Logout":
                logout();
                break;
            case "Profile":
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void logout() {
        ServiceFactory.getUserService().logout();
        ((MainActivity)getContext()).startSignInProcess();
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
