package com.joiaapp.joia.settings;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.joiaapp.joia.R;

/**
 * Created by arnell on 9/15/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class ProfileActivity extends Activity implements View.OnClickListener {

    Button btnBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        btnBirthday = (Button) findViewById(R.id.btnBirthday);
        btnBirthday.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBirthday) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }
    }
}
