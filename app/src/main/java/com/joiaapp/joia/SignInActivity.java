package com.joiaapp.joia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.joiaapp.joia.dto.User;

/**
 * Created by arnell on 1/9/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class SignInActivity extends Activity implements View.OnClickListener {

    private Button btnSubmit;
    private EditText etEmail;
    private EditText etPassword;

    private static final int REGISTER_REQUEST_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register__sign_in);

        btnSubmit = (Button) findViewById(R.id.btnSubmitSignIn);
        btnSubmit.setOnClickListener(this);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnSubmit)) {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.length() > 0 && password.length() > 0) {
                attemptToSignIn(email, password);
            }
        }
    }

    private void attemptToSignIn(String email, String password) {
        UserService userService = UserService.getInstance();
        userService.signIn(email, password, new ResponseHandler<User>() {
            @Override
            public void onResponse(User user) {


                Intent iData = new Intent();
                setResult(Activity.RESULT_OK, iData);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                CharSequence text = "Unable to sign in with the provided credentials!";
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        });


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
