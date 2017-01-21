package com.joiaapp.joia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by arnell on 10/28/2016.
 */

public class WriteFragment extends Fragment implements View.OnClickListener, MainAppFragment {
    private View rootView;
    private ViewFlipper viewFlipper;
    private RelativeLayout rlWriteIntro;
    private LinearLayout llWriteForm;
    private RelativeLayout rlWriteReview;
    private RelativeLayout rlPublishSuccess;

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

    private static final List<String> PROMPT_ITEMS = Arrays.asList("I smiled today because ...", "I felt loved today because ...", "My day changed when ...", "I laughed today when ...", "I appreciate that ...", "(none)");

    //Nav Settings
    private String navTitle = "Write a Message";
    private boolean navBackButtonVisible = true;
    private boolean navNextButtonVisible = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_write, container, false);

        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.vfWrite);
        rlWriteIntro = (RelativeLayout) rootView.findViewById(R.id.rlWriteIntro);
        llWriteForm = (LinearLayout) rootView.findViewById(R.id.llWriteForm);
        rlWriteReview = (RelativeLayout) rootView.findViewById(R.id.rlWriteReview);
        rlPublishSuccess = (RelativeLayout) rootView.findViewById(R.id.rlPublishSuccess);

        setDisplayedView(rlWriteIntro);

        btnWriteGetStarted = (Button) rootView.findViewById(R.id.btnWriteGetStarted);
        btnWriteGetStarted.setOnClickListener(this);

        spPrompt = (Spinner) rootView.findViewById(R.id.spPrompt);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_spinner_dropdown_item, PROMPT_ITEMS);
        spPrompt.setAdapter(adapter);


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
        if (view.equals(rlWriteIntro) || view.equals(rlPublishSuccess)) {
            navBackButtonVisible = false;
            navNextButtonVisible = false;
        } else if (view.equals(llWriteForm)) {
            navBackButtonVisible = true;
            navNextButtonVisible = true;
        } else if (view.equals(rlWriteReview)) {
            navBackButtonVisible = true;
            navNextButtonVisible = false;
        }
        ((MainActivity) getActivity()).updateNavigationBar(null);
    }

    private Message getCurrentMessageFromView() {
        String messageText = etMessageText.getEditableText().toString().trim();
        String selectedPrompt = (String) spPrompt.getSelectedItem();
        if (messageText.isEmpty()) {
            return null;
        } else {
            Message message = new Message();
            message.setUserId(1); //TODO: current user
            message.setDate(new Date());
            message.setPromptText(selectedPrompt);
            message.setText(messageText);
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
                tvWriteMessageIndex.setText(String.format("%s of %s Today", messagesInProgress.size() + 1, TOTAL_MESSAGES));
                if (!unfinishedMessages.isEmpty()) {
                    Message lastUnfinished = unfinishedMessages.remove(unfinishedMessages.size()-1);
                    populateWriteView(lastUnfinished);
                }
            } else {
                reviewMessages();
            }
        }
    }

    public void onNavBackButtonTap() {
        if (viewFlipper.getDisplayedChild() == viewFlipper.indexOfChild(rlWriteReview)) {
            messagesInProgress.addAll(messageReviewArrayAdapter.getMessages());
            messageReviewArrayAdapter.clear();
            setDisplayedView(llWriteForm);
        }
        else {
            Message currentMessage = getCurrentMessageFromView();
            if (currentMessage != null) {
                unfinishedMessages.add(currentMessage);
            }
        }
        clearWriteFields();
        if (messagesInProgress.isEmpty()) {
            setDisplayedView(rlWriteIntro);
        } else {
            Message lastMessage = messagesInProgress.remove(messagesInProgress.size()-1);
            populateWriteView(lastMessage);
        }
    }

    private void populateWriteView(Message message) {
        int promptIdx = PROMPT_ITEMS.indexOf(message.getPromptText());
        spPrompt.setSelection(promptIdx);
        etMessageText.setText(message.getText());
    }

    private void reviewMessages() {
        setDisplayedView(rlWriteReview);
        lvReviewMessages = (ListView) rootView.findViewById(R.id.lvReviewMessages);
        messageReviewArrayAdapter = new MessageReviewArrayAdapter(getActivity(), messagesInProgress);
        lvReviewMessages.setAdapter(messageReviewArrayAdapter);
        btnPublishToGroup = (Button) rootView.findViewById(R.id.btnPublishToGroup);
        btnPublishToGroup.setOnClickListener(this);
        messagesInProgress.clear();
    }

    private void publishReviewedMessages() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
        for (Message m : messageReviewArrayAdapter.getMessages()) {
            dbHelper.createMessage(m);
        }
        setDisplayedView(rlPublishSuccess);
    }

    private void clearWriteFields() {
        etMessageText.getEditableText().clear();
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
                setDisplayedView(llWriteForm);
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