package com.joiaapp.joia.read;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.joiaapp.joia.DatabaseHelper;
import com.joiaapp.joia.MainAppFragment;
import com.joiaapp.joia.R;
import com.joiaapp.joia.dto.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnell on 11/6/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class ReadFragment extends Fragment implements View.OnClickListener, MainAppFragment {
    ListView lvReadMessages;
    MessageJournalArrayAdapter messageJournalArrayAdapter;
    List<Message> messages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read, container, false);

        lvReadMessages = (ListView) rootView.findViewById(R.id.lvReadMessages);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
        messages.addAll(dbHelper.getMessages());
        // TODO: load messages from server: GroupService.getInstance().getGroupMessages();
        messageJournalArrayAdapter = new MessageJournalArrayAdapter(getActivity(), messages);
        lvReadMessages.setAdapter(messageJournalArrayAdapter);
        return rootView;
    }

    public void refreshView() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
        List<Message> updatedMessages = dbHelper.getMessages();
        messageJournalArrayAdapter.setMessages(updatedMessages);
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
