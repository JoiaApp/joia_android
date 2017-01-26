package com.joiaapp.joia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

/**
 * Created by arnell on 1/10/2017.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {

    ViewFlipper vfRegister;

    // register__choose_group.xml
    private ViewGroup loChooseGroup;
    private Button btnJoinAGroup;
    private Button btnCreateAGroup;

    // register__join_group.xml
    private ViewGroup loJoinGroup;
    private EditText etJoinGroupId;
    private EditText etGroupPassword;
    private Button btnSubmitJoinGroup;

    // register__create_user.xml
    private ViewGroup loCreateUser;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSubmitCreateUser;

    // register__create_group.xml
    private ViewGroup loCreateGroup;
    private EditText etCreateGroupId;
    private Button btnSubmitCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register__all);

        vfRegister = (ViewFlipper) findViewById(R.id.vfRegister);

        //TODO: create a separate activity for choosing? and remove register__choose_group.xml from register__all.xml
        // register__choose_group.xml
        loChooseGroup = (ViewGroup) findViewById(R.id.loChooseGroup);
        btnJoinAGroup = (Button) loChooseGroup.findViewById(R.id.btnJoinAGroup);
        btnJoinAGroup.setOnClickListener(this);
        btnCreateAGroup = (Button) loChooseGroup.findViewById(R.id.btnCreateAGroup);
        btnCreateAGroup.setOnClickListener(this);

        // register__join_group.xml
        loJoinGroup = (ViewGroup) findViewById(R.id.loJoinGroup);
        etJoinGroupId = (EditText) loJoinGroup.findViewById(R.id.etGroupId);
        etGroupPassword = (EditText) loJoinGroup.findViewById(R.id.etGroupPassword);
        btnSubmitJoinGroup = (Button) loJoinGroup.findViewById(R.id.btnSubmitJoinGroup);
        btnSubmitJoinGroup.setOnClickListener(this);

        // register__create_user.xml
        loCreateUser = (ViewGroup) findViewById(R.id.loCreateUser);
        etName = (EditText) loCreateUser.findViewById(R.id.etName);
        etEmail = (EditText) loCreateUser.findViewById(R.id.etEmail);
        etPassword = (EditText) loCreateUser.findViewById(R.id.etPassword);
        btnSubmitCreateUser = (Button) loCreateUser.findViewById(R.id.btnSubmitCreateUser);
        btnSubmitCreateUser.setOnClickListener(this);

        // register__create_group.xml
        loCreateGroup = (ViewGroup) findViewById(R.id.loCreateGroup);
        etCreateGroupId = (EditText) loCreateGroup.findViewById(R.id.etGroupId);
        btnSubmitCreateGroup = (Button) loCreateGroup.findViewById(R.id.btnSubmitCreateGroup);
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
        setDisplayedView(loJoinGroup);
    }

    private void onCreateAGroup() {
        setDisplayedView(loCreateGroup);
    }

    private void onSubmitJoinGroup() {
        setDisplayedView(loCreateUser);
    }

    private void onSubmitCreateUser() {
        if (etEmail.getEditableText().length() > 0 &&
                etPassword.getEditableText().length() > 0) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    private void onSubmitCreateGroup() {
        setDisplayedView(loCreateUser);
    }

    private void setDisplayedView(View view) {
        int viewIndex = vfRegister.indexOfChild(view);
        vfRegister.setDisplayedChild(viewIndex);
    }
}
