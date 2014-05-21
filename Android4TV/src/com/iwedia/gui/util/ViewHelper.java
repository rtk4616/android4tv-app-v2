package com.iwedia.gui.util;

import android.view.View;
import android.view.ViewGroup;

public class ViewHelper {
    public static View findViewByClass(ViewGroup root, Class<?> c) {
        return parseViewGroup(root, c);
    }

    private static View parseViewGroup(ViewGroup group, Class<?> c) {
        int n = group.getChildCount();
        for (int i = 0; i < n; i++) {
            View view = group.getChildAt(i);
            if (c.isInstance(view)) {
                return view;
            }
            View result;
            if (view instanceof ViewGroup) {
                result = parseViewGroup((ViewGroup) view, c);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
