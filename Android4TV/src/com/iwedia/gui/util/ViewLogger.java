package com.iwedia.gui.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ViewLogger {
    String mTag = "ViewLogger";

    public ViewLogger(String tag) {
        mTag = tag;
    }

    public void logViewHierarchy(ViewGroup root) {
        parseViewGroup(root, 0);
    }

    private void parseViewGroup(ViewGroup group, int level) {
        int n = group.getChildCount();
        for (int i = 0; i < n; i++) {
            View view = group.getChildAt(i);
            onViewResult(view, level);
            if (view instanceof ViewGroup) {
                parseViewGroup((ViewGroup) view, level + 1);
            }
        }
    }

    public void onViewResult(View view, int level) {
        StringBuffer tab = new StringBuffer();
        for (int i = 0; i < level; i++) {
            tab.append("  ");
        }
        Log.i(mTag, tab + view.toString());
    }
}
