package com.joiaapp.joia.write;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.joiaapp.joia.GroupService;
import com.joiaapp.joia.MainActivity;
import com.joiaapp.joia.MainAppFragment;
import com.joiaapp.joia.PromptService;
import com.joiaapp.joia.R;
import com.joiaapp.joia.RequestHandler;
import com.joiaapp.joia.UserService;
import com.joiaapp.joia.dto.Message;
import com.joiaapp.joia.dto.Prompt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arnell on 10/28/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class WriteFragment extends Fragment implements View.OnClickListener, MainAppFragment {
    private View rootView;
    private ViewFlipper viewFlipper;
    private ViewGroup vgIntro;
    private ViewGroup vgForm;
    private ViewGroup vgReview;
    private ViewGroup vgPublishSuccess;

    private Button btnWriteGetStarted;

    private Spinner spPrompt;
    private EditText etMessageText;
    private TextView tvWriteMessageIndex;

    private MessageReviewArrayAdapter messageReviewArrayAdapter;
    private ListView lvReviewMessages;
    private Button btnPublishToGroup;

    private Button btnGoToJournal;

    private List<Message> messagesInProgress = new ArrayList<>();
    private List<Message> unfinishedMessages = new ArrayList<>();
    private static final int TOTAL_MESSAGES = 3;

    ArrayAdapter<Prompt> promptArrayAdapter;

    //Nav Settings
    private String navTitle = "Write a Message";
    private boolean navBackButtonVisible = true;
    private boolean navNextButtonVisible = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_write, container, false);

        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.vfWrite);
        vgIntro = (ViewGroup) rootView.findViewById(R.id.vgIntro);
        vgForm = (ViewGroup) rootView.findViewById(R.id.vgForm);
        vgReview = (ViewGroup) rootView.findViewById(R.id.vgReview);
        vgPublishSuccess = (ViewGroup) rootView.findViewById(R.id.vgPublishSuccess);

        setDisplayedView(vgIntro);

        btnWriteGetStarted = (Button) rootView.findViewById(R.id.btnWriteGetStarted);
        btnWriteGetStarted.setOnClickListener(this);

        spPrompt = (Spinner) rootView.findViewById(R.id.spPrompt);

        promptArrayAdapter = new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<Prompt>());
        spPrompt.setAdapter(promptArrayAdapter);

        PromptService.getInstance().getPrompts(new RequestHandler<List<Prompt>>() {
            @Override
            public void onResponse(List<Prompt> response) {
                // TODO: add a "(none)" response
                promptArrayAdapter.addAll(response);
                promptArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Failed to load prompts.", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        etMessageText = (EditText) rootView.findViewById(R.id.etMessageText);
        tvWriteMessageIndex = (TextView) rootView.findViewById(R.id.tvWriteMessageIndex);
        tvWriteMessageIndex.setText(String.format("%s of %s Today", 1, TOTAL_MESSAGES));

        btnGoToJournal = (Button) rootView.findViewById(R.id.btnGoToJournal);
        btnGoToJournal.setOnClickListener(this);

        return rootView;
    }

    private void setDisplayedView(View view) {
        int viewIndex = viewFlipper.indexOfChild(view);
        viewFlipper.setDisplayedChild(viewIndex);
        if (view.equals(vgIntro) || view.equals(vgPublishSuccess)) {
            navBackButtonVisible = false;
            navNextButtonVisible = false;
        } else if (view.equals(vgForm)) {
            navBackButtonVisible = true;
            navNextButtonVisible = true;
        } else if (view.equals(vgReview)) {
            navBackButtonVisible = true;
            navNextButtonVisible = false;
        }
        ((MainActivity) getActivity()).updateNavigationBar(null);
    }

    private Message getCurrentMessageFromView() {
        String messageText = etMessageText.getText().toString().trim();
        Prompt selectedPrompt = (Prompt) spPrompt.getSelectedItem();
        if (messageText.isEmpty()) {
            return null;
        } else {
            Message message = new Message();
            message.setUserId(UserService.getInstance().getCurrentUser().getId());
            message.setCreatedAt(new Date());
            message.setText(selectedPrompt, messageText);
            return message;
        }
    }

    public void onNavNextButtonTap() {
        Message message = getCurrentMessageFromView();
        clearWriteFields();
        if (message == null && !messagesInProgress.isEmpty() && unfinishedMessages.isEmpty()) {
            reviewMessages();
        } else {
            if (message != null) {
                messagesInProgress.add(message);
            }
            if (messagesInProgress.size() < TOTAL_MESSAGES) {
                if (!unfinishedMessages.isEmpty()) {
                    Message lastUnfinished = unfinishedMessages.remove(unfinishedMessages.size()-1);
                    populateWriteView(lastUnfinished);
                }
                updateMessageIndexLabel();
            } else {
                reviewMessages();
            }
        }
    }

    public void onNavBackButtonTap() {
        if (viewFlipper.getDisplayedChild() == viewFlipper.indexOfChild(vgReview)) {
            messagesInProgress.addAll(messageReviewArrayAdapter.getMessages());
            messageReviewArrayAdapter.clear();
            setDisplayedView(vgForm);
        }
        else {
            Message currentMessage = getCurrentMessageFromView();
            if (currentMessage != null) {
                unfinishedMessages.add(currentMessage);
            }
        }
        clearWriteFields();
        if (messagesInProgress.isEmpty()) {
            setDisplayedView(vgIntro);
        } else {
            Message lastMessage = messagesInProgress.remove(messagesInProgress.size()-1);
            populateWriteView(lastMessage);
            updateMessageIndexLabel();
        }
    }

    private void populateWriteView(Message message) {
        int promptIdx = promptArrayAdapter.getPosition(message.getPromptObj());
        spPrompt.setSelection(promptIdx);
        etMessageText.setText(message.getUserText());
    }

    private void updateMessageIndexLabel() {
        tvWriteMessageIndex.setText(String.format("%s of %s Today", messagesInProgress.size() + 1, TOTAL_MESSAGES));
    }

    private void reviewMessages() {
        setDisplayedView(vgReview);
        lvReviewMessages = (ListView) rootView.findViewById(R.id.lvReviewMessages);
        messageReviewArrayAdapter = new MessageReviewArrayAdapter(getActivity(), messagesInProgress);
        lvReviewMessages.setAdapter(messageReviewArrayAdapter);
        btnPublishToGroup = (Button) rootView.findViewById(R.id.btnPublishToGroup);
        btnPublishToGroup.setOnClickListener(this);
        messagesInProgress.clear();
    }

    private void publishReviewedMessages() {
        GroupService groupService = GroupService.getInstance();
        groupService.publishGroupMessages(groupService.getCurrentGroup(), messageReviewArrayAdapter.getMessages(), new RequestHandler<List<Message>>() {
            @Override
            public void onResponse(List<Message> response) {
                if (response.isEmpty()) {
                    setDisplayedView(vgPublishSuccess);
                } else {
                    //TODO: handle failed publishes
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Failed to publish messages.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Not used for GroupService.publishGroupMessages()
            }
        });
    }

    private void clearWriteFields() {
        etMessageText.getText().clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNavBackButton:
                onNavBackButtonTap();
                break;
            case R.id.btnNavNextButton:
                onNavNextButtonTap();
                break;
            case R.id.btnWriteGetStarted:
                setDisplayedView(vgForm);
                onNavNextButtonTap();
                break;
            case R.id.btnPublishToGroup:
                publishReviewedMessages();
                break;
            case R.id.btnGoToJournal:
                ((MainActivity)getActivity()).showJournalView();
                break;
        }
    }

    @Override
    public String getNavBarTitle() {
        return navTitle;
    }

    @Override
    public boolean isNavBarBackButtonVisible() {
        return navBackButtonVisible;
    }

    @Override
    public boolean isNavBarNextButtonVisible() {
        return navNextButtonVisible;
    }
}