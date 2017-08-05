package com.joiaapp.joia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.joiaapp.joia.dto.Group;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.service.ServiceFactory;
import com.joiaapp.joia.service.UserService;
import com.joiaapp.joia.util.SoftKeyboardVisibilityHandler;

import java.util.Arrays;

import static com.joiaapp.joia.FieldHelper.emptyTextFieldCheck;
import static com.joiaapp.joia.FieldHelper.getFieldText;

/**
 * Created by arnell on 1/10/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {

    ViewFlipper vfRegister;

    // register__choose_group.xml
    private ViewGroup vgChooseGroup;
    private Button btnJoinAGroup;
    private Button btnCreateAGroup;

    // register__join_group.xml
    private ViewGroup vgJoinGroup;
    private EditText etJoinGroupId;
    private EditText etGroupPassword;
    private Button btnSubmitJoinGroup;

    // register__create_user.xml
    private ViewGroup vgCreateUser;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSubmitCreateUser;

    // register__create_group.xml
    private ViewGroup vgCreateGroup;
    private EditText etCreateGroupName;
    private Button btnSubmitCreateGroup;

    private Group groupToJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register__all);

        vfRegister = (ViewFlipper) findViewById(R.id.vfRegister);

        // register__choose_group.xml
        vgChooseGroup = (ViewGroup) findViewById(R.id.loChooseGroup);
        btnJoinAGroup = (Button) vgChooseGroup.findViewById(R.id.btnJoinAGroup);
        btnJoinAGroup.setOnClickListener(this);
        btnCreateAGroup = (Button) vgChooseGroup.findViewById(R.id.btnCreateAGroup);
        btnCreateAGroup.setOnClickListener(this);

        // register__join_group.xml
        vgJoinGroup = (ViewGroup) findViewById(R.id.loJoinGroup);
        etJoinGroupId = (EditText) vgJoinGroup.findViewById(R.id.etGroupId);
        etGroupPassword = (EditText) vgJoinGroup.findViewById(R.id.etGroupPassword);
        btnSubmitJoinGroup = (Button) vgJoinGroup.findViewById(R.id.btnSubmitJoinGroup);
        btnSubmitJoinGroup.setOnClickListener(this);

        // register__create_user.xml
        vgCreateUser = (ViewGroup) findViewById(R.id.loCreateUser);
        etName = (EditText) vgCreateUser.findViewById(R.id.etName);
        etEmail = (EditText) vgCreateUser.findViewById(R.id.etEmail);
        etPassword = (EditText) vgCreateUser.findViewById(R.id.etPassword);
        btnSubmitCreateUser = (Button) vgCreateUser.findViewById(R.id.btnSubmitCreateUser);
        btnSubmitCreateUser.setOnClickListener(this);

        // register__create_group.xml
        vgCreateGroup = (ViewGroup) findViewById(R.id.loCreateGroup);
        etCreateGroupName = (EditText) vgCreateGroup.findViewById(R.id.etGroupName);
        btnSubmitCreateGroup = (Button) vgCreateGroup.findViewById(R.id.btnSubmitCreateGroup);
        btnSubmitCreateGroup.setOnClickListener(this);

        View llRegisterAll = findViewById(R.id.llRegisterAll);
        SoftKeyboardVisibilityHandler.hideWhenKeyboardVisible(llRegisterAll, Arrays.asList(
                findViewById(R.id.ivOpalHeader),
                findViewById(R.id.tvChooseGroupExplanation),
                findViewById(R.id.tvCreateGroupExplanation),
                findViewById(R.id.tvCreateUserExplanation),
                findViewById(R.id.tvJoinGroupExplanation)
        ));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnJoinAGroup:
                onJoinAGroup();
                break;
            case R.id.btnCreateAGroup:
                onCreateAGroup();
                break;
            case R.id.btnSubmitJoinGroup:
                onSubmitJoinGroup();
                break;
            case R.id.btnSubmitCreateUser:
                onSubmitCreateUser();
                break;
            case R.id.btnSubmitCreateGroup:
                onSubmitCreateGroup();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (vfRegister.getCurrentView().getId()) {
            case R.id.loChooseGroup:
                super.onBackPressed();
                break;
            case R.id.loJoinGroup:
                setDisplayedView(vgChooseGroup);
                break;
            case R.id.loCreateUser:
                if (groupToJoin.getId() == null) {
                    setDisplayedView(vgCreateGroup);
                } else {
                    setDisplayedView(vgJoinGroup);
                }
                break;
            case R.id.loCreateGroup:
                setDisplayedView(vgChooseGroup);
                break;
        }
    }

    private void onJoinAGroup() {
        setDisplayedView(vgJoinGroup);
    }

    private void onCreateAGroup() {
        setDisplayedView(vgCreateGroup);
    }

    private void onSubmitJoinGroup() {
        if (emptyTextFieldCheck("Required", etJoinGroupId, etGroupPassword)) {
            return;
        }
        ServiceFactory.getGroupService().getGroup(getFieldText(etJoinGroupId), getFieldText(etGroupPassword), new ResponseHandler<Group>() {
            @Override
            public void onResponse(Group response) {
                groupToJoin = response;
                setDisplayedView(vgCreateUser);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                CharSequence text = "Invalid group id or password!";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void onSubmitCreateUser() {
        if (emptyTextFieldCheck("Required", etName, etEmail, etPassword)) {
            return;
        }
        final User newUser = new User();
        newUser.setName(getFieldText(etName));
        newUser.setEmail(getFieldText(etEmail));//TODO: validate email
        newUser.setPassword(getFieldText(etPassword));
        UserService userService = ServiceFactory.getUserService();
        userService.createUserInGroup(newUser, groupToJoin, new ResponseHandler<User>() {
            @Override
            public void onResponse(User user) {
                onCreateUserSuccessCallback(user);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: recover
                CharSequence text;
                User user = ServiceFactory.getUserService().getCurrentUser();
                Group group = ServiceFactory.getGroupService().getCurrentGroup();
                if (user == null) {
                    text = "Unable to create user with the provided credentials!";
                } else if (group == null) {
                    text = "Unable to create group!";
                } else {
                    text = "Unable to join newly created group!";
                }
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void onCreateUserSuccessCallback(User user) {
        Intent iData = new Intent();
        setResult(Activity.RESULT_OK, iData);
        finish();
    }

    private void onSubmitCreateGroup() {
        if (emptyTextFieldCheck("Required", etCreateGroupName)) {
            return;
        }
        Group group = new Group();
        group.setName(getFieldText(etCreateGroupName));
        groupToJoin = group;

        setDisplayedView(vgCreateUser);
    }

    private void setDisplayedView(View view) {
        int viewIndex = vfRegister.indexOfChild(view);
        vfRegister.setDisplayedChild(viewIndex);
    }
}
