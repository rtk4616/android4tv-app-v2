package com.iwedia.gui.multimedia;

import com.iwedia.comm.content.Content;

import java.util.ArrayList;

/**
 * Navigation handler for multimedia browsing
 * 
 * @author Veljko Ilkic
 */
public class MultimediaNavigationHandler {
    /** Navigation path */
    public static ArrayList<MultimediaNavigationObject> multimediaNavigationPath = new ArrayList<MultimediaNavigationObject>();

    /** Clear navigation path */
    public static void clearNavigationPath() {
        multimediaNavigationPath.clear();
    }

    /** Add new navigation object */
    public static void addNavigationObject(
            MultimediaNavigationObject navigationObject) {
        multimediaNavigationPath.add(navigationObject);
    }

    /** Find position to select */
    public static int findPositionOfPreviousFolder(
            MultimediaNavigationObject navigationObject,
            Content[] currentVisibleContent) {
        // Find index of navigation object
        for (int i = 0; i < currentVisibleContent.length; i++) {
            if (currentVisibleContent[i] != null) {
                if (navigationObject.getFolderName().equals(
                        currentVisibleContent[i].getName())) {
                    return i;
                }
            }
        }
        return 0;
    }

    /** Remove navigation object */
    public static void removeNavigationObject(
            MultimediaNavigationObject navigationObject) {
        multimediaNavigationPath.remove(navigationObject);
    }

    /**
     * Return to previous folder
     * 
     * @param levelDifference
     *        Level difference between current and previous folder
     */
    public static void returnToPreviousFolder(int levelDifference) {
        try {
            for (int i = 0; i < levelDifference; i++) {
                multimediaNavigationPath
                        .remove(multimediaNavigationPath.size() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Get navigation object for current folder */
    public static MultimediaNavigationObject getCurrentNavigationObject() {
        try {
            return multimediaNavigationPath
                    .get(multimediaNavigationPath.size() - 1);
        } catch (Exception e) {
            return null;
        }
    }
}
