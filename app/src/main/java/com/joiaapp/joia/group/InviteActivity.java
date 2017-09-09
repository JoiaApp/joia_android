package com.joiaapp.joia.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.joiaapp.joia.R;
import com.joiaapp.joia.service.GroupService;
import com.joiaapp.joia.service.ServiceFactory;
import com.joiaapp.joia.util.ResponseHandler;
import com.joiaapp.joia.util.SoftKeyboardVisibilityHandler;

/**
 * Created by arnell on 9/1/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class InviteActivity extends Activity implements View.OnClickListener {
    public final static int INVITE_ACTIVITY_REQUEST_CODE = 201;

    private Button btnSubmit;
    private EditText etEmail;
    private ImageView ivOpalHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite);

        btnSubmit = (Button) findViewById(R.id.btnSubmitInvite);
        btnSubmit.setOnClickListener(this);
        etEmail = (EditText) findViewById(R.id.etEmail);
        ivOpalHeader = (ImageView) findViewById(R.id.ivOpalHeader);

        RelativeLayout rlInvite = (RelativeLayout) findViewById(R.id.rlInvite);
        SoftKeyboardVisibilityHandler.hideWhenKeyboardVisible(rlInvite, ivOpalHeader);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmitInvite) {
            GroupService groupService = ServiceFactory.getGroupService();
            groupService.sendInvite(etEmail.getText().toString(), groupService.getCurrentGroup(), new ResponseHandler<String>() {
                @Override
                public void onResponse(String response) {
                    Intent iData = new Intent();
                    setResult(Activity.RESULT_OK, iData);
                    finish();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed to send invite.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }
}
