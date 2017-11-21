package com.joiaapp.joia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.joiaapp.joia.dto.User;
import com.joiaapp.joia.service.ServiceFactory;
import com.joiaapp.joia.service.UserService;
import com.joiaapp.joia.util.ResponseHandler;
import com.joiaapp.joia.util.SoftKeyboardVisibilityHandler;

/**
 * Created by arnell on 1/9/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class SignInActivity extends Activity implements View.OnClickListener {
    private ViewFlipper vfSignIn;
    private ImageView ivOpalHeader;

    private ViewGroup vgSignIn;
    private Button btnSubmit;
    private Button btnForgot;
    private EditText etEmail;
    private EditText etPassword;

    private ViewGroup vgForgotPassword;
    private EditText etForgotPasswordEmail;
    private Button btnSubmitResetPassword;


    private static final int REGISTER_REQUEST_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin__all);

        vfSignIn = (ViewFlipper) findViewById(R.id.vfSignIn);
        ivOpalHeader = (ImageView) findViewById(R.id.ivOpalHeader);

        vgSignIn = (ViewGroup) findViewById(R.id.loSignIn);
        btnSubmit = (Button) findViewById(R.id.btnSubmitSignIn);
        btnSubmit.setOnClickListener(this);
        btnForgot = (Button) findViewById(R.id.btnForgotPassword);
        btnForgot.setOnClickListener(this);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        vgForgotPassword = (ViewGroup) findViewById(R.id.loForgotPassword);
        etForgotPasswordEmail = (EditText) vgForgotPassword.findViewById(R.id.etEmail);
        btnSubmitResetPassword = (Button) vgForgotPassword.findViewById(R.id.btnSubmitResetPassword);
        btnSubmitResetPassword.setOnClickListener(this);

        LinearLayout llSignInAll = (LinearLayout) findViewById(R.id.llSignInAll);
        SoftKeyboardVisibilityHandler.hideWhenKeyboardVisible(llSignInAll, ivOpalHeader);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnSubmit)) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.length() > 0 && password.length() > 0) {
                attemptToSignIn(email, password);
            }
        } else if (v.equals(btnForgot)) {
            etForgotPasswordEmail.setText(etEmail.getText().toString());
            setDisplayedView(vgForgotPassword);
        } else if (v.equals(btnSubmitResetPassword)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Not yet implemented!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        switch (vfSignIn.getCurrentView().getId()) {
            case R.id.loSignIn:
                super.onBackPressed();
                break;
            case R.id.loForgotPassword:
                setDisplayedView(vgSignIn);
                break;
        }
    }

    private void attemptToSignIn(String email, String password) {
        UserService userService = ServiceFactory.getUserService();
        userService.signIn(email, password, new ResponseHandler<User>() {
            @Override
            public void onResponse(User user) {
                Intent iData = new Intent();
                setResult(Activity.RESULT_OK, iData);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                CharSequence text = "Unable to sign in with the provided credentials!";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void setDisplayedView(View view) {
        int viewIndex = vfSignIn.indexOfChild(view);
        vfSignIn.setDisplayedChild(viewIndex);
    }


    //new SignInTask().execute(email, password);
//    private class SignInTask extends AsyncTask<String, String, User> {
//        @Override
//        protected User doInBackground(String... params) {
//
//        }
//
//        @Override
//        protected void onPostExecute(User user) {
//            System.out.println("asdf");
//        }
//    }
}
