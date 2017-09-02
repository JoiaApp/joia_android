package com.joiaapp.joia.group;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.joiaapp.joia.R;
import com.joiaapp.joia.util.SoftKeyboardVisibilityHandler;

/**
 * Created by arnell on 9/1/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class InviteActivity extends Activity implements View.OnClickListener {

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

        RelativeLayout rlRegisterSignIn = (RelativeLayout) findViewById(R.id.rlInvite);
        SoftKeyboardVisibilityHandler.hideWhenKeyboardVisible(rlRegisterSignIn, ivOpalHeader);
    }

    @Override
    public void onClick(View v) {

    }
}
