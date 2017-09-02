package com.joiaapp.joia.util;

import android.widget.EditText;

/**
 * Created by arnell on 3/24/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class FieldHelper {
    public static boolean emptyTextFieldCheck(String errorText, EditText... editTextFields) {
        boolean oneWasEmpty = false;
        for (EditText editText : editTextFields) {
            boolean fieldEmpty = editText.getText().toString().isEmpty();
            if (fieldEmpty) {
                editText.setError(errorText);
            }
            oneWasEmpty = oneWasEmpty || fieldEmpty;
        }
        return oneWasEmpty;
    }

    public static String getFieldText(EditText editText) {
        return editText.getText().toString();
    }
}
