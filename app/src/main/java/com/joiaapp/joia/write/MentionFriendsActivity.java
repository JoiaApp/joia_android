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
import com.joiaapp.joia.dto.Mention;
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

public class MentionFriendsActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String MENTIONED = "MENTIONED";
    private ListView lvMentionFriends;
    private Button btnDoneMentioning;
    private GroupMembersArrayAdapter groupMembersArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write__mention_friends);
        lvMentionFriends = (ListView) findViewById(R.id.lvMentionFriends);
        btnDoneMentioning =  (Button) findViewById(R.id.btnDoneMentioning);

        groupMembersArrayAdapter = new GroupMembersArrayAdapter(this, new ArrayList<User>(), true);
        lvMentionFriends.setAdapter(groupMembersArrayAdapter);
        lvMentionFriends.setOnItemClickListener(this);

        btnDoneMentioning.setOnClickListener(this);

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
        if (v.getId() == R.id.btnDoneMentioning) {
            Intent iData = new Intent();
            ArrayList<Mention> mentions = new ArrayList<>();
            for (User user : groupMembersArrayAdapter.getSelected()) {
                mentions.add(new Mention(user));
            }
            iData.putExtra(MENTIONED, mentions);
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
