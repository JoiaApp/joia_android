package com.joiaapp.joia.write;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.joiaapp.joia.R;
import com.joiaapp.joia.ResponseHandler;
import com.joiaapp.joia.dto.Group;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.group.GroupMembersArrayAdapter;
import com.joiaapp.joia.service.GroupService;
import com.joiaapp.joia.service.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnell on 5/15/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class TagFriendsActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView lvTagFriends;
    private Button btnDoneTagging;
    private GroupMembersArrayAdapter groupMembersArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write__tag_friends);
        lvTagFriends = (ListView) findViewById(R.id.lvTagFriends);
        btnDoneTagging =  (Button) findViewById(R.id.btnDoneTagging);

        groupMembersArrayAdapter = new GroupMembersArrayAdapter(this, new ArrayList<User>(), true);
        lvTagFriends.setAdapter(groupMembersArrayAdapter);
        lvTagFriends.setOnItemClickListener(this);

        btnDoneTagging.setOnClickListener(this);

        GroupService groupService = ServiceFactory.getGroupService();
        final Group group = groupService.getCurrentGroup();
        if (!group.getMembers().isEmpty()) {
            setArrayAdapterMembers(group.getMembers());
        } else {
            groupService.getGroupMembers(group, new ResponseHandler<List<User>>() {
                @Override
                public void onResponse(List<User> response) {
                    setArrayAdapterMembers(response);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed to load group members.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }

    private void setArrayAdapterMembers(List<User> members) {
        List<User> selectableUsers = new ArrayList<>();
        Integer currentUserId = ServiceFactory.getUserService().getCurrentUser().getId();
        for (User member : members) {
            if (!member.getId().equals(currentUserId)) {
                selectableUsers.add(member);
            }
        }
        groupMembersArrayAdapter.setMembers(selectableUsers);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnDoneTagging) {
            Intent iData = new Intent();
            iData.putExtra("TAGGED_FRIENDS", groupMembersArrayAdapter.getSelected().toArray());
            setResult(Activity.RESULT_OK, iData);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User clickedUser = (User) parent.getItemAtPosition(position);
        groupMembersArrayAdapter.toggleSelected(clickedUser);
    }
}
