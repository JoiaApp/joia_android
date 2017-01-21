package com.joiaapp.joia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by arnell on 1/18/2017.
 */

public class InitialActivity extends Activity implements View.OnClickListener {
    private static final int SIGN_IN_ACTIVITY_REQUEST_CODE = 20;
    private static final int REGISTER_ACTIVITY_REQUEST_CODE = 30;

    private Button btnSignIn;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register__select_sign_in);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnSignIn)) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, SIGN_IN_ACTIVITY_REQUEST_CODE);
        } else if (v.equals(btnRegister)) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivityForResult(intent, REGISTER_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            finish();
        }
        if (requestCode == SIGN_IN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

        }
    }
}
