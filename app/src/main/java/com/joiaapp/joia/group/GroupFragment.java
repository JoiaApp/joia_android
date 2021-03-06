package com.joiaapp.joia.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.joiaapp.joia.MainAppFragment;
import com.joiaapp.joia.R;
import com.joiaapp.joia.dto.Group;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.service.GroupService;
import com.joiaapp.joia.service.ServiceFactory;
import com.joiaapp.joia.util.ResponseHandler;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by arnell on 12/22/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class GroupFragment extends Fragment implements MainAppFragment, AdapterView.OnItemClickListener {
    private TextView tvGroupName;
    private TextView tvGroupMemberCount;
    private ListView lvGroupMembers;
    private GroupMembersArrayAdapter groupMembersArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        tvGroupName = (TextView) rootView.findViewById(R.id.tvGroupName);
        tvGroupMemberCount = (TextView) rootView.findViewById(R.id.tvGroupMemberCount);
        lvGroupMembers = (ListView) rootView.findViewById(R.id.lvGroupMembers);
        groupMembersArrayAdapter = new GroupMembersArrayAdapter(getActivity(), new ArrayList<User>(), false, true);
        lvGroupMembers.setAdapter(groupMembersArrayAdapter);
        lvGroupMembers.setOnItemClickListener(this);
        GroupService groupService = ServiceFactory.getGroupService();
        final Group group = groupService.getCurrentGroup();
        if (group != null) {
            groupService.getGroupMembers(group, new ResponseHandler<List<User>>() {
                @Override
                public void onResponse(List<User> response) {
                    tvGroupName.setText(group.getName());
                    tvGroupMemberCount.setText(String.format("%s Members", response.size()));
                    groupMembersArrayAdapter.setMembers(response);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Failed to load group members.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
        return rootView;
    }

    public void refreshView() {
        GroupService groupService = ServiceFactory.getGroupService();
        final Group currentGroup = groupService.getCurrentGroup();
        if (groupService.getCurrentGroup() == null) {
            return;
        }
        groupService.getGroupMembers(currentGroup, new ResponseHandler<List<User>>() {
            @Override
            public void onResponse(List<User> response) {
                tvGroupName.setText(currentGroup.getName());
                tvGroupMemberCount.setText(String.format("%s Members", response.size()));
                groupMembersArrayAdapter.setMembers(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Failed to load group members.", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        User clickedUser = (User) adapterView.getItemAtPosition(position);
        if (clickedUser.getId() == null) {
            Intent intent = new Intent(getContext(), InviteActivity.class);
            startActivityForResult(intent, InviteActivity.INVITE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == InviteActivity.INVITE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invite sent successfully.", Toast.LENGTH_LONG);
            toast.show();
        }
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
