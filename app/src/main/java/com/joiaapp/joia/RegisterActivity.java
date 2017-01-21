package com.joiaapp.joia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by arnell on 1/10/2017.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {

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

        //TODO: create a separate activity for choosing? and remove register__choose_group.xml from register__all.xml
//        // register__choose_group.xml
//        loChooseGroup = (ViewGroup) findViewById(R.id.loChooseGroup);
//        btnJoinAGroup = (Button) loChooseGroup.findViewById(R.id.btnJoinAGroup);
//        btnCreateAGroup = (Button) loChooseGroup.findViewById(R.id.btnCreateAGroup);

        // register__join_group.xml
        loJoinGroup = (ViewGroup) findViewById(R.id.loJoinGroup);
        etJoinGroupId = (EditText) loJoinGroup.findViewById(R.id.etGroupId);
        etGroupPassword = (EditText) loJoinGroup.findViewById(R.id.etGroupPassword);
        btnSubmitJoinGroup = (Button) loJoinGroup.findViewById(R.id.btnSubmitJoinGroup);

        // register__create_user.xml
        loCreateUser = (ViewGroup) findViewById(R.id.loCreateUser);
        etName = (EditText) loCreateUser.findViewById(R.id.etName);
        etEmail = (EditText) loCreateUser.findViewById(R.id.etEmail);
        etPassword = (EditText) loCreateUser.findViewById(R.id.etPassword);
        btnSubmitCreateUser = (Button) loCreateUser.findViewById(R.id.btnSubmitCreateUser);

        // register__create_group.xml
        loCreateGroup = (ViewGroup) findViewById(R.id.loCreateGroup);
        etCreateGroupId = (EditText) loCreateGroup.findViewById(R.id.etGroupId);
        btnSubmitCreateGroup = (Button) loCreateUser.findViewById(R.id.btnSubmitCreateGroup);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    private void onSubmitJoinGroup() {

    }

    private void onSubmitCreateUser() {
        if (etEmail.getEditableText().length() > 0 &&
                etPassword.getEditableText().length() > 0) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    private void onSubmitCreateGroup() {

    }
}
