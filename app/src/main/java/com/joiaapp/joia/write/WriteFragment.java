package com.joiaapp.joia.write;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.joiaapp.joia.MainActivity;
import com.joiaapp.joia.MainAppFragment;
import com.joiaapp.joia.R;
import com.joiaapp.joia.ResponseHandler;
import com.joiaapp.joia.dto.Message;
import com.joiaapp.joia.dto.Prompt;
import com.joiaapp.joia.service.GroupService;
import com.joiaapp.joia.service.ServiceFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by arnell on 10/28/2016.
 * Copyright 2017 Joia. All rights reserved.
 */

public class WriteFragment extends Fragment implements View.OnClickListener, MainAppFragment {
    private static final int TAG_FRIENDS_REQUEST_CODE = 9999;
    private View rootView;
    private ViewFlipper viewFlipper;
    private ViewGroup vgIntro;
    private ViewGroup vgForm;
    private ViewGroup vgTagFriends;
    private ViewGroup vgReview;
    private ViewGroup vgPublishSuccess;

    private Button btnWriteGetStarted;

    private Spinner spPrompt;
    private EditText etMessageText;
    private TextView tvWriteMessageIndex;

    private Button btnTagFriends;

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

        ServiceFactory.getPromptService().getPrompts(new ResponseHandler<List<Prompt>>() {
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
        // TODO: put this somewhere else
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                ((MainActivity) getActivity()).setTabLayoutHidden(heightDiff > 100);
            }
        });
        tvWriteMessageIndex = (TextView) rootView.findViewById(R.id.tvWriteMessageIndex);
        tvWriteMessageIndex.setText(String.format("%s of %s Today", 1, TOTAL_MESSAGES));

        btnTagFriends = (Button) rootView.findViewById(R.id.btnTagFriends);
        btnTagFriends.setOnClickListener(this);

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
            message.setUserId(ServiceFactory.getUserService().getCurrentUser().getId());
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

    public void onTagFriendsButtonTap() {
//        ViewGroup sceneRoot = (ViewGroup) rootView;
//        Scene writeScene = Scene.getSceneForLayout(sceneRoot, R.layout.write__form, getContext());
//        Scene tagFriendsScene = new Scene(sceneRoot, vgIntro);
//        Transition transition = new Slide(Gravity.TOP);
//        TransitionManager.beginDelayedTransition(sceneRoot, transition);

        Intent intent = new Intent(getContext(), TagFriendsActivity.class);
        startActivityForResult(intent, TAG_FRIENDS_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAG_FRIENDS_REQUEST_CODE && resultCode == RESULT_OK) {

        }
    }

    private void reviewMessages() {
        setDisplayedView(vgReview);
        lvReviewMessages = (ListView) rootView.findViewById(R.id.lvReviewMessages);
        messageReviewArrayAdapter = new MessageReviewArrayAdapter(getActivity(), messagesInProgress);
        lvReviewMessages.setAdapter(messageReviewArrayAdapter);
        btnPublishToGroup = (Button) rootView.findViewById(R.id.btnPublishToGroup);
        btnPublishToGroup.setOnClickListener(this);
        messagesInProgress.clear();
        // TODO: put this somewhere else
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
    }

    private void publishReviewedMessages() {
        final GroupService groupService = ServiceFactory.getGroupService();
        groupService.publishGroupMessages(groupService.getCurrentGroup(), messageReviewArrayAdapter.getMessages(), new ResponseHandler<List<Message>>() {
            @Override
            public void onResponse(List<Message> response) {
                if (response.isEmpty()) {
                    setDisplayedView(vgPublishSuccess);
                    TextView tvPublishSuccessMessage = (TextView) rootView.findViewById(R.id.tvPublishSuccessMessage);
                    tvPublishSuccessMessage.setText(String.format("Your messages have been published to %s. There have been %s new messages posted today in your group journal.",
                            groupService.getCurrentGroup().getName(), groupService.getNumberOfNewMessagesTodayFromCache()));

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
            case R.id.btnTagFriends:
                onTagFriendsButtonTap();
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