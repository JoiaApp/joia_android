package com.joiaapp.joia.read;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.joiaapp.joia.GroupService;
import com.joiaapp.joia.MainAppFragment;
import com.joiaapp.joia.R;
import com.joiaapp.joia.RequestHandler;
import com.joiaapp.joia.dto.Message;

import java.util.List;

/**
 * Created by arnell on 11/6/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class ReadFragment extends Fragment implements View.OnClickListener, MainAppFragment {
    ListView lvReadMessages;
    MessageJournalArrayAdapter messageJournalArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read, container, false);

        lvReadMessages = (ListView) rootView.findViewById(R.id.lvReadMessages);
        messageJournalArrayAdapter = new MessageJournalArrayAdapter(getActivity());
        lvReadMessages.setAdapter(messageJournalArrayAdapter);
        refreshView();
        return rootView;
    }

    public void refreshView() {
        GroupService groupService = GroupService.getInstance();
        groupService.getGroupMessages(groupService.getCurrentGroup(), new RequestHandler<List<Message>>() {
            @Override
            public void onResponse(List<Message> response) {
                messageJournalArrayAdapter.setMessages(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Failed to load messages.", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public String getNavBarTitle() {
        return "Journal";
    }

    @Override
    public boolean isNavBarBackButtonVisible() {
        return false;
    }

    @Override
    public boolean isNavBarNextButtonVisible() {
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}
