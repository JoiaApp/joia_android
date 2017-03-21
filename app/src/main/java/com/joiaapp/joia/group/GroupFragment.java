package com.joiaapp.joia.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.joiaapp.joia.DatabaseHelper;
import com.joiaapp.joia.MainAppFragment;
import com.joiaapp.joia.R;
import com.joiaapp.joia.dto.User;

import java.util.List;

/**
 * Created by arnell on 12/22/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GroupFragment extends Fragment implements View.OnClickListener, MainAppFragment {
    private ListView lvGroupMembers;
    private GroupMembersArrayAdapter groupMembersArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
        List<User> members = dbHelper.getGroupMembers();
        lvGroupMembers = (ListView) rootView.findViewById(R.id.lvGroupMembers);
        groupMembersArrayAdapter = new GroupMembersArrayAdapter(getActivity(), members);
        lvGroupMembers.setAdapter(groupMembersArrayAdapter);
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public String getNavBarTitle() {
        return "Group";
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
