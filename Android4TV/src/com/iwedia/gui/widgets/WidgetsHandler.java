package com.iwedia.gui.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.iwedia.comm.content.Content;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.listeners.MainKeyListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handler class for handling installed widgets
 * 
 * @author Veljko Ilkic
 */
public class WidgetsHandler {
    public static final String WIDGET_OBJECT_FILE_NAME = "widgetObjects.bin";
    public static final String WIDGET_CELL_FILE_NAME = "widgetCells.bin";
    /** Constant key for bundle saving */
    public static final String WIDGET_PACKAGE_NAMES = "WIDGET_PACKAGE_NAMES";
    public static final String WIDGET_CLASS_NAMES = "WIDGET_CLASS_NAMES";
    public static final String WIDGET_ID_COUNTER = "WIDGER_ID_COUNTER";
    static final int APPWIDGET_HOST_ID = 2037;
    /** Reference of main activity */
    private Activity activity;
    /** Widget dialog */
    private Dialog widgetDialog;
    /** Layout inflater */
    private LayoutInflater inflater;
    /** Widget host object */
    private AppWidgetHost mAppWidgetHost;
    /** Widget manager object */
    private AppWidgetManager mAppWidgetManager;
    /** Drag fields */
    private DragController mDragController;
    private static DragLayer mDragLayer;
    /** View holders for widget objects */
    private ArrayList<AppWidgetHostView> widgetsViewHolders = new ArrayList<AppWidgetHostView>();
    /** List of active widgets */
    private ArrayList<WidgetObject> widgetObjects = new ArrayList<WidgetObject>();
    /** Counter for widget ids */
    private int widgetIdCounter = 0;
    /** Arranging handler */
    private WidgetArrangingHandler widgetArrangingHandler;
    /** Drop spot area */
    private LinearLayout widgetDropSpotArea;
    /** Drop zone animations */
    private Animation showDropZone;
    private Animation hideDropZone;

    /** Constructor 1 */
    public WidgetsHandler(Activity activity) {
        super();
        // Take reference of main activity
        this.activity = activity;
    }

