package com.iwedia.gui.listeners;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.ChannelInstallationDialog;
import com.iwedia.gui.content_list.ContentListHandler;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.mainmenu.MainMenuHandlingClass;
import com.iwedia.gui.mainmenu.gallery.A4TVCoverAdapterView;
import com.iwedia.gui.mainmenu.gallery.A4TVCoverAdapterView.OnItemClickListener;
import com.iwedia.gui.mainmenu.gallery.animations.TransitionItemAnimationHandler;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.multimedia.MultimediaNavigationHandler;

/**
 * OnClick listener for main menu gallery
 * 
 * @author Veljko Ilkic
 */
public class A4TVGalleryOnClickListener implements OnItemClickListener {
    private final String TAG = "A4TVGalleryOnClickListener";
    /** Reference of main activity */
    private Activity activity;
    /** Reference of main menu handler */
    private MainMenuHandlingClass mainMenuHandler;
    /** Reference of content list handler */
    private ContentListHandler contentListHandler;
    /** Reference of multimedia handler */
    private MultimediaHandler multimediaHandler;
    /** Google play package */
    private final String google_play_package = "com.android.vending";
    public static int lastClickedIndexPosition = 0;

    /** Constructor 1 */
    public A4TVGalleryOnClickListener(Activity activity) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of main menu handler
        this.mainMenuHandler = ((MainActivity) this.activity)
                .getMainMenuHandler();
    }

    public void onItemClick(A4TVCoverAdapterView<?> parent, View view,
            int position, long id) {
        lastClickedIndexPosition = position;
        // Load image id for root submenu item
        MainMenuContent.submenuRootResId = mainMenuHandler
                .getImagesCurrentSelectedSubMenuItems(MainMenuContent.currentState)[position];
        // Check action that need to be done
        if (MainMenuContent
                .checkIdResourceAction(MainMenuContent.submenuRootResId) == MainMenuContent.OPEN_DIALOG) {
            /**
             * boolean that shows is dialog opened and if we create new dialog
             * at the end
             */
            boolean isOpenedDialog = false;
            if (MainMenuContent
                    .checkIdResourceDialog(MainMenuContent.submenuRootResId) == MainMenuContent.OPEN_CONTENT_LIST) {
                // Check if content list is initialized
                if (contentListHandler == null) {
                    ((MainActivity) activity).initContentList();
                    contentListHandler = ((MainActivity) this.activity)
                            .getContentListHandler();
                }
                // Close main menu
                mainMenuHandler.closeMainMenu(false);
                // Show content list dialog
                if (contentListHandler != null) {
                    contentListHandler.showContentList();
                }
                // Filter current filter
                if (contentListHandler != null)
                    contentListHandler.filterContent(
                            ContentListHandler.CONTENT_LIST_LAST_FILTER, true);
                // Set flag to true
                MainKeyListener.contentListFromMainMenu = true;
                isOpenedDialog = true;
            }
            /** OPEN MULTIMEDIA DIALOG */
            if (MainMenuContent
                    .checkIdResourceDialog(MainMenuContent.submenuRootResId) == MainMenuContent.OPEN_MULTIMEDIA) {
                if (multimediaHandler == null) {
                    ((MainActivity) activity).initMultimediaHandler();
                    multimediaHandler = ((MainActivity) this.activity)
                            .getMultimediaHandler();
                }
                if (MainActivity.service != null) {
                    // Clear navigation path
                    MultimediaNavigationHandler.clearNavigationPath();
                    // Update app state of key listener
                    MainKeyListener
                            .setAppState(MainKeyListener.MULTIMEDIA_FIRST);
                    // Init multimedia just in case
                    MultimediaHandler.multimediaScreen = MultimediaHandler.MULTIMEDIA_FIRST_SCREEN;
                    MultimediaHandler.secondScreenFolderLevel = 0;
                    // Open first multimedia screen and reset path
                    ((MainActivity) activity).getMultimediaHandler().new LoadTask(
                            "/").execute();
                    // Set flag to true
                    MainKeyListener.multimediaFromMainMenu = true;
                    isOpenedDialog = true;
                } else {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(com.iwedia.gui.R.string.proxy_service_is_null);
                }
            }
            /** OPEN APPLICATIONS DIALOG */
            if (MainMenuContent
                    .checkIdResourceDialog(MainMenuContent.submenuRootResId) == MainMenuContent.OPEN_APPLICATIONS) {
                if (contentListHandler == null) {
                    ((MainActivity) activity).initContentList();
                    contentListHandler = ((MainActivity) this.activity)
                            .getContentListHandler();
                }
                // Hide main menu
                mainMenuHandler.closeMainMenu(false);
                // Show content list
                if (contentListHandler != null) {
                    contentListHandler.showContentList();
                }
                // Delay start of loading content
                Handler delay = new Handler();
                delay.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contentListHandler.filterContent(FilterType.APPS, true);
                    }
                }, 200);
                // Set flag for content list openning
                MainKeyListener.contentListFromMainMenu = true;
                isOpenedDialog = true;
            }
            /** OPEN INPUT SELECTIONS DIALOG */
            if (MainMenuContent
                    .checkIdResourceDialog(MainMenuContent.submenuRootResId) == MainMenuContent.OPEN_INPUT_SELECTIONS) {
                if (contentListHandler == null) {
                    ((MainActivity) activity).initContentList();
                    contentListHandler = ((MainActivity) this.activity)
                            .getContentListHandler();
                }
                if (contentListHandler.areInputsEnabled()) {
                    // Hide main menu
                    mainMenuHandler.closeMainMenu(false);
                    // Show content list
                    if (contentListHandler != null) {
                        contentListHandler.showContentList();
                    }
                    // Delay start of loading content
                    Handler delay = new Handler();
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            contentListHandler.filterContent(FilterType.INPUTS,
                                    true);
                        }
                    }, 200);
                    // Set flag for content list opening
                    MainKeyListener.contentListFromMainMenu = true;
                    isOpenedDialog = true;
                } else {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(R.string.inputs_not_enabled);
                }
            }
            /** OPEN GOOGLE PLAY APPLICATION */
            if (MainMenuContent
                    .checkIdResourceDialog(MainMenuContent.submenuRootResId) == MainMenuContent.OPEN_GOOGLE_PLAY) {
                // create intent to open google play
                try {
                    Intent launchIntent = activity.getPackageManager()
                            .getLaunchIntentForPackage(google_play_package);
                    activity.startActivity(launchIntent);
                } catch (Exception e) {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(R.string.google_play_error);
                }
                isOpenedDialog = true;
            }
            /** PROGRAM BLOCKING DIALOG */
            if (MainMenuContent
                    .checkIdResourceDialog(MainMenuContent.submenuRootResId) == MainMenuContent.OPEN_PROGRAM_BLOCKING_DIALOG) {
                // TypedArray atts = activity.getTheme().obtainStyledAttributes(
                // new int[] { R.attr.A4TVDialogTransparent });
                // int backgroundPictureID = atts.getResourceId(0, 0);
                //
                // A4TVDialog dialogNew = new A4TVDialog(activity,
                // A4TVDialog.REGULAR_DIALOG, backgroundPictureID);
                //
                // dialogNew.setContentView(R.layout.program_blocking);
                // dialogNew.getWindow().getAttributes().height =
                // MainActivity.screenHeight;
                // dialogNew.getWindow().getAttributes().width =
                // MainActivity.screenWidth;
                //
                // dialogNew.show();
                A4TVToast toast = new A4TVToast(activity);
                toast.showToast(R.string.not_implemented);
                isOpenedDialog = true;
            }
            /** Open rest of the dialogs ****************************************************************/
            if (!isOpenedDialog) {
                // fill lists for creating dialogs
                A4TVDialog dialog = MainMenuContent
                        .getDialogFromMainMenuResource(
                                MainMenuContent.submenuRootResId,
                                (MainActivity) activity);
                if (dialog != null
                        && dialog instanceof ChannelInstallationDialog) {
                    Content activeContent = null;
                    try {
                        if (MainActivity.service != null)
                            activeContent = MainActivity.service
                                    .getContentListControl()
                                    .getActiveContent(0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if ((activeContent != null)
                            && (activeContent.getFilterType() == FilterType.INPUTS)) {
                        A4TVToast toast = new A4TVToast(activity);
                        toast.showToast(R.string.not_supported_action_for_input);
                        return;
                    }
                }
                if (dialog != null) {
                    dialog.show();
                }
            }
            // Open dialogs
            // Toast.makeText(mainMenuHandler.getActivity(), "Open dialog",
            // Toast.LENGTH_SHORT).show();
        }
        if (MainMenuContent
                .checkIdResourceAction(MainMenuContent.submenuRootResId) == MainMenuContent.LOAD_SUBMENU) {
            // Load submenu
            // Rember index of item in navigation path
            MainMenuHandlingClass.navigationPath.add(position);
            // Delay starting of trnslate animation
            Handler delay = new Handler();
            delay.postDelayed(new Runnable() {
                public void run() {
                    // Start transition
                    mainMenuHandler.getTransitionItemAnimHandler().translate(
                            mainMenuHandler.getTransitionItemAnimHandler()
                                    .getSubmenuRootImage(),
                            mainMenuHandler.getFlip3dAnimationHandler()
                                    .getImage(),
                            TransitionItemAnimationHandler.ANIMATE_UP);
                }
            }, 10);
        }
    }
}
