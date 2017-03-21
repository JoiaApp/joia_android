package com.joiaapp.joia;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by arnell on 2/1/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class CustomFontTextView extends TextView {
    private static final String sScheme = "http://schemas.android.com/apk/res-auto";
    private static final String sAttribute = "customFont";

    enum CustomFont {
        OPENSANS_REGULAR("fonts/OpenSans-Regular.ttf"),
        OPENSANS_SEMIBOLD("fonts/OpenSans-Semibold.ttf");

        private final String fileName;

        CustomFont(String fileName) {
            this.fileName = fileName;
        }

        static CustomFont fromString(String fontName) {
            return CustomFont.valueOf(fontName.toUpperCase(Locale.US));
        }

        public Typeface asTypeface(Context context) {
            return Typeface.createFromAsset(context.getAssets(), fileName);
        }
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView, 0, 0);
            if (typedArray.length() == 0) {
            //final String fontName = attrs.getAttributeValue("", sAttribute);
                throw new IllegalArgumentException("You must provide \"" + sAttribute + "\" for your text view");
            } else {
                final Typeface customTypeface = CustomFont.fromString(typedArray.getString(0)).asTypeface(context);
                setTypeface(customTypeface);
            }
        }
    }
}
