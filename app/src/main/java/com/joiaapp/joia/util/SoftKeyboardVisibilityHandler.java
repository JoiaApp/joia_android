package com.joiaapp.joia.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by arnell on 8/4/2017.
 * Copyright 2017 Joia. All rights reserved.
 */

public class SoftKeyboardVisibilityHandler {

    static public void hideWhenKeyboardVisible(final View rootView, final Collection<View> viewsToHide) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                boolean shouldHide = heightDiff > 100;
                for (View view : viewsToHide) {
                    view.setVisibility(shouldHide ? View.GONE : View.VISIBLE);
                }
            }
        });
    }

    static public void hideWhenKeyboardVisible(final View rootView, final View viewToHide) {
        hideWhenKeyboardVisible(rootView, Collections.singletonList(viewToHide));

    }
}