    /** Init widget handler method */
    public void init() {
        // Create widget dialog
        widgetDialog = new Dialog(activity,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        widgetDialog.setContentView(com.iwedia.gui.R.layout.widget_layout_main);
        // Init dialog
        Window window = widgetDialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        widgetDialog.setCancelable(false);
        widgetDialog.setOnKeyListener(new MainKeyListener(
                (MainActivity) activity));
        // Init widget arranging handler
        widgetArrangingHandler = new WidgetArrangingHandler(activity);
        // Get widget manager
        mAppWidgetManager = AppWidgetManager.getInstance(activity);
        // Init widget host
        mAppWidgetHost = new AppWidgetHost(activity, APPWIDGET_HOST_ID);
        mAppWidgetHost.startListening();
        // Init drag controller
        mDragController = new DragController(activity, this);
        DragController dragController = mDragController;
        mDragLayer = (DragLayer) widgetDialog
                .findViewById(com.iwedia.gui.R.id.drag_layer);
        mDragLayer.setDragController(dragController);
        dragController.addDropTarget(mDragLayer);
        // //////////////////////////////////////////
        // Drop spot area
        // //////////////////////////////////////////
        widgetDropSpotArea = (LinearLayout) widgetDialog
                .findViewById(R.id.widgetDropSpot);
        FrameLayout.LayoutParams dropSpotParams = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                WidgetArrangingHandler.DROP_SPOT_HEIGHT);
        dropSpotParams.gravity = Gravity.BOTTOM;
        widgetDropSpotArea.setLayoutParams(dropSpotParams);
        // Set visible on start
        widgetDropSpotArea.setVisibility(View.INVISIBLE);
        // /////////////////////////////////////////////
        // Load drop zone animations
        // /////////////////////////////////////////////
        showDropZone = AnimationUtils.loadAnimation(activity,
                com.iwedia.gui.R.anim.scale_alpha_in2);
        hideDropZone = AnimationUtils.loadAnimation(activity,
                com.iwedia.gui.R.anim.scale_alpha_out);
        // Attach animation listener on hide animation
        hideDropZone.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                widgetDropSpotArea.setVisibility(View.INVISIBLE);
            }
        });
        try {
            // Load important data on init
            loadImportantData();
            // Reinit widgets
            for (int i = 0; i < widgetObjects.size(); i++) {
                String packageName = widgetObjects.get(i).getPackageName();
                String className = widgetObjects.get(i).getClassName();
                int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
                ComponentName componentName = new ComponentName(packageName,
                        className);
                mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId,
                        componentName);
                addAppWidget(appWidgetId, widgetObjects.get(i), false);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Show widgets */
    public void showWidgets() {
        // Show widget dialog
        widgetDialog.show();
    }

    /** Create widget object */
    public void createWidgetComponent(String packageName, String className) {
        // Create new widget object
        WidgetObject widgetObject = new WidgetObject(packageName, className,
                null, -1);
        // Store widget object into list
        widgetObjects.add(widgetObject);
        // Get id for widget
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        // Create component name
        ComponentName componentName = new ComponentName(packageName, className);
        // Bind widget
        mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, componentName);
        // Add widget on screen
        addAppWidget(appWidgetId, widgetObject, true);
        try {
            saveImportantData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Add widget on screen */
    private void addAppWidget(int appWidgetId, final WidgetObject widgetObject,
            boolean init) {
        // Increment widget counter
        widgetIdCounter++;
        // Get widget info
        AppWidgetProviderInfo appWidget = mAppWidgetManager
                .getAppWidgetInfo(appWidgetId);
        // Widget host view
        final AppWidgetHostView widgetHostView = attachWidget(mAppWidgetHost
                .createView(activity, appWidgetId, appWidget));
        // Add widget into widget list
        widgetsViewHolders.add(widgetHostView);
        // Inflate widget holder xml
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /** Widget overlay for long click detection */
        final WidgetOverlayLinearLayout widgetOverlayFrame;
        /** Real content holder for widget */
        final LinearLayout widgetView;
        // Inflate main widget layout
        FrameLayout widgetMainView = (FrameLayout) inflater.inflate(
                R.layout.widget_layout_item, null);
        // Get overlay layout
        widgetOverlayFrame = (com.iwedia.gui.widgets.WidgetOverlayLinearLayout) widgetMainView
                .findViewById(R.id.widgetOverlayFrame);
        // Get widget content holder
        widgetView = (LinearLayout) widgetMainView
                .findViewById(R.id.widgetView);
        // ////////////////////////////////////////////////////
        // Handle widget click if system didn't bug fix
        // ////////////////////////////////////////////////////
        widgetHostView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    ((LinearLayout) widgetHostView.getParent())
                            .dispatchTouchEvent(event);
                return true;
            }
        });
        // //////////////////////////////////////////////
        // Prepare overlay layout for manipulation
        // ///////////////////////////////////////////////
        // widgetOverlayFrame.setVisibility(View.GONE);
        widgetOverlayFrame.setWidgetObject(widgetObject);
        widgetOverlayFrame.setWidgetHandler(this);
        widgetOverlayFrame.setWidgetMainLayout(widgetMainView);
        widgetOverlayFrame.setWidgetContent(widgetHostView);
        // Attach widget content in container
        widgetView.addView(widgetHostView);
        // ////////////////////////////
        // Set tag on init
        // ////////////////////////////
        if (init) {
            widgetMainView.setTag(new WidgetViewTagObject(widgetHostView
                    .getId(), -1, null));
        }
        // /////////////////////////////////////////////////////
        // Reset tag if widget is already showed on screen
        // //////////////////////////////////////////////////////
        else {
            widgetMainView.setTag(new WidgetViewTagObject(widgetHostView
                    .getId(), widgetObject.getCellId(), widgetObject
                    .getHowManyCellsTake()));
        }
        // Determine widget size
        int[] widgetSizeCells = widgetArrangingHandler
                .howManyCellsTake(appWidget);
        // Set widget size in object
        widgetObject.setHowManyCellsTake(widgetSizeCells);
        // Real widget size on screen
        int[] widgetSizeReal = new int[2];
        // Calculate width and height of widget on screen
        widgetSizeReal[0] = widgetSizeCells[0]
                * widgetArrangingHandler.getCellWidth();
        widgetSizeReal[1] = widgetSizeCells[1]
                * widgetArrangingHandler.getCellHeight();
        // Set widget size
        DragLayer.LayoutParams widgetParams = new DragLayer.LayoutParams(
                widgetSizeReal[0], widgetSizeReal[1], 0, 0);
        widgetMainView.setLayoutParams(widgetParams);
        LinearLayout.LayoutParams widgetHostParams = new LinearLayout.LayoutParams(
                widgetSizeReal[0],
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        widgetHostView.setLayoutParams(widgetHostParams);
        // /////////////////////////////////
        // Init time
        // /////////////////////////////////
        if (init) {
            // /////////////////////////////////////////////////////////
            // Check if there is enough space for widget and show it
            // //////////////////////////////////////////////////////////
            if (widgetArrangingHandler.setXY(widgetMainView, widgetSizeCells,
                    widgetObject)) {
                // Add widget on screen
                mDragLayer.addView(widgetMainView);
                mDragLayer.invalidate();
            }
            // ///////////////////////////////////////////////////////////////
            // Remove added widget if there is no enough space on screen
            // ///////////////////////////////////////////////////////////////
            else {
                // Unbind widget
                mAppWidgetHost.deleteAppWidgetId(widgetHostView.getId());
                // Remove widget data from list
                widgetsViewHolders.remove(widgetsViewHolders.size() - 1);
                widgetObjects.remove(widgetsViewHolders.size() - 1);
                // Show no empty space message
                A4TVToast toast = new A4TVToast(activity);
                toast.showToast(activity.getResources().getString(
                        com.iwedia.gui.R.string.no_enough_space_on_screen));
            }
        }
        // ///////////////////////////
        // After init
        // ///////////////////////////
        else {
            // Just add widgets on screen
            widgetArrangingHandler.setXYAfterInit(widgetMainView, widgetObject);
            // Add widget on screen
            mDragLayer.addView(widgetMainView);
            mDragLayer.invalidate();
        }
    }

    /** Check if widget is already active */
    public boolean checkWidgetVisibility(Content widgetContent) {
        String packageName = widgetContent.getImage();
        // Check if widget with the same package name is already in list
        for (int i = 0; i < widgetObjects.size(); i++) {
            if (widgetObjects.get(i).getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /** Get id of widget */
    public int getIndexOfWidget(String packageName) {
        int i;
        // Find widget with the same package name in list and return index
        for (i = 0; i < widgetObjects.size(); i++) {
            if (widgetObjects.get(i).getPackageName().equals(packageName)) {
                break;
            }
        }
        return i;
    }

    /** Remove widget content */
    public void removeWidgetFromScreen(Content widgetContent) {
        // Get package name
        String packageName = widgetContent.getImage();
        // Get index of widget in list
        int widgetIndex = getIndexOfWidget(packageName);
        // View that needs to be removed
        View viewToRemove = null;
        // Find view that needs to be removed
        for (int i = 0; i < mDragLayer.getChildCount(); i++) {
            if (((WidgetViewTagObject) mDragLayer.getChildAt(i).getTag())
                    .getWidgetId() == widgetsViewHolders.get(widgetIndex)
                    .getId()) {
                viewToRemove = mDragLayer.getChildAt(i);
                break;
            }
        }
        // Unbind widget from host
        mAppWidgetHost.deleteAppWidgetId(widgetsViewHolders.get(widgetIndex)
                .getId());
        // Deinit flag in widget cell grid
        if (viewToRemove != null)
            widgetArrangingHandler.deinitTakenCells(
                    ((WidgetViewTagObject) viewToRemove.getTag()).getCellId(),
                    (int[]) ((WidgetViewTagObject) viewToRemove.getTag())
                            .getCellsTaken());
        // Refresh graphic layer
        mDragLayer.removeView(viewToRemove);
        mDragLayer.invalidate();
        // Remove widget data from list
        widgetsViewHolders.remove(widgetIndex);
        widgetObjects.remove(widgetIndex);
        try {
            saveImportantData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Remove widget from drop zone */
    public void removeWidgetFromDropZone(WidgetObject widgetObject) {
        // Get package name
        String packageName = widgetObject.getPackageName();
        // Get index of widget in list
        int widgetIndex = getIndexOfWidget(packageName);
        // View that needs to be removed
        View viewToRemove = null;
        // Find view that needs to be removed
        for (int i = 0; i < mDragLayer.getChildCount(); i++) {
            if (((WidgetViewTagObject) mDragLayer.getChildAt(i).getTag())
                    .getWidgetId() == widgetsViewHolders.get(widgetIndex)
                    .getId()) {
                viewToRemove = mDragLayer.getChildAt(i);
                break;
            }
        }
        // Unbind widget from host
        mAppWidgetHost.deleteAppWidgetId(widgetsViewHolders.get(widgetIndex)
                .getId());
        // Deinit flag in widget cell grid
        if (viewToRemove != null)
            widgetArrangingHandler.deinitTakenCells(
                    ((WidgetViewTagObject) viewToRemove.getTag()).getCellId(),
                    (int[]) ((WidgetViewTagObject) viewToRemove.getTag())
                            .getCellsTaken());
        // Refresh graphic layer
        mDragLayer.removeView(viewToRemove);
        mDragLayer.invalidate();
        // Remove widget data from list
        widgetsViewHolders.remove(widgetIndex);
        widgetObjects.remove(widgetIndex);
        try {
            saveImportantData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Clear screen from widgets */
    public void clearScreen() {
        // Close widget dialog
        widgetDialog.dismiss();
        // Clear list of widgets
        widgetsViewHolders.clear();
        // Clear widget layout container
        if (mDragLayer != null) {
            mDragLayer.removeAllViews();
            mDragLayer.invalidate();
        }
        // Widget counter deinit
        widgetIdCounter = 0;
    }

    /** Remove all widgets */
    public void removeAllWidgets() {
        // Remove all widget holders
        widgetsViewHolders.clear();
        // Remove all widget objects
        widgetObjects.clear();
        // Init grid cell
        widgetArrangingHandler.initCellGrid();
    }

    /** Аttach widget method and set params */
    private AppWidgetHostView attachWidget(AppWidgetHostView widget) {
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        widget.setLayoutParams(flp);
        widget.setId(100 + widgetIdCounter);
        return widget;
    }

    /** Dragging method */
    public boolean startDrag(View v, WidgetObject widgetObject) {
        showWidgetBackground(v);
        showDropArea();
        Object dragInfo = v;
        mDragController.startDrag(v, mDragLayer, dragInfo,
                DragController.DRAG_ACTION_MOVE, widgetObject);
        return true;
    }

    /** Show widget background while dragging */
    public void showWidgetBackground(View widgetView) {
        for (int i = 0; i < mDragLayer.getChildCount(); i++) {
            mDragLayer.getChildAt(i).setBackgroundResource(
                    R.color.widget_static_background);
        }
        widgetView.setBackgroundResource(R.color.widget_selected_background);
        widgetView.invalidate();
    }

    /** Hide widgets background on drag end */
    public void hideWidgetBackground(View widgetView) {
        for (int i = 0; i < mDragLayer.getChildCount(); i++) {
            mDragLayer.getChildAt(i).setBackgroundResource(
                    android.R.color.transparent);
        }
        hideDropArea();
    }

    /** Show drop area */
    public void showDropArea() {
        widgetDropSpotArea.setVisibility(View.VISIBLE);
        widgetDropSpotArea.startAnimation(showDropZone);
    }

    /** Hide drop area */
    public void hideDropArea() {
        widgetDropSpotArea.startAnimation(hideDropZone);
    }

    /** Drop zone active indicator */
    public void widgetInsideDropZone() {
        widgetDropSpotArea.setBackgroundColor(activity.getResources().getColor(
                R.color.widget_drop_zone_active));
    }

    /** Drop zone inactive */
    public void widgetOutsideDropZone() {
        // get drawable from theme for image source
        TypedArray atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuDescriptionBackground });
        int backgroundID = atts.getResourceId(0, 0);
        widgetDropSpotArea.setBackgroundResource(backgroundID);
        atts.recycle();
    }

    /** Save important data */
    public void saveImportantData() throws FileNotFoundException, IOException {
        FileSaveLoad.save(widgetObjects, WIDGET_OBJECT_FILE_NAME, activity);
        FileSaveLoad.save(widgetArrangingHandler.getWidgetCells(),
                WIDGET_CELL_FILE_NAME, activity);
    }

    /** Show widgets */
    public void showWidgetDialog() {
        widgetDialog.show();
    }

    /** Hide widgets from screen */
    public void hideWidgetDialog() {
        widgetDialog.cancel();
    }

    /**
     * Load important data
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unchecked")
    public void loadImportantData() throws FileNotFoundException, IOException,
            ClassNotFoundException {
        widgetObjects = (ArrayList<WidgetObject>) FileSaveLoad.load(
                WIDGET_OBJECT_FILE_NAME, activity);
        widgetArrangingHandler.setWidgetCells((WidgetCell[]) FileSaveLoad.load(
                WIDGET_CELL_FILE_NAME, activity));
    }

    // /////////////////////////////////////////
    // Getters and setters
    // /////////////////////////////////////////
    /** Get widget host */
    public AppWidgetHost getmAppWidgetHost() {
        return mAppWidgetHost;
    }

    /** Set widget host object */
    public void setmAppWidgetHost(AppWidgetHost mAppWidgetHost) {
        this.mAppWidgetHost = mAppWidgetHost;
    }

    /** Get Widget Manager object */
    public AppWidgetManager getmAppWidgetManager() {
        return mAppWidgetManager;
    }

    /** Set widget manager object */
    public void setmAppWidgetManager(AppWidgetManager mAppWidgetManager) {
        this.mAppWidgetManager = mAppWidgetManager;
    }

    /** Get list of active widget objects */
    public ArrayList<WidgetObject> getWidgetObjects() {
        return widgetObjects;
    }

    /** Set list of widget objects */
    public void setWidgetObjects(ArrayList<WidgetObject> widgetObjects) {
        this.widgetObjects = widgetObjects;
    }

    /** Get widget arranging handler */
    public WidgetArrangingHandler getWidgetArrangingHandler() {
        return widgetArrangingHandler;
    }
}
