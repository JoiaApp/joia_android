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

import static com.joiaapp.joia.FieldHelper.getFieldText;
import static com.joiaapp.joia.FieldHelper.emptyTextFieldCheck;

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

        //TODO: create a separate activity for choosing? and remove register__choose_group.xml from register__all.xml
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
        GroupService.getInstance().getGroup(getFieldText(etJoinGroupId), getFieldText(etGroupPassword), new RequestHandler<Group>() {
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
        User newUser = new User();
        newUser.setName(getFieldText(etName));
        newUser.setEmail(getFieldText(etEmail));//TODO: validate email
        newUser.setPassword(getFieldText(etPassword));
        UserService userService = UserService.getInstance();
        userService.createUser(newUser, new RequestHandler<User>() {
            @Override
            public void onResponse(User user) {
                System.out.println("Successful user creation!");
                System.out.println("User id: " + user.getId());
                UserService.getInstance().setCurrentUser(user);

                if (groupToJoin.getId() == null) {
                    // create group
                    GroupService.getInstance().createGroup(groupToJoin, new RequestHandler<Group>() {
                        @Override
                        public void onResponse(Group response) {
                            GroupService.getInstance().setCurrentGroup(response);
                            //TODO: still need to join the recently created group, but this callback hell is getting bad. need to reorganize this.
                            Intent iData = new Intent();
                            setResult(Activity.RESULT_OK, iData);
                            finish();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            CharSequence text = "Unable to create group!";
                            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                            toast.show();
                            //TODO: recover
                        }
                    });
                } else {
                    // join group
                    GroupService.getInstance().joinGroup(groupToJoin, user, new RequestHandler<Group>(){
                        @Override
                        public void onResponse(Group response) {
                            GroupService.getInstance().setCurrentGroup(response);

                            Intent iData = new Intent();
                            setResult(Activity.RESULT_OK, iData);
                            finish();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            CharSequence text = "Unable to join group!";
                            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                            toast.show();
                            //TODO: recover
                        }
                    });
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                CharSequence text = "Unable to create user with the provided credentials!";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
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
