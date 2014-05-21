package com.iwedia.service.widget;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

import com.iwedia.comm.content.widgets.WidgetItem;

public class WidgetManager {
    private List<AppWidgetProviderInfo> appWidgetList;
    private List<WidgetItem> listOfWidgetItems;
    private AppWidgetManager mAppWidgetManager;
    private static WidgetManager instance;

    public WidgetManager(Context context) {
        instance = this;
        initializeInstalledWidgets(context);
    }

    public void initializeInstalledWidgets(Context context) {
        mAppWidgetManager = AppWidgetManager.getInstance(context);
        listOfWidgetItems = new ArrayList<WidgetItem>();
        appWidgetList = mAppWidgetManager.getInstalledProviders();
        for (int i = 0; i < appWidgetList.size(); i++) {
            WidgetItem widgetItem = new WidgetItem();
            widgetItem.setClassName(appWidgetList.get(i).provider
                    .getClassName());
            widgetItem.setPackageName(appWidgetList.get(i).provider
                    .getPackageName());
            widgetItem.setName(appWidgetList.get(i).label);
            listOfWidgetItems.add(widgetItem);
        }
    }

    public static WidgetManager getInstance() {
        return instance;
    }

    public WidgetItem getWidgetItem(int index) {
        return listOfWidgetItems.get(index);
    }

    public int getSize() {
        return listOfWidgetItems.size();
    }
}
