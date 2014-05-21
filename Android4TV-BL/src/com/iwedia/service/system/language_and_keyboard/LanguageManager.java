package com.iwedia.service.system.language_and_keyboard;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.util.Log;

import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.application.ApplicationManager;
import com.iwedia.service.widget.WidgetManager;

@SuppressLint("DefaultLocale")
public class LanguageManager {
    private static final String LOG_TAG = "LanguageManager";
    private static LanguageManager instance = null;
    private Object am;
    private Class<?> ActivityManagerNative;
    private Class<?> IActivityManager;
    private Method getDefault;
    private Method getConfiguration;
    private Configuration config;
    private List<String> listOfAvailableLanguages;

    public LanguageManager() {
        try {
            // this is the only way to change config settings for now
            ActivityManagerNative = Class
                    .forName("android.app.ActivityManagerNative");
            IActivityManager = Class.forName("android.app.IActivityManager");
            getDefault = null;
            getDefault = ActivityManagerNative.getMethod("getDefault",
                    (Class<?>[]) null);
            am = IActivityManager.cast(getDefault.invoke(ActivityManagerNative,
                    (Object[]) null));
            getConfiguration = am.getClass().getMethod("getConfiguration",
                    (Class<?>[]) null);
            config = (Configuration) getConfiguration.invoke(am,
                    (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listOfAvailableLanguages = new ArrayList<String>();
        listOfAvailableLanguages.add("English");
        listOfAvailableLanguages.add("中文 (简体)");
        listOfAvailableLanguages.add("中文 (繁體)");
        listOfAvailableLanguages.add("Norsk");
        listOfAvailableLanguages.add("Svenska");
        listOfAvailableLanguages.add("Dansk");
        listOfAvailableLanguages.add("Suomi");
        listOfAvailableLanguages.add("Icelandic");
    }

    public void changeLanguage(String language) {
        Locale locale;
        if (language.contains("English")) {
            locale = new Locale("en");
        } else if (language.contains("简体")) {
            locale = new Locale("zh", "CN");
        } else if (language.contains("繁體")) {
            locale = new Locale("zh", "TW");
        } else if (language.contains("Norsk")) {
            locale = new Locale("nb", "NO");
        } else if (language.contains("Svenska")) {
            locale = new Locale("sv", "SE");
        } else if (language.contains("DanskÂ¸")) {
            locale = new Locale("da", "DK");
        } else if (language.contains("Suomi")) {
            locale = new Locale("fi", "FI");
        } else if (language.contains("Icelandic")) {
            locale = new Locale("is", "IS");
        } else {
            locale = new Locale(language);
        }
        Locale.setDefault(locale);
        config.locale = locale;
        updateConfiguration("updateConfiguration");
        ApplicationManager.getInstance().initApplications();
        WidgetManager.getInstance().initializeInstalledWidgets(
                IWEDIAService.getContext());
    }

    public void setFontScale(float scale) {
        config.fontScale = scale;
        updateConfiguration("updatePersistentConfiguration");
    }

    public float getActiveFontScale() {
        return config.fontScale;
    }

    public void updateConfiguration(String update) {
        try {
            Class<?> configClass = config.getClass();
            Field f = configClass.getField("userSetLocale");
            f.setBoolean(config, true);
            Class<?>[] args = new Class[1];
            args[0] = Configuration.class;
            Method updateConfiguration = am.getClass().getMethod(update, args);
            updateConfiguration.invoke(am, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getActiveLanguage() {
        int index = 0;
        Locale.setDefault(config.locale);
        String language = config.locale.toString();
        if (language.contains("en")) {
            index = 0;
        } else if (language.contains("zh_CN")) {
            index = 1;
        } else if (language.contains("zh_TW")) {
            index = 2;
        } else if (language.contains("nb_NO")) {
            index = 3;
        } else if (language.contains("sv_SE")) {
            index = 4;
        } else if (language.contains("da")) {
            index = 5;
        } else if (language.contains("fi_FI")) {
            index = 6;
        } else if (language.contains("is_IS")) {
            index = 7;
        }
        return index;
    }

    public List<String> getAvailableLanguages() {
        return listOfAvailableLanguages;
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    /**
     * @return Avalable audio languages for current service. If they are not
     *         available, it returns null.
     */
    public String convertTrigramsToLanguage(String language, boolean isLanguage) {
        String languageToDisplay;
        if (isLanguage) {
            languageToDisplay = checkTrigrams(language);
        } else {
            languageToDisplay = checkCountries(language);
        }
        if (languageToDisplay.contains(" ")) {
            int indexOfSecondWord = languageToDisplay.indexOf(" ") + 1;
            languageToDisplay = languageToDisplay.substring(0, 1).toUpperCase(
                    new Locale(languageToDisplay))
                    + languageToDisplay.substring(1, indexOfSecondWord)
                    + languageToDisplay.substring(indexOfSecondWord,
                            indexOfSecondWord + 1).toUpperCase()
                    + languageToDisplay.substring(indexOfSecondWord + 1);
        } else {
            languageToDisplay = languageToDisplay.substring(0, 1).toUpperCase()
                    + languageToDisplay.substring(1);
        }
        return languageToDisplay;
    }

    public String checkTrigrams(String language) {
        if (language.equals("fre")) {
            language = "fra";
        } else if (language.equals("sve")) {
            language = "swe";
        } else if (language.equals("dut") || language.equals("nla")) {
            language = "nl";
        } else if (language.equals("ger")) {
            language = "deu";
        } else if (language.equals("alb")) {
            language = "sqi";
        } else if (language.equals("arm")) {
            language = "hye";
        } else if (language.equals("baq")) {
            language = "eus";
        } else if (language.equals("chi")) {
            language = "zho";
        } else if (language.equals("cze")) {
            language = "ces";
        } else if (language.equals("per")) {
            language = "fas";
        } else if (language.equals("gae")) {
            language = "gla";
        } else if (language.equals("geo")) {
            language = "kat";
        } else if (language.equals("gre")) {
            language = "ell";
        } else if (language.equals("ice")) {
            language = "isl";
        } else if (language.equals("ice")) {
            language = "isl";
        } else if (language.equals("mac") || language.equals("mak")) {
            language = "mk";
        } else if (language.equals("may")) {
            language = "msa";
        } else if (language.equals("rum")) {
            language = "ron";
        } else if (language.equals("scr")) {
            language = "sr";
        } else if (language.equals("slo")) {
            language = "slk";
        } else if (language.equals("esl") || language.equals("esp")) {
            language = "spa";
        } else if (language.equals("wel")) {
            language = "cym";
        }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "CheckTrigrams" + language);
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        String languageToDisplay = Locale.getDefault().getDisplayLanguage();
        if (languageToDisplay.equals("qaa")) {
            languageToDisplay = "Original";
        }
        if (languageToDisplay.equals("mul")) {
            languageToDisplay = "Multiple";
        }
        if (languageToDisplay.equals("und")) {
            languageToDisplay = "Undefined";
        }
        return languageToDisplay;
    }

    private String checkCountries(String country) {
        Locale locale;
        if (country.startsWith("AUT")) {
            locale = new Locale("at", "AT");
        } else if (country.startsWith("DNK")) {
            locale = new Locale("dk", "DK");
        } else if (country.startsWith("EST")) {
            locale = new Locale("ee", "EE");
        } else if (country.startsWith("POL")) {
            locale = new Locale("pl", "PL");
        } else if (country.startsWith("PRT")) {
            locale = new Locale("pt", "PT");
        } else if (country.startsWith("SRB")) {
            locale = new Locale("rs", "RS");
        } else if (country.startsWith("SVK")) {
            locale = new Locale("sk", "SK");
        } else if (country.startsWith("SVN")) {
            locale = new Locale("si", "SI");
        } else if (country.startsWith("SWE")) {
            locale = new Locale("se", "SE");
        } else if (country.startsWith("TUR")) {
            locale = new Locale("tr", "TR");
        } else if (country.startsWith("UKR")) {
            locale = new Locale("ua", "UA");
        } else if (country.startsWith("CHI")) {
            locale = new Locale("zh", "CN");
        } else if (country.startsWith("OTH")) {
            locale = new Locale("gb", "GB");
        } else {
            locale = new Locale(country, country.toUpperCase().substring(0, 2));
        }
        return locale.getDisplayCountry();
    }
}
