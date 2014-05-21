package com.iwedia.gui;

import android.app.Activity;

public class ThemeUtils {
    private static int sTheme;
    public final static int THEME_DEFAULT = -1;
    public final static int THEME_GRAY = 1;
    public final static int THEME_ICS = 0;
    private final static String THEME_ACTIVE = "theme";
    private static String themes[] = { "ICS", "Gray" };

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity
     * of the same type.
     */
    public static void changeToTheme(MainActivity activity, int theme) {
        sTheme = theme;
        /*
         * This can be called instead previous two lines, it creates new
         * instance and kills current one
         */
        MainActivity.sharedPrefs.edit().putInt(THEME_ACTIVE, theme).commit();
        activity.recreate();
        onActivityCreateSetTheme(activity);
    }

    public static int getActiveThemeIndex() {
        return MainActivity.sharedPrefs.getInt(THEME_ACTIVE, THEME_ICS);
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity) {
        try {
            sTheme = MainActivity.sharedPrefs.getInt(THEME_ACTIVE, THEME_ICS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (sTheme) {
            default:
            case THEME_ICS:
                activity.setTheme(R.style.ICS_like);
                break;
            case THEME_GRAY:
                activity.setTheme(R.style.Gray);
                break;
        }
    }

    public static String[] getThemes() {
        return themes;
    }
}
